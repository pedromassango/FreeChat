package com.aman.freechat.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.aman.freechat.utils.AppUtility;

/**
 * Created by Aman on 1/8/2016.
 */
public final class Db {
    private static final String DB_NAME = "freechat";
    private static SQLiteDatabase sqLiteDatabase;
    private static Db instance;
    private DBHelper dbHelper;

    private Db() {

    }

    public static Db getInstance() {
        if (instance == null) {
            instance = new Db();
        }

        return instance;
    }

    public SQLiteDatabase getDb() {
        return sqLiteDatabase;
    }

    public void beginTransaction() {
        sqLiteDatabase.beginTransaction();
    }

    public void setTransactionSuccessful() {
        sqLiteDatabase.setTransactionSuccessful();
    }

    public void endTransaction() {
        sqLiteDatabase.endTransaction();
    }

    public Cursor rawQuery(String sql) {
        return sqLiteDatabase.rawQuery(sql, null);
    }

    // public static void rollBack() { sqLiteDatabase.r}

    public Cursor rawQuery(String sql, String[] args) {
        return sqLiteDatabase.rawQuery(sql, args);
    }

    public void execSql(String sql) {
        sqLiteDatabase.execSQL(sql);
    }

    public void execSql(String sql, String[] args) {
        sqLiteDatabase.execSQL(sql, args);
    }

    public long insert(String table, String nullColumnHack, ContentValues contentValues) {
        return sqLiteDatabase.insert(table, nullColumnHack, contentValues);
    }

    public int update(String table, ContentValues contentValues, String whereClause, String[] whereArgs) {
        return sqLiteDatabase.update(table, contentValues, whereClause, whereArgs);
    }

    int delete(String table, String whereClause, String[] whereArgs) {
        return sqLiteDatabase.delete(table, whereClause, whereArgs);
    }

    /**
     * <p>
     *     initialize the database
     * </p>
     *
     * <p>
     *     Note : never call getWritableDatabase()/getReadableDatabase() on a UI thread. Even though our database isn't large enough
     *     it is a good practice to call this on a separate thread.
     * </p>
     * @param context To show the loading dialog
     */
    public void init(final Context context) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                AppUtility.showDialog(context);
            }

            @Override
            protected Void doInBackground(Void... params) {
                if (dbHelper == null) {
                    dbHelper = new DBHelper(context, DB_NAME);
                    dbHelper.openDatabase();
                    dbHelper.close();
                    sqLiteDatabase = dbHelper.getWritableDatabase();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                AppUtility.dismissDialog();
            }
        }.execute();
    }
}