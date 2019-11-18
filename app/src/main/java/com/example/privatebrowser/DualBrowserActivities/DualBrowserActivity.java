package com.example.privatebrowser.DualBrowserActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.privatebrowser.BrowserActivities.BrowserSearchActivity;
import com.example.privatebrowser.BrowserActivities.HistoryActivity;
import com.example.privatebrowser.Classes.BrowserWebViewClass;
import com.example.privatebrowser.Classes.ChangeLanguage;
import com.example.privatebrowser.Classes.WebViewClass;
import com.example.privatebrowser.CustomWidgets.CustomEditText;
import com.example.privatebrowser.DatabaseOperation.DatabaseClass;
import com.example.privatebrowser.HomeActivity;
import com.example.privatebrowser.IncognitoActivities.BookmarksActivity;
import com.example.privatebrowser.IncognitoActivities.DownloadActivity;
import com.example.privatebrowser.IncognitoActivities.MainActivity;
import com.example.privatebrowser.IncognitoActivities.SearchActivity;
import com.example.privatebrowser.Interfaces.DrawableClickListener;
import com.example.privatebrowser.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class DualBrowserActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener,
        SwipeRefreshLayout.OnRefreshListener {

    private WebView webViewDualBrowser, webViewDualIncognito;
    private ImageView actionBackDual, actionForwardDual, optionMenuDualBrowser, optionMenuDualIncognito,
            actionBackDualIncognito, actionForwardDualIncognito;
    private CustomEditText searchEditTextDualBrowser, searchEditTextDualIncognito;
    private PopupWindow mWebPopUpBrowser, mWebPopUpIncognito;
    private BrowserWebViewClass browserWebViewClass;
    private WebViewClass incognitoWebViewClass;
    private ProgressBar progressBarDual, progressBarIncognito;
    private ChangeLanguage changeLanguage;
    private ImageView imageViewIncognitoDual;
    private TextView textViewGoneIncognito;
    private ConstraintLayout layoutProgressBar;
    private SwipeRefreshLayout swipeDual, swipeIncognito;
    private String savedUrlBrowser, savedUrlIncognito;
    private String getUrlBrowser, getUrlIncognito;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguage = new ChangeLanguage(this);
        changeLanguage.loadLocale();
        setContentView(R.layout.activity_dual_browser);

        databaseClass = new DatabaseClass(this);
        browserWebViewClass = new BrowserWebViewClass(this);
        incognitoWebViewClass = new WebViewClass();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        inItUi();
        inItWebPop();
        inItWebPopIncognito();
        registerListeners();

        browser = getIntent().getStringExtra("browser");
        url2 = getIntent().getStringExtra("url");
        getUrlBrowser = getIntent().getStringExtra("saved_url_browser");
        getUrlIncognito = getIntent().getStringExtra("saved_url_incognito");

        if (browser.equals("dual_browser")) {
            webViewDualBrowser.setVisibility(View.VISIBLE);
            swipeDual.setVisibility(View.VISIBLE);
            progressBarDual.setVisibility(View.VISIBLE);

            browserWebViewClass.setBrowserWebView(webViewDualBrowser);
            browserWebViewClass.startBrowserWebView(url2, webViewDualBrowser,
                    DualBrowserActivity.this, DualBrowserActivity.this, progressBarDual,
                    swipeDual);

            if (getUrlIncognito != null) {

                if (getUrlIncognito.length() > 0) {
                    swipeIncognito.setVisibility(View.VISIBLE);
                    imageViewIncognitoDual.setVisibility(View.GONE);
                    webViewDualIncognito.setVisibility(View.VISIBLE);
                    layoutProgressBar.setVisibility(View.VISIBLE);
                    textViewGoneIncognito.setVisibility(View.GONE);
//
                    incognitoWebViewClass.hideProgressBar(layoutProgressBar);
                    incognitoWebViewClass.setWebView(webViewDualIncognito);
                    incognitoWebViewClass.startWebView(getUrlIncognito, webViewDualIncognito,
                            DualBrowserActivity.this, DualBrowserActivity.this, progressBarIncognito,
                            swipeIncognito);

                }
            }
            savedUrlBrowser = url2;
        } else if (browser.equals("dual_incognito")) {

            swipeIncognito.setVisibility(View.VISIBLE);
            imageViewIncognitoDual.setVisibility(View.GONE);
            webViewDualIncognito.setVisibility(View.VISIBLE);
            layoutProgressBar.setVisibility(View.VISIBLE);
            textViewGoneIncognito.setVisibility(View.GONE);
            incognitoLinearLayout.setVisibility(View.VISIBLE);

            incognitoWebViewClass.hideProgressBar(layoutProgressBar);
            incognitoWebViewClass.setWebView(webViewDualIncognito);
            incognitoWebViewClass.startWebView(url2, webViewDualIncognito,
                    DualBrowserActivity.this, DualBrowserActivity.this, progressBarIncognito,
                    swipeIncognito);

            if (getUrlBrowser != null) {

                if (getUrlBrowser.length() > 0) {
                    webViewDualBrowser.setVisibility(View.VISIBLE);
                    swipeDual.setVisibility(View.VISIBLE);
                    progressBarDual.setVisibility(View.VISIBLE);

                    browserWebViewClass.setBrowserWebView(webViewDualBrowser);
                    browserWebViewClass.startBrowserWebView(getUrlBrowser, webViewDualBrowser,
                            DualBrowserActivity.this, DualBrowserActivity.this, progressBarDual,
                            swipeDual);

                }
            }
            savedUrlBrowser = url2;

        }

        MobileAds.initialize(this,getResources().getString(R.string.app_id));
    }

    String browser;
    String url2;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();

        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure you want to exit dual browser?")

                // Specifying a listener allows you to take an action before dismissing the dialog.
                // The dialog is automatically dismissed when a dialog button is clicked.
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        finish();
                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                        finish();
                    }
                })

                // A null listener allows the button to dismiss the dialog and take no further action.
                .setNegativeButton(android.R.string.no, null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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

                if (v.getId() == R.id.et_search_dual_browser) {

//                    webViewDualBrowser.setVisibility(View.VISIBLE);
//                    swipeDual.setVisibility(View.VISIBLE);
//                    progressBarDual.setVisibility(View.VISIBLE);
//
//                    url = "https://www.google.com/#q=" + searchEditTextDualBrowser.getText().toString();
//
//                    browserWebViewClass.setBrowserWebView(webViewDualBrowser);
//                    browserWebViewClass.startBrowserWebView(url, webViewDualBrowser,
//                            DualBrowserActivity.this, DualBrowserActivity.this, progressBarDual,
//                            swipeDual);

                    webViewDualBrowser.setVisibility(View.VISIBLE);
                    swipeDual.setVisibility(View.VISIBLE);
                    progressBarDual.setVisibility(View.VISIBLE);

                    String url;

                    if (URLUtil.isValidUrl(searchEditTextDualBrowser.getText().toString()) && checkDomain("main")) {
                        url = searchEditTextDualBrowser.getText().toString();
                        Toast.makeText(DualBrowserActivity.this, "empty", Toast.LENGTH_SHORT).show();
                    }
//                            else if(URLUtil.isValidUrl("https://"+searchEditText.getText().toString()) && bool)
//                            {
//                                url = "https://"+searchEditText.getText().toString();
//                                Toast.makeText(BrowserSearchActivity.this, "https", Toast.LENGTH_SHORT).show();
//
//                            }
                    else if (URLUtil.isValidUrl("http://" + searchEditTextDualBrowser.getText().toString()) && checkDomain("main")) {
                        url = "http://" + searchEditTextDualBrowser.getText().toString();
                        Toast.makeText(DualBrowserActivity.this, "http", Toast.LENGTH_SHORT).show();
                    } else {
                        url = "https://www.google.com/#q=" + searchEditTextDualBrowser.getText().toString();
                    }

//                        String url = "https://www.google.com/#q=" + searchEditTextDualBrowser.getText().toString();

                    browserWebViewClass.setBrowserWebView(webViewDualBrowser);
                    browserWebViewClass.startBrowserWebView(url, webViewDualBrowser,
                            DualBrowserActivity.this, DualBrowserActivity.this, progressBarDual,
                            swipeDual);

                } else if (v.getId() == R.id.et_search_dual_incognito) {
//                    imageViewIncognitoDual.setVisibility(View.GONE);
//                    webViewDualIncognito.setVisibility(View.VISIBLE);
//                    layoutProgressBar.setVisibility(View.VISIBLE);
//                    textViewGoneIncognito.setVisibility(View.GONE);
//
//                    url = "https://www.google.com/#q=" + searchEditTextDualIncognito.getText().toString();
//
//                    incognitoWebViewClass.hideProgressBar(layoutProgressBar);
//                    incognitoWebViewClass.setWebView(webViewDualIncognito);
//                    incognitoWebViewClass.startWebView(url, webViewDualIncognito,
//                            DualBrowserActivity.this, DualBrowserActivity.this, progressBarIncognito,
//                            swipeIncognito);

                    imageViewIncognitoDual.setVisibility(View.GONE);
                    webViewDualIncognito.setVisibility(View.VISIBLE);
                    swipeIncognito.setVisibility(View.VISIBLE);
                    layoutProgressBar.setVisibility(View.VISIBLE);
                    textViewGoneIncognito.setVisibility(View.GONE);
                    incognitoLinearLayout.setVisibility(View.VISIBLE);

                    String url;

                    if (URLUtil.isValidUrl(searchEditTextDualIncognito.getText().toString()) && checkDomain("incognito")) {
                        url = searchEditTextDualIncognito.getText().toString();
                        Toast.makeText(DualBrowserActivity.this, "empty", Toast.LENGTH_SHORT).show();
                    }
                    else if (URLUtil.isValidUrl("http://" + searchEditTextDualIncognito.getText().toString()) && checkDomain("incognito")) {
                        url = "http://" + searchEditTextDualIncognito.getText().toString();
                        Toast.makeText(DualBrowserActivity.this, "http Incognito", Toast.LENGTH_SHORT).show();
                    } else {
                        url = "https://www.google.com/#q=" + searchEditTextDualIncognito.getText().toString();
                    }

//                        String url = "https://www.google.com/#q=" + searchEditTextDualIncognito.getText().toString();

                    incognitoWebViewClass.hideProgressBar(layoutProgressBar);
                    incognitoWebViewClass.setWebView(webViewDualIncognito);
                    incognitoWebViewClass.startWebView(url, webViewDualIncognito,
                            DualBrowserActivity.this, DualBrowserActivity.this, progressBarIncognito,
                            swipeIncognito);
                }


//                Intent intent = new Intent(DualBrowserActivity.this, MainActivity.class);
//                String url, url2;
//
//                if (v.getId() == R.id.et_search_dual_browser) {
//                    url = "https://www.google.com/#q=" + searchEditTextDualBrowser.getText().toString();
//                    intent.putExtra("url", url);
//                } else if (v.getId() == R.id.et_search_dual_incognito) {
//                    url2 = "https://www.google.com/#q=" + searchEditTextDualIncognito.getText().toString();
//                    intent.putExtra("url", url2);
//                }
//                Toast.makeText(this, "TEST", Toast.LENGTH_SHORT).show();
//
//                intent.putExtra("browser", "incognito");
//                startActivity(intent);
            } else {
                Toast.makeText(this, "Internet Not Connected", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();

        searchEditTextClickListener();
        searchEditTextIncognitoClickListener();
    }

    LinearLayout linearLayout;

    private void registerListeners() {

        searchEditTextDualBrowser.setOnClickListener(this);
        optionMenuDualBrowser.setOnClickListener(this);
        actionBackDual.setOnClickListener(this);
        actionForwardDual.setOnClickListener(this);
        searchEditTextDualIncognito.setOnClickListener(this);
        actionBackDualIncognito.setOnClickListener(this);
        actionForwardDualIncognito.setOnClickListener(this);
        optionMenuDualIncognito.setOnClickListener(this);
        linearLayout.setOnClickListener(this);
        incognitoLinearLayoutOption.setOnClickListener(this);

        searchEditTextDualBrowser.setOnKeyListener(this);
        searchEditTextDualIncognito.setOnKeyListener(this);

        swipeDual.setOnRefreshListener(this);
        swipeIncognito.setOnRefreshListener(this);
    }

    LinearLayout incognitoLinearLayout;
    LinearLayout incognitoLinearLayoutOption;

    private void inItUi() {

        webViewDualBrowser = findViewById(R.id.webView_dual_browser);
        webViewDualIncognito = findViewById(R.id.webView_dual_incognito);

        actionBackDual = findViewById(R.id.action_back_dual);
        actionForwardDual = findViewById(R.id.action_forward_dual);
        actionBackDualIncognito = findViewById(R.id.action_back_dual_incognito);
        actionForwardDualIncognito = findViewById(R.id.action_forward_dual_incognito);
        optionMenuDualBrowser = findViewById(R.id.option_menu_dual_browser);
        optionMenuDualIncognito = findViewById(R.id.option_menu_dual_incognito);

        searchEditTextDualBrowser = findViewById(R.id.et_search_dual_browser);
        searchEditTextDualIncognito = findViewById(R.id.et_search_dual_incognito);

        progressBarDual = findViewById(R.id.progress_bar_dual);
        progressBarIncognito = findViewById(R.id.progress_bar_dual_incognito);

        imageViewIncognitoDual = findViewById(R.id.incognito_dual_image_view);

        layoutProgressBar = findViewById(R.id.layout_progress_bar);

        textViewGoneIncognito = findViewById(R.id.text_view_gone_incognito);

        linearLayout = findViewById(R.id.linear_layout_option);

        swipeDual = findViewById(R.id.swipe_refresh_layout_dual);
        swipeIncognito = findViewById(R.id.swipe_refresh_layout_dual_incognito);

        incognitoBarConstraintLayout = findViewById(R.id.incognito_bar_constraint_layout);
        dualIncognitoLinearLayout = findViewById(R.id.dual_incognito_linear_layout);

        incognitoLinearLayout = findViewById(R.id.linear_layout_incognito);
        incognitoLinearLayoutOption = findViewById(R.id.linear_layout_incognito_option);
    }

    /**
     * Checks if internet is connected or not
     *
     * @return true if yes, false if not available
     */

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void searchEditTextClickListener() {

        searchEditTextDualBrowser.setDrawableClickListener(new DrawableClickListener() {

            public void onClick(DrawableClickListener.DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {//Do something here
                    if (isNetworkAvailable()) {

                        webViewDualBrowser.setVisibility(View.VISIBLE);
                        swipeDual.setVisibility(View.VISIBLE);
                        progressBarDual.setVisibility(View.VISIBLE);

                        String url;

                        if (URLUtil.isValidUrl(searchEditTextDualBrowser.getText().toString()) && checkDomain("main")) {
                            url = searchEditTextDualBrowser.getText().toString();
                            Toast.makeText(DualBrowserActivity.this, "empty", Toast.LENGTH_SHORT).show();
                        }
//                            else if(URLUtil.isValidUrl("https://"+searchEditText.getText().toString()) && bool)
//                            {
//                                url = "https://"+searchEditText.getText().toString();
//                                Toast.makeText(BrowserSearchActivity.this, "https", Toast.LENGTH_SHORT).show();
//
//                            }
                        else if (URLUtil.isValidUrl("http://" + searchEditTextDualBrowser.getText().toString()) && checkDomain("main")) {
                            url = "http://" + searchEditTextDualBrowser.getText().toString();
                            Toast.makeText(DualBrowserActivity.this, "http", Toast.LENGTH_SHORT).show();
                        } else {
                            url = "https://www.google.com/#q=" + searchEditTextDualBrowser.getText().toString();
                        }

//                        String url = "https://www.google.com/#q=" + searchEditTextDualBrowser.getText().toString();

                        browserWebViewClass.setBrowserWebView(webViewDualBrowser);
                        browserWebViewClass.startBrowserWebView(url, webViewDualBrowser,
                                DualBrowserActivity.this, DualBrowserActivity.this, progressBarDual,
                                swipeDual);

//                            Intent intent = new Intent(DualBrowserActivity.this, MainActivity.class);
//                            String url = "https://www.google.com/#q=" + searchEditTextDualBrowser.getText().toString();
//                            intent.putExtra("url", url);
//                            intent.putExtra("browser", "incognito");
//                            startActivity(intent);
                    } else {
                        Toast.makeText(DualBrowserActivity.this, "Internet Not connected", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }

    private void searchEditTextIncognitoClickListener() {

        searchEditTextDualIncognito.setDrawableClickListener(new DrawableClickListener() {

            public void onClick(DrawableClickListener.DrawablePosition target) {
                if (target == DrawablePosition.RIGHT) {//Do something here
                    if (isNetworkAvailable()) {

                        imageViewIncognitoDual.setVisibility(View.GONE);
                        webViewDualIncognito.setVisibility(View.VISIBLE);
                        swipeIncognito.setVisibility(View.VISIBLE);
                        layoutProgressBar.setVisibility(View.VISIBLE);
                        textViewGoneIncognito.setVisibility(View.GONE);
                        incognitoLinearLayout.setVisibility(View.VISIBLE);

                        String url;

                        if (URLUtil.isValidUrl(searchEditTextDualIncognito.getText().toString()) && checkDomain("incognito")) {
                            url = searchEditTextDualIncognito.getText().toString();
                            Toast.makeText(DualBrowserActivity.this, "empty", Toast.LENGTH_SHORT).show();
                        }
//                            else if(URLUtil.isValidUrl("https://"+searchEditText.getText().toString()) && bool)
//                            {
//                                url = "https://"+searchEditText.getText().toString();
//                                Toast.makeText(BrowserSearchActivity.this, "https", Toast.LENGTH_SHORT).show();
//
//                            }
                        else if (URLUtil.isValidUrl("http://" + searchEditTextDualIncognito.getText().toString()) && checkDomain("incognito")) {
                            url = "http://" + searchEditTextDualIncognito.getText().toString();
                            Toast.makeText(DualBrowserActivity.this, "http Incognito", Toast.LENGTH_SHORT).show();
                        } else {
                            url = "https://www.google.com/#q=" + searchEditTextDualIncognito.getText().toString();
                        }

//                        String url = "https://www.google.com/#q=" + searchEditTextDualIncognito.getText().toString();

                        incognitoWebViewClass.hideProgressBar(layoutProgressBar);
                        incognitoWebViewClass.setWebView(webViewDualIncognito);
                        incognitoWebViewClass.startWebView(url, webViewDualIncognito,
                                DualBrowserActivity.this, DualBrowserActivity.this, progressBarIncognito,
                                swipeIncognito);


//                            Intent intent = new Intent(DualBrowserActivity.this, MainActivity.class);
//                            String url = "https://www.google.com/#q=" + searchEditTextDualBrowser.getText().toString();
//                            intent.putExtra("url", url);
//                            intent.putExtra("browser", "incognito");
//                            startActivity(intent);
                    } else {
                        Toast.makeText(DualBrowserActivity.this, "Internet Not connected", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        });
    }

    /**
     * This checks whether the url has a top level domain, if yes returns true.
     *
     * @return
     */
    private boolean checkDomain(String browser) {

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
            if (browser.equals("main")) {
                checkDomain = searchEditTextDualBrowser.getText().toString().contains(checkUrl.get(i));
            } else if (browser.equals("incognito")) {
                checkDomain = searchEditTextDualIncognito.getText().toString().contains(checkUrl.get(i));
            }

            if (checkDomain) {
                break;
            }
        }
        return checkDomain;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.action_back_dual:
                if (webViewDualBrowser.canGoBack()) {
                    webViewDualBrowser.goBack();
                }
                break;
            case R.id.action_forward_dual:
                if (webViewDualBrowser.canGoForward()) {
                    webViewDualBrowser.goForward();
                }
                break;
            case R.id.linear_layout_option:
                mWebPopUpBrowser.showAsDropDown(v, v.getWidth() / 2 - mWebPopUpBrowser.getContentView().getWidth() / 2, 0);
                break;
            case R.id.option_menu_dual_browser:
                mWebPopUpBrowser.showAsDropDown(v, v.getWidth() / 2 - mWebPopUpBrowser.getContentView().getWidth() / 2, 0);
                break;

            case R.id.action_back_dual_incognito:
                if (webViewDualIncognito.canGoBack()) {
                    webViewDualIncognito.goBack();
                }
                break;
            case R.id.action_forward_dual_incognito:
                if (webViewDualIncognito.canGoForward()) {
                    webViewDualIncognito.goForward();
                }
                break;
            case R.id.option_menu_dual_incognito:
//                Toast.makeText(this, "master", Toast.LENGTH_SHORT).show();
                mWebPopUpIncognito.showAsDropDown(v, v.getWidth() / 2 - mWebPopUpIncognito.getContentView().getWidth() / 2, 0);
                break;
            case  R.id.linear_layout_incognito_option:
                mWebPopUpIncognito.showAsDropDown(v, v.getWidth() / 2 - mWebPopUpIncognito.getContentView().getWidth() / 2, 0);
                break;
        }
    }

    private String url;
    private String title;
    private DatabaseClass databaseClass;
    ConstraintLayout incognitoBarConstraintLayout;
    LinearLayout dualIncognitoLinearLayout;

    private void inItWebPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.web_popup_menu, null);
        mWebPopUpBrowser = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //final TextView setting = alert.findViewById(R.id.setting_btn);

        TextView addToBookmark = view.findViewById(R.id.add_to_book);
        TextView downloads = view.findViewById(R.id.downloads_dual_browser);
        TextView history = view.findViewById(R.id.history_dual_browser);
        TextView fullscreen = view.findViewById(R.id.fullscreen_dual_browser);


        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                dualIncognitoLinearLayout.setVisibility(View.GONE);
//                incognitoBarConstraintLayout.setVisibility(View.GONE);
//
//                swipeDual.setLayoutParams();
                if (isFullscreen) {
                    fullscreen.setText(getString(R.string.fullscreen));
                    fullscreen.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.fullscreen2), null, null, null);
                    exitFullscreenBrowser();
                    mWebPopUpBrowser.dismiss();

                } else {
                    fullscreen.setText(getString(R.string.exit_fullscreen));
                    fullscreen.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.exitscreen), null, null, null);
                    fullScreenBrowser();
                    mWebPopUpBrowser.dismiss();
                }
            }
        });


        downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DualBrowserActivity.this, DownloadActivity.class);
                mWebPopUpBrowser.dismiss();
                startActivity(intent);

            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DualBrowserActivity.this, HistoryActivity.class);
                mWebPopUpBrowser.dismiss();
                startActivity(intent);
            }
        });

        addToBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mWebPopUpBrowser.dismiss();

                url = webViewDualBrowser.getUrl();
                title = webViewDualBrowser.getTitle();
