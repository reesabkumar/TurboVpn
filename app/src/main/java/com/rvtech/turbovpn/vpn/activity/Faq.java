package com.rvtech.turbovpn.vpn.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.northghost.hydraclient.R;

public class Faq extends AppCompatActivity {

    ImageView backToActivity;
    TextView activity_name;
    private AdView faqAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        MobileAds.initialize(this, initializationStatus -> {
        });

        faqAdView = findViewById(R.id.faqads);
        AdRequest adRequest = new AdRequest.Builder().build();
        faqAdView.loadAd(adRequest);

        activity_name = (TextView) findViewById(R.id.activity_name);
        backToActivity = (ImageView) findViewById(R.id.finish_activity);

        activity_name.setText("FAQs");

        backToActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
