package example.com.sdi_mrdd.dataitems;

import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;

import java.io.IOException;
import java.io.StringReader;

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
