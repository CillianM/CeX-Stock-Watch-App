package ie.cex.DatabaseHandler;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler {

    public static final String NAME = "name";
    public static final String PIC = "pic";
    public static final String URL = "url";
    public static final String TABLE_NAME = "stock";
    public static final String DATA_BASE_NAME = "dataDB";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_CREATE = "create table stock (name text not null, " + "pic text not null," +
            "url text not null);";

    DataBaseHelper dbhelper;
    Context ctx;
    SQLiteDatabase db;


    public DatabaseHandler(Context ctx)
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