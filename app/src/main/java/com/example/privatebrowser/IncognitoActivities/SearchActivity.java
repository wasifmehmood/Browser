package com.example.privatebrowser.IncognitoActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.privatebrowser.Adapters.SearchBookmarksAdapter;
import com.example.privatebrowser.BrowserActivities.BrowserSearchActivity;
import com.example.privatebrowser.Classes.ChangeLanguage;
import com.example.privatebrowser.CustomWidgets.CustomEditText;
import com.example.privatebrowser.DatabaseOperation.DatabaseClass;
import com.example.privatebrowser.HomeActivity;
import com.example.privatebrowser.Interfaces.DrawableClickListener;
import com.example.privatebrowser.Interfaces.FeaturesInterface;
import com.example.privatebrowser.R;
import com.example.privatebrowser.Vpn.VpnActivity;
import com.example.privatebrowser.utils.Utils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener,
        FeaturesInterface, SearchBookmarksAdapter.SearchBookmarkButtonAdapterListener {

    private Toolbar toolbar;
    private CustomEditText editText;
    //    private SearchBookmarksAdapter searchBookmarksAdapter;
    private ArrayList<String> bookmarkName;
    private ArrayList<String> bookmarkUrlName;
    //    private FloatingActionButton addBookmarkBtn;
//    RecyclerView mRecyclerView;
    private DatabaseClass databaseClass;
    private EditText bookmarkNameEt;
    private EditText bookmarkUrlEt;
    private String bookmarkNameStr;
    private String bookmarkUrlStr;
    ChangeLanguage changeLanguage;

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguage = new ChangeLanguage(this);
        changeLanguage.loadLocale();
        setContentView(R.layout.activity_search);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        databaseClass = new DatabaseClass(this);

        inItUI();

        setCustomActionBar();

        registerListeners();


    }

    @Override
    protected void onStart() {
        super.onStart();

        getBookmarksFromSQLite();

//        setRecyclerView();

        searchEditTextClickListener();
    }

    private void searchEditTextClickListener() {

        editText.setDrawableClickListener(new DrawableClickListener() {

            public void onClick(DrawableClickListener.DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        //Do something here
                        if (isNetworkAvailable()) {

                            String url;

                            Intent intent = new Intent(SearchActivity.this, MainActivity.class);

                            if(URLUtil.isValidUrl(editText.getText().toString()) && checkDomain())
                            {
                                url = editText.getText().toString();
                                Toast.makeText(SearchActivity.this, "empty", Toast.LENGTH_SHORT).show();
                            }
//                            else if(URLUtil.isValidUrl("https://"+searchEditText.getText().toString()) && bool)
//                            {
//                                url = "https://"+searchEditText.getText().toString();
//                                Toast.makeText(BrowserSearchActivity.this, "https", Toast.LENGTH_SHORT).show();
//
//                            }
                            else if (URLUtil.isValidUrl("http://"+editText.getText().toString()) && checkDomain())
                            {
                                url = "http://"+editText.getText().toString();
                                Toast.makeText(SearchActivity.this, "http", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                url = "https://www.google.com/#q=" + editText.getText().toString();
                            }

//                            String url = "https://www.google.com/#q=" + editText.getText().toString();
                            intent.putExtra("url", url);
                            intent.putExtra("browser", "incognito");
                            startActivity(intent);
                        } else {
                            Toast.makeText(SearchActivity.this, "Internet Not connected", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        break;
                }
            }

        });
    }

    /**
     * This checks whether the url has a top level domain, if yes returns true.
     *
     * @return
     */

    private boolean checkDomain() {

        ArrayList<String> checkUrl = new ArrayList<>();
        checkUrl.add(".com");
        checkUrl.add(".net");
        checkUrl.add(".org");
        checkUrl.add(".edu");
        checkUrl.add(".int");
        checkUrl.add(".gov");
        checkUrl.add(".mil");
        boolean checkDomain = false;

        for (int i=0; i<checkUrl.size(); i++) {
            checkDomain = editText.getText().toString().contains(checkUrl.get(i));

            if (checkDomain)
            {
                break;
            }
        }
        return checkDomain;
    }

//    private void setRecyclerView() {
//
//        mRecyclerView.setHasFixedSize(true);
//
//        mRecyclerView.setAdapter(searchBookmarksAdapter);
//
//        setListsInRecyclerAdapter();
//
//    }

//    void setListsInRecyclerAdapter() {
//        searchBookmarksAdapter.setSearchBookmarksUrlList(bookmarkUrlName);
//        searchBookmarksAdapter.setSearchBookmarksNameList(bookmarkName);
//    }


    /**
     * Retrieve data from database where value of bookmark is true it adds that bookmark to
     * list.
     */


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

    /**
     * This method registers keyListeners with the widgets on user-interface
     */
    private void registerListeners() {

        editText.setOnKeyListener(this);
        editText.setOnClickListener(this);
//        addBookmarkBtn.setOnClickListener(this);
    }

    /**
     * Toolbar widget is set as an ActionBar
     */
    private void setCustomActionBar() {

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }


    /**
     * Widgets/Variables are initialized here
     */
    private void inItUI() {

//        mRecyclerView = findViewById(R.id.search_recycler_view);
//        searchBookmarksAdapter = new SearchBookmarksAdapter(this, this);
        toolbar = findViewById(R.id.custom_action_bar);
        editText = findViewById(R.id.et_search);
//        addBookmarkBtn = findViewById(R.id.add_bookmark_btn);
    }

    /**
     * Override method for creating/inflating an Option Menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_options_menu, menu);
        return true;
    }


    /**
     * Override method for what happens when an item in option menu is selected
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.bookmarks:
                showBookmarks();
                return true;
            case R.id.item_home:
                startHomeActivity();
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

    private void startHomeActivity() {

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }


    private void startVpn() {

        Intent intent = new Intent(this, VpnActivity.class);
        startActivity(intent);
    }


    /**
     * Override method for what happens when a widget is clicked. When button is clicked another
     * activity of webView is started with passing the entered url to the webView
     */
    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.add_bookmark_btn) {
//            addBookmark();
        }
    }

//    private void addBookmark() {
//
//        customDialog();
//    }
//
//    private void customDialog() {
//
//        // custom dialog
//        final Dialog dialog = new Dialog(this);
//        dialog.setContentView(R.layout.custom_dialog);
//        dialog.setTitle("Title...");
//
//
//        // set the custom dialog components - text, image and button
//        bookmarkNameEt = dialog.findViewById(R.id.bookmarkNameEt);
//        bookmarkUrlEt = dialog.findViewById(R.id.bookmarkUrlEt);
//
//
//        Button addBookmarkBtn = dialog.findViewById(R.id.addBookmarkBtn);
//
//        addBookmarkBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                bookmarkNameStr = bookmarkNameEt.getText().toString();
//                bookmarkUrlStr = bookmarkUrlEt.getText().toString();
//
//
//                if (databaseClass.searchBookmarkRecord(bookmarkUrlStr).equals("Empty")) {
//
//                    if (!(bookmarkNameStr.equals("") && bookmarkUrlStr.equals(""))) {
//
//                        if (bookmarkUrlStr.contains("https://")) {
//                            databaseClass.insertBookmarkRecord(bookmarkUrlStr, bookmarkNameStr, true);
//                        } else if (!bookmarkUrlStr.contains("https://")) {
//                            databaseClass.insertBookmarkRecord("https://" + bookmarkUrlStr, bookmarkNameStr, true);
//                        }
//
//                    } else {
//                        Toast.makeText(SearchActivity.this, "Enter the fields", Toast.LENGTH_SHORT).show();
//                    }
//                    getBookmarksFromSQLite();
////                    setListsInRecyclerAdapter();
////                    searchBookmarksAdapter.notifyDataSetChanged();
//                }
//
//                dialog.dismiss();
//            }
//        });
//
//        dialog.show();
//    }

    /**
     * Override method for what happens when user touches on the screen
     *
     * @param event Stores the value where the screen is touched
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {

//        toolbar.animate().translationY(0).
//                setInterpolator(new DecelerateInterpolator()).start();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        editText.clearFocus();
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

                String url;

                Intent intent = new Intent(SearchActivity.this, MainActivity.class);

                if(URLUtil.isValidUrl(editText.getText().toString()) && checkDomain())
                {
                    url = editText.getText().toString();
                    Toast.makeText(SearchActivity.this, "empty", Toast.LENGTH_SHORT).show();
                }
//                            else if(URLUtil.isValidUrl("https://"+searchEditText.getText().toString()) && bool)
//                            {
//                                url = "https://"+searchEditText.getText().toString();
//                                Toast.makeText(BrowserSearchActivity.this, "https", Toast.LENGTH_SHORT).show();
//
//                            }
                else if (URLUtil.isValidUrl("http://"+editText.getText().toString()) && checkDomain())
                {
                    url = "http://"+editText.getText().toString();
                    Toast.makeText(SearchActivity.this, "http", Toast.LENGTH_SHORT).show();
                }
                else {
                    url = "https://www.google.com/#q=" + editText.getText().toString();
                }

//                String url = "https://www.google.com/#q=" + editText.getText().toString();
                intent.putExtra("url", url);
                intent.putExtra("browser", "incognito");
                startActivity(intent);
            } else {
                Toast.makeText(this, "Internet Not Connected", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return false;
    }

    @Override
    public void showBookmarks() {

        Intent intent = new Intent(this, BookmarksActivity.class);
        intent.putExtra("browser", "incognito");
        startActivity(intent);
    }

    @Override
    public void showDownloads() {

        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
    }

    @Override
    public void btnOnClick(View v, int position) {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("url", bookmarkUrlName.get(position));
        intent.putExtra("browser", "incognito");
        startActivity(intent);
    }
}
