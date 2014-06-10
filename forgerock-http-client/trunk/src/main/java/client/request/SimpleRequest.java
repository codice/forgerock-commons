package client.request;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by brian.bailey@forgerock.com on 06/06/2014.
 */
public class SimpleRequest implements Request {

    private Map<String, String> headers = new HashMap<String, String>();
    private Map<String, String> queryParameters = new HashMap<String, String>();
    private String method;
    private String uri;
    private String messageBody;
    private Map<String, String> cookies;

    public void addHeader(String field, String value) {
        this.headers.put(field, value);
    }

    public void addQueryParameter(String field, String value) {
        this.queryParameters.put(field, value);
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public void setCookies(Map<String, String> cookies) {
        //TODO Check how map is sent from JS and Groovy (as an array?)
        //May need to add cookie to a map, then translate in client?
        this.cookies = cookies;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public Map<String, String> getQueryParameters() {
        return queryParameters;
    }

    public String getMethod() {
        return method;
    }

    public String getUri() {
        return uri;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public Map<String, String> getCookies() {
        return cookies;
    }

}
