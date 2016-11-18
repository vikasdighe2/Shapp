package com.eysa.shapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by Alex on 10/26/2016.
 */
public class ForgotPasswordActivity  extends Activity {

    ImageView register_back_image;
    DataAccess dataAccess;
    Button submit_button;
    EditText enter_email;
    int status = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_view);
        register_back_image = (ImageView) this.findViewById(R.id.register_back_image);
        submit_button = (Button) this.findViewById(R.id.submit_button);
        enter_email = (EditText) this.findViewById(R.id.enter_email);
        register_back_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
            }
        });
        dataAccess = DataAccess.getInstance(getApplicationContext());

        submit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status = dataAccess.forgetPassword(enter_email.getText().toString());
                if (status == 1){
                    new AlertDialog.Builder(ForgotPasswordActivity.this)
                            .setTitle("Shapp")
                            .setMessage("Please check your email")
                            .setIcon(R.drawable.app_icon)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                }
                            }).show();
                } else {
                    new AlertDialog.Builder(ForgotPasswordActivity.this)
                            .setTitle("Shapp")
                            .setMessage("Please enter the register email")
                            .setIcon(R.drawable.app_icon)
                            .setNegativeButton("Ok", null).show();
                }
            }
        });
    }
}
