package com.rvtech.turbovpn.vpn.activity;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.anchorfree.partner.api.response.RemainingTraffic;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.vpnsdk.callbacks.Callback;
import com.anchorfree.vpnsdk.exceptions.VpnException;
import com.anchorfree.vpnsdk.vpnservice.VPNState;

import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.rvtech.turbovpn.vpn.Config;
import com.rvtech.turbovpn.vpn.MainApplication;
import com.rvtech.turbovpn.vpn.fragments.ServersFragment;
import com.rvtech.turbovpn.vpn.nativeTemplete.NativeTemplateStyle;
import com.rvtech.turbovpn.vpn.nativeTemplete.TemplateView;
import com.rvtech.turbovpn.vpn.utils.Converter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.google.android.material.navigation.NavigationView;
import com.infideap.drawerbehavior.AdvanceDrawerLayout;
import com.kikt.view.CustomRatingBar;
import com.northghost.hydraclient.R;
import com.pepperonas.materialdialog.MaterialDialog;
import com.skyfishjy.library.RippleBackground;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import debugger.Helper;

public abstract class UIActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    protected static final String TAG = MainActivity.class.getSimpleName();
    protected static final String HELPER_TAG = "Helper";

    private RippleBackground rippleBackground;
    private RippleBackground ripplepoint1;
    private RippleBackground ripplepoint2;
    private RippleBackground ripplepoint3;
    private AdView mAdView;

    @BindView(R.id.connection_details_block)
    LinearLayout connection_details_block;

    private AdvanceDrawerLayout drawer;

    //    @BindView(R.id.main_toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.connect_btn)
    ImageView vpn_connect_btn;

    @BindView(R.id.uploading_speed)
    TextView uploading_speed_textview;

    @BindView(R.id.downloading_speed)
    TextView downloading_speed_textview;

    @BindView(R.id.vpn_connection_time)
    TextView vpn_connection_time;

    @BindView(R.id.vpn_connection_time_text)
    TextView vpn_connection_time_text;


    @BindView(R.id.connection_state)
    TextView connectionStateTextView;

    @BindView(R.id.selected_server)
    TextView selectedServerTextView;

    @BindView(R.id.drawer_opener)
    ImageView Drawer_opener_image;

    //    Lottie Variebles Start
    @BindView(R.id.vpn_connecting)
    protected LottieAnimationView vpn_connection_state;

    @BindView(R.id.vpn_speed_test)
    protected LottieAnimationView speedTest;
