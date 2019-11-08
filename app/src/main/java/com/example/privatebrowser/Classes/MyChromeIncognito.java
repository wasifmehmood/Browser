package com.example.privatebrowser.Classes;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.privatebrowser.DatabaseOperation.DatabaseClass;
import com.example.privatebrowser.R;

import androidx.constraintlayout.widget.ConstraintLayout;

public class MyChromeIncognito extends WebChromeClient {

    private View mCustomView;
    private WebChromeClient.CustomViewCallback mCustomViewCallback;
    protected FrameLayout mFullscreenContainer;
    private int mOriginalOrientation;
    private int mOriginalSystemUiVisibility;

    String url;
    WebView webView;
    Context context;
    Activity activity;
    ProgressBar progressBar;
    ConstraintLayout layoutProgressBar;

    private EditText toolbarEditText, incognitoEditText;
    private ImageView imageView;
    DatabaseClass databaseClass;

    MyChromeIncognito(String url, WebView webView, final Context context, Activity activity, ProgressBar progressBar, ConstraintLayout layoutProgressBar) {
        this.url = url;
        this.webView = webView;
        this.context = context;
        this.activity = activity;
        this.progressBar = progressBar;
        this.layoutProgressBar = layoutProgressBar;

        databaseClass = new DatabaseClass(context);
        toolbarEditText = activity.findViewById(R.id.toolbar_et);
        incognitoEditText = activity.findViewById(R.id.et_search_dual_incognito);
        imageView = activity.findViewById(R.id.bitmap);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);

        if (progressBar.getId() == R.id.progress_bar) {
            toolbarEditText.setText(view.getUrl());

            imageView.setVisibility(View.GONE);
        } else if (progressBar.getId() == R.id.progress_bar_dual_incognito) {
            incognitoEditText.setText(view.getUrl());
        }
        if (newProgress < 100 && progressBar.getVisibility() == ProgressBar.GONE) {
            progressBar.setVisibility(ProgressBar.VISIBLE);
            if (layoutProgressBar != null) {
                layoutProgressBar.setVisibility(View.VISIBLE);
            }
        }

        progressBar.setProgress(newProgress);
        if (newProgress == 100) {
            progressBar.setVisibility(ProgressBar.GONE);
            if (layoutProgressBar != null) {
                layoutProgressBar.setVisibility(View.GONE);
            }
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


    }
}
