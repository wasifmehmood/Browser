package com.example.privatebrowser.Classes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebHistoryItem;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class BrowserWebViewClass {


//    /**
//     * webView is set as in an Incognito mode, History is not saved, cookies are cleared and cache is cleared
//     */
//    public void setBrowserWebView(WebView webView) {
//
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
//    }

    /**
     * Method for starting a webView through a url, it shows a progress dialog until a page is fully loaded
     *
     * @param url has the url received from previous activity
     */

    public void startBrowserWebView(String url, WebView webView, final Context context, Activity activity) {

        //Create new webView Client to show progress dialog
        //When opening a url or click on link

        Toast.makeText(context, "u222rl", Toast.LENGTH_SHORT).show();
        if (URLUtil.isNetworkUrl(url)) {
            webView.setWebViewClient(new WebViewClient() {
                ProgressBar progressBar;
                ProgressDialog progressDialog;

                //If you will not use this method url links are open in new browser not in webView
                //Show loader on url load
                public boolean shouldOverrideUrlLoading(WebView view, String url) {

                    if (progressDialog == null) {
                        // in standard case YourActivity.this
                        progressBar = new ProgressBar(context);

                        progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Loading...");
                        try {
                            progressDialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                    return false;
                }

                public void onPageFinished(WebView view, String url) {

                    try {
                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            });

            // Javascript enabled on webView
            webView.getSettings().setJavaScriptEnabled(true);

            webView.loadUrl(url);

        }
    }
}
