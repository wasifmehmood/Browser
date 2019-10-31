package com.example.privatebrowser.Reciever;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DownloadCompleteReciever extends BroadcastReceiver {

    public static long downloadID;

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        //Checking if the received broadcast is for our enqueued download by matching download id
        if (downloadID == id) {
            Log.i("download", " complete");
        }
    }
}
