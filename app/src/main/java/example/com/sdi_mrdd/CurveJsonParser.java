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
 * Created by Kevin on 2/28/2015.
 */
public class CurveJsonParser implements Parser<Curve> {
    private static final CurveJsonParser instance = new CurveJsonParser();

    public static CurveJsonParser getInstance() {
        return instance;
    }

    private static final String TAG = "CurveJsonParser";
    /**
     * Converts a json string to a list of Curve objects
     * @param jsonString    The JSON representation of the list of curves
     * @return  List<Curve>  A List containing the Curves from the JSON string.
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
                if (token.equals("wellbore_curves")) {
                /* Grab the json array string from wellbore_curves */
                    jsonReader.beginArray();
                    /*Loop through each curve in the json array */
                    while (jsonReader.hasNext()) {
                        /* Pass each json object to retrieve a curve object */
                        curves.add(readJsonCurve(jsonReader, "wellbore_curves"));
                    }
                }
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
     * @param reader    JsonReader that contains a curve representation
     * @return  Curve    Java object representation of the passed Curve Json object
     * @throws IOException  Thrown if error when reading json object
     */
    private Curve readJsonCurve(JsonReader reader, String curveType) throws IOException, JSONException {
        String curveId = null;
        String name = null;
        String location = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String token = reader.nextName();
            if (token.equals("id")) {
                curveId = reader.nextString();
            } else if (token.equals("name")) {
                name = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
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
