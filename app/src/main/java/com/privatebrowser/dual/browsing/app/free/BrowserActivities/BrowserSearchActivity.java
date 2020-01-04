package com.privatebrowser.dual.browsing.app.free.BrowserActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

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
import android.view.View;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.privatebrowser.dual.browsing.app.free.Adapters.SearchBookmarksAdapter;
import com.privatebrowser.dual.browsing.app.free.Classes.ChangeLanguage;
import com.privatebrowser.dual.browsing.app.free.CustomWidgets.CustomEditText;
import com.privatebrowser.dual.browsing.app.free.DatabaseOperation.DatabaseClass;
import com.privatebrowser.dual.browsing.app.free.HomeActivity;
import com.privatebrowser.dual.browsing.app.free.IncognitoActivities.BookmarksActivity;
import com.privatebrowser.dual.browsing.app.free.IncognitoActivities.DownloadActivity;
import com.privatebrowser.dual.browsing.app.free.IncognitoActivities.MainActivity;
import com.privatebrowser.dual.browsing.app.free.IncognitoActivities.SearchActivity;
import com.privatebrowser.dual.browsing.app.free.Interfaces.DrawableClickListener;
import com.privatebrowser.dual.browsing.app.free.R;
import com.privatebrowser.dual.browsing.app.free.Vpn.VpnActivity;
import com.privatebrowser.dual.browsing.app.free.utils.Utils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Objects;


