package com.example.privatebrowser.IncognitoActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.privatebrowser.Adapters.BookmarksAdapter;
import com.example.privatebrowser.Classes.ChangeLanguage;
import com.example.privatebrowser.DatabaseOperation.DatabaseClass;

import com.example.privatebrowser.R;
import com.example.privatebrowser.utils.Utils;

import java.util.ArrayList;

import static com.example.privatebrowser.Adapters.BookmarksAdapter.bookmarksList;
import static com.example.privatebrowser.Adapters.BookmarksAdapter.bookmarksUrlList;

public class BookmarksActivity extends AppCompatActivity implements View.OnClickListener,
        BookmarksAdapter.BookmarksAdapterOnClickHandler, BookmarksAdapter.AdapterListener {

    private BookmarksAdapter bookmarkAdapter;
    private ArrayList<String> bookmarkName;
    private ArrayList<String> bookmarkUrlName;
    private RecyclerView mRecyclerView;
    private DatabaseClass databaseClass;
    private Button clearBookmarks;
    ChangeLanguage changeLanguage;
    String browser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguage = new ChangeLanguage(this);
        changeLanguage.loadLocale();
        setContentView(R.layout.activity_bookmarks);

        databaseClass = new DatabaseClass(this);
        getBookmarksFromSQLite();

        browser = getIntent().getStringExtra("browser");

        inItUi();

        setRecyclerView();

        registerListeners();

        if(bookmarkName.isEmpty())
        {
            clearBookmarks.setVisibility(View.INVISIBLE);
        }
    }

    private void registerListeners() {

        clearBookmarks.setOnClickListener(this);
    }


    private void inItUi() {

        mRecyclerView = findViewById(R.id.recycler_view_bookmarks);
        clearBookmarks = findViewById(R.id.button_clear_bookmarks);
    }

    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        bookmarkAdapter = new BookmarksAdapter(this, this, this);

        mRecyclerView.setAdapter(bookmarkAdapter);

        bookmarkAdapter.setBookmarkUrl(bookmarkUrlName);
        bookmarkAdapter.setBookmarkNames(bookmarkName);
    }

    /**
     * Read data from SQLite database and read where the bookmark is saved means where the the value is true in
     * bookmark database. It adds that url and its name to the arrayList and pass the arrayList to recyclerView
     * through adapter.
     */
    private void getBookmarksFromSQLite() {

        bookmarkName = new ArrayList<>();
        bookmarkUrlName = new ArrayList<>();

        databaseClass.readAllBookmarkRecord();
        ArrayList<String> bookmarkUrl = Utils.nameUtils;
        bookmarkName = Utils.bookmarkNameUtils;
        ArrayList<String> bookmarkBool = Utils.boolUtils;

        for (int i = 0; i < bookmarkName.size(); i++) {
            if (bookmarkBool.get(i).equals("true")) {
                bookmarkUrlName.add(bookmarkUrl.get(i));

            }
        }
    }

    /**
     * When the list item is clicked in recyclerview the following link would open.
     *
     * @param bookmarksStr
     */

    @Override
    public void onClick(String bookmarksStr) {

        Intent intent = new Intent(BookmarksActivity.this, MainActivity.class);
        intent.putExtra("browser", browser);
        intent.putExtra("url", bookmarksStr);
        startActivity(intent);
    }

    /**
     * When the cross icon is clicked the bookmark is removed from the database. The true value is falsed.
     *
     * @param v        The view is passed
     * @param position The position of the clicked item is recieved
     */

    @Override
    public void btnOnClick(View v, int position) {

        databaseClass.updateBookmarkRecord(bookmarksList.get(position), bookmarksUrlList.get(position), false);

        bookmarksList.remove(position);
        bookmarksUrlList.remove(position);

        bookmarkAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.button_clear_bookmarks)
        {
            databaseClass.deleteBookmarkRecord();
            bookmarksList.clear();
            bookmarksUrlList.clear();
            bookmarkAdapter.notifyDataSetChanged();
            clearBookmarks.setVisibility(View.INVISIBLE);
        }
    }
}
