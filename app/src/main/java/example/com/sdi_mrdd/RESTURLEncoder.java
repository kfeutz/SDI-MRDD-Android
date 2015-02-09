package example.com.sdi_mrdd;

import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;

/**
 * Created by dhnishi on 2/3/15.
 */
public class RESTURLEncoder {
    public URI convertToURLEscapingIllegalCharacters(String string){
        try {
            String decodedURL = URLDecoder.decode(string, "UTF-8");
            URL url = new URL(decodedURL);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            return uri;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
