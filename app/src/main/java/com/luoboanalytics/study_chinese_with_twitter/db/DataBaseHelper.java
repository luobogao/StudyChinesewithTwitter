package com.luoboanalytics.study_chinese_with_twitter.db;


import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.luoboanalytics.study_chinese_with_twitter.analysis.Word;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class DataBaseHelper extends SQLiteOpenHelper {

    private static SQLiteDatabase dictionaryDB;
    private final Context myContext;
    private static final String DATABASE_NAME = "CCDICT.db";
    public final static String DATABASE_PATH = "/data/data/com.luoboanalytics.study_chinese_with_twitter/databases/";
    public static final int DATABASE_VERSION = 1;
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.myContext = context;

    }




    //Create a empty database on the system
    public void createDatabase() throws IOException
    {

        boolean dbExist = checkDataBase();

        if(dbExist)
        {
            Log.v("DB Exists", "db exists");
            // By calling this method here onUpgrade will be called on a
            // writeable database, but only if the version number has been
            // bumped
            //onUpgrade(myDataBase, DATABASE_VERSION_old, DATABASE_VERSION);
        }

        boolean dbExist1 = checkDataBase();
        if(!dbExist1)
        {
            this.getReadableDatabase();
            try
            {
                this.close();
                copyDataBase();
            }
            catch (IOException e)
            {
                throw new Error("Error copying database");
            }
        }

    }
    //Check database already exist or not
    private boolean checkDataBase()
    {
        boolean checkDB = false;
        try
        {
            String myPath = DATABASE_PATH + DATABASE_NAME;
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        }
        catch(SQLiteException e)
        {
        }
        return checkDB;
    }
    //Copies your database from your local assets-folder to the just created empty database in the system folder
    private void copyDataBase() throws IOException
    {
        Log.d("DB", "Starting copy");
        InputStream mInput = myContext.getAssets().open(DATABASE_NAME);
        Log.d("DB", "Loaded default db");
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        OutputStream mOutput = new FileOutputStream(outFileName);
        byte[] mBuffer = new byte[2024];
        int mLength;
        while ((mLength = mInput.read(mBuffer)) > 0) {
            mOutput.write(mBuffer, 0, mLength);
        }
        mOutput.flush();
        mOutput.close();
        mInput.close();
    }
    //delete database
    public void db_delete()
    {
        File file = new File(DATABASE_PATH + DATABASE_NAME);
        if(file.exists())
        {
            file.delete();
            System.out.println("delete database file.");
        }
    }
    //Open database
    public static void openDatabases() throws SQLException
    {
        String myPath = DATABASE_PATH + DATABASE_NAME;
        dictionaryDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    // Generic query function which returns all results as an array of json objects
    public static JSONArray query(String queryStr) throws SQLException{
        Cursor cursor;
        //Log.d("QUERY", "querying: " + queryStr);

        cursor = dictionaryDB.rawQuery(queryStr, null);
        //Log.d("QUERY", "found: " + Integer.toString(cursor.getCount()));

        JSONArray resultSet = new JSONArray();
        cursor.moveToFirst();
        while (cursor.isAfterLast() == false) {
            int totalColumn = cursor.getColumnCount();
            JSONObject rowObject = new JSONObject();
            for (int i = 0; i < totalColumn; i++) {
                if (cursor.getColumnName(i) != null) {
                    try {
                        rowObject.put(cursor.getColumnName(i),
                                cursor.getString(i));
                    } catch (Exception e) {
                        Log.d("DB", e.getMessage());
                    }
                }
            }
            resultSet.put(rowObject);
            cursor.moveToNext();
        }

        cursor.close();
        if (resultSet.length() == 0){
            resultSet = null;
        }
        return resultSet;
    }


    public static JSONObject queryWord(String searchWord) throws JSONException {
        JSONArray words = query("select * from dictionary where traditional = '" + searchWord + "' or simplified = '" + searchWord + "' order by frequency limit 1");

        JSONObject word = new JSONObject();
        if (words != null){
            return words.getJSONObject(0);

        }
        else
        {
            word = null;

        }
        return word;

    }

    public synchronized void closeDataBase()throws SQLException
    {
        if(dictionaryDB != null)
            dictionaryDB.close();
        super.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
        {
            Log.v("Database Upgrade", "Database version higher than old.");
            db_delete();
        }

    }

}