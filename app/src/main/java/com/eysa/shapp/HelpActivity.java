package com.eysa.shapp;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;


public class HelpActivity extends Activity {

    ImageView mybids_page_close_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.help_activity);

        mybids_page_close_image = (ImageView) findViewById(R.id.page_close_image);
        mybids_page_close_image.setColorFilter(new PorterDuffColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.WHITE_COLOR), PorterDuff.Mode.SRC_ATOP));
        mybids_page_close_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
