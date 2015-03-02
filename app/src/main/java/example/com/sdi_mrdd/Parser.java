package example.com.sdi_mrdd;;

import java.util.List;

public interface Parser<T> {

    public List<T> parse(String jsonString);
}