//                    Intent intent = new Intent(myMainActivity.this, Dual_incognoTabsActivity.class);
                String urlToHost;

                try {
                    URL url = new URL(webViewDualBrowser.getUrl());
                    urlToHost = url.getHost();

                    if (databaseClass.searchBookmarkRecord(url.toString()).equals("Empty")) {
                        databaseClass.insertBookmarkRecord(webViewDualBrowser.getUrl(), urlToHost, true);
                        Toast.makeText(DualBrowserActivity.this, "Bookmark Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DualBrowserActivity.this, "Bookmark Already Exists", Toast.LENGTH_SHORT).show();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        //TextView upsideDown = alert.findViewById(R.id.upside_btn);
        TextView exit = view.findViewById(R.id.exit_btn);
        TextView tv_show_bookmarks = view.findViewById(R.id.tv_show_bookmark);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebPopUpBrowser.dismiss();
                finishAffinity();
            }
        });

        tv_show_bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(DualBrowserActivity.this, BookmarksActivity.class);
                intent.putExtra("browser", "dual_browser");
                mWebPopUpBrowser.dismiss();

                savedUrlBrowser = searchEditTextDualBrowser.getText().toString();
                savedUrlIncognito = searchEditTextDualIncognito.getText().toString();

                intent.putExtra("saved_url_browser", savedUrlBrowser);
                intent.putExtra("saved_url_incognito", savedUrlIncognito);
                intent.putExtra("activity", String.valueOf(this));

                startActivity(intent);
            }
        });

        mWebPopUpBrowser.setAnimationStyle(R.style.TopPopAnim);
        mWebPopUpBrowser.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWebPopUpBrowser.setFocusable(true);
        mWebPopUpBrowser.setOutsideTouchable(false);
    }

    LinearLayout linearLayoutDual, linearLayoutIncognito, linearLayoutDualIncognito;

    ConstraintLayout dualConstraint, incognitoConstraint, incognitoConstraintProgressBar;
    ProgressBar dualProgressBar;

    void initUi2() {
        linearLayoutDual = findViewById(R.id.linear_layout_dual);
        linearLayoutIncognito = findViewById(R.id.linear_layout_incognito);


        dualConstraint = findViewById(R.id.dual_constraint);
        incognitoConstraint = findViewById(R.id.incognito_bar_constraint_layout);
        incognitoConstraintProgressBar = findViewById(R.id.layout_progress_bar);

        dualProgressBar = findViewById(R.id.progress_bar_dual);

        linearLayoutDualIncognito = findViewById(R.id.dual_incognito_linear_layout);

    }

    boolean isFullscreen = false;
    LinearLayout.LayoutParams param, paramIncognito;

    void fullScreenBrowser() {

        if (interstitialAd.isLoaded() && interstitialAd != null) {
            interstitialAd.show();
        } else {
            isFullscreen = true;
            initUi2();

            param = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2.0f
            );
            linearLayoutDual.setLayoutParams(param);

            incognitoConstraint.setVisibility(View.GONE);
            incognitoConstraintProgressBar.setVisibility(View.GONE);
            linearLayoutDualIncognito.setVisibility(View.GONE);
            reqNewInterstitial();
        }
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                reqNewInterstitial();
                isFullscreen = true;
                initUi2();

                param = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2.0f
                );
                linearLayoutDual.setLayoutParams(param);

                incognitoConstraint.setVisibility(View.GONE);
                incognitoConstraintProgressBar.setVisibility(View.GONE);
                linearLayoutDualIncognito.setVisibility(View.GONE);
            }
        });

    }

    private void exitFullscreenBrowser() {
        isFullscreen = false;

        param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
        );
        linearLayoutDual.setLayoutParams(param);

        incognitoConstraint.setVisibility(View.VISIBLE);
        incognitoConstraintProgressBar.setVisibility(View.VISIBLE);
        linearLayoutDualIncognito.setVisibility(View.VISIBLE);
        progressBarDual.setVisibility(View.GONE);

    }

    private void inItWebPopIncognito() {
        View view = LayoutInflater.from(this).inflate(R.layout.web_popup_menu_incognito, null);
        mWebPopUpIncognito = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        TextView addToBookmark = view.findViewById(R.id.add_to_book_incognito);
        TextView downloads = view.findViewById(R.id.downloads_dual_incognito);
        TextView fullscreen = view.findViewById(R.id.fullscreen_dual_incognito);


        fullscreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (isFullscreenIncognito) {
                    fullscreen.setText(getString(R.string.fullscreen));
                    fullscreen.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.fullscreen1), null, null, null);
                    exitFullscreenIncognito();
                    mWebPopUpIncognito.dismiss();
                } else {
                    fullscreen.setText(getString(R.string.exit_fullscreen));
                    fullscreen.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.exitscreen2), null, null, null);
                    fullScreenIncognito();
                    mWebPopUpIncognito.dismiss();

                }
            }
        });
        downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DualBrowserActivity.this, DownloadActivity.class);
                mWebPopUpIncognito.dismiss();
                startActivity(intent);
            }
        });

        addToBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mWebPopUpIncognito.dismiss();

                url = webViewDualIncognito.getUrl();
                title = webViewDualIncognito.getTitle();
                String urlToHost;

                try {
                    URL url = new URL(webViewDualIncognito.getUrl());
                    urlToHost = url.getHost();

                    if (databaseClass.searchBookmarkRecord(url.toString()).equals("Empty")) {
                        databaseClass.insertBookmarkRecord(webViewDualIncognito.getUrl(), urlToHost, true);
                        Toast.makeText(DualBrowserActivity.this, "Bookmark Saved", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(DualBrowserActivity.this, "Bookmark Already Exists", Toast.LENGTH_SHORT).show();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        TextView exit = view.findViewById(R.id.exit_btn_incognito);
        TextView tv_show_bookmarks = view.findViewById(R.id.tv_show_bookmark_incognito);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mWebPopUpIncognito.dismiss();

                finishAffinity();
            }
        });

        tv_show_bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savedUrlIncognito = searchEditTextDualBrowser.getText().toString();

                Intent intent = new Intent(DualBrowserActivity.this, BookmarksActivity.class);
                intent.putExtra("browser", "dual_incognito");

                mWebPopUpIncognito.dismiss();

                intent.putExtra("saved_url_browser", savedUrlBrowser);
                intent.putExtra("saved_url_incognito", savedUrlIncognito);
                intent.putExtra("activity", String.valueOf(this));

                startActivity(intent);
            }
        });

        mWebPopUpIncognito.setAnimationStyle(R.style.TopPopAnim);
        mWebPopUpIncognito.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mWebPopUpIncognito.setFocusable(true);
        mWebPopUpIncognito.setOutsideTouchable(false);
    }

    boolean isFullscreenIncognito = false;

    private void fullScreenIncognito() {

        if (interstitialAd.isLoaded() && interstitialAd != null) {
            interstitialAd.show();
        } else {
            isFullscreenIncognito = true;
            initUi2();

            paramIncognito = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    2.0f
            );
            linearLayoutDualIncognito.setLayoutParams(paramIncognito);

            dualConstraint.setVisibility(View.GONE);
            dualProgressBar.setVisibility(View.GONE);
            if (linearLayoutDual.getVisibility() == View.VISIBLE) {
                linearLayoutDual.setVisibility(View.GONE);
            } else {
                linearLayoutIncognito.setVisibility(View.VISIBLE);
            }
            reqNewInterstitial();
        }
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                reqNewInterstitial();
                isFullscreenIncognito = true;
                initUi2();

                paramIncognito = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        2.0f
                );
                linearLayoutDualIncognito.setLayoutParams(paramIncognito);

                dualConstraint.setVisibility(View.GONE);
                dualProgressBar.setVisibility(View.GONE);
                if (linearLayoutDual.getVisibility() == View.VISIBLE) {
                    linearLayoutDual.setVisibility(View.GONE);
                } else {
                    linearLayoutIncognito.setVisibility(View.VISIBLE);
                }
            }
        });


    }

    private void exitFullscreenIncognito() {

        isFullscreenIncognito = false;

        paramIncognito = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                0,
                1.0f
        );
        linearLayoutDualIncognito.setLayoutParams(paramIncognito);

        dualConstraint.setVisibility(View.VISIBLE);
        dualProgressBar.setVisibility(View.VISIBLE);
        if (linearLayoutDual.getVisibility() == View.GONE) {
            linearLayoutDual.setVisibility(View.VISIBLE);
        } else {
            linearLayoutIncognito.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh() {
        if (swipeDual.isRefreshing()) {
            swipeDual.setRefreshing(true);
            ReloadWebView(webViewDualBrowser.getUrl());
        } else {
            swipeIncognito.setRefreshing(true);
            ReloadWebViewIncognito(webViewDualIncognito.getUrl());
        }
    }

    private void ReloadWebViewIncognito(String url) {

        webViewDualIncognito.loadUrl(url);
    }

    private void ReloadWebView(String url) {

        webViewDualBrowser.loadUrl(url);
    }

    /**
     * Method for initializing and loading the interstitial ad
     */
    public void reqNewInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.Interstitial));
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }
}
