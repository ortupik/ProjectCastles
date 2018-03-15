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
 * Created by sammy on 5/20/2016.
 */
public class ChatsDb {

    //chats info
    private  static final String KEY_USERID= "user_id";
    private  static final String KEY_CHAT_ID= "chat_id";
    private  static final String KEY_USERNAME = "username";
    private  static final String KEY_PHONE_NO = "phoneNo";
    private  static final String KEY_LAST_MESSAGE = "lastMessage";
    private  static final String KEY_CREATEDAT= "time";
    public  static final String KEY_PROFILE_PHOTO = "profile_photo";
    public  static final String KEY_LAST_SEEN = "last_seen";



    //db info
    private   static final String DATABASE_NAME = "castle2";
    private   static final String DATABASE_TABLE = "chats";
    private   static final int DATABASE_VERSION = 1;



    private DbHelper ourhelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;



    private static class DbHelper extends SQLiteOpenHelper {


        public DbHelper(Context context) {
            super(context,DATABASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE  " + DATABASE_TABLE + " (" +
                            KEY_USERID + " TEXT NOT NULL PRIMARY KEY, " +
                            KEY_USERNAME + " TEXT NOT NULL, " +
                            KEY_PHONE_NO + " TEXT NOT NULL, " +
                            KEY_CHAT_ID + " TEXT NOT NULL , " +
                            KEY_PROFILE_PHOTO + " TEXT NOT NULL , " +
                            KEY_LAST_MESSAGE + " TEXT NOT NULL, " +
                            KEY_LAST_SEEN+ " TEXT DEFAULT NULL,"+
                            KEY_CREATEDAT + " TEXT NOT NULL );"
            );
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
    public ChatsDb(Context c){
        ourContext=c;
    }


    public ChatsDb open() throws SQLException {
        ourhelper=new DbHelper(ourContext);
        ourDatabase=ourhelper.getWritableDatabase();
        return this;
    }
    public  void close(){
        ourhelper.close();
    }

    public long createEntry(String chatId, String userId, String username, String phoneNo, String lastMessage, String time, String profilePhoto, String lastSeen) {
        if (checkUserId(userId)) {
            ContentValues cv = new ContentValues();
            cv.put(KEY_USERID, userId);
            cv.put(KEY_USERNAME, username);
            cv.put(KEY_PHONE_NO, phoneNo);
            cv.put(KEY_CHAT_ID, chatId);
            cv.put(KEY_PROFILE_PHOTO,profilePhoto);
            cv.put(KEY_LAST_MESSAGE, lastMessage);
            cv.put(KEY_CREATEDAT, time);
            cv.put(KEY_LAST_SEEN, lastSeen);

            System.out.println("inserted successfully");
            return ourDatabase.insert(DATABASE_TABLE, null, cv);
        }else{
            updateChat(chatId,userId,username,phoneNo,lastMessage, time,profilePhoto,lastSeen);
            return 0;
        }
    }

    private void updateChat(String chatId, String userId, String username, String phoneNo, String lastMessage, String time, String profilePhoto, String lastSeen) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_USERID, userId);
        cv.put(KEY_USERNAME, username);
        cv.put(KEY_PHONE_NO, phoneNo);
        cv.put(KEY_CHAT_ID, chatId);
        cv.put(KEY_LAST_MESSAGE, lastMessage);
        cv.put(KEY_LAST_MESSAGE, lastMessage);
        cv.put(KEY_CREATEDAT, time);
        cv.put(KEY_PROFILE_PHOTO,profilePhoto);
        cv.put(KEY_LAST_SEEN, lastSeen);
        ourDatabase.update(DATABASE_TABLE,cv,KEY_USERID+"="+userId,null);
    }

    private boolean checkUserId(String userID) {
        String[] columns = new String[] {KEY_USERID,KEY_USERNAME,KEY_PHONE_NO,KEY_CHAT_ID,KEY_PROFILE_PHOTO, KEY_LAST_MESSAGE,KEY_CREATEDAT,KEY_LAST_SEEN};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns,KEY_USERID + "="+userID,null,null,null,null);
        if(c.getCount() > 0){
            return false;
        }else{
            return true;
        }
    }

    public ArrayList<HashMap<String,String>> getDbChats(){

        ArrayList<HashMap<String,String>> dbMessages = new ArrayList<>();

        String[] columns = new String[] {KEY_USERID,KEY_USERNAME,KEY_PHONE_NO,KEY_CHAT_ID,KEY_PROFILE_PHOTO, KEY_LAST_MESSAGE,KEY_CREATEDAT,KEY_LAST_SEEN};
        Cursor c =ourDatabase.query(DATABASE_TABLE,columns,null,null,null,null,null);

        int iMessage = c.getColumnIndex(KEY_LAST_MESSAGE);
        int iTime = c.getColumnIndex(KEY_CREATEDAT);
        int iUsername = c.getColumnIndex(KEY_USERNAME);
        int iPhoneNo = c.getColumnIndex(KEY_PHONE_NO);
        int iUserId = c.getColumnIndex(KEY_USERID);
        int iProfilePhoto = c.getColumnIndex(KEY_PROFILE_PHOTO);
        int iLastSeen = c.getColumnIndex(KEY_LAST_SEEN);


        System.out.println("from Chats counts  "+c.getCount());


        if (c.getCount() > 0) {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {

                HashMap<String,String> data = new HashMap<>();
                data.put(KEY_LAST_MESSAGE, c.getString(iMessage));
                data.put(KEY_CREATEDAT, c.getString(iTime));
                data.put(KEY_USERNAME, c.getString(iUsername));
                data.put(KEY_PHONE_NO, c.getString(iPhoneNo));
                data.put(KEY_USERID, c.getString(iUserId));
                data.put(KEY_PROFILE_PHOTO,c.getString(iProfilePhoto));
                data.put(KEY_LAST_SEEN,c.getString(iLastSeen));
                dbMessages.add(data);

                   // System.out.println("db Chats ");
                  //  System.out.println(data);
            }
            return dbMessages;

        }else{
            System.out.println("No dbChats");
        }

        return null;

    }

}
