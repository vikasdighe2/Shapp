package com.eysa.shapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AddProductFragment extends Fragment {

    static final int REQUEST_CAMERA = 1;
    static final int SELECT_FILE = 2;

    User loggedInUser;
    DatabaseHandler db;
    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader_fragment imageLoader;
    Calendar calendar;
    InputMethodManager imm;
    String image1, image2, image3, image4, image5, vidio, date,time,dateTime;

    View rootView;
    LinearLayout product_info_block, image_capture_container, quality_dropview, category_dropview, brand_dropview
            , model_dropview, group_dropview;
    ImageView product_info_close_image, info_block_next_image, addproduct_page_close_image, images_block_next_image;

    BounceScrollView info_block_scroll_view;

    EditText product_name_text, product_details_text, min_bit_amount_text, buynow_amount_text
            , start_time_text, end_time_text, quality_text, category_text, brand_text, model_text,  group_text;
    DatePicker start_datepicker, end_datepicker;
    TimePicker start_timepicker, end_timepicker;
    String selected_start_time, selected_end_time, selected_quality_id, selected_category_id, selected_brand_id, selected_model_id, selected_group_id;
    Switch havebill_switch;
    Button addProduct;
    int hasBrands = 0;
    Context context;
    String userLatitude, userLongitude;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       /* super.onCreate(savedInstanceState);
        setContentView(R.layout.add_product_view);*/
        rootView = inflater.inflate(R.layout.add_product_view, container, false);
        context=container.getContext();

        db = DatabaseHandler.getInstance(getActivity().getApplicationContext());
        loggedInUser = db.getUser();
        imageLoader = new ImageLoader_fragment(getActivity().getApplicationContext());
        dataAccess = DataAccess.getInstance(getActivity().getApplicationContext());
        calendar = Calendar.getInstance();
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        inflaterData = (LayoutInflater)  getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        Bundle bundle = AddProductFragment.this.getArguments();
        if (bundle != null){
        image1 = bundle.getString("image1");
        image2 = bundle.getString("image2");
        image3 = bundle.getString("image3");
        image4 = bundle.getString("image4");
        image5 = bundle.getString("image5");
        vidio = bundle.getString("vidio");
        }

        product_info_block = (LinearLayout) rootView.findViewById(R.id.product_info_block);

        product_info_close_image = (ImageView) rootView.findViewById(R.id.product_info_close_image);
        info_block_next_image = (ImageView) rootView.findViewById(R.id.info_block_next_image);
        addProduct = (Button) rootView.findViewById(R.id.add_product);

        havebill_switch = (Switch) rootView.findViewById(R.id.havebill_switch);
        product_name_text = (EditText) rootView.findViewById(R.id.product_name_text);
        product_details_text = (EditText) rootView.findViewById(R.id.product_details_text);
        min_bit_amount_text = (EditText) rootView.findViewById(R.id.min_bit_amount_text);
        buynow_amount_text = (EditText) rootView.findViewById(R.id.buynow_amount_text);

        start_time_text = (EditText) rootView.findViewById(R.id.start_time_text);
        start_datepicker = (DatePicker) rootView.findViewById(R.id.start_datepicker);
        start_timepicker = (TimePicker) rootView.findViewById(R.id.start_timepicker);

        end_time_text = (EditText) rootView.findViewById(R.id.end_time_text);
        end_datepicker = (DatePicker) rootView.findViewById(R.id.end_datepicker);
        end_timepicker = (TimePicker) rootView.findViewById(R.id.end_timepicker);

        quality_text = (EditText) rootView.findViewById(R.id.quality_text);
        quality_dropview = (LinearLayout) rootView.findViewById(R.id.quality_dropview);

        category_text = (EditText) rootView.findViewById(R.id.category_text);
        category_dropview = (LinearLayout) rootView.findViewById(R.id.category_dropview);

        brand_text = (EditText) rootView.findViewById(R.id.brand_text);
        brand_dropview = (LinearLayout) rootView.findViewById(R.id.brand_dropview);

        model_text = (EditText) rootView.findViewById(R.id.model_text);
        model_dropview = (LinearLayout) rootView.findViewById(R.id.model_dropview);

        group_text = (EditText) rootView.findViewById(R.id.group_text);
        group_dropview = (LinearLayout) rootView.findViewById(R.id.group_dropview);

        info_block_scroll_view = (BounceScrollView) rootView.findViewById(R.id.info_block_scroll_view);


        product_info_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_place_holder, new SellFragment());
                fragmentTransaction.commit();
            }
        });

        addProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                save_product();
            }
        });


        /**********************START DATE TIME FIELD EVENTS**************************************/
        start_time_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (start_datepicker.getVisibility() == View.VISIBLE) {
                    //start_datepicker.startAnimation(dropview_out);
                    start_datepicker.setVisibility(View.GONE);
                } else {

                    //start_datepicker.startAnimation(dropview_in);
                    start_datepicker.setVisibility(View.VISIBLE);

                }

            }
        });

        /**********************START DATEPICKER EVENTS**************************************/
        start_datepicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                start_time_text.setText(year + "-" + monthOfYear + "-" + dayOfMonth);

                selected_start_time = year + "-" + monthOfYear + "-" + dayOfMonth;
                start_datepicker.setVisibility(View.GONE);
                start_timepicker.setVisibility(View.VISIBLE);

            }
        });

        /**********************START TIMEPICKER EVENTS**************************************/
        start_timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                start_time_text.setText(start_time_text.getText().toString() + " " + hourOfDay + ":" + minute + ":00");

                selected_start_time = start_time_text.getText().toString();
                start_timepicker.setVisibility(View.GONE);


            }
        });


        /**********************END DATE TIME FIELD EVENTS**************************************/
        end_time_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (end_datepicker.getVisibility() == View.VISIBLE) {
                    //start_datepicker.startAnimation(dropview_out);
                    end_datepicker.setVisibility(View.GONE);
                } else {

                    //start_datepicker.startAnimation(dropview_in);
                    end_datepicker.setVisibility(View.VISIBLE);

                }

            }
        });

        /**********************START DATEPICKER EVENTS**************************************/
        end_datepicker.setMinDate(System.currentTimeMillis() - 1000);
        end_datepicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                int month = monthOfYear + 1;
                date = year + "-" + month + "-" + dayOfMonth;
                end_time_text.setText(date);
                selected_end_time = year + "-" + month + "-" + dayOfMonth;
                end_datepicker.setVisibility(View.GONE);
                end_timepicker.setVisibility(View.VISIBLE);

            }
        });

        /**********************START TIMEPICKER EVENTS**************************************/
        end_timepicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {

            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                time =  hourOfDay + ":" + minute + ":00";
                dateTime = date +" "+time;
                end_time_text.setText(dateTime);

                selected_end_time = end_time_text.getText().toString();
                end_timepicker.setVisibility(View.GONE);


            }
        });


        /**********************QUALITY DROPDOWN FIELD EVENTS**************************************/
        quality_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (quality_dropview.getVisibility() == View.VISIBLE) {
                    //start_datepicker.startAnimation(dropview_out);
                    quality_dropview.setVisibility(View.GONE);
                } else {

                    //start_datepicker.startAnimation(dropview_in);
                    refresh_quality_dropdown();

                }

            }
        });


       /* *********************CATEGORY DROPDOWN FIELD EVENTS**************************************/
        category_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (category_dropview.getVisibility() == View.VISIBLE) {
                    //start_datepicker.startAnimation(dropview_out);
                    category_dropview.setVisibility(View.GONE);
                } else {
                        refresh_category_dropdown();

                }

            }
        });




       /* *********************BRAND DROPDOWN FIELD EVENTS**************************************/
        brand_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (brand_dropview.getVisibility() == View.VISIBLE) {
                    //start_datepicker.startAnimation(dropview_out);
                    brand_dropview.setVisibility(View.GONE);
                } else {

                        refresh_brand_dropdown();
                }
            }
        });



       /* *********************MODEL DROPDOWN FIELD EVENTS**************************************/
        model_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (model_dropview.getVisibility() == View.VISIBLE) {
                    //start_datepicker.startAnimation(dropview_out);
                    model_dropview.setVisibility(View.GONE);
                } else {

                        refresh_model_dropdown();
                }
            }
        });

        group_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                if (group_dropview.getVisibility() == View.VISIBLE) {
                    //start_datepicker.startAnimation(dropview_out);
                    group_dropview.setVisibility(View.GONE);
                } else {
                    refresh_group_dropdown();
                }
            }
        });

        GPSTracker gpsTracker = new GPSTracker(context);
        if (gpsTracker.getIsGPSTrackingEnabled()){
            userLatitude = String.valueOf(gpsTracker.latitude);
            userLongitude = String.valueOf(gpsTracker.longitude);
        }else{
            gpsTracker.showSettingsAlert();
        }


        return rootView;

    } /* end of onCreate() */



    public void save_product(){


        String PRO_NAME = product_name_text.getText().toString();
        String PRO_DESC = product_details_text.getText().toString();
        String PRO_MIN_BID_PRICE = min_bit_amount_text.getText().toString();
        String PRO_BUY_NOW_PRICE = buynow_amount_text.getText().toString();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date now = new Date();
        String PRO_START_TIME = df.format(now);
        String PRO_END_TIME = end_time_text.getText().toString();
        String PRO_BIL_INCLUDED="0";
        if(havebill_switch.isChecked()){
            PRO_BIL_INCLUDED="1";
        }




        BasicHttpClient httpClientUserData = new BasicHttpClient(Constants.API_URL);;
        httpClientUserData.addHeader("X-Api-Key", loggedInUser.api_key);
        httpClientUserData.addHeader("X-Api-UserEmail", loggedInUser.email_address);

        ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                .add("product_name", PRO_NAME)
                .add("product_description", PRO_DESC)
                .add("min_bid_price", PRO_MIN_BID_PRICE)
                .add("buy_now_price", PRO_BUY_NOW_PRICE)
                .add("bill_included", PRO_BIL_INCLUDED)
                .add("bid_start", PRO_START_TIME)
                .add("bid_end", PRO_END_TIME)
                .add("category_id", selected_category_id)
                .add("brand_id", selected_brand_id)
                .add("model_id", selected_model_id)
                .add("group_id", selected_group_id)
                .add("file_field_0", image1)
                .add("file_field_1", image2)
                .add("file_field_2", image3)
                .add("file_field_3", image4)
                .add("file_field_4", image5)
                .add("is_android",""+1)
                .add("latitude",userLatitude)
                .add("longitude",userLongitude)
                ;
        httpClientUserData.setConnectionTimeout(60000);
        httpClientUserData.setReadTimeout(60000);
        HttpResponse httpResponseUserData = httpClientUserData.post(Constants.CREATE_PRODUCT, httpClientUserDataParameters);

        try {
            final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());
            if(DataObject.getInt("status") == 1) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Shapp")
                        .setMessage(R.string.new_product_added_successfully)
                        .setIcon(R.drawable.app_icon)
                        .setPositiveButton("", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {

                            }
                        })
                        .setNegativeButton("Ok", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_place_holder, new SellFragment());
                                fragmentTransaction.commit();

                            }
                        }).show();
                dataAccess.downloadMySales();
                dataAccess.downloadGroupsData();

            }
            else if(DataObject.getInt("status") == 0) {

                new AlertDialog.Builder(getActivity())
                        .setTitle("Shapp")
                        .setMessage(DataObject.getString("message"))
                        .setIcon(R.drawable.app_icon)
                        .setNegativeButton("Ok", null).show();
            }

        } catch(Exception e) {
            Log.w("Error", e.getMessage());
        }
    } /* end of save_product() */





    public void refresh_quality_dropdown(){

        quality_dropview.setVisibility(View.VISIBLE);
        BasicHttpClient httpClientUserData = new BasicHttpClient(Constants.API_URL);;
        httpClientUserData.addHeader("X-Api-Key", loggedInUser.api_key);
        httpClientUserData.addHeader("X-Api-UserEmail", loggedInUser.email_address);

        ParameterMap httpClientUserDataParameters = httpClientUserData.newParams();
        httpClientUserData.setConnectionTimeout(60000);
        HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_QUALITIES, httpClientUserDataParameters);


        try {
            quality_dropview.removeAllViews();
            quality_dropview.requestLayout();
            final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());
            if(DataObject.getInt("status") == 1) {
                JSONArray QualityArr = (JSONArray) DataObject.get("items");


                for (int i = 0; i < QualityArr.length(); i++) {
                    //Log.d("collections.length()", "" + collections.length());
                    final JSONObject qualityObject = (JSONObject) QualityArr.get(i);

                    final String qlt_id = qualityObject.getString("id");
                    final String qlt_name = qualityObject.getString("name");
                    final LinearLayout dropdown_custom_row = (LinearLayout) inflaterData.inflate(R.layout.dropdown_custom_row, null);
                    TextView dropdown_item_text = (TextView) dropdown_custom_row.findViewById(R.id.dropdown_item_text);
                    dropdown_item_text.setText(""+qlt_name);

                    dropdown_custom_row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selected_quality_id = qlt_id;
                            quality_text.setText(qlt_name);
                            quality_dropview.setVisibility(View.GONE);

                        }
                    });


                    dropdown_custom_row.requestLayout();
                    quality_dropview.addView(dropdown_custom_row);

                }

            }
        } catch(Exception e) {
            Log.w("Error", e.getMessage());
        }

    } /* end of refresh_quality_dropdown() */


    public void refresh_category_dropdown(){

        category_dropview.setVisibility(View.VISIBLE);
        BasicHttpClient httpClientUserData = new BasicHttpClient(Constants.API_URL);;
        httpClientUserData.addHeader("X-Api-Key", loggedInUser.api_key);
        httpClientUserData.addHeader("X-Api-UserEmail", loggedInUser.email_address);

        ParameterMap httpClientUserDataParameters = httpClientUserData.newParams();
        httpClientUserData.setConnectionTimeout(60000);
        HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_CATEGORIES, httpClientUserDataParameters);


        try {
            category_dropview.removeAllViews();
            category_dropview.requestLayout();

            final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());
            if(DataObject.getInt("status") == 1) {
                JSONArray CategoryArr = (JSONArray) DataObject.get("items");

                for (int i = 0; i < CategoryArr.length(); i++) {
                    //Log.d("collections.length()", "" + collections.length());
                    final JSONObject categoryObject = (JSONObject) CategoryArr.get(i);

                    final String cat_id = categoryObject.getString("id");
                    final String cat_name = categoryObject.getString("name");
                    final int brand = categoryObject.getInt("has_brands");
                    final LinearLayout dropdown_custom_row = (LinearLayout) inflaterData.inflate(R.layout.dropdown_custom_row, null);
                    TextView dropdown_item_text = (TextView) dropdown_custom_row.findViewById(R.id.dropdown_item_text);
                    dropdown_item_text.setText(""+cat_name);

                    dropdown_custom_row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selected_category_id = cat_id;
                            category_text.setText(cat_name);
                            category_dropview.setVisibility(View.GONE);
                            if (brand > 0){
                                brand_text.setVisibility(View.VISIBLE);
                            }

                        }
                    });


                    dropdown_custom_row.requestLayout();
                    category_dropview.addView(dropdown_custom_row);

                }
            }
        } catch(Exception e) {
            Log.w("Error", e.getMessage());
        }

    } /* end of refresh_category_dropdown() */


    public void refresh_brand_dropdown(){

        brand_dropview.setVisibility(View.VISIBLE);
        BasicHttpClient httpClientUserData = new BasicHttpClient(Constants.API_URL);;
        httpClientUserData.addHeader("X-Api-Key", loggedInUser.api_key);
        httpClientUserData.addHeader("X-Api-UserEmail", loggedInUser.email_address);

        ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                .add("category_id", selected_category_id);
        httpClientUserData.setConnectionTimeout(60000);
        HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_BRANDS, httpClientUserDataParameters);


        try {
            final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());
            brand_dropview.removeAllViews();
            brand_dropview.requestLayout();
            if(DataObject.getInt("status") == 1) {
                JSONArray BrandsArr = (JSONArray) DataObject.get("items");

                for (int i = 0; i < BrandsArr.length(); i++) {
                    //Log.d("collections.length()", "" + collections.length());
                    final JSONObject brandObject = (JSONObject) BrandsArr.get(i);

                    final String brand_id = brandObject.getString("id");
                    final String brand_name = brandObject.getString("name");
                    final int models = brandObject.getInt("has_models");
                    final LinearLayout dropdown_custom_row = (LinearLayout) inflaterData.inflate(R.layout.dropdown_custom_row, null);
                    TextView dropdown_item_text = (TextView) dropdown_custom_row.findViewById(R.id.dropdown_item_text);
                    dropdown_item_text.setText(""+brand_name);

                    dropdown_custom_row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selected_brand_id = brand_id;
                            brand_text.setText(brand_name);
                            brand_dropview.setVisibility(View.GONE);
                            if(models > 0){
                                model_text.setVisibility(View.VISIBLE);
                            }

                        }
                    });


                    dropdown_custom_row.requestLayout();
                    brand_dropview.addView(dropdown_custom_row);

                }
            }
        } catch(Exception e) {
            Log.w("Error", e.getMessage());
        }

    } /* end of refresh_brand_dropdown() */



    public void refresh_model_dropdown(){

        model_dropview.setVisibility(View.VISIBLE);
        BasicHttpClient httpClientUserData = new BasicHttpClient(Constants.API_URL);;
        httpClientUserData.addHeader("X-Api-Key", loggedInUser.api_key);
        httpClientUserData.addHeader("X-Api-UserEmail", loggedInUser.email_address);

        ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                .add("brand_id", selected_brand_id);
        httpClientUserData.setConnectionTimeout(60000);
        HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_MODELS, httpClientUserDataParameters);


        try {
            model_dropview.removeAllViews();
            model_dropview.requestLayout();

            final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());
            if(DataObject.getInt("status") == 1) {
                JSONArray ModelsArr = (JSONArray) DataObject.get("items");

                for (int i = 0; i < ModelsArr.length(); i++) {
                    //Log.d("collections.length()", "" + collections.length());
                    final JSONObject modelObject = (JSONObject) ModelsArr.get(i);

                    final String model_id = modelObject.getString("id");
                    final String model_name = modelObject.getString("name");
                    final LinearLayout dropdown_custom_row = (LinearLayout) inflaterData.inflate(R.layout.dropdown_custom_row, null);
                    TextView dropdown_item_text = (TextView) dropdown_custom_row.findViewById(R.id.dropdown_item_text);
                    dropdown_item_text.setText(""+model_name);

                    dropdown_custom_row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selected_model_id = model_id;
                            model_text.setText(model_name);
                            model_dropview.setVisibility(View.GONE);

                        }
                    });


                    dropdown_custom_row.requestLayout();
                    model_dropview.addView(dropdown_custom_row);

                }
            }
        } catch(Exception e) {
            Log.w("Error", e.getMessage());
        }

    } /* end of refresh_model_dropdown() */

    public void refresh_group_dropdown(){

        group_dropview.setVisibility(View.VISIBLE);
        BasicHttpClient httpClientUserData = new BasicHttpClient(Constants.API_URL);;
        httpClientUserData.addHeader("X-Api-Key", loggedInUser.api_key);
        httpClientUserData.addHeader("X-Api-UserEmail", loggedInUser.email_address);

        ParameterMap httpClientUserDataParameters = httpClientUserData.newParams();
        httpClientUserData.setConnectionTimeout(60000);
        HttpResponse httpResponseUserData = httpClientUserData.post(Constants.SEARCH_GROUP, httpClientUserDataParameters);


        try {
            group_dropview.removeAllViews();
            group_dropview.requestLayout();

            final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());
            if(DataObject.getInt("status") == 1) {
                JSONArray ModelsArr = (JSONArray) DataObject.get("items");

                for (int i = 0; i < ModelsArr.length(); i++) {
                    //Log.d("collections.length()", "" + collections.length());
                    final JSONObject modelObject = (JSONObject) ModelsArr.get(i);

                    final String group_id = modelObject.getString("id");
                    final String group_name = modelObject.getString("name");
                    final LinearLayout dropdown_custom_row = (LinearLayout) inflaterData.inflate(R.layout.dropdown_custom_row, null);
                    TextView dropdown_item_text = (TextView) dropdown_custom_row.findViewById(R.id.dropdown_item_text);
                    dropdown_item_text.setText(""+group_name);

                    dropdown_custom_row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selected_group_id = group_id;
                            group_text.setText(group_name);
                            group_dropview.setVisibility(View.GONE);

                        }
                    });


                    dropdown_custom_row.requestLayout();
                    group_dropview.addView(dropdown_custom_row);

                }
            }
        } catch(Exception e) {
            Log.w("Error", e.getMessage());
        }

    } /* end of refresh_model_dropdown() */



}
