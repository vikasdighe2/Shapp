package com.eysa.shapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;


public class FollowerDetailsActivity extends Activity {

    DatabaseHandler db;
    User loggedInUser;
    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader imageLoader;

    TextView follower_details_page_header_text, follower_name, follower_location_text, pro_count_text, follower_count_text, rating_count_text;
    CircleImageView follower_image;
    ImageView follower_details_page_close_image;

    String FOLLOWER_ID, FOLLOWER_NAME, FOLLOWER_LOCATION, FOLLOWER_IMAGE;
    int RATING_COUNT, PRODUCTS_COUNT, FOLLOWERS_COUNT;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.follower_details_view);

        imageLoader = new ImageLoader(getApplicationContext(), FollowerDetailsActivity.this);
        dataAccess = DataAccess.getInstance(getApplicationContext());

        inflaterData = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        db = DatabaseHandler.getInstance(this);
        loggedInUser = db.getUser();


        pro_count_text = (TextView) this.findViewById(R.id.pro_count_text);
        follower_count_text = (TextView) this.findViewById(R.id.follower_count_text);
        rating_count_text = (TextView) this.findViewById(R.id.rating_count_text);
        follower_details_page_header_text = (TextView) this.findViewById(R.id.follower_details_page_header_text);
        follower_name = (TextView) this.findViewById(R.id.follower_name);
        follower_location_text = (TextView) this.findViewById(R.id.follower_location_text);
        follower_details_page_close_image = (ImageView) this.findViewById(R.id.follower_details_page_close_image);
        follower_image = (CircleImageView) this.findViewById(R.id.follower_image);

        follower_details_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            FOLLOWER_ID = extras.getString("follower_id");
            FOLLOWER_NAME = extras.getString("follower_name");
            FOLLOWER_LOCATION = extras.getString("follower_location");
            FOLLOWER_IMAGE = extras.getString("follower_image");
            RATING_COUNT = extras.getInt("rating_count", 0);
            PRODUCTS_COUNT = extras.getInt("product_count", 0);
            FOLLOWERS_COUNT = extras.getInt("followers_count", 0);
        }

        imageLoader.DisplayImage(FOLLOWER_IMAGE, follower_image);

        follower_details_page_header_text.setText(""+FOLLOWER_NAME);
        follower_name.setText(""+FOLLOWER_NAME);
        follower_location_text.setText(""+FOLLOWER_LOCATION);
        rating_count_text.setText(""+RATING_COUNT);
        pro_count_text.setText(""+PRODUCTS_COUNT);
        follower_count_text.setText(""+FOLLOWERS_COUNT);
       /* GridView gridView = (GridView) this.findViewById(R.id.grid_view);
        gridView.setColumnWidth(R.layout.follower_card);*/

    } /* end of onCreate() */





}