//    Lottie Variebles Ended

    /*google ads*/
    private UnifiedNativeAd nativeAd;
    private InterstitialAd mInterstitialAd;

    @BindView(R.id.adview)
    TemplateView templateView;


    private Handler mUIHandler = new Handler(Looper.getMainLooper());
    final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };

    private void initSpeedTest() {

        speedTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UIActivity.this, SpeedTest.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_drawer);
        //mine admob banner
        MobileAds.initialize(this, initializationStatus -> {
        });

        mAdView = findViewById(R.id.mainadView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        //mine banner ends


        ButterKnife.bind(this);
        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);

        drawer = (AdvanceDrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        drawer.setViewScale(Gravity.START, 0.9f);
        drawer.setRadius(Gravity.START, 35);
        drawer.setViewElevation(Gravity.START, 20);


        rippleBackground = findViewById(R.id.content_ripple);
        ripplepoint1 = findViewById(R.id.Ripple_point1);
        ripplepoint2 = findViewById(R.id.Ripple_point2);
        ripplepoint3 = findViewById(R.id.Ripple_point3);

        ripplepoint1.startRippleAnimation();
        ripplepoint2.startRippleAnimation();
        ripplepoint3.startRippleAnimation();


        setupDrawer();


        //enable ads

        templateView.setVisibility(View.GONE);
        if (!Config.ads_subscription && getResources().getBoolean(R.bool.ads_flag)) {
            handleAds();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    startUIUpdateTask();
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUIUpdateTask();
    }

    protected abstract void isLoggedIn(Callback<Boolean> callback);

    protected abstract void loginToVpn();

    protected abstract void logOutFromVnp();

    @OnClick(R.id.go_pro)
    public void go_pro_click() {
        startActivity(new Intent(UIActivity.this, PurchasesActivity.class));
    }

    @OnClick(R.id.vpn_select_country)
    public void showRegionDialog() {
        //RegionChooserDialog.newInstance().show(getSupportFragmentManager(), RegionChooserDialog.TAG);

        ServersFragment.newInstance().show(getSupportFragmentManager(), ServersFragment.TAG);

//        startActivity(new Intent(UIActivity.this, ServersActivity.class));

    }

    @OnClick(R.id.share_app_link)
    public void shareAppClick() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey Buddy \uD83D\uDE0A," +
                    " Did you heard about a New & Advanced Powerful VPN \uD83E\uDD29 with fully secured and Advanced Servers also has Excellent User Interface \uD83D\uDE0D. " +
                    "It helps us to Browse Internet Safely and give access to tons of Internet content for Free \uD83E\uDD13." +
                    " Hurry Check it out Now: https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
            startActivity(Intent.createChooser(shareIntent, "choose one"));
        } catch (Exception e) {
        }
    }

    @OnClick(R.id.connect_btn)
    public void onConnectBtnClick(View v) {
        vpn_connection_time.setVisibility(View.GONE);
        vpn_connection_state.setVisibility(View.VISIBLE);

        if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
            mInterstitialAd.show();
        } else {
            isConnected(new Callback<Boolean>() {
                @Override
                public void success(@NonNull Boolean aBoolean) {
//                    vpn_connection_state.setVisibility(View.GONE);
//                    vpn_connection_time.setVisibility(View.VISIBLE);
//                    vpn_connection_time.setText("Connected");

                    if (aBoolean) {

                        new MaterialDialog.Builder(UIActivity.this)
                                .title("Confirmation")
                                .message("Are You Sure to Disconnect The Turbo VPN")
                                .positiveText("Disconnect")
                                .negativeText("CANCEL")
                                .positiveColor(R.color.pink_700)
                                .negativeColor(R.color.yellow_700)
                                .buttonCallback(new MaterialDialog.ButtonCallback() {
                                    @Override
                                    public void onPositive(MaterialDialog dialog) {
                                        super.onPositive(dialog);
                                        disconnectFromVnp();
//                                        vpn_connect_btn.setImageResource(R.drawable.ic_connect_vpn);
                                    }

                                    @Override
                                    public void onNegative(MaterialDialog dialog) {
                                        super.onNegative(dialog);
//                                                Toast.makeText(MainActivity.this, "Cancel", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .show();

                    } else {
                        connectToVpn();
                    }
                }

                @Override
                public void failure(@NonNull VpnException e) {

                }
            });
        }
    }


    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVnp();

    protected abstract void chooseServer();

    protected abstract void getCurrentServer(Callback<String> callback);

    protected void startUIUpdateTask() {
        stopUIUpdateTask();
        mUIHandler.post(mUIUpdateRunnable);
    }

    protected void stopUIUpdateTask() {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        updateUI();
    }

    protected abstract void checkRemainingTraffic();

    protected void updateUI() {
        UnifiedSDK.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {

                switch (vpnState) {
                    case IDLE: {
                        rippleBackground.stopRippleAnimation();
                        rippleBackground.setVisibility(View.INVISIBLE);
                        uploading_speed_textview.setText("0 B");
                        downloading_speed_textview.setText("0 B");

                        vpn_connection_state.setVisibility(View.GONE);
                        vpn_connection_time.setVisibility(View.VISIBLE);
                        vpn_connection_time.setText("Not Connected");

                        connectionStateTextView.setText(R.string.disconnected);
                        hideConnectProgress();
//                        vpn_connect_btn.setImageResource(R.drawable.ic_connect_vpn);

                        Helper.printLog("idle");
                        break;
                    }
                    case CONNECTED: {
                        vpn_connection_state.setVisibility(View.GONE);
                        vpn_connection_time.setVisibility(View.VISIBLE);
                        vpn_connection_time.setText("Connected");

                        rippleBackground.startRippleAnimation();
                        rippleBackground.setVisibility(View.VISIBLE);
                        connectionStateTextView.setText(R.string.connected);
                        hideConnectProgress();
                        Helper.printLog("connected");
                        break;
                    }
                    case CONNECTING_VPN:
                        connectionStateTextView.setText(R.string.connecting);

                        rippleBackground.stopRippleAnimation();
                        rippleBackground.setVisibility(View.INVISIBLE);
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        showConnectProgress();
                        break;
                    }
                    case PAUSED: {
//                        connectionStateTextView.setText(R.string.paused);
                        Helper.printLog("paused");
                        rippleBackground.startRippleAnimation();
                        rippleBackground.setVisibility(View.VISIBLE);
                        break;
                    }
                }
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });
        UnifiedSDK.getInstance().getBackend().isLoggedIn(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean isLoggedIn) {

                //make connect button enabled
            }

            @Override
            public void failure(@NonNull VpnException e) {

            }
        });

        getCurrentServer(new Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Locale locale = new Locale("", currentServer);
                        selectedServerTextView.setText(currentServer != null ? locale.getDisplayCountry() : "UNKNOWN");
                    }
                });
            }

            @Override
            public void failure(@NonNull VpnException e) {
                selectedServerTextView.setText("UNKNOWN");
            }
        });

    }

    protected void updateTrafficStats(long outBytes, long inBytes) {
        String outString = Converter.humanReadableByteCountOld(outBytes, false);
        String inString = Converter.humanReadableByteCountOld(inBytes, false);

        uploading_speed_textview.setText(inString);
        downloading_speed_textview.setText(outString);
    }

    protected void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {
        } else {
            String trafficUsed = Converter.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = Converter.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";

        }
    }

    protected void showLoginProgress() {
    }

    protected void hideLoginProgress() {
    }

    protected void showConnectProgress() {
        connectionStateTextView.setVisibility(View.VISIBLE);
    }

    protected void hideConnectProgress() {
        connectionStateTextView.setVisibility(View.VISIBLE);
    }

    protected void showMessage(String msg) {
        Toast.makeText(UIActivity.this, msg, Toast.LENGTH_SHORT).show();
    }

    private void handleUserLogin() {
        ((MainApplication) getApplication()).setNewHostAndCarrier(Config.baseURL, Config.carrierID);
        loginToVpn();
    }

    private void setupDrawer() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                UIActivity.this, drawer, null, 0, 0);//R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(UIActivity.this);

        initSpeedTest();
    }

    @OnClick(R.id.drawer_opener)
    public void OpenDrawer(View v) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
        // Handle navigation view item clicks here.
