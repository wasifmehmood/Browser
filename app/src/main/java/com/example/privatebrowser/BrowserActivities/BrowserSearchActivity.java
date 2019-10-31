package com.example.privatebrowser.BrowserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.privatebrowser.CustomWidgets.CustomEditText;
import com.example.privatebrowser.IncognitoActivities.MainActivity;
import com.example.privatebrowser.IncognitoActivities.SearchActivity;
import com.example.privatebrowser.IncognitoActivities.StartActivity;
import com.example.privatebrowser.Interfaces.DrawableClickListener;
import com.example.privatebrowser.R;
import com.google.android.material.bottomappbar.BottomAppBar;

import java.util.Objects;

public class BrowserSearchActivity extends AppCompatActivity implements View.OnKeyListener{

    CustomEditText searchEditText;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser_search);

        inItUi();

        setCustomActionBar();

        searchEditTextClickListener();
    }
    void inItUi(){

        toolbar = findViewById(R.id.custom_action_bar);
        searchEditText = findViewById(R.id.et_browser_search);

    }
    private void setCustomActionBar() {

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }

    private void searchEditTextClickListener() {

        searchEditText.setDrawableClickListener(new DrawableClickListener() {

            public void onClick(DrawableClickListener.DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        //Do something here
                        if (isNetworkAvailable()) {
                            Intent intent = new Intent(BrowserSearchActivity.this, MainActivity.class);
                            String url = "https://www.google.com/#q=" + searchEditText.getText().toString();
                            intent.putExtra("url", url);
                            intent.putExtra("browser","main");
                            startActivity(intent);
                        } else {
                            Toast.makeText(BrowserSearchActivity.this, "Internet Not connected", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        break;
                }
            }

        });
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // If the event is a key-down event on the "enter" button
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            // Perform action on key press
            if (isNetworkAvailable()) {
                Intent intent = new Intent(BrowserSearchActivity.this, MainActivity.class);
                String url = "https://www.google.com/#q=" + searchEditText.getText().toString();
                intent.putExtra("url", url);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Internet Not Connected", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return false;
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.browser_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.incognito:
                startIncognitoBrowser();
                return true;
            case R.id.bookmarks:
                showBookmarks();
                return true;
            case R.id.history:
                showHistory();
                return true;
            case R.id.downloads:
                showDownloads();
                return true;
            case R.id.exit:
                System.exit(0);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    private void showBookmarks() {
    }

    private void showHistory() {
    }

    private void showDownloads() {
    }

    private void startIncognitoBrowser() {

        Intent intent = new Intent(this, StartActivity.class);
        startActivity(intent);
    }
}
