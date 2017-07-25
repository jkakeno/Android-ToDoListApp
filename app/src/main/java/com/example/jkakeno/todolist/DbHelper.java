package com.example.jkakeno.todolist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import java.util.ArrayList;

//This class creates the db
public class DbHelper extends SQLiteOpenHelper{

    private static final String TAG = DbHelper.class.getSimpleName();
    private static final String DB_NAME="MyDataBase";
    private static final int DB_VER = 1;
    public static final String DB_TABLE="Task";
    public static final String DB_TASKNAME = "TaskName";
    public static final String DB_DUEDATE = "DueDate";

    private SQLiteDatabase mDb;
    Entry mEntry;
    long entryId;
    private String dateFormatted;


    public DbHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
//        String query = String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT NOT NULL);",DB_TABLE,DB_TASKNAME);
//        String query = "CREATE TABLE " + DB_TABLE + "(" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DB_TASKNAME + " TEXT NOT NULL);";
        String query = "CREATE TABLE " + DB_TABLE + "(" + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + DB_TASKNAME + " TEXT," + DB_DUEDATE + " INT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = "DELETE TABLE IF EXISTS " + DB_TABLE;
        db.execSQL(query);
        onCreate(db);
    }

//Insert entry in db
    public long insertNewTask(String task, long date){
        SQLiteDatabase db= this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_TASKNAME,task);
        values.put(DB_DUEDATE,date);
        entryId = db.insertWithOnConflict(DB_TABLE,null,values,SQLiteDatabase.CONFLICT_REPLACE);
//        Log.v(TAG, String.valueOf(entryId));
        db.close();
        return entryId;
    }

//Delete entry from db
    public void deleteTask(long entryId){
        SQLiteDatabase db = this.getWritableDatabase();
//Delete entry in DB_TABLE where ID column in DB_TABLE equals the entryId passed
        db.delete(DB_TABLE,BaseColumns._ID + " = ?",new String[]{String.valueOf(entryId)});
        db.close();
    }


    public ArrayList<Entry> sortEntry (String selection){
        ArrayList<Entry> entryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sortMode = selection.equals("Ascending") ? "ASC" : "DESC";
        String query = "SELECT * FROM " + DB_TABLE + " ORDER BY " + DB_DUEDATE + " " + sortMode;
        Cursor cursor = db.rawQuery(query,null);
//While there's a row to move to with in the cursor
        while(cursor.moveToNext()){
            String taskName = cursor.getString(cursor.getColumnIndex(DB_TASKNAME));
            long dueDate = cursor.getLong(cursor.getColumnIndex(DB_DUEDATE));
            int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)); // <---------
//            Log.d(TAG, String.valueOf(id));
//Make a new Entry and add to list
            entryList.add(new Entry(id,taskName,dueDate));
        }
        cursor.close();
        db.close();
        return entryList;
    }

    public ArrayList<Entry> getEntryList(){
        ArrayList<Entry> entryList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DB_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        while(cursor.moveToNext()){
            String taskName = cursor.getString(cursor.getColumnIndex(DB_TASKNAME));
            long dueDate = cursor.getLong(cursor.getColumnIndex(DB_DUEDATE));
            int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID)); // <---------
//            Log.d(TAG, String.valueOf(id));
//Make a new Entry and add to list
            entryList.add(new Entry(id, taskName,dueDate));
        }
        cursor.close();
        db.close();
        return entryList;
    }

    public ArrayList<Integer> getEntryIdList (){
        ArrayList<Integer> idList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DB_TABLE;
        Cursor cursor = db.rawQuery(selectQuery, null);
        while(cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex(BaseColumns._ID));
            idList.add(id);
        }
        return idList;
    }
}