//        int id = menuitem.getItemId();
        switch (menuitem.getItemId()) {
            case R.id.nav_upgrade:
//            upgrade application is available...
//                RegionChooserDialog.newInstance().show(getSupportFragmentManager(), RegionChooserDialog.TAG);
                ServersFragment.newInstance().show(getSupportFragmentManager(), ServersFragment.TAG);
                break;
            case R.id.nav_unlock:
                //showSubsDilog();
                startActivity(new Intent(UIActivity.this, PurchasesActivity.class));
                break;
            case R.id.nav_helpus:
//            find help about the application
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developer_email)});
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.help_to_improve_us_email_subject));
                intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.help_to_improve_us_body));

                try {
                    startActivity(Intent.createChooser(intent, "send mail"));
                } catch (ActivityNotFoundException ex) {
                    Toast.makeText(this, "No mail app found!!!", Toast.LENGTH_SHORT);
                } catch (Exception ex) {
                    Toast.makeText(this, "Unexpected Error!!!", Toast.LENGTH_SHORT);
                }
                break;
            case R.id.nav_rate:
//            rate application...
                rateUs();
                break;

            case R.id.nav_share:
//            share the application...
                try {
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "share app");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Hey Buddy \uD83D\uDE0A," +
                            " Did you heard about a New & Advanced Powerful VPN \uD83E\uDD29 with fully secured and Advanced Servers also has Excellent User Interface \uD83D\uDE0D. " +
                            "It helps us to Browse Internet Safely and give access to tons of Internet content for Free \uD83E\uDD13." +
                            " Hurry Check it out Now: https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                    startActivity(Intent.createChooser(shareIntent, "choose one"));
                } catch (Exception e) {
                }
                break;
            case R.id.nav_setting:
