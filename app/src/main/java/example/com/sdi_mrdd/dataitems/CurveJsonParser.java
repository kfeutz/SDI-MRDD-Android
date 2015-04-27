package example.com.sdi_mrdd.dataitems;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kevin on 3/11/2015.
 */
public class CurveJsonParser {
    /* Instance to itself (Singleton) */
    private static final CurveJsonParser instance = new CurveJsonParser();

    /* Class tag for Log cat */
    private static final String TAG = "CurveJsonParser";

    /**
     * Retrieves the instance of the curve json parser
     *
     * @return  CurveJsonParser     The curve json parser object
     */
    public static CurveJsonParser getInstance() {
        return instance;
    }

    /**
     * Converts a json string to a list of Curve objects
     *
     * @param jsonString        The JSON representation of the list of curves
     * @return  List<Curve>     A List containing the Curves from the JSON string.
     */
    /*@Override*/
    public Curve parse(String jsonString, String trueCurveId, String curveToCreateName) {
        JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
        String curveId = trueCurveId;
        String curveName = curveToCreateName;
        String ivName = null;
        String dvName = null;
        String ivUnit = "";
        String dvUnit = "";

        try {
            jsonReader.beginArray();
            /* Loop through json array twice to avoid unnecessary attributes */
            jsonReader.skipValue();
            jsonReader.skipValue();
            /* Start array to grab iv and dv names */
            jsonReader.beginArray();
            ivName = jsonReader.nextString();
            dvName = jsonReader.nextString();

            jsonReader.endArray();
            jsonReader.endArray();
            jsonReader.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse curves\n" + Log.getStackTraceString(e));
        }
        return new TimeCurve (curveId, curveName, ivName, dvName, ivUnit, dvUnit);
    }
}
