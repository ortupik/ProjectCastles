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
public class StudentDb {
    public  static final String KEY_DEPT = "dept";
    public  static final String KEY_REGNO= "regNo";
    public  static final String KEY_GROUP = "classGroup";

    private   static final String DATABASE_NAME = "studentDb";
    private   static final String DATABASE_TABLE = "StudentsTable";
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
                          KEY_DEPT + " TEXT NOT NULL, " +
                         KEY_REGNO + " TEXT NOT NULL PRIMARY KEY, " +
                           KEY_GROUP + " TEXT NOT NULL);"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);
        }
    }
    public StudentDb(Context c){
        ourContext=c;
    }


    public StudentDb open() throws SQLException {

        ourhelper=new Dbhelper(ourContext);
        ourDatabase=ourhelper.getWritableDatabase();
        return this;
    }
    public  void close(){

        ourhelper.close();
    }
    public long createEntry(String dept, String group, String regno) {

        ContentValues cv = new ContentValues();
        cv.put(KEY_DEPT,dept);
        cv.put(KEY_REGNO,regno);
        cv.put(KEY_GROUP,group);
        return   ourDatabase.insert(DATABASE_TABLE,null,cv);

    }
    //get student details
    public ArrayList<HashMap<String,String>> getStudentData() {

        ArrayList<HashMap<String,String>> userData = new ArrayList<>();

        String[] columns = new String[]{KEY_DEPT,KEY_REGNO,KEY_GROUP};
        Cursor c = ourDatabase.query(DATABASE_TABLE, columns,null,null,null,null,null);

        int iDept = c.getColumnIndex(KEY_DEPT);
        int iRegNo = c.getColumnIndex(KEY_REGNO);
        int iGroup = c.getColumnIndex(KEY_GROUP);


        for (c.moveToFirst();!c.isAfterLast();c.moveToNext()){
            String dept = c.getString(iDept);
            String regNo = c.getString(iRegNo);
            String group = c.getString(iGroup);

            HashMap<String,String> data = new HashMap<>();
            data.put(KEY_DEPT,dept);
            data.put(KEY_GROUP,group);
            data.put(KEY_REGNO,regNo);
            userData.add(data);
        }

        return  userData;
    }

}
