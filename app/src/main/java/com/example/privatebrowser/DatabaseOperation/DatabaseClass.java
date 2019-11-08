package com.example.privatebrowser.DatabaseOperation;


import android.content.Context;
import android.widget.Toast;


public class DatabaseClass {

    private final String bookmarkDBName = "Bookmarks.db";
    private final String TABLE_NAME_BOOKMARK = "bookmarks";
    private final String downloadsDBName = "Downloads.db";
    private final String TABLE_NAME = "downloads";
    private final String historyDBName = "History.db";
    private final String TABLE_NAME_HISTORY = "history";
    private final int DB_VERSION = 1;


    private final Context context;

    public DatabaseClass(Context context) {
        this.context = context;
    }

    public void deleteRecord() {
        DeleteClass deleteClass = new DeleteClass(context, downloadsDBName, DB_VERSION, TABLE_NAME);
        deleteClass.deleteAll();
    }

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

    /**
     * CRUD operations for History Table
     */
    public void deleteHistoryRecord() {
        DeleteClass deleteClass = new DeleteClass(context, historyDBName, DB_VERSION, TABLE_NAME_HISTORY);
        deleteClass.deleteAll();
    }

    public void insertHistoryRecord(String url, String bookmark, Boolean bool) {
//        Toast.makeText(context, ""+TABLE_NAME_HISTORY+" "+historyDBName+" "+DB_VERSION, Toast.LENGTH_SHORT).show();
        InsertClass insertClass = new InsertClass(context, historyDBName, DB_VERSION, TABLE_NAME_HISTORY);
        insertClass.saveRecord(url, bookmark, bool);
    }

//    void readBookmarkRecord() {
//        SearchAllClass searchAllClass = new SearchAllClass(context, bookmarkDBName, DB_VERSION, TABLE_NAME_BOOKMARK);
//        searchAllClass.searchAllRecord();
//    }

    public String searchHistoryRecord(String bookmark) {
        SearchClass searchClass = new SearchClass(context, historyDBName, DB_VERSION, TABLE_NAME_HISTORY);
        return searchClass.searchBookmarkFileName(bookmark);
    }

    public void readAllHistoryRecord() {

        SearchAllClass searchAllClass = new SearchAllClass(context, historyDBName, DB_VERSION, TABLE_NAME_HISTORY);
        searchAllClass.searchAllHistoryRecord();
    }

    public void updateHistoryRecord(String url, String fileName, Boolean bool) {
        UpdateClass updateClass = new UpdateClass(context, historyDBName, DB_VERSION, TABLE_NAME_HISTORY);
        updateClass.updateBookmarkRecord(url, fileName, bool);
    }

}
