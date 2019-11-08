package com.example.privatebrowser.Classes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.privatebrowser.DatabaseOperation.DatabaseClass;
import com.example.privatebrowser.R;

public class BrowserWebViewClass {


    DatabaseClass databaseClass;

    public BrowserWebViewClass(Context context) {
        databaseClass = new DatabaseClass(context);
    }

    //    /**
//     * webView is set as in an Incognito mode, History is not saved, cookies are cleared and cache is cleared
//     */
    public void setBrowserWebView(WebView webView) {


        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setGeolocationEnabled(true);
        webSettings.setSupportMultipleWindows(true); // This forces ChromeClient enabled.
        webSettings.setAllowFileAccess(true);

////        Make sure no caching is done
//        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//        webView.getSettings().setAppCacheEnabled(false);
//        webView.clearCache(true);
//
////        Make sure No cookies are created
//        CookieManager.getInstance().removeAllCookies(null);
//        CookieManager.getInstance().removeSessionCookies(null);
//
////        Make sure no autoFill for Forms/ user-name password happens for the app
//        webView.getSettings().setSavePassword(false);
//        webView.getSettings().setSaveFormData(false);
    }

    /**
     * Method for starting a webView through a url, it shows a progress dialog until a page is fully loaded
     *
     * @param url has the url received from previous activity
     */
    private EditText toolbarEditText;
    private ImageView imageView;

    public void startBrowserWebView(String url, WebView webView, final Context context,
                                    Activity activity, ProgressBar progressBar) {

        //Create new webView Client to show progress dialog
        //When opening a url or click on link




        if (URLUtil.isNetworkUrl(url)) {

            webView.setWebChromeClient(new MyChrome(url, webView, context, activity, progressBar));

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

//                    view.loadUrl(url);
                    return false;
                }

                @Override
                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    super.onPageStarted(view, url, favicon);

                }
            });


//            webView.setWebViewClient(new WebViewClient() {
//
//
//                //If you will not use this method url links are open in new browser not in webView
//                //Show loader on url load
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//                    if (progressDialog == null) {
//                        // in standard case YourActivity.this
//                        progressBar = new ProgressBar(context);
//
//                        progressDialog = new ProgressDialog(context);
//                        progressDialog.setMessage("Loading...");
//
//
//                        try {
//                            progressDialog.show();
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                    return false;
//                }
//
//                @Override
//                public void onPageStarted(WebView view, String url, Bitmap favicon) {
//
//                    String historyUrl = "";
//                    String historyUrl2;
//                    Bitmap bitmap;
//                    WebBackForwardList mWebBackForwardList = webView.copyBackForwardList();
//                    if (mWebBackForwardList.getCurrentIndex() >= 0)
//
//                        historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()).getTitle();
//
//                        historyUrl2 = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()).getUrl();
//                        bitmap = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()).getFavicon();
//
//                        imageView.setImageBitmap(bitmap);
//                    Toast.makeText(context, "history: " + historyUrl2+" Title: "+historyUrl, Toast.LENGTH_SHORT).show();
//                }
//                public void onPageFinished(WebView view, String url) {
//
//                    try {
//                        if (progressDialog.isShowing()) {
//                            progressDialog.dismiss();
//                            progressDialog = null;
//
//                        }
//                    } catch (Exception exception) {
//                        exception.printStackTrace();
//                    }
//                }
//            });

            // Javascript enabled on webView
            webView.getSettings().setJavaScriptEnabled(true);

            webView.loadUrl(url);

        }
    }

}
