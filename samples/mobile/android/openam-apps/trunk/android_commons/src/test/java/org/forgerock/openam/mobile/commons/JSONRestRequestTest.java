package org.forgerock.openam.mobile.commons;

import java.io.IOException;
import java.util.HashMap;
import junit.framework.Assert;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class JSONRestRequestTest {

    private Listener<UnwrappedResponse> listener;
    private ActionType successAction;
    private ActionType failAction;

    private final String KEY = "key";
    private final String VALUE = "value";

    private final String URL = "http://www.google.com";
    private final String JSON_STRING = "{\"key\":\"value\"}";
    private final String EMPTY_JSON_STRING = "{}";

    private HashMap<String, String> headers = new HashMap<String, String>();
    private HashMap<String, String> params = new HashMap<String, String>();

    @Before
    public void setUp() throws Exception {
        listener = mock(Listener.class);
        successAction = mock(ActionType.class);
        failAction = mock(ActionType.class);

        params.put(KEY, VALUE);
        headers.put(KEY, VALUE);
    }

    @Test
    public void insertDataTest() throws IOException, JSONException {
        HttpPost request = new HttpPost(URL);

        JSONObject json = new JSONObject(JSON_STRING);

        JSONRestRequest restRequest = new JSONRestRequest(listener, successAction, failAction, request,
                params, headers, json);

        String entity = EntityUtils.toString(restRequest.getRequest().getEntity());

        assertEquals(entity, JSON_STRING);
        Assert.assertEquals(request.getHeaders("Accept-API-Version")[0].getValue(), "protocol=1.0, resource=1.0");
    }

    @Test
    public void insertDataTestNull() throws IOException {
        HttpPost request = new HttpPost(URL);

        JSONRestRequest restRequest = new JSONRestRequest(listener, successAction, failAction, request,
                params, headers, null);

        String entity = EntityUtils.toString(restRequest.getRequest().getEntity());

        assertEquals(entity, EMPTY_JSON_STRING);
        Assert.assertEquals(request.getHeaders("Accept-API-Version")[0].getValue(), "protocol=1.0, resource=1.0");
    }

}
