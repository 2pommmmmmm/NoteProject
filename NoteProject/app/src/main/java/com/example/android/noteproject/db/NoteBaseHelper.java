package com.example.android.noteproject.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;


import com.example.android.noteproject.db.NoteDbSchema.NoteTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


/**
 *
 */

public class NoteBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "noteDB.db";
    private Context mContenxt;
    public NoteBaseHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
        this.mContenxt = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table "+ NoteTable.NAME + "("+
                        "id integer primary key autoincrement," +
                        NoteTable.Cols.TITLE + ","+
                        NoteTable.Cols.URL +","+
                        NoteTable.Cols.CONTENT + ","+
                        NoteTable.Cols.MODIFYTIME + ")"
                );

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //---------------------------------数据备份部分

    private static String DB_PATH = "/data/data/com.example.android.noteproject/databases/";

    private boolean checkDataBase(){
        SQLiteDatabase checkDB = null;
        try{
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){}
        if(checkDB != null){
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    public void createDataBase()  {
        boolean dbExist = checkDataBase();
        if(dbExist){
            return;
        }else{
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                //throw new Error("Error copying database");
            }
        }
    }

    public void returnData(){

        try {

            File dbFile = mContenxt.getDatabasePath(DATABASE_NAME);
            File file = new File(Environment.getExternalStorageDirectory(), DATABASE_NAME);
            Log.i("test","1 " + dbFile.getAbsolutePath());
            Log.i("test","2 " + file.getAbsolutePath());
            if (!dbFile.exists()) {

                    //file.createNewFile();
                    Log.i("test","no db exites");

            }else {
                Log.i("test"," db exites");
            }
            FileInputStream is = new FileInputStream(file);
            FileOutputStream out = new FileOutputStream(dbFile,false);
            byte[] buff = new byte[1024];
            int n = 0;
            while ((n = is.read(buff)) > 0) {
                Log.e("tag", "len=" + n);
                out.write(buff, 0, n);
            }
            is.close();
            out.close();
        }catch (Exception e){

        }

    }

    private void copyDataBase() throws IOException {

        InputStream myInput = mContenxt.getAssets().open(DATABASE_NAME);
        String outFileName = DB_PATH + DATABASE_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }
}
