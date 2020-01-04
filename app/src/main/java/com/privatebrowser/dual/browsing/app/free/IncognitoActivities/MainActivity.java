package com.privatebrowser.dual.browsing.app.free.IncognitoActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.privatebrowser.dual.browsing.app.free.BrowserActivities.HistoryActivity;
import com.privatebrowser.dual.browsing.app.free.Classes.BrowserWebViewClass;
import com.privatebrowser.dual.browsing.app.free.Classes.ChangeLanguage;
import com.privatebrowser.dual.browsing.app.free.DatabaseOperation.DatabaseClass;

import com.privatebrowser.dual.browsing.app.free.HomeActivity;
import com.privatebrowser.dual.browsing.app.free.Interfaces.FeaturesInterface;
import com.privatebrowser.dual.browsing.app.free.R;
import com.privatebrowser.dual.browsing.app.free.Classes.WebViewClass;
import com.privatebrowser.dual.browsing.app.free.Reciever.DownloadCompleteReciever;
import com.privatebrowser.dual.browsing.app.free.Vpn.VpnActivity;
import com.privatebrowser.dual.browsing.app.free.utils.Utils;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements FeaturesInterface, DownloadListener,
        View.OnClickListener, SwipeRefreshLayout.OnRefreshListener, View.OnKeyListener {

    private WebView webView;
    private WebViewClass webViewClass;
    private BrowserWebViewClass browserWebViewClass;
    private Toolbar toolbar;
    private long downloadID;
    private final File folder = new File(Environment.getExternalStorageDirectory() + "/PrivateBrowser");
    private DatabaseClass databaseClass;
    private String browser;
    private EditText searchEditText;
    private EditText bookmarkNameEt;
    private EditText bookmarkUrlEt;
    private String bookmarkNameStr;
    private String bookmarkUrlStr;
    private ImageButton vpnButton;
    private ChangeLanguage changeLanguage;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipe;
    private ConstraintLayout constraintLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        browser = getIntent().getStringExtra("browser");
        if (browser.equals("main")) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.IncognitoTheme);
        }
        super.onCreate(savedInstanceState);
        changeLanguage = new ChangeLanguage(this);
        changeLanguage.loadLocale();
        setContentView(R.layout.activity_main);

        databaseClass = new DatabaseClass(this);
        if (!folder.exists()) {
            folder.mkdir();
        }
        inItUi();
        setCustomActionBar();


        if (browser.equals("main")) {
            browserWebViewClass.setBrowserWebView(webView);

        } else if (browser.equals("incognito")) {
            webViewClass.setWebView(webView);
            constraintLayout.setBackgroundColor(getResources().getColor(R.color.grey));
            toolbar.setBackgroundColor(getResources().getColor(R.color.grey));
        }

        urlCheck();
        myRegisterReciever();
        registerListeners();
        listFilesForFolder(folder);

        if (savedInstanceState != null) {
            webView.restoreState(savedInstanceState);
        }

        registerForContextMenu(webView);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);


        if (webView.getHitTestResult().getType() == WebView.HitTestResult.IMAGE_TYPE ||
                webView.getHitTestResult().getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE ||
                webView.getHitTestResult().getType() == WebView.HitTestResult.UNKNOWN_TYPE) {

            if (webView.getHitTestResult().getExtra() != null) {
                if (webView.getHitTestResult().getExtra().contains("http")) {

                    menu.setHeaderTitle(R.string.downloads);

                    menu.add(0, v.getId(), 0, "Download image");
                }
            }
        }
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {


        String url = webView.getHitTestResult().getExtra();
        String url2 = webView.getUrl();

//        Toast.makeText(this, "" + url, Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(this, DownloadActivity.class);
//        intent.putExtra("url", url);
//        startActivity(intent);
//        Toast.makeText(this, "" + url, Toast.LENGTH_SHORT).show();
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager
                .PERMISSION_GRANTED)) {
            Toast.makeText(this, "Allow storage permission to download files", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 3);
        } else {
            downloadFile(url, "/PrivateBrowser");
        }

        return super.onContextItemSelected(item);
    }

    private void downloadFile(String url, String directoryPictures) {
        if (url != null) {
            Uri uri = Uri.parse(url);

            String guessFileName = URLUtil.guessFileName(url, null, null);

            DownloadManager.Request request = new DownloadManager.Request(uri);

            request.setDestinationInExternalPublicDir(directoryPictures, guessFileName);
            request.setTitle(guessFileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

            DownloadManager manager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);

            if (manager != null) {
                manager.enqueue(request);
                databaseClass.insertRecord(guessFileName, true);
            }

            Toast.makeText(this, "Downloading", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        webView.saveState(outState);
    }

    private void registerListeners() {
        webView.setDownloadListener(this);
        vpnButton.setOnClickListener(this);
        swipe.setOnRefreshListener(this);
        searchEditText.setOnKeyListener(this);

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
//                listFilesForFolder(folder);
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
        browserWebViewClass = new BrowserWebViewClass(this);
        progressBar = findViewById(R.id.progress_bar);
        vpnButton = findViewById(R.id.button_vpn);
        searchEditText = findViewById(R.id.toolbar_et);
        constraintLayout = findViewById(R.id.main_constraint_layout);

        swipe = findViewById(R.id.swipe_refresh_layout);
    }


    /**
     * Here mainToolbar is set as an actionbar
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

            browserWebViewClass.startBrowserWebView(url, webView, MainActivity.this, this,
                    progressBar, swipe);
//            Toast.makeText(this, "main", Toast.LENGTH_SHORT).show();

        } else if (url != null && browser.equals("incognito")) {
            webViewClass.startWebView(url, webView, MainActivity.this, this, progressBar, swipe);
        }
    }

    private void urlCheck2(String url) {

        if (url != null && browser.equals("main")) {

            browserWebViewClass.startBrowserWebView(url, webView, MainActivity.this, this,
                    progressBar, swipe);
//            Toast.makeText(this, "main", Toast.LENGTH_SHORT).show();

        } else if (url != null && browser.equals("incognito")) {
            webViewClass.startWebView(url, webView, MainActivity.this, this, progressBar, swipe);
        }
    }

    /**
     * Override method for creating/inflating an option menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        if (browser.equals("main")) {
            inflater.inflate(R.menu.my_web_option_menu, menu);
            vpnButton.setBackgroundResource(R.drawable.menu_vpn_new);
        } else if (browser.equals("incognito")) {
            inflater.inflate(R.menu.incognito_option_menu, menu);
            vpnButton.setBackgroundResource(R.drawable.menu_vpn_incog);
        }
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
            case R.id.item_home:
                startHomeActivity();
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
            case R.id.history:
                showHistory();
                return true;
            case R.id.exit:
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startHomeActivity() {

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void addToBookmarks() {

        customDialog(webView.getUrl());

    }

    private void showHistory() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

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
     * This checks whether the url has a top level domain, if yes returns true.
     *
     * @return
     */

    private boolean checkDomain() {

        ArrayList<String> checkUrl = new ArrayList<>();
        checkUrl.add(".com");
        checkUrl.add(".net");
        checkUrl.add(".org");
        checkUrl.add(".edu");
        checkUrl.add(".int");
        checkUrl.add(".gov");
        checkUrl.add(".mil");
        boolean checkDomain = false;

        for (int i = 0; i < checkUrl.size(); i++) {
            checkDomain = searchEditText.getText().toString().contains(checkUrl.get(i));

            if (checkDomain) {
                break;
            }
        }
        return checkDomain;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Override method for what happens when user clicks a button on the keyboard
     *
     * @param v       retrieves the view
     * @param keyCode has the key code of keyboard
     * @param event   has the value of the key which is pressed
     */

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // If the event is a key-down event on the "enter" button
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            // Perform action on key press
            if (isNetworkAvailable()) {

                String url;

                if (URLUtil.isValidUrl(searchEditText.getText().toString()) && checkDomain()) {
                    url = searchEditText.getText().toString();
//                    Toast.makeText(MainActivity.this, "empty", Toast.LENGTH_SHORT).show();
                }
//                            else if(URLUtil.isValidUrl("https://"+searchEditText.getText().toString()) && bool)
//                            {
//                                url = "https://"+searchEditText.getText().toString();
//                                Toast.makeText(BrowserSearchActivity.this, "https", Toast.LENGTH_SHORT).show();
//
//                            }
                else if (URLUtil.isValidUrl("http://" + searchEditText.getText().toString()) && checkDomain()) {
                    url = "http://" + searchEditText.getText().toString();
//                    Toast.makeText(MainActivity.this, "http", Toast.LENGTH_SHORT).show();
                } else {
                    url = "https://www.google.com/#q=" + searchEditText.getText().toString();
                }

//                Toast.makeText(this, "" + url, Toast.LENGTH_SHORT).show();

                urlCheck2(url);

            } else {
                Toast.makeText(this, "Internet Not Connected", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return false;
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
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().removeSessionCookies(null);
            webView.clearHistory();
            webView.clearCache(true);
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
        intent.putExtra("browser", browser);
        startActivity(intent);
    }

    @Override
    public void showDownloads() {

        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
    }


    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType, long contentLength) {

        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager
                .PERMISSION_GRANTED)) {
            Toast.makeText(this, "Allow storage permission to download files", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 4);
        } else {
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
                databaseClass.insertRecord(URLUtil.guessFileName(url, contentDisposition, mimeType), true);
//            downloadID = dm.enqueue(request);
            }
            Toast.makeText(getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button_vpn) {
            Intent intent = new Intent(this, VpnActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onRefresh() {

        swipe.setRefreshing(true);
        ReloadWebView(webView.getUrl());
    }

    private void ReloadWebView(String url) {

        webView.loadUrl(url);
    }
}
