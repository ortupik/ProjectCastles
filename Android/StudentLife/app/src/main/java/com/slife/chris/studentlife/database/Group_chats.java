package com.slife.chris.studentlife.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by sammy on 5/20/2016.
 */
public class Group_chats {

    public  static final String KEY_GROUPID= "group_id";
    public  static final String KEY_USERID= "user_id";
    public  static final String KEY_MESSAGEID= "message_id";
    public  static final String KEY_MESSAGE= "message";
    public  static final String KEY_CREATEDAT= "createdat";

    private   static final String DATABASE_NAME = "hotornotdb";

    private   static final String DATABASE_TABLE = "Groups_chatstable";

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
                            KEY_USERID + " INTEGER NOT NULL, " +
                            KEY_GROUPID + " INTEGER NOT NULL, " +
                            KEY_MESSAGE + " TEXT NOT NULL, " +
                            KEY_MESSAGEID + " INTEGER NOT NULL, " +

                            KEY_CREATEDAT + " );"

            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
    public Group_chats(Context c){

        ourContext=c;
    }


    public Group_chats open() throws SQLException {

        ourhelper=new Dbhelper(ourContext);
        ourDatabase=ourhelper.getWritableDatabase();
        return this;
    }
    public  void close(){

        ourhelper.close();
    }
    public long createEntry(int user1id, int groupid, String message, int messageid, int time) {

        ContentValues cv=new ContentValues();
        cv.put(KEY_USERID,user1id);
        cv.put(KEY_GROUPID,groupid);
        cv.put(KEY_MESSAGE,message);
        cv.put(KEY_MESSAGEID,messageid);
        cv.put(KEY_CREATEDAT,time);
        return   ourDatabase.insert(DATABASE_TABLE,null,cv);

    }



}



