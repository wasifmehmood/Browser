package com.example.privatebrowser.Vpn;

import androidx.annotation.NonNull;

import com.anchorfree.hydrasdk.HydraSdk;
import com.anchorfree.hydrasdk.callbacks.TrafficListener;
import com.anchorfree.hydrasdk.callbacks.VpnStateListener;
//import com.example.privatebrowser.dialog.LoginDialog;
import com.example.privatebrowser.VpnDialog.RegionChooserDialog;

import android.app.ProgressDialog;
import android.util.Log;

import com.anchorfree.hydrasdk.SessionConfig;
import com.anchorfree.hydrasdk.SessionInfo;
import com.anchorfree.hydrasdk.api.AuthMethod;
import com.anchorfree.hydrasdk.api.data.Country;
import com.anchorfree.hydrasdk.api.data.ServerCredentials;
import com.anchorfree.hydrasdk.api.response.RemainingTraffic;
import com.anchorfree.hydrasdk.api.response.User;
import com.anchorfree.hydrasdk.callbacks.Callback;
import com.anchorfree.hydrasdk.callbacks.CompletableCallback;
import com.anchorfree.hydrasdk.compat.CredentialsCompat;
import com.anchorfree.hydrasdk.dns.DnsRule;
import com.anchorfree.hydrasdk.exceptions.ApiHydraException;
import com.anchorfree.hydrasdk.exceptions.HydraException;
import com.anchorfree.hydrasdk.exceptions.NetworkRelatedException;
import com.anchorfree.hydrasdk.exceptions.RequestException;
import com.anchorfree.hydrasdk.exceptions.VPNException;
import com.anchorfree.hydrasdk.vpnservice.VPNState;
import com.anchorfree.reporting.TrackingConstants;

import java.util.LinkedList;
import java.util.List;

public class VpnActivity extends UIActivity implements TrafficListener, VpnStateListener,
        RegionChooserDialog.RegionChooserInterface {

    private String selectedCountry = "";

    @Override
    protected void onStart() {
        super.onStart();
        HydraSdk.addTrafficListener(this);
        HydraSdk.addVpnListener(this);
        if(!HydraSdk.isLoggedIn())
        {
            loginToVpn();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        HydraSdk.removeVpnListener(this);
        HydraSdk.removeTrafficListener(this);

    }

    @Override
    public void onTrafficUpdate(long bytesTx, long bytesRx) {
        updateUI();
        updateTrafficStats(bytesTx, bytesRx);
    }

    @Override
    public void vpnStateChanged(@NonNull VPNState vpnState) {
        updateUI();
    }

    @Override
    public void vpnError(@NonNull HydraException e) {
        updateUI();
        handleError(e);
    }

    @Override
    protected boolean isLoggedIn() {
        return HydraSdk.isLoggedIn();
    }

    @Override
    protected void loginToVpn() {
//        showLoginProgress();
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Signing In");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0);
        progressDialog.show();

        AuthMethod authMethod = AuthMethod.anonymous();
        HydraSdk.login(authMethod, new Callback<User>() {
            @Override
            public void success(@NonNull User user) {
//                hideLoginProgress();
                updateUI();
                progressDialog.dismiss();
//                connectToVpn();
            }

            @Override
            public void failure(@NonNull HydraException e) {
//                hideLoginProgress();
                updateUI();

                handleError(e);
            }
        });
    }

    @Override
    protected void logOutFromVpn() {
//        showLoginProgress();

        HydraSdk.logout(new CompletableCallback() {
            @Override
            public void complete() {
//                disconnectFromVpn();
            }

            @Override
            public void error(@NonNull HydraException e) {

            }
        });
        selectedCountry = "";

//        hideLoginProgress();
        updateUI();
    }

    @Override
    protected void isConnected(final Callback<Boolean> callback) {
        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState vpnState) {
                callback.success(vpnState == VPNState.CONNECTED);
            }

            @Override
            public void failure(@NonNull HydraException e) {
                callback.success(false);
            }
        });
    }

    @Override
    protected void connectToVpn() {

        if (HydraSdk.isLoggedIn()) {
//            showConnectProgress();
            List<String> bypassDomains = new LinkedList<>();
            bypassDomains.add("*facebook.com");
            bypassDomains.add("*wtfismyip.com");
            HydraSdk.startVPN(new SessionConfig.Builder()
                    .withReason(TrackingConstants.GprReasons.M_UI)
                    .withVirtualLocation(selectedCountry)
                    .addDnsRule(DnsRule.Builder.bypass().fromDomains(bypassDomains))
                    .build(), new Callback<ServerCredentials>() {
                @Override
                public void success(@NonNull ServerCredentials serverCredentials) {
                    hideConnectProgress();
                    startUIUpdateTask();
                }

                @Override
                public void failure(@NonNull HydraException e) {
                    hideConnectProgress();
                    updateUI();

                    handleError(e);
                }
            });
        } else {
            loginToVpn();
            // showMessage("Login please");
        }
    }

    @Override
    protected void disconnectFromVpn() {
//        showConnectProgress();
        HydraSdk.stopVPN(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
            @Override
            public void complete() {
                hideConnectProgress();
                stopUIUpdateTask();
//                logOutFromVpn();
            }

            @Override
            public void error(@NonNull HydraException e) {
                hideConnectProgress();
                updateUI();

                handleError(e);
            }
        });
    }

    @Override
    protected void chooseServer() {


        if (HydraSdk.isLoggedIn()) {
            RegionChooserDialog.newInstance().show(getSupportFragmentManager(), RegionChooserDialog.TAG);
        } else {
            showMessage("Login please");
        }
    }

    @Override
    protected void getCurrentServer(final Callback<String> callback) {
        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    HydraSdk.getSessionInfo(new Callback<SessionInfo>() {
                        @Override
                        public void success(@NonNull SessionInfo sessionInfo) {
                            callback.success(CredentialsCompat.getServerCountry(sessionInfo.getCredentials()));
                        }

                        @Override
                        public void failure(@NonNull HydraException e) {
                            callback.success(selectedCountry);
                        }
                    });
                } else {
                    callback.success(selectedCountry);
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {
                callback.failure(e);
            }
        });
    }

    @Override
    protected void checkRemainingTraffic() {
        HydraSdk.remainingTraffic(new Callback<RemainingTraffic>() {
            @Override
            public void success(@NonNull RemainingTraffic remainingTraffic) {
                updateRemainingTraffic(remainingTraffic);
            }

            @Override
            public void failure(@NonNull HydraException e) {
                updateUI();

                handleError(e);
            }
        });
    }

