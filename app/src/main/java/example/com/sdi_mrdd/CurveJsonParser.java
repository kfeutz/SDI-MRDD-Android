package example.com.sdi_mrdd;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper singleton class which parses JSON strings into
 * Curve objects based on the current REST api.
 *
 * Created by Kevin on 2/28/2015.
 */
public class CurveJsonParser implements Parser<Curve> {

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
    @Override
    public List<Curve> parse(String jsonString) {
        JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
        List<Curve> curves = new ArrayList<>();
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
                        curves.add(readJsonCurve(jsonReader, "wellbore_curves"));
                    }
                }
                /* Retrieve array containing all time_curves */
                else if (token.equals("time_curves")) {
                    /* Grab the json array string from wellbore_curves */
                    jsonReader.beginArray();
                    /*Loop through each curve in the json array */
                    while (jsonReader.hasNext()) {
                        /* Pass each json object to retrieve a curve object */
                        curves.add(readJsonCurve(jsonReader, "time_curves"));
                    }
                }
                else {
                    jsonReader.skipValue();
                }
            }
            jsonReader.endArray();
            jsonReader.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse curves\n" + Log.getStackTraceString(e));
        } catch (JSONException e) {
            Log.e(TAG, "Failed to parse curves\n" + Log.getStackTraceString(e));
        }
        return curves;
    }

    /**
     * Parses a JSON Curve Object and returns a java Curve object
     * Used as a helper method in parse above
     *
     * @param reader        JsonReader that contains a curve representation
     * @param curveType     The type of curve to create. Either wellbore_curve or time_curve
     * @return  Curve       Java object representation of the passed Curve Json object
     * @throws IOException  Thrown if error when reading json object
     */
    private Curve readJsonCurve(JsonReader reader, String curveType) throws IOException, JSONException {
        String curveId = null;
        String name = null;

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
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        /* Create WellboreCurves and TimeCurves respectively */
        if (curveType.equals("wellbore_curves")) {
            return new WellboreCurve(curveId, name);
        }
        else if (curveType.equals("time_curves")) {
            return new TimeCurve(curveId, name);
        }
        else {
            return null;
        }
    }
}
