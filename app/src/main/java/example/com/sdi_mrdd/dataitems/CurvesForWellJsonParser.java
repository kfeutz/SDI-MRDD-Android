package example.com.sdi_mrdd.dataitems;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper singleton class which parses JSON strings into
 * Curve objects based on the current REST api.
 *
 * Created by Kevin on 2/28/2015.
 */
public class CurvesForWellJsonParser /*implements Parser<Curve>*/ {

    /* Instance to itself (Singleton) */
    private static final CurvesForWellJsonParser instance = new CurvesForWellJsonParser();

    /* Class tag for Log cat */
    private static final String TAG = "CurveJsonParser";

    /**
     * Retrieves the instance of the curve json parser
     *
     * @return  CurveJsonParser     The curve json parser object
     */
    public static CurvesForWellJsonParser getInstance() {
        return instance;
    }

    /**
     * Converts a json string to a list of Curve objects
     *
     * @param jsonString        The JSON representation of the list of curves
     * @return  List<Curve>     A List containing the Curves from the JSON string.
     */
    /*@Override*/

    public ArrayList<Curve> parse(String jsonString) {
        JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
        ArrayList<Curve> curves = new ArrayList<>();
        Map<String, String> singleCurve = new HashMap<>();
        String curveJsonArray = null;
        JSONArray curveArray = null;

        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String token = jsonReader.nextName();
                /* Retrieve array containing all wellbore_curves */
                if (token.equals("wellbore_curves")) {
                    /* Grab the json array string from wellbore_curves */
                    jsonReader.beginArray();
                    /*Loop through each curve in the json array */
                    while (jsonReader.hasNext()) {
                        /* Pass each json object to retrieve a curve object */
                        singleCurve = readJsonCurve(jsonReader, "wellbore_curves");
                        for(String curveName : singleCurve.keySet()) {
                            curves.add(new WellboreCurve(singleCurve.get(curveName), curveName,
                                    "", "", "", ""));
                        }

                    }
                    jsonReader.endArray();
                }
                /* Retrieve array containing all time_curves */
                else if (token.equals("time_curves")) {
                    /* Grab the json array string from wellbore_curves */
                    jsonReader.beginArray();
                    /*Loop through each curve in the json array */
                    while (jsonReader.hasNext()) {
                        /* Pass each json object to retrieve a curve object */
                        singleCurve = readJsonCurve(jsonReader, "time_curves");
                        for(String curveName : singleCurve.keySet()) {
                            curves.add(new TimeCurve(singleCurve.get(curveName), curveName,
                                    "", "", "", ""));
                        }
                    }
                    jsonReader.endArray();
                }
                else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            jsonReader.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse curves\n" + Log.getStackTraceString(e));
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse curves\n" + Log.getStackTraceString(e));
        }
        return curves;
    }

    /*public Map<String, String> parse(String jsonString) {
        JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
        Map<String, String> curves = new HashMap<>();
        Map<String, String> singleCurve = new HashMap<>();
        String curveJsonArray = null;
        JSONArray curveArray = null;

        try {
            jsonReader.beginObject();
            while (jsonReader.hasNext()) {
                String token = jsonReader.nextName();
                *//* Retrieve array containing all wellbore_curves *//*
                if (token.equals("wellbore_curves")) {
                    *//* Grab the json array string from wellbore_curves *//*
                    jsonReader.beginArray();
                    *//*Loop through each curve in the json array *//*
                    while (jsonReader.hasNext()) {
                        *//* Pass each json object to retrieve a curve object *//*
                        singleCurve = readJsonCurve(jsonReader, "wellbore_curves");
                        for(String curveName : singleCurve.keySet()) {
                            if (curves.get(curveName) == null) {
                                curves.putAll(singleCurve);
                            }
                        }

                    }
                    jsonReader.endArray();
                }
                *//* Retrieve array containing all time_curves *//*
                else if (token.equals("time_curves")) {
                    *//* Grab the json array string from wellbore_curves *//*
                    jsonReader.beginArray();
                    *//*Loop through each curve in the json array *//*
                    while (jsonReader.hasNext()) {
                        *//* Pass each json object to retrieve a curve object *//*
                        singleCurve = readJsonCurve(jsonReader, "time_curves");
                        for(String curveName : singleCurve.keySet()) {
                            if (curves.get(curveName) == null) {
                                curves.putAll(singleCurve);
                            }
                        }
                    }
                    jsonReader.endArray();
                }
                else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endObject();
            jsonReader.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse curves\n" + Log.getStackTraceString(e));
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse curves\n" + Log.getStackTraceString(e));
        }
        return curves;
    }*/

    /**
     * Parses a JSON Curve Object and returns a java Curve object
     * Used as a helper method in parse above
     *
     * @param reader        JsonReader that contains a curve representation
     * @param curveType     The type of curve to create. Either wellbore_curve or time_curve
     * @return  Curve       Java object representation of the passed Curve Json object
     * @throws IOException  Thrown if error when reading json object
     */
    private Map<String, String> readJsonCurve(JsonReader reader, String curveType) throws IOException, JSONException {
        String curveId = null;
        String name = null;
        Map<String, String> curveMap = new HashMap<String, String>();

        reader.beginObject();
        /* Loop for entire curve object */
        while (reader.hasNext()) {
            String token = reader.nextName();
            /* Grab curve id field */
            if (token.equals("id")) {
                curveId = reader.nextString();
            }
            /* Grab curve name field */
            else if (token.equals("name")) {
                name = reader.nextString();
            }
            /* Grab curve iv_name field */
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        /* Create WellboreCurves and TimeCurves respectively */
        if (curveType.equals("wellbore_curves")) {
            curveMap.put(name, curveId);
            return curveMap;
        }
        else if (curveType.equals("time_curves")) {
            curveMap.put(name, curveId);
            return curveMap;
        }
        else {
            return null;
        }
    }
}
