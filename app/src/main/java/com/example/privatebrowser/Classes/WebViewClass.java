package com.example.privatebrowser.Classes;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.URLUtil;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.privatebrowser.IncognitoActivities.SearchActivity;
import com.example.privatebrowser.R;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class WebViewClass {

    public WebViewClass() {

    }

    /**
     * webView is set as in an Incognito mode, History is not saved, cookies are cleared and cache is cleared
     */
    public void setWebView(WebView webView) {

//        Make sure no caching is done
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);
        webView.clearCache(true);

//        Make sure No cookies are created
        CookieManager.getInstance().removeAllCookies(null);
        CookieManager.getInstance().removeSessionCookies(null);

//        Make sure no autoFill for Forms/ user-name password happens for the app
        webView.getSettings().setSavePassword(false);
        webView.getSettings().setSaveFormData(false);
    }

    /**
     * Method for starting a webView through a url, it shows a progress dialog until a page is fully loaded
     *
     * @param url has the url received from previous activity
     */
    private EditText toolbarEditText;
    private ImageView imageView;

    public void startWebView(String url, WebView webView, final Context context, Activity activity,
                             ProgressBar progressBar, SwipeRefreshLayout swipe) {

        //Create new webView Client to show progress dialog
        //When opening a url or click on link
        toolbarEditText = activity.findViewById(R.id.toolbar_et);
        imageView = activity.findViewById(R.id.bitmap);

        if (URLUtil.isNetworkUrl(url)) {

            webView.setWebChromeClient(new MyChromeIncognito(url, webView, context, activity, progressBar, layoutProgressBar));

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

                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);

                    swipe.setRefreshing(false);
                }
            });


//            webView.setWebViewClient(new WebViewClient() {
//                ProgressBar progressBar;
//                ProgressDialog progressDialog;
//
//
//                //If you will not use this method url links are open in new browser not in webView
//                //Show loader on url load
//                public boolean shouldOverrideUrlLoading(WebView view, String url) {
//
//                    imageView.setVisibility(View.GONE);
//                    toolbarEditText.setText(url);
//
//                    if (progressDialog == null) {
//                        // in standard case YourActivity.this
//                        progressBar = new ProgressBar(context);
//
//                        progressDialog = new ProgressDialog(context);
//                        progressDialog.setMessage("Loading...");
//                        try {
//                            progressDialog.show();
//                        }catch (Exception e)
//                        {
//                            e.printStackTrace();
//                        }
//
//                    }
//                    return false;
//                }
//
//                @Override
//                public void onPageStarted(WebView view, String url, Bitmap favicon) {
//                    super.onPageStarted(view, url, favicon);
//
//                    if(favicon != null) {
//                        Drawable d = new BitmapDrawable(activity.getResources(),
//                                Bitmap.createScaledBitmap(favicon, 50, 50, true));
//
////                    imageView.setImageBitmap(icon);
//                        imageView.setImageDrawable(d);
//
//                        toolbarEditText.setCompoundDrawables(d, null, null, null);
//                        toolbarEditText.setCompoundDrawablePadding(10);
//                    }
//                }
//
//                public void onPageFinished(WebView view, String url) {
//
//                    try {
//                        if (progressDialog.isShowing()) {
//                            progressDialog.dismiss();
//                            progressDialog = null;
//                        }
//                    } catch (Exception exception) {
//                        exception.printStackTrace();
//                    }
//                }
//            });

            // Javascript enabled on webView
            webView.getSettings().setJavaScriptEnabled(true);

            webView.loadUrl(url);

            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().removeSessionCookies(null);
            webView.clearFormData();
            webView.clearCache(true);
            webView.clearHistory();
        }
    }

    private ConstraintLayout layoutProgressBar;

    public void hideProgressBar(ConstraintLayout layoutProgressBar) {
        this.layoutProgressBar = layoutProgressBar;
    }

}
