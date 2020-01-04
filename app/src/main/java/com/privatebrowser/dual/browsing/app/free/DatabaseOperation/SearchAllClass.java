package com.privatebrowser.dual.browsing.app.free.DatabaseOperation;

import android.content.Context;
import android.database.Cursor;

import com.privatebrowser.dual.browsing.app.free.DatabaseHelper.DatabaseHelper;
import com.privatebrowser.dual.browsing.app.free.utils.Utils;

import java.util.ArrayList;

class SearchAllClass {

    private ArrayList<String> names;
    private ArrayList<String> bool;
    private ArrayList<String> id;

    Context context;
    private final DatabaseHelper dbh;

    public SearchAllClass(Context context, String DB_NAME, int DB_VERSION, String TABLE_NAME)
    {
        dbh = new DatabaseHelper(context, DB_NAME, DB_VERSION, TABLE_NAME);
        this.context = context;
    }

    public void searchAllRecord()
    {
        Cursor cursor = dbh.searchAll();

        names = new ArrayList<>();
        bool = new ArrayList<>();
        id = new ArrayList<>();

//        int i = 0;
        while (cursor.moveToNext()) {
            id.add(cursor.getString(0));
            names.add(cursor.getString(1));
            bool.add(cursor.getString(2));

//            Toast.makeText(context, "Name: "+ names.get(i++) +" Bool: "+bool, Toast.LENGTH_SHORT).show();
        }
        Utils.idUtils = id;
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


    public void searchAllHistoryRecord()
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
        Utils.boolHistoryUtils = bool;
        Utils.nameHistoryUtils = names;
        Utils.bookmarkNameHistoryUtils = bookmarkName;
    }
}
