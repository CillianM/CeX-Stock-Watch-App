package ie.cex.DatabaseHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserHandler {

    public static final String URL = "url";
    public static final String EMAIL = "email";
    public static final String PASSWORD = "password";
    public static final String BARCODE = "barcode";
    public static final String TABLE_NAME = "user";
    public static final String DATA_BASE_NAME = "myDB";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_CREATE = "create table user (url text not null," + "password text not null," + "email text not null," + "barcode text not null);";

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

    public long insertData(String url,String email,String password,String barcode)throws Exception
    {
        ContentValues content = new ContentValues();
        content.put(URL,url);
        content.put(EMAIL, email);
        content.put(PASSWORD,password);
        content.put(BARCODE, barcode);
        return db.insertOrThrow(TABLE_NAME, null, content);
    }

    public int returnAmount()
    {
        return (int)DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public Cursor returnData()
    {
        return db.query(TABLE_NAME, new String[]{URL, EMAIL,PASSWORD, BARCODE}, null, null, null, null, null);
    }


    public boolean updateURL(String oldURL,String newURL)
    {
        ContentValues content = new ContentValues();
        content.put(URL,newURL);
        db.update(TABLE_NAME,content,URL + " = ?", new String[] { oldURL });
        return true;
    }

    public boolean updateEmail(String oldEmail,String newEmail)
    {
        ContentValues content = new ContentValues();
        content.put(EMAIL,newEmail);
        db.update(TABLE_NAME, content, EMAIL + " = ?", new String[]{oldEmail});
        return true;
    }

    public boolean updatePassword(String oldPass,String newPass)
    {
        ContentValues content = new ContentValues();
        content.put(PASSWORD,newPass);
        db.update(TABLE_NAME, content, PASSWORD + " = ?", new String[]{oldPass});
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

