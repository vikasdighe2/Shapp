package com.eysa.shapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import org.json.JSONObject;

public class StarRatingActivity extends Activity {


    DatabaseHandler db;
    User loggedInUser;
    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader imageLoader;

    String PRODUCT_ID, PRODUCT_NAME, PRODUCT_IMAGE;
    int LATEST_BID, MIN_BID, RATE_VALUE, RATED_BY_USER = 0, RATINGS = 0;

    ImageView rating_page_close_image, star_1, star_2, star_3, star_4, star_5;
    CircleImageView rating_pro_image;
    TextView product_name_text;
    Button rating_submit_button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.star_rating_view);

        imageLoader = new ImageLoader(getApplicationContext(), StarRatingActivity.this);
        dataAccess = DataAccess.getInstance(getApplicationContext());

        inflaterData = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        db = DatabaseHandler.getInstance(this);
        loggedInUser = db.getUser();

        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            PRODUCT_ID = extras.getString("product_id");
            PRODUCT_NAME = extras.getString("product_name");
            MIN_BID = extras.getInt("min_bid_amt", 0);
            RATED_BY_USER = extras.getInt("rated_by_user", 0);
            LATEST_BID = extras.getInt("latest_bid_amt", 0);
            PRODUCT_IMAGE = extras.getString("pro_image");
            RATINGS = extras.getInt("ratings", 0);
        }
        star_1 = (ImageView) this.findViewById(R.id.star_1);
        star_2 = (ImageView) this.findViewById(R.id.star_2);
        star_3 = (ImageView) this.findViewById(R.id.star_3);
        star_4 = (ImageView) this.findViewById(R.id.star_4);
        star_5 = (ImageView) this.findViewById(R.id.star_5);

        if (RATED_BY_USER == 1){
            star_1.setImageResource(R.drawable.star_filled);
            star_2.setImageResource(R.drawable.star_empty);
            star_3.setImageResource(R.drawable.star_empty);
            star_4.setImageResource(R.drawable.star_empty);
            star_5.setImageResource(R.drawable.star_empty);
        } else if(RATED_BY_USER == 2){
            star_1.setImageResource(R.drawable.star_filled);
            star_2.setImageResource(R.drawable.star_filled);
            star_3.setImageResource(R.drawable.star_empty);
            star_4.setImageResource(R.drawable.star_empty);
            star_5.setImageResource(R.drawable.star_empty);
        }  else if(RATED_BY_USER == 3){
            star_1.setImageResource(R.drawable.star_filled);
            star_2.setImageResource(R.drawable.star_filled);
            star_3.setImageResource(R.drawable.star_empty);
            star_4.setImageResource(R.drawable.star_empty);
            star_5.setImageResource(R.drawable.star_empty);
        }  else if(RATED_BY_USER == 4){
            star_1.setImageResource(R.drawable.star_filled);
            star_2.setImageResource(R.drawable.star_filled);
            star_3.setImageResource(R.drawable.star_empty);
            star_4.setImageResource(R.drawable.star_empty);
            star_5.setImageResource(R.drawable.star_empty);
        }  else if(RATED_BY_USER == 5){
            star_1.setImageResource(R.drawable.star_filled);
            star_2.setImageResource(R.drawable.star_filled);
            star_3.setImageResource(R.drawable.star_empty);
            star_4.setImageResource(R.drawable.star_empty);
            star_5.setImageResource(R.drawable.star_empty);
        }

        if (RATINGS == 1){
            star_1.setImageResource(R.drawable.star_filled);
        } else if(RATINGS == 2){
            star_1.setImageResource(R.drawable.star_filled);
            star_2.setImageResource(R.drawable.star_filled);
        } else if (RATINGS == 3){
            star_1.setImageResource(R.drawable.star_filled);
            star_2.setImageResource(R.drawable.star_filled);
            star_3.setImageResource(R.drawable.star_filled);
        } else if(RATINGS == 4){
            star_1.setImageResource(R.drawable.star_filled);
            star_2.setImageResource(R.drawable.star_filled);
            star_3.setImageResource(R.drawable.star_filled);
            star_4.setImageResource(R.drawable.star_filled);
        } else if(RATINGS == 5){
            star_1.setImageResource(R.drawable.star_filled);
            star_2.setImageResource(R.drawable.star_filled);
            star_3.setImageResource(R.drawable.star_filled);
            star_4.setImageResource(R.drawable.star_filled);
            star_5.setImageResource(R.drawable.star_filled);
        }


        rating_page_close_image = (ImageView) this.findViewById(R.id.rating_page_close_image);
        rating_pro_image = (CircleImageView) this.findViewById(R.id.rating_pro_image);
        product_name_text = (TextView) this.findViewById(R.id.product_name_text);
        rating_submit_button = (Button) this.findViewById(R.id.rating_submit_button);



        imageLoader.DisplayImage(PRODUCT_IMAGE, rating_pro_image);
        product_name_text.setText(PRODUCT_NAME);


        rating_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });




        star_1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                RATE_VALUE = 1;

                star_1.setImageResource(R.drawable.star_filled);
                star_2.setImageResource(R.drawable.star_empty);
                star_3.setImageResource(R.drawable.star_empty);
                star_4.setImageResource(R.drawable.star_empty);
                star_5.setImageResource(R.drawable.star_empty);

                return true;
            }
        });


        star_2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                RATE_VALUE = 2;

                star_1.setImageResource(R.drawable.star_filled);
                star_2.setImageResource(R.drawable.star_filled);
                star_3.setImageResource(R.drawable.star_empty);
                star_4.setImageResource(R.drawable.star_empty);
                star_5.setImageResource(R.drawable.star_empty);

                return true;
            }
        });


        star_3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                RATE_VALUE = 3;

                star_1.setImageResource(R.drawable.star_filled);
                star_2.setImageResource(R.drawable.star_filled);
                star_3.setImageResource(R.drawable.star_filled);
                star_4.setImageResource(R.drawable.star_empty);
                star_5.setImageResource(R.drawable.star_empty);

                return true;
            }
        });


        star_4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                RATE_VALUE = 4;

                star_1.setImageResource(R.drawable.star_filled);
                star_2.setImageResource(R.drawable.star_filled);
                star_3.setImageResource(R.drawable.star_filled);
                star_4.setImageResource(R.drawable.star_filled);
                star_5.setImageResource(R.drawable.star_empty);

                return true;
            }
        });


        star_5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                RATE_VALUE = 5;

                star_1.setImageResource(R.drawable.star_filled);
                star_2.setImageResource(R.drawable.star_filled);
                star_3.setImageResource(R.drawable.star_filled);
                star_4.setImageResource(R.drawable.star_filled);
                star_5.setImageResource(R.drawable.star_filled);

                return true;
            }
        });




        rating_submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (RATE_VALUE >= 0) {

                    try {
                        BasicHttpClient httpClientUserData = new BasicHttpClient(Constants.API_URL);
                        httpClientUserData.addHeader("X-Api-Key", loggedInUser.api_key);
                        httpClientUserData.addHeader("X-Api-UserEmail", loggedInUser.email_address);

                        ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                                .add("rating_for", "product")
                                .add("item_id", PRODUCT_ID)
                                .add("item_rating", String.valueOf(RATE_VALUE));
                        httpClientUserData.setConnectionTimeout(2000);
                        HttpResponse httpResponse = httpClientUserData.post(Constants.ADD_RATING_TO_PRODUCT, httpClientUserDataParameters);
                        JSONObject receivedResponse = new JSONObject(httpResponse.getBodyAsString());

                        if (receivedResponse.getInt("status") == 1) {


                            new AlertDialog.Builder(StarRatingActivity.this)
                                    .setTitle("Shapp")
                                    .setMessage("Your request was successfull")
                                    .setIcon(R.drawable.ic_launcher)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            //((MainActivity) getActivity()).onCreateFragBackPressed();
                                            Intent ProductDetailsClass = new Intent(StarRatingActivity.this, ProductDetailsActivity.class);
                                            ProductDetailsClass.putExtra("SOURCE_PAGE", "discover");
                                            ProductDetailsClass.putExtra("product_id",PRODUCT_ID);
                                            startActivity(ProductDetailsClass);
                                        }
                                    }).show();
                        } else {

                            new AlertDialog.Builder(StarRatingActivity.this)
                                    .setTitle("Shapp")
                                    .setMessage("You have already rated this product.")
                                    .setIcon(R.drawable.ic_launcher)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            //((MainActivity) getActivity()).onCreateFragBackPressed();
                                            finish();
                                        }
                                    }).show();

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {

                    new AlertDialog.Builder(StarRatingActivity.this)
                            .setTitle("Shapp")
                            .setMessage("Please select Star(s) before submitting.")
                            .setIcon(R.drawable.ic_launcher)
                            .setNegativeButton("Ok", null).show();
                }

            }
        });
    } /* end of onCreate() */
}