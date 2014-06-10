package client.response;

import java.util.List;
import java.util.Map;

/**
 * Created by brian.bailey@forgerock.com on 06/06/2014.
 */
public class SimpleResponse implements Response {

    private Integer statusCode;
    private String reasonPhrase;
    private Map<String, String> headers;
    private String messageBody;
    private Map<String, String> cookies;

    public SimpleResponse(Integer statusCode, String reasonPhrase, Map<String, String> headers, String messageBody, Map<String, String> cookies) {
        this.statusCode = statusCode;
        this.reasonPhrase = reasonPhrase;
        this.headers = headers;
        this.messageBody = messageBody;
        this.cookies = cookies;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public boolean hasHeaders() {
        return (!(headers == null) || !(headers.isEmpty()));
    }

    //TODO Check how map is sent from JS and Groovy (as an array?)
    //May need to interpret as array?
    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public boolean hasCookies() {
        return (!(cookies == null) || !(cookies.isEmpty()));
    }

    //TODO Check how map is sent from JS and Groovy (as an array?)
    //May need to interpret as array?
    public Map<String, String> getCookies() {
        return cookies;
    }
}
