package com.eysa.shapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InvitePeoplesActivity extends Activity {

    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader imageLoader;
    ImageLoader_fragment imageLoader_fragment;
    DatabaseHandler db;
    User loggedInUser;
    Utils utils = new Utils();
    Handler handler;

    Animation dropview_in, dropview_out;

    ImageView search_page_close_image, filter_control_button, clearEditText,invite_page_close_image;
    EditText inputSearchText;
    LinearLayout item_search_layout, search_people_tab_view;
    String QUERY,group_id;
    String user_id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.invite_peoples_activity);


        imageLoader = new ImageLoader(getApplicationContext(), InvitePeoplesActivity.this);
        imageLoader_fragment = new ImageLoader_fragment(this);
        dataAccess = DataAccess.getInstance(getApplicationContext());
        db = DatabaseHandler.getInstance(this);

        loggedInUser = db.getUser();
        handler = new Handler();

        dropview_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.dropview_in);
        dropview_out = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.dropview_out);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            QUERY = extras.getString("query");
            group_id = extras.getString("groupId");
        }
        invite_page_close_image=(ImageView) this.findViewById(R.id.invite_page_close_image);
        invite_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        inputSearchText = (EditText) this.findViewById(R.id.inputSearch_leftFragment);

        item_search_layout = (LinearLayout) this.findViewById(R.id.item_pages_container);

        filter_control_button = (ImageView) this.findViewById(R.id.filter_control_button);
        clearEditText = (ImageView) this.findViewById(R.id.clearEditText);
        clearEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSearchText.setText("");
            }
        });

        item_search_layout.removeAllViews();

        inputSearchText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                JSONArray searchResultArr = dataAccess.getSearchResult("peoples", inputSearchText.getText().toString());
                try {
                    DisplayPeopleCard(searchResultArr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(eventReceiver, new IntentFilter("search_downloaded"));

    } /* end of onCreate() */

    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //refresh_leaderboard_values();
            //refresh_statistics_values();
            //refresh_performance_values();
        }
    };

    public void DisplayPeopleCard(final JSONArray searchResultArr) throws JSONException {
        item_search_layout.removeAllViews();
        item_search_layout.requestLayout();
        for (int i = 0; i < searchResultArr.length(); i++) {
            search_people_tab_view = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.search_people_tab_view, null);
            final JSONObject searchObject = (JSONObject) searchResultArr.get(i);

            TextView group_name_text = (TextView) search_people_tab_view.findViewById(R.id.group_name_text);
            group_name_text.setText(searchObject.getString("full_name"));

            CircleImageView rating_pro_image = (CircleImageView)  search_people_tab_view.findViewById(R.id.rating_pro_image);
            imageLoader_fragment.DisplayImage(searchObject.getString("avatar"), rating_pro_image);

            Button search_invite = (Button) search_people_tab_view.findViewById(R.id.search_invite);

            TextView pro_count_text = (TextView) search_people_tab_view.findViewById(R.id.pro_count_text);
            pro_count_text.setText(searchObject.getString("products_count"));

            TextView mem_count_text = (TextView) search_people_tab_view.findViewById(R.id.mem_count_text);
            mem_count_text.setText(searchObject.getString("following_count"));

            TextView follow_count_count = (TextView) search_people_tab_view.findViewById(R.id.follow_count_count);
            follow_count_count.setText(searchObject.getString("followers_count"));

            search_invite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        BasicHttpClient httpClientUserData = new BasicHttpClient(Constants.API_URL);
                        ParameterMap httpClientUserDataParameters = httpClientUserData.newParams().add("group_id", group_id).add("user_id",searchObject.getString("id") );
                        httpClientUserData.setConnectionTimeout(60000);
                        HttpResponse httpResponseUserData = httpClientUserData.post(Constants.INVITE_TO_GROUP, httpClientUserDataParameters);
                        JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());

                        try {
                            if(DataObject.getInt("status") == 1) {
                                Toast.makeText(getApplication(),"Invitation sent...", Toast.LENGTH_SHORT).show();
                            }
                        } catch(Exception e) {
                            Log.w("Error", e.getMessage());
                        }


                    } catch (Throwable t) {
                        //showMessageWithToast("Your email and password combination do not match");
                    }
                }
            });

            search_people_tab_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent GroupDetailsClass = new Intent(InvitePeoplesActivity.this,ProfileDetailsActivity.class);
                    try {
                        GroupDetailsClass.putExtra("profile_id", searchObject.getString("id"));
                        GroupDetailsClass.putExtra("admin_name", searchObject.getString("full_name"));
                        GroupDetailsClass.putExtra("admin_photo", searchObject.getString("avatar"));
                        GroupDetailsClass.putExtra("profile_location", searchObject.getString("location"));
                        GroupDetailsClass.putExtra("query", inputSearchText.getText().toString());
                        GroupDetailsClass.putExtra("is_search", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(GroupDetailsClass);
                }
            });

            search_people_tab_view.requestLayout();
            item_search_layout.addView(search_people_tab_view);
        }
    }

}
