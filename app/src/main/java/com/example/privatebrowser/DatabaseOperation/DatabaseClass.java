package com.example.privatebrowser.DatabaseOperation;


import android.content.Context;


public class DatabaseClass {

    private final String bookmarkDBName = "Bookmarks.db";
    private final String TABLE_NAME_BOOKMARK = "bookmarks";
    private final String downloadsDBName = "Downloads.db";
    private final int DB_VERSION = 1;
    private final String TABLE_NAME = "downloads";

    private final Context context;

    public DatabaseClass(Context context) {
        this.context = context;
    }

//    void deleteRecord() {
//        DeleteClass deleteClass = new DeleteClass(context, downloadsDBName, DB_VERSION, TABLE_NAME);
//        deleteClass.deleteAll();
//    }

    public void insertRecord(String fileName, Boolean bool) {
        InsertClass insertClass = new InsertClass(context, downloadsDBName, DB_VERSION, TABLE_NAME);
        insertClass.saveRecord(fileName, bool);
    }


    public void readRecord() {
        SearchAllClass searchAllClass = new SearchAllClass(context, downloadsDBName, DB_VERSION, TABLE_NAME);
        searchAllClass.searchAllRecord();
    }

    public String searchRecord(String fileName) {
        SearchClass searchClass = new SearchClass(context, downloadsDBName, DB_VERSION, TABLE_NAME);
        return searchClass.searchName(fileName);
    }

    public void updateRecord(String fileName, Boolean bool) {
        UpdateClass updateClass = new UpdateClass(context, downloadsDBName, DB_VERSION, TABLE_NAME);
        updateClass.updateRecord(fileName, bool);
    }

    public void deleteBookmarkRecord() {
        DeleteClass deleteClass = new DeleteClass(context, bookmarkDBName, DB_VERSION, TABLE_NAME_BOOKMARK);
        deleteClass.deleteAll();
    }

    public void insertBookmarkRecord(String url, String bookmark, Boolean bool) {
        InsertClass insertClass = new InsertClass(context, bookmarkDBName, DB_VERSION, TABLE_NAME_BOOKMARK);
        insertClass.saveRecord(url, bookmark, bool);
    }

//    void readBookmarkRecord() {
//        SearchAllClass searchAllClass = new SearchAllClass(context, bookmarkDBName, DB_VERSION, TABLE_NAME_BOOKMARK);
//        searchAllClass.searchAllRecord();
//    }

    public String searchBookmarkRecord(String bookmark) {
        SearchClass searchClass = new SearchClass(context, bookmarkDBName, DB_VERSION, TABLE_NAME_BOOKMARK);
        return searchClass.searchBookmarkFileName(bookmark);
    }

    public void readAllBookmarkRecord() {

        SearchAllClass searchAllClass = new SearchAllClass(context, bookmarkDBName, DB_VERSION, TABLE_NAME_BOOKMARK);
        searchAllClass.searchAllBookmarkRecord();
    }

    public void updateBookmarkRecord(String url, String fileName, Boolean bool) {
        UpdateClass updateClass = new UpdateClass(context, bookmarkDBName, DB_VERSION, TABLE_NAME_BOOKMARK);
        updateClass.updateBookmarkRecord(url, fileName, bool);
    }

}