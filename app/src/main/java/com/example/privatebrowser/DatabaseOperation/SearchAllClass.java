package com.example.privatebrowser.DatabaseOperation;

import android.content.Context;
import android.database.Cursor;

import com.example.privatebrowser.DatabaseHelper.DatabaseHelper;
import com.example.privatebrowser.utils.Utils;

import java.util.ArrayList;

class SearchAllClass {

    private ArrayList<String> names;
    private ArrayList<String> bool;
    private ArrayList<String> id;

    private final DatabaseHelper dbh;

    public SearchAllClass(Context context, String DB_NAME, int DB_VERSION, String TABLE_NAME)
    {
        dbh = new DatabaseHelper(context, DB_NAME, DB_VERSION, TABLE_NAME);
    }

    public void searchAllRecord()
    {
        Cursor cursor = dbh.searchAll();

        names = new ArrayList<>();
        bool = new ArrayList<>();
        id = new ArrayList<>();

        while (cursor.moveToNext()) {
            id.add(cursor.getString(0));
            names.add(cursor.getString(1));
            bool.add(cursor.getString(2));
        }
        Utils.boolUtils = bool;
        Utils.nameUtils = names;

    }
    ArrayList<String> bookmarkName;
    public void searchAllBookmarkRecord()
    {
        Cursor cursor = dbh.searchAll();

        names = new ArrayList<>();
        bool = new ArrayList<>();
        id = new ArrayList<>();
        bookmarkName = new ArrayList<>();

        while (cursor.moveToNext()) {
            id.add(cursor.getString(0));
            names.add(cursor.getString(1));
            bookmarkName.add(cursor.getString(2));
            bool.add(cursor.getString(3));
        }
        Utils.boolUtils = bool;
        Utils.nameUtils = names;
        Utils.bookmarkNameUtils = bookmarkName;
    }
}