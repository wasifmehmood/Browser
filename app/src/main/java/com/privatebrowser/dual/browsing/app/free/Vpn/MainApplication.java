package com.privatebrowser.dual.browsing.app.free.Vpn;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import com.anchorfree.hydrasdk.HydraSDKConfig;
import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.api.ClientInfo;
import com.anchorfree.hydrasdk.vpnservice.connectivity.NotificationConfig;
import com.privatebrowser.dual.browsing.app.free.BuildConfig;
import com.privatebrowser.dual.browsing.app.free.R;

public class MainApplication extends Application {

    private static final String CHANNEL_ID = "vpn";

    @Override
    public void onCreate() {
        super.onCreate();
        initVpn();
    }

    public void initVpn() {
        notificationChannel();
        SharedPreferences prefs = getPrefs();
        ClientInfo clientInfo = ClientInfo.newBuilder()
                .baseUrl(prefs.getString(BuildConfig.STORED_HOST_URL_KEY, BuildConfig.BASE_HOST))
                .carrierId(prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, BuildConfig.BASE_CARRIER_ID))
                .build();

        NotificationConfig notificationConfig = NotificationConfig.newBuilder()
                .title(getResources().getString(R.string.app_name))
                .channelId(CHANNEL_ID)
                .build();

        HydraSdk.setLoggingLevel(Log.VERBOSE);

        HydraSDKConfig config = HydraSDKConfig.newBuilder()
                .observeNetworkChanges(true) //sdk will handle network changes and start/stop vpn
                .captivePortal(true) //sdk will handle if user is behind captive portal wifi
                .moveToIdleOnPause(false)//sdk will report PAUSED state
                .build();
        HydraSdk.init(this, clientInfo, notificationConfig, config);
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
        initVpn();
    }

    public SharedPreferences getPrefs() {
        return getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    private void notificationChannel() {
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
