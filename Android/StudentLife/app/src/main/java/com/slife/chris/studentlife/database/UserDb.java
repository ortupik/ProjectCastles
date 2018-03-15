package com.slife.chris.studentlife.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sammy on 5/21/2016.
 */
public class UserDb {

    //USER DETAILS
    private  static final String KEY_USER_ID= "user_id";
    private  static final String KEY_USERNAME= "username";
    private  static final String KEY_STATUS = "status";
    private  static final String KEY_PHONE ="phoneNo";
    private  static final String KEY_PROFILE_PHOTO = "profile_photo";

    //db details
    private   static final String DATABASE_NAME = "castleDb";
    private   static final String DATABASE_TABLE = "UserDb";
    private   static final int DATABASE_VERSION = 1;

    //variables
    private DbHelper ourhelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;



    private static class DbHelper extends SQLiteOpenHelper {


        public DbHelper(Context context) {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                            KEY_USER_ID + " TEXT NOT NULL PRIMARY KEY, " +
                            KEY_USERNAME + " TEXT NOT NULL, " +
                            KEY_STATUS + " DEFAULT NULL, " +
                            KEY_PROFILE_PHOTO + " DEFAULT NULL, " +
                            KEY_PHONE + " TEXT NOT NULL );"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
    public UserDb(Context c){
        ourContext = c;
    }


    public UserDb open() throws SQLException {

        ourhelper=new DbHelper(ourContext);
        ourDatabase = ourhelper.getWritableDatabase();
        return this;
    }
    public  void close(){
        ourhelper.close();
    }
    public long createEntry(String user_id, String username, String phone, String status, String profile_photo) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_USER_ID, user_id);
        cv.put(KEY_USERNAME, username);
        cv.put(KEY_STATUS, status);
        cv.put(KEY_PHONE, phone);
        cv.put(KEY_PROFILE_PHOTO, profile_photo);
        return ourDatabase.insert(DATABASE_TABLE, null, cv);

    }

    //get user details
    public ArrayList<HashMap<String,String>> getUserData() {

        ArrayList<HashMap<String,String>> userData = new ArrayList<>();

        String[] columns = new String[]{KEY_USER_ID,KEY_USERNAME,KEY_STATUS,KEY_PHONE,KEY_PROFILE_PHOTO};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns,null,null,null,null,null);

        int iUserId = c.getColumnIndex(KEY_USER_ID);
        int iStatus = c.getColumnIndex(KEY_STATUS);
        int iUsername = c.getColumnIndex(KEY_USERNAME);
        int iPhoneNo = c.getColumnIndex(KEY_PHONE);
        int iProfilePhoto = c.getColumnIndex(KEY_PROFILE_PHOTO);

        for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            String user_id = c.getString(iUserId);
            String username = c.getString(iUsername);
            String status = c.getString(iStatus);
            String phone = c.getString(iPhoneNo);
            String profile_photo = c.getString(iProfilePhoto);

            HashMap<String,String> data = new HashMap<>();
            data.put("username",username);
            data.put("user_id",user_id);
            data.put("status",status);
            data.put("phoneNo",phone);
            data.put("profile_photo",profile_photo);
            userData.add(data);
        }

        return  userData;
    }


}
