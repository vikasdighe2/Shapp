package com.eysa.shapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.util.List;


public class FollowersActivity extends Activity {


    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader_fragment imageLoader_fragment;
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    int status = 0;
    ImageView mybids_page_close_image;
    LinearLayout followers_row_container, followers_item_row;
    List<Followers> downloadedFollowers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.followers_view);

        followers_row_container = (LinearLayout) this.findViewById(R.id.followers_row_container);
        inflaterData = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mybids_page_close_image = (ImageView) this.findViewById(R.id.productdetails_page_close_image);
        mybids_page_close_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR), PorterDuff.Mode.SRC_ATOP));
        mybids_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fileCache = new FileCache(this);
        imageLoader_fragment = new ImageLoader_fragment(this);
        dataAccess = DataAccess.getInstance(this);
        downloadedFollowers = dataAccess.get_downloadedFollowersData();

        if(downloadedFollowers.size() > 0){
            DisplayFollowers();
        }
        else {
            dataAccess.downloadFollowersData();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(eventReceiver, new IntentFilter("followers_downloaded"));

    } /* end of onCreateView() */


    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(this != null) {
                DisplayFollowers();
            }
        }
    };





    public void DisplayFollowers(){

        downloadedFollowers = dataAccess.get_downloadedFollowersData();
        followers_row_container.removeAllViews();
        followers_row_container.requestLayout();

        for(int r=0; r<downloadedFollowers.size();r++) {
            followers_item_row = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.followers_item_row, null);
            final Followers followersObj = downloadedFollowers.get(r);


            final ImageView follower_image = (ImageView) followers_item_row.findViewById(R.id.follower_image);
            followers_item_row.post(new Runnable() {
                @Override
                public void run() {
                    Log.d("follower_image", ""+followersObj.avatar);
                    memoryCache.remove(Constants.BASE_URL + "" + followersObj.avatar);
                    fileCache.remove(Constants.BASE_URL + "" + followersObj.avatar);
                    //discover_item_image.setLayoutParams(new RelativeLayout.LayoutParams(products_item_row.getWidth(), (products_item_row.getWidth()-100)));
                    imageLoader_fragment.DisplayImage(followersObj.avatar, follower_image);
                }
            });

            TextView follower_name_text = (TextView) followers_item_row.findViewById(R.id.follower_name_text);
            follower_name_text.setText(followersObj.full_name);

            TextView follower_location_text = (TextView) followers_item_row.findViewById(R.id.follower_location_text);
            follower_location_text.setText(followersObj.location);

            TextView pro_count_text = (TextView) followers_item_row.findViewById(R.id.pro_count_text);
            pro_count_text.setText(""+followersObj.products_count);

            TextView following_count_text = (TextView) followers_item_row.findViewById(R.id.following_count_text);
            following_count_text.setText(""+followersObj.following_count);

            TextView follow_count_count = (TextView) followers_item_row.findViewById(R.id.follow_count_count);
            follow_count_count.setText(""+followersObj.followers_count);

            Button button_unfollow = (Button) followers_item_row.findViewById(R.id.button_unfollow);
            button_unfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    status = dataAccess.followUnfollowMethod(followersObj.id);
                    if (status == 1){
                        Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            followers_item_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent GroupDetailsClass = new Intent(FollowersActivity.this,ProfileDetailsActivity.class);
                        GroupDetailsClass.putExtra("profile_id", followersObj.id);
                        GroupDetailsClass.putExtra("admin_name", followersObj.full_name);
                        GroupDetailsClass.putExtra("profile_location", followersObj.location);
                        GroupDetailsClass.putExtra("admin_photo", followersObj.avatar);
                    startActivity(GroupDetailsClass);

                }
            });


            followers_item_row.requestLayout();
            followers_row_container.addView(followers_item_row);

        } /* end of FOR loop */


    } /* end of DisplayFollowers() */

}
