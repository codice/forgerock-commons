package client.request;

import java.util.Map;

/**
 * Created by brian.bailey@forgerock.com on 05/06/2014.
 */
public interface Request {

    public void addHeader(String field, String value);
    public void addQueryParameter(String field, String value);
    public void setMethod(String method);
    public void setUri(String uri);
    public void setMessageBody(String messageBody);
    public void setCookies(Map<String, String> cookies);
    public Map<String, String> getHeaders();
    public Map<String, String> getQueryParameters();
    public String getMethod();
    public String getUri();
    public String getMessageBody();
    public Map<String, String> getCookies();

}
