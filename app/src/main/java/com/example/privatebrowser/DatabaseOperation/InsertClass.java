package com.example.privatebrowser.DatabaseOperation;

import android.content.Context;
import android.widget.Toast;

import com.example.privatebrowser.DatabaseHelper.DatabaseHelper;

class InsertClass {


    private final Context context;
    private final DatabaseHelper dbh;

    InsertClass(Context context, String DB_NAME, int DB_VERSION, String TABLE_NAME)
    {
        this.context = context;
        dbh = new DatabaseHelper(context, DB_NAME, DB_VERSION, TABLE_NAME);
    }

    void saveRecord(String fileName, Boolean bool) {

        boolean res = dbh.insertData(fileName, bool);
//        if(res) {
//            Toast.makeText(context,"Values saved in database", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Toast.makeText(context,"Values are not saved in database", Toast.LENGTH_SHORT).show();
//        }
    }

    void saveRecord(String url, String fileName, Boolean bool) {

        boolean res = dbh.insertData(url, fileName, bool);
//        if(res) {
//            Toast.makeText(context,"Values saved in database", Toast.LENGTH_SHORT).show();
//        }
//        else {
//            Toast.makeText(context,"Values are not saved in database", Toast.LENGTH_SHORT).show();
//        }
    }

}
