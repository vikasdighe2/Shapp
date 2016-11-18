package com.eysa.shapp;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.regex.Pattern;


public class LoginFragment extends Fragment {

    OnLoginItemPressedListner loginListener;
    TextView emailField;
    TextView passwordField;
    TextView forgot_password_text;
    Button loginButton;
    ImageView register_back_image;
    CallbackManager callbackManager;
    LoginButton fb_login_button;
    Button fb;
    RelativeLayout propic_loading_block;
    public interface OnLoginItemPressedListner {
        void onLoginPressed();
        void onLoginFragBackPressed();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.login_view, container, false);
        //RelativeLayout inflaterView = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.login_view, null);

        emailField = (TextView) rootView.findViewById(R.id.enter_email);
        emailField.addTextChangedListener(emailChangeListener);
        passwordField = (TextView) rootView.findViewById(R.id.enter_password);
        passwordField.addTextChangedListener(emailChangeListener);
        forgot_password_text = (TextView) rootView.findViewById(R.id.forgot_password_text);
        register_back_image = (ImageView) rootView.findViewById(R.id.register_back_image);
        propic_loading_block = (RelativeLayout) rootView.findViewById(R.id.loading_block);


        register_back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //loginListener.onCreateAccountPressed();
                    ((MainActivity) getActivity()).onCreateFragBackPressed();
                } catch (Exception e) {
                    Log.d("LoginFragment", "exception: " + e);
                }
            }
        });

        //Button loginButton = (Button) inflaterView.findViewById(R.id.login_btn);
        loginButton = (Button) rootView.findViewById(R.id.login_button);
        loginButton.setEnabled(false);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showMessageWithToast("Processing your request. Please Wait.");
                LoginParameters parameters = new LoginParameters(emailField.getText().toString(), passwordField.getText().toString());
                CheckLogin loginCheck = new CheckLogin();
                String response = loginCheck.execute(parameters).toString();
                Log.d("Login Resp", response);
            }
        });

        forgot_password_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(), ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });


        /**********************FACEBOOK CALLBACK***************************/
        fb_login_button = (LoginButton) rootView.findViewById(R.id.fb_login_button);
        fb = (Button) rootView.findViewById(R.id.fb);
        fb_login_button.setBackgroundResource(R.drawable.fb_icon);
        fb.setBackgroundResource(R.drawable.fb_icon);
        fb_login_button.setText(null);
        fb_login_button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
        fb_login_button.setReadPermissions(Arrays.asList("public_profile", "email", "user_birthday", "user_friends"));

        // If using in a fragment
        //fb_login_button.setFragment();
        // Other app specific specialization

        // Callback registration
        callbackManager = CallbackManager.Factory.create();
        fb_login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("loginSucess", "hello"+loginResult);
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("cancel", "cancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("exception", ""+exception);
            }
        });

        /*************************************************/

        fb.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (v == fb) {
                    fb_login_button.performClick();
                }
            }
        });





        return rootView;
    }


    final TextWatcher emailChangeListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //if (isValidEmailId(emailField.getText().toString().trim()) && !passwordField.getText().toString().equals("")) {
            if (!passwordField.getText().toString().equals("")) {
                loginButton.setEnabled(true);
                loginButton.setBackgroundResource(R.drawable.reg_active_btn_filled);
            } else {
                loginButton.setEnabled(false);
                loginButton.setBackgroundResource(R.drawable.reg_inactive_btn_filled);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private boolean isValidEmailId(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }

    private static class LoginParameters {
        String emailAddress;
        String enteredPassword;

        LoginParameters(String emailAddress, String enteredpassword) {
            this.emailAddress = emailAddress;
            this.enteredPassword = enteredpassword;
        }
    }


    class CheckLogin extends AsyncTask<LoginParameters,String,String> {

        @Override
        protected String doInBackground(LoginParameters... params) {
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
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            int priceRange = 0;
                            try {
                                if (userDataObject.getString("price_range") != null) {
                                  priceRange  = userDataObject.getInt("price_range");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {

                                    DatabaseHandler db = DatabaseHandler.getInstance(getActivity().getApplicationContext());
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
                                            priceRange,
                                            userDataObject.getString("country_id"),
                                            userDataObject.getString("country_name"),
                                            userDataObject.getString("state_id"),
                                            userDataObject.getString("state_name"),
                                            userDataObject.getString("city_id"),
											userDataObject.getString("city_name"));

                                db.addUser(newUser);

                                    //loginListener.onLoginNextPress();
                                    ((MainActivity) getActivity()).onLoginPressed();

                            } catch(Exception e) {
                                Log.w("Error", e.getMessage());
                            }

                        }
                    });
                }
                else{
                    String message = obj.getString("message");
                    showMessageWithToast(message);
                }
            } catch (Throwable t) {
                t.printStackTrace();
                //showMessageWithToast("Your email and password combination do not match");
            }
        }

        public String PostCheckLogin(LoginParameters... params) {
            String receivedResponse = "";
            try {
                BasicHttpClient httpClient = new BasicHttpClient(Constants.API_URL);


                ParameterMap parameters = httpClient.newParams().add("email_address", params[0].emailAddress).add("password", params[0].enteredPassword);
                httpClient.setConnectionTimeout(60000);
                httpClient.setReadTimeout(60000);
                HttpResponse httpResponse = httpClient.post(Constants.CHECK_LOGIN, parameters);
                JSONObject obj = new JSONObject(httpResponse.getBodyAsString());
                receivedResponse = obj.toString();
            }
            catch(Exception exception) {

            }

            return receivedResponse;
        }
    }


    public void showMessageWithToast(String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

}
