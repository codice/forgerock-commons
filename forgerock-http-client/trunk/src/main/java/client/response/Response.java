package client.response;

import java.util.List;
import java.util.Map;

/**
 * Created by brian.bailey@forgerock.com on 05/06/2014.
 */
public interface Response {

    public Integer getStatusCode();
    public String getReasonPhrase();
    public boolean hasHeaders();
    public Map<String, String> getHeaders();
    public String getMessageBody();
    public boolean hasCookies();
    public Map<String, String> getCookies();

}
