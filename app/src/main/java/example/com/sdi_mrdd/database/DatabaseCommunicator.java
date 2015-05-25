package example.com.sdi_mrdd.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import example.com.sdi_mrdd.dataitems.Curve;
import example.com.sdi_mrdd.dataitems.Plot;
import example.com.sdi_mrdd.dataitems.TimeCurve;
import example.com.sdi_mrdd.dataitems.WellboreCurve;

/**
 * This class serves as a communicator between the SQLite database
 * and our Android activities. Contains a SQLiteDatabase and
 * SQLiteDatabase helper. This class provides methods that perform SQLite queries
 * to write and read data to and from our local database.
 *
 * Created by Kevin on 2/16/2015.
 */
public class DatabaseCommunicator {

    /* Reference to SQLite database */
    private SQLiteDatabase database;

    /* SQLite database helper instance */
    private SQLiteHelper dbHelper;

    /* Array of the curve table column names */
    private String[] allCurveColumns = { SQLiteHelper.COLUMN_CURVE_KEY,  SQLiteHelper.COLUMN_CURVE_ID,
            SQLiteHelper.COLUMN_CURVE, SQLiteHelper.COLUMN_WELL_DASH, SQLiteHelper.COLUMN_CURVE_TYPE,
            SQLiteHelper.COLUMN_CURVE_IVVAL, SQLiteHelper.COLUMN_CURVE_DVVAL};

    /* Array of the plot table column names */
    private String[] allPlotColumns = { SQLiteHelper.COLUMN_PLOT_KEY,  SQLiteHelper.COLUMN_PLOT_NAME,
            SQLiteHelper.COLUMN_PLOT_WELLID};

    /* Array of the plotcurves table column names */
    private String[] allPlotCurvesColumns = { SQLiteHelper.COLUMN_PLOTCURVES_KEY,
            SQLiteHelper.COLUMN_PLOTCURVES_CURVE, SQLiteHelper.COLUMN_PLOTCURVES_PLOT};

    /* Array of the dashboardcurves table column names */
    private String[] allDashboardCurvesColumns = { SQLiteHelper.COLUMN_DASHBOARDCURVES_KEY,
            SQLiteHelper.COLUMN_DASHBOARDCURVES_WELL, SQLiteHelper.COLUMN_DASHBOARDCURVES_CURVE};

    /**
     * Creates a new database helper based on the context. Typically pass
     * the application that owns whatever activity is creating the database.
     * Ex: this.getApplication()
     *
     * @param context   Used to open or create the database
     */
    public DatabaseCommunicator(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    /**
     * Retrieves the database reference
     *
     * @throws SQLException
     */
    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    /**
     * Closes any open database object. Important to do when activity stops and database is
     * no longer in use.
     */
    public void close() {
        dbHelper.close();
    }

    /**
     * Checks to see if the Database is already running on the thread. Typically do this
     * before using it.
     *
     * @return  True if the database is locked by the current thread, false otherwise.
     */
    public boolean isDbLockedByCurrentThread() { return database.isDbLockedByCurrentThread(); }

    /**
     * Checks to see if the Database locked on other threads. Typically do this
     * before using it.
     *
     * @return  True if the database is locked by other threads, false otherwise.
     */
    public boolean isDbLockedByOtherThreads() {return database.isDbLockedByOtherThreads();}

    /**
     * Checks to see if the database connection is open. Typically d this before opening it.
     *
     * @return  True if the database connection is already open, false otherwise.
     */
    public boolean isOpen() { return database.isOpen(); }

    /**
     * Adds a curve to the curve table in the SQLite database
     *
     * @param curveId   ID of the curve from SDI's servers
     * @param name      Name of the curve
     * @param wellDash  ID of the well the curve belongs too
     * @return  Curve   The newly created curve
     */
    public Curve createTimeCurve(String curveId, String name, String ivName, String dvName,
                             String ivUnit, String dvUnit, String wellDash, String curveType) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_CURVE_ID, curveId);
        values.put(SQLiteHelper.COLUMN_CURVE, name);
        values.put(SQLiteHelper.COLUMN_CURVE_IVNAME, ivName);
        values.put(SQLiteHelper.COLUMN_CURVE_DVNAME, dvName);
        values.put(SQLiteHelper.COLUMN_CURVE_IVUNIT, ivUnit);
        values.put(SQLiteHelper.COLUMN_CURVE_DVUNIT, dvUnit);
        values.put(SQLiteHelper.COLUMN_WELL_DASH, wellDash);
        values.put(SQLiteHelper.COLUMN_CURVE_TYPE, curveType);

