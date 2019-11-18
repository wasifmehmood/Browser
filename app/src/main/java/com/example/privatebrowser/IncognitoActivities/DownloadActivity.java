package com.example.privatebrowser.IncognitoActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.privatebrowser.Adapters.DownloadsAdapter;
import com.example.privatebrowser.Classes.ChangeLanguage;
import com.example.privatebrowser.DatabaseOperation.DatabaseClass;
import com.example.privatebrowser.FileFilter.AudioFileFilter;
import com.example.privatebrowser.FileFilter.DocFileFilter;
import com.example.privatebrowser.FileFilter.ImageFileFilter;
import com.example.privatebrowser.FileFilter.VideoFileFilter;
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
    Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changeLanguage = new ChangeLanguage(this);
        changeLanguage.loadLocale();
        setContentView(R.layout.activity_download);

        activity = this;

        databaseClass = new DatabaseClass(this);
        if (!folder.exists()) {
            folder.mkdir();
        }

        listFilesForFolder(folder);

        inItUi();

        setRecyclerView();

        registerListeners();

        if (fileNames.isEmpty()) {
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


//    ArrayList<Integer> position;

    private void listFilesForFolder(final File folder) {

        fileNames = new ArrayList<>();
//        position = new ArrayList<>();

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
    public void onClick(String fileName, View v, int position) {

        final Context context = this;

        Activity activity = this.activity;

        Toast.makeText(context, "Clicked: " + fileName, Toast.LENGTH_SHORT).show();

        File file = new File(Environment.getExternalStorageDirectory() + "/PrivateBrowser/" + fileName);

        Uri selectedUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + "/PrivateBrowser/" + fileName));

        AudioFileFilter audioFileFilter = new AudioFileFilter(file);
        DocFileFilter docFileFilter = new DocFileFilter(file);
        ImageFileFilter imageFileFilter = new ImageFileFilter(file);
        VideoFileFilter videoFileFilter = new VideoFileFilter(file);

        Uri parsedUri = Uri.parse(Environment.getExternalStorageDirectory() + "/PrivateBrowser/" + fileName);
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        if (audioFileFilter.accept(file)) {

            intent.setDataAndType(selectedUri, "audio/*");
        }

        else if(docFileFilter.accept(file)) {

            intent.setDataAndType(selectedUri, "text/*");
        }
        else if(imageFileFilter.accept(file))
        {
            intent.setDataAndType(selectedUri, "image/*");
        }
        else if(videoFileFilter.accept(file))
        {
            intent.setDataAndType(selectedUri, "video/*");
        }
        if (intent.resolveActivityInfo(activity.getPackageManager(), 0) != null) {
            activity.startActivity(intent);
        } else {
            // if you reach this place, it means there is no any file
            // explorer app installed on your device
        }

    }

    @Override
    public void btnOnClick(View v, int position) {

        databaseClass.updateRecord(mFileName.get(position), false);
        Toast.makeText(activity, ""+mFileName.get(position), Toast.LENGTH_SHORT).show();

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
