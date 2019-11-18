package com.example.privatebrowser.Vpn;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
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
import com.example.privatebrowser.Classes.ChangeLanguage;
import com.example.privatebrowser.R;

import com.example.privatebrowser.utils.Converter;

import java.util.Objects;

public abstract class UIActivity extends AppCompatActivity {

    static final String TAG = VpnActivity.class.getSimpleName();
    private ChangeLanguage changeLanguage;

    @BindView(R.id.main_toolbar)
    protected Toolbar toolbar;

//    @BindView(R.id.login_btn)
//    TextView loginBtnTextView;
//
//    @BindView(R.id.login_state)
//    TextView loginStateTextView;

//    @BindView(R.id.login_progress)
//    ProgressBar loginProgressBar;

//    @BindView(R.id.connect_btn)
//    TextView connectBtnTextView;

//    @BindView(R.id.connection_state)
//    TextView connectionStateTextView;

    @BindView(R.id.connection_progress)
    ProgressBar connectionProgressBar;

    @BindView(R.id.traffic_stats)
    TextView trafficStats;

    @BindView(R.id.traffic_limit)
    TextView trafficLimitTextView;

    @BindView(R.id.optimal_server_btn)
    TextView currentServerBtn;

    @BindView(R.id.selected_server)
    TextView selectedServerTextView;

    @BindView(R.id.connect_btn)
    Button connectButton;

    @BindView(R.id.image_view_back)
    ImageView imageViewBack;

    @BindView(R.id.text_view_connect)
    TextView textViewConnect;

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

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
        
        imageViewBack.setOnClickListener(new View.OnClickListener() {
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
//
//    @OnClick(R.id.login_btn)
//    public void onLoginBtnClick(View v) {
//        if (isLoggedIn()) {
//            logOutFromVpn();
//        } else {
//            LoginDialog.newInstance().show(getSupportFragmentManager(), LoginDialog.TAG);
//        }
//    }

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
//                    if(!isLoggedIn()) {
//                        loginToVpn();
//                    }
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
                trafficLimitTextView.setVisibility(vpnState == VPNState.CONNECTED ? View.VISIBLE : View.INVISIBLE);

                switch (vpnState) {
                    case IDLE: {
//                        connectBtnTextView.setEnabled(true);
//                        connectBtnTextView.setText(R.string.connect);
//                        connectionStateTextView.setText(R.string.disconnected);
                        connectButton.setBackgroundResource(R.drawable.connect_new);
                        textViewConnect.setText(R.string.connect);
//                        hideConnectProgress();
                        break;
                    }
                    case CONNECTED: {
//                        connectBtnTextView.setEnabled(true);
//                        connectBtnTextView.setText(R.string.disconnect);
//                        connectionStateTextView.setText(R.string.connected);
                        connectButton.setBackgroundResource(R.drawable.disconnect_new);
                        textViewConnect.setText(R.string.connected);
//                        hideConnectProgress();
                        break;
                    }
                    case CONNECTING_VPN:
                    case CONNECTING_CREDENTIALS:
                    case CONNECTING_PERMISSIONS: {
                        connectButton.setBackgroundResource(R.drawable.connecting_new);
                        textViewConnect.setText(R.string.connecting);
//                        connectButton.setText(R.string.connecting);
//                        connectButton.setText(R.string.connecting);
//                        connectButton.setEnabled(false);
//                        showConnectProgress();
                        break;
                    }
                    case PAUSED: {
//                        connectBtnTextView.setEnabled(false);
//                        connectBtnTextView.setText(R.string.paused);
//                        connectionStateTextView.setText(R.string.paused);
                        break;
                    }
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
//        loginBtnTextView.setText(HydraSdk.isLoggedIn() ? R.string.log_out : R.string.log_in);
//        loginStateTextView.setText(HydraSdk.isLoggedIn() ? R.string.logged_in : R.string.logged_out);



        getCurrentServer(new Callback<String>() {
            @Override
            public void success(@NonNull final String currentServer) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentServerBtn.setText(currentServer != null ? R.string.current_server : R.string.optimal_server);
                        selectedServerTextView.setText(currentServer != null ? currentServer : "UNKNOWN");
                    }
                });
            }

            @Override
            public void failure(@NonNull HydraException e) {
                currentServerBtn.setText(R.string.optimal_server);
                selectedServerTextView.setText("UNKNOWN");
            }
        });

    }

    void updateTrafficStats(long outBytes, long inBytes) {
        String outString = Converter.humanReadableByteCountOld(outBytes, false);
        String inString = Converter.humanReadableByteCountOld(inBytes, false);

        trafficStats.setText(getResources().getString(R.string.traffic_stats, outString, inString));
    }

    void updateRemainingTraffic(RemainingTraffic remainingTrafficResponse) {
        if (remainingTrafficResponse.isUnlimited()) {
            trafficLimitTextView.setText("UNLIMITED available");
        } else {
            String trafficUsed = Converter.megabyteCount(remainingTrafficResponse.getTrafficUsed()) + "Mb";
            String trafficLimit = Converter.megabyteCount(remainingTrafficResponse.getTrafficLimit()) + "Mb";

            trafficLimitTextView.setText(getResources().getString(R.string.traffic_limit, trafficUsed, trafficLimit));
        }
    }

//    protected void showLoginProgress() {
//        loginProgressBar.setVisibility(View.VISIBLE);
//        loginStateTextView.setVisibility(View.GONE);
//    }
//
//    protected void hideLoginProgress() {
//        loginProgressBar.setVisibility(View.GONE);
//        loginStateTextView.setVisibility(View.VISIBLE);
//    }

    void showConnectProgress() {
        connectionProgressBar.setVisibility(View.VISIBLE);
//        connectionStateTextView.setVisibility(View.GONE);
    }

    void hideConnectProgress() {
        connectionProgressBar.setVisibility(View.GONE);
//        connectionStateTextView.setVisibility(View.VISIBLE);
    }

    void showMessage(String msg) {
        Toast.makeText(UIActivity.this, msg, Toast.LENGTH_SHORT).show();
    }
}
