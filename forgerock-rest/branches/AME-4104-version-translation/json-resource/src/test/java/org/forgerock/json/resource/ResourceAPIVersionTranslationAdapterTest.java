/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2014 ForgeRock AS.
 */

package org.forgerock.json.resource;

import org.forgerock.json.fluent.JsonValue;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.mockito.stubbing.Stubber;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.forgerock.json.fluent.JsonValue.json;
import static org.forgerock.json.fluent.JsonValue.object;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class ResourceAPIVersionTranslationAdapterTest {

    private static final Version CLIENT_VERSION = Version.valueOf(2);

    private ResourceAPIVersionTranslationAdapter testAdapter;

    @Mock
    private RequestHandler mockDelegate;

    @Mock
    private ResourceAPIVersionTranslator mockTranslator;

    private ServerContext serverContext;

    @BeforeMethod
    public void setupMockAdapter() {
        MockitoAnnotations.initMocks(this);

        serverContext = serverContext(CLIENT_VERSION);

        given(mockTranslator.getSupportedClientVersion()).willReturn(CLIENT_VERSION);

        testAdapter = new ResourceAPIVersionTranslationAdapter(mockDelegate, mockTranslator);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldRejectNullDelegate() {
        new ResourceAPIVersionTranslationAdapter(null, mockTranslator);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldRejectNullTranslator() {
        new ResourceAPIVersionTranslationAdapter(mockDelegate, null);
    }

    @Test(dataProvider = "RequestProvider")
    public void shouldRejectIncompatibleClientVersion(Request request, ResultHandler<?> handler) {
        // Given
        // Incompatible client version
        serverContext = serverContext(Version.valueOf(CLIENT_VERSION.getMajor() + 1));

        // When
        dispatch(testAdapter, request, handler);

        // Then
        verify(handler).handleError(isA(BadRequestException.class));
        verifyZeroInteractions(mockDelegate);
    }

    @Test(dataProvider = "RequestProvider")
    public void shouldTranslateContext(Request request, ResultHandler<?> handler) {
        // Given

        // When
        dispatch(testAdapter, request, handler);

        // Then
        verify(mockTranslator).translateContext(serverContext);
    }

    @Test(dataProvider = "RequestProvider")
    public void shouldTranslateRequests(Request request, ResultHandler<?> handler) {
        // Given

        // When
        dispatch(testAdapter, request, handler);

        // Then
        verify(mockTranslator).translateRequest(serverContext, request);
    }

    @Test
    public void shouldTranslateActionResponse() {
        // Given
        ActionRequest request = mock(ActionRequest.class);
        ResultHandler<JsonValue> handler = mock(ResultHandler.class);
        JsonValue result = json(object());

        doActionResult(result);

        // When
        testAdapter.handleAction(serverContext, request, handler);

        // Then
        verify(mockTranslator).translateActionResponse(serverContext, request, result);
    }

    @Test
    public void shouldTranslateResourceResponseForCreateRequests() {
        // Given
        CreateRequest request = mock(CreateRequest.class);
        ResultHandler<Resource> handler = mock(ResultHandler.class);
        Resource result = new Resource("test", "test", json(object()));

        doResourceResult(result).when(mockDelegate).handleCreate(any(ServerContext.class), any(CreateRequest.class),
                any(ResultHandler.class));

        // When
        testAdapter.handleCreate(serverContext, request, handler);

        // Then
        verify(mockTranslator).translateResourceResponse(serverContext, request, result);
    }

    @Test
    public void shouldTranslateResourceResponseForDeleteRequests() {
        // Given
        DeleteRequest request = mock(DeleteRequest.class);
        ResultHandler<Resource> handler = mock(ResultHandler.class);
        Resource result = new Resource("test", "test", json(object()));

        doResourceResult(result).when(mockDelegate).handleDelete(any(ServerContext.class), any(DeleteRequest.class),
                any(ResultHandler.class));

        // When
        testAdapter.handleDelete(serverContext, request, handler);

        // Then
        verify(mockTranslator).translateResourceResponse(serverContext, request, result);
    }

    @Test
    public void shouldTranslateResourceResponseForPatchRequests() {
        // Given
        PatchRequest request = mock(PatchRequest.class);
        ResultHandler<Resource> handler = mock(ResultHandler.class);
        Resource result = new Resource("test", "test", json(object()));

        doResourceResult(result).when(mockDelegate).handlePatch(any(ServerContext.class), any(PatchRequest.class),
                any(ResultHandler.class));

        // When
        testAdapter.handlePatch(serverContext, request, handler);

        // Then
        verify(mockTranslator).translateResourceResponse(serverContext, request, result);
    }

    @Test
    public void shouldTranslateResourceResponseForReadRequests() {
        // Given
        ReadRequest request = mock(ReadRequest.class);
        ResultHandler<Resource> handler = mock(ResultHandler.class);
        Resource result = new Resource("test", "test", json(object()));

        doResourceResult(result).when(mockDelegate).handleRead(any(ServerContext.class), any(ReadRequest.class),
                any(ResultHandler.class));

        // When
        testAdapter.handleRead(serverContext, request, handler);

        // Then
        verify(mockTranslator).translateResourceResponse(serverContext, request, result);
    }

    @Test
    public void shouldTranslateResourceResponseForUpdateRequests() {
        // Given
        UpdateRequest request = mock(UpdateRequest.class);
        ResultHandler<Resource> handler = mock(ResultHandler.class);
        Resource result = new Resource("test", "test", json(object()));

        doResourceResult(result).when(mockDelegate).handleUpdate(any(ServerContext.class), any(UpdateRequest.class),
                any(ResultHandler.class));

        // When
        testAdapter.handleUpdate(serverContext, request, handler);

        // Then
        verify(mockTranslator).translateResourceResponse(serverContext, request, result);
    }

    @Test
    public void shouldTranslateResourceResponseForQueryRequests() {
        // Given
        QueryRequest request = mock(QueryRequest.class);
        QueryResultHandler handler = mock(QueryResultHandler.class);
        Resource result = new Resource("test", "test", json(object()));

        doQueryResourceResult(result).when(mockDelegate).handleQuery(any(ServerContext.class), any(QueryRequest.class),
                any(QueryResultHandler.class));

        // When
        testAdapter.handleQuery(serverContext, request, handler);

        // Then
        verify(mockTranslator).translateResourceResponse(serverContext, request, result);
    }

    @Test
    public void shouldTranslateQueryResultResponse() {
        // Given
        QueryRequest request = mock(QueryRequest.class);
        QueryResultHandler handler = mock(QueryResultHandler.class);
        QueryResult result = new QueryResult();

        doQueryResult(result).when(mockDelegate).handleQuery(any(ServerContext.class), any(QueryRequest.class),
                any(QueryResultHandler.class));

        // When
        testAdapter.handleQuery(serverContext, request, handler);

        // Then
        verify(mockTranslator).translateQueryResult(serverContext, request, result);
    }

    @Test
    public void shouldTranslateResourceExceptions() {
        // Given
        CreateRequest request = mock(CreateRequest.class);
        ResourceException error = ResourceException.getException(ResourceException.BAD_REQUEST);

        doErrorResult(error).when(mockDelegate).handleCreate(any(ServerContext.class), any(CreateRequest.class),
                any(ResultHandler.class));

        // When
        testAdapter.handleCreate(serverContext, request, mock(ResultHandler.class));

        // Then
        verify(mockTranslator).translateException(serverContext, request, error);
    }

    /**
     * Provides test requests for all CREST request types, plus mock result handlers of an appropriate type for the
     * request.
     */
    @DataProvider(name = "RequestProvider")
    public Object[][] getTestRequests() {
        return new Object[][] {
            { Requests.newActionRequest("test", "test"), mock(ResultHandler.class) },
            { Requests.newCreateRequest("test", json(object())), mock(ResultHandler.class) },
            { Requests.newDeleteRequest("test"), mock(ResultHandler.class) },
            { Requests.newPatchRequest("test"), mock(ResultHandler.class) },
            { Requests.newQueryRequest("test"), mock(QueryResultHandler.class) },
            { Requests.newReadRequest("test"), mock(ResultHandler.class) },
            { Requests.newUpdateRequest("test", json(object())), mock(ResultHandler.class) }
        };
    }

    private void doActionResult(final JsonValue result) {
        doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final ResultHandler<JsonValue> handler = (ResultHandler<JsonValue>) invocationOnMock.getArguments()[2];
                handler.handleResult(result);
                return null;
            }
        }).when(mockDelegate).handleAction(any(ServerContext.class), any(ActionRequest.class),
                any(ResultHandler.class));
    }

    private Stubber doResourceResult(final Resource result) {
        return doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final ResultHandler<Resource> handler = (ResultHandler<Resource>) invocationOnMock.getArguments()[2];
                handler.handleResult(result);
                return null;
            }
        });
    }

    private Stubber doQueryResourceResult(final Resource result) {
        return doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final QueryResultHandler handler = (QueryResultHandler) invocationOnMock.getArguments()[2];
                handler.handleResource(result);
                return null;
            }
        });
    }

    private Stubber doQueryResult(final QueryResult result) {
        return doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocationOnMock) throws Throwable {
                final QueryResultHandler handler = (QueryResultHandler) invocationOnMock.getArguments()[2];
                handler.handleResult(result);
                return null;
            }
        });
    }

    private Stubber doErrorResult(final ResourceException error) {
        return doAnswer(new Answer<Void>() {
            @Override
            public Void answer(final InvocationOnMock invocation) throws Throwable {
                final ResultHandler<?> handler = (ResultHandler<?>) invocation.getArguments()[2];
                handler.handleError(error);
                return null;
            }
        });
    }


    private void dispatch(final RequestHandler target, final Request request, final ResultHandler<?> handler,
                          final ServerContext context) {
        request.accept(new RequestDispatcher(target, context), handler);
    }

    private void dispatch(final RequestHandler target, final Request request, final ResultHandler<?> handler) {
        dispatch(target, request, handler, serverContext);
    }

    private ServerContext serverContext(Version clientVersion) {
        return new ServerContext(new AcceptAPIVersionContext(new RootContext(), "test",
                AcceptAPIVersion.newBuilder().withDefaultResourceVersion(clientVersion)
                        .withDefaultProtocolVersion("1.0").build()));
    }
}
