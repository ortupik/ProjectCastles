package com.slife.chris.studentlife.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;

/**
 * Created by sammy on 5/20/2016.
 */
public class Group_chat_room {


    public  static final String KEY_GROUPID= "group_id";
    public  static final String KEY_USERID= "user_id";
    public  static final String KEY_ROLE= "role";
    public  static final String KEY_ID ="id";

    private   static final String DATABASE_NAME = "hotornotdb";

    private   static final String DATABASE_TABLE = "Group_chat_roomtable";

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
                            KEY_USERID + " INTEGER NOT NULL, " +
                            KEY_ROLE + " INTEGER NOT NULL, " +
                            KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                             " );"

            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
    public Group_chat_room(Context c){

        ourContext=c;
    }


    public Group_chat_room open() throws SQLException {

        ourhelper=new Dbhelper(ourContext);
        ourDatabase=ourhelper.getWritableDatabase();
        return this;
    }
    public  void close(){

        ourhelper.close();

    }
    public long createEntry(int groupid, String role, int id, int userid) {

        ContentValues cv=new ContentValues();

        cv.put(KEY_GROUPID,groupid);
        cv.put(KEY_ROLE,role);
        cv.put(KEY_ID,id);
        cv.put(KEY_USERID,userid);
        return   ourDatabase.insert(DATABASE_TABLE,null,cv);

    }
}
