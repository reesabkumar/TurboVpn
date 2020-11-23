package com.rvtech.turbovpn.vpn.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.airbnb.lottie.LottieAnimationView;
import com.rvtech.turbovpn.vpn.Config;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.northghost.hydraclient.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import debugger.Helper;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class SpeedTest extends AppCompatActivity {

    @BindView(R.id.tv_uploadSpeed)
    TextView tvUploadSpeed;
    @BindView(R.id.tv_downloadSpeed)
    TextView tvDownloadSpeed;
    @BindView(R.id.percentTxt)
    TextView percentTxt;
    @BindView(R.id.speed_identification)
    TextView speed_identification;

    @BindView(R.id.activity_name)
    TextView activity_name;

    @BindView(R.id.download_text)
    TextView download_text;
    @BindView(R.id.upload_text)
    TextView upload_text;

    @BindView(R.id.start_btn)
    RelativeLayout start_btn;

    @BindView(R.id.speed_test_animation)
    RelativeLayout speed_test_animation;

    @BindView(R.id.finish_activity)
    ImageView finish_activity;

    @BindView(R.id.upload_icon)
    ImageView upload_icon;

    @BindView(R.id.download_icon)
    ImageView download_icon;

    @BindView(R.id.vpn_speed_test)
    LottieAnimationView loading_bar;

    private NativeAd nativeAd;
    private NativeAdLayout nativeAdLayout;

    private LinearLayout adView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed_test);
        ButterKnife.bind(this);

        activity_name.setText("Speed Test");

        start_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownloadSpeedTest();

                start_btn.setVisibility(View.GONE);

                speed_test_animation.setVisibility(View.VISIBLE);
                loading_bar.playAnimation();
                percentTxt.setVisibility(View.VISIBLE);
                speed_identification.setVisibility(View.VISIBLE);
                speed_identification.setText("Downloading Speed");


                download_text.setTextColor(Color.parseColor("#000000"));
                tvDownloadSpeed.setTextColor(Color.parseColor("#000000"));
                download_icon.setColorFilter(ContextCompat.getColor(SpeedTest.this, R.color.black));

                upload_text.setTextColor(Color.parseColor("#000000"));
                tvUploadSpeed.setTextColor(Color.parseColor("#000000"));
                upload_icon.setColorFilter(ContextCompat.getColor(SpeedTest.this, R.color.black));
            }
        });
        finish_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        if (!Config.ads_subscription && getResources().getBoolean(R.bool.ads_flag)) {
            loadNativeAd();
        }
    }

    private void startDownloadSpeedTest() {

        Thread downloadSpeedThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SpeedTestSocket speedTestSocket = new SpeedTestSocket();

                // add a listener to wait for speedtest completion and progress
                speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                    @Override
                    public void onCompletion(SpeedTestReport report) {
                        // called when download/upload is complete
                        Helper.printLog("Final download Speed: " + formatFileSize(Double.parseDouble(report.getTransferRateBit().toString())));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                startUploadSpeedTest();
                                speed_identification.setText("Uploading Speed");

                                download_icon.setColorFilter(ContextCompat.getColor(SpeedTest.this, R.color.primary));
                                download_text.setTextColor(Color.parseColor("#2FA6F8"));
                                tvDownloadSpeed.setTextColor(Color.parseColor("#2FA6F8"));
                            }
                        });

                    }

                    @Override
                    public void onError(SpeedTestError speedTestError, String errorMessage) {
                        // called when a download/upload error occur
                        //tvDownloadSpeed.setText(errorMessage);
                    }

                    @Override
                    public void onProgress(float percent, SpeedTestReport report) {
                        // called to notify download/upload progress

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                percentTxt.setText(percent + "%");
                                tvDownloadSpeed.setText(formatFileSize(Double.parseDouble(report.getTransferRateBit().toString())));
                            }
                        });
                    }
                });
                speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1M.iso", 10000);
            }
        });

        downloadSpeedThread.start();
    }

    private void startUploadSpeedTest() {

        Thread uploadSpeedThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SpeedTestSocket speedTestSocket = new SpeedTestSocket();

                // add a listener to wait for speedtest completion and progress
                speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

                    @Override
                    public void onCompletion(SpeedTestReport report) {
                        // called when download/upload is complete
                        Helper.printLog("Final download Speed: " + formatFileSize(Double.parseDouble(report.getTransferRateBit().toString())));

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loading_bar.pauseAnimation();
                                speed_test_animation.setVisibility(View.GONE);
                                percentTxt.setVisibility(View.GONE);
                                speed_identification.setVisibility(View.GONE);
                                start_btn.setVisibility(View.VISIBLE);

                                upload_text.setTextColor(Color.parseColor("#2FA6F8"));
                                tvUploadSpeed.setTextColor(Color.parseColor("#2FA6F8"));
                                upload_icon.setColorFilter(ContextCompat.getColor(SpeedTest.this, R.color.primary));
                            }
                        });

                    }

                    @Override
                    public void onError(SpeedTestError speedTestError, String errorMessage) {
                        // called when a download/upload error occur
                        //tvDownloadSpeed.setText(errorMessage);
                    }

                    @Override
                    public void onProgress(float percent, SpeedTestReport report) {
                        // called to notify download/upload progress

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                percentTxt.setText(percent + "%");
                                tvUploadSpeed.setText(formatFileSize(Double.parseDouble(report.getTransferRateBit().toString())));
                            }
                        });
                    }
                });
                speedTestSocket.startUpload("http://ipv4.ikoula.testdebit.info/", 1000000, 10000);
            }
        });
        uploadSpeedThread.start();
    }


    public static String formatFileSize(double size) {

        String hrSize;
        double b = size;
        double k = size / 1024.0;
        double m = ((size / 1024.0) / 1024.0);
        double g = (((size / 1024.0) / 1024.0) / 1024.0);
        double t = ((((size / 1024.0) / 1024.0) / 1024.0) / 1024.0);

        DecimalFormat dec = new DecimalFormat("0.00");

        if (t > 1) {
            hrSize = dec.format(t).concat(" ");
        } else if (g > 1) {
            hrSize = dec.format(g);
        } else if (m > 1) {
            hrSize = dec.format(m).concat("Mbps");
        } else if (k > 1) {
            hrSize = dec.format(k).concat("Kbps");
        } else {
            hrSize = dec.format(b);
        }

        return hrSize;
    }

    private void loadNativeAd() {
        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(this, getResources().getString(R.string.fb_native_banner_id));

        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
            }

            @Override
            public void onError(Ad ad, AdError adError) {
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
            }
        });

        // Request an ad
        nativeAd.loadAd();
    }

    private void inflateAd(NativeAd nativeAd) {

        nativeAd.unregisterView();

        // Add the Ad view into the ad container.
        nativeAdLayout = findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(SpeedTest.this);
        // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdOptionsViewprivate NativeAdLayout nativeAdLayout;
        LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(SpeedTest.this, nativeAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdTitle.setText(nativeAd.getAdvertiserName());
        nativeAdBody.setText(nativeAd.getAdBodyText());
        nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
        nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
        sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

        // Create a list of clickable views
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);

        // Register the Title and CTA button to listen for clicks.
        nativeAd.registerViewForInteraction(
                adView,
                nativeAdMedia,
                nativeAdIcon,
                clickableViews);
    }
}
