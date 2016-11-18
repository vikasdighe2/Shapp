package com.eysa.shapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import org.json.JSONArray;
import org.json.JSONObject;


public class CommentsActivity extends Activity {

    DatabaseHandler db;
    User loggedInUser;
    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader imageLoader;

    ImageView comments_page_close_image, comment_send_image;
    String PRODUCT_ID;
    LinearLayout comments_container;
    EditText enter_comment_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_view);


        db = DatabaseHandler.getInstance(this);
        loggedInUser = db.getUser();
        imageLoader = new ImageLoader(getApplicationContext(), CommentsActivity.this);
        dataAccess = DataAccess.getInstance(getApplicationContext());

        inflaterData = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        comments_page_close_image = (ImageView) this.findViewById(R.id.comments_page_close_image);
        comment_send_image = (ImageView) this.findViewById(R.id.comment_send_image);
        enter_comment_text = (EditText) this.findViewById(R.id.enter_comment_text);
        comments_container = (LinearLayout) this.findViewById(R.id.comments_container);


        Bundle extras = getIntent().getExtras();
        if (extras != null)
        {
            PRODUCT_ID = extras.getString("product_id");
        }

        //if(!PRODUCT_ID.equals("")){
        Display_comments();
        //}


        comments_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });

        comment_send_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String comment_string = enter_comment_text.getText().toString();

                if(!comment_string.equals("")){
                    enter_comment_text.setText("");

                    try {
                        BasicHttpClient httpClientUserData = new BasicHttpClient(Constants.API_URL);;
                        httpClientUserData.addHeader("X-Api-Key", loggedInUser.api_key);
                        httpClientUserData.addHeader("X-Api-UserEmail", loggedInUser.email_address);

                        ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                                .add("product_id", PRODUCT_ID)
                                .add("comment_text", comment_string);
                        httpClientUserData.setConnectionTimeout(60000);
                        HttpResponse httpResponseUserData = httpClientUserData.post(Constants.ADD_COMMENT_TO_PRODUCT, httpClientUserDataParameters);


                        final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());
                        if(DataObject.getInt("status") == 1) {
                            new AlertDialog.Builder(CommentsActivity.this)
                                    .setTitle("Shapp")
                                    .setMessage("Your comment Added Successfully")
                                    .setIcon(R.drawable.app_icon)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            //((MainActivity) getActivity()).onCreateFragBackPressed();
                                            recreate();
                                        }
                                    }).show();

                        }
                        else {
                            new AlertDialog.Builder(CommentsActivity.this)
                                    .setTitle("Shapp")
                                    .setMessage("Failed to Process your request")
                                    .setIcon(R.drawable.app_icon)
                                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int whichButton) {
                                            //((MainActivity) getActivity()).onCreateFragBackPressed();
                                            recreate();
                                        }
                                    }).show();

                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });


    } /* end of onCreate() */


    public void Display_comments(){

        try {
            BasicHttpClient httpClientUserData = new BasicHttpClient(Constants.API_URL);;
            httpClientUserData.addHeader("X-Api-Key", loggedInUser.api_key);
            httpClientUserData.addHeader("X-Api-UserEmail", loggedInUser.email_address);

            ParameterMap httpClientUserDataParameters = httpClientUserData.newParams()
                    .add("product_id", PRODUCT_ID)
                    .add("page_number", "1");
            httpClientUserData.setConnectionTimeout(60000);
            HttpResponse httpResponseUserData = httpClientUserData.post(Constants.GET_PRODUCT_COMMENTS, httpClientUserDataParameters);


            final JSONObject DataObject = new JSONObject(httpResponseUserData.getBodyAsString());
            if(DataObject.getInt("status") == 1) {
                JSONArray commentsArr = (JSONArray) DataObject.get("comments");

                comments_container.removeAllViews();
                comments_container.requestLayout();

                for(int r=0; r<commentsArr.length();r++) {
                    LinearLayout comment_row = (LinearLayout) inflaterData.inflate(R.layout.comment_row, null);
                    final JSONObject commentObj = (JSONObject) commentsArr.getJSONObject(r);

                    TextView user_name_text = (TextView) comment_row.findViewById(R.id.user_name_text);
                    TextView comment_time_text = (TextView) comment_row.findViewById(R.id.comment_time_text);
                    TextView comment_text = (TextView) comment_row.findViewById(R.id.comment_text);

                    user_name_text.setText(commentObj.getString("user_name"));
                    comment_time_text.setText(commentObj.getString("created_at"));
                    comment_text.setText(commentObj.getString("comment_text"));

                    comments_container.addView(comment_row);
                    comments_container.requestLayout();
                }

            }
        }
        catch (Exception e){
            e.printStackTrace();
        }




    } /* end of Display_comments() */




}