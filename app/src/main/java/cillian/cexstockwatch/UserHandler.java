package cillian.cexstockwatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserHandler {

    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String BARCODE = "barcode";
    public static final String TABLE_NAME = "user";
    public static final String DATA_BASE_NAME = "myDB";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_CREATE = "create table user (name text not null, " +
            "email text not null," + "barcode text not null);";

    DataBaseHelper dbhelper;
    Context ctx;
    SQLiteDatabase db;


    public UserHandler(Context ctx)
    {
        this.ctx = ctx;
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
                e.printStackTrace();
            }
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXIST stock");
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

    public long insertData(String name,String email,String barcode)
    {
        ContentValues content = new ContentValues();
        content.put(NAME,name);
        content.put(EMAIL, email);
        content.put(BARCODE, barcode);
        return db.insert(TABLE_NAME,null,content);
    }

    public int returnAmount()
    {
        return (int)DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public Cursor returnData()
    {
        return db.query(TABLE_NAME, new String[]{NAME, EMAIL, BARCODE}, null, null, null, null, null);
    }

    public boolean updateName(String oldname,String newname)
    {
        ContentValues content = new ContentValues();
        content.put(NAME,newname);
        db.update(TABLE_NAME,content,NAME + " = ?", new String[] { oldname });
        return true;
    }

    public boolean updateEmail(String oldEmail,String newEmail)
    {
        ContentValues content = new ContentValues();
        content.put(EMAIL,newEmail);
        db.update(TABLE_NAME, content, EMAIL + " = ?", new String[]{oldEmail});
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

