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
public class ChatMessagesDb {

    public  static final String KEY_USERID= "user_id";
    public  static final String KEY_CHATID= "chat_id";
    public  static final String KEY_MESSAGEID = "message_id";
    public  static final String KEY_MESSAGE = "message";
    public  static final String KEY_CREATEDAT= "created_at";
    public  static final String KEY_WHO_SENT= "sender";
    private static final String KEY_TEMP_MESSAGE_ID = "temp_message_id";
    private static final String KEY_FILEPATH = "filepath";
    private static final String KEY_TYPE = "type";
    private static final String KEY_DELIVERED = "delivered";

    //db defaults
    private   static final String DATABASE_NAME = "dekut_castle";
    private   static final String DATABASE_TABLE = "chatMessagesDb";
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
            db.execSQL("CREATE TABLE " + DATABASE_TABLE + " (" +
                            KEY_USERID + " INTEGER NOT NULL , " +
                            KEY_CHATID + " INTEGER NOT NULL, " +
                            KEY_MESSAGEID + " DEFAULT NULL, " +
                            KEY_MESSAGE + " TEXT NOT NULL, " +
                            KEY_WHO_SENT + " TEXT NOT NULL, " +
                            KEY_TYPE + " TEXT NOT NULL, " +
                            KEY_FILEPATH + " TEXT NOT NULL, " +
                            KEY_TEMP_MESSAGE_ID + " TEXT NOT NULL PRIMARY KEY, " +
                            KEY_DELIVERED + " TEXT NOT NULL, " +
                            KEY_CREATEDAT + " TIMESTAMP NOT NULL  DEFAULT CURRENT_TIMESTAMP );"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
    public ChatMessagesDb(Context c){
        ourContext=c;
    }


    public ChatMessagesDb open() throws SQLException {
        ourhelper = new DbHelper(ourContext);
        ourDatabase = ourhelper.getWritableDatabase();
        return this;
    }

    public  void close(){
        ourhelper.close();
    }

    public long createEntry( String messageId, String message, String userId, String chatId, String time, String whoSent,String type, String filepath, String tempMessageId, String delivered) {
        if(checkTempMessageId(tempMessageId)) {
            ContentValues cv = new ContentValues();
            cv.put(KEY_MESSAGEID, messageId);
            cv.put(KEY_USERID, userId);
            cv.put(KEY_CHATID, Integer.parseInt(chatId));
            cv.put(KEY_MESSAGE, message);
            cv.put(KEY_CREATEDAT, time);
            cv.put(KEY_WHO_SENT, whoSent);
            cv.put(KEY_TYPE, type);
            cv.put(KEY_TEMP_MESSAGE_ID, tempMessageId);
            cv.put(KEY_FILEPATH, filepath);
            cv.put(KEY_DELIVERED, delivered);
            System.out.println("inserted successfully");
            return ourDatabase.insert(DATABASE_TABLE, null, cv);
        }
        return 0;
    }
    private boolean checkTempMessageId(String tempMessageId) {
        String[] columns = new String[] {KEY_MESSAGEID,KEY_USERID,KEY_CHATID,KEY_MESSAGE,KEY_CREATEDAT,KEY_WHO_SENT,KEY_TYPE,KEY_FILEPATH,KEY_TEMP_MESSAGE_ID,KEY_DELIVERED};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns,KEY_TEMP_MESSAGE_ID + "="+tempMessageId,null,null,null,null);
        if(c.getCount() > 0){
            return false;
        }else{
            return true;
        }
    }
    public ArrayList getDbChatMessages(String chatId){

        String[] columns = new String[] {KEY_MESSAGEID,KEY_USERID,KEY_CHATID,KEY_MESSAGE,KEY_CREATEDAT,KEY_WHO_SENT,KEY_TYPE,KEY_FILEPATH,KEY_TEMP_MESSAGE_ID,KEY_DELIVERED};
        Cursor c = ourDatabase.rawQuery("SELECT * FROM "+DATABASE_TABLE+" WHERE "+KEY_CHATID  +" = "+chatId,null);

        int iMessage = c.getColumnIndex(KEY_MESSAGE);
        int iTime = c.getColumnIndex(KEY_CREATEDAT);
        int iWhoSent = c.getColumnIndex(KEY_WHO_SENT);
        int iMessageID = c.getColumnIndex(KEY_MESSAGEID);
        int itempMessageId = c.getColumnIndex(KEY_TEMP_MESSAGE_ID);
        int iFilePath = c.getColumnIndex(KEY_FILEPATH);
        int iType = c.getColumnIndex(KEY_TYPE);
        int iDelivered = c.getColumnIndex(KEY_DELIVERED);

        ArrayList<HashMap<String,String>> dbMessages = new ArrayList<>();

        System.out.println("from Chat Messages counts  "+c.getCount());


        if (c.getCount() > 0) {

            for (c.moveToFirst(); !c.isAfterLast(); c.moveToNext()) {
                HashMap<String,String> data = new HashMap<>();

                    data.put("message", c.getString(iMessage));
                    data.put("created_at", c.getString(iTime));
                    data.put("sender", c.getString(iWhoSent));
                    data.put("message_id", c.getString(iMessageID));
                    data.put("temp_message_id", c.getString(itempMessageId));
                    data.put("filepath", c.getString(iFilePath));
                    data.put("type", c.getString(iType));
                    data.put("delivered", c.getString(iDelivered));
                    dbMessages.add(data);

                    System.out.println("message array successfully ");
                    System.out.println(data);

            }
            return dbMessages;

        }else{
            System.out.println("No messages Array");
        }

        return null;

    }

    public String getLastDbMessageTime(){
        String[] columns = new String[] {KEY_MESSAGEID,KEY_USERID,KEY_CHATID,KEY_MESSAGE,KEY_CREATEDAT,KEY_WHO_SENT,KEY_TYPE,KEY_FILEPATH,KEY_TEMP_MESSAGE_ID,KEY_DELIVERED};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns, null, null, null, null, KEY_CREATEDAT + " DESC ", "1");
        int iTime = c.getColumnIndex(KEY_CREATEDAT);

        if(c.getCount() > 0) {
            for (c.moveToFirst(); !c.moveToLast(); c.moveToNext()) {
                System.out.println("Found last time");
                return c.getString(iTime);
            }
        }else{
            System.out.println("Did not find last message time");
        }
        return null;
    }



}
