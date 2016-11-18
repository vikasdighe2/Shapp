package com.eysa.shapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MyBidsActivity extends Activity {

    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader imageLoader;
    ImageLoader_fragment imageLoader_fragment;
    Utils utils = new Utils();
    int status = 0;

    LinearLayout mybids_product_row_container, products_item_row;
    List<Products> downloadedMyBids;

    ImageView mybids_page_close_image;
    TextView mybids_page_header_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybids_view);

        mybids_product_row_container = (LinearLayout) this.findViewById(R.id.mybids_product_row_container);
        inflaterData = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader_fragment = new ImageLoader_fragment(this);
        imageLoader = new ImageLoader(getApplicationContext(), MyBidsActivity.this);
        dataAccess = DataAccess.getInstance(getApplicationContext());
        downloadedMyBids = dataAccess.get_downloadedMyBidsData();


        mybids_page_header_text = (TextView) this.findViewById(R.id.mybids_page_header_text);
        mybids_page_header_text.setText(R.string.my_bid);

        mybids_page_close_image = (ImageView) this.findViewById(R.id.mybids_page_close_image);
        mybids_page_close_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR), PorterDuff.Mode.SRC_ATOP));
        mybids_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        if(downloadedMyBids.size() > 0){
            DisplayMyBids();
        }
        else {
            dataAccess.downloadMyBids();
        }

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(eventReceiver, new IntentFilter("mybids_downloaded"));



    } /* end of onCreate() */


    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(MyBidsActivity.this != null) {
                DisplayMyBids();
            }
        }
    };





    public void DisplayMyBids(){

        downloadedMyBids = dataAccess.get_downloadedMyBidsData();
        mybids_product_row_container.removeAllViews();
        mybids_product_row_container.requestLayout();

        for(int r=0; r<downloadedMyBids.size();r++) {
            products_item_row = (LinearLayout) LayoutInflater.from(MyBidsActivity.this).inflate(R.layout.bid_product, null);
            final Products prodObj = downloadedMyBids.get(r);


            ImageView product_image = (ImageView) products_item_row.findViewById(R.id.product_image1);
            imageLoader_fragment.DisplayImage(prodObj.product_image, product_image);

            TextView product_name_text = (TextView) products_item_row.findViewById(R.id.product_name_text1);
            product_name_text.setText(prodObj.product_name);

            TextView my_bids_comment_count = (TextView) products_item_row.findViewById(R.id.my_bids_comment_count);
            my_bids_comment_count.setText("" + prodObj.comments_count);

            TextView product_details_text = (TextView) products_item_row.findViewById(R.id.product_details_text1);
            product_details_text.setText(prodObj.uploaded_in);

            TextView mybid_amount_text = (TextView) products_item_row.findViewById(R.id.mybid_amount_text1);
            mybid_amount_text.setText(""+prodObj.my_bid + " Kr");
            TextView mybid_text = (TextView) products_item_row.findViewById(R.id.mybid_text);
            if (prodObj.my_bid > prodObj.latest_bid){
                mybid_amount_text.setTextColor(getResources().getColor(R.color.BID_NOW_COLOR));
                mybid_text.setTextColor(getResources().getColor(R.color.BID_NOW_COLOR));
            }

            TextView latestbid_amount_text = (TextView) products_item_row.findViewById(R.id.latestbid_amount_text1);
            latestbid_amount_text.setText(""+prodObj.latest_bid + " Kr");

            final TextView time_left_count1 = (TextView) products_item_row.findViewById(R.id.time_left_count1);
            long timeInMiliseconds = 0;
            try {
                timeInMiliseconds = utils.getTimeInMiliSeconds(prodObj.bid_end);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            new CountDownTimer(timeInMiliseconds, 1000) {

                public void onTick(long millisUntilFinished) {
                    time_left_count1.setText(""+String.format("%02d:%02d:%02d:%02ds",
                            TimeUnit.MILLISECONDS.toDays( millisUntilFinished),
                            TimeUnit.MILLISECONDS.toHours( millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toHours( millisUntilFinished) / 24),
                            TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toMinutes( millisUntilFinished) / 60),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }

                public void onFinish() {
                    time_left_count1.setText("00:00:00:00");
                }
            }.start();

            Button comment_button = (Button) products_item_row.findViewById(R.id.comment_button1);
            if (prodObj.bid_closed == 1) {
                comment_button.setText(R.string.bid_closed);
                comment_button.setBackgroundResource(R.drawable.btn_bid_closed);
            } else {
                comment_button.setText(R.string.bid);
                comment_button.setBackgroundResource(R.drawable.btn_bid_now);
                comment_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent biddingClass = new Intent(getApplicationContext(), BiddingActivity.class);
                        biddingClass.putExtra("product_id", prodObj.id);
                        biddingClass.putExtra("min_bid_amt", prodObj.min_bid_price);
                        biddingClass.putExtra("latest_bid_amt", prodObj.latest_bid);
                        biddingClass.putExtra("pro_image", prodObj.product_image);
                        biddingClass.putExtra("product_name", prodObj.product_name);
                        biddingClass.putExtra("buy_now", prodObj.buy_now_price);
                        startActivity(biddingClass);
                    }
                });
            }

            Button buy_button = (Button) products_item_row.findViewById(R.id.buy_button1);
            buy_button.setText("BUY $"+prodObj.buy_now_price);
            buy_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyBidsActivity.this);
                    alertDialog.setTitle("Shapp App");

                    // Setting Dialog Message
                    alertDialog.setMessage(R.string.are_you_sure_want_to_buy);
                    alertDialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int which) {
                                    status = dataAccess.BuyProduct(prodObj.id);
                                    if (status == 1){
                                        Toast.makeText(MyBidsActivity.this, ""+ R.string.admin_will_contact_you , Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    alertDialog.setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });
                    alertDialog.show();

                }
            });

            products_item_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent ProductDetailsClass = new Intent(MyBidsActivity.this, ProductDetailsActivity.class);
                    ProductDetailsClass.putExtra("SOURCE_PAGE", "my_bids");
                    ProductDetailsClass.putExtra("product_id", prodObj.id);
                    startActivity(ProductDetailsClass);

                }
            });
            products_item_row.requestLayout();
            mybids_product_row_container.addView(products_item_row);

        } /* end of FOR loop */
    } /* end of DisplayMyBids() */
}
