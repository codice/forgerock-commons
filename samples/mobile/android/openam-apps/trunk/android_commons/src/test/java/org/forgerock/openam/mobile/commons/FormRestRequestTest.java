package org.forgerock.openam.mobile.commons;

import java.io.IOException;
import java.util.HashMap;
import static junit.framework.Assert.assertEquals;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class FormRestRequestTest {

    private Listener<UnwrappedResponse> listener;
    private ActionType successAction;
    private ActionType failAction;

    private final String KEY = "key";
    private final String VALUE = "value";

    private final String URL = "http://www.google.com";

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
    public void insertDataTest() throws IOException {
        HttpPost request = new HttpPost(URL);

        HashMap<String, String> formParameters = new HashMap<String, String>();
        formParameters.put(KEY, VALUE);

        FormRestRequest restRequest = new FormRestRequest(listener, successAction, failAction, request,
                params, headers, formParameters);

        String keyEqualsValue = KEY + '=' + VALUE;
        String entity = EntityUtils.toString(restRequest.getRequest().getEntity());

        assertEquals(entity, keyEqualsValue);
        assertEquals(request.getHeaders("Accept-API-Version")[0].getValue(), "protocol=1.0, resource=1.0");
    }

}
