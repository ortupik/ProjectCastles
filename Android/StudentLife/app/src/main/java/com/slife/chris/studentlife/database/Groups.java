package com.slife.chris.studentlife.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by sammy on 5/20/2016.
 */
public class Groups {

    public  static final String KEY_GROUPID= "group_id";
    public  static final String KEY_GROUPNAME= "groupname";
    public  static final String KEY_CREATEDBY= "createdby";
    public  static final String KEY_CREATEDAT ="createdat";

    private   static final String DATABASE_NAME = "hotornotdb";

    private   static final String DATABASE_TABLE = "Groupstable";

    private   static final int DATABASE_VERSION = 1;



    private  Dbhelper ourhelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;



    private static class Dbhelper extends SQLiteOpenHelper {


        public Dbhelper(Context context) {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                            KEY_GROUPID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                            KEY_GROUPNAME + " TEXT NOT NULL, " +
                            KEY_CREATEDBY + " TEXT NOT NULL, " +

                            KEY_CREATEDAT + " TEXT NOT NULL );"

            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
    public Groups(Context c){

        ourContext=c;
    }


    public Groups open() throws SQLException {

        ourhelper=new Dbhelper(ourContext);
        ourDatabase=ourhelper.getWritableDatabase();
        return this;
    }
    public  void close(){

        ourhelper.close();

    }
    public long createEntry(int groupid, String gname, String createdat, String createdby) {

        ContentValues cv=new ContentValues();

        cv.put(KEY_GROUPID,groupid);
        cv.put(KEY_GROUPNAME,gname);
        cv.put(KEY_CREATEDBY,createdby);
        cv.put(KEY_CREATEDAT,createdat);
        return   ourDatabase.insert(DATABASE_TABLE,null,cv);

    }
}
