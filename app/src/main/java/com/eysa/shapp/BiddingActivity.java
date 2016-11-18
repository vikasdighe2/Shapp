package com.eysa.shapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import org.json.JSONObject;


public class BiddingActivity extends Activity {


    DatabaseHandler db;
    User loggedInUser;
    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader imageLoader;

    EditText enter_bidding_amt_field;
    TextView latestbid_text,bidding_page_header_text;
    CircleImageView bidding_pro_image;
    LinearLayout bid_button;
    ImageView bidding_page_close_image;

    String PRODUCT_ID, PRODUCT_NAME, PRODUCT_IMAGE;
    int LATEST_BID, MIN_BID, BUY_NOW;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bidding_view);

        imageLoader = new ImageLoader(getApplicationContext(), BiddingActivity.this);
        dataAccess = DataAccess.getInstance(getApplicationContext());

        inflaterData = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        db = DatabaseHandler.getInstance(this);
        loggedInUser = db.getUser();


        enter_bidding_amt_field = (EditText) this.findViewById(R.id.enter_bidding_amt_field);
        latestbid_text = (TextView) this.findViewById(R.id.latestbid_text);
        bidding_page_header_text = (TextView) this.findViewById(R.id.bidding_page_header_text);
        bidding_page_header_text.setText(R.string.product);
        bidding_pro_image = (CircleImageView) this.findViewById(R.id.bidding_pro_image);
        bid_button = (LinearLayout) this.findViewById(R.id.bid_button);
        bidding_page_close_image = (ImageView) this.findViewById(R.id.bidding_page_close_image);


        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            PRODUCT_ID = extras.getString("product_id");
            PRODUCT_NAME = extras.getString("product_name");
            MIN_BID = extras.getInt("min_bid_amt", 0);
            LATEST_BID = extras.getInt("latest_bid_amt", 0);
            BUY_NOW = extras.getInt("buy_now", 0);
            PRODUCT_IMAGE = extras.getString("pro_image");
        }

        imageLoader.DisplayImage(PRODUCT_IMAGE, bidding_pro_image);
        latestbid_text.setText(""+LATEST_BID);

        bidding_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        bid_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int BID_AMT = 0;
                if (!(enter_bidding_amt_field.getText().toString()).equals("")) {
                    try {
                        BID_AMT = Integer.parseInt(enter_bidding_amt_field.getText().toString());
                    } catch (NumberFormatException e){
                        e.printStackTrace();
                    }
                }
                if (BID_AMT <= BUY_NOW) {

                    if (BID_AMT >= MIN_BID) {

                        if (BID_AMT >= LATEST_BID) {

                            try {
                                BasicHttpClient httpClientUserData = new BasicHttpClient(Constants.API_URL);
                                ;
                                httpClientUserData.addHeader("X-Api-Key", loggedInUser.api_key);
                                httpClientUserData.addHeader("X-Api-UserEmail", loggedInUser.email_address);

                                ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                                        .add("product_id", PRODUCT_ID)
                                        .add("bid_amount", String.valueOf(BID_AMT));
                                httpClientUserData.setConnectionTimeout(60000);
                                HttpResponse httpResponseUserData = httpClientUserData.post(Constants.ADD_BID_TO_PRODUCT, httpClientUserDataParameters);

                                final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());


                                if (DataObject.getInt("status") == 1) {


                                    new AlertDialog.Builder(BiddingActivity.this)
                                            .setTitle("Shapp")
                                            .setMessage(R.string.your_bidding_amount_added_successfully)
                                            .setIcon(R.drawable.app_icon)
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    //((MainActivity) getActivity()).onCreateFragBackPressed();
                                                    dataAccess.downloadMyBids();
                                                    dataAccess.downloadSavedShapps();
                                                    finish();
                                                }
                                            }).show();
                                } else {

                                    new AlertDialog.Builder(BiddingActivity.this)
                                            .setTitle("Shapp")
                                            .setMessage(DataObject.getString("message"))
                                            .setIcon(R.drawable.app_icon)
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

                            new AlertDialog.Builder(BiddingActivity.this)
                                    .setTitle("Shapp")
                                    .setMessage(R.string.bidding_should_be_greater_than_latest_bidding_amount)
                                    .setIcon(R.drawable.app_icon)
                                    .setNegativeButton("Ok", null).show();
                        }


                    } else {
                        new AlertDialog.Builder(BiddingActivity.this)
                                .setTitle("Shapp")
                                .setMessage(R.string.bidding_amount_should_be_greater_than_min_amount)
                                .setIcon(R.drawable.app_icon)
                                .setNegativeButton("Ok", null).show();

                    }
                } else{
                    new AlertDialog.Builder(BiddingActivity.this)
                            .setTitle("Shapp")
                            .setMessage(R.string.bidding_amount_should_be_less_than_buy_now_price)
                            .setIcon(R.drawable.app_icon)
                            .setNegativeButton("Ok", null).show();
                }


            }
        });




    } /* end of OnCreate() */






}