package example.com.sdi_mrdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2/16/2015.
 */
public class DatabaseCommunicator {
    // Database fields
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allCurveColumns = { SQLiteHelper.COLUMN_CURVE_KEY,  SQLiteHelper.COLUMN_CURVE_ID,
            SQLiteHelper.COLUMN_CURVE, SQLiteHelper.COLUMN_WELL_DASH };

    private String[] allPlotColumns = { SQLiteHelper.COLUMN_PLOT_KEY,  SQLiteHelper.COLUMN_PLOT_NAME,
            SQLiteHelper.COLUMN_PLOT_WELLID};

    private String[] allPlotCurvesColumns = { SQLiteHelper.COLUMN_PLOTCURVES_KEY,  SQLiteHelper.COLUMN_PLOTCURVES_CURVE,
            SQLiteHelper.COLUMN_PLOTCURVES_PLOT};

    private String[] allDashboardCurvesColumns = { SQLiteHelper.COLUMN_DASHBOARDCURVES_KEY,  SQLiteHelper.COLUMN_DASHBOARDCURVES_WELL,
            SQLiteHelper.COLUMN_DASHBOARDCURVES_CURVE};

    public DatabaseCommunicator(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    /**
     * Adds a curve to the SQLite database
     * @param curveId   ID of the curve from SDI's servers
     * @param name      Name of the curve
     * @param wellDash  ID of the well the curve belongs too
     * @return  Curve   The newly created curve
     */
    public Curve createCurve(String curveId, String name, String wellDash) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_CURVE_ID, curveId);
        values.put(SQLiteHelper.COLUMN_CURVE, name);
        values.put(SQLiteHelper.COLUMN_WELL_DASH, wellDash);
        long insertId = database.insert(SQLiteHelper.TABLE_CURVES, null,
                values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_CURVES,
                allCurveColumns, SQLiteHelper.COLUMN_CURVE_KEY + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Curve newCurve = cursorToCurve(cursor);
        cursor.close();
        return newCurve;
    }

    public void deleteCurve(Curve curve) {
        String id = curve.getId();
        System.out.println("Curve deleted with id: " + id);
        database.delete(SQLiteHelper.TABLE_CURVES, SQLiteHelper.COLUMN_CURVE_ID
                + " = " + id, null);
    }

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
        // make sure to close the cursor
        cursor.close();
        return curves;
    }

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

    public List<Curve> getCurvesForDashboard(String wellId) {
        List<Curve> curves = new ArrayList<Curve>();
        /* SELECT c.id, c.curveId, c.name, c.wellId FROM curves c, dashboardcurves pc,
         * WHERE c.curveId = pc.curveId
         * AND pc.plotId = plot.getId()
         */
        String selectCurvesQuery = "SELECT "
                + "c." + SQLiteHelper.COLUMN_CURVE_KEY + ", " + "c." + SQLiteHelper.COLUMN_CURVE_ID
                + ", " + "c." +  SQLiteHelper.COLUMN_CURVE + ", " + "c." +  SQLiteHelper.COLUMN_WELL_DASH
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
        // make sure to close the cursor
        cursor.close();
        return curves;
    }

    public ArrayList<String> getCurveStringsForDashboard(String wellDashName) {
        ArrayList<String> curves = new ArrayList<String>();
        Cursor cursor = database.query(SQLiteHelper.TABLE_CURVES,
                allCurveColumns, SQLiteHelper.COLUMN_WELL_DASH + " = " + "'" + wellDashName + "'",
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            curves.add(cursor.getString(2));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return curves;
    }

    private Curve cursorToCurve(Cursor cursor) {
        Curve curve = new TimeCurve(cursor.getString(1), cursor.getString(2));
        curve.setId(cursor.getString(1));
        return curve;
    }

    /**
     * Adds a plot to the SQLite database
     * @param name      Custome name of the plot
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
            tempComment.setCurves(getCurvesForPlot(tempComment));
            plots.add(tempComment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return plots;
    }

    /**
     * Retrieves a list of curves belonging to the passed plot
     * @param plot  The plot used to retrieve its curves
     * @return      An arrayList of curves belong to the passed plot
     */
    public List<Curve> getCurvesForPlot(Plot plot) {
        List<Curve> curves = new ArrayList<Curve>();
        String curveColumn[] = {SQLiteHelper.COLUMN_PLOTCURVES_CURVE};
        /* SELECT c.id, c.curveId, c.name, c.wellId FROM curves c, plotcurves pc,
         * WHERE c.curveId = pc.curveId
         * AND pc.plotId = plot.getId()
         */
        String selectCurvesQuery = "SELECT "
                + "c." + SQLiteHelper.COLUMN_CURVE_KEY + ", " + "c." + SQLiteHelper.COLUMN_CURVE_ID
                + ", " + "c." +  SQLiteHelper.COLUMN_CURVE + ", " + "c." +  SQLiteHelper.COLUMN_WELL_DASH
                + " FROM " + SQLiteHelper.TABLE_CURVES + " c, " + SQLiteHelper.TABLE_PLOTCURVES + " pc "
                + "WHERE c." + SQLiteHelper.COLUMN_CURVE_ID + " = " + "pc." + SQLiteHelper.COLUMN_PLOTCURVES_CURVE
                + " AND pc." + SQLiteHelper.COLUMN_PLOTCURVES_PLOT + " = " + "'" + plot.getId() + "';";

        Cursor cursor = database.rawQuery(selectCurvesQuery, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Curve tempComment = cursorToCurve(cursor);
            curves.add(tempComment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return curves;
    }

    public List<Plot> getAllPlots() {
        List<Plot> plots = new ArrayList<Plot>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_PLOTS,
                allPlotColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Plot tempComment = cursorToPlot(cursor);
            plots.add(tempComment);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return plots;
    }

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

    private Plot cursorToPlot(Cursor cursor) {
        Plot plot = new Plot(cursor.getInt(0), cursor.getString(1), cursor.getString(2));
        return plot;
    }
}
