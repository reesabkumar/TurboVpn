package com.rvtech.turbovpn.vpn;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.anchorfree.partner.api.ClientInfo;
import com.anchorfree.sdk.HydraTransportConfig;
import com.anchorfree.sdk.NotificationConfig;
import com.anchorfree.sdk.TransportConfig;
import com.anchorfree.sdk.UnifiedSDK;
import com.anchorfree.sdk.UnifiedSDKConfig;
import com.anchorfree.vpnsdk.callbacks.CompletableCallback;
import com.facebook.ads.AudienceNetworkAds;
import com.northghost.caketube.OpenVpnTransportConfig;
import com.northghost.hydraclient.BuildConfig;
import com.northghost.hydraclient.R;
import com.onesignal.OneSignal;
import com.pixplicity.easyprefs.library.Prefs;

import java.util.ArrayList;
import java.util.List;

public class MainApplication  extends MultiDexApplication {

    private static final String CHANNEL_ID = "vpn";

    private static Context context;


    public static Context getApplication() {
        return context;
    }

    public static Context getStaticContext() {
        return getApplication().getApplicationContext();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        context = this;
        // Initialize the Prefs class
        new Prefs.Builder()
                .setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();

        AudienceNetworkAds.initialize(this);

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();


        initHydraSdk();
    }

    UnifiedSDK unifiedSDK;

    public void initHydraSdk() {
        createNotificationChannel();
        SharedPreferences prefs = getPrefs();
        ClientInfo clientInfo = ClientInfo.newBuilder()
                .baseUrl(prefs.getString(BuildConfig.STORED_HOST_URL_KEY, BuildConfig.BASE_HOST))
                .carrierId(prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, BuildConfig.BASE_CARRIER_ID))
                .build();
        List<TransportConfig> transportConfigList = new ArrayList<>();
        transportConfigList.add(HydraTransportConfig.create());
        transportConfigList.add(OpenVpnTransportConfig.tcp());
        transportConfigList.add(OpenVpnTransportConfig.udp());
        UnifiedSDK.update(transportConfigList, CompletableCallback.EMPTY);
        UnifiedSDKConfig config = UnifiedSDKConfig.newBuilder().idfaEnabled(false).build();
        unifiedSDK = UnifiedSDK.getInstance(clientInfo, config);

        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                .title(getResources().getString(R.string.app_name))
                .channelId(CHANNEL_ID)
                .build();
        UnifiedSDK.update(notificationConfig);

        UnifiedSDK.setLoggingLevel(Log.VERBOSE);
    }

    public void setNewHostAndCarrier(String hostUrl, String carrierId) {
        SharedPreferences prefs = getPrefs();
        if (TextUtils.isEmpty(hostUrl)) {
            prefs.edit().remove(BuildConfig.STORED_HOST_URL_KEY).apply();
        } else {
            prefs.edit().putString(BuildConfig.STORED_HOST_URL_KEY, hostUrl).apply();
        }

        if (TextUtils.isEmpty(carrierId)) {
            prefs.edit().remove(BuildConfig.STORED_CARRIER_ID_KEY).apply();
        } else {
            prefs.edit().putString(BuildConfig.STORED_CARRIER_ID_KEY, carrierId).apply();
        }
        initHydraSdk();
    }

    public SharedPreferences getPrefs() {
        return getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Sample VPN";
            String description = "VPN notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
