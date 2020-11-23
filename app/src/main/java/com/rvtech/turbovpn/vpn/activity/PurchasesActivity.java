package com.rvtech.turbovpn.vpn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.northghost.hydraclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PurchasesActivity extends AppCompatActivity {

    @BindView(R.id.ads_pur)
    RelativeLayout adsPur;
    @BindView(R.id.vip_pur)
    RelativeLayout vipPur;

    @BindView(R.id.finish_activity)
    ImageView back_button;

    @BindView(R.id.activity_name)
    TextView activity_name;
    private AdView madview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_purchases);
        MobileAds.initialize(this, initializationStatus -> {
        });

        madview = findViewById(R.id.purchaseadView);
        AdRequest adRequest = new AdRequest.Builder().build();
        madview.loadAd(adRequest);
        ButterKnife.bind(this);
        activity_name.setText("Get Premium");
    }

    @OnClick({R.id.ads_pur, R.id.vip_pur , R.id.finish_activity})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ads_pur:
                Intent intent_ads = new Intent(this, AdsSubscriptionActivity.class);
                startActivity(intent_ads);
                break;
            case R.id.vip_pur:
                Intent intent_vip = new Intent(this, PaidServerSubscriptionActivity.class);
                startActivity(intent_vip);
                break;
            case R.id.finish_activity:
                finish();
                break;
        }
    }

}
