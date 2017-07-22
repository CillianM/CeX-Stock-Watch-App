package ie.cex.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserHandler {

    private static final String URL = "url";
    private static final String NAME = "name";
    private static final String BARCODE = "barcode";
    private static final String TABLE_NAME = "user";
    private static final String DATA_BASE_NAME = "myDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CREATE = "create table user (url text not null," + "name text not null," + "barcode text not null);";

    private DataBaseHelper dbhelper;
    private SQLiteDatabase db;


    public UserHandler(Context ctx)
    {
        dbhelper = new DataBaseHelper(ctx);
    }

    private static class DataBaseHelper extends SQLiteOpenHelper
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
            db.execSQL("DROP TABLE IF EXIST user");
            onCreate(db);
        }
    }

    public UserHandler open()
    {
        db = dbhelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        dbhelper.close();
    }

    public long insertData(String url, String name, String barcode)
    {
        ContentValues content = new ContentValues();
        content.put(URL,url);
        content.put(NAME, name);
        content.put(BARCODE, barcode);
        return db.insertOrThrow(TABLE_NAME, null, content);
    }

    public int returnAmount()
    {
        return (int)DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public Cursor returnData()
    {
        return db.query(TABLE_NAME, new String[]{URL, NAME, BARCODE}, null, null, null, null, null);
    }

    public boolean updateName(String oldName, String newName)
    {
        ContentValues content = new ContentValues();
        content.put(NAME, newName);
        db.update(TABLE_NAME, content, NAME + " = ?", new String[]{oldName});
        return true;
    }

    public boolean updateBarcode(String oldBarcode,String newBarcode)
    {
        ContentValues content = new ContentValues();
        content.put(BARCODE,newBarcode);
        db.update(TABLE_NAME, content, BARCODE + " = ?", new String[]{oldBarcode});
        return true;
    }

}

