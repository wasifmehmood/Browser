package com.example.privatebrowser.IncognitoActivities;


import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import android.widget.ImageButton;
import android.widget.Toast;

import com.example.privatebrowser.R;
import com.example.privatebrowser.Vpn.VpnActivity;

import androidx.core.app.ActivityCompat;

public class StartActivity extends Activity implements View.OnClickListener {

    private ImageButton startBrowsing, startVpn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        inItUi();

        registerListener();

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

    private void inItUi() {

        startBrowsing = findViewById(R.id.btn_start_browsing);
        startVpn = findViewById(R.id.btn_vpn);
    }

    private void registerListener() {

        startBrowsing.setOnClickListener(this);
        startVpn.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_start_browsing:
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_vpn:
                Intent vpnIntent = new Intent(this, VpnActivity.class);
                startActivity(vpnIntent);
                break;
        }

    }
}
