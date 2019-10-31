package com.example.privatebrowser.DatabaseOperation;

import android.content.Context;
import android.widget.Toast;

import com.example.privatebrowser.DatabaseHelper.DatabaseHelper;


class UpdateClass {

    private final DatabaseHelper dbh;
    private final Context context;

    UpdateClass(Context context, String DB_NAME, int DB_VERSION, String TABLE_NAME)
    {
        this.context = context;
        dbh = new DatabaseHelper(context, DB_NAME, DB_VERSION, TABLE_NAME);
    }

    void updateRecord(String fileName, Boolean bool) {

        boolean res = dbh.updateData(fileName, bool);
//        if(res) {
//            Toast.makeText(context,"Values updated in database", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Toast.makeText(context,"Values are not updated in database", Toast.LENGTH_SHORT).show();
//        }
    }
    void updateBookmarkRecord(String url, String fileName, Boolean bool) {

        boolean res = dbh.updateBookmarkData(url,fileName, bool);
//        if(res) {
//            Toast.makeText(context,"Values updated in database", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Toast.makeText(context,"Values are not updated in database", Toast.LENGTH_SHORT).show();
//        }
    }
}
