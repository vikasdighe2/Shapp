package com.eysa.shapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.eysa.shapp.tindercard.FlingCardListener;
import com.eysa.shapp.tindercard.SwipeFlingAdapterView;
import com.fasterxml.jackson.databind.node.LongNode;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class DiscoverFragment extends Fragment implements FlingCardListener.ActionDownInterface {

    View rootView;
    public static MyAppAdapter myAppAdapter;
    public static ViewHolder viewHolder;
    private SwipeFlingAdapterView flingContainer;
    Utils utils = new Utils();
    DataAccess dataAccess;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    LayoutInflater inflaterData;
    ImageLoader_fragment imageLoader_fragment;
    int flag = 0, status = 0;
    List<Products> downloadedDiscover;
    ImageView left_drawer_control_button, discover_page_header_image, search_control_button, filter_control;

    int windowwidth;
    int screenCenter;
    int x_cord, y_cord, x, y;
    int Likes = 0;
    RelativeLayout discover_row_container;
    float alphaValue = 0;

    public static void removeBackground() {


        // viewHolder.background.setVisibility(View.GONE);
        myAppAdapter.notifyDataSetChanged();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.discover_view, container, false);

        flingContainer = (SwipeFlingAdapterView) rootView.findViewById(R.id.frame);

        discover_row_container = (RelativeLayout) rootView.findViewById(R.id.discover_row_container);
        inflaterData = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        windowwidth = size.x;
        screenCenter = windowwidth / 2;

        imageLoader_fragment = new ImageLoader_fragment(getActivity().getApplicationContext());
        dataAccess = DataAccess.getInstance(getActivity().getApplicationContext());

        left_drawer_control_button = (ImageView) rootView.findViewById(R.id.left_drawer_control_button);
        search_control_button = (ImageView) rootView.findViewById(R.id.search_control_button);
        discover_page_header_image = (ImageView) rootView.findViewById(R.id.discover_page_header_image);
        filter_control = (ImageView) rootView.findViewById(R.id.filter_control);
        left_drawer_control_button.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.WHITE_COLOR), PorterDuff.Mode.SRC_ATOP));
        discover_page_header_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.WHITE_COLOR), PorterDuff.Mode.SRC_ATOP));
        filter_control.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), FilterActivity.class);
                startActivity(intent);
            }
        });

        discover_page_header_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DiscoverFragment discoverFragment = new DiscoverFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_place_holder, discoverFragment, "discoverFragment");
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        left_drawer_control_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                ((MainActivity) getActivity()).open_left_drawer();
                return false;
            }
        });

        search_control_button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                Intent searchClass = new Intent(getActivity().getApplicationContext(), SearchActivity.class);
                searchClass.putExtra("SOURCE_PAGE", "discover");
                startActivity(searchClass);
                return false;
            }
        });
        downloadedDiscover = dataAccess.get_downloadedDiscoverData();
        if (downloadedDiscover.size() > 0) {
            DisplayDiscoverData();
        }

        dataAccess.downloadDiscoverData(1);

        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(eventReceiver, new IntentFilter("discover_downloaded"));

        return rootView;
    } /* end of onCreateView() */


    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DisplayDiscoverData();
        }
    };


    public void DisplayDiscoverData() {
        downloadedDiscover = dataAccess.get_downloadedDiscoverData();
        final List<Products> downloadedDiscoverBackUp = dataAccess.get_downloadedDiscoverData();
        myAppAdapter = new MyAppAdapter(downloadedDiscover, getActivity());
        flingContainer.setAdapter(myAppAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {

            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                flag++;
                myAppAdapter.notifyDataSetChanged();
                downloadedDiscover.remove(0);
                if (flag == downloadedDiscoverBackUp.size()) {
                    startScheduleTask();
                }
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                flag++;
                downloadedDiscover.remove(0);
                myAppAdapter.notifyDataSetChanged();
                try {
                    Intent biddingClass = new Intent(getActivity().getApplicationContext(), BiddingActivity.class);
                    biddingClass.putExtra("product_id", downloadedDiscoverBackUp.get(flag - 1).id);
                    biddingClass.putExtra("min_bid_amt", downloadedDiscoverBackUp.get(flag - 1).min_bid_price);
                    biddingClass.putExtra("latest_bid_amt", downloadedDiscoverBackUp.get(flag - 1).latest_bid);
                    biddingClass.putExtra("pro_image", downloadedDiscoverBackUp.get(flag - 1).product_image);
                    biddingClass.putExtra("product_name", downloadedDiscoverBackUp.get(flag - 1).product_name);
                    biddingClass.putExtra("buy_now", downloadedDiscoverBackUp.get(flag - 1).buy_now_price);
                    startActivity(biddingClass);
                   /* if (flag == downloadedDiscoverBackUp.size()) {
                        startScheduleTask();
                    }*/
                } catch (IndexOutOfBoundsException E) {
                    E.printStackTrace();
                }
            }

            @Override
            public void OnDownExit(Object dataObject) {
                try {
                    flag++;
                    myAppAdapter.notifyDataSetChanged();
                    downloadedDiscover.remove(0);
                    dataAccess.AddRemoveSavedShapp(downloadedDiscoverBackUp.get(flag - 1).id);
                    if (flag == downloadedDiscoverBackUp.size()) {
                        startScheduleTask();
                    }
                } catch (IndexOutOfBoundsException E) {
                    E.printStackTrace();
                }

            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {

            }

            @Override
            public void onScroll(float scrollProgressPercent) {
                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.discover_item_block);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
                view.findViewById(R.id.down_view).setAlpha(scrollProgressPercent > 0 ? 1 - scrollProgressPercent : 0);
            }
        });

        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                try {
                    Intent ProductDetailsClass = new Intent(getActivity().getApplicationContext(), ProductDetailsActivity.class);
                    ProductDetailsClass.putExtra("SOURCE_PAGE", "discover");
                    ProductDetailsClass.putExtra("product_id", downloadedDiscoverBackUp.get(flag).id);
                    startActivity(ProductDetailsClass);
                } catch (IndexOutOfBoundsException E) {
                    E.printStackTrace();
                }
            }
        });

    } /* end of DisplayMyBids() */

    @Override
    public void onActionDownPerform() {
        Log.e("action", "bingo");
    }

    public static class ViewHolder {
        public static FrameLayout background;
        public ImageView productImage;
        public CircleImageView discoverMemberImage;
        public TextView userNameText;
        public TextView userLocationText;
        public TextView discoverItemLocation;
        public TextView productId;
        public Button bidButton;
        public TextView discoverItemNameText;
        public TextView minpriceAmountText;
        public TextView buynowText;
        public TextView latestbidAmountText;
        public TextView bidEndTimer;
        public LinearLayout buyNowButton;
        public RelativeLayout userProfile;

        public ImageView tinder_fav_icon;
        public ImageView verified_icon;
        public TextView discover_item_km;
    }

    public class MyAppAdapter extends BaseAdapter {
        public double userLatitude=0.0d;
        public double userLongitude=0.0d;
        public double productLatitude=0.0d;
        public double productLongitude=0.0d;
        public double distance=0.0d;
        public double dist=0.0d;

        public List<Products> parkingList;
        public Context context;

        private MyAppAdapter(List<Products> apps, Context context) {
            this.parkingList = apps;
            this.context = context;
        }

        @Override
        public int getCount() {
            return parkingList.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            View rowView = convertView;

            if (rowView == null) {

                LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = inflater.inflate(R.layout.discover_item_row, parent, false);
                // configure view holder
                viewHolder = new ViewHolder();
                viewHolder.discoverItemNameText = (TextView) rowView.findViewById(R.id.discover_item_name_text);
                viewHolder.background = (FrameLayout) rowView.findViewById(R.id.discover_item_block);
                viewHolder.productImage = (ImageView) rowView.findViewById(R.id.discover_item_image);
                viewHolder.userNameText = (TextView) rowView.findViewById(R.id.username_text);
                viewHolder.discoverMemberImage = (CircleImageView) rowView.findViewById(R.id.discover_memberImage);
                viewHolder.userLocationText = (TextView) rowView.findViewById(R.id.user_location_text);
                viewHolder.discoverItemLocation = (TextView) rowView.findViewById(R.id.discover_item_location);
                viewHolder.bidButton = (Button) rowView.findViewById(R.id.bid_button);
                viewHolder.minpriceAmountText = (TextView) rowView.findViewById(R.id.minprice_amount_text);
                viewHolder.buynowText = (TextView) rowView.findViewById(R.id.buynow_value_text);
                viewHolder.latestbidAmountText = (TextView) rowView.findViewById(R.id.latestbid_amount_text);
                viewHolder.bidEndTimer = (TextView) rowView.findViewById(R.id.timeleft_amount_text);
                viewHolder.productId = (TextView) rowView.findViewById(R.id.product_id);
                viewHolder.buyNowButton = (LinearLayout) rowView.findViewById(R.id.buy_now_button);
                viewHolder.userProfile = (RelativeLayout) rowView.findViewById(R.id.user_profile);
                viewHolder.verified_icon = (ImageView) rowView.findViewById(R.id.verified_icon);
                viewHolder.discover_item_km = (TextView) rowView.findViewById(R.id.discover_item_km);
                rowView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.discoverItemNameText.setText(parkingList.get(position).product_name);
            viewHolder.userNameText.setText(parkingList.get(position).uploaded_by_name);
            imageLoader_fragment.DisplayImage(parkingList.get(position).product_image, viewHolder.productImage);
            imageLoader_fragment.DisplayImage(parkingList.get(position).uploaded_by_image, viewHolder.discoverMemberImage);
            viewHolder.userLocationText.setText("");
            viewHolder.discoverItemLocation.setText(parkingList.get(position).uploaded_in);
            viewHolder.productId.setText(parkingList.get(position).id);
            if(parkingList.get(position).user_is_premium==1){
                viewHolder.verified_icon.setVisibility(View.VISIBLE);
            }else {
                viewHolder.verified_icon.setVisibility(View.GONE);
            }

            //CalculateDistance(parkingList.get(position).latitude, parkingList.get(position).longitude, viewHolder.discover_item_km);
            if(!(parkingList.get(position).latitude).equals("0.0") && !(parkingList.get(position).longitude).equals("0.0")){
                distance(parkingList.get(position).latitude, parkingList.get(position).longitude, viewHolder.discover_item_km);
            }

            if (parkingList.get(position).bid_closed == 1) {
                viewHolder.bidButton.setText(R.string.bid_closed);
                viewHolder.bidButton.setBackgroundResource(R.drawable.btn_bid_closed);
            } else {
                viewHolder.bidButton.setText(R.string.bid);
                viewHolder.bidButton.setBackgroundResource(R.drawable.btn_bid_now);
                viewHolder.bidButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent biddingClass = new Intent(getActivity().getApplicationContext(), BiddingActivity.class);
                        biddingClass.putExtra("product_id", parkingList.get(position).id);
                        biddingClass.putExtra("min_bid_amt", parkingList.get(position).min_bid_price);
                        biddingClass.putExtra("latest_bid_amt", parkingList.get(position).latest_bid);
                        biddingClass.putExtra("pro_image", parkingList.get(position).product_image);
                        biddingClass.putExtra("product_name", parkingList.get(position).product_name);
                        biddingClass.putExtra("buy_now", parkingList.get(position).buy_now_price);
                        startActivity(biddingClass);
                    }
                });
            }
            viewHolder.minpriceAmountText.setText(parkingList.get(position).min_bid_price + "" + " Kr");
            viewHolder.buynowText.setText(parkingList.get(position).buy_now_price + "" + " Kr");
            viewHolder.latestbidAmountText.setText(parkingList.get(position).latest_bid + "" + " Kr");
            long timeInMiliseconds = 0;
            try {
                timeInMiliseconds = utils.getTimeInMiliSeconds(parkingList.get(position).bid_end);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            final TextView text =  viewHolder.bidEndTimer;
            new CountDownTimer(timeInMiliseconds, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    text.setText("" + String.format("%02d:%02d:%02d:%02ds",
                            TimeUnit.MILLISECONDS.toDays(millisUntilFinished),
                            TimeUnit.MILLISECONDS.toHours(millisUntilFinished) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toHours(millisUntilFinished) / 24),
                            TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) / 60),
                            TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
                }


                public void onFinish() {
                    text.setText("00:00:00:00");
                }
            }.start();
            viewHolder.buyNowButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                    alertDialog.setTitle("Shapp App");

                    // Setting Dialog Message
                    alertDialog.setMessage("Are you sure, you want to buy?");
                    alertDialog.setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    status = dataAccess.BuyProduct(parkingList.get(position).id);
                                    if (status == 1) {
                                        Toast.makeText(getActivity(), "Admin will contact you", Toast.LENGTH_SHORT).show();
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

            viewHolder.userProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent GroupDetailsClass = new Intent(getActivity(), ProfileDetailsActivity.class);
                    GroupDetailsClass.putExtra("profile_id", parkingList.get(position).uploaded_by_id);
                    GroupDetailsClass.putExtra("admin_name", parkingList.get(position).uploaded_by_name);
                    GroupDetailsClass.putExtra("admin_photo", parkingList.get(position).uploaded_by_image);
                    GroupDetailsClass.putExtra("profile_location", parkingList.get(position).uploaded_in);
                    startActivity(GroupDetailsClass);

                }
            });

            return rowView;
        }

        public double distance(String lat1, String lon1, TextView textView) {
            productLatitude = Double.parseDouble(lat1);
            productLongitude = Double.parseDouble(lon1);
            GPSTracker gpsTracker = new GPSTracker(context);
            if (gpsTracker.getIsGPSTrackingEnabled()){
                userLatitude = Double.parseDouble(String.valueOf(gpsTracker.latitude));
                userLongitude = Double.parseDouble(String.valueOf(gpsTracker.longitude));
            }else{
                gpsTracker.showSettingsAlert();
            }

            double theta = productLongitude - userLongitude;
            dist = Math.sin(deg2rad(productLatitude)) * Math.sin(deg2rad(userLatitude)) + Math.cos(deg2rad(productLatitude)) * Math.cos(deg2rad(userLatitude)) * Math.cos(deg2rad(theta));
            dist = Math.acos(dist);
            dist = rad2deg(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            BigDecimal bd = new BigDecimal(dist).setScale(2, RoundingMode.HALF_EVEN);
            dist = bd.doubleValue();
            textView.setText("(" + "" + dist + " km)");
            return (dist);
        }

        public double deg2rad(double deg) {
            return (deg * Math.PI / 180.0);
        }
        public double rad2deg(double rad) {
            return (rad * 180.0 / Math.PI);
        }

        public double CalculateDistance(String latitude, String longitude, TextView textView){
            productLatitude = Double.parseDouble(latitude);
            productLongitude = Double.parseDouble(longitude);
            GPSTracker gpsTracker = new GPSTracker(context);
            if (gpsTracker.getIsGPSTrackingEnabled()){
                userLatitude = Double.parseDouble(String.valueOf(gpsTracker.latitude));
                userLongitude = Double.parseDouble(String.valueOf(gpsTracker.longitude));
            }else{
                gpsTracker.showSettingsAlert();
            }

            Location startPoint=new Location("locationA");
            startPoint.setLatitude(userLatitude);
            startPoint.setLongitude(userLongitude);

            Location endPoint=new Location("locationB");
            endPoint.setLatitude(productLatitude);
            endPoint.setLongitude(productLongitude);
            distance=startPoint.distanceTo(endPoint);
            distance=distance/1000;
            textView.setText("(" + "" + distance + " km)");
            return distance;
        }
    }

    public void startScheduleTask() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        DiscoverFragment discoverFragment = new DiscoverFragment();
                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        transaction.replace(R.id.fragment_place_holder, discoverFragment, "discoverFragment");
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                },
                1000
        );
    }
}

