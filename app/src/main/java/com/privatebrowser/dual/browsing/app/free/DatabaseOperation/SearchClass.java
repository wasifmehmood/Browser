package com.privatebrowser.dual.browsing.app.free.DatabaseOperation;

import android.content.Context;
import android.database.Cursor;

import com.privatebrowser.dual.browsing.app.free.DatabaseHelper.DatabaseHelper;

import java.util.ArrayList;

class SearchClass {

    private final DatabaseHelper dbh;

    ArrayList<String> url;
    private ArrayList<String> fileNames;
    private ArrayList<String> bool;

    SearchClass(Context context, String DB_NAME, int DB_VERSION, String TABLE_NAME)
    {
        dbh = new DatabaseHelper(context, DB_NAME, DB_VERSION, TABLE_NAME);
    }

    String searchName(String fileName) {

        Cursor cursor = dbh.searchData(fileName);
        fileNames = new ArrayList<>();
        bool = new ArrayList<>();

        while (cursor.moveToNext()) {

            fileNames.add(cursor.getString(1));
            bool.add(cursor.getString(2));
        }

        if (bool.size() > 0) {
            return bool.get(0);

        }

        return "Empty";
    }

    public String searchBookmarkName(String fileName) {

        Cursor cursor = dbh.searchData(fileName);
        fileNames = new ArrayList<>();
        bool = new ArrayList<>();

        while (cursor.moveToNext()) {

            fileNames.add(cursor.getString(2));
            bool.add(cursor.getString(3));
        }

        if (bool.size() > 0) {
            return bool.get(0);

        }

        return "Empty";
    }
    String searchBookmarkFileName(String fileName) {

        Cursor cursor = dbh.searchBookmarkData(fileName);
        fileNames = new ArrayList<>();
        bool = new ArrayList<>();

        while (cursor.moveToNext()) {

            fileNames.add(cursor.getString(2));
//            Toast.makeText(context, "ID "+cursor.getString(0)+" URL"+cursor.getString(1)+
//                    "Name"+cursor.getString(2), Toast.LENGTH_SHORT).show();
            bool.add(cursor.getString(3));
        }

        if (fileNames.size() > 0) {

            return fileNames.get(0);
        }

        return "Empty";
    }
}
