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

import java.util.List;


public class GroupsFragment extends Fragment {
    Context context;
    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader_fragment imageLoader_fragment;
    int status = 0;

    View rootView;
    LinearLayout groups_product_row_container, groups_item_row;
    List<Groups> downloadedGroups;

    ImageView groups_add_image, mybids_page_close_image;
    TextView groups_page_header_text;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.groups_view, container, false);
        context=container.getContext();
        groups_product_row_container = (LinearLayout) rootView.findViewById(R.id.groups_product_row_container);
        //inflaterData = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        imageLoader_fragment = new ImageLoader_fragment(context);
        dataAccess = DataAccess.getInstance(context);
        downloadedGroups = dataAccess.get_downloadedGroupsData();

        groups_page_header_text = (TextView) rootView.findViewById(R.id.groups_page_header_text);
        groups_add_image = (ImageView) rootView.findViewById(R.id.groups_add_image);

        /*mybids_page_close_image = (ImageView) rootView.findViewById(R.id.productdetails_page_close_image);
        mybids_page_close_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(context, R.color.WHITE_COLOR), PorterDuff.Mode.SRC_ATOP));
        mybids_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/


        groups_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateGroupAcivity.class);
                startActivity(intent);
            }
        });

        if(downloadedGroups.size() > 0){
            DisplayGroups();
        }
        else {
            dataAccess.downloadGroupsData();
        }

        LocalBroadcastManager.getInstance(context).registerReceiver(eventReceiver, new IntentFilter("groups_downloaded"));

        return rootView;

    } /* end of onCreateView() */


    private BroadcastReceiver eventReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
                DisplayGroups();
        }
    };





    public void DisplayGroups(){

        downloadedGroups = dataAccess.get_downloadedGroupsData();
        groups_product_row_container.removeAllViews();
        groups_product_row_container.requestLayout();

        for(int r=0; r<downloadedGroups.size();r++) {
            groups_item_row = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.groups_item_row, null);
            final Groups groupObj = downloadedGroups.get(r);

            ImageView group_image = (ImageView) groups_item_row.findViewById(R.id.group_image);
            imageLoader_fragment.DisplayImage(groupObj.group_image, group_image);

            TextView group_name_text = (TextView) groups_item_row.findViewById(R.id.group_name_text);
            group_name_text.setText(groupObj.group_name);

            TextView group_time_text = (TextView) groups_item_row.findViewById(R.id.group_time_text);
            group_time_text.setText(groupObj.admin_name);

            TextView pro_count_text = (TextView) groups_item_row.findViewById(R.id.pro_count_text);
            pro_count_text.setText(""+groupObj.products_count);

            TextView mem_count_text = (TextView) groups_item_row.findViewById(R.id.mem_count_text);
            mem_count_text.setText(""+groupObj.members_count);

            TextView follow_count_count = (TextView) groups_item_row.findViewById(R.id.follow_count_count);
            follow_count_count.setText(""+groupObj.followers_count);

            ImageView button_delete_group = (ImageView) groups_item_row.findViewById(R.id.button_delete_group);
                button_delete_group.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    status = dataAccess.DeleteGroup(groupObj.id);
                       if(status == 1){
                           if(downloadedGroups.size() > 0){
                               DisplayGroups();
                           }
                           else {
                               dataAccess.downloadGroupsData();
                           }

                           LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(eventReceiver, new IntentFilter("groups_downloaded"));
                           Toast.makeText(getActivity().getApplicationContext(), "Group deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            groups_item_row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent GroupDetailsClass = new Intent(context,GroupDetailsActivity.class);
                    GroupDetailsClass.putExtra("group_id", groupObj.id);
                    GroupDetailsClass.putExtra("group_name", groupObj.group_name);
                    GroupDetailsClass.putExtra("admin_name", groupObj.admin_name);
                    GroupDetailsClass.putExtra("admin_photo", groupObj.admin_photo);
                    GroupDetailsClass.putExtra("group_image", groupObj.group_image);
                    GroupDetailsClass.putExtra("group_location", groupObj.admin_location);
                    GroupDetailsClass.putExtra("followers_count", groupObj.followers_count);
                    GroupDetailsClass.putExtra("products_count", groupObj.products_count);
                    GroupDetailsClass.putExtra("members_count", groupObj.members_count);
                    GroupDetailsClass.putExtra("is_following", groupObj.is_following);
                    startActivity(GroupDetailsClass);

                }
            });
            groups_item_row.requestLayout();
            groups_product_row_container.addView(groups_item_row);

        } /* end of FOR loop */

    } /* end of DisplayGroups() */





}
