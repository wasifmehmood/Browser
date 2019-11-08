package com.example.privatebrowser.DualBrowserActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.privatebrowser.BrowserActivities.HistoryActivity;
import com.example.privatebrowser.Classes.BrowserWebViewClass;
import com.example.privatebrowser.Classes.ChangeLanguage;
import com.example.privatebrowser.Classes.WebViewClass;
import com.example.privatebrowser.CustomWidgets.CustomEditText;
import com.example.privatebrowser.DatabaseOperation.DatabaseClass;
import com.example.privatebrowser.IncognitoActivities.BookmarksActivity;
import com.example.privatebrowser.IncognitoActivities.DownloadActivity;
import com.example.privatebrowser.Interfaces.DrawableClickListener;
import com.example.privatebrowser.R;

import java.net.MalformedURLException;
import java.net.URL;

public class DualBrowserActivity extends AppCompatActivity implements View.OnClickListener {

    WebView webViewDualBrowser, webViewDualIncognito;
    ImageView actionBackDual, actionForwardDual, optionMenuDualBrowser, optionMenuDualIncognito,
            actionBackDualIncognito, actionForwardDualIncognito;
    CustomEditText searchEditTextDualBrowser, searchEditTextDualIncognito;
    private PopupWindow mWebPopUpBrowser, mWebPopUpIncognito;
    BrowserWebViewClass browserWebViewClass;
    WebViewClass incognitoWebViewClass;
    ProgressBar progressBarDual, progressBarIncognito;
    ChangeLanguage changeLanguage;
    ImageView imageViewIncognitoDual;
    TextView textViewGoneIncognito;
    ConstraintLayout layoutProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        changeLanguage = new ChangeLanguage();
//        changeLanguage.setAppLocale("ru", this);
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

//        Drawable image = this.getResources().getDrawable( R.drawable.search );
//        int h = image.getIntrinsicHeight();
//        int w = image.getIntrinsicWidth();
//        image.setBounds( 0, 0, w, h );
//        button.setCompoundDrawables( image, null, null, null );
//
//        Bitmap searchIcon = BitmapFactory.decodeResource(getResources(), R.drawable.search);
//
//        Drawable d = new BitmapDrawable(this.getResources(),
//                Bitmap.createScaledBitmap(searchIcon, 45, 45, true));
//
//        searchEditTextDualBrowser.setCompoundDrawables(null,null, d, null);
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
    }

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
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    private void searchEditTextClickListener() {

        searchEditTextDualBrowser.setDrawableClickListener(new DrawableClickListener() {

            public void onClick(DrawableClickListener.DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        //Do something here
                        if (isNetworkAvailable()) {

                            webViewDualBrowser.setVisibility(View.VISIBLE);
                            progressBarDual.setVisibility(View.VISIBLE);

                            String url = "https://www.google.com/#q=" + searchEditTextDualBrowser.getText().toString();

                            browserWebViewClass.setBrowserWebView(webViewDualBrowser);
                            browserWebViewClass.startBrowserWebView(url, webViewDualBrowser,
                                    DualBrowserActivity.this, DualBrowserActivity.this, progressBarDual);

//                            Intent intent = new Intent(DualBrowserActivity.this, MainActivity.class);
//                            String url = "https://www.google.com/#q=" + searchEditTextDualBrowser.getText().toString();
//                            intent.putExtra("url", url);
//                            intent.putExtra("browser", "incognito");
//                            startActivity(intent);
                        } else {
                            Toast.makeText(DualBrowserActivity.this, "Internet Not connected", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        break;
                }
            }

        });
    }



    private void searchEditTextIncognitoClickListener() {

        searchEditTextDualIncognito.setDrawableClickListener(new DrawableClickListener() {

            public void onClick(DrawableClickListener.DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        //Do something here
                        if (isNetworkAvailable()) {

                            imageViewIncognitoDual.setVisibility(View.GONE);
                            webViewDualIncognito.setVisibility(View.VISIBLE);
                            layoutProgressBar.setVisibility(View.VISIBLE);
                            textViewGoneIncognito.setVisibility(View.GONE);

                            String url = "https://www.google.com/#q=" + searchEditTextDualIncognito.getText().toString();

                            incognitoWebViewClass.hideProgressBar(layoutProgressBar);
                            incognitoWebViewClass.setWebView(webViewDualIncognito);
                            incognitoWebViewClass.startWebView(url, webViewDualIncognito,
                                    DualBrowserActivity.this, DualBrowserActivity.this, progressBarIncognito);


//                            Intent intent = new Intent(DualBrowserActivity.this, MainActivity.class);
//                            String url = "https://www.google.com/#q=" + searchEditTextDualBrowser.getText().toString();
//                            intent.putExtra("url", url);
//                            intent.putExtra("browser", "incognito");
//                            startActivity(intent);
                        } else {
                            Toast.makeText(DualBrowserActivity.this, "Internet Not connected", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        break;
                }
            }

        });
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
        }
    }

    private String url;
    private String title;
    private DatabaseClass databaseClass;

    private void inItWebPop() {
        View view = LayoutInflater.from(this).inflate(R.layout.web_popup_menu, null);
        mWebPopUpBrowser = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //final TextView setting = alert.findViewById(R.id.setting_btn);

        TextView addToBookmark = view.findViewById(R.id.add_to_book);
        TextView downloads = view.findViewById(R.id.downloads_dual_browser);
        TextView history = view.findViewById(R.id.history_dual_browser);

        downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DualBrowserActivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DualBrowserActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        addToBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                finishAffinity();
            }
        });

        tv_show_bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DualBrowserActivity.this, BookmarksActivity.class);
                startActivity(intent);
            }
        });

        mWebPopUpBrowser.setAnimationStyle(R.style.TopPopAnim);
        mWebPopUpBrowser.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mWebPopUpBrowser.setFocusable(true);
        mWebPopUpBrowser.setOutsideTouchable(false);
    }

    private void inItWebPopIncognito() {
        View view = LayoutInflater.from(this).inflate(R.layout.web_popup_menu_incognito, null);
        mWebPopUpIncognito = new PopupWindow(view, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        //final TextView setting = alert.findViewById(R.id.setting_btn);

        TextView addToBookmark = view.findViewById(R.id.add_to_book_incognito);
        TextView downloads = view.findViewById(R.id.downloads_dual_incognito);

        downloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(DualBrowserActivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });

        addToBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
        TextView exit = view.findViewById(R.id.exit_btn_incognito);
        TextView tv_show_bookmarks = view.findViewById(R.id.tv_show_bookmark_incognito);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishAffinity();
            }
        });

        tv_show_bookmarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DualBrowserActivity.this, BookmarksActivity.class);
                startActivity(intent);
            }
        });

        mWebPopUpIncognito.setAnimationStyle(R.style.TopPopAnim);
        mWebPopUpIncognito.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mWebPopUpIncognito.setFocusable(true);
        mWebPopUpIncognito.setOutsideTouchable(false);
    }

}
