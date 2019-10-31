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

import com.example.privatebrowser.R;

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
    private EditText toolbarEditText;

    public void startBrowserWebView(String url, WebView webView, final Context context,
                                    Activity activity, ProgressBar progressBar) {

        //Create new webView Client to show progress dialog
        //When opening a url or click on link

        toolbarEditText = activity.findViewById(R.id.toolbar_et);
        imageView = activity.findViewById(R.id.bitmap);


//        Toast.makeText(context, "u222rl", Toast.LENGTH_SHORT).show();
        if (URLUtil.isNetworkUrl(url)) {


            ProgressDialog progressDialog;


//            WebSettings webSettings = webView.getSettings();
//            webSettings.setJavaScriptEnabled(true);
//            webSettings.setGeolocationEnabled(true);
            webView.getSettings().setSupportMultipleWindows(true); // This forces ChromeClient enabled.

            webView.setWebChromeClient(new WebChromeClient() {
//                ProgressBar progressBar;

                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    imageView.setVisibility(View.GONE);
                    if (newProgress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
                        progressBar.setVisibility(ProgressBar.VISIBLE);
                    }

                    progressBar.setProgress(newProgress);
                    if (newProgress == 100) {
                        progressBar.setVisibility(ProgressBar.GONE);

                    }

                }

                @Override
                public void onReceivedIcon(WebView view, Bitmap icon) {
                    super.onReceivedIcon(view, icon);

                    //Shrinking bitmap.
                    Drawable d = new BitmapDrawable(activity.getResources(),
                            Bitmap.createScaledBitmap(icon,50,50,true));

//                    imageView.setImageBitmap(icon);
                    imageView.setImageDrawable(d);

                    Toast.makeText(context, "WWW", Toast.LENGTH_SHORT).show();
                    toolbarEditText.setCompoundDrawables(d,null,null,null);
                    toolbarEditText.setCompoundDrawablePadding(10);

                }

                String title;

                @Override
                public void onReceivedTitle(WebView view, String title) {
                    super.onReceivedTitle(view, title);

                    toolbarEditText.setText(webView.getUrl());
                    this.title = title;
                    Toast.makeText(context, "Title: " + title, Toast.LENGTH_SHORT).show();

                    String historyUrl = "";
                    String historyUrl2;

                    WebBackForwardList mWebBackForwardList = webView.copyBackForwardList();
                    if (mWebBackForwardList.getCurrentIndex() >= 0)

                        historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()).getTitle();

                        historyUrl2 = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()).getUrl();

                    Toast.makeText(context, "history: " + historyUrl2 + " Title: " + historyUrl, Toast.LENGTH_SHORT).show();

                }
            });

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

    ImageView imageView;

}
