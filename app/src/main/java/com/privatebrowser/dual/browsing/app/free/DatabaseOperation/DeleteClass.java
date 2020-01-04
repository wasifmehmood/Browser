package com.privatebrowser.dual.browsing.app.free.DatabaseOperation;

import android.content.Context;

import com.privatebrowser.dual.browsing.app.free.DatabaseHelper.DatabaseHelper;

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
