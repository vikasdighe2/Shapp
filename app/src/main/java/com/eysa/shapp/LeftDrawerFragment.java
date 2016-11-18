package com.eysa.shapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.LoginManager;


public class LeftDrawerFragment extends Fragment {

    View rootView;
    DatabaseHandler db;
    DataAccess dataAccess;
    User loggedInUser;
    ImageLoader_fragment imageLoader_fragment;



    LinearLayout drawer_page_layout, drawer_separator_layout, discover_button_row, mysales_button_row, mybids_button_row
            , followers_button_row, chat_button_row, help_button_row, logout_button_row, add_pro_button, groups_button_row;
    TextView discover_button_text, mysales_button_text, mybids_button_text, notifications_button_text, chat_button_text
            , help_button_text, logout_button_text, version_text, username_text, groups_button_text;
    ImageView discover_icon_image, mysales_icon_image, mybids_icon_image, notifications_icon_image, chat_icon_image
            , help_icon_image, logout_icon_image, snap_image, left_drawer_close_button, groups_icon_image;


    CircleImageView memberImage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.left_fragment_view, container, false);
        imageLoader_fragment = new ImageLoader_fragment(getActivity().getApplicationContext());
        db = DatabaseHandler.getInstance(getActivity().getApplicationContext());
        dataAccess = DataAccess.getInstance(getActivity().getApplicationContext());
        loggedInUser = db.getUser();

        /**********************Elements Collected**************************************/
        add_pro_button = (LinearLayout) rootView.findViewById(R.id.add_pro_button);

        drawer_page_layout = (LinearLayout) rootView.findViewById(R.id.drawer_page_layout);
        drawer_separator_layout = (LinearLayout) rootView.findViewById(R.id.drawer_separator_layout);
        version_text = (TextView) rootView.findViewById(R.id.version_text);

        left_drawer_close_button = (ImageView) rootView.findViewById(R.id.left_drawer_close_button);

        memberImage = (CircleImageView) rootView.findViewById(R.id.memberImage);
        username_text = (TextView) rootView.findViewById(R.id.username_text);

        discover_button_row = (LinearLayout) rootView.findViewById(R.id.discover_button_row);
        discover_button_text = (TextView) rootView.findViewById(R.id.discover_button_text);
        discover_icon_image = (ImageView) rootView.findViewById(R.id.discover_icon_image);

        mysales_button_row = (LinearLayout) rootView.findViewById(R.id.mysales_button_row);
        mysales_button_text = (TextView) rootView.findViewById(R.id.mysales_button_text);
        mysales_icon_image = (ImageView) rootView.findViewById(R.id.mysales_icon_image);

        mybids_button_row = (LinearLayout) rootView.findViewById(R.id.mybids_button_row);
        mybids_button_text = (TextView) rootView.findViewById(R.id.mybids_button_text);
        mybids_icon_image = (ImageView) rootView.findViewById(R.id.mybids_icon_image);


        followers_button_row = (LinearLayout) rootView.findViewById(R.id.followers_button_row);
        notifications_button_text = (TextView) rootView.findViewById(R.id.notifications_button_text);
        notifications_icon_image = (ImageView) rootView.findViewById(R.id.notifications_icon_image);

        groups_button_row = (LinearLayout) rootView.findViewById(R.id.groups_button_row);
        groups_button_text = (TextView) rootView.findViewById(R.id.groups_button_text);
        groups_icon_image = (ImageView) rootView.findViewById(R.id.groups_icon_image);

        chat_button_row = (LinearLayout) rootView.findViewById(R.id.chat_button_row);
        chat_button_text = (TextView) rootView.findViewById(R.id.chat_button_text);
        chat_icon_image = (ImageView) rootView.findViewById(R.id.chat_icon_image);

        help_button_row = (LinearLayout) rootView.findViewById(R.id.help_button_row);
        help_button_text = (TextView) rootView.findViewById(R.id.help_button_text);
        help_icon_image = (ImageView) rootView.findViewById(R.id.help_icon_image);

        logout_button_row = (LinearLayout) rootView.findViewById(R.id.logout_button_row);
        logout_button_text = (TextView) rootView.findViewById(R.id.logout_button_text);
        logout_icon_image = (ImageView) rootView.findViewById(R.id.logout_icon_image);

        imageLoader_fragment.DisplayImage(loggedInUser.avatar, memberImage);
        username_text = (TextView) rootView.findViewById(R.id.username_text);
        username_text.setText(loggedInUser.first_name);


        /**********************ICON COLOR CODE**************************************/
        discover_icon_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.left_drawer_icon_color), PorterDuff.Mode.SRC_ATOP));
        mysales_icon_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.left_drawer_icon_color), PorterDuff.Mode.SRC_ATOP));
        mybids_icon_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.left_drawer_icon_color), PorterDuff.Mode.SRC_ATOP));
        notifications_icon_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.left_drawer_icon_color), PorterDuff.Mode.SRC_ATOP));
        groups_icon_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.left_drawer_icon_color), PorterDuff.Mode.SRC_ATOP));
        chat_icon_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.left_drawer_icon_color), PorterDuff.Mode.SRC_ATOP));
        help_icon_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.left_drawer_icon_color), PorterDuff.Mode.SRC_ATOP));
        logout_icon_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.left_drawer_icon_color), PorterDuff.Mode.SRC_ATOP));
        //snap_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getActivity().getApplicationContext(), R.color.PINK_COLOR), PorterDuff.Mode.SRC_ATOP));





        /**********************SNAP BUTTON EVENT**************************************/
        add_pro_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addProductClass = new Intent(getActivity(), SellActivity.class);
                startActivity(addProductClass);

            }
        });

        /**********************CLOSE BUTTON EVENT**************************************/
        left_drawer_close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent discoverClass = new Intent(getActivity(), DiscoverActivity.class);
                //startActivity(discoverClass);
                ((MainActivity)getActivity()).close_left_drawer();
            }
        });


        /**********************DISCOVER BUTTON EVENT**************************************/
        discover_button_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent discoverClass = new Intent(getActivity(), DiscoverActivity.class);
                //startActivity(discoverClass);
                ((MainActivity)getActivity()).close_left_drawer();
            }
        });



        /**********************MY SALES BUTTON EVENT**************************************/
        mysales_button_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mysalesClass = new Intent(getActivity(), MySalesActivity.class);
                startActivity(mysalesClass);
            }
        });

        /**********************MY BIDS BUTTON EVENT**************************************/
        mybids_button_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mybidsClass = new Intent(getActivity(), MyBidsActivity.class);
                startActivity(mybidsClass);
            }
        });

        /**********************NOTIFICATIONS BUTTON EVENT**************************************/
        followers_button_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notificationsClass = new Intent(getActivity(), FollowersActivity.class);
                startActivity(notificationsClass);
            }
        });

        groups_button_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notificationsClass = new Intent(getActivity(), ChatActivity.class);
                startActivity(notificationsClass);
            }
        });


        /**********************CHAT BUTTON EVENT**************************************/
        chat_button_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent discoverClass = new Intent(getActivity(), SettingsActivity.class);
                startActivity(discoverClass);
            }
        });


        /**********************HELP BUTTON EVENT**************************************/
        help_button_row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent helpClass = new Intent(getActivity(), HelpActivity.class);
                startActivity(helpClass);
            }
        });


        /**********************LOGOUT EVENT**************************************/
        logout_button_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rightMenuListner.onLogoutPressed();
                new AlertDialog.Builder(getActivity())
                        .setTitle("Confirmation!")
                        .setMessage("Do you really want to logout?")
                        .setIcon(R.drawable.app_icon)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                ((MainActivity)getActivity()).onLogoutPressed();
                                LoginManager.getInstance().logOut();
                            }})
                        .setNegativeButton("No", null).show();
            }
        });
        return rootView;
    } /* end of onCreateView() */
}
