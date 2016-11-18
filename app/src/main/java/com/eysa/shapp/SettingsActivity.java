package com.eysa.shapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsActivity extends Activity {

    static final int REQUEST_CAMERA = 1;
    static final int SELECT_FILE = 2;


    DatabaseHandler db;
    User loggedInUser;
    DataAccess dataAccess;
    LayoutInflater inflaterData;
    ImageLoader_fragment imageLoader_fragment;
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    Handler handler;
    InputMethodManager imm;



    View rootView;
    CircleImageView profile_picture;
    RelativeLayout propic_loading_block;
    LinearLayout country_list_container, state_list_container, city_list_container;
    ImageView propic_loading_spinner, profile_page_close_image;
    TextView user_name_text, user_delete_text;
    EditText user_fname_text, user_lname_text, user_email_text, user_phone_text, user_password_text, user_newpassword_text
            , user_confirm_text, user_country_text, user_state_text, user_city_text;
    Switch send_push_switch, auto_refresh_switch, send_email_switch;

    Animation animSpinner, dropview_in, dropview_out;

    JSONArray CountryJsonArray, StateJsonArray, CityJsonArray;
    String selected_country_id, selected_country_name, selected_state_id, selected_state_name, selected_city_id, selected_city_name;

    ImageView confirmpassword_field_next_image, mybids_page_close_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_view);

        fileCache = new FileCache(this);
        imageLoader_fragment = new ImageLoader_fragment(this);
        dataAccess = DataAccess.getInstance(this);
        inflaterData = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        db = DatabaseHandler.getInstance(this);
        loggedInUser = db.getUser();

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        profile_picture = (CircleImageView) this.findViewById(R.id.profile_picture);
        mybids_page_close_image = (ImageView) this.findViewById(R.id.productdetails_page_close_image);
        mybids_page_close_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR), PorterDuff.Mode.SRC_ATOP));
        mybids_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              finish();
            }
        });



        propic_loading_block = (RelativeLayout) this.findViewById(R.id.propic_loading_block);
        propic_loading_spinner = (ImageView) this.findViewById(R.id.propic_loading_spinner);
        user_name_text = (TextView) this.findViewById(R.id.user_name_text);
        user_delete_text = (TextView) this.findViewById(R.id.user_delete_text);

        user_fname_text = (EditText) this.findViewById(R.id.user_fname_text);
        user_lname_text = (EditText) this.findViewById(R.id.user_lname_text);
        user_email_text = (EditText) this.findViewById(R.id.user_email_text);
        user_phone_text = (EditText) this.findViewById(R.id.user_phone_text);
        user_password_text = (EditText) this.findViewById(R.id.user_password_text);
        user_newpassword_text = (EditText) this.findViewById(R.id.user_newpassword_text);
        user_confirm_text = (EditText) this.findViewById(R.id.user_confirm_text);
        user_country_text = (EditText) this.findViewById(R.id.user_country_text);
        user_state_text = (EditText) this.findViewById(R.id.user_state_text);
        user_city_text = (EditText) this.findViewById(R.id.user_city_text);

        send_push_switch = (Switch) this.findViewById(R.id.send_push_switch);
        auto_refresh_switch = (Switch) this.findViewById(R.id.auto_refresh_switch);
        send_email_switch = (Switch) this.findViewById(R.id.send_email_switch);

        country_list_container = (LinearLayout) this.findViewById(R.id.country_list_container);
        state_list_container = (LinearLayout) this.findViewById(R.id.state_list_container);
        city_list_container = (LinearLayout) this.findViewById(R.id.city_list_container);

        confirmpassword_field_next_image = (ImageView) this.findViewById(R.id.confirmpassword_field_next_image);

        refresh_user_data();

        animSpinner = AnimationUtils.loadAnimation(this, R.anim.spinner_animation);
        //password_field_spinner_image.startAnimation(animSpinner);
        //newpassword_field_spinner_image.startAnimation(animSpinner);
        //confirmpassword_field_spinner_image.startAnimation(animSpinner);
        propic_loading_spinner.startAnimation(animSpinner);

        dropview_in = AnimationUtils.loadAnimation(this, R.anim.dropview_in);
        dropview_out = AnimationUtils.loadAnimation(this.getApplicationContext(), R.anim.dropview_out);

        user_confirm_text.addTextChangedListener(confirmPasswordChangeListener);

        /**********************DELETE ACCOUNT EVENTS**************************************/
        if (!(loggedInUser.email_address.equals("demo@shapp.dk"))) {
            user_delete_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    android.app.AlertDialog alert_delete_ac = new android.app.AlertDialog.Builder(SettingsActivity.this)
                            .setTitle("Confirmation")
                            .setMessage("Are you sure you want to delete your account? You will loose all your records and this cannot be undone.")
                            .setIcon(R.drawable.app_icon)
                            .setPositiveButton("Delete Account", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //send_account_delete_request();

                                }
                            }).setNegativeButton("Cancel", null).show();
                }
            });


            /**********************PROFILE PICTURE UPLOAD EVENTS**************************************/
            profile_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectImage();
                }
            });


            /**********************FIRSTNAME EVENTS**************************************/
            user_fname_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {

                    } else {
                        String current_fname = user_fname_text.getText().toString();
                        dataAccess.update_user_data("first_name", current_fname);
                        User user = new User(loggedInUser.id, current_fname, loggedInUser.last_name, loggedInUser.email_address, current_fname + " " + loggedInUser.last_name, loggedInUser.push_notification, loggedInUser.auto_refresh, loggedInUser.email_notification, loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key, loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, loggedInUser.phone_number, loggedInUser.category_id, loggedInUser.category_name, loggedInUser.quality_id, loggedInUser.quality_name, loggedInUser.show_all, loggedInUser.radius, loggedInUser.price_range, loggedInUser.country_id, loggedInUser.country_name, loggedInUser.state_id, loggedInUser.state_name, loggedInUser.city_id, loggedInUser.city_name);
                        db.updateUser(user);
                    }
                }
            });

            user_phone_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {

                    } else {
                        String current_phone = user_phone_text.getText().toString();
                        dataAccess.update_user_data("phone_number", current_phone);
                        User user = new User(loggedInUser.id, loggedInUser.first_name, loggedInUser.last_name, loggedInUser.email_address, loggedInUser.full_name, loggedInUser.push_notification, loggedInUser.auto_refresh, loggedInUser.email_notification, loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key, loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, current_phone, loggedInUser.category_id, loggedInUser.category_name, loggedInUser.quality_id, loggedInUser.quality_name, loggedInUser.show_all, loggedInUser.radius, loggedInUser.price_range, loggedInUser.country_id, loggedInUser.country_name, loggedInUser.state_id, loggedInUser.state_name, loggedInUser.city_id, loggedInUser.city_name);
                        db.updateUser(user);
                    }
                }
            });


            /**********************LASTNAME EVENTS**************************************/
            user_lname_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {

                    } else {
                        String current_lname = user_lname_text.getText().toString();
                        dataAccess.update_user_data("last_name", current_lname);
                        User user = new User(loggedInUser.id, loggedInUser.first_name, current_lname, loggedInUser.email_address, loggedInUser.first_name + " " + current_lname, loggedInUser.push_notification, loggedInUser.auto_refresh, loggedInUser.email_notification, loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key, loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, loggedInUser.phone_number, loggedInUser.category_id, loggedInUser.category_name, loggedInUser.quality_id, loggedInUser.quality_name, loggedInUser.show_all, loggedInUser.radius, loggedInUser.price_range, loggedInUser.country_id, loggedInUser.country_name, loggedInUser.state_id, loggedInUser.state_name, loggedInUser.city_id, loggedInUser.city_name);
                        db.updateUser(user);

                    }
                }
            });


            /**********************EMAIL CHANGE EVENTS**************************************/
            user_email_text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {

                    } else {
                        String current_email = user_email_text.getText().toString();
                        dataAccess.update_user_data("email_address", current_email);
                        User user = new User(loggedInUser.id, loggedInUser.first_name, loggedInUser.last_name, current_email, loggedInUser.full_name, loggedInUser.push_notification, loggedInUser.auto_refresh, loggedInUser.email_notification, loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key, loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, loggedInUser.phone_number, loggedInUser.category_id, loggedInUser.category_name, loggedInUser.quality_id, loggedInUser.quality_name, loggedInUser.show_all, loggedInUser.radius, loggedInUser.price_range, loggedInUser.country_id, loggedInUser.country_name, loggedInUser.state_id, loggedInUser.state_name, loggedInUser.city_id, loggedInUser.city_name);
                        db.updateUser(user);
                    }
                }
            });


            /**********************COUNTRY FIELD EVENTS**************************************/
            user_country_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (country_list_container.getVisibility() == View.VISIBLE) {
                        country_list_container.startAnimation(dropview_out);
                        country_list_container.setVisibility(View.INVISIBLE);
                        country_list_container.setVisibility(View.GONE);

                    } else {
                        country_list_container.setVisibility(View.VISIBLE);
                    }

                }
            });


            user_country_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (!user_country_text.getText().toString().equals("")) {
                        //search_clear_btn.setVisibility(View.VISIBLE);
                    } else {
                        //search_clear_btn.setVisibility(View.GONE);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    filter_country_list();
                }
            });

            /**********************STATE FIELD EVENTS**************************************/
            user_state_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DownloadStateList(selected_country_id);
                    if (state_list_container.getVisibility() == View.VISIBLE) {
                        state_list_container.startAnimation(dropview_out);
                        state_list_container.setVisibility(View.INVISIBLE);
                        state_list_container.setVisibility(View.GONE);

                    } else {
                        state_list_container.setVisibility(View.VISIBLE);
                    }
                }
            });


            user_state_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (!user_state_text.getText().toString().equals("")) {
                        //search_clear_btn.setVisibility(View.VISIBLE);
                    } else {
                        //search_clear_btn.setVisibility(View.GONE);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    filter_state_list();
                }
            });


            /**********************CITY FIELD EVENTS**************************************/
            user_city_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DownloadCityList(selected_state_id);
                    if (city_list_container.getVisibility() == View.VISIBLE) {
                        city_list_container.startAnimation(dropview_out);
                        city_list_container.setVisibility(View.INVISIBLE);
                        city_list_container.setVisibility(View.GONE);

                    } else {
                        city_list_container.setVisibility(View.VISIBLE);
                    }

                }
            });


            user_city_text.addTextChangedListener(new TextWatcher() {
                @Override
                public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                    if (!user_city_text.getText().toString().equals("")) {
                        //search_clear_btn.setVisibility(View.VISIBLE);
                    } else {
                        //search_clear_btn.setVisibility(View.GONE);
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    filter_city_list();
                }
            });

            confirmpassword_field_next_image.setClickable(true);
            confirmpassword_field_next_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        String curr_pass = user_password_text.getText().toString();
                        String new_pass = user_newpassword_text.getText().toString();
                        String conf_pass = user_confirm_text.getText().toString();
                        if (curr_pass.equals("")) {
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle("Shapp")
                                    .setMessage("Enter Current Password")
                                    .setIcon(R.drawable.app_icon)
                                    .setNegativeButton("Ok", null).show();

                        } else if (new_pass.equals("")) {
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle("Shapp")
                                    .setMessage("Enter New Password")
                                    .setIcon(R.drawable.app_icon)
                                    .setNegativeButton("Ok", null).show();

                        } else if (conf_pass.equals("")) {
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle("Shapp")
                                    .setMessage("Enter Confirm Password")
                                    .setIcon(R.drawable.app_icon)
                                    .setNegativeButton("Ok", null).show();

                        } else if (!conf_pass.equals(new_pass)) {
                            new AlertDialog.Builder(SettingsActivity.this)
                                    .setTitle("Shapp")
                                    .setMessage("Enter Confirm Password")
                                    .setIcon(R.drawable.app_icon)
                                    .setNegativeButton("Ok", null).show();

                        } else {
                            update_user_password(curr_pass, new_pass);
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        dataAccess.downloadUserDataFromServer();
        LocalBroadcastManager.getInstance(this.getApplicationContext()).registerReceiver(userdataReceiver, new IntentFilter("userdata_downloaded"));

    } /* end of onCreate() */



    private BroadcastReceiver userdataReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh_user_data();
            DownloadCountryList();
            updateSQLLiteDataOfUser();
        }
    };




    public void refresh_user_data(){

        loggedInUser = db.getUser();

        if(loggedInUser!=null){

            user_name_text.setText(loggedInUser.full_name);

            user_fname_text.setText(loggedInUser.first_name);
            user_lname_text.setText(loggedInUser.last_name);
            user_email_text.setText(loggedInUser.email_address);
            user_phone_text.setText(loggedInUser.phone_number);

            user_country_text.setText(loggedInUser.country_name);
            selected_country_id = loggedInUser.country_id;
            user_state_text.setText(loggedInUser.state_name);
            selected_state_id = loggedInUser.state_id;
            user_city_text.setText(loggedInUser.city_name);
            selected_city_id = loggedInUser.city_id;

            if (loggedInUser.avatar.length() > 5) {
                memoryCache.remove(Constants.BASE_URL + "" + loggedInUser.avatar);
                fileCache.remove(Constants.BASE_URL + "" + loggedInUser.avatar);
                imageLoader_fragment.DisplayImage(loggedInUser.avatar, profile_picture);
            }


            /***********************PUSH SWITCH FLAG SET and EVENTS***************************/
            if (loggedInUser.push_notification == 1) {
                send_push_switch.setChecked(true);
            } else {
                send_push_switch.setChecked(false);
            }
            if (!(loggedInUser.email_address.equals("demo@shapp.dk"))) {
                send_push_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            dataAccess.update_user_data("push_switch", "1");
                            User user = new User(loggedInUser.id, loggedInUser.first_name, loggedInUser.last_name, loggedInUser.email_address, loggedInUser.full_name, 1, loggedInUser.auto_refresh, loggedInUser.email_notification, loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key, loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, loggedInUser.phone_number, loggedInUser.category_id, loggedInUser.category_name, loggedInUser.quality_id, loggedInUser.quality_name, loggedInUser.show_all, loggedInUser.radius, loggedInUser.price_range, loggedInUser.country_id, loggedInUser.country_name, loggedInUser.state_id, loggedInUser.state_name, loggedInUser.city_id, loggedInUser.city_name);
                            db.updateUser(user);
                        } else {
                            dataAccess.update_user_data("push_switch", "0");
                            User user = new User(loggedInUser.id, loggedInUser.first_name, loggedInUser.last_name, loggedInUser.email_address, loggedInUser.full_name, 0, loggedInUser.auto_refresh, loggedInUser.email_notification, loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key, loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, loggedInUser.phone_number, loggedInUser.category_id, loggedInUser.category_name, loggedInUser.quality_id, loggedInUser.quality_name, loggedInUser.show_all, loggedInUser.radius, loggedInUser.price_range, loggedInUser.country_id, loggedInUser.country_name, loggedInUser.state_id, loggedInUser.state_name, loggedInUser.city_id, loggedInUser.city_name);
                            db.updateUser(user);
                        }
                    }
                });
            }

            /***********************REFRESH SWITCH FLAG SET and EVENTS***************************/
            if (loggedInUser.auto_refresh == 1) {
                auto_refresh_switch.setChecked(true);
            } else {
                auto_refresh_switch.setChecked(false);
            }
            if (!(loggedInUser.email_address.equals("demo@shapp.dk"))) {
                auto_refresh_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            dataAccess.update_user_data("refresh_switch", "1");
                            User user = new User(loggedInUser.id, loggedInUser.first_name, loggedInUser.last_name, loggedInUser.email_address, loggedInUser.full_name, loggedInUser.push_notification, 1, loggedInUser.email_notification, loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key, loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, loggedInUser.phone_number, loggedInUser.category_id, loggedInUser.category_name, loggedInUser.quality_id, loggedInUser.quality_name, loggedInUser.show_all, loggedInUser.radius, loggedInUser.price_range, loggedInUser.country_id, loggedInUser.country_name, loggedInUser.state_id, loggedInUser.state_name, loggedInUser.city_id, loggedInUser.city_name);
                            db.updateUser(user);
                        } else {
                            dataAccess.update_user_data("refresh_switch", "0");
                            User user = new User(loggedInUser.id, loggedInUser.first_name, loggedInUser.last_name, loggedInUser.email_address, loggedInUser.full_name, loggedInUser.push_notification, 0, loggedInUser.email_notification, loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key, loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, loggedInUser.phone_number, loggedInUser.category_id, loggedInUser.category_name, loggedInUser.quality_id, loggedInUser.quality_name, loggedInUser.show_all, loggedInUser.radius, loggedInUser.price_range, loggedInUser.country_id, loggedInUser.country_name, loggedInUser.state_id, loggedInUser.state_name, loggedInUser.city_id, loggedInUser.city_name);
                            db.updateUser(user);
                        }
                    }
                });
            }

            /***********************PUSH SWITCH FLAG SET and EVENTS***************************/
            if (loggedInUser.email_notification == 1) {
                send_email_switch.setChecked(true);
            } else {
                send_email_switch.setChecked(false);
            }
            if (!(loggedInUser.email_address.equals("demo@shapp.dk"))) {
                send_email_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            dataAccess.update_user_data("email_switch", "1");
                            User user = new User(loggedInUser.id, loggedInUser.first_name, loggedInUser.last_name, loggedInUser.email_address, loggedInUser.full_name, 1, loggedInUser.auto_refresh, 1, loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key, loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, loggedInUser.phone_number, loggedInUser.category_id, loggedInUser.category_name, loggedInUser.quality_id, loggedInUser.quality_name, loggedInUser.show_all, loggedInUser.radius, loggedInUser.price_range, loggedInUser.country_id, loggedInUser.country_name, loggedInUser.state_id, loggedInUser.state_name, loggedInUser.city_id, loggedInUser.city_name);
                            db.updateUser(user);
                        } else {
                            dataAccess.update_user_data("email_switch", "0");
                            User user = new User(loggedInUser.id, loggedInUser.first_name, loggedInUser.last_name, loggedInUser.email_address, loggedInUser.full_name, 0, loggedInUser.auto_refresh, 1, loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key, loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, loggedInUser.phone_number, loggedInUser.category_id, loggedInUser.category_name, loggedInUser.quality_id, loggedInUser.quality_name, loggedInUser.show_all, loggedInUser.radius, loggedInUser.price_range, loggedInUser.country_id, loggedInUser.country_name, loggedInUser.state_id, loggedInUser.state_name, loggedInUser.city_id, loggedInUser.city_name);
                            db.updateUser(user);
                        }
                    }
                });
            }


            /*if(loggedInUser.country_id!=null && !loggedInUser.country_id.equals("")){
                DownloadStateList(loggedInUser.country_id);
            }

            if(loggedInUser.state_id!=null && !loggedInUser.state_id.equals("")) {
                DownloadCityList(loggedInUser.state_id);
            }*/



        }
        else{

        }

    } /* end of refresh_user_data() */


    private void selectImage() {
        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity().getApplicationContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });

        //AlertDialog dialog = builder.create();
        builder.show();
    }

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("resultCode:", "" + resultCode + " - " + requestCode);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                thumbnail.compress(Bitmap.CompressFormat.PNG, 90, bytes);
                File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".png");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                profile_picture.setImageBitmap(getRoundedCornerBitmap(thumbnail, 100));
                updateProfileImage(thumbnail);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this.getApplicationContext(), selectedImageUri, projection, null, null, null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);
                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 500;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                profile_picture.setImageBitmap(getRoundedCornerBitmap(bm, 250));
                updateProfileImage(bm);
            }
        }
    }

    public void updateProfileImage(Bitmap bm) {
        propic_loading_block.setVisibility(View.VISIBLE);
       // dataAccess.updateUserProfileImage(bm);
    }



    public void filter_country_list(){

        country_list_container.removeAllViews();
        country_list_container.requestLayout();

        String searchString = user_country_text.getText().toString();
        searchString = searchString.trim().toLowerCase();
        Log.d("searchString", "" + searchString);

        if(!loggedInUser.country_name.trim().toLowerCase().contentEquals(searchString)) {

            try {

                for (int i = 0; i < CountryJsonArray.length(); i++) {
                    try {
                        LinearLayout dropdown_country_row = (LinearLayout) inflaterData.inflate(R.layout.dropdown_custom_row, null);

                        final String country_id = CountryJsonArray.optJSONObject(i).getString("id");
                        final String country_name = CountryJsonArray.optJSONObject(i).getString("name");

                        final TextView dropdown_item_text = (TextView) dropdown_country_row.findViewById(R.id.dropdown_item_text);
                        dropdown_item_text.setText(country_name);


                        dropdown_country_row.setClickable(true);
                        dropdown_country_row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                selected_country_id = country_id;
                                selected_country_name = country_name;
                                user_country_text.setText(country_name);
                                country_list_container.setVisibility(View.GONE);
                                DownloadStateList(country_id);
                                dataAccess.update_user_data("country_name", country_name);
                                User user = new User(loggedInUser.id, loggedInUser.first_name,loggedInUser.last_name,loggedInUser.email_address, loggedInUser.full_name, loggedInUser.push_notification, loggedInUser.auto_refresh, loggedInUser.email_notification, loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key, loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, loggedInUser.phone_number, loggedInUser.category_id, loggedInUser.category_name, loggedInUser.quality_id, loggedInUser.quality_name, loggedInUser.show_all, loggedInUser.radius, loggedInUser.price_range, loggedInUser.country_id, country_name, loggedInUser.state_id, loggedInUser.state_name, loggedInUser.city_id, loggedInUser.city_name);
                                db.updateUser(user);

                            }
                        });

                        if (country_name.toLowerCase().contains(searchString)) {
                            country_list_container.addView(dropdown_country_row);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } /* END of FOR loop Location array */

            } catch (Exception exception) {
                exception.printStackTrace();
            }

            country_list_container.setVisibility(View.VISIBLE);

        }
    } /* end of filter_country_list() */

    public void DownloadCountryList() {

        Log.d("DownloadCountryList", "called");
        try {
            JSONObject obj = new JSONObject();
            BasicHttpClient httpClient = new BasicHttpClient(Constants.API_URL);

            ParameterMap parameters = httpClient.newParams();
            httpClient.setConnectionTimeout(60000);
            httpClient.setReadTimeout(60000);
            HttpResponse httpResponse = httpClient.post(Constants.SEARCH_COUNTRIES, parameters);
            Log.d("httpResponse: ", "" + httpResponse);
            obj = new JSONObject(httpResponse.getBodyAsString());
            CountryJsonArray = obj.getJSONArray("items");
            Log.d("Country List", "" + CountryJsonArray);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    } /* edn of DownloadCountryList() */



    public void DownloadStateList(final String country_id) {

        Log.d("DownloadStateList", "called");
        try {
            JSONObject obj = new JSONObject();
            BasicHttpClient httpClient = new BasicHttpClient(Constants.API_URL);

            ParameterMap parameters = httpClient.newParams().add("country_id", country_id);
            httpClient.setConnectionTimeout(60000);
            httpClient.setReadTimeout(60000);
            HttpResponse httpResponse = httpClient.post(Constants.SEARCH_STATES, parameters);
            Log.d("httpResponse: ", "" + httpResponse);
            obj = new JSONObject(httpResponse.getBodyAsString());
            StateJsonArray = obj.getJSONArray("items");
            Log.d("State List", "" + StateJsonArray);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    } /* edn of DownloadStateList() */


    public void filter_state_list(){

        state_list_container.removeAllViews();
        state_list_container.requestLayout();

        String searchString = user_state_text.getText().toString();
        searchString = searchString.trim().toLowerCase();
        Log.d("searchString", "" + searchString);


        if(!loggedInUser.state_name.trim().toLowerCase().contentEquals(searchString)) {
            try {

                for (int i = 0; i < StateJsonArray.length(); i++) {
                    try {
                        LinearLayout dropdown_state_row = (LinearLayout) inflaterData.inflate(R.layout.dropdown_custom_row, null);

                        final String state_id = StateJsonArray.optJSONObject(i).getString("id");
                        final String state_name = StateJsonArray.optJSONObject(i).getString("name");

                        final TextView dropdown_item_text = (TextView) dropdown_state_row.findViewById(R.id.dropdown_item_text);
                        dropdown_item_text.setText(state_name);


                        dropdown_state_row.setClickable(true);
                        dropdown_state_row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                selected_state_id = state_id;
                                selected_state_name = state_name;
                                user_state_text.setText(state_name);
                                state_list_container.setVisibility(View.GONE);
                                DownloadCityList(state_id);
                                dataAccess.update_user_data("state_name", state_name);
                                User user = new User(loggedInUser.id, loggedInUser.first_name,loggedInUser.last_name,loggedInUser.email_address, loggedInUser.full_name, loggedInUser.push_notification, loggedInUser.auto_refresh, loggedInUser.email_notification, loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key, loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, loggedInUser.phone_number, loggedInUser.category_id, loggedInUser.category_name, loggedInUser.quality_id, loggedInUser.quality_name, loggedInUser.show_all, loggedInUser.radius, loggedInUser.price_range, loggedInUser.country_id, loggedInUser.country_name, loggedInUser.state_id, state_name, loggedInUser.city_id, loggedInUser.city_name);
                                db.updateUser(user);

                            }
                        });

                        if (state_name.toLowerCase().contains(searchString)) {
                            state_list_container.addView(dropdown_state_row);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } /* END of FOR loop Location array */

            } catch (Exception exception) {
                exception.printStackTrace();
            }

            state_list_container.setVisibility(View.VISIBLE);
        }
    } /* end of filter_state_list() */



    public void DownloadCityList(final String state_id) {

        Log.d("DownloadCityList", "called");
        try {
            JSONObject obj = new JSONObject();
            BasicHttpClient httpClient = new BasicHttpClient(Constants.API_URL);

            ParameterMap parameters = httpClient.newParams().add("state_id", state_id);
            httpClient.setConnectionTimeout(60000);
            httpClient.setReadTimeout(60000);
            HttpResponse httpResponse = httpClient.post(Constants.SEARCH_CITIES, parameters);
            Log.d("httpResponse: ", "" + httpResponse);
            obj = new JSONObject(httpResponse.getBodyAsString());
            CityJsonArray = obj.getJSONArray("items");
            Log.d("City_List", "" + CityJsonArray);

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    } /* edn of DownloadCityList() */


    public void filter_city_list(){

        city_list_container.removeAllViews();
        city_list_container.requestLayout();

        String searchString = user_city_text.getText().toString();
        searchString = searchString.trim().toLowerCase();
        Log.d("searchString", "" + searchString);


        if(!loggedInUser.city_name.trim().toLowerCase().contentEquals(searchString)) {
            try {

                for (int i = 0; i < CityJsonArray.length(); i++) {
                    try {
                        LinearLayout dropdown_city_row = (LinearLayout) inflaterData.inflate(R.layout.dropdown_custom_row, null);

                        final String city_id = CityJsonArray.optJSONObject(i).getString("id");
                        final String city_name = CityJsonArray.optJSONObject(i).getString("name");

                        final TextView dropdown_item_text = (TextView) dropdown_city_row.findViewById(R.id.dropdown_item_text);
                        dropdown_item_text.setText(city_name);


                        dropdown_city_row.setClickable(true);
                        dropdown_city_row.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                                selected_city_id = city_id;
                                selected_city_name = city_name;
                                user_city_text.setText(city_name);
                                city_list_container.setVisibility(View.GONE);
                                dataAccess.update_user_data("city_name", city_name);
                                User user = new User(loggedInUser.id, loggedInUser.first_name,loggedInUser.last_name,loggedInUser.email_address, loggedInUser.full_name, loggedInUser.push_notification, loggedInUser.auto_refresh, loggedInUser.email_notification, loggedInUser.longitude, loggedInUser.latitude, loggedInUser.channel_key, loggedInUser.api_key, loggedInUser.is_premium, loggedInUser.fb_id, loggedInUser.avatar, loggedInUser.phone_number, loggedInUser.category_id, loggedInUser.category_name, loggedInUser.quality_id, loggedInUser.quality_name, loggedInUser.show_all, loggedInUser.radius, loggedInUser.price_range, loggedInUser.country_id, loggedInUser.country_name, loggedInUser.state_id, loggedInUser.state_name, loggedInUser.city_id, city_name);
                                db.updateUser(user);
                            }
                        });

                        if (city_name.toLowerCase().contains(searchString)) {
                            city_list_container.addView(dropdown_city_row);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } /* END of FOR loop Location array */

            } catch (Exception exception) {
                exception.printStackTrace();
            }

            city_list_container.setVisibility(View.VISIBLE);

        }
    } /* end of filter_city_list() */


    public void update_user_password(final String curr_pass, final String new_pass){

        try {
            JSONObject obj = new JSONObject();
            BasicHttpClient httpClient = new BasicHttpClient(Constants.API_URL);

            ParameterMap parameters = httpClient.newParams()
                    .add("current_password", curr_pass)
                    .add("new_password", new_pass)
                    .add("confirm_password", new_pass);
            httpClient.setConnectionTimeout(60000);
            httpClient.setReadTimeout(60000);
            HttpResponse httpResponse = httpClient.post(Constants.UPDATE_PROFILE, parameters);
            try {
                JSONObject receivedResponse = new JSONObject(httpResponse.getBodyAsString());
                if (receivedResponse.getInt("status") == 1) {
                    //downloadUserPrifile();
                    new AlertDialog.Builder(this)
                            .setTitle("Shapp")
                            .setMessage("Password updated Successfully")
                            .setIcon(R.drawable.app_icon)
                            .setNegativeButton("Ok", null).show();

                }
                else{
                    new AlertDialog.Builder(this)
                            .setTitle("Shapp")
                            .setMessage(receivedResponse.getString("message"))
                            .setIcon(R.drawable.app_icon)
                            .setNegativeButton("Ok", null).show();

                }
            } catch (Throwable t) {
                Log.d("e", t.toString());
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }

    } /* end of update_user_password() */




    final TextWatcher confirmPasswordChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if (user_confirm_text.getText().toString().equals("")) {
                confirmpassword_field_next_image.setVisibility(View.GONE);
            } else {
                confirmpassword_field_next_image.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /* Update the sql Lite data */

    public void updateSQLLiteDataOfUser(){
        try {
            User user = new User(loggedInUser.id,
                    user_fname_text.getText().toString(),
                    user_lname_text.getText().toString(),
                    user_email_text.getText().toString(),
                    loggedInUser.full_name,
                    loggedInUser.push_notification,
                    loggedInUser.auto_refresh,
                    loggedInUser.email_notification,
                    loggedInUser.longitude,
                    loggedInUser.latitude,
                    loggedInUser.channel_key,
                    loggedInUser.api_key,
                    loggedInUser.is_premium,
                    loggedInUser.fb_id,
                    loggedInUser.avatar,
                    loggedInUser.phone_number,
                    loggedInUser.category_id,
                    loggedInUser.category_name,
                    loggedInUser.quality_id,
                    loggedInUser.quality_name,
                    loggedInUser.show_all,
                    loggedInUser.radius,
                    loggedInUser.price_range,
                    loggedInUser.country_id,
                    loggedInUser.country_name,
                    loggedInUser.state_id,
                    loggedInUser.state_name,
                    loggedInUser.city_id,
                    loggedInUser.city_name
            );
            DatabaseHandler db = DatabaseHandler.getInstance(SettingsActivity.this);
            db.updateUser(user);
        } catch (Exception E){
            E.printStackTrace();
        }
    }




}