        String insertCurveQuery = "INSERT OR IGNORE INTO " + SQLiteHelper.TABLE_CURVES + " ("
                + SQLiteHelper.COLUMN_CURVE_ID + ", "
                + SQLiteHelper.COLUMN_CURVE + ", "
                + SQLiteHelper.COLUMN_CURVE_IVNAME + ", "
                + SQLiteHelper.COLUMN_CURVE_DVNAME + ", "
                + SQLiteHelper.COLUMN_CURVE_IVUNIT + ", "
                + SQLiteHelper.COLUMN_CURVE_DVUNIT + ", "
                + SQLiteHelper.COLUMN_WELL_DASH + ", "
                + SQLiteHelper.COLUMN_CURVE_TYPE + ", "
                + SQLiteHelper.COLUMN_CURVE_IVVAL + ", "
                + SQLiteHelper.COLUMN_CURVE_DVVAL + ")"
                + " values ('" + curveId + "', '" + name + "', '" + ivName +
                    "', '" + dvName + "', '" + ivUnit + "', '" + dvUnit +
                    "', '" + wellDash + "', '" + curveType +
                    "', 'Loading data...', 'Loading data...');";
        Cursor cursor = database.rawQuery(insertCurveQuery, null);

        cursor.moveToFirst();
        Curve newCurve;
        /* Curve already exists */
        if (cursor.getColumnCount() == 0) {
            newCurve = new TimeCurve(curveId, name, ivName, dvName, ivUnit, dvUnit);
        }
        else {
            newCurve = cursorToCurve(cursor);
            cursor.close();
        }
        return newCurve;
    }

    public WellboreCurve createWellboreCurve(String curveId, String name, String ivName, String dvName,
                                             String ivUnit, String dvUnit, String wellDash, String curveType,
                                             String wellboreId, String wellboreType) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_CURVE_ID, curveId);
        values.put(SQLiteHelper.COLUMN_CURVE, name);
        values.put(SQLiteHelper.COLUMN_CURVE_IVNAME, ivName);
        values.put(SQLiteHelper.COLUMN_CURVE_DVNAME, dvName);
        values.put(SQLiteHelper.COLUMN_CURVE_IVUNIT, ivUnit);
        values.put(SQLiteHelper.COLUMN_CURVE_DVUNIT, dvUnit);
        values.put(SQLiteHelper.COLUMN_WELL_DASH, wellDash);
        values.put(SQLiteHelper.COLUMN_CURVE_TYPE, curveType);

        String insertCurveQuery = "INSERT OR IGNORE INTO " + SQLiteHelper.TABLE_CURVES + " ("
                + SQLiteHelper.COLUMN_CURVE_ID + ", "
                + SQLiteHelper.COLUMN_CURVE + ", "
                + SQLiteHelper.COLUMN_CURVE_IVNAME + ", "
                + SQLiteHelper.COLUMN_CURVE_DVNAME + ", "
                + SQLiteHelper.COLUMN_CURVE_IVUNIT + ", "
                + SQLiteHelper.COLUMN_CURVE_DVUNIT + ", "
                + SQLiteHelper.COLUMN_WELL_DASH + ", "
                + SQLiteHelper.COLUMN_CURVE_TYPE + ", "
                + SQLiteHelper.COLUMN_CURVE_IVVAL + ", "
                + SQLiteHelper.COLUMN_CURVE_DVVAL + ", "
                + SQLiteHelper.COLUMN_CURVE_WELLBORE_ID + ", "
                + SQLiteHelper.COLUMN_CURVE_WELLBORE_TYPE + ")"
                + " values ('" + curveId + "', '" + name + "', '" + ivName +
                "', '" + dvName + "', '" + ivUnit + "', '" + dvUnit +
                "', '" + wellDash + "', '" + curveType +
                "', 'Loading data...', 'Loading data...', '" + wellboreId +
                "', '" + wellboreType + "' );";
        Cursor cursor = database.rawQuery(insertCurveQuery, null);

        cursor.moveToFirst();
        WellboreCurve newCurve;
        /* Curve already exists */
        if (cursor.getColumnCount() == 0) {
            newCurve = new WellboreCurve(curveId, name, ivName, dvName, ivUnit, dvUnit, wellboreId, wellboreType);
        }
        else {
            newCurve = (WellboreCurve) cursorToCurve(cursor);
            cursor.close();
        }
        return newCurve;
    }

    public void deleteCurveFromDashboard(String curveId, String wellId) {
        String rawDeleteQuery = "DELETE FROM "+ SQLiteHelper.TABLE_DASHBOARDCURVES
                + " WHERE "+ SQLiteHelper.COLUMN_DASHBOARDCURVES_CURVE + " = '" + curveId
                + "' AND " + SQLiteHelper.COLUMN_DASHBOARDCURVES_WELL + " = '" + wellId + "';";

        Cursor cursor = database.rawQuery(rawDeleteQuery, null);
        cursor.moveToFirst();
        cursor.close();
    }
    /**
     * Deletes a curve from the curve table in the SQLite database
     *
     * @param curve     The Curve object to delete
     */
    public void deleteCurve(Curve curve) {
        String id = curve.getId();
        database.delete(SQLiteHelper.TABLE_CURVES, SQLiteHelper.COLUMN_CURVE_ID
                + " = " + id, null);
    }

    /**
     * Retrieves all curves from the curve table in the SQLite database
     *
     * @return  List<Curve>     List of all curves in the database
     */
    public List<Curve> getAllCurves() {
        List<Curve> curves = new ArrayList<Curve>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_CURVES,
                allCurveColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Curve tempComment = cursorToCurve(cursor);
            curves.add(tempComment);
            cursor.moveToNext();
        }
        cursor.close();
        return curves;
    }

    public void updateCurveValues(String curveId, String ivVal, String dvVal) {
        String updateQuery = "UPDATE " + SQLiteHelper.TABLE_CURVES
                + " SET " + SQLiteHelper.COLUMN_CURVE_IVVAL + "='" + ivVal
                + "', " + SQLiteHelper.COLUMN_CURVE_DVVAL + "='" + dvVal
                + "' WHERE " + SQLiteHelper.COLUMN_CURVE_ID
                + "='" + curveId + "';";
/*        String whereClause = SQLiteHelper.COLUMN_CURVE_ID + "=" + curveId;
        ContentValues args = new ContentValues();
        args.put(SQLiteHelper.COLUMN_CURVE_IVVAL, ivVal);
        args.put(SQLiteHelper.COLUMN_CURVE_DVVAL, dvVal);
        database.update(SQLiteHelper.TABLE_CURVES, args, whereClause, null);*/

        Cursor cursor = database.rawQuery(updateQuery, null);
        cursor.moveToFirst();/*
        while (!cursor.isAfterLast()) {
            Curve tempComment = cursorToCurve(cursor);
            cursor.moveToNext();
        }*/
        cursor.close();
    }

    /**
     * Adds a curve to a specific well dashboard by inserting it into the
     * dashboardcurves table.
     *
     * @param curveToAdd    The curve object to add to a well dashboard
     * @param wellId        The id of the well that owns the dashboard
     * @return  Curve       The added curve object
     */
    public Curve addCurveToDashboard(Curve curveToAdd, String wellId) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_DASHBOARDCURVES_WELL, wellId);
        values.put(SQLiteHelper.COLUMN_DASHBOARDCURVES_CURVE, curveToAdd.getId());

        long insertId = database.insert(SQLiteHelper.TABLE_DASHBOARDCURVES, null,
                values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_DASHBOARDCURVES,
                allDashboardCurvesColumns, SQLiteHelper.COLUMN_DASHBOARDCURVES_KEY + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        return curveToAdd;
    }

    /**
     * Retrieves all curve objects belonging to the passed well's dashboard
     *
     * @param wellId            The id to the well that the curves should belong to.
     * @return  List<Curve>     List of curves belonging to the passed well's dashboard.
     */
    public List<Curve> getCurvesForDashboard(String wellId) {
        List<Curve> curves = new ArrayList<Curve>();
        /* SELECT c.id, c.curveId, c.name, c.wellId FROM curves c, dashboardcurves dc,
         * WHERE c.curveId = dc.curveId
         * AND dc.plotId = wellId
         */
        String selectCurvesQuery = "SELECT "
                + "c." + SQLiteHelper.COLUMN_CURVE_KEY + ", "
                + "c." + SQLiteHelper.COLUMN_CURVE_ID + ", "
                + "c." +  SQLiteHelper.COLUMN_CURVE + ", "
                + "c." +  SQLiteHelper.COLUMN_CURVE_IVNAME + ", "
                + "c." +  SQLiteHelper.COLUMN_CURVE_DVNAME + ", "
                + "c." +  SQLiteHelper.COLUMN_CURVE_IVUNIT + ", "
                + "c." +  SQLiteHelper.COLUMN_CURVE_DVUNIT + ", "
                + "c." +  SQLiteHelper.COLUMN_WELL_DASH + ", "
                + "c." +  SQLiteHelper.COLUMN_CURVE_TYPE + ", "
                + "c." + SQLiteHelper.COLUMN_CURVE_IVVAL + ", "
                + "c." + SQLiteHelper.COLUMN_CURVE_DVVAL + ", "
                + "c." + SQLiteHelper.COLUMN_CURVE_WELLBORE_ID + ", "
                + "c." + SQLiteHelper.COLUMN_CURVE_WELLBORE_TYPE
                + " FROM " + SQLiteHelper.TABLE_CURVES + " c, " + SQLiteHelper.TABLE_DASHBOARDCURVES + " dc "
                + "WHERE c." + SQLiteHelper.COLUMN_CURVE_ID + " = " + "dc." + SQLiteHelper.COLUMN_DASHBOARDCURVES_CURVE
                + " AND dc." + SQLiteHelper.COLUMN_DASHBOARDCURVES_WELL + " = " + "'" + wellId + "';";

        Cursor cursor = database.rawQuery(selectCurvesQuery, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Curve tempComment = cursorToCurve(cursor);
            curves.add(tempComment);
            cursor.moveToNext();
        }

        cursor.close();
        return curves;
    }

    /**
     * Creates a curve based on the Cursor position. The cursor corresponds to the columns
     * on the well table, which is currently ['_id', 'curve_id', 'name', 'well_id']
     *
     * @param cursor    Cursor pointing at query result rows
     * @return          The curve corresponding to the cursor
     */
    private Curve cursorToCurve(Cursor cursor) {
        Curve curve;

        if(cursor.getString(8).equals("time_curve")) {
            curve = new TimeCurve(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6));
            curve.setId(cursor.getString(1));
            curve.setIvValue(cursor.getString(9));
            curve.setDvValue(cursor.getString(10));
        }
        else {
            curve = new WellboreCurve(cursor.getString(1), cursor.getString(2),
                    cursor.getString(3), cursor.getString(4), cursor.getString(5), cursor.getString(6),
                    cursor.getString(11), cursor.getString(12));
            curve.setId(cursor.getString(1));
            curve.setIvValue(cursor.getString(9));
            curve.setDvValue(cursor.getString(10));
        }
        return curve;
    }

    /**
     * Adds a plot to the SQLite database
     *
     * @param name      Custom name of the plot
     * @param wellId    ID to the well in which the Plot belongs
     * @return  Plot   The newly created plot
     */
    public Plot createPlot(String name, String wellId) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_PLOT_NAME, name);
        values.put(SQLiteHelper.COLUMN_PLOT_WELLID, wellId);
        long insertId = database.insert(SQLiteHelper.TABLE_PLOTS, null,
                values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_PLOTS,
                allPlotColumns, SQLiteHelper.COLUMN_PLOT_KEY + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Plot newPlot = cursorToPlot(cursor);
        cursor.close();
        return newPlot;
    }

    /**
     * Get all Plot objects stored with the well Id
     *
     * @param wellId    Id of the well to which the plot belongs
     * @return          List of plots belonging to the passed wellId
     */
    public List<Plot> getPlotsForWell(String wellId) {
        List<Plot> plots = new ArrayList<Plot>();
        Cursor cursor = database.query(SQLiteHelper.TABLE_PLOTS,
                allPlotColumns, SQLiteHelper.COLUMN_PLOT_WELLID + " = " + "'" + wellId + "'",
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Plot tempComment = cursorToPlot(cursor);
            /* Get each curve and add it to the plot */
            tempComment.setCurves(getCurvesForPlot(tempComment));
            plots.add(tempComment);
            cursor.moveToNext();
        }
        cursor.close();
        return plots;
    }

    /**
     * Retrieves a list of curves belonging to the passed plot. Helper method called by
     * getPlotsForWell(...)
     *
     * @param plot  The plot used to retrieve its curves
     * @return      An arrayList of curves belonging to the passed plot
     */
    public List<Curve> getCurvesForPlot(Plot plot) {
        List<Curve> curves = new ArrayList<Curve>();
        /* SELECT c.id, c.curveId, c.name, c.wellId FROM curves c, plotcurves pc,
         * WHERE c.curveId = pc.curveId
         * AND pc.plotId = plot.getId()
         */
        long plotId = plot.getId();
        String selectCurvesQuery = "SELECT "
                + "c." + SQLiteHelper.COLUMN_CURVE_KEY + ", "
                + "c." + SQLiteHelper.COLUMN_CURVE_ID + ", "
                + "c." +  SQLiteHelper.COLUMN_CURVE + ", "
                + "c." +  SQLiteHelper.COLUMN_CURVE_IVNAME + ", "
                + "c." +  SQLiteHelper.COLUMN_CURVE_DVNAME + ", "
                + "c." +  SQLiteHelper.COLUMN_CURVE_IVUNIT + ", "
                + "c." +  SQLiteHelper.COLUMN_CURVE_DVUNIT + ", "
                + "c." +  SQLiteHelper.COLUMN_WELL_DASH + ", "
                + "c." +  SQLiteHelper.COLUMN_CURVE_TYPE + ", "
                + "c." + SQLiteHelper.COLUMN_CURVE_IVVAL + ", "
                + "c." + SQLiteHelper.COLUMN_CURVE_DVVAL + ", "
                + "c." + SQLiteHelper.COLUMN_CURVE_WELLBORE_ID + ", "
                + "c." + SQLiteHelper.COLUMN_CURVE_WELLBORE_TYPE
                + " FROM " + SQLiteHelper.TABLE_CURVES + " c, " + SQLiteHelper.TABLE_PLOTCURVES + " pc "
                + "WHERE c." + SQLiteHelper.COLUMN_CURVE_ID + " = " + "pc." + SQLiteHelper.COLUMN_PLOTCURVES_CURVE
                + " AND pc." + SQLiteHelper.COLUMN_PLOTCURVES_PLOT + " = " +  plotId;

        Cursor cursor = database.rawQuery(selectCurvesQuery, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Curve tempComment = cursorToCurve(cursor);
            curves.add(tempComment);
            cursor.moveToNext();
        }
        cursor.close();
        return curves;
    }

    /**
     * Adds a Curve object to a Plot object by inserting a row on the plotcurves table.
     *
     * @param plot          The plot object to add a curve to.
     * @param curveToAdd    The curve object to be added to the plot.
     * @return Plot         The plot object with the curve object added to its list.
     */
    public Plot addCurveToPlot(Plot plot, Curve curveToAdd) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_PLOTCURVES_CURVE, curveToAdd.getId());
        values.put(SQLiteHelper.COLUMN_PLOTCURVES_PLOT, plot.getId());
        long insertId = database.insert(SQLiteHelper.TABLE_PLOTCURVES, null,
                values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_PLOTCURVES,
                allPlotCurvesColumns, SQLiteHelper.COLUMN_PLOTCURVES_KEY + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        plot.addCurve(curveToAdd);
        return plot;
    }

    /**
     * Converts a Cursor to a plot by retrieving indices in the cursor that
     * match the plot table rows ['_id', 'name', 'wellId']
     *
     * @param cursor    Cursor pointing to the plot table
     * @return Plot     A plot created from cursor fields
     */
    private Plot cursorToPlot(Cursor cursor) {
        Plot plot = new Plot(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
        return plot;
    }
}
