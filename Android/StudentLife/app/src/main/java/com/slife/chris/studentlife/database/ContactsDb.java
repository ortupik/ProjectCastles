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
 * Created by sammy  && Chris on 5/20/2016.&& 6/18/2016
 */
public class ContactsDb {

    //db Initializable vars
    private   static final String DATABASE_NAME = "castle11";
    private   static final String DATABASE_TABLE = "contacts";
    private   static final int DATABASE_VERSION = 2;

    //user details
    public  static final String KEY_USER_ID = "user_id";
    public  static final String KEY_CHAT_ID = "chat_id";
    public  static final String KEY_USERNAME = "username";
    public  static final String KEY_STATUS = "status";
    public  static final String KEY_PHONE_NO = "phoneNo";
    public  static final String KEY_PROFILE_PHOTO = "profile_photo";
    public  static final String KEY_LAST_SEEN = "last_seen";




    private DbHelper ourhelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    public long createEntry(String userId, String username, String phoneNo, String status, String profilePhoto, String lastSeen) {
        if (checkUserId(userId)) {
            ContentValues cv = new ContentValues();
            cv.put(KEY_USER_ID, userId);
            cv.put(KEY_USERNAME, username);
            cv.put(KEY_STATUS, status);
            cv.put(KEY_PHONE_NO, phoneNo);
            cv.put(KEY_PROFILE_PHOTO, profilePhoto);
            cv.put(KEY_LAST_SEEN, lastSeen);
            return ourDatabase.insert(DATABASE_TABLE, null, cv);

        }else{//update user info

        }
        return 0;
    }

    private boolean checkUserId(String userId) {

        String[] columns = new String[]{KEY_USER_ID,KEY_USERNAME,KEY_STATUS,KEY_PHONE_NO,KEY_PROFILE_PHOTO,KEY_LAST_SEEN};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns,KEY_USER_ID + "="+userId,null,null,null,null);
        if(c.getCount() > 0){
            return false;
        }else{
            return true;
        }
    }


    public ArrayList<HashMap<String,String>> getData() {

        ArrayList<HashMap<String,String>> contactsData = new ArrayList<>();

        String[] columns = new String[]{KEY_USER_ID,KEY_CHAT_ID,KEY_USERNAME,KEY_STATUS,KEY_PHONE_NO,KEY_PROFILE_PHOTO,KEY_LAST_SEEN};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns,null,null,null,null,null);

        int iUserId = c.getColumnIndex(KEY_USER_ID);
        int iChatId = c.getColumnIndex(KEY_CHAT_ID);
        int iStatus = c.getColumnIndex(KEY_STATUS);
        int iUsername = c.getColumnIndex(KEY_USERNAME);
        int iPhoneNo = c.getColumnIndex(KEY_PHONE_NO);
        int iProfilePhoto = c.getColumnIndex(KEY_PROFILE_PHOTO);
        int iLastSeen = c.getColumnIndex(KEY_LAST_SEEN);

        for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            String username = c.getString(iUsername);
            String chatID = c.getString(iChatId);
            String status = c.getString(iStatus);
            String userId = c.getString(iUserId);
            String phone = c.getString(iPhoneNo);
            String profilePhoto = c.getString(iProfilePhoto);
            String lastSeen = c.getString(iLastSeen);

            HashMap<String,String> data = new HashMap<>();
            data.put(KEY_USERNAME,username);
            data.put(KEY_USER_ID,String.valueOf(userId));
            data.put(KEY_CHAT_ID,String.valueOf(chatID));
            data.put(KEY_STATUS,status);
            data.put(KEY_PHONE_NO,phone);
            data.put(KEY_PROFILE_PHOTO,profilePhoto);
            data.put(KEY_LAST_SEEN,lastSeen);
            contactsData.add(data);
        }

        return  contactsData;
    }

    //gets the info of a single user
    public ArrayList<HashMap<String,String>> getUserData(String user_id) {

        ArrayList<HashMap<String,String>> userData = new ArrayList<>();

        String[] columns = new String[]{KEY_USER_ID,KEY_CHAT_ID,KEY_USERNAME,KEY_STATUS,KEY_PHONE_NO,KEY_PROFILE_PHOTO,KEY_LAST_SEEN};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns,KEY_USER_ID + "="+user_id,null,null,null,null);

        int iStatus = c.getColumnIndex(KEY_STATUS);
        int iChatId = c.getColumnIndex(KEY_CHAT_ID);
        int iUsername = c.getColumnIndex(KEY_USERNAME);
        int iPhoneNo = c.getColumnIndex(KEY_PHONE_NO);
        int iProfilePhoto = c.getColumnIndex(KEY_PROFILE_PHOTO);
        int iLastSeen = c.getColumnIndex(KEY_LAST_SEEN);

        for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            String username = c.getString(iUsername);
            String status = c.getString(iStatus);
            String phone = c.getString(iPhoneNo);
            String profile_photo = c.getString(iProfilePhoto);
            String chatID = c.getString(iChatId);
            String lastSeen = c.getString(iLastSeen);

            HashMap<String,String> data = new HashMap<>();
            data.put(KEY_USERNAME,username);
            data.put(KEY_USER_ID,user_id);
            data.put(KEY_CHAT_ID,chatID);
            data.put(KEY_STATUS,status);
            data.put(KEY_PHONE_NO,phone);
            data.put(KEY_PROFILE_PHOTO,profile_photo);
            data.put(KEY_LAST_SEEN,lastSeen);
            userData.add(data);
        }

        return  userData;
    }

    private static class DbHelper extends SQLiteOpenHelper {


        public DbHelper(Context context) {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                    KEY_USER_ID + " TEXT NOT NULL UNIQUE, " +
                    KEY_CHAT_ID + " INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, " +
                    KEY_USERNAME + " TEXT NOT NULL, " +
                    KEY_STATUS + " DEFAULT NULL, " +
                    KEY_PHONE_NO + " TEXT NOT NULL, " +
                    KEY_PROFILE_PHOTO + " DEFAULT NULL," +
                    KEY_LAST_SEEN+" DEFAULT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
    public ContactsDb(Context c){
        ourContext=c;
    }


    public ContactsDb open() throws SQLException {
        ourhelper=new DbHelper(ourContext);
        ourDatabase=ourhelper.getWritableDatabase();
        return this;
    }
    public  void close(){
        ourhelper.close();
    }




}



