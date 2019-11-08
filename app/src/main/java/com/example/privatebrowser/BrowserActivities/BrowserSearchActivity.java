package com.example.privatebrowser.BrowserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.privatebrowser.Adapters.SearchBookmarksAdapter;
import com.example.privatebrowser.Classes.ChangeLanguage;
import com.example.privatebrowser.CustomWidgets.CustomEditText;
import com.example.privatebrowser.DatabaseOperation.DatabaseClass;
import com.example.privatebrowser.IncognitoActivities.BookmarksActivity;
import com.example.privatebrowser.IncognitoActivities.DownloadActivity;
import com.example.privatebrowser.IncognitoActivities.MainActivity;
import com.example.privatebrowser.IncognitoActivities.SearchActivity;
import com.example.privatebrowser.IncognitoActivities.StartActivity;
import com.example.privatebrowser.Interfaces.DrawableClickListener;
import com.example.privatebrowser.R;
import com.example.privatebrowser.Vpn.VpnActivity;
import com.example.privatebrowser.utils.Utils;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class BrowserSearchActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener,
        SearchBookmarksAdapter.SearchBookmarkButtonAdapterListener {

    CustomEditText searchEditText;
    Toolbar toolbar;
    private DatabaseClass databaseClass;
    private EditText bookmarkNameEt;
    private EditText bookmarkUrlEt;
    private String bookmarkNameStr;
    private String bookmarkUrlStr;
    RecyclerView mRecyclerView;
    private SearchBookmarksAdapter searchBookmarksAdapter;
    private FloatingActionButton addBookmarkBtn;
    ChangeLanguage changeLanguage;
    TextView textViewBookmark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguage = new ChangeLanguage(this);
        changeLanguage.loadLocale();
        setContentView(R.layout.activity_browser_search);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        databaseClass = new DatabaseClass(this);

        inItUi();

        setCustomActionBar();

        searchEditTextClickListener();

        registerListeners();

//        Bitmap searchIcon = BitmapFactory.decodeResource(getResources(), R.drawable.search);
//
//        Drawable d = new BitmapDrawable(this.getResources(),
//                Bitmap.createScaledBitmap(searchIcon, 34, 34, true));
//
//        searchEditText.setCompoundDrawables(null,null, d, null);
    }

    private void registerListeners() {

        searchEditText.setOnKeyListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getBookmarksFromSQLite();

        if(bookmarkName.isEmpty())
        {
            textViewBookmark.setVisibility(View.INVISIBLE);
        }
        else
        {
            textViewBookmark.setVisibility(View.VISIBLE);
        }

        setRecyclerView();

        searchEditTextClickListener();

        addBookmarkBtn.setOnClickListener(this);
    }


    void inItUi() {

        mRecyclerView = findViewById(R.id.search_recycler_view);
        searchBookmarksAdapter = new SearchBookmarksAdapter(this, this);
        toolbar = findViewById(R.id.custom_action_bar);
        searchEditText = findViewById(R.id.et_browser_search);
        addBookmarkBtn = findViewById(R.id.add_bookmark_btn);
        textViewBookmark = findViewById(R.id.text_bookmark);

    }

    private void setCustomActionBar() {

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }

    private void setRecyclerView() {

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(searchBookmarksAdapter);

        setListsInRecyclerAdapter();

    }

    void setListsInRecyclerAdapter() {

        searchBookmarksAdapter.setSearchBookmarksUrlList(bookmarkUrlName);
        searchBookmarksAdapter.setSearchBookmarksNameList(bookmarkName);
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
                            intent.putExtra("browser", "main");
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

    private void addBookmark() {

        customDialog();
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
                intent.putExtra("browser", "main");
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

    private ArrayList<String> bookmarkName;
    private ArrayList<String> bookmarkUrlName;

    private void getBookmarksFromSQLite() {

        bookmarkName = new ArrayList<>();
        bookmarkUrlName = new ArrayList<>();

        databaseClass.readAllBookmarkRecord();
        ArrayList<String> bookmarkUrl = Utils.nameUtils;
        ArrayList<String> bookmarkNames = Utils.bookmarkNameUtils;
        ArrayList<String> bookmarkBool = Utils.boolUtils;

        for (int i = 0; i < bookmarkNames.size(); i++) {
            if (bookmarkBool.get(i).equals("true")) {
                bookmarkUrlName.add(bookmarkUrl.get(i));
                bookmarkName.add(bookmarkNames.get(i));
            }
        }
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
            case R.id.menu_vpn:
                startVpn();
                return true;
            case R.id.exit:
                finishAffinity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startVpn() {

        Intent intent = new Intent(this, VpnActivity.class);
        startActivity(intent);
    }

    private void customDialog() {

        // custom dialog
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.setTitle("Title...");


        // set the custom dialog components - text, image and button
        bookmarkNameEt = dialog.findViewById(R.id.bookmarkNameEt);
        bookmarkUrlEt = dialog.findViewById(R.id.bookmarkUrlEt);


        Button addBookmarkBtn = dialog.findViewById(R.id.addBookmarkBtn);

        addBookmarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bookmarkNameStr = bookmarkNameEt.getText().toString();
                bookmarkUrlStr = bookmarkUrlEt.getText().toString();


                if (databaseClass.searchBookmarkRecord(bookmarkUrlStr).equals("Empty")) {

                    if (!(bookmarkNameStr.equals("") && bookmarkUrlStr.equals(""))) {

                        if (bookmarkUrlStr.contains("https://")) {
                            databaseClass.insertBookmarkRecord(bookmarkUrlStr, bookmarkNameStr, true);
                        } else if (!bookmarkUrlStr.contains("https://")) {
                            databaseClass.insertBookmarkRecord("https://" + bookmarkUrlStr, bookmarkNameStr, true);
                        }

                        textViewBookmark.setVisibility(View.VISIBLE);

                    } else {
                        Toast.makeText(BrowserSearchActivity.this, "Enter the fields", Toast.LENGTH_SHORT).show();
                    }
                    getBookmarksFromSQLite();
                    setListsInRecyclerAdapter();
                    searchBookmarksAdapter.notifyDataSetChanged();
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showBookmarks() {

        Intent intent = new Intent(this, BookmarksActivity.class);
        intent.putExtra("browser", "main");
        startActivity(intent);
    }

    private void showHistory() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    private void showDownloads() {

        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);

    }

    private void startIncognitoBrowser() {

        Intent intent = new Intent(this, SearchActivity.class);
        startActivity(intent);
    }

    @Override
    public void btnOnClick(View v, int position) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("url", bookmarkUrlName.get(position));
        intent.putExtra("browser", "main");
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.add_bookmark_btn) {
            addBookmark();
        }
    }
}
