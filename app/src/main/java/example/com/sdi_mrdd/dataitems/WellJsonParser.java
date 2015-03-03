package example.com.sdi_mrdd.dataitems;

import android.util.JsonReader;
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper singleton class which parses JSON strings into
 * Well objects based on the current REST api.
 *
 * Created by Kevin on 2/28/2015.
 */
public class WellJsonParser implements Parser<Well> {

    /* Instance to itself (Singleton) */
    private static final WellJsonParser instance = new WellJsonParser();

    /* Class tag for Log cat */
    private static final String TAG = "WellJsonParser";

    /**
     * Retrieves the instance of the well json parser
     *
     * @return  WellJsonParser     The well json parser object
     */
    public static WellJsonParser getInstance() {
        return instance;
    }

    /**
     * Converts a json string to a list of Well objects
     *
     * @param jsonString    The JSON representation of the list of wells
     * @return  List<Well>  A List containing the Wells from the JSON string.
     */
    @Override
    public List<Well> parse(String jsonString) {
        JsonReader jsonReader = new JsonReader(new StringReader(jsonString));
        List<Well> wells = new ArrayList<>();

        try {
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                wells.add(readJsonWell(jsonReader));
            }
            jsonReader.endArray();
            jsonReader.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to parse wells\n" + Log.getStackTraceString(e));
        }

        return wells;
    }

    /**
     * Parses a JSON Well Object and returns a java Well object
     * Used as a helper method in parse above
     *
     * @param reader    JsonReader that contains a well representation
     * @return  Well    Java object representation of the passed Well Json object
     * @throws IOException  Thrown if error when reading json object
     */
    private Well readJsonWell(JsonReader reader) throws IOException {
        String wellId = null;
        String name = null;
        String location = null;

        reader.beginObject();
        while (reader.hasNext()) {
            String token = reader.nextName();
            if (token.equals("id")) {
                wellId = reader.nextString();
            } else if (token.equals("name")) {
                name = reader.nextString();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return new Well(wellId, name);
    }
}
