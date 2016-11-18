package com.eysa.shapp;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class NotificationsFragment extends Fragment {


    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader_fragment imageLoader;
    Utils utils = new Utils();

    LinearLayout notifications_row_container, notification_item_row;
    List<Notifications> downloadedNotifications;

    ImageView notification_close_image;
    TextView notification_header_text;
    View rootView;
    Context context;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       /* super.onCreate(savedInstanceState);
        setContentView(R.layout.notifications_view);*/
        rootView = inflater.inflate(R.layout.notifications_view, container, false);
        context=container.getContext();


        notifications_row_container = (LinearLayout) rootView.findViewById(R.id.notifications_row_container);
        inflaterData = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader = new ImageLoader_fragment(getActivity().getApplicationContext());
        dataAccess =DataAccess.getInstance(getActivity().getApplicationContext());

        notification_close_image = (ImageView) rootView.findViewById(R.id.notification_close_image);
      /*  notification_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        notification_header_text = (TextView) rootView.findViewById(R.id.notification_header_text);
        notification_header_text.setText(R.string.notification);


        dataAccess.downloadNotifications(1);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(eventReceiver, new IntentFilter("notifications_downloaded"));


    return rootView;
    }


    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(NotificationsFragment.this != null) {
                DisplayNotifications();
            }
        }
    };

    public void DisplayNotifications(){
        downloadedNotifications = dataAccess.get_downloadedNotifications();
        notifications_row_container.removeAllViews();
        notifications_row_container.requestLayout();

        for(int r=0; r<downloadedNotifications.size();r++) {
            notification_item_row = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.notification_item_row, null);
            final Notifications notiObj = downloadedNotifications.get(r);

            TextView noti_title_text = (TextView) notification_item_row.findViewById(R.id.noti_title_text);
            if(Integer.parseInt(notiObj.notification_type) == Constants.PUSH_NOTIFICATION){
                noti_title_text.setText(R.string.push_notifications);
            }
            else if(Integer.parseInt(notiObj.notification_type) == Constants.EMAIL_NOTIFICATION){
                noti_title_text.setText(R.string.email_notifications);
            }


            long timeInMiliSeconds =0;
            TextView noti_time_text = (TextView) notification_item_row.findViewById(R.id.noti_time_text);
            try {
                 timeInMiliSeconds = utils.getTimeInMiliSecondsForChat(notiObj.created_at);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (TimeUnit.MILLISECONDS.toHours(timeInMiliSeconds) > 24){
                noti_time_text.setText(""+TimeUnit.MILLISECONDS.toDays(timeInMiliSeconds)+" days ago");
            } else if (TimeUnit.MILLISECONDS.toMinutes(timeInMiliSeconds) > 60){
                noti_time_text.setText(""+TimeUnit.MILLISECONDS.toHours(timeInMiliSeconds)+" hours ago");
            } else if(TimeUnit.MILLISECONDS.toSeconds(timeInMiliSeconds) > 60){
                noti_time_text.setText(""+TimeUnit.MILLISECONDS.toMinutes(timeInMiliSeconds)+" minitues ago");
            }

            TextView noti_content_text = (TextView) notification_item_row.findViewById(R.id.noti_content_text);
            noti_content_text.setText(notiObj.notification_string);


            notification_item_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                }
            });


            notification_item_row.requestLayout();
            notifications_row_container.addView(notification_item_row);

        } /* end of FOR loop */


    } /* end of DisplayNotifications() */







}
