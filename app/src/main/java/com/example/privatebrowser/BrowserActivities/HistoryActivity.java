package com.example.privatebrowser.BrowserActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.privatebrowser.Adapters.HistoryAdapter;
import com.example.privatebrowser.Classes.ChangeLanguage;
import com.example.privatebrowser.DatabaseOperation.DatabaseClass;
import com.example.privatebrowser.IncognitoActivities.MainActivity;
import com.example.privatebrowser.R;
import com.example.privatebrowser.utils.Utils;

import static com.example.privatebrowser.Adapters.HistoryAdapter.historyItemList;
import static com.example.privatebrowser.Adapters.HistoryAdapter.historyUrlList;


import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity implements HistoryAdapter.AdapterListener, View.OnClickListener,
        HistoryAdapter.HistoryAdapterOnClickHandler {

    private HistoryAdapter historyAdapter;
    private RecyclerView mRecyclerView;
    private DatabaseClass databaseClass;
    private ArrayList<String> historyTitle;
    private ArrayList<String> historyUrls;
    private Button clearBrowsing;
    ChangeLanguage changeLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguage = new ChangeLanguage(this);
        changeLanguage.loadLocale();
        setContentView(R.layout.activity_history);

        databaseClass = new DatabaseClass(this);

        inItUi();

        registerListeners();
    }

    private void registerListeners() {

        clearBrowsing.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        getBookmarksFromSQLite();

        setRecyclerView();
    }



    private void inItUi() {

        mRecyclerView = findViewById(R.id.recycler_view_history);
        clearBrowsing = findViewById(R.id.button_clear_browsing);

    }


    private void setRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        historyAdapter = new HistoryAdapter(this, this, this);

        historyAdapter.setHistoryItem(historyTitle);
        historyAdapter.setHistoryUrl(historyUrls);

        mRecyclerView.setAdapter(historyAdapter);


    }


    private void getBookmarksFromSQLite() {

        historyTitle = new ArrayList<>();
        historyUrls = new ArrayList<>();

        databaseClass.readAllHistoryRecord();

        ArrayList<String> historyUrl = Utils.nameHistoryUtils;
        historyTitle = Utils.bookmarkNameHistoryUtils;
        ArrayList<String> historyBool = Utils.boolHistoryUtils;

        Toast.makeText(this, "" + historyTitle.size() + " " + historyUrls.size(), Toast.LENGTH_SHORT).show();
        for (int i = 0; i < historyTitle.size(); i++) {

            if (historyBool.get(i).equals("true")) {

                historyUrls.add(historyUrl.get(i));
            }
        }
    }

    @Override
    public void btnOnClick(View v, int position) {


        databaseClass.updateHistoryRecord(historyUrlList.get(position), historyItemList.get(position), false);

        historyUrlList.remove(position);
        historyItemList.remove(position);
        Toast.makeText(this, "" + historyUrlList.size() + " " + historyItemList.size(), Toast.LENGTH_SHORT).show();

        historyAdapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(String historyStr) {

        Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
        intent.putExtra("url", historyStr);
        intent.putExtra("browser", "main");
        startActivity(intent);

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button_clear_browsing) {

            databaseClass.deleteHistoryRecord();
            historyUrlList.clear();
            historyItemList.clear();
            historyAdapter.notifyDataSetChanged();

        }
    }
}
