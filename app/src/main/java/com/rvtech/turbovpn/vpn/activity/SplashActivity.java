package com.rvtech.turbovpn.vpn.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.anchorfree.partner.api.auth.AuthMethod;
import com.anchorfree.partner.api.response.User;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.rvtech.turbovpn.vpn.Config;
import com.rvtech.turbovpn.vpn.MainApplication;
import com.rvtech.turbovpn.vpn.billing.BillingClass;
import com.google.android.material.snackbar.Snackbar;
import com.northghost.hydraclient.BuildConfig;
import com.northghost.hydraclient.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity implements BillingClass.BillingErrorHandler {

    @BindView(R.id.parent)
    RelativeLayout parent;

    private BillingClass billingClass;

    public SharedPreferences getPrefs() {
        return getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        Snackbar snackbar = Snackbar
                .make(parent, "Logging in, Please wait...", Snackbar.LENGTH_LONG);
        snackbar.show();

        loginUser();

        billingClass = new BillingClass(this);
        billingClass.setmCallback(this);
    }

    private void loginUser() {
        //logging in
        ((MainApplication) getApplication()).setNewHostAndCarrier(Config.baseURL, Config.carrierID);
        AuthMethod authMethod = AuthMethod.anonymous();
        UnifiedSDK.getInstance().getBackend().login(authMethod, new Callback<User>() {
            @Override
            public void success(User user) {

                billingClass.startConnection();
            }

            @Override
            public void failure(VpnException e) {
                Snackbar snackbar = Snackbar
                        .make(parent, "Authentication Error, Please try again.", Snackbar.LENGTH_INDEFINITE);

                snackbar.setAction("Try again", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        loginUser();
                    }
                });
                snackbar.show();
            }
        });
    }

    @Override
    public void displayErrorMessage(String message) {
        if (message.equals("done")) {

            if (
                    billingClass.isSubscribed(Config.remove_ads_one_month) ||
                            billingClass.isSubscribed(Config.remove_ads_three_month) ||
                            billingClass.isSubscribed(Config.remove_ads_six_month) ||
                            billingClass.isSubscribed(Config.remove_ads_one_year)
            ) {
                Config.ads_subscription = true;
            }
            if (
                    billingClass.isSubscribed(Config.unlock_servers_one_month) ||
                            billingClass.isSubscribed(Config.unlock_servers_three_month) ||
                            billingClass.isSubscribed(Config.unlock_servers_six_month) ||
                            billingClass.isSubscribed(Config.unlock_servers_one_year)
            ) {
                Config.servers_subscription = true;
            }

            //check for subscription
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();
        } else if (message.equals("error")) {
            Snackbar snackbar = Snackbar
                    .make(parent, "Authentication Error, Please try again.", Snackbar.LENGTH_INDEFINITE);

            snackbar.setAction("Try again", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loginUser();
                }
            });
            snackbar.show();
        }
    }
}