//    @Override
//    public void setLoginParams(String hostUrl, String carrierId) {
//        ((MainApplication) getApplication()).setNewHostAndCarrier(hostUrl, carrierId);
//    }
//
//    @Override
//    public void loginUser() {
//        loginToVpn();
//    }

    @Override
    public void onRegionSelected(Country item) {

        selectedCountry = item.getCountry();
        updateUI();

        HydraSdk.getVpnState(new Callback<VPNState>() {
            @Override
            public void success(@NonNull VPNState state) {
                if (state == VPNState.CONNECTED) {
                    showMessage("Reconnecting to VPN with " + selectedCountry);
                    HydraSdk.stopVPN(TrackingConstants.GprReasons.M_UI, new CompletableCallback() {
                        @Override
                        public void complete() {
                            connectToVpn();
                        }

                        @Override
                        public void error(@NonNull HydraException e) {
                            // In this case we try to reconnect
                            selectedCountry = "";
                            connectToVpn();
                        }
                    });
                }
            }

            @Override
            public void failure(@NonNull HydraException e) {

            }
        });
    }

    // Example of error handling
    private void handleError(Throwable e) {
        Log.w(TAG, e);
        if (e instanceof NetworkRelatedException) {
            showMessage("Check internet connection");
        } else if (e instanceof VPNException) {
            switch (((VPNException) e).getCode()) {
                case VPNException.REVOKED:
                    showMessage("User revoked vpn permissions");
                    break;
                case VPNException.VPN_PERMISSION_DENIED_BY_USER:
                    showMessage("User canceled to grant vpn permissions");
                    break;
                case VPNException.HYDRA_ERROR_BROKEN:
                    showMessage("Connection with vpn service was lost");
                    break;
                case VPNException.HYDRA_DCN_BLOCKED_BW:
                    showMessage("Client traffic exceeded");
                    break;
                default:
                    showMessage("Error in VPN Service");
                    break;
            }
        } else if (e instanceof ApiHydraException) {
            switch (((ApiHydraException) e).getContent()) {
                case RequestException.CODE_NOT_AUTHORIZED:
                    showMessage("User unauthorized");
                    break;
                case RequestException.CODE_TRAFFIC_EXCEED:
                    showMessage("Server unavailable");
                    break;
                default:
                    showMessage("Other error. Check RequestException constants");
                    break;
            }
        }
    }

    //fake method to support migration documentation and list all available methods

}

