package example.com.sdi_mrdd.dataitems;;

import java.util.List;

/**
 * Defines methods that should be used by all Parser objects
 *
 * @param <T>
 */
public interface Parser<T> {

    /**
     * Definition for parse method
     *
     * @param jsonString    The JSON string to parse
     * @return              List of java objects representing the JSON string
     */
    public List<T> parse(String jsonString);
}
