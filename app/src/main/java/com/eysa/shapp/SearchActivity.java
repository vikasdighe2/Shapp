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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SearchActivity extends Activity {

    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader imageLoader;
    ImageLoader_fragment imageLoader_fragment;
    DatabaseHandler db;
    User loggedInUser;
    Utils utils = new Utils();
    Handler handler;
    int flag = 1;

    Animation dropview_in, dropview_out;
    int ACTIVE_TAB_INT, PRODUCTS_INT=1, GROUPS_INT=2, PEOPLE_INT=3;

    ImageView search_page_close_image, filter_control_button, clearEditText;
    RelativeLayout tab_proudcts_button, tab_groups_button, tab_people_button;
    TextView tab_products_text, tab_groups_text, tab_people_text;
    EditText inputSearchText;
    LinearLayout search_products_tab_view, item_search_layout, search_group_tab_view, search_people_tab_view;
    String ID, QUERY;
    int BACKFLAG = 0;

    Button comment_button_sale;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_view);


        imageLoader = new ImageLoader(getApplicationContext(), SearchActivity.this);
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
            BACKFLAG = extras.getInt("flag", 0);
        }

        inputSearchText = (EditText) this.findViewById(R.id.inputSearch_leftFragment);

        item_search_layout = (LinearLayout) this.findViewById(R.id.item_pages_container);

        search_page_close_image = (ImageView) this.findViewById(R.id.search_page_close_image);
        filter_control_button = (ImageView) this.findViewById(R.id.filter_control_button);
        clearEditText = (ImageView) this.findViewById(R.id.clearEditText);
        clearEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputSearchText.setText("");
            }
        });

        tab_proudcts_button = (RelativeLayout) this.findViewById(R.id.tab_proudcts_button);
        tab_products_text = (TextView) this.findViewById(R.id.tab_products_text);

        tab_groups_button = (RelativeLayout) this.findViewById(R.id.tab_groups_button);
        tab_groups_text = (TextView) this.findViewById(R.id.tab_groups_text);

        tab_people_button = (RelativeLayout) this.findViewById(R.id.tab_people_button);
        tab_people_text = (TextView) this.findViewById(R.id.tab_people_text);
        search_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        if(loggedInUser!=null){

            filter_control_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent filterClass = new Intent(getApplicationContext(), FilterActivity.class);
                    startActivity(filterClass);
                }
            });

        }



        /**********************DASHBOARD CLICK EVENT**************************************/
        tab_proudcts_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =1;
                item_search_layout.removeAllViews();
                inputSearchText.setText("");

                if(ACTIVE_TAB_INT == PRODUCTS_INT){

                }
                else {
                    ACTIVE_TAB_INT = PRODUCTS_INT;

                    tab_products_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.WHITE_COLOR));
                    tab_groups_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.TEXT_FIELD_BORDER));
                    tab_people_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.TEXT_FIELD_BORDER));

                    tab_proudcts_button.setBackgroundResource(R.color.TEXT_FIELD_BORDER);
                    tab_groups_button.setBackgroundResource(R.color.WHITE_COLOR);
                    tab_people_button.setBackgroundResource(R.color.WHITE_COLOR);

                }

            }
        });


        /**********************STATISTICS CLICK EVENT**************************************/
        tab_groups_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag =2;
                item_search_layout.removeAllViews();
                inputSearchText.setText("");

                if (ACTIVE_TAB_INT == GROUPS_INT) {

                } else {
                    ACTIVE_TAB_INT = GROUPS_INT;

                    tab_products_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.TEXT_FIELD_BORDER));
                    tab_groups_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.WHITE_COLOR));
                    tab_people_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.TEXT_FIELD_BORDER));

                    tab_proudcts_button.setBackgroundResource(R.color.WHITE_COLOR);
                    tab_groups_button.setBackgroundResource(R.color.TEXT_FIELD_BORDER);
                    tab_people_button.setBackgroundResource(R.color.WHITE_COLOR);

                }

            }
        });


        /**********************PERFORMANCE CLICK EVENT**************************************/
        tab_people_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 3;
                item_search_layout.removeAllViews();
                inputSearchText.setText("");

                if (ACTIVE_TAB_INT == PEOPLE_INT) {

                } else {
                    ACTIVE_TAB_INT = PEOPLE_INT;

                    tab_products_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.TEXT_FIELD_BORDER));
                    tab_groups_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.TEXT_FIELD_BORDER));
                    tab_people_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.WHITE_COLOR));

                    tab_proudcts_button.setBackgroundResource(R.color.WHITE_COLOR);
                    tab_groups_button.setBackgroundResource(R.color.WHITE_COLOR);
                    tab_people_button.setBackgroundResource(R.color.TEXT_FIELD_BORDER);
                }

            }
        });

        inputSearchText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (flag == 1) {
                    JSONArray searchResultArr = dataAccess.getSearchResult("products", inputSearchText.getText().toString());
                    try {
                        DisplayProductCard(searchResultArr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if(flag == 2) {
                    JSONArray searchResultArr = dataAccess.getSearchResult("groups", inputSearchText.getText().toString());
                    try {
                        DisplayGroupCard(searchResultArr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else{
                    JSONArray searchResultArr = dataAccess.getSearchResult("peoples", inputSearchText.getText().toString());
                    try {
                        DisplayPeopleCard(searchResultArr);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        if (BACKFLAG == 1) {
            flag =1;
            tab_products_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.WHITE_COLOR));
            tab_groups_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.TEXT_FIELD_BORDER));
            tab_people_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.TEXT_FIELD_BORDER));

            tab_proudcts_button.setBackgroundResource(R.color.TEXT_FIELD_BORDER);
            tab_groups_button.setBackgroundResource(R.color.WHITE_COLOR);
            tab_people_button.setBackgroundResource(R.color.WHITE_COLOR);
            inputSearchText.setText(QUERY);
            JSONArray searchResultArr = dataAccess.getSearchResult("products", QUERY);
            try {
                DisplayProductCard(searchResultArr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(BACKFLAG == 2) {
            flag = 2;
            tab_products_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.TEXT_FIELD_BORDER));
            tab_groups_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.WHITE_COLOR));
            tab_people_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.TEXT_FIELD_BORDER));

            tab_proudcts_button.setBackgroundResource(R.color.WHITE_COLOR);
            tab_groups_button.setBackgroundResource(R.color.TEXT_FIELD_BORDER);
            tab_people_button.setBackgroundResource(R.color.WHITE_COLOR);
            inputSearchText.setText(QUERY);
            JSONArray searchResultArr = dataAccess.getSearchResult("groups", QUERY);
            try {
                DisplayGroupCard(searchResultArr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if(BACKFLAG == 3){
            flag = 3;
            tab_products_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.TEXT_FIELD_BORDER));
            tab_groups_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.TEXT_FIELD_BORDER));
            tab_people_text.setTextColor(ContextCompat.getColor(SearchActivity.this, R.color.WHITE_COLOR));

            tab_proudcts_button.setBackgroundResource(R.color.WHITE_COLOR);
            tab_groups_button.setBackgroundResource(R.color.WHITE_COLOR);
            tab_people_button.setBackgroundResource(R.color.TEXT_FIELD_BORDER);
            inputSearchText.setText(QUERY);
            JSONArray searchResultArr = dataAccess.getSearchResult("peoples", QUERY);
            try {
                DisplayPeopleCard(searchResultArr);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

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


    public void DisplayProductCard(JSONArray searchResultArr) throws JSONException {
        item_search_layout.removeAllViews();
        item_search_layout.requestLayout();
        for (int i = 0; i < searchResultArr.length(); i++) {
            search_products_tab_view = (LinearLayout) LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.search_products_tab_view, null);
            final JSONObject searchObject = (JSONObject) searchResultArr.get(i);

            TextView product_name_text = (TextView) search_products_tab_view.findViewById(R.id.product_name_text);
            product_name_text.setText(searchObject.getString("product_name"));

            TextView product_details_text = (TextView) search_products_tab_view.findViewById(R.id.product_details_text);
            product_details_text.setText(searchObject.getString("uploaded_in"));

            ImageView product_image = (ImageView)  search_products_tab_view.findViewById(R.id.product_image);
            imageLoader_fragment.DisplayImage(searchObject.getString("product_image"), product_image);
            TextView mybid_amount_text = (TextView) search_products_tab_view.findViewById(R.id.mybid_amount_text);
            mybid_amount_text.setText(searchObject.getString("min_bid_price"));

            TextView latestbid_amount_text = (TextView) search_products_tab_view.findViewById(R.id.latestbid_amount_text);
            latestbid_amount_text.setText(searchObject.getString("latest_bid"));

            final TextView time_left_count = (TextView) search_products_tab_view.findViewById(R.id.time_left_count);

            long timeInMiliseconds = 0;
            try {
                timeInMiliseconds = utils.getTimeInMiliSeconds(searchObject.getString("bid_end"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            new CountDownTimer(timeInMiliseconds, 1000) {

                public void onTick(long millisUntilFinished) {
                    time_left_count.setText(""+String.format("%02d:%02d:%02d:%02d",
                            TimeUnit.MILLISECONDS.toDays( millisUntilFinished),
                            TimeUnit.MILLISECONDS.toHours( millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toHours( millisUntilFinished) / 24),
                            TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished) / 60),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }

                public void onFinish() {
                    time_left_count.setText("00:00:00:00");
                }
            }.start();

            Button comment_button_sale = (Button)  search_products_tab_view.findViewById(R.id.comment_button_sale);
            comment_button_sale.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent commentsClass = new Intent(getApplicationContext(), CommentsActivity.class);
                    try {
                        commentsClass.putExtra("product_id", searchObject.getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(commentsClass);

                }
            });


            TextView comments_count_text = (TextView) search_products_tab_view.findViewById(R.id.comments_count_text);
            if (searchObject.getInt("is_sold") == 1){
                comments_count_text.setText("Sold");
            } else {
                comments_count_text.setText("Not Sold");
            }

            search_products_tab_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ProductDetailsClass = new Intent(SearchActivity.this, ProductDetailsActivity.class);
                    try {
                        ProductDetailsClass.putExtra("SOURCE_PAGE", searchObject.getString("product_name").toString());
                        ProductDetailsClass.putExtra("product_id",searchObject.getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(ProductDetailsClass);

                }
            });

            search_products_tab_view.requestLayout();
            item_search_layout.addView(search_products_tab_view);
        }
    }

    public void DisplayGroupCard(JSONArray searchResultArr) throws JSONException {
        item_search_layout.removeAllViews();
        item_search_layout.requestLayout();
        for (int i = 0; i < searchResultArr.length(); i++) {
            search_group_tab_view = (LinearLayout) LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.search_groups_tab_view, null);
            final JSONObject searchObject = (JSONObject) searchResultArr.get(i);

            TextView group_time_text = (TextView) search_group_tab_view.findViewById(R.id.group_time_text);
            group_time_text.setText(searchObject.getString("group_name"));

            TextView group_name_text = (TextView) search_group_tab_view.findViewById(R.id.group_name_text);
            group_name_text.setText(searchObject.getString("admin_name"));

            TextView pro_count_text = (TextView) search_group_tab_view.findViewById(R.id.pro_count_text);
            pro_count_text.setText(searchObject.getString("products_count"));

            TextView mem_count_text = (TextView) search_group_tab_view.findViewById(R.id.mem_count_text);
            mem_count_text.setText(searchObject.getString("members_count"));

            TextView follow_count_count = (TextView) search_group_tab_view.findViewById(R.id.follow_count_count);
            follow_count_count.setText(searchObject.getString("followers_count"));
            search_group_tab_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent GroupDetailsClass = new Intent(SearchActivity.this,GroupDetailsActivity.class);
                    try {
                        GroupDetailsClass.putExtra("group_id", searchObject.getString("id"));
                    GroupDetailsClass.putExtra("group_name", searchObject.getString("group_name"));
                    GroupDetailsClass.putExtra("admin_name", searchObject.getString("admin_name"));
                    GroupDetailsClass.putExtra("admin_photo", searchObject.getString("admin_photo"));
                    GroupDetailsClass.putExtra("group_image", searchObject.getString("group_image"));
                    GroupDetailsClass.putExtra("group_location", searchObject.getString("admin_location"));
                    GroupDetailsClass.putExtra("followers_count", searchObject.getInt("followers_count"));
                    GroupDetailsClass.putExtra("products_count", searchObject.getInt("products_count"));
                    GroupDetailsClass.putExtra("members_count", searchObject.getInt("members_count"));
                    GroupDetailsClass.putExtra("is_following", searchObject.getInt("is_following"));
                    GroupDetailsClass.putExtra("query", inputSearchText.getText().toString());
                    GroupDetailsClass.putExtra("is_search", 1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(GroupDetailsClass);

                }
            });
            search_group_tab_view.requestLayout();
            item_search_layout.addView(search_group_tab_view);
        }
    }
    public void DisplayPeopleCard(JSONArray searchResultArr) throws JSONException {
        item_search_layout.removeAllViews();
        item_search_layout.requestLayout();
        for (int i = 0; i < searchResultArr.length(); i++) {
            search_people_tab_view = (LinearLayout) LayoutInflater.from(this.getApplicationContext()).inflate(R.layout.search_people_tab_view, null);
            final JSONObject searchObject = (JSONObject) searchResultArr.get(i);

            TextView group_name_text = (TextView) search_people_tab_view.findViewById(R.id.group_name_text);
            group_name_text.setText(searchObject.getString("full_name"));

            CircleImageView rating_pro_image = (CircleImageView)  search_people_tab_view.findViewById(R.id.rating_pro_image);
            imageLoader_fragment.DisplayImage(searchObject.getString("avatar"), rating_pro_image);

            TextView pro_count_text = (TextView) search_people_tab_view.findViewById(R.id.pro_count_text);
            pro_count_text.setText(searchObject.getString("products_count"));

            TextView mem_count_text = (TextView) search_people_tab_view.findViewById(R.id.mem_count_text);
            mem_count_text.setText(searchObject.getString("following_count"));

            TextView follow_count_count = (TextView) search_people_tab_view.findViewById(R.id.follow_count_count);
            follow_count_count.setText(searchObject.getString("followers_count"));

            search_people_tab_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent GroupDetailsClass = new Intent(SearchActivity.this,ProfileDetailsActivity.class);
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