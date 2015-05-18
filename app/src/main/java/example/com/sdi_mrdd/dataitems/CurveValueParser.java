package example.com.sdi_mrdd.dataitems;

import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;

import org.json.JSONArray;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Kevin on 3/11/2015.
 */
public class CurveValueParser {
    /* Instance to itself (Singleton) */
    private static final CurveValueParser instance = new CurveValueParser();

    /* Class tag for Log cat */
    private static final String TAG = "CurveValueParser";

    public static CurveValueParser getInstance() {
        return instance;
    }

    /**
     * To get latest curve values, parse the start and end iv of initial curve call.
     * Then, call again with start param defined as the oldIv field.
     * @param jsonString
     * @return
     */
    public Map<String, String> getLastIvValue(String jsonString) {
        Map<String, String> recentCurveValues = new HashMap<>();
        try {
            JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
            jsonReader.beginArray();
            /* Skip first array of points */
            jsonReader.skipValue();
            /* Read oldIv from json */
            /* Map values do not exist so we are at the most recent values */
            if(jsonReader.peek() == JsonToken.NULL) {
                recentCurveValues = parseRecentValues(jsonString);
                jsonReader.skipValue();
            }
            else {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String token = jsonReader.nextName();
                    /* Grab curve id field */
                    if (token.equals("startIV")) {
                        jsonReader.nextString();
                    }
                    else if (token.equals("oldIV")) {
                        recentCurveValues.put("ivValue", jsonReader.nextString());
                    }
                }
                jsonReader.endObject();
            }
            /* Skip array of iv and dv names */
            jsonReader.skipValue();
            jsonReader.endArray();
            jsonReader.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse curves\n" + Log.getStackTraceString(e));
        }
        return recentCurveValues;
    }

    public Map<String, String> parseRecentValues(String jsonString) {
        Map<String, String> recentCurveValues = new HashMap<>();
        JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
        ArrayList<String> ivValues = new ArrayList<>();
        ArrayList<String> dvValues = new ArrayList<>();

        try {
            jsonReader.beginArray();
            /* Start array of points */
            jsonReader.beginArray();
            while(jsonReader.hasNext()) {
                /* Begin one point array */
                jsonReader.beginArray();
                /* First value in point array is ivValue per API */
                ivValues.add(jsonReader.nextString());
                /* Second value in point array is dvValue per API */
                dvValues.add(jsonReader.nextString());
                jsonReader.endArray();
            }
            /* End array of points */
            jsonReader.endArray();
            /* Map ranges do not exist*/
            jsonReader.skipValue();
            /* Skip array of iv and dv names */
            jsonReader.skipValue();

            jsonReader.endArray();
            jsonReader.close();

            /* Grab last iv and dv values in array */
            if(ivValues.size() - 1 >= 0) {
                recentCurveValues.put("ivValue", ivValues.get(ivValues.size() - 1));
                recentCurveValues.put("dvValue", dvValues.get(dvValues.size() - 1));
            }
            /* No data retrived from sdi server */
            else {
                recentCurveValues.put("ivValue", "No data");
                recentCurveValues.put("dvValue", "No data");
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse curves\n" + Log.getStackTraceString(e));
        }
        return recentCurveValues;
    }

    /* Return true if all of the curve's data has been returned */
    public boolean parseIvDvValues(Curve curveToChange, String jsonString) {
        JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
        ArrayList<String> ivValues = new ArrayList<>();
        ArrayList<String> dvValues = new ArrayList<>();
        String oldNextStartUnit = "";
        try {
            jsonReader.beginArray();
            /* Start array of points */
            jsonReader.beginArray();
            while(jsonReader.hasNext()) {
                /* Begin one point array */
                jsonReader.beginArray();
                /* First value in point array is ivValue per API */
                ivValues.add(jsonReader.nextString());
                /* Second value in point array is dvValue per API */
                dvValues.add(jsonReader.nextString());
                jsonReader.endArray();
            }
            /* End array of points */
            jsonReader.endArray();
            /* Map values do not exist */
            if(jsonReader.peek() == JsonToken.NULL) {
                oldNextStartUnit = curveToChange.getNextStartUnit();
                jsonReader.skipValue();
            }
            else {
                jsonReader.beginObject();
                oldNextStartUnit = curveToChange.getNextStartUnit();
                while (jsonReader.hasNext()) {
                    String token = jsonReader.nextName();
                    /* Grab curve id field */
                    if (token.equals("startIV")) {
                        curveToChange.setNextStartUnit(jsonReader.nextString());
                    }
                    else if (token.equals("oldIV")) {
                        curveToChange.setNextEndUnit(jsonReader.nextString());
                    }
                }
                jsonReader.endObject();
            }
            /* Skip array of iv and dv names */
            jsonReader.skipValue();

            jsonReader.endArray();
            jsonReader.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse curves\n" + Log.getStackTraceString(e));
        }
        /* End of points in SDI database */
        if (curveToChange.getNextStartUnit().equals(oldNextStartUnit)) {
            /* If the curve only returns one set of points then we need to add
             * the points to the curve
             */
            if(curveToChange.getIvValues().size() == 0) {
                curveToChange.setIvValues(ivValues);
                curveToChange.setDvValues(dvValues);
            }
            return true;
        }
        else {
            curveToChange.setIvValues(ivValues);
            curveToChange.setDvValues(dvValues);
        }
        return false;
    }
}
