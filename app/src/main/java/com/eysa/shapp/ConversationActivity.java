package com.eysa.shapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class ConversationActivity extends Activity {

    ImageView close_image;
    EditText messege_text;
    TextView send;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_message_view);

        close_image = (ImageView) this.findViewById(R.id.close_image);
        messege_text = (EditText) this.findViewById(R.id.messege_text);
        send = (TextView) this.findViewById(R.id.send);

        close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
