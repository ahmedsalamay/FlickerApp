package com.example.ahmed.flickr.DataModel;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Ahmed on 10/30/2017.
 */
//handle data base manipulations
public class  DBHandler {
    private  DBHelper helper;

    public DBHandler(Context context) {
        helper =new DBHelper(context);
    }

    public long insert(String url, byte[] bytes){
        SQLiteDatabase sqLiteDatabase=helper.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(PhotoContract.PhotoEntry.COLUMN_URL,url);
        contentValues.put(PhotoContract.PhotoEntry.COLUMN_BYTE_ARRAY,bytes);
        long id= sqLiteDatabase.insert(PhotoContract.PhotoEntry.TABLE_NAME,null,contentValues);
        return id;
    }
    public void delete(){
        SQLiteDatabase sqLiteDatabase=helper.getReadableDatabase();
        sqLiteDatabase.delete(PhotoContract.PhotoEntry.TABLE_NAME,null,null);
    }
    public ArrayList <PhotoEntities> getAllData(){
        SQLiteDatabase sqLiteDatabase=helper.getWritableDatabase();
        ArrayList<PhotoEntities> photoEntities =new ArrayList<>();
        String[] columns={PhotoContract.PhotoEntry.COLUMN_URL, PhotoContract.PhotoEntry.COLUMN_BYTE_ARRAY};
        Cursor cursor= sqLiteDatabase.query(PhotoContract.PhotoEntry.TABLE_NAME,columns,null,null,null,null,null);
        cursor.moveToFirst();

        while (cursor.moveToNext()){
            String url=cursor.getString(cursor.getColumnIndex(PhotoContract.PhotoEntry.COLUMN_URL));
            byte [] bytes=cursor.getBlob(cursor.getColumnIndex(PhotoContract.PhotoEntry.COLUMN_BYTE_ARRAY));
            photoEntities.add(new PhotoEntities(url,bytes));
        }
        return photoEntities;
    }
    public  byte [] getByteArray(String url ){
        SQLiteDatabase sqLiteDatabase=helper.getReadableDatabase();
        String[] columns={PhotoContract.PhotoEntry.COLUMN_URL, PhotoContract.PhotoEntry.COLUMN_BYTE_ARRAY};

        Cursor cursor= sqLiteDatabase.query(PhotoContract.PhotoEntry.TABLE_NAME,
                columns, PhotoContract.PhotoEntry.COLUMN_URL + "= ?", new String[]{url},
                null, null, null, null);
        if(cursor.getCount()>0) {
            cursor.moveToFirst();
            byte[] bytes = cursor.getBlob(cursor.getColumnIndex(PhotoContract.PhotoEntry.COLUMN_BYTE_ARRAY));
            return bytes;
        }
        return null;
    }
    public class DBHelper extends SQLiteOpenHelper {

        private static final int DATABASE_VERSION = 1;

        static final String DATABASE_NAME = "flickr.db";

        public DBHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {

            final String SQL_CREATE_PHOTOS_TABLE = "CREATE TABLE " + PhotoContract.PhotoEntry.TABLE_NAME + " (" +
                    PhotoContract.PhotoEntry.COLUMN_URL + " TXT PRIMARY KEY," +
                    PhotoContract.PhotoEntry.COLUMN_BYTE_ARRAY + " BLOB UNIQUE NOT NULL " +
                    " );";
            sqLiteDatabase.execSQL(SQL_CREATE_PHOTOS_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + PhotoContract.PhotoEntry.TABLE_NAME);
            onCreate(sqLiteDatabase);

        }
    }
}