public class BrowserSearchActivity extends AppCompatActivity implements View.OnKeyListener, View.OnClickListener,
        SearchBookmarksAdapter.SearchBookmarkButtonAdapterListener {

    private CustomEditText searchEditText;
    private Toolbar toolbar;
    private DatabaseClass databaseClass;
    private EditText bookmarkNameEt;
    private EditText bookmarkUrlEt;
    private String bookmarkNameStr;
    private String bookmarkUrlStr;
    private RecyclerView mRecyclerView;
    private SearchBookmarksAdapter searchBookmarksAdapter;
    private FloatingActionButton addBookmarkBtn;
    private ChangeLanguage changeLanguage;
    private TextView textViewBookmark;
    private AdView adView;
    private InterstitialAd interstitialAd;
    private ArrayList<String> bookmarkName;
    private ArrayList<String> bookmarkUrlName;

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

        bannerAd();

        reqNewInterstitial();

        MobileAds.initialize(this,getResources().getString(R.string.app_id));

    }

    /**
     * This method registers all the listeners
     */

    private void registerListeners() {

        searchEditText.setOnKeyListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getBookmarksFromSQLite();

        if (bookmarkName.isEmpty()) {
            textViewBookmark.setVisibility(View.INVISIBLE);
        } else {
            textViewBookmark.setVisibility(View.VISIBLE);
        }

        setRecyclerView();

        searchEditTextClickListener();

        addBookmarkBtn.setOnClickListener(this);
    }


    /**
     * Here all the views are initialized
     */
    void inItUi() {

        mRecyclerView = findViewById(R.id.search_recycler_view);
        searchBookmarksAdapter = new SearchBookmarksAdapter(this, this);
        toolbar = findViewById(R.id.custom_action_bar);
        searchEditText = findViewById(R.id.et_browser_search);
        addBookmarkBtn = findViewById(R.id.add_bookmark_btn);
        textViewBookmark = findViewById(R.id.text_bookmark);

    }

    /**
     * Toolbar is set as a actionbar
     */

    private void setCustomActionBar() {

        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("");
    }

    /**
     * RecyclerView is set, its adapter and setting list in adapter method is called
     */

    private void setRecyclerView() {

        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setAdapter(searchBookmarksAdapter);

        setListsInRecyclerAdapter();

    }

    /**
     * This method set the lists in the adapter
     */

    void setListsInRecyclerAdapter() {

        searchBookmarksAdapter.setSearchBookmarksUrlList(bookmarkUrlName);
        searchBookmarksAdapter.setSearchBookmarksNameList(bookmarkName);
    }

    /**
     * Click Listener on the search drawable of the CUSTOM EDITTEXT. When the search
     * drawable is clicked first the internet connectivity is checked if true then
     * url is checked, if the url is a simple search on google it will be directed to
     * google search, if the url is proper url of a website it will be redirected to
     * the corresponding website. For checking if its a website url checkdomain method
     * is used.
     */
    private void searchEditTextClickListener() {

        searchEditText.setDrawableClickListener(new DrawableClickListener() {

            public void onClick(DrawableClickListener.DrawablePosition target) {
                switch (target) {
                    case RIGHT:
                        //Do something here
                        String url;

                        if (isNetworkAvailable()) {
                            Intent intent = new Intent(BrowserSearchActivity.this, MainActivity.class);
                            if (URLUtil.isValidUrl(searchEditText.getText().toString()) && checkDomain()) {
                                url = searchEditText.getText().toString();
                                Toast.makeText(BrowserSearchActivity.this, "empty", Toast.LENGTH_SHORT).show();
                            }
//                            else if(URLUtil.isValidUrl("https://"+searchEditText.getText().toString()) && bool)
//                            {
//                                url = "https://"+searchEditText.getText().toString();
//                                Toast.makeText(BrowserSearchActivity.this, "https", Toast.LENGTH_SHORT).show();
//
//                            }
                            else if (URLUtil.isValidUrl("http://" + searchEditText.getText().toString()) && checkDomain()) {
                                url = "http://" + searchEditText.getText().toString();
                                Toast.makeText(BrowserSearchActivity.this, "http", Toast.LENGTH_SHORT).show();
                            } else {
                                url = "https://www.google.com/#q=" + searchEditText.getText().toString();
                            }

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

//    boolean thread(String url)
//    {
//        final boolean[] checkProtocol = {false};
//
//        Thread thread = new Thread(){
//            @Override
//            public void run() {
//
//                try {
//                checkProtocol[0] = usesHttps(url);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        thread.start();
//
//        return checkProtocol[0];
//    }
//
//    static boolean usesHttps(final String urlWithoutProtocol) throws IOException {
//        try {
//            Jsoup.connect("http://" + urlWithoutProtocol).get();
//            return false;
//        } catch (final IOException e) {
//            Jsoup.connect("https://" + urlWithoutProtocol).get();
//            return true;
//        }
//    }

    private void addBookmark() {

        customDialog();
    }

    /**
     * This method is a listener for GO button on the keyboard, if pressed first the internet
     * connectivity is checked if true then url is checked, if the url is a simple search on
     * google it will be directed to google search, if the url is proper url of a website it
     * will be redirected to the corresponding website. For checking if its a website url
     * checkdomain method is used.
     *
     * @param keyCode
     * @param event
     * @return
     */

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        // If the event is a key-down event on the "enter" button

        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {
            // Perform action on key press

            if (isNetworkAvailable()) {

                String url;

                Intent intent = new Intent(BrowserSearchActivity.this, MainActivity.class);
                if (URLUtil.isValidUrl(searchEditText.getText().toString()) && checkDomain()) {
                    url = searchEditText.getText().toString();
                    Toast.makeText(BrowserSearchActivity.this, "empty", Toast.LENGTH_SHORT).show();
                }
//                            else if(URLUtil.isValidUrl("https://"+searchEditText.getText().toString()) && bool)
//                            {
//                                url = "https://"+searchEditText.getText().toString();
//                                Toast.makeText(BrowserSearchActivity.this, "https", Toast.LENGTH_SHORT).show();
//
//                            }
                else if (URLUtil.isValidUrl("http://" + searchEditText.getText().toString()) && checkDomain()) {
                    url = "http://" + searchEditText.getText().toString();
                    Toast.makeText(BrowserSearchActivity.this, "http", Toast.LENGTH_SHORT).show();
                } else {
                    url = "https://www.google.com/#q=" + searchEditText.getText().toString();
                }
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

        for (int i = 0; i < checkUrl.size(); i++) {
            checkDomain = searchEditText.getText().toString().contains(checkUrl.get(i));

            if (checkDomain) {
                break;
            }
        }
        return checkDomain;
    }

    /**
     * This method checks if the network is available or not.
     *
     * @return
     */

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * This method reads the bookmark database and add the bookmarks to the arrayLists
     * which are not cleared(cleared bookmarks are checked through bookmark bool).
     * These lists will later on be passed to the recyclerView for loading
     *
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
            case R.id.item_home:
                startHomeActivity();
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

    /**
     * This method starts the home activity when the home menu item is clicked
     *
     */

    private void startHomeActivity() {

        Intent intent = new Intent(this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * This method starts the vpn activity when vpn menu item is clicked
     *
     */

    private void startVpn() {

        Intent intent = new Intent(this, VpnActivity.class);
        startActivity(intent);
    }

    /**
     *
     * This method opens a customDialogBox when the floating action button is clicked
     * to add a bookmark.
     *
     */

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

    /**
     *
     * This method starts a bookmark activity when the bookmark menu item is clicked.
     * Loads an interstitial add too if is loaded.
     *
     */

    private void showBookmarks() {

        if (interstitialAd.isLoaded() && interstitialAd != null) {
            interstitialAd.show();
        } else {
            Intent intent = new Intent(this, BookmarksActivity.class);
            intent.putExtra("browser", "main");
            startActivity(intent);
            reqNewInterstitial();
        }
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                reqNewInterstitial();
                Intent intent = new Intent(BrowserSearchActivity.this, BookmarksActivity.class);
                intent.putExtra("browser", "main");
                startActivity(intent);
            }
        });


    }

    /**
     * This method starts history activity if history menu item is clicked
     */
    private void showHistory() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }

    /**
     * This method starts download activity if downloads menu item is clicked
     * also loads interstitial ad
     *
     */

    private void showDownloads() {

        if (interstitialAd.isLoaded() && interstitialAd != null) {
            interstitialAd.show();
        } else {
            Intent intent = new Intent(this, DownloadActivity.class);
            startActivity(intent);
            reqNewInterstitial();
        }
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                reqNewInterstitial();
                Intent intent = new Intent(BrowserSearchActivity.this, DownloadActivity.class);
                startActivity(intent);
            }
        });

    }

    /**
     * This method starts incognito browser activity if incognito menu item is clicked
     * and also loads interstitial ad if available
     */
    private void startIncognitoBrowser() {

        if (interstitialAd.isLoaded() && interstitialAd != null) {
            interstitialAd.show();
        } else {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
            reqNewInterstitial();
        }
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                reqNewInterstitial();
                Intent intent = new Intent(BrowserSearchActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * This method starts the browser MainActivity when a bookmark is clicked shown below the editText
     * if available.
     * @param v
     * @param position
     */

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

    /**
     * Initialization and listener of banner ad
     */
    private void bannerAd() {
        adView = findViewById(R.id.banner_large_browser);
        AdRequest adrequest = new AdRequest.Builder()
                .build();
        adView.loadAd(adrequest);
        adView.setAdListener(new AdListener() {

            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdFailedToLoad(int error) {
                adView.setVisibility(View.GONE);
            }
        });
    }

    /**
     * Method for initializing and loading the interstitial ad
     */
    public void reqNewInterstitial() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(getResources().getString(R.string.Interstitial));
        interstitialAd.loadAd(new AdRequest.Builder().build());
    }
}
