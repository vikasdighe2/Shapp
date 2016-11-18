package com.eysa.shapp;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.turbomanage.httpclient.BasicHttpClient;
import com.turbomanage.httpclient.HttpResponse;
import com.turbomanage.httpclient.ParameterMap;

import org.json.JSONObject;

import java.util.regex.Pattern;


public class CreateAcFragment extends Fragment {

    EditText  fnameField, lnameField, emailField, passwordField, confirmPasswordField;
    Button registerButton;
    private static final int SELECT_PICTURE = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.register_view, container, false);
        //RelativeLayout inflaterView = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(R.layout.login_view, null);

        fnameField = (EditText) rootView.findViewById(R.id.enter_fname);
        fnameField.addTextChangedListener(emailChangeListener);

        lnameField = (EditText) rootView.findViewById(R.id.enter_lname);
        lnameField.addTextChangedListener(emailChangeListener);

        emailField = (EditText) rootView.findViewById(R.id.enter_email);
        emailField.addTextChangedListener(emailChangeListener);

        passwordField = (EditText) rootView.findViewById(R.id.enter_password);
        passwordField.addTextChangedListener(emailChangeListener);

        confirmPasswordField = (EditText) rootView.findViewById(R.id.confirm_password);

        //Button loginButton = (Button) inflaterView.findViewById(R.id.login_btn);
        registerButton = (Button) rootView.findViewById(R.id.register_button);
        registerButton.setEnabled(false);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!passwordField.getText().toString().equals(confirmPasswordField.getText().toString())) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Shapp")
                            .setMessage("Password and Confirm Password did not matched")
                            .setIcon(R.drawable.app_icon)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {

                                }
                            }).show();
                }
                else{

                    showMessageWithToast("Processing your request. Please Wait.");
                    RegisterParameters parameters = new RegisterParameters(fnameField.getText().toString(),lnameField.getText().toString(), emailField.getText().toString(), passwordField.getText().toString());
                    CreateAc create_ac = new CreateAc();
                    String response = create_ac.execute(parameters).toString();
                    Log.d("Register_Resp", response);

                }
            }
        });

        ImageView back_button = (ImageView) rootView.findViewById(R.id.register_back_image);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("LoginFragment", "back_button Clicked");
                try {
                    //loginListener.onCreateAccountPressed();
                    ((MainActivity) getActivity()).onCreateFragBackPressed();
                } catch (Exception e) {
                    Log.d("LoginFragment", "exception: " + e);
                }
            }
        });
        ImageView imageView = (ImageView) rootView.findViewById(R.id.new_pro_pic);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
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
            if (!passwordField.getText().toString().equals("") && !emailField.getText().toString().equals("")) {
                registerButton.setEnabled(true);
                registerButton.setBackgroundResource(R.drawable.reg_active_btn_filled);
            } else {
                registerButton.setEnabled(false);
                registerButton.setBackgroundResource(R.drawable.reg_inactive_btn_filled);
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

    private static class RegisterParameters {
        String fname;
        String lname;
        String emailAddress;
        String enteredPassword;

        RegisterParameters(String fname, String lname, String emailAddress, String enteredpassword) {
            this.fname = fname;
            this.lname = lname;
            this.emailAddress = emailAddress;
            this.enteredPassword = enteredpassword;
        }
    }


    class CreateAc extends AsyncTask<RegisterParameters,String,String> {

        @Override
        protected String doInBackground(RegisterParameters... params) {
            String res = PostCreateAc(params);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("post execute Res", result);
            try {
                JSONObject obj = new JSONObject(result);
                int status = obj.getInt("status");

                if (status == 1) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Shapp")
                            .setMessage("Account created Successfully. Login to Continue.")
                            .setIcon(R.drawable.app_icon)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((MainActivity) getActivity()).onCreateFragBackPressed();
                                }
                            }).show();

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

        public String PostCreateAc(RegisterParameters... params) {
            String receivedResponse = "";
            try {
                BasicHttpClient httpClient = new BasicHttpClient(Constants.API_URL);

                ParameterMap parameters = httpClient.newParams()
                        .add("first_name", params[0].fname)
                        .add("last_name", params[0].lname)
                        .add("email_address", params[0].emailAddress)
                        .add("password", params[0].enteredPassword)
                        .add("longitude", "")
                        .add("latitude", "")
                        .add("fb_id", "");

                httpClient.setConnectionTimeout(60000);
                httpClient.setReadTimeout(60000);
                HttpResponse httpResponse = httpClient.post(Constants.CREATE_ACCOUNT, parameters);
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

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
    }



}
