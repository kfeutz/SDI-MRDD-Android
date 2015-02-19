package example.com.sdi_mrdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Kevin on 2/16/2015.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_CURVES = "curves";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_CURVE = "curve";
    public static final String COLUMN_WELL_DASH = "wellDash";

    private static final String DATABASE_NAME = "mrdd.db";
    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_CREATE = "create table "
            + TABLE_CURVES + "(" + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_CURVE
            + " text not null, " +  COLUMN_WELL_DASH
            + " text not null);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(SQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURVES);
        onCreate(db);
    }
}
