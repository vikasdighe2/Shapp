package com.eysa.shapp;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import org.json.JSONObject;


public class MainActivity extends Activity {


    LoginFragment loginFragment;
    DiscoverLogInFragment discoverLogInFragment;
    CreateAcFragment createAcFragment;
    DiscoverFragment discoverFragment;
    LeftDrawerFragment leftDrawerFragment;
    SellFragment sellFragment;
    SavedFragment savedFragment;
    GroupsFragment groupsFragment;
    FollowersActivity followersFragment;
    AddProductFragment addProductActivity;
    NotificationsFragment notificationsFragment;
    ProgressDialog dialog;
    DatabaseHandler db;
    DataAccess dataAccess;
    User loggedInUser;
    TableLayout tab_table;
    FrameLayout indexFrame, fragment_place_holder, create_login_account_frame, header_bar;
    ImageView index_logo_plain, header_active_brand_logo;
    RelativeLayout tabs_col_saved_shapps, tabs_col_followers, tabs_col_discover, tabs_col_groups, tabs_col_settings;
    TextView tabs_text_saved_shapps, tabs_text_followers, tabs_text_discover, tabs_text_groups, tabs_text_settings;
    ImageView tabs_image_saved_shapps, tabs_image_followers, tabs_image_discover, tabs_image_groups, tabs_image_settings;
    View mLeftDrawerView;
    DrawerLayout mDrawerLayout;


    CallbackManager callbackManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MainActivity.this==null){
            recreate();
        }
        setContentView(R.layout.activity_main);
        HideSystemUI();

        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.d("PERMISSION", "Permission is not granted");

                ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);

            }
        } /* end of if Mashalmallow*/

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mLeftDrawerView = this.findViewById(R.id.left_drawer);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        fragment_place_holder = (FrameLayout) this.findViewById(R.id.fragment_place_holder);
        tab_table = (TableLayout) this.findViewById(R.id.tabsTable);
        tabs_col_saved_shapps = (RelativeLayout) this.findViewById(R.id.tabs_col_saved_shapps);
        tabs_col_followers = (RelativeLayout) this.findViewById(R.id.tabs_col_followers);
        tabs_col_discover = (RelativeLayout) this.findViewById(R.id.tabs_col_discover);
        tabs_col_groups = (RelativeLayout) this.findViewById(R.id.tabs_col_groups);
        tabs_col_settings = (RelativeLayout) this.findViewById(R.id.tabs_col_settings);

        tabs_text_saved_shapps = (TextView) this.findViewById(R.id.tabs_text_saved_shapps);
        tabs_text_followers = (TextView) this.findViewById(R.id.tabs_text_followers);
        tabs_text_discover = (TextView) this.findViewById(R.id.tabs_text_discover);
        tabs_text_groups = (TextView) this.findViewById(R.id.tabs_text_groups);
        tabs_text_settings = (TextView) this.findViewById(R.id.tabs_text_settings);

        tabs_image_saved_shapps = (ImageView) this.findViewById(R.id.tabs_image_saved_shapps);
        tabs_image_followers = (ImageView) this.findViewById(R.id.tabs_image_followers);
        tabs_image_discover = (ImageView) this.findViewById(R.id.tabs_image_discover);
        tabs_image_groups = (ImageView) this.findViewById(R.id.tabs_image_groups);
        tabs_image_settings = (ImageView) this.findViewById(R.id.tabs_image_settings);
        //header_bar = (FrameLayout) findViewById(R.id.header_bar);
        //header_active_brand_logo = (ImageView) findViewById(R.id.header_active_brand_logo);
        //index_logo_plain = (ImageView) findViewById(R.id.index_logo_plain);



        dialog = new ProgressDialog(MainActivity.this);


        tabs_col_saved_shapps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    load_fragment_saved_shapps();
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        tabs_col_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_fragment_followers();
            }
        });

        tabs_col_discover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_fragment_discover();
            }
        });

        tabs_col_groups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_fragment_groups();

            }
        });

        tabs_col_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_fragment_settings();
            }
        });


        /*******************************************************/
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        AppEventsLogger.activateApp(this);

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("loginResult_M", ""+loginResult);// this = YourActivity
                        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        dialog.setMessage("Loading. Please wait...");
                        dialog.setIndeterminate(true);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.show();
                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {
                                        Log.v("LoginActivity", response.toString());

                                        // Application code
                                        try {
                                            String id = object.getString("id");
                                            String fname = object.getString("first_name");
                                            String lname = object.getString("last_name");
                                            String email = object.getString("email");
                                          //  String birthday = object.getString("birthday"); // 01/31/1980 format

                                            FB_LoginParameters parameters = new FB_LoginParameters(id, fname, lname, email);
                                            FB_CheckLogin loginCheck = new FB_CheckLogin();
                                            String FB_response = loginCheck.execute(parameters).toString();

                                        }
                                        catch (Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,first_name,last_name,email,gender,birthday");
                        request.setParameters(parameters);
                        request.executeAsync();

                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("loginResult_M", "Cancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d("loginResult_M", ""+exception);
                    }
                });
        /*******************************************************/




        //String loggedInUser = null;

        dataAccess = DataAccess.getInstance(MainActivity.this);
        db = DatabaseHandler.getInstance(this);

        loggedInUser = db.getUser();
        if(loggedInUser == null){
            Log.d("onCreate", "loggedInUser is NULL");
            tab_table.setVisibility(View.GONE);
            load_fragment_login();
        }
        else{

            try {
                dataAccess.downloadMyBids();
                dataAccess.downloadSavedShapps();
                dataAccess.downloadMySales();
                dataAccess.downloadDiscoverData(1);
                dataAccess.downloadGroupsData();
                load_fragment_leftdrawer();
                load_fragment_discover();
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

        /**********full Screen code*******************/
        final Handler handler = new Handler();
        Runnable runnableHideUI = new Runnable() {
            @Override
            public void run() {
                //Log.d("hideSystemUI", "hideSystemUI");
                HideSystemUI();
                handler.postDelayed(this, 2000);
            }
        };
        handler.postDelayed(runnableHideUI, 2000);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.d("PERMISSION","Permission: "+permissions[0]+ "was "+grantResults[0]);
            recreate();
            //resume tasks needing this permission
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /**********VIEW ELEMENT CLICK EVENT METHODS****************/


    /*********************************************/

    public void load_fragment_leftdrawer(){
        dataAccess.downloadMyBids();
        dataAccess.downloadSavedShapps();
        dataAccess.downloadMySales();
        dataAccess.downloadDiscoverData(1);
        dataAccess.downloadGroupsData();
        dataAccess.downloadFollowersData();
        leftDrawerFragment = new LeftDrawerFragment();
        FragmentTransaction transaction2 = getFragmentManager().beginTransaction();
        transaction2.replace(R.id.left_drawer, leftDrawerFragment, "leftDrawerFragment");
        transaction2.commit();
    }

    /*********************************************/
    public void load_fragment_login(){
        fragment_place_holder.setVisibility(View.VISIBLE);
        Fragment loginFrag = (Fragment) getFragmentManager().findFragmentByTag("loginFragment");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        discoverLogInFragment = new DiscoverLogInFragment();

        if (loginFrag != null && loginFrag.isVisible()) {
            getFragmentManager().beginTransaction().remove(loginFrag).commit();
        }
        FragmentTransaction transaction_l = getFragmentManager().beginTransaction();
        transaction_l.add(R.id.fragment_place_holder, discoverLogInFragment, "loginFragment");
        transaction_l.addToBackStack(null);
        transaction_l.commit();

    } /* end of load_fragmentt_login() */

    /*********************************************/
    public void onLoginFragBackPressed()
    {
        fragment_place_holder.setVisibility(View.GONE);
    } /* end of onLoginFragBackPressed() */

    /*********************************************/
    public void onLoginPressed()
    {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    } /* end of onLoginPressed() */

    /*********************************************/

    public void index_create_clicked(View view){
        load_fragment_createac();
    }/* end of index_create_clicked() */

    /*********************************************/
    public void load_fragment_createac(){

        fragment_place_holder.setVisibility(View.VISIBLE);
        Fragment createacFrag = (Fragment) getFragmentManager().findFragmentByTag("createAcFragment");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        createAcFragment = new CreateAcFragment();

        if (createacFrag != null && createacFrag.isVisible()) {
            getFragmentManager().beginTransaction().remove(createacFrag).commit();
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_place_holder, createAcFragment, "createAcFragment");
        transaction.addToBackStack(null);
        transaction.commit();


    } /* end of load_fragment_createac() */

    /*********************************************/
    public void onCreateFragBackPressed()
    {
        Fragment createacFrag = (Fragment) getFragmentManager().findFragmentByTag("createAcFragment");
        createAcFragment = new CreateAcFragment();
        if (createacFrag != null && createacFrag.isVisible()) {
            getFragmentManager().beginTransaction().remove(createacFrag).commit();
        }

        load_fragment_login();
    } /* end of onCreateFragBackPressed() */

    /*********************************************/

    public void load_fragment_saved_shapps(){

        setActiveTab("savedFragment");

        fragment_place_holder.setVisibility(View.VISIBLE);
        Fragment savedFrag = (Fragment) getFragmentManager().findFragmentByTag("savedFragment");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        savedFragment = new SavedFragment();

        if (savedFrag != null && savedFrag.isVisible()) {
            getFragmentManager().beginTransaction().remove(savedFrag).commit();
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_place_holder, savedFragment, "savedFragment");
        transaction.addToBackStack(null);
        transaction.commit();

    } /* end of load_fragment_userindex() */
    /*********************************************/

    public void load_fragment_followers(){

        setActiveTab("followersFragment");

        fragment_place_holder.setVisibility(View.VISIBLE);
        Fragment followersFrag = (Fragment) getFragmentManager().findFragmentByTag("sellFragment");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sellFragment = new SellFragment();

        if (followersFrag != null && followersFrag.isVisible()) {
            getFragmentManager().beginTransaction().remove(followersFrag).commit();
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_place_holder, sellFragment, "sellFragment");
        transaction.addToBackStack(null);
        transaction.commit();

    } /* end of load_fragment_userindex() */
    /*********************************************/

    public void load_fragment_discover(){

        setActiveTab("discoverFragment");

        fragment_place_holder.setVisibility(View.VISIBLE);
        Fragment discoverFrag = (Fragment) getFragmentManager().findFragmentByTag("discoverFragment");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        discoverFragment = new DiscoverFragment();

        if (discoverFrag != null && discoverFrag.isVisible()) {
            getFragmentManager().beginTransaction().remove(discoverFrag).commit();
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_place_holder, discoverFragment, "discoverFragment");
        transaction.addToBackStack(null);
        transaction.commit();

    } /* end of load_fragment_userindex() */
    /*********************************************/
    public void load_fragment_groups(){

        setActiveTab("groupsFragment");

        fragment_place_holder.setVisibility(View.VISIBLE);
        Fragment groupsFrag = (Fragment) getFragmentManager().findFragmentByTag("notificationFragment");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        notificationsFragment = new NotificationsFragment();

        if (groupsFrag != null && groupsFrag.isVisible()) {
            getFragmentManager().beginTransaction().remove(groupsFrag).commit();
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_place_holder, notificationsFragment, "notificationFragment");
        transaction.addToBackStack(null);
        transaction.commit();

    } /* end of load_fragment_userindex() */
    /*********************************************/

    public void load_fragment_settings(){

        setActiveTab("settingsFragment");

        fragment_place_holder.setVisibility(View.VISIBLE);
        Fragment settingsFrag = (Fragment) getFragmentManager().findFragmentByTag("chatFragment");
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        groupsFragment = new GroupsFragment();

        if (settingsFrag != null && settingsFrag.isVisible()) {
            getFragmentManager().beginTransaction().remove(settingsFrag).commit();
        }

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_place_holder, groupsFragment, "chatFragment");
        transaction.addToBackStack(null);
        transaction.commit();

    } /* end of load_fragment_userindex() */
    /*********************************************/



    public void setActiveTab(String activeTabName){


        tabs_col_saved_shapps.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_COLOR));
        tabs_image_saved_shapps.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_UNSELECTED_ICON_TXET_COLOR), PorterDuff.Mode.SRC_ATOP));
        tabs_text_saved_shapps.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_UNSELECTED_ICON_TXET_COLOR));

        tabs_col_followers.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_COLOR));
        tabs_image_followers.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_UNSELECTED_ICON_TXET_COLOR), PorterDuff.Mode.SRC_ATOP));
        tabs_text_followers.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_UNSELECTED_ICON_TXET_COLOR));

        tabs_col_discover.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_COLOR));
        tabs_image_discover.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_UNSELECTED_ICON_TXET_COLOR), PorterDuff.Mode.SRC_ATOP));
        tabs_text_discover.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_UNSELECTED_ICON_TXET_COLOR));

        tabs_col_groups.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_COLOR));
        tabs_image_groups.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_UNSELECTED_ICON_TXET_COLOR), PorterDuff.Mode.SRC_ATOP));
        tabs_text_groups.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_UNSELECTED_ICON_TXET_COLOR));

        tabs_col_settings.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_COLOR));
        tabs_image_settings.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_UNSELECTED_ICON_TXET_COLOR), PorterDuff.Mode.SRC_ATOP));
        tabs_text_settings.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.TAB_BAR_UNSELECTED_ICON_TXET_COLOR));



        if(activeTabName == "savedFragment"){
            tabs_col_saved_shapps.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.PINK_COLOR));
            tabs_image_saved_shapps.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR), PorterDuff.Mode.SRC_ATOP));
            tabs_text_saved_shapps.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR));


        }else if(activeTabName == "followersFragment"){
            tabs_col_followers.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.PINK_COLOR));
            tabs_image_followers.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR), PorterDuff.Mode.SRC_ATOP));
            tabs_text_followers.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR));


        }else if(activeTabName == "discoverFragment"){
            tabs_col_discover.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.PINK_COLOR));
            tabs_image_discover.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR), PorterDuff.Mode.SRC_ATOP));
            tabs_text_discover.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR));

        }else if(activeTabName == "groupsFragment"){
            tabs_col_groups.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.PINK_COLOR));
            tabs_image_groups.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR), PorterDuff.Mode.SRC_ATOP));
            tabs_text_groups.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR));


        }else if(activeTabName.equals("settingsFragment")){
            tabs_col_settings.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.PINK_COLOR));
            //tab1_img.setImageResource(R.mipmap.ic_launcher);
            tabs_image_settings.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR), PorterDuff.Mode.SRC_ATOP));
            tabs_text_settings.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR));

        }

    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /*********************************************/

    public void onLogoutPressed()
    {
        User loggedinUser = db.getUser();
        if (loggedinUser != null) {
            db.deleteUser(loggedinUser);
        }

        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        Intent intent = getIntent();
        finish();
        startActivity(intent);
    } /* end of onLoginFragBackPressed() */

    public void open_left_drawer(){
        mDrawerLayout.openDrawer(mLeftDrawerView);

    } /* end of close_left_drawer() */

    public void close_left_drawer(){
        mDrawerLayout.closeDrawer(mLeftDrawerView);

    } /* end of close_left_drawer() */

    /**************FACEBOOK LOGIN PART***********************/

    private static class FB_LoginParameters {

        String fbId;
        String firstName;
        String lastName;
        String emailAddress;

        FB_LoginParameters(String fbId, String firstName, String lastName, String emailAddress) {
            this.fbId = fbId;
            this.firstName = firstName;
            this.lastName = lastName;
            this.emailAddress = emailAddress;
        }
    }


    class FB_CheckLogin extends AsyncTask<FB_LoginParameters,String,String> {

        @Override
        protected String doInBackground(FB_LoginParameters... params) {
            String res = PostCheckLogin(params);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("post execute Res", result);
            try {
                JSONObject obj = new JSONObject(result);
                int status = obj.getInt("status");

                if (status == 1) {

                    final JSONObject userDataObject = obj.getJSONObject("user_data");
                    runOnUiThread(new Runnable() {
                        public void run() {


                            try {

                                DatabaseHandler db = DatabaseHandler.getInstance(getApplicationContext());
                                User newUser = new User(
                                        userDataObject.getString("id"),
                                        userDataObject.getString("first_name"),
                                        userDataObject.getString("last_name"),
                                        userDataObject.getString("email_address"),
                                        userDataObject.getString("full_name"),
                                        userDataObject.getInt("push_notification"),
                                        userDataObject.getInt("auto_refresh"),
                                        userDataObject.getInt("email_notification"),
                                        userDataObject.getInt("longitude"),
                                        userDataObject.getInt("latitude"),
                                        userDataObject.getString("channel_key"),
                                        userDataObject.getString("api_key"),
                                        userDataObject.getInt("is_premium"),
                                        userDataObject.getString("fb_id"),
                                        userDataObject.getString("avatar"),
                                        userDataObject.getString("phone_number"),
                                        userDataObject.getString("category_id"),
                                        userDataObject.getString("category_name"),
                                        userDataObject.getString("quality_id"),
                                        userDataObject.getString("quality_name"),
                                        userDataObject.getInt("show_all"),
                                        userDataObject.getInt("radius"),
                                        userDataObject.getInt("price_range"),
                                        userDataObject.getString("country_id"),
                                        userDataObject.getString("country_name"),
                                        userDataObject.getString("state_id"),
                                        userDataObject.getString("state_name"),
                                        userDataObject.getString("city_id"),
                                        userDataObject.getString("city_name"));

                                db.addUser(newUser);

                                //loginListener.onLoginNextPress();
                                //((MainActivity) getActivity()).onLoginPressed();
                                recreate();

                            } catch(Exception e) {
                                Log.d("Error", e.getMessage());
                            }

                        }
                    });
                }
                else{
                    String message = obj.getString("message");
                    //showMessageWithToast(message);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                //showMessageWithToast("Your email and password combination do not match");
            }
        }

        public String PostCheckLogin(FB_LoginParameters... params) {
            String receivedResponse = "";
            try {
                BasicHttpClient httpClient = new BasicHttpClient(Constants.API_URL);


                ParameterMap parameters = httpClient.newParams().add("fb_id", params[0].fbId)
                        .add("email_address", params[0].emailAddress)
                        .add("first_name", params[0].firstName)
                        .add("last_name", params[0].lastName);
                httpClient.setConnectionTimeout(60000);
                httpClient.setReadTimeout(60000);
                HttpResponse httpResponse = httpClient.post(Constants.FB_LOGIN, parameters);
                JSONObject obj = new JSONObject(httpResponse.getBodyAsString());
                receivedResponse = obj.toString();
                dialog.dismiss();

            }
            catch(Exception exception) {

            }

            return receivedResponse;
        }
    }


    /*************************************/



    public void HideSystemUI(){

        final View decorView = getWindow().getDecorView();

        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility(flags);

    }




    @Override
    protected void onResume(){
        super.onResume();
        HideSystemUI();

    }

       @Override
       protected void onRestart(){
        super.onRestart();
        HideSystemUI();

        }
}
