package com.privatebrowser.dual.browsing.app.free;


import androidx.annotation.RequiresApi;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.privatebrowser.dual.browsing.app.free.R;
import com.privatebrowser.dual.browsing.app.free.BrowserActivities.BrowserSearchActivity;
import com.privatebrowser.dual.browsing.app.free.Classes.ChangeLanguage;
import com.privatebrowser.dual.browsing.app.free.DualBrowserActivities.DualBrowserActivity;

import com.privatebrowser.dual.browsing.app.free.IncognitoActivities.SearchActivity;
import com.privatebrowser.dual.browsing.app.free.Vpn.VpnActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;


public class HomeActivity extends Activity implements View.OnClickListener {


    private AdView adView;
    private InterstitialAd interstitialAd;

    private ImageButton normalBrowserBtn, privateBrowserBtn, dualBrowserBtn, vpnButton;
    private ChangeLanguage changeLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguage = new ChangeLanguage(this);
        changeLanguage.loadLocale();
        if (ChangeLanguage.sharedPreferences.getString(ChangeLanguage.Locale_KeyValue, "").isEmpty()) {
            changeLanguage.customDialog();
        }
        setContentView(R.layout.activity_home);

        MobileAds.initialize(this, getResources().getString(R.string.app_id));

        initUi();

        registerListeners();

        permissionCheck();
        bannerAd();
        reqNewInterstitial();

    }

    private void initUi() {

        normalBrowserBtn = findViewById(R.id.btn_start_normal_browsing);
        privateBrowserBtn = findViewById(R.id.btn_start_private_browsing);
        dualBrowserBtn = findViewById(R.id.btn_dual_browser);
        vpnButton = findViewById(R.id.btn_vpn);
    }

    private void registerListeners() {

        normalBrowserBtn.setOnClickListener(this);
        privateBrowserBtn.setOnClickListener(this);
        dualBrowserBtn.setOnClickListener(this);
        vpnButton.setOnClickListener(this);
    }

    private void permissionCheck() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                2);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                }
                else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Permission denied to read/write to your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.btn_start_normal_browsing) {

            if (interstitialAd.isLoaded() && interstitialAd != null) {
                interstitialAd.show();
            } else {
                Intent intent = new Intent(this, BrowserSearchActivity.class);
                startActivity(intent);
                reqNewInterstitial();
            }
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    reqNewInterstitial();
                    Intent intent = new Intent(HomeActivity.this, BrowserSearchActivity.class);
                    startActivity(intent);
                }
            });
        } else if (v.getId() == R.id.btn_start_private_browsing) {
            if (interstitialAd.isLoaded() && interstitialAd != null) {
                interstitialAd.show();
            } else {
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                reqNewInterstitial();
            }
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    reqNewInterstitial();
                    Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
                    startActivity(intent);

                }
            });

        } else if (v.getId() == R.id.btn_dual_browser) {

            if (interstitialAd.isLoaded() && interstitialAd != null) {
                interstitialAd.show();
            } else {
                Intent intent = new Intent(this, DualBrowserActivity.class);
                intent.putExtra("browser", "dual");
                startActivity(intent);
                reqNewInterstitial();
            }
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    reqNewInterstitial();
                    Intent intent = new Intent(HomeActivity.this, DualBrowserActivity.class);
                    intent.putExtra("browser", "dual");
                    startActivity(intent);
                }
            });


        } else if (v.getId() == R.id.btn_vpn) {

            if (interstitialAd.isLoaded() && interstitialAd != null) {
                interstitialAd.show();
            } else {
                Intent intent = new Intent(this, VpnActivity.class);
                startActivity(intent);
                reqNewInterstitial();
            }
            interstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdClosed() {
                    super.onAdClosed();
                    reqNewInterstitial();
                    Intent intent = new Intent(HomeActivity.this, VpnActivity.class);
                    startActivity(intent);
                }
            });

        }
    }

    @Override
    public void onBackPressed() {

        exitt();

//        finishAffinity();
    }

    public void reqNewInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.Interstitial));
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void bannerAd() {
        adView = findViewById(R.id.banner);
        AdRequest adrequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adrequest);
        adView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int error) {
                adView.setVisibility(View.GONE);
            }

        });
    }

    public void exitt() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater layoutInflater = getLayoutInflater();
        final View dialogView = layoutInflater.inflate(R.layout.alert_dialog, null);
        Button btn_positive = dialogView.findViewById(R.id.dialog_positive_btn);
        Button btn_neutral = dialogView.findViewById(R.id.exit);
        builder.setView(dialogView);
//        RotateAnimation rotateAnimation = new RotateAnimation(30.0f, 360.0f,
//                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        rotateAnimation.setDuration((long) 2 * 1000);
//        rotateAnimation.setRepeatCount(Animation.INFINITE);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        //Activity com.example.waseemvpnwifiscanner.Activities.MainActivity has leaked window DecorView@f0fe06[MainActivity] that was originally added here
        btn_positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rate_us();
                alertDialog.cancel();
                //                finish();
            }
        });
        // Set negative/no button click listener
//        btn_neutral.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // Dismiss the alert dialog\
//                alertDialog.cancel();
//            }
//        });
        //Neutral Button
        btn_neutral.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View view) {
                moveTaskToBack(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    finishAffinity();
                }
            }
        });
    }

    public void rate_us() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
    }

}
