package ie.cex.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHandler {

    private static final String NAME = "name";
    private static final String PIC = "pic";
    private static final String URL = "url";
    private static final String TABLE_NAME = "stock";
    private static final String DATA_BASE_NAME = "dataDB";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_CREATE = "create table stock (name text not null, " + "pic text not null," +
            "url text not null);";

    private DataBaseHelper dbhelper;
    private SQLiteDatabase db;


    public DatabaseHandler(Context ctx)
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
            db.execSQL("DROP TABLE IF EXIST stock");
            onCreate(db);
        }
    }

    public DatabaseHandler open()
    {
        db = dbhelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        dbhelper.close();
    }

    public long insertData(String name,String url,String pic)
    {
        ContentValues content = new ContentValues();
        content.put(NAME,name);
        content.put(PIC,pic);
        content.put(URL, url);
        return db.insert(TABLE_NAME,null,content);
    }

    public int returnAmount()
    {
        return (int)DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public Cursor returnData()
    {
        return db.query(TABLE_NAME, new String[]{NAME, URL,PIC}, null, null, null, null, null);
    }

    public boolean updateName(String oldname,String newname)
    {
        ContentValues content = new ContentValues();
        content.put(NAME,newname);
        db.update(TABLE_NAME,content,NAME + " = ?", new String[] { oldname });
        return true;
    }

    public void removeName(String name)
    {
        db.delete(TABLE_NAME, NAME + " = ?", new String[]{name});
    }
}