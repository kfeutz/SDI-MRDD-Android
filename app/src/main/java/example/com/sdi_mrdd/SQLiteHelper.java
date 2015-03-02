package example.com.sdi_mrdd;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * This class defines the database name, table names, and table structure of our
 * SQLite database
 *
 * Created by Kevin on 2/16/2015.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    /* Database Version */
    private static final int DATABASE_VERSION = 1;
    /* Database Name */
    private static final String DATABASE_NAME = "mrdd.db";

    /* Database table names */
    public static final String TABLE_CURVES = "curves";
    public static final String TABLE_PLOTS = "plots";
    public static final String TABLE_PLOTCURVES = "plotcurves";
    public static final String TABLE_DASHBOARDCURVES = "dashboardcurves";

    /* Columns for Curves Table */
    public static final String COLUMN_CURVE_KEY = "_id";
    public static final String COLUMN_CURVE = "curve";
    public static final String COLUMN_CURVE_ID = "curveId";
    public static final String COLUMN_WELL_DASH = "wellDash";

    /* Columns for Plots Table */
    public static final String COLUMN_PLOT_KEY = "_id";
    public static final String COLUMN_PLOT_NAME = "name";
    public static final String COLUMN_PLOT_WELLID = "well_id";

    /* Columns for PlotCurves Table */
    public static final String COLUMN_PLOTCURVES_KEY = "_id";
    public static final String COLUMN_PLOTCURVES_PLOT = "plot_id";
    public static final String COLUMN_PLOTCURVES_CURVE = "curve_id";

    /* Columns for DashboardCurves Table */
    public static final String COLUMN_DASHBOARDCURVES_KEY = "_id";
    public static final String COLUMN_DASHBOARDCURVES_WELL = "well_id";
    public static final String COLUMN_DASHBOARDCURVES_CURVE = "curve_id";


    /* Create table statement for Curves table */
    private static final String CREATE_TABLE_CURVES = "create table "
            + TABLE_CURVES + "(" + COLUMN_CURVE_KEY
            + " integer primary key autoincrement, " + COLUMN_CURVE_ID
            + " text not null, " + COLUMN_CURVE
            + " text not null, " + COLUMN_WELL_DASH
            + " text not null);";

    /* Create table statement for Plots table */
    private static final String CREATE_TABLE_PLOTS = "create table "
            + TABLE_PLOTS + "(" + COLUMN_PLOT_KEY
            + " integer primary key autoincrement, " + COLUMN_PLOT_NAME
            + " text not null, " + COLUMN_PLOT_WELLID
            + " text not null);";

    /* Create table statement for PlotCurves table */
    private static final String CREATE_TABLE_PLOTCURVES = "create table "
            + TABLE_PLOTCURVES + "(" + COLUMN_PLOTCURVES_KEY
            + " integer primary key autoincrement, " + COLUMN_PLOTCURVES_PLOT
            + " text not null, " + COLUMN_PLOTCURVES_CURVE
            + " text not null);";

    /* Create table statement for DasboardCurves table */
    private static final String CREATE_TABLE_DASHBOARDCURVES = "create table "
            + TABLE_DASHBOARDCURVES + "(" + COLUMN_DASHBOARDCURVES_KEY
            + " integer primary key autoincrement, " + COLUMN_DASHBOARDCURVES_WELL
            + " text not null, " + COLUMN_DASHBOARDCURVES_CURVE
            + " text not null);";

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Creates each table based on our String queries defined above.
     *
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CURVES);
        db.execSQL(CREATE_TABLE_PLOTS);
        db.execSQL(CREATE_TABLE_PLOTCURVES);
        db.execSQL(CREATE_TABLE_DASHBOARDCURVES);
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
