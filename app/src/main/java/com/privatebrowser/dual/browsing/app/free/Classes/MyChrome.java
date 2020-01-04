package com.privatebrowser.dual.browsing.app.free.Classes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.webkit.WebBackForwardList;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.privatebrowser.dual.browsing.app.free.DatabaseOperation.DatabaseClass;
import com.privatebrowser.dual.browsing.app.free.R;

public class MyChrome extends WebChromeClient {

    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    protected FrameLayout mFullscreenContainer;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;

    private String url;
    private WebView webView;
    private Context context;
    private Activity activity;
    private ProgressBar progressBar;

    private EditText toolbarEditText, dualEditText;
    private ImageView imageView;
    private DatabaseClass databaseClass;

    MyChrome(String url, WebView webView, final Context context, Activity activity, ProgressBar progressBar)
    {
        this.url = url;
        this.webView = webView;
        this.context = context;
        this.activity = activity;
        this.progressBar = progressBar;

        databaseClass = new DatabaseClass(context);

        toolbarEditText = activity.findViewById(R.id.toolbar_et);
        imageView = activity.findViewById(R.id.bitmap);
        dualEditText = activity.findViewById(R.id.et_search_dual_browser);

    }


    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);

        if (progressBar.getId() == R.id.progress_bar) {
            toolbarEditText.setText(view.getUrl());

            imageView.setVisibility(View.GONE);
        }
        else if(progressBar.getId() == R.id.progress_bar_dual)
        {
            dualEditText.setText(view.getUrl());
        }

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
        if (progressBar.getId() == R.id.progress_bar) {

            Drawable d = new BitmapDrawable(activity.getResources(),
                    Bitmap.createScaledBitmap(icon, 50, 50, true));

            imageView.setImageDrawable(d);

            toolbarEditText.setCompoundDrawables(d, null, null, null);
            toolbarEditText.setCompoundDrawablePadding(10);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        super.onReceivedTitle(view, title);


//                    this.title = title;
//                    Toast.makeText(context, "Title: " + title, Toast.LENGTH_SHORT).show();

        String historyTitle = "";
        String historyUrl;

        WebBackForwardList mWebBackForwardList = webView.copyBackForwardList();
        if (mWebBackForwardList.getCurrentIndex() >= 0)

            historyTitle = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()).getTitle();

        historyUrl = mWebBackForwardList.getItemAtIndex(mWebBackForwardList.getCurrentIndex()).getUrl();


//                    Log.d("tag", " Title: " + historyTitle + " Url: " + historyUrl);

        if (!historyTitle.equals("Google")) {
            databaseClass.insertHistoryRecord(historyUrl, historyTitle, true);
        }
    }


    public Bitmap getDefaultVideoPoster() {
        if (mCustomView == null) {
            return null;
        }
        return BitmapFactory.decodeResource(activity.getApplicationContext().getResources(), 2130837573);
    }

    public void onHideCustomView() {
        ((FrameLayout) activity.getWindow().getDecorView()).removeView(this.mCustomView);
        this.mCustomView = null;
        activity.getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
        activity.setRequestedOrientation(this.mOriginalOrientation);
        this.mCustomViewCallback.onCustomViewHidden();
        this.mCustomViewCallback = null;
    }

    public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
        if (this.mCustomView != null) {
            onHideCustomView();
            return;
        }
        this.mCustomView = paramView;
        this.mOriginalSystemUiVisibility = activity.getWindow().getDecorView().getSystemUiVisibility();
        this.mOriginalOrientation = activity.getRequestedOrientation();
        this.mCustomViewCallback = paramCustomViewCallback;
        ((FrameLayout) activity.getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
        activity.getWindow().getDecorView().setSystemUiVisibility(3846);
    }
}
