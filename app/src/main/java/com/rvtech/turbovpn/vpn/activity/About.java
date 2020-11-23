package com.rvtech.turbovpn.vpn.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.northghost.hydraclient.R;

public class About extends AppCompatActivity {

    ImageView backToActivity;
    TextView activity_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        activity_name = (TextView) findViewById(R.id.activity_name);
        backToActivity = (ImageView) findViewById(R.id.finish_activity);

        activity_name.setText("About");
        backToActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
