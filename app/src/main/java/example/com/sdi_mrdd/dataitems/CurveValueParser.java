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
            recentCurveValues.put("ivValue", ivValues.get(ivValues.size() - 1));
            recentCurveValues.put("dvValue", dvValues.get(dvValues.size() - 1));
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse curves\n" + Log.getStackTraceString(e));
        }
        return recentCurveValues;
    }

    public Curve parseIvDvValues(Curve curveToChange, String jsonString) {
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
            /* Map values do not exist */
            if(jsonReader.peek() == JsonToken.NULL) {
                curveToChange.setNextStartUnit(0);
                curveToChange.setNextEndUnit(0);
                jsonReader.skipValue();
            }
            else {
                jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String token = jsonReader.nextName();
                    /* Grab curve id field */
                    if (token.equals("startIV")) {
                        curveToChange.setNextStartUnit(Long.parseLong(jsonReader.nextString()));
                    }
                    else if (token.equals("oldIV")) {
                        curveToChange.setNextEndUnit(Long.parseLong(jsonReader.nextString()));
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
        curveToChange.setIvValues(ivValues);
        curveToChange.setDvValues(dvValues);
        return curveToChange;
    }

    public String parseIvValues(String jsonString) {
        JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
        String yo = jsonString;
        String curveJsonArray = null;
        String ivValues = null;
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String token = jsonReader.nextName();
                /* Grab curve id field */
                if (token.equals("iv_values")) {
                    ivValues = jsonReader.nextString();
                }
                else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            jsonReader.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse iv values\n" + Log.getStackTraceString(e));
        }
        return ivValues;
    }

    public String parseDvValues(String jsonString) {
        JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
        String curveJsonArray = null;
        String dvValues = null;
        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String token = jsonReader.nextName();
                /* Grab curve id field */
                if (token.equals("dv_values")) {
                    dvValues = jsonReader.nextString();
                }
                else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            jsonReader.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse dv vlaues \n" + Log.getStackTraceString(e));
        }
        return dvValues;
    }
}
