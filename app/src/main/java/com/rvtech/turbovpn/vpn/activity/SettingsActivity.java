package com.rvtech.turbovpn.vpn.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.northghost.hydraclient.R;
import com.onesignal.OneSignal;
import com.pixplicity.easyprefs.library.Prefs;

public class SettingsActivity extends AppCompatActivity {

    ImageView backToActivity;
    TextView activity_name;
    SwitchMaterial switchMaterial;
    private AdView mAdView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //mine admob banner
        MobileAds.initialize(this, initializationStatus -> {
        });

        mAdView = findViewById(R.id.settingadView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        //mine admob banner end

        activity_name = (TextView) findViewById(R.id.activity_name);
        backToActivity = (ImageView) findViewById(R.id.finish_activity);
        switchMaterial = findViewById(R.id.notificationSwitch);

        checkNotiSettings();

        activity_name.setText("Settings");
        backToActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        switchMaterial.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Prefs.putBoolean("noti", true);
                    OneSignal.setSubscription(true);
                } else {
                    Prefs.putBoolean("noti", false);
                    OneSignal.setSubscription(false);
                }
            }
        });
    }

    private void checkNotiSettings() {

        if (Prefs.contains("noti") && Prefs.getBoolean("noti", true)) {
            switchMaterial.setChecked(true);
        } else if (Prefs.contains("noti") && !Prefs.getBoolean("noti", true)) {
            switchMaterial.setChecked(false);
        } else {
            switchMaterial.setChecked(true);
            OneSignal.setSubscription(true);
        }
    }
}
