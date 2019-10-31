package com.example.privatebrowser.IncognitoActivities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebBackForwardList;
import android.webkit.WebHistoryItem;
import android.webkit.WebView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.privatebrowser.Classes.BrowserWebViewClass;
import com.example.privatebrowser.DatabaseOperation.DatabaseClass;

import com.example.privatebrowser.Interfaces.FeaturesInterface;
import com.example.privatebrowser.R;
import com.example.privatebrowser.Classes.WebViewClass;
import com.example.privatebrowser.Reciever.DownloadCompleteReciever;
import com.example.privatebrowser.utils.Utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FeaturesInterface, DownloadListener {

    private WebView webView;
    private WebViewClass webViewClass;
    private BrowserWebViewClass browserWebViewClass;
    private Toolbar toolbar;
    private long downloadID;
    private final File folder = new File(Environment.getExternalStorageDirectory() + "/PrivateBrowser");
    private DatabaseClass databaseClass;
    String browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseClass = new DatabaseClass(this);
        if (!folder.exists()) {
            folder.mkdir();
        }
        inItUi();
        setCustomActionBar();

        browser = getIntent().getStringExtra("browser");

        if (browser.equals("main")) {


        } else if (browser.equals("incognito")) {
            webViewClass.setWebView(webView);
            Toast.makeText(this, "Damn", Toast.LENGTH_SHORT).show();
        }
        urlCheck();
        myRegisterReciever();
        webView.setDownloadListener(this);
        listFilesForFolder(folder);

//        WebBackForwardList currentList = webView.copyBackForwardList();
//        int currentSize = currentList.getSize();
//
//        Toast.makeText(this, ""+currentSize, Toast.LENGTH_SHORT).show();
//        for (int i=0; i<currentSize; ++i)
//        {
//            WebHistoryItem item = currentList.getItemAtIndex(i);
//            String url2 = item.getUrl();
//            Log.d("url", "The URL at index: " + i + " is " + url2 );
//            Toast.makeText(this, "url", Toast.LENGTH_SHORT).show();
//        }

    }

    /**
     * This function reads the download folder and and gets its length. It also gets the length
     * of SQLite database in which the name of the file its value true or false is stored.
     * Then it compares both lengths, if it is not equal it searches the specific file name
     * which is not stored. So the one which is not stored will return Empty and will be
     * inserted into the SQLite.
     *
     * @param folder This parameter has the path of the download folder
     */
    private void listFilesForFolder(final File folder) {


        if (folder.listFiles() != null) {
            for (final File fileEntry : folder.listFiles()) {
                {
                    if (fileEntry.isDirectory()) {
                        listFilesForFolder(fileEntry);
                    } else {

                        databaseClass.readRecord();
                        if (Utils.boolUtils.size() != folder.length()) {
                            if (databaseClass.searchRecord(fileEntry.getName()).equals("Empty")) {
                                databaseClass.insertRecord(fileEntry.getName(), true);

                            }
                        }
                    }
                }
            }
        }
    }

    private void myRegisterReciever() {
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
                Toast.makeText(MainActivity.this, "Download Completed", Toast.LENGTH_SHORT).show();
                listFilesForFolder(folder);
            }
        }
    };

    /**
     * Here variables/widgets are initialized
     */
    private void inItUi() {

        toolbar = findViewById(R.id.custom_action_bar);
        webView = findViewById(R.id.webkit);
        webViewClass = new WebViewClass();
        browserWebViewClass = new BrowserWebViewClass();
    }

    /**
     * Here toolbar is set as an actionbar
     */
    private void setCustomActionBar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }

    /**
     * URL is checked as whether it has http or https or not
     */


    private void urlCheck() {

        String url = getIntent().getStringExtra("url");

        if (url != null && browser.equals("main")) {

            browserWebViewClass.startBrowserWebView(url, webView, MainActivity.this, this);
            Toast.makeText(this, "main", Toast.LENGTH_SHORT).show();

        } else if (url != null && browser.equals("incognito")) {
            webViewClass.startWebView(url, webView, MainActivity.this);
            Toast.makeText(this, "incognito", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Override method for creating/inflating an option menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_web_option_menu, menu);

        return true;
    }

    /**
     * override method for what happens when an item is selected in the option menu
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_back:
                if (webView.canGoBack())
                    webView.goBack();
                else if (!webView.canGoBack())
                    onBackPressed();
                return true;
            case R.id.action_forward:
                if (webView.canGoForward())
                    webView.goForward();
                return true;
            case R.id.bookmarks:
                showBookmarks();
                return true;
            case R.id.downloads:
                showDownloads();
                return true;
            case R.id.bookmarkThis:
                addToBookmarks();
                return true;
            case R.id.exit:
                finish();
                moveTaskToBack(true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void addToBookmarks() {

        customDialog(webView.getUrl());

    }

    private EditText bookmarkNameEt;
    private EditText bookmarkUrlEt;
    private String bookmarkNameStr;
    private String bookmarkUrlStr;

    //DIALOG
    private void customDialog(String url) {

        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("Title...");


        // set the custom dialog components - text, image and button
        bookmarkNameEt = dialog.findViewById(R.id.bookmarkNameEt);
        bookmarkUrlEt = dialog.findViewById(R.id.bookmarkUrlEt);

        bookmarkUrlEt.setText(url);

        Button addBookmarkBtn = dialog.findViewById(R.id.addBookmarkBtn);

        addBookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bookmarkNameStr = bookmarkNameEt.getText().toString();
                bookmarkUrlStr = bookmarkUrlEt.getText().toString();

                String urlToHost;

                try {
                    URL url = new URL(webView.getUrl());
                    urlToHost = url.getHost();

                    if (databaseClass.searchBookmarkRecord(bookmarkUrlStr).equals("Empty")) {

                        if (bookmarkNameStr.equals("")) {
                            databaseClass.insertBookmarkRecord(webView.getUrl(), urlToHost, true);
                            Toast.makeText(MainActivity.this, "Bookmark Saved", Toast.LENGTH_SHORT).show();
                        } else {
                            databaseClass.insertBookmarkRecord(webView.getUrl(), bookmarkNameStr, true);
                            Toast.makeText(MainActivity.this, "Bookmark Saved", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Bookmark Already Exists", Toast.LENGTH_SHORT).show();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }


                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /**
     * Override method for what happens when back button is pressed
     */
    @Override
    public void onBackPressed() {

        if (webView.canGoBack()) {
            if (browser.equals("incognito")) {
                CookieManager.getInstance().removeAllCookies(null);
                CookieManager.getInstance().removeSessionCookies(null);
                webView.clearCache(true);
            }
            webView.goBack();

        } else {
            super.onBackPressed();
        }
    }

    /**
     * Override method for what happens when application is sent to a pause state
     */

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (browser.equals("incognito")) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().removeSessionCookies(null);
            webView.clearHistory();
            webView.clearCache(true);
        }
            unregisterReceiver(onDownloadComplete);
            super.onDestroy();
    }

    @Override
    public void showBookmarks() {

        Intent intent = new Intent(this, BookmarksActivity.class);
        startActivity(intent);
    }

    @Override
    public void showDownloads() {

        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {
        DownloadManager.Request request = new DownloadManager.Request(
                Uri.parse(url));
        request.setMimeType(mimeType);
        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        request.addRequestHeader("User-Agent", userAgent);
        request.setDescription("Downloading File...");
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(
                "/PrivateBrowser", URLUtil.guessFileName(
                        url, contentDisposition, mimeType));
        DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        if (dm != null) {
            DownloadCompleteReciever.downloadID = dm.enqueue(request);
            downloadID = dm.enqueue(request);
        }
        Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
    }

}
