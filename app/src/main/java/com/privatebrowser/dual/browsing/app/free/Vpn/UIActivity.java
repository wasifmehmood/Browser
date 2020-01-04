package com.privatebrowser.dual.browsing.app.free.Vpn;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.api.response.RemainingTraffic;
import com.anchorfree.hydrasdk.callbacks.Callback;
import com.anchorfree.hydrasdk.exceptions.HydraException;
import com.anchorfree.hydrasdk.vpnservice.VPNState;
import com.privatebrowser.dual.browsing.app.free.Classes.ChangeLanguage;
import com.privatebrowser.dual.browsing.app.free.R;

import com.privatebrowser.dual.browsing.app.free.utils.Converter;

import java.util.Objects;

public abstract class UIActivity extends AppCompatActivity {

    static final String TAG = VpnActivity.class.getSimpleName();
    private ChangeLanguage changeLanguage;

    protected Toolbar mainToolbar;

    ProgressBar progressBarConnecting;

    TextView trafficStats;

    TextView trafficLimitTv;

    TextView currServerButton;

    TextView selectedServerTv;

    Button connectButton;

    ImageView imgViewBack;

    TextView connectTv;

    private final Handler mUIHandler = new Handler(Looper.getMainLooper());
    private final Runnable mUIUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateUI();
            checkRemainingTraffic();
            mUIHandler.postDelayed(mUIUpdateRunnable, 10000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguage = new ChangeLanguage(this);
        changeLanguage.loadLocale();
        setContentView(R.layout.activity_vpn);

        ButterKnife.bind(this);

        connectTv = findViewById(R.id.text_view_connect);
        imgViewBack = findViewById(R.id.image_view_back);
        connectButton = findViewById(R.id.connect_btn);
        selectedServerTv = findViewById(R.id.selected_server);
        currServerButton = findViewById(R.id.optimal_server_btn);
        trafficLimitTv = findViewById(R.id.traffic_limit);
        trafficStats = findViewById(R.id.traffic_stats);
        progressBarConnecting = findViewById(R.id.connection_progress);
        mainToolbar = findViewById(R.id.main_toolbar);

        setSupportActionBar(mainToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        
        imgViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIActivity.super.onBackPressed();
            }
        });
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
            public void failure(@NonNull HydraException e) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopUIUpdateTask();
    }

    protected abstract boolean isLoggedIn();

    protected abstract void loginToVpn();

    protected abstract void logOutFromVpn();

    @OnClick(R.id.connect_btn)
    public void onConnectBtnClick(View v) {
        isConnected(new Callback<Boolean>() {
            @Override
            public void success(@NonNull Boolean aBoolean) {
                if (aBoolean) {
                    disconnectFromVpn();
                    if(isLoggedIn()) {
                        logOutFromVpn();
                    }
                } else {

                    connectToVpn();
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
    }

    protected abstract void isConnected(Callback<Boolean> callback);

    protected abstract void connectToVpn();

    protected abstract void disconnectFromVpn();

    @OnClick(R.id.optimal_server_btn)
    public void onServerChooserClick(View v) {
        chooseServer();
    }

    protected abstract void chooseServer();

    protected abstract void getCurrentServer(Callback<String> callback);

    void startUIUpdateTask() {
        stopUIUpdateTask();
        mUIHandler.post(mUIUpdateRunnable);
    }

    void stopUIUpdateTask() {
        mUIHandler.removeCallbacks(mUIUpdateRunnable);
        updateUI();
    }

    protected abstract void checkRemainingTraffic();

    void updateUI() {
        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {

                trafficStats.setVisibility(vpnState == VPNState.CONNECTED ? View.VISIBLE : View.INVISIBLE);
                trafficLimitTv.setVisibility(vpnState == VPNState.CONNECTED ? View.VISIBLE : View.INVISIBLE);

                switch (vpnState) {
                    case IDLE: {

                        connectButton.setBackgroundResource(R.drawable.connect_new);
                        connectTv.setText(R.string.connect);
                        break;
                    }
                    case CONNECTED: {
                        connectButton.setBackgroundResource(R.drawable.disconnect_new);
                        connectTv.setText(R.string.connected);
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        connectButton.setBackgroundResource(R.drawable.connecting_new);
                        connectTv.setText(R.string.connecting);
                        break;
                    }
                    case PAUSED: {

                        break;
                    }
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });

        getCurrentServer(new Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currServerButton.setText(currentServer != null ? R.string.current_server : R.string.optimal_server);
                        selectedServerTv.setText(currentServer != null ? currentServer : "UNKNOWN");
                    }
                });
            }

            @Override
            public void failure(@NonNull HydraException e) {
                currServerButton.setText(R.string.optimal_server);
                selectedServerTv.setText("UNKNOWN");
            }
        });

    }

    void updateTrafficStates(long outBytes, long inBytes) {
        String outString = Converter.humanReadableByteCountOld(outBytes, false);
        String inString = Converter.humanReadableByteCountOld(inBytes, false);

        trafficStats.setText(getResources().getString(R.string.traffic_stats, outString, inString));
    }

    void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {
            trafficLimitTv.setText("UNLIMITED available");
        } else {
            String trafficUsed = Converter.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = Converter.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";

            trafficLimitTv.setText(getResources().getString(R.string.traffic_limit, trafficUsed, trafficLimit));
        }
    }

    void hideConnectProgress() {
        progressBarConnecting.setVisibility(View.GONE);

    }

    void showMessage(String msg) {
        Toast.makeText(UIActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
