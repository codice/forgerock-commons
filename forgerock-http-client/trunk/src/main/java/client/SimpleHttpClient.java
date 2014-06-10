package client;

import client.request.Request;
import client.response.Response;
import client.response.SimpleResponse;
import org.restlet.Client;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.data.Protocol;
import org.restlet.representation.Representation;
import org.restlet.resource.ClientResource;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by brian.bailey@forgerock.com on 06/06/2014.
 */
public class SimpleHttpClient implements HttpClient {

    private static int READ_TIMEOUT = 30000;
    private static int CONNECT_TIMEOUT = 10000;

    public Response perform(Request request) {

        org.restlet.Request clientRequest = createClientRequest(request);

        Client client = new Client(Protocol.HTTP);
        org.restlet.Response clientResponse = client.handle(clientRequest);

        Response response = createResponse(clientResponse);

        return response;

    }

    private Response createResponse(org.restlet.Response clientResponse) {
        Integer statusCode = clientResponse.getStatus().getCode();
        String reasonPhrase = clientResponse.getStatus().getDescription();
        String messageBody = clientResponse.getEntityAsText();

        Form headersForm = (Form) clientResponse.getAttributes().get("org.restlet.http.headers");
        Map<String, String> headersMap = headersForm.getValuesMap();

        Map<String, String> cookiesMap = clientResponse.getCookieSettings().getValuesMap();

        return new SimpleResponse(statusCode, reasonPhrase, headersMap, messageBody, cookiesMap);
    }

    private org.restlet.Request createClientRequest(Request request) {
        org.restlet.Request clientRequest = new org.restlet.Request();
        clientRequest.setMethod(Method.valueOf(request.getMethod()));
        clientRequest.setResourceRef(request.getUri());
        if (hasEntity(request)) {
            clientRequest.setEntity(request.getMessageBody(), MediaType.ALL);
        }
        if (hasHeaders(request)) {
            clientRequest.setAttributes(null);
        }
        if (hasCookies(request)) {
            clientRequest.setCookies(null);
        }

        return clientRequest;
    }

    private boolean hasEntity(Request request) {
        return (request.getMessageBody() != null && !(request.getMessageBody().isEmpty()));
    }

    private boolean hasHeaders(Request request) {
        return (request.getHeaders() != null && !(request.getHeaders().isEmpty()));
    }

    private boolean hasCookies(Request request) {
        return (request.getCookies() != null && !(request.getCookies().isEmpty()));
    }

}