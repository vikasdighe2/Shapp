package com.eysa.shapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class ChatActivity extends Activity {

    LinearLayout purchased, sold, chat_row_container, purchasedRow, soldRow;
    DataAccess dataAccess;
    ImageLoader_fragment imageLoader_fragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view);

        purchased = (LinearLayout) this.findViewById(R.id.purchased);
        sold = (LinearLayout) this.findViewById(R.id.sold);
        chat_row_container = (LinearLayout) this.findViewById(R.id.chat_row_container);
        dataAccess = DataAccess.getInstance(this.getApplicationContext());
        imageLoader_fragment = new ImageLoader_fragment(this.getApplicationContext());
        purchased.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchased.setBackground(getResources().getDrawable(R.drawable.background_fill_blue));
                sold.setBackground(getResources().getDrawable(R.drawable.background_blue));
                JSONArray purchasedChatData = dataAccess.getChatList("purchased");
                try {
                    DisplayPurchesedChat(purchasedChatData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        sold.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchased.setBackground(getResources().getDrawable(R.drawable.background_blue));
                sold.setBackground(getResources().getDrawable(R.drawable.background_fill_blue));
                JSONArray soldChatData = dataAccess.getChatList("sold");
                try {
                    DisplaySoldChat(soldChatData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void DisplayPurchesedChat(JSONArray purchasedChatData) throws JSONException {
        chat_row_container.removeAllViews();
        chat_row_container.requestLayout();
        if (purchasedChatData.length() > 0) {
            for (int r = 0; r < purchasedChatData.length(); r++) {
                purchasedRow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.chat_item_view, null);
                final JSONObject searchObject = (JSONObject) purchasedChatData.get(r);


                final CircleImageView follower_image = (CircleImageView) purchasedRow.findViewById(R.id.rating_pro_image);
                imageLoader_fragment.DisplayImage(searchObject.getString("uploaded_by_image"), follower_image);

                TextView product_name_text = (TextView) purchasedRow.findViewById(R.id.product_name_text);
                TextView user_name = (TextView) purchasedRow.findViewById(R.id.user_name);
                TextView location = (TextView) purchasedRow.findViewById(R.id.location);
                TextView status = (TextView) purchasedRow.findViewById(R.id.status);

                product_name_text.setText(searchObject.getString("product_name"));
                user_name.setText(searchObject.getString("uploaded_by_name"));
                location.setText(searchObject.getString("uploaded_in"));
                if (searchObject.getInt("is_online") == 1) {
                    status.setText("Online");
                    status.setTextColor(getResources().getColor(R.color.INTRO_GRADIENT_THREE));
                } else {
                    status.setText("Offline");
                }
                purchasedRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatActivity.this, ConversationActivity.class);
                        startActivity(intent);
                    }
                });
                purchasedRow.requestLayout();
                chat_row_container.addView(purchasedRow);
            } /* end of FOR loop */
        }



    }

    public void DisplaySoldChat(JSONArray soldChatData) throws JSONException {
        chat_row_container.removeAllViews();
        chat_row_container.requestLayout();
        if (soldChatData.length() > 0) {
            for (int r = 0; r < soldChatData.length(); r++) {
                purchasedRow = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.chat_item_view, null);
                final JSONObject searchObject = (JSONObject) soldChatData.get(r);


                final CircleImageView follower_image = (CircleImageView) purchasedRow.findViewById(R.id.rating_pro_image);
                imageLoader_fragment.DisplayImage(searchObject.getString("uploaded_by_image"), follower_image);

                TextView product_name_text = (TextView) purchasedRow.findViewById(R.id.product_name_text);
                TextView user_name = (TextView) purchasedRow.findViewById(R.id.user_name);
                TextView location = (TextView) purchasedRow.findViewById(R.id.location);
                TextView status = (TextView) purchasedRow.findViewById(R.id.status);

                product_name_text.setText(searchObject.getString("product_name"));
                user_name.setText(searchObject.getString("uploaded_by_name"));
                location.setText(searchObject.getString("uploaded_in"));
                if (searchObject.getInt("is_online") == 1) {
                    status.setText("Online");
                    status.setTextColor(getResources().getColor(R.color.INTRO_GRADIENT_THREE));
                } else {
                    status.setText("Offline");
                }
                purchasedRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ChatActivity.this, ConversationActivity.class);
                        startActivity(intent);
                    }
                });
                purchasedRow.requestLayout();
                chat_row_container.addView(purchasedRow);
            }
        }
    }
}
