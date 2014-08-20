package org.forgerock.openam.mobile.commons;

import java.io.IOException;
import java.util.HashMap;
import static junit.framework.Assert.assertEquals;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;

@RunWith(RobolectricTestRunner.class)
public class ASyncRestRequestTest {

    private Invoker invoker;
    private Listener<UnwrappedResponse> listener;
    private ActionType successAction;
    private ActionType failAction;

    private final String URL = "http://www.google.com";

    private final String KEY = "key";
    private final String VALUE = "value";

    HashMap<String, String> headers = new HashMap<String, String>();
    HashMap<String, String> params = new HashMap<String, String>();

    @Before
    public void setUp() throws Exception {
        invoker = mock(Invoker.class);
        listener = mock(Listener.class);
        successAction = mock(ActionType.class);
        failAction = mock(ActionType.class);

        headers.put(KEY, VALUE);
        params.put(KEY, VALUE);

    }

    /**
     * Validates that all expected calls are successfully made by the async class, and
     * upon completion the returning actionType is correctly set to the successAction
     *
     * @throws java.io.IOException
     */
    @Test
    public void testDoInBackground() throws IOException {
        //setup
        final HttpGet request = new HttpGet(URL);
        final HttpClient client = mock(HttpClient.class);
        final HttpResponse httpResponse = mock(HttpResponse.class);
        final ResponseUnwrapperFactory factory = mock(ResponseUnwrapperFactory.class);
        final ResponseUnwrapper unwrapper = mock(ResponseUnwrapper.class);
        final UnwrappedResponse response = mock(UnwrappedResponse.class);

        final DumbASyncRestRequest restRequest = new DumbASyncRestRequest(invoker, listener, successAction, failAction, request, params, headers);
        restRequest.setHttpClient(client);
        restRequest.setResponseUnwrapperFactory(factory);

        //given
        given(client.execute(request)).willReturn(httpResponse);
        given(factory.createUnwrapper(httpResponse, successAction, failAction)).willReturn(unwrapper);
        given(unwrapper.unwrapHttpResponse()).willReturn(response);

        //when
        restRequest.execute();
        Robolectric.runBackgroundTasks();

        //then
        verify(client).execute(request);
        verify(factory).createUnwrapper(httpResponse, successAction, failAction);
        verify(unwrapper).unwrapHttpResponse();

        String keyEqualsValue = KEY + '=' + VALUE;

        assert(request.getURI().toString().contains(keyEqualsValue));
        assertEquals(request.getFirstHeader(KEY).getValue(), VALUE);
        assertEquals(request.getHeaders("Accept-API-Version")[0].getValue(), "protocol=1.0, resource=1.0");
        assertEquals(restRequest.getCurrentActionState(), successAction);
    }

    /**
     * Validates that upon failure, the failaction is left as the outcome type, and
     * the correct flow is observed.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testDoInBackgroundFailHttp() throws IOException {
        //setup
        final HttpGet request = new HttpGet(URL);
        final HttpClient client = mock(HttpClient.class);
        final IOException ioexception = mock(IOException.class);

        final DumbASyncRestRequest restRequest = new DumbASyncRestRequest(invoker, listener, successAction, failAction, request, params, headers);
        restRequest.setHttpClient(client);

        //given
        given(client.execute(request)).willThrow(ioexception);

        //when
        restRequest.execute();
        Robolectric.runBackgroundTasks();

        //then
        verify(client).execute(request);

        assertEquals(request.getFirstHeader(KEY).getValue(), VALUE);
        assertEquals(request.getHeaders("Accept-API-Version")[0].getValue(), "protocol=1.0, resource=1.0");
        assertEquals(restRequest.getCurrentActionState(), failAction);
    }


    /**
     * Validates that upon failure, the failaction is left as the outcome type, and
     * the correct flow is observed.
     *
     * @throws java.io.IOException
     */
    @Test
    public void testDoInBackgroundFailUnwrap() throws IOException {
        //setup
        final HttpGet request = new HttpGet(URL);
        final HttpClient client = mock(HttpClient.class);
        final HttpResponse httpResponse = mock(HttpResponse.class);
        final ResponseUnwrapperFactory factory = mock(ResponseUnwrapperFactory.class);
        final ResponseUnwrapper unwrapper = mock(ResponseUnwrapper.class);
        final IOException ioexception = mock(IOException.class);

        final DumbASyncRestRequest restRequest = new DumbASyncRestRequest(invoker, listener, successAction, failAction, request, params, headers);
        restRequest.setHttpClient(client);
        restRequest.setResponseUnwrapperFactory(factory);

        //given
        given(client.execute(request)).willReturn(httpResponse);
        given(factory.createUnwrapper(httpResponse, successAction, failAction)).willReturn(unwrapper);
        given(unwrapper.unwrapHttpResponse()).willThrow(ioexception);

        //when
        restRequest.execute();
        Robolectric.runBackgroundTasks();

        //then
        verify(client).execute(request);
        verify(factory).createUnwrapper(httpResponse, successAction, failAction);
        verify(unwrapper).unwrapHttpResponse();

        assertEquals(request.getFirstHeader(KEY).getValue(), VALUE);
        assertEquals(request.getHeaders("Accept-API-Version")[0].getValue(), "protocol=1.0, resource=1.0");
        assertEquals(restRequest.getCurrentActionState(), failAction);
    }

    // The invoker allows the test to monitor how the abstract methods are being interacted with.
    private static interface Invoker {

        public void insertData();

    }

    //allows us to test the abstract ASyncRestRequest class
    private static class DumbASyncRestRequest extends ASyncRestRequest<HttpGet> {

        private final Invoker invoker;

        public DumbASyncRestRequest(Invoker invoker, Listener< UnwrappedResponse > listener, ActionType successAction,
                    ActionType failAction, HttpGet request, HashMap<String, String> params, HashMap<String, String> headers) {
            super(listener, successAction, failAction, request, params, headers);
            this.invoker = invoker;
        }

        public void insertData() {
            invoker.insertData();
        }

        @Override
        protected void onPostExecute(UnwrappedResponse result) {
            super.onPostExecute(result);
        }
    }

}
