package com.eysa.shapp;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import org.json.JSONException;
import org.json.JSONObject;

public class DiscoverLogInFragment extends Fragment {
    Button sin_up, log_in;
    TextView demoLogin;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.discover_log_in, container, false);

        sin_up = (Button) rootView.findViewById(R.id.sin_up);
        log_in = (Button) rootView.findViewById(R.id.log_in);
        demoLogin = (TextView) rootView.findViewById(R.id.demoUserLogIn);

        sin_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_place_holder, new CreateAcFragment());
                fragmentTransaction.commit();
            }
        });

        log_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_place_holder, new LoginFragment());
                fragmentTransaction.commit();
            }
        });

        demoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginParameters parameters = new LoginParameters("demo@shapp.dk", "demonstration");
                CheckLogin loginCheck = new CheckLogin();
                String response = loginCheck.execute(parameters).toString();
            }
        });
        return rootView;
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
