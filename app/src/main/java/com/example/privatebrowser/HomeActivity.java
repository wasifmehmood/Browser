package com.example.privatebrowser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.privatebrowser.BrowserActivities.BrowserSearchActivity;
import com.example.privatebrowser.Classes.ChangeLanguage;
import com.example.privatebrowser.DualBrowserActivities.DualBrowserActivity;
import com.example.privatebrowser.IncognitoActivities.MainActivity;
import com.example.privatebrowser.IncognitoActivities.SearchActivity;
import com.example.privatebrowser.Vpn.VpnActivity;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class HomeActivity extends Activity implements View.OnClickListener {

    ImageButton normalBrowserBtn, privateBrowserBtn, dualBrowserBtn, vpnButton;
    private ChangeLanguage changeLanguage;

//    public void setLocale(String localeStr){
//
//        Locale myLocale = new Locale(localeStr);
//        Locale.setDefault(myLocale);
//        Configuration config = new Configuration();
//        config.setLocale(myLocale);
//        Context context = this;
//        context.getResources().updateConfiguration(config,
//                context.getResources().getDisplayMetrics());
//    }

//    private static final String Locale_Preference = "Locale Preference";
//    private static final String Locale_KeyValue = "Saved Locale";
//
//    //Get locale method in preferences
//    public void loadLocale() {
//
//        changeLanguage = new ChangeLanguage(this);
//        String language = ChangeLanguage.sharedPreferences.getString(Locale_KeyValue, "");
//
//        changeLanguage.setAppLocale(language);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguage = new ChangeLanguage(this);
        changeLanguage.loadLocale();
        if(ChangeLanguage.sharedPreferences.getString(ChangeLanguage.Locale_KeyValue,"").isEmpty()) {
            changeLanguage.customDialog();
        }
        setContentView(R.layout.activity_home);

        normalBrowserBtn = findViewById(R.id.btn_start_normal_browsing);
        privateBrowserBtn = findViewById(R.id.btn_start_private_browsing);
        dualBrowserBtn = findViewById(R.id.btn_dual_browser);
        vpnButton = findViewById(R.id.btn_vpn);

        normalBrowserBtn.setOnClickListener(this);
        privateBrowserBtn.setOnClickListener(this);
        dualBrowserBtn.setOnClickListener(this);
        vpnButton.setOnClickListener(this);

        permissionCheck();

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
                } else {

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
            Intent intent = new Intent(this, BrowserSearchActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_start_private_browsing) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_dual_browser) {
            Intent intent = new Intent(this, DualBrowserActivity.class);
            startActivity(intent);
        } else if (v.getId() == R.id.btn_vpn) {
            Intent intent = new Intent(this, VpnActivity.class);
            startActivity(intent);
        }
    }
}
