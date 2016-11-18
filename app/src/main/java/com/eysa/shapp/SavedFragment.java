package com.eysa.shapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


public class SavedFragment extends Fragment {

    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader_fragment imageLoader_fragment;

    View rootView;
    LinearLayout mybids_product_row_container, products_item_row;
    List<Products> downloadedSavedShapps;
    List<Products> data;
    Utils utils = new Utils();
    ImageView mybids_page_close_image;
    TextView mybids_page_header_text;
    int status = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.saved_shapp_fragment, container, false);

        mybids_product_row_container = (LinearLayout) rootView.findViewById(R.id.mybids_product_row_container);
        inflaterData = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader_fragment = new ImageLoader_fragment(getActivity().getApplicationContext());
        dataAccess = DataAccess.getInstance(getActivity().getApplicationContext());
        downloadedSavedShapps = dataAccess.get_downloadedSavedShappsData();

        mybids_page_header_text = (TextView) rootView.findViewById(R.id.mybids_page_header_text);
        mybids_page_header_text.setText("Saved Shapps");

        mybids_page_close_image = (ImageView) rootView.findViewById(R.id.mybids_page_close_image);
        mybids_page_close_image.setVisibility(View.GONE);
        /*mybids_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/
        dataAccess.downloadSavedShapps();
        if(downloadedSavedShapps.size() > 0){
            DisplaySavedShapps();
        }
        else {
            dataAccess.downloadSavedShapps();
        }

        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(eventReceiver, new IntentFilter("saved_shapps_downloaded"));



        return rootView;
    } /* end of onCreateView() */


    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(SavedFragment.this != null) {
                DisplaySavedShapps();
            }
        }
    };





    public void DisplaySavedShapps(){

        downloadedSavedShapps = dataAccess.get_downloadedSavedShappsData();
        mybids_product_row_container.removeAllViews();
        mybids_product_row_container.requestLayout();
        List<String> id = new ArrayList<>();

        for(int r=0; r<downloadedSavedShapps.size();r++) {
            try {
                products_item_row = (LinearLayout) LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.products_item_row, null);
                final Products prodObj = downloadedSavedShapps.get(r);
                if (!id.contains(prodObj.product_name)) {
                    id.add(prodObj.product_name);
                    ImageView product_image = (ImageView) products_item_row.findViewById(R.id.product_image_saved);
                    imageLoader_fragment.DisplayImage(prodObj.product_image, product_image);
                    TextView product_name_text = (TextView) products_item_row.findViewById(R.id.product_name_text);
                    product_name_text.setText(prodObj.product_name);

          /*  TextView product_details_text = (TextView) products_item_row.findViewById(R.id.product_details_text);
            product_details_text.setText("");*/

                    TextView saved_shapp_comment_count = (TextView) products_item_row.findViewById(R.id.saved_shapp_comment_count);
                    saved_shapp_comment_count.setText("" + prodObj.comments_count);

                    TextView mybid_amount_text = (TextView) products_item_row.findViewById(R.id.mybid_amount_text);
                    mybid_amount_text.setText("" + prodObj.my_bid + " Kr");

                    TextView latestbid_amount_text = (TextView) products_item_row.findViewById(R.id.latestbid_amount_text);
                    latestbid_amount_text.setText("" + prodObj.latest_bid + " Kr");

                    final TextView time_left_count = (TextView) products_item_row.findViewById(R.id.time_left_count);
                    long timeInMiliseconds = 0;
                    try {
                        timeInMiliseconds = utils.getTimeInMiliSeconds(prodObj.bid_end);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    new CountDownTimer(timeInMiliseconds, 1000) {

                        public void onTick(long millisUntilFinished) {
                            time_left_count.setText("" + String.format("%02d:%02d:%02d:%02ds",
                                    TimeUnit.MILLISECONDS.toDays(millisUntilFinished),
                                    TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toHours(millisUntilFinished) / 24),
                                    TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) / 60),
                                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                        }

                        public void onFinish() {
                            time_left_count.setText("00:00:00:00");
                        }
                    }.start();

          /*  TextView comments_count_text = (TextView) products_item_row.findViewById(R.id.comments_count_text);
            comments_count_text.setText(""+prodObj.comments_count);*/

                    Button buttun_type_one = (Button) products_item_row.findViewById(R.id.buttun_type_one);
                    buttun_type_one.setText("Buy " + prodObj.buy_now_price + " Kr");
                    buttun_type_one.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                            alertDialog.setTitle("Shapp App");

                            // Setting Dialog Message
                            alertDialog.setMessage(R.string.are_you_sure_want_to_buy);
                            alertDialog.setPositiveButton("Yes",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            status = dataAccess.BuyProduct(prodObj.id);
                                            if (status == 1) {
                                                Toast.makeText(getActivity(), "" + R.string.admin_will_contact_you, Toast.LENGTH_SHORT).show();
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
                    Button buttun_type_two = (Button) products_item_row.findViewById(R.id.buttun_type_two);
                    if (prodObj.bid_closed == 1) {
                        buttun_type_two.setText(R.string.bid_closed);
                    } else {
                        buttun_type_two.setText(R.string.bid);
                        buttun_type_two.setBackgroundResource(R.drawable.btn_bid_now);
                        buttun_type_two.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent biddingClass = new Intent(getActivity().getApplicationContext(), BiddingActivity.class);
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

                    products_item_row.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent ProductDetailsClass = new Intent(getActivity().getApplicationContext(), ProductDetailsActivity.class);
                            ProductDetailsClass.putExtra("SOURCE_PAGE", "my_bids");
                            ProductDetailsClass.putExtra("product_id", prodObj.id);
                            startActivity(ProductDetailsClass);

                        }
                    });


                    products_item_row.requestLayout();
                    mybids_product_row_container.addView(products_item_row);
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        } /* end of FOR loop */


    } /* end of DisplaySavedShapps() */





}
