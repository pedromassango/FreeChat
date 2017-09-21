package com.aman.freechat.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by Aman on 1/8/2016.
 */
public class DBHelper extends SQLiteOpenHelper {
    private static String DB_PATH = "";
    private  String DB_NAME = "";
    private SQLiteDatabase mDatabase;
    private Context mContext;

    public DBHelper(Context context, String DB_NAME) {
        super(context, DB_NAME, null, 1);
        this.DB_NAME = DB_NAME;

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            //DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
            DB_PATH = context.getDatabasePath(DB_NAME).getPath();
        }
        this.mContext = context;

        try {
            createDataBase();
        } catch (Exception ex) {
            Log.e("FreeChat", ex.toString() + "  UnableToCreateDatabase");
            throw new Error("UnableToCreateDatabase");
        }
    }

    private void createDataBase() {
        Log.e("created","db");
        boolean mDatabaseExists = checkDatabase();
        if (!mDatabaseExists) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDatabaseFromAssets();
                Log.e("CONTRA", "created");
            } catch (IOException e) {
                throw new Error("Error creating database\n" + e.getMessage());
            }
        } else {
            Log.e("CONTRA", "Exists " + DB_NAME);
        }
    }

    //copy database from assets
    private void copyDatabaseFromAssets() throws IOException {
         Log.e("copy database", "check path " + DB_PATH);
         Log.e("copy database", " FILENAME " + DB_PATH + DB_NAME);

        InputStream mInput = mContext.getAssets().open("freechat");
        String fileName = DB_PATH + DB_NAME;
        OutputStream output = new FileOutputStream(fileName);
        byte[] mBuffer = new byte[1024];
        int length;
        while ((length = mInput.read(mBuffer)) > 0) {
            output.write(mBuffer, 0, length);
        }

        output.flush();
        output.close();
        mInput.close();
    }

    //open database to query
    public boolean openDatabase() {
        String path = DB_PATH + DB_NAME;
        File file = new File(path);
        if (file.exists() && !file.isDirectory())
            mDatabase = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.CREATE_IF_NECESSARY);

        return mDatabase != null;
    }

    //check if database exists /data/data/package Name/db Name
    private boolean checkDatabase() {
        File dbFile = new File(DB_PATH + DB_NAME);
        Log.d("DB_FILE", dbFile + " " + dbFile.exists());

        return dbFile.exists();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public static void exportDatabse(String databaseName) {
        try {
            File sd = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String currentDBPath = DB_PATH;
                String backupDBPath = "freechat";
                File currentDB = new File(currentDBPath, databaseName);
                File backupDB = new File(sd, backupDBPath);

                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                }
        } catch (Exception e) {
            Log.e("DBER", "exportDatabse: " + e.getMessage());
            e.printStackTrace();
        }
    }
}