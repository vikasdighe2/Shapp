package com.eysa.shapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class FilterActivity extends Activity {

    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader imageLoader;
    DatabaseHandler db;
    User loggedInUser;
    Handler handler;
    int seektime = 0;
    int switchValue = 0;

    Switch all_switch;
    TextView radius_value_text, price_range_field;
    SeekBar radius_seekbar;
    EditText search_category, search_quality;
    ImageView filter_page_close_image;
    InputMethodManager imm;
    String selected_category_id, category_name;

    LinearLayout category_list_container, dropdown_cat_row;
    Animation dropview_in, dropview_out;

    List<Category> categoryData;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.filter_view);


        imageLoader = new ImageLoader(getApplicationContext(), FilterActivity.this);
        dataAccess = DataAccess.getInstance(getApplicationContext());
        db = DatabaseHandler.getInstance(this);

        loggedInUser = db.getUser();
        handler = new Handler();
        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inflaterData = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dropview_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.dropview_in);
        dropview_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.dropview_out);

        filter_page_close_image = (ImageView) this.findViewById(R.id.filter_page_close_image);
        all_switch = (Switch) this.findViewById(R.id.all_switch);

        if (loggedInUser.show_all == 1){
            all_switch.setChecked(true);
        } else {
            all_switch.setChecked(false);
        }
        all_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    switchValue =1;
                }
                else {
                    switchValue =0;
                }
            }
        });
        radius_value_text = (TextView) this.findViewById(R.id.radius_value_text);
        radius_value_text.setText(String.valueOf(loggedInUser.radius));
        price_range_field = (TextView) this.findViewById(R.id.price_range_field);
        price_range_field.setText(String.valueOf(loggedInUser.price_range));
        radius_seekbar = (SeekBar) this.findViewById(R.id.radius_seekbar);
        search_category = (EditText) this.findViewById(R.id.search_category);
        search_category.setText(loggedInUser.category_name);
        category_list_container = (LinearLayout) this.findViewById(R.id.category_dropview);
        radius_seekbar.setProgress(loggedInUser.radius);
        radius_seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                int MIN = 0;
                if (progress < MIN) {

                    radius_value_text.setText(""+seektime);
                } else {
                    seektime = progress;
                }
                radius_value_text.setText(""+ seektime);

            }
        });

        filter_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSerachFilters();
                finish();
            }
        });


        /**********************CATEGORY DROPDOWN FIELD EVENTS**************************************/
        search_category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (category_list_container.getVisibility() == View.VISIBLE) {
                    //start_datepicker.startAnimation(dropview_out);
                    category_list_container.setVisibility(View.GONE);
                } else {
                        refresh_category_dropdown();

                }

            }
        });
    } /* end of onCreate() */



    public void refresh_category_dropdown(){

        category_list_container.setVisibility(View.VISIBLE);
        BasicHttpClient httpClientUserData = new BasicHttpClient(Constants.API_URL);;
        httpClientUserData.addHeader("X-Api-Key", loggedInUser.api_key);
        httpClientUserData.addHeader("X-Api-UserEmail", loggedInUser.email_address);

        ParameterMap httpClientUserDataParameters = httpClientUserData.newParams();
        httpClientUserData.setConnectionTimeout(60000);
        HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_CATEGORIES, httpClientUserDataParameters);


        try {
            category_list_container.removeAllViews();
            category_list_container.requestLayout();

            final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());
            if(DataObject.getInt("status") == 1) {
                JSONArray CategoryArr = (JSONArray) DataObject.get("items");

                for (int i = 0; i < CategoryArr.length(); i++) {
                    //Log.d("collections.length()", "" + collections.length());
                    final JSONObject categoryObject = (JSONObject) CategoryArr.get(i);

                    final String cat_id = categoryObject.getString("id");
                    final String cat_name = categoryObject.getString("name");
                    final LinearLayout dropdown_custom_row = (LinearLayout) inflaterData.inflate(R.layout.dropdown_custom_row, null);
                    TextView dropdown_item_text = (TextView) dropdown_custom_row.findViewById(R.id.dropdown_item_text);
                    dropdown_item_text.setText(""+cat_name);

                    dropdown_custom_row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selected_category_id = cat_id;
                            search_category.setText(cat_name);
                            category_name = cat_name;
                            category_list_container.setVisibility(View.GONE);

                        }
                    });

                    dropdown_custom_row.requestLayout();
                    category_list_container.addView(dropdown_custom_row);
                }
            }
        } catch(Exception e) {
            Log.w("Error", e.getMessage());
        }
    } /* end of refresh_category_dropdown() */

    public void updateSerachFilters(){
        if ( price_range_field.getText().toString().equals("")){
            price_range_field.setText("0");
        }
        int price_range = 0;
        try {
             price_range = Integer.parseInt(price_range_field.getText().toString());
        } catch (NumberFormatException e){
            e.printStackTrace();
        }
        int status = dataAccess.UpdateSearchFilter(String.valueOf(switchValue), String.valueOf(seektime), price_range_field.getText().toString(), selected_category_id);
        User user = new User(loggedInUser.id, loggedInUser.first_name,loggedInUser.last_name,loggedInUser.email_address, loggedInUser.full_name
                , loggedInUser.push_notification, loggedInUser.auto_refresh, loggedInUser.email_notification,
                loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key,
                loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, loggedInUser.phone_number,
                loggedInUser.category_id, category_name, loggedInUser.quality_id, loggedInUser.quality_name,
                switchValue, seektime,  price_range, loggedInUser.country_id,
                loggedInUser.country_name, loggedInUser.state_id, loggedInUser.state_name, loggedInUser.city_id, loggedInUser.city_name);
        db.updateUser(user);
    }

}