//            Application settings...
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.nav_faq:
                startActivity(new Intent(this, Faq.class));
                break;

            case R.id.nav_about:
                showAboutDialog();
                break;

            case R.id.nav_policy:
                Uri uri = Uri.parse(getResources().getString(R.string.privacy_policy_link)); // missing 'http://' will cause crashed
                Intent intent_policy = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent_policy);
                break;
        }
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAboutDialog() {
        Dialog about_dialog = new Dialog(this);

        about_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        about_dialog.setContentView(R.layout.dialog_about);
        about_dialog.setCancelable(true);
        about_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(about_dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        ((ImageButton) about_dialog.findViewById(R.id.bt_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                about_dialog.dismiss();
            }
        });

        about_dialog.show();
        about_dialog.getWindow().setAttributes(lp);
    }

    protected void rateUs() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.rating_dialog_layout);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView poitiveButton = dialog.findViewById(R.id.dialog_rating_button_positive);
        TextView negativeButton = dialog.findViewById(R.id.dialog_rating_button_negative);
        CustomRatingBar ratingBar = dialog.findViewById(R.id.dialog_rating_rating_bar);

        ratingBar.setOnStarChangeListener(new CustomRatingBar.OnStarChangeListener() {
            @Override
            public void onStarChange(CustomRatingBar ratingBar, float star) {
                if (star <= 4) {
                    dialog.dismiss();
                    showFeedbacDialog();
                } else {
                    //go to playStore
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName()));
                    startActivity(intent);
                }
            }
        });

        poitiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    private void showFeedbacDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_feedback);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView poitiveButton = dialog.findViewById(R.id.dialog_rating_button_feedback_submit);
        TextView negativeButton = dialog.findViewById(R.id.dialog_rating_button_feedback_cancel);
        EditText editText = dialog.findViewById(R.id.dialog_rating_feedback);

        poitiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (editText.getText().toString().isEmpty() || editText.getText().toString().length() < 10) {
                    Helper.showToast(UIActivity.this, "Please explain the issue");
                } else {
                    // go to email
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.developer_email)});
                    intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedback_email_subject));
                    intent.putExtra(Intent.EXTRA_TEXT, editText.getText().toString());

                    try {
                        startActivity(Intent.createChooser(intent, "Send E-mail"));
                    } catch (ActivityNotFoundException ex) {
                        Helper.showToast(UIActivity.this, "No email app found.");
                    } catch (Exception ex) {
                        Helper.showToast(UIActivity.this, "Network Error");
                    }
                }
            }
        });

        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    /**
     * Google Admob Native ad
     **/
    private void handleAds() {

        if (getResources().getBoolean(R.bool.ads_flag)) {

            //loading native ad
            refreshAd();
            //interstitial
            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial));
            mInterstitialAd.loadAd(new AdRequest.Builder()
                    .build());

            mInterstitialAd.setAdListener(new AdListener() {

                @Override
                public void onAdFailedToLoad(int i) {
                    super.onAdFailedToLoad(i);
                }

                @Override
                public void onAdLoaded() {
                    super.onAdLoaded();
                }

                @Override
                public void onAdClosed() {
                    super.onAdClosed();

                    //load new ad
                    mInterstitialAd.loadAd(new AdRequest.Builder()
                            .build());

                    vpn_connection_time.setVisibility(View.GONE);
                    vpn_connection_state.setVisibility(View.VISIBLE);
                    //same things that was on button click
                    isConnected(new Callback<Boolean>() {
                        @Override
                        public void success(@NonNull Boolean aBoolean) {
                            if (aBoolean) {
                                disconnectFromVnp();
                            } else {

                                connectToVpn();
                            }
                        }

                        @Override
                        public void failure(@NonNull VpnException e) {

                        }
                    });
                }
            });
        }
    }

    //loading native ad
    private void populateUnifiedNativeAdView(UnifiedNativeAd nativeAd, UnifiedNativeAdView
            adView) {
        // Set the media view. Media content will be automatically populated in the media view once
        MediaView mediaView = adView.findViewById(R.id.ad_media);
        adView.setMediaView(mediaView);

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

        // The headline is guaranteed to be in every UnifiedNativeAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                    nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad. The SDK will populate the adView's MediaView
        // with the media content from this native ad.
        adView.setNativeAd(nativeAd);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAd.getVideoController();

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc.hasVideoContent()) {

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.

                    super.onVideoEnd();
                }
            });
        } else {
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     */
    private void refreshAd() {

        AdLoader.Builder builder = new AdLoader.Builder(this, getString(R.string.admob_native_advance));

        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                // You must call destroy on old ads when you are done with them,
                // otherwise you will have a memory leak.
                if (nativeAd != null) {
                    nativeAd.destroy();
                }
//                nativeAd = unifiedNativeAd;
//
//
//                RelativeLayout relativeLayout =
//                        findViewById(R.id.fl_adplaceholder);
//                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater()
//                        .inflate(R.layout.ad_unified, null);
//                populateUnifiedNativeAdView(unifiedNativeAd, adView);
//
//
//                relativeLayout.removeAllViews();
//                relativeLayout.addView(adView);

                NativeTemplateStyle styles = new
                        NativeTemplateStyle.Builder().build();
                TemplateView adView = findViewById(R.id.adview);
                adView.setVisibility(View.VISIBLE);
                adView.setStyles(styles);
                adView.setNativeAd(unifiedNativeAd);
            }

        });

        VideoOptions videoOptions = new VideoOptions.Builder()
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                Log.w(HELPER_TAG, "onAdFailedToLoad: " + errorCode);
            }
        }).build();

        adLoader.loadAd(new AdRequest.Builder()
                .build());
    }

    // in app subs/billing

}
