package com.example.adk_recorder.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.adk_recorder.Interfaces.OnDatabaseChangeListener;
import com.example.adk_recorder.Models.RecordingItem;

import java.util.ArrayList;
import java.util.Currency;

public class DBHelper extends SQLiteOpenHelper {

    private Context context;
    private static final String DATABASE_NAME = "saved_recordings.db";//static
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "saved_recording_table";

    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PATH = "path";
    public static final String COLUMN_LENGTH = "length";
    public static final String COLUMN_TIME_ADDED = "time_added";

    private static OnDatabaseChangeListener OnDatabaseChangeListener;

    private static final String COMA_SEP = ",";


    private static final String SQLITE_CREATE_TABLE= "CREATE TABLE " + TABLE_NAME + " ("+ "id INTEGER PRIMARY KEY" +
            " AUTOINCREMENT"+COMA_SEP +
            COLUMN_NAME + " TEXT"+ COMA_SEP +
            COLUMN_PATH + " TEXT" + COMA_SEP +
            COLUMN_LENGTH+ " INTEGER" + COMA_SEP +
            COLUMN_TIME_ADDED + " INTEGER" + ")";
    public DBHelper(Context context)
    {
        super(context,DATABASE_NAME,null, DATABASE_VERSION);
        this.context=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLITE_CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " +TABLE_NAME );

    }

    public boolean addRecording(RecordingItem recordingItem){
        try
        {
            SQLiteDatabase db = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_NAME,recordingItem.getName());
            contentValues.put(COLUMN_PATH,recordingItem.getPath());
            contentValues.put(COLUMN_LENGTH,recordingItem.getLength());
            contentValues.put(COLUMN_TIME_ADDED,recordingItem.getTime_added());

            db.insert(TABLE_NAME,null, contentValues);

            if(OnDatabaseChangeListener!=null)
            {
                OnDatabaseChangeListener.onNewDatabaseEntryAdded(recordingItem);
            }

            return true;
        }catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }

    }

    public ArrayList<RecordingItem> getAllAudiios()
    {
        ArrayList<RecordingItem> arrayList = new ArrayList<>();
        SQLiteDatabase db= this.getReadableDatabase();

        Cursor cursor=db.rawQuery("select * from " + TABLE_NAME,null);
        if(cursor!=null)
        {
            while(cursor.moveToNext())
            {
                String name = cursor.getString(1);
                String path = cursor.getString(2);
                int length = (int)cursor.getLong(3);
                Long timeAdded= cursor.getLong(4);

                RecordingItem recordingItem=new RecordingItem(name,path,length,timeAdded);
                arrayList.add(recordingItem);
            }
            cursor.close();
            return  arrayList;
        }
        else
        {
            return  null;
        }
    }
    public static void setOnDatabaseChangeListener(OnDatabaseChangeListener listener)
    {
        OnDatabaseChangeListener = listener;
    }
}
