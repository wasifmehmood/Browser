package com.example.privatebrowser.DatabaseHelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class DatabaseHelper extends SQLiteOpenHelper {

    private static String DB_NAME;
    private static int DB_VERSION;
    private static String TABLE_NAME;
    private final SQLiteDatabase db;
    private final Context context;
    public DatabaseHelper(Context context, String DB_NAME, int DB_VERSION, String TABLE_NAME) {
        super(context, DB_NAME, null, DB_VERSION);
        this.context = context;
        DatabaseHelper.DB_NAME = DB_NAME;
        DatabaseHelper.DB_VERSION = DB_VERSION;
        DatabaseHelper.TABLE_NAME = TABLE_NAME;
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (TABLE_NAME.equals("downloads")) {
            db.execSQL("Create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,VALUE TEXT);");
//            Toast.makeText(context, "DONE", Toast.LENGTH_SHORT).show();
        }

        else if(TABLE_NAME.equals("bookmarks"))
        {
            db.execSQL("Create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,URL TEXT,NAME TEXT,VALUE TEXT);");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String name, Boolean bool) {
//        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("NAME",name);
        cv.put("VALUE",bool.toString());
//        Toast.makeText(context, "name: "+name+" bool: "+bool.toString(), Toast.LENGTH_SHORT).show();
        long result = db.insert(TABLE_NAME,null,cv);
//        Toast.makeText(context, "result: "+result, Toast.LENGTH_SHORT).show();
        return result != -1;
    }

    public boolean insertData(String url, String name, Boolean bool) {

        ContentValues cv = new ContentValues();
        cv.put("URL", url);
        cv.put("NAME",name);
        cv.put("VALUE",bool.toString());

        long result = db.insert(TABLE_NAME,null,cv);

        return result != -1;
    }

    public boolean updateData(String name, boolean bool) {

        ContentValues cv = new ContentValues();
        cv.put("VALUE",String.valueOf(bool));
        long result = db.update(TABLE_NAME, cv, "NAME = ?",new String[] { name });
        return result != -1;
    }
    public boolean updateBookmarkData(String url, String name, boolean bool) {

        ContentValues cv = new ContentValues();
        cv.put("VALUE",String.valueOf(bool));
        long result = db.update(TABLE_NAME, cv, "NAME = ?",new String[] { name });
        return result != -1;
    }

    public Cursor searchData(String name) {
        String query = "Select * from " + TABLE_NAME + " where NAME = '" + name + "'";
        return db.rawQuery(query,null);
    }

    public Cursor searchBookmarkData(String name) {
        String query = "Select * from " + TABLE_NAME + " where URL = '" + name + "'";
        return db.rawQuery(query,null);
    }

//    public Cursor searchDataId(int id) {
//        String query = "Select * from " + TABLE_NAME + " where Id = " +id;
//        return db.rawQuery(query,null);
//    }

    public Cursor searchAll() {
        String query = "Select * from " + TABLE_NAME;
        return db.rawQuery(query,null);
    }

    public void deleteData()
    {
        db.execSQL("delete from "+ TABLE_NAME);
    }
}