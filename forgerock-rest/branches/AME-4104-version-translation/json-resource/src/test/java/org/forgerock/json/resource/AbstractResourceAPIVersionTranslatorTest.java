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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.fest.assertions.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class AbstractResourceAPIVersionTranslatorTest {
    private static final Version CLIENT_VERSION = Version.valueOf(1);
    private static final Version SERVER_VERSION = Version.valueOf(2);

    private AbstractResourceAPIVersionTranslator testTranslator;

    @Mock
    private ServerContext mockContext;

    @Mock
    private Request mockRequest;

    @BeforeMethod
    public void createTestTranslator() {
        MockitoAnnotations.initMocks(this);
        testTranslator = new AbstractResourceAPIVersionTranslator(CLIENT_VERSION, SERVER_VERSION) {
        };
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldRejectNullClientVersion() {
        new AbstractResourceAPIVersionTranslator(null, SERVER_VERSION) { };
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void shouldRejectNullServerVersion() {
        new AbstractResourceAPIVersionTranslator(CLIENT_VERSION, null) { };
    }

    @Test
    public void shouldReportCorrectClientVersion() {
        // Given

        // When
        Version result = testTranslator.getSupportedClientVersion();

        // Then
        assertThat(result).isSameAs(CLIENT_VERSION);
    }

    @Test
    public void shouldReportCorrectServerVersion() {
        // Given

        // When
        Version result = testTranslator.getSupportedServerVersion();

        // Then
        assertThat(result).isSameAs(SERVER_VERSION);
    }

    @Test
    public void shouldReturnOriginalRequestByDefault() {
        // Given

        // When
        Request result = testTranslator.translateRequest(mockContext, mockRequest);

        // Then
        assertThat(result).isSameAs(mockRequest);
    }

    @Test
    public void shouldReturnOriginalActionResponseByDefault() {
        // Given
        JsonValue value = JsonValue.json(JsonValue.object());

        // When
        JsonValue result = testTranslator.translateActionResponse(mockContext, mock(ActionRequest.class), value);

        // Then
        assertThat((Object) result).isSameAs(value);
    }

    @Test
    public void shouldReturnOriginalResourceByDefault() {
        // Given
        Resource resource = new Resource("test", "test", JsonValue.json(JsonValue.object()));

        // When
        Resource result = testTranslator.translateResourceResponse(mockContext, mockRequest, resource);

        // Then
        assertThat(result).isSameAs(resource);
    }

    @Test
    public void shouldReturnOriginalQueryResultByDefault() {
        // Given
        QueryResult queryResult = new QueryResult();

        // When
        QueryResult result = testTranslator.translateQueryResult(mockContext, mock(QueryRequest.class), queryResult);

        // Then
        assertThat(result).isSameAs(queryResult);
    }

    @Test
    public void shouldReturnOriginalExceptionByDefault() {
        // Given
        ResourceException error = ResourceException.getException(ResourceException.BAD_REQUEST);

        // When
        ResourceException result = testTranslator.translateException(mockContext, mockRequest, error);

        // Then
        assertThat(result).isSameAs(error);
    }

    @Test
    public void shouldTranslateResourceVersionInContext() {
        // Given
        ServerContext context = getTestServerContext(CLIENT_VERSION);

        // When
        ServerContext result = testTranslator.translateContext(context);

        // Then
        assertThat(result.asContext(AcceptAPIVersionContext.class).getResourceVersion())
                .isEqualTo(SERVER_VERSION);
    }

    @Test
    public void shouldNotTranslateContextIfVersionAlreadyCorrect() {
        // Given
        ServerContext context = getTestServerContext(SERVER_VERSION);

        // When
        ServerContext result = testTranslator.translateContext(context);

        // Then
        assertThat(result).isSameAs(context);
    }

    private ServerContext getTestServerContext(final Version resourceVersion) {
        final AcceptAPIVersionContext versionContext =
                new AcceptAPIVersionContext(new RootContext(), "test",
                        AcceptAPIVersion.newBuilder()
                                .withDefaultResourceVersion(resourceVersion)
                                .withDefaultProtocolVersion("1.0")
                                .build());

        return new ServerContext(versionContext);
    }
}
