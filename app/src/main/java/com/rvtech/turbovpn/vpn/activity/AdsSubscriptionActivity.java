package com.rvtech.turbovpn.vpn.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.northghost.hydraclient.R;
import com.rvtech.turbovpn.vpn.billing.BillingClass;

import net.igenius.customcheckbox.CustomCheckBox;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AdsSubscriptionActivity extends AppCompatActivity implements CustomCheckBox.OnCheckedChangeListener, BillingClass.BillingErrorHandler {

    @BindView(R.id.one_month_checkBox)
    CustomCheckBox oneMonthCheckBox;
    @BindView(R.id.three_month_checkBox)
    CustomCheckBox threeMonthCheckBox;
    @BindView(R.id.six_month_checkBox)
    CustomCheckBox sixMonthCheckBox;
    @BindView(R.id.one_year_checkBox)
    CustomCheckBox oneYearCheckBox;

    //Billing class
    private BillingClass billingClass;
    //others
    private int checkedIndex = -1;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_subscription);
        ButterKnife.bind(this);

        billingClass = new BillingClass(this);
        billingClass.setmCallback(this);
        billingClass.startConnection();

        //checkbox listeners
        oneMonthCheckBox.setOnCheckedChangeListener(this);
        threeMonthCheckBox.setOnCheckedChangeListener(this);
        sixMonthCheckBox.setOnCheckedChangeListener(this);
        oneYearCheckBox.setOnCheckedChangeListener(this);
        //ads initialization mine
       MobileAds.initialize(this, initializationStatus -> {
         });

        mAdView = findViewById(R.id.adssubscription);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @OnClick(R.id.purchases_back_button)
    public void onViewClicked() {
        finish();
    }

    @Override
    public void onCheckedChanged(CustomCheckBox checkBox, boolean isChecked) {
        uncheckBox(checkedIndex);

        switch (checkBox.getId()) {
            case R.id.one_month_checkBox:
                //uncheckBox(checkedIndex);
                if (oneMonthCheckBox.isChecked()) {
                    billingClass.purchaseItemByPos(0);
                    checkedIndex = 0;
                }
                break;
            case R.id.three_month_checkBox:
                //uncheckBox(checkedIndex);
                if (threeMonthCheckBox.isChecked()) {
                    billingClass.purchaseItemByPos(1);
                    checkedIndex = 1;
                }
                break;
            case R.id.six_month_checkBox:
                //uncheckBox(checkedIndex);
                if (sixMonthCheckBox.isChecked()) {
                    billingClass.purchaseItemByPos(2);
                    checkedIndex = 2;
                }
                break;
            case R.id.one_year_checkBox:
                //uncheckBox(checkedIndex);
                if (oneYearCheckBox.isChecked()) {
                    billingClass.purchaseItemByPos(3);
                    checkedIndex = 3;
                }
                break;

            default:
                break;
        }
    }

    private void uncheckBox(int index) {
        if (index == -1) {
            return;
        } else if (!oneMonthCheckBox.isChecked() && !threeMonthCheckBox.isChecked() && !sixMonthCheckBox.isChecked() && !oneYearCheckBox.isChecked()) {
            return;
        } else if (index == 0) {
            if (oneMonthCheckBox.isChecked())
                oneMonthCheckBox.setChecked(false);
        } else if (index == 1) {
            if (threeMonthCheckBox.isChecked())
                threeMonthCheckBox.setChecked(false);
        } else if (index == 2) {
            if (sixMonthCheckBox.isChecked())
                sixMonthCheckBox.setChecked(false);
        } else if (index == 3) {
            if (oneYearCheckBox.isChecked())
                oneYearCheckBox.setChecked(false);
        }
    }

    @Override
    public void displayErrorMessage(String message) {

    }
}