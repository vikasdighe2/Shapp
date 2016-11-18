package com.eysa.shapp;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class MySalesActivity extends Activity {

    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader_fragment imageLoader_fragment;
    ImageLoader imageLoader;
    Utils utils = new Utils();

    LinearLayout mybids_product_row_container, products_item_row;
    List<Products> downloadedMySales;

    ImageView mybids_page_close_image;
    TextView mybids_page_header_text;
    String text = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mybids_view);

        mybids_product_row_container = (LinearLayout) this.findViewById(R.id.mybids_product_row_container);
        inflaterData = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = new ImageLoader(getApplicationContext(), MySalesActivity.this);
        imageLoader_fragment = new ImageLoader_fragment(this);
        dataAccess = DataAccess.getInstance(getApplicationContext());
        downloadedMySales = dataAccess.get_downloadedMySalesData();

        mybids_page_close_image = (ImageView) this.findViewById(R.id.mybids_page_close_image);
        mybids_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mybids_page_header_text = (TextView) this.findViewById(R.id.mybids_page_header_text);
        mybids_page_header_text.setText(R.string.my_sales);


        if(downloadedMySales.size() > 0){
            DisplayMySales();
        }
        else {
            dataAccess.downloadMySales();
        }

        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(eventReceiver, new IntentFilter("mysales_downloaded"));



    } /* end of onCreate() */


    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(MySalesActivity.this != null) {
                DisplayMySales();
            }
        }
    };





    public void DisplayMySales(){

        downloadedMySales = dataAccess.get_downloadedMySalesData();
        mybids_product_row_container.removeAllViews();
        mybids_product_row_container.requestLayout();

        for(int r=0; r<downloadedMySales.size();r++) {
            products_item_row = (LinearLayout) LayoutInflater.from(MySalesActivity.this).inflate(R.layout.sale_product_view, null);
            final Products prodObj = downloadedMySales.get(r);


            ImageView product_image = (ImageView) products_item_row.findViewById(R.id.product_image);
            imageLoader_fragment.DisplayImage(prodObj.product_image, product_image);

            TextView product_name_text = (TextView) products_item_row.findViewById(R.id.product_name_text);
            product_name_text.setText(prodObj.product_name);

            TextView product_location = (TextView) products_item_row.findViewById(R.id.product_details_text);
            product_location.setText(prodObj.uploaded_in);

            TextView mybid_amount_text = (TextView) products_item_row.findViewById(R.id.mybid_amount_text);
            mybid_amount_text.setText(""+prodObj.bids_count);

            TextView latestbid_amount_text = (TextView) products_item_row.findViewById(R.id.latestbid_amount_text);
            latestbid_amount_text.setText(""+prodObj.latest_bid + " Kr");

            TextView comments_count_text = (TextView) products_item_row.findViewById(R.id.comments_count_text);
            if (prodObj.is_sold == 0) {
                comments_count_text.setText("Not Sold");
            } else {
                comments_count_text.setText("Sold");
            }
            Button bidButton = (Button) products_item_row.findViewById(R.id.buttun_type_two);
            Button comment_button = (Button) products_item_row.findViewById(R.id.comment_button_sale);
            if (prodObj.bid_closed == 1) {
                bidButton.setText(R.string.bid_closed);
                bidButton.setBackgroundResource(R.drawable.btn_bid_closed);
            } else {
                bidButton.setText(R.string.bid);
                bidButton.setBackgroundResource(R.drawable.btn_bid_now);
                bidButton.setOnClickListener(new View.OnClickListener() {
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

                comment_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent commentsClass = new Intent(getApplicationContext(), CommentsActivity.class);
                        commentsClass.putExtra("product_id", prodObj.id);
                        startActivity(commentsClass);

                    }
                });
            }

            final TextView time_left_count = (TextView) products_item_row.findViewById(R.id.time_left_count);
            long timeInMiliseconds = 0;
            try {
                timeInMiliseconds = utils.getTimeInMiliSeconds(prodObj.bid_end);
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
                    Intent ProductDetailsClass = new Intent(MySalesActivity.this, ProductDetailsActivity.class);
                    ProductDetailsClass.putExtra("SOURCE_PAGE", "my_bids");
                    ProductDetailsClass.putExtra("product_id", prodObj.id);
                    startActivity(ProductDetailsClass);

                }
            });


            products_item_row.requestLayout();
            mybids_product_row_container.addView(products_item_row);

        } /* end of FOR loop */


    } /* end of DisplayMySales() */

    public String getTimer(){

        return text;
    }

}
