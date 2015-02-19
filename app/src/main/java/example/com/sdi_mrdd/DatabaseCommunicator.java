package example.com.sdi_mrdd;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin on 2/16/2015.
 */
public class DatabaseCommunicator {
    // Database fields
    private SQLiteDatabase database;
    private SQLiteHelper dbHelper;
    private String[] allColumns = { SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_CURVE, SQLiteHelper.COLUMN_WELL_DASH };

    public DatabaseCommunicator(Context context) {
        dbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public Curve createCurve(String name, String wellDash) {
        ContentValues values = new ContentValues();
        values.put(SQLiteHelper.COLUMN_CURVE, name);
        values.put(SQLiteHelper.COLUMN_WELL_DASH, wellDash);
        long insertId = database.insert(SQLiteHelper.TABLE_CURVES, null,
                values);
        Cursor cursor = database.query(SQLiteHelper.TABLE_CURVES,
                allColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        Curve newCurve = cursorToCurve(cursor);
        cursor.close();
        return newCurve;
    }

    public void deleteCurve(Curve curve) {
        long id = curve.getId();
        System.out.println("Curve deleted with id: " + id);
        database.delete(SQLiteHelper.TABLE_CURVES, SQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<Curve> getAllCurves() {
        List<Curve> curves = new ArrayList<Curve>();

        Cursor cursor = database.query(SQLiteHelper.TABLE_CURVES,
                allColumns, null, null, null, null, null);

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

    public List<Curve> getCurvesForDashboard(String wellDashName) {
        List<Curve> curves = new ArrayList<Curve>();
        Cursor cursor = database.query(SQLiteHelper.TABLE_CURVES,
                allColumns, SQLiteHelper.COLUMN_WELL_DASH + " = " + "'" + wellDashName + "'",
                null, null, null, null);

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
                allColumns, SQLiteHelper.COLUMN_WELL_DASH + " = " + "'" + wellDashName + "'",
                null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            curves.add(cursor.getString(1));
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return curves;
    }

    private Curve cursorToCurve(Cursor cursor) {
        Curve curve = new Curve(cursor.getString(1), 0);
        curve.setId(cursor.getLong(0));
        return curve;
    }
}
