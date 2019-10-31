package com.example.privatebrowser.DatabaseOperation;

import android.content.Context;

import com.example.privatebrowser.DatabaseHelper.DatabaseHelper;

class DeleteClass {

    private final DatabaseHelper dbh;

    DeleteClass(Context context, String DB_NAME, int DB_VERSION, String TABLE_NAME)
    {
        dbh = new DatabaseHelper(context, DB_NAME, DB_VERSION, TABLE_NAME);
    }
    void deleteAll()
    {
        dbh.deleteData();
    }
}
