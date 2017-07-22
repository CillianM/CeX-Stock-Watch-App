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

    private static final String URL = "url";
    private static final String NAME = "name";
    private static final String PICURL = "picurl";
    private static final String SELL = "sell";
    private static final String CASH = "cash";
    private static final String CREDIT = "credit";
    private static final String TABLE_NAME = "scanner";
    private static final String DATA_BASE_NAME = "myDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CREATE = "create table scanner (url text not null," + "name text not null," + "picurl text not null," + "sell text not null," + "cash text not null," + "credit text not null);";

    private DataBaseHelper dbhelper;
    private SQLiteDatabase db;


    public ScannerHandler(Context ctx)
    {
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

