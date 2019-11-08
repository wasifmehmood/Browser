package com.example.privatebrowser.IncognitoActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;

import com.example.privatebrowser.Adapters.DownloadsAdapter;
import com.example.privatebrowser.Classes.ChangeLanguage;
import com.example.privatebrowser.DatabaseOperation.DatabaseClass;
import com.example.privatebrowser.R;
import com.example.privatebrowser.utils.Utils;

import java.io.File;
import java.util.ArrayList;

import static com.example.privatebrowser.Adapters.DownloadsAdapter.mFileName;

public class DownloadActivity extends AppCompatActivity implements View.OnClickListener,
        DownloadsAdapter.DownloadsAdapterOnClickHandler, DownloadsAdapter.adapterListener {

    private RecyclerView mRecyclerView;
    private DownloadsAdapter mDownloadsVariable;
    private ArrayList<String> fileNames;
    private DatabaseClass databaseClass;
    private final File folder = new File(Environment.getExternalStorageDirectory() + "/PrivateBrowser");
    private Button clearDownloads;
    ChangeLanguage changeLanguage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguage = new ChangeLanguage(this);
        changeLanguage.loadLocale();
        setContentView(R.layout.activity_download);

        databaseClass = new DatabaseClass(this);
        if (!folder.exists()) {
            folder.mkdir();
        }

        listFilesForFolder(folder);

        inItUi();

        setRecyclerView();

        registerListeners();

        if(fileNames.isEmpty())
        {
            clearDownloads.setVisibility(View.INVISIBLE);
        }
    }

    private void registerListeners() {

        clearDownloads.setOnClickListener(this);
    }


    private void inItUi() {

        mRecyclerView = findViewById(R.id.recycler_view_downloads);
        clearDownloads = findViewById(R.id.button_clear_downloads);
    }

    private void setRecyclerView() {

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        layoutManager.setReverseLayout(false);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mDownloadsVariable = new DownloadsAdapter(this, this, this);

        mRecyclerView.setAdapter(mDownloadsVariable);

        mDownloadsVariable.setFileNames(fileNames);

    }


    private void listFilesForFolder(final File folder) {

        fileNames = new ArrayList<>();
        int x = 0;
        if (folder.listFiles() != null) {
            for (final File fileEntry : folder.listFiles()) {
                {
                    if (fileEntry.isDirectory()) {
                        listFilesForFolder(fileEntry);
                    } else {

                        databaseClass.readRecord();

                        try {
                            if (!Utils.boolUtils.get(x).equals("false")) {
                                fileNames.add(fileEntry.getName());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
                x++;
            }
        }
    }

    @Override
    public void onClick(String fileName) {

        final Context context = this;

    }

    @Override
    public void btnOnClick(View v, int position) {

        databaseClass.updateRecord(mFileName.get(position), false);

        mFileName.remove(position);

        mDownloadsVariable.notifyDataSetChanged();

    }

    @Override
    public void onClick(View v) {

        if (v.getId() == R.id.button_clear_downloads) {
            databaseClass.deleteRecord();
            mFileName.clear();
            mDownloadsVariable.notifyDataSetChanged();
            clearDownloads.setVisibility(View.INVISIBLE);
        }

    }
}
