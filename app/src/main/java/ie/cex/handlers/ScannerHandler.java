package ie.cex.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ScannerHandler {

    public static final String URL = "url";
    public static final String NAME = "name";
    public static final String PICURL = "picurl";
    public static final String SELL = "sell";
    public static final String CASH = "cash";
    public static final String CREDIT = "credit";
    public static final String TABLE_NAME = "scanner";
    public static final String DATA_BASE_NAME = "myDB";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_CREATE = "create table scanner (url text not null," + "name text not null," + "picurl text not null," + "sell text not null," + "cash text not null," + "credit text not null);";

    DataBaseHelper dbhelper;
    Context ctx;
    SQLiteDatabase db;


    public ScannerHandler(Context ctx)
    {
        this.ctx = ctx;
        dbhelper = new DataBaseHelper(ctx);
    }

    private class DataBaseHelper extends SQLiteOpenHelper
    {

        DataBaseHelper(Context ctx)
        {
            super(ctx,DATA_BASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try
            {
                db.execSQL(TABLE_CREATE);
            }

            catch(SQLException e)
            {
                Log.e("Error", e.getMessage());
            }
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXIST scanner");
            onCreate(db);
        }
    }

    public void createTable()
    {
        db.execSQL(TABLE_CREATE);
    }
    public ScannerHandler open()
    {
        db = dbhelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        dbhelper.close();
    }

    public long insertData(String url, String name, String picurl, String sell, String cash, String credit)
    {
        ContentValues content = new ContentValues();
        content.put(URL,url);
        content.put(NAME, name);
        content.put(PICURL,picurl);
        content.put(SELL, sell);
        content.put(CASH, cash);
        content.put(CREDIT, credit);
        return db.insertOrThrow(TABLE_NAME, null, content);
    }

    public int returnAmount()
    {
        return (int)DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public Cursor returnData()
    {
        return db.query(TABLE_NAME, new String[]{URL, NAME, PICURL, SELL, CASH, CREDIT}, null, null, null, null, null);
    }

    public void deleteList()
    {
        db.execSQL("DROP TABLE IF EXISTS scanner");
    }

}

