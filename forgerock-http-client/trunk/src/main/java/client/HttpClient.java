package client;

import client.request.Request;
import client.response.Response;

/**
 * Created by brian.bailey@forgerock.com on 05/06/2014.
 */
public interface HttpClient {

    public Response perform(Request request);

}
