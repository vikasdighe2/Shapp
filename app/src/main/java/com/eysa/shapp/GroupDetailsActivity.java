package com.eysa.shapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.concurrent.TimeUnit;


public class GroupDetailsActivity extends Activity {

    DatabaseHandler db;
    int status = 0;
    Utils utils = new Utils();
    User loggedInUser;
    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader_fragment imageLoader_fragment;
    ImageLoader imageLoader;
    LinearLayout followers_row_container, followers_item_row, products_item_row;
    JSONArray downloadedFollowers;
    TextView group_details_page_header_text, admin_name, admin_location, pro_count_text, mem_count_text, follow_count_count
            , follower, products, members;
    CircleImageView group_admin_image;
    ImageView group_details_page_close_image, group_background, follo_unfollow;

    String GROUP_ID, GROUP_NAME, ADMIN_NAME, ADMIN_PHOTO, GROUP_LOCATION, GROUP_IMAGE, QUERY;
    int IS_FOLLOWING;
    int FOLLOWERS_COUNT, PRODUCTS_COUNT, MEMBERS_COUNT, IS_SEARCH = 0;
    Button invite_btn;
    TextView group_edit_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_details_view);

        imageLoader = new ImageLoader(getApplicationContext(), GroupDetailsActivity.this);
        dataAccess = DataAccess.getInstance(getApplicationContext());

        inflaterData = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        db = DatabaseHandler.getInstance(this);
        loggedInUser = db.getUser();
        imageLoader_fragment = new ImageLoader_fragment(this);
        followers_row_container = (LinearLayout) this.findViewById(R.id.followers_row_container);
        pro_count_text = (TextView) this.findViewById(R.id.pro_count_text);
        mem_count_text = (TextView) this.findViewById(R.id.mem_count_text);
        members = (TextView) this.findViewById(R.id.members);
        products = (TextView) this.findViewById(R.id.products);
        follower = (TextView) this.findViewById(R.id.follower);
        follow_count_count = (TextView) this.findViewById(R.id.follow_count_count);
        group_details_page_header_text = (TextView) this.findViewById(R.id.group_details_page_header_text);
        admin_name = (TextView) this.findViewById(R.id.admin_name);
        admin_location = (TextView) this.findViewById(R.id.admin_location);
        group_details_page_close_image = (ImageView) this.findViewById(R.id.group_details_page_close_image);
        group_background = (ImageView) this.findViewById(R.id.group_background);
        follo_unfollow = (ImageView) this.findViewById(R.id.follo_unfollow);
        group_admin_image = (CircleImageView) this.findViewById(R.id.group_admin_image);
        group_edit_btn=(TextView) this.findViewById(R.id.group_edit_btn);
        invite_btn=(Button) this.findViewById(R.id.invite_btn);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            GROUP_ID = extras.getString("group_id");
            GROUP_NAME = extras.getString("group_name");
            ADMIN_NAME = extras.getString("admin_name");
            ADMIN_PHOTO = extras.getString("admin_photo");
            GROUP_IMAGE = extras.getString("group_image");
            QUERY = extras.getString("query");
            IS_FOLLOWING = extras.getInt("is_following", 0);
            GROUP_LOCATION = extras.getString("group_location");
            FOLLOWERS_COUNT = extras.getInt("followers_count", 0);
            PRODUCTS_COUNT = extras.getInt("products_count", 0);
            MEMBERS_COUNT = extras.getInt("members_count", 0);
            IS_SEARCH = extras.getInt("is_search", 0);
        }

        group_details_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (IS_SEARCH == 1) {
                    Intent GroupDetailsClass = new Intent(GroupDetailsActivity.this, SearchActivity.class);
                    GroupDetailsClass.putExtra("flag", 2);
                    GroupDetailsClass.putExtra("query", QUERY);
                    startActivity(GroupDetailsClass);
                } else {
                    finish();
                }
            }
        });

        imageLoader.DisplayImage(ADMIN_PHOTO, group_admin_image);
        imageLoader_fragment.DisplayImage(GROUP_IMAGE, group_background);

        group_details_page_header_text.setText(""+GROUP_NAME);
        admin_name.setText(""+ADMIN_NAME);
        admin_location.setText(""+GROUP_LOCATION);
        follow_count_count.setText(""+FOLLOWERS_COUNT);
        pro_count_text.setText(""+PRODUCTS_COUNT);
        mem_count_text.setText(""+MEMBERS_COUNT);

        follower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follower.setTextColor(getResources().getColor(R.color.TAB_BAR_SELECTED_COLOR));
                products.setTextColor(getResources().getColor(R.color.TEXT_FIELD_BORDER));
                members.setTextColor(getResources().getColor(R.color.TEXT_FIELD_BORDER));
                JSONArray followerData = dataAccess.getGroupItemDetails(GROUP_ID, "followers");
                try {
                    DisplayFollowerCard(followerData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        products.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follower.setTextColor(getResources().getColor(R.color.TEXT_FIELD_BORDER));
                products.setTextColor(getResources().getColor(R.color.TAB_BAR_SELECTED_COLOR));
                members.setTextColor(getResources().getColor(R.color.TEXT_FIELD_BORDER));
                JSONArray productData = dataAccess.getGroupItemDetails(GROUP_ID, "products");
                try {
                    DisplayProductsCard(productData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        members.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follower.setTextColor(getResources().getColor(R.color.TEXT_FIELD_BORDER));
                products.setTextColor(getResources().getColor(R.color.TEXT_FIELD_BORDER));
                members.setTextColor(getResources().getColor(R.color.TAB_BAR_SELECTED_COLOR));
                JSONArray memberData = dataAccess.getGroupItemDetails(GROUP_ID, "members");
                DisplayMembersCard(memberData);
            }
        });

/********************************follow unfollow check**************************************/
            if (loggedInUser.full_name.equals(ADMIN_NAME)){
                follo_unfollow.setVisibility(View.INVISIBLE);
                group_edit_btn.setVisibility(View.VISIBLE);
                invite_btn.setVisibility(View.VISIBLE);
            }
            if( IS_FOLLOWING == 1){
                follo_unfollow.setImageResource(R.drawable.follow_icon);
            } else {
                follo_unfollow.setImageResource(R.drawable.unfollow);
            }
        follo_unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if( IS_FOLLOWING == 1){
                        follo_unfollow.setImageResource(R.drawable.unfollow);
                        int status = dataAccess.followUnfollowGroupMethod(GROUP_ID);
                    } else {
                        follo_unfollow.setImageResource(R.drawable.follow_icon);
                        int status = dataAccess.followUnfollowGroupMethod(GROUP_ID);
                    }
            }
        });

        invite_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupDetailsActivity.this, InvitePeoplesActivity.class);
                intent.putExtra("groupId", GROUP_ID);
                startActivity(intent);
            }
        });

        group_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupDetailsActivity.this, CreateGroupAcivity.class);
                intent.putExtra("updateFlag", 1);
                intent.putExtra("groupId", GROUP_ID);
                startActivity(intent);
            }
        });


    } /* end of onCreate() */

    public void DisplayFollowerCard(JSONArray followerData) throws JSONException {
        followers_row_container.removeAllViews();
        followers_row_container.requestLayout();

        for(int r=0; r<followerData.length();r++) {
            followers_item_row = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.followers_item_row, null);
            final JSONObject searchObject = (JSONObject) followerData.get(r);


            final ImageView follower_image = (ImageView) followers_item_row.findViewById(R.id.follower_image);
            imageLoader_fragment.DisplayImage(searchObject.getString("avatar"), follower_image);

            TextView follower_name_text = (TextView) followers_item_row.findViewById(R.id.follower_name_text);
            follower_name_text.setText(searchObject.getString("full_name"));

            TextView follower_location_text = (TextView) followers_item_row.findViewById(R.id.follower_location_text);
            follower_location_text.setText(searchObject.getString("location"));

            TextView pro_count_text = (TextView) followers_item_row.findViewById(R.id.pro_count_text);
            pro_count_text.setText(""+searchObject.getString("products_count"));

            TextView following_count_text = (TextView) followers_item_row.findViewById(R.id.following_count_text);
            following_count_text.setText(""+searchObject.getString("following_count"));

            TextView follow_count_count = (TextView) followers_item_row.findViewById(R.id.follow_count_count);
            follow_count_count.setText(""+searchObject.getString("followers_count"));

            Button button_unfollow = (Button) followers_item_row.findViewById(R.id.button_unfollow);
            button_unfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        status = dataAccess.followUnfollowMethod(searchObject.getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (status == 1){
                        Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            followers_item_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent GroupDetailsClass = new Intent(GroupDetailsActivity.this,ProfileDetailsActivity.class);
                    try {
                        GroupDetailsClass.putExtra("profile_id", searchObject.getString("id"));
                    GroupDetailsClass.putExtra("admin_name", searchObject.getString("full_name"));
                    GroupDetailsClass.putExtra("profile_location", searchObject.getString("location"));
                    GroupDetailsClass.putExtra("admin_photo", searchObject.getString("avatar"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(GroupDetailsClass);

                }
            });


            followers_item_row.requestLayout();
            followers_row_container.addView(followers_item_row);

        } /* end of FOR loop */


    }

    public void DisplayProductsCard(JSONArray productData) throws JSONException {
        followers_row_container.removeAllViews();
        followers_row_container.requestLayout();

        for(int r=0; r<productData.length();r++) {
            products_item_row = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.group_product_view, null);
            final JSONObject searchObject = (JSONObject) productData.get(r);


            ImageView product_image = (ImageView) products_item_row.findViewById(R.id.product_image);
            imageLoader_fragment.DisplayImage(searchObject.getString("product_image"), product_image);

            TextView product_name_text = (TextView) products_item_row.findViewById(R.id.product_name_text);
            product_name_text.setText(searchObject.getString("product_name"));

            TextView product_location = (TextView) products_item_row.findViewById(R.id.product_details_text);
            product_location.setText(searchObject.getString("uploaded_in"));

            TextView mybid_amount_text = (TextView) products_item_row.findViewById(R.id.mybid_amount_text);
            mybid_amount_text.setText(""+searchObject.getString("bids_count"));

            TextView latestbid_amount_text = (TextView) products_item_row.findViewById(R.id.latestbid_amount_text);
            latestbid_amount_text.setText(""+searchObject.getString("latest_bid"));

            TextView comments_count_text = (TextView) products_item_row.findViewById(R.id.comments_count_text);
            if (searchObject.getInt("is_sold") == 0) {
                comments_count_text.setText("Not Sold");
            } else {
                comments_count_text.setText("Sold");
            }
            Button bidButton = (Button) products_item_row.findViewById(R.id.buttun_type_two);
            Button comment_button = (Button) products_item_row.findViewById(R.id.comment_button_sale);
            if (searchObject.getInt("bid_closed") == 1) {
                bidButton.setText(R.string.bid_closed);
                bidButton.setBackgroundResource(R.drawable.btn_bid_closed);
            } else {
                bidButton.setText(R.string.bid);
                bidButton.setBackgroundResource(R.drawable.btn_bid_now);
                bidButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent biddingClass = new Intent(getApplicationContext(), BiddingActivity.class);
                        try {
                            biddingClass.putExtra("product_id", searchObject.getString("id"));
                        biddingClass.putExtra("min_bid_amt", searchObject.getString("min_bid_price"));
                        biddingClass.putExtra("latest_bid_amt", searchObject.getString("latest_bid"));
                        biddingClass.putExtra("pro_image", searchObject.getString("product_image"));
                        biddingClass.putExtra("product_name", searchObject.getString("product_name"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(biddingClass);
                    }
                });

                comment_button.setOnClickListener(new View.OnClickListener() {
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
            }

            final TextView time_left_count = (TextView) products_item_row.findViewById(R.id.time_left_count);
            long timeInMiliseconds = 0;
            try {
                timeInMiliseconds = utils.getTimeInMiliSeconds(searchObject.getString("bid_end"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            new CountDownTimer(timeInMiliseconds, 1000) {

                public void onTick(long millisUntilFinished) {
                    time_left_count.setText(""+String.format("%02d:%02d:%02d:%02ds",
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


            products_item_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ProductDetailsClass = new Intent(GroupDetailsActivity.this, ProductDetailsActivity.class);
                    ProductDetailsClass.putExtra("SOURCE_PAGE", "my_bids");
                    try {
                        ProductDetailsClass.putExtra("product_id", searchObject.getString("id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(ProductDetailsClass);

                }
            });


            products_item_row.requestLayout();
            followers_row_container.addView(products_item_row);

        } /* end of FOR loop */

    }

    public void DisplayMembersCard(JSONArray memberData){

    }

}
