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
    public Curve parse(String jsonString, String trueCurveId) {
        JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
        String curveJsonArray = null;
        String curveId = trueCurveId;
        String curveName = null;
        String ivName = null;
        String dvName = null;
        String ivUnit = null;
        String dvUnit = null;
        JSONArray curveArray = null;

        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String token = jsonReader.nextName();
                /* Grab curve name field */
                if (token.equals("name")) {
                    curveName = jsonReader.nextString();
                }
                /* Grab curve iv_name field */
                else if (token.equals("iv_name")) {
                    ivName = jsonReader.nextString();
                }
                else if (token.equals("dv_name")) {
                    dvName = jsonReader.nextString();
                }
                else if (token.equals("iv_unit")) {
                    ivUnit = jsonReader.nextString();
                }
                else if (token.equals("dv_unit")) {
                    dvUnit = jsonReader.nextString();
                }
                else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            jsonReader.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse curves\n" + Log.getStackTraceString(e));
        }
        return new TimeCurve (curveId, curveName, ivName, dvName, ivUnit, dvUnit);
    }
}
