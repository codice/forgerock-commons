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
 * Copyright 2012-2015 ForgeRock AS.
 */
package org.forgerock.json.resource.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.AcceptAPIVersion;
import org.forgerock.json.resource.AcceptAPIVersionContext;
import org.forgerock.json.resource.ActionRequest;
import static org.forgerock.json.resource.ActionRequest.ACTION_ID_CREATE;
import org.forgerock.json.resource.AdviceContext;
import org.forgerock.json.resource.BadRequestException;
import org.forgerock.json.resource.ConflictException;
import org.forgerock.json.resource.ConnectionFactory;
import org.forgerock.json.resource.Context;
import org.forgerock.json.resource.CreateRequest;
import org.forgerock.json.resource.DeleteRequest;
import org.forgerock.json.resource.NotSupportedException;
import org.forgerock.json.resource.PatchRequest;
import org.forgerock.json.resource.PreconditionFailedException;
import org.forgerock.json.resource.QueryRequest;
import org.forgerock.json.resource.ReadRequest;
import org.forgerock.json.resource.Request;
import org.forgerock.json.resource.RequestUtil;
import org.forgerock.json.resource.ResourceException;
import org.forgerock.json.resource.ResourceName;
import org.forgerock.json.resource.UpdateRequest;
import org.forgerock.json.resource.Version;

import static org.forgerock.json.resource.servlet.HttpUtils.CONTENT_TYPE_REGEX;
import static org.forgerock.json.resource.servlet.HttpUtils.ETAG_ANY;
import static org.forgerock.json.resource.servlet.HttpUtils.HEADER_IF_MATCH;
import static org.forgerock.json.resource.servlet.HttpUtils.HEADER_IF_NONE_MATCH;
import static org.forgerock.json.resource.servlet.HttpUtils.METHOD_DELETE;
import static org.forgerock.json.resource.servlet.HttpUtils.METHOD_GET;
import static org.forgerock.json.resource.servlet.HttpUtils.METHOD_PATCH;
import static org.forgerock.json.resource.servlet.HttpUtils.METHOD_POST;
import static org.forgerock.json.resource.servlet.HttpUtils.METHOD_PUT;
import static org.forgerock.json.resource.servlet.HttpUtils.MIME_TYPE_APPLICATION_JSON;
import static org.forgerock.json.resource.servlet.HttpUtils.MIME_TYPE_MULTIPART_FORM_DATA;
import static org.forgerock.json.resource.servlet.HttpUtils.PARAM_ACTION;
import static org.forgerock.json.resource.servlet.HttpUtils.PARAM_QUERY_EXPRESSION;
import static org.forgerock.json.resource.servlet.HttpUtils.PARAM_QUERY_FILTER;
import static org.forgerock.json.resource.servlet.HttpUtils.PARAM_QUERY_ID;
import static org.forgerock.json.resource.servlet.HttpUtils.PROTOCOL_NAME;
import static org.forgerock.json.resource.servlet.HttpUtils.PROTOCOL_VERSION;
import static org.forgerock.json.resource.servlet.HttpUtils.RESTRICTED_HEADER_NAMES;
import static org.forgerock.json.resource.servlet.HttpUtils.checkNotNull;
import static org.forgerock.json.resource.servlet.HttpUtils.fail;
import static org.forgerock.json.resource.servlet.HttpUtils.getIfMatch;
import static org.forgerock.json.resource.servlet.HttpUtils.getIfNoneMatch;
import static org.forgerock.json.resource.servlet.HttpUtils.getJsonActionContent;
import static org.forgerock.json.resource.servlet.HttpUtils.getJsonContent;
import static org.forgerock.json.resource.servlet.HttpUtils.getMethod;
import static org.forgerock.json.resource.servlet.HttpUtils.getParameter;
import static org.forgerock.json.resource.servlet.HttpUtils.hasParameter;
import static org.forgerock.json.resource.servlet.HttpUtils.prepareResponse;
import static org.forgerock.json.resource.servlet.HttpUtils.rejectIfMatch;
import static org.forgerock.json.resource.servlet.HttpUtils.rejectIfNoneMatch;
import static org.forgerock.json.resource.VersionConstants.ACCEPT_API_VERSION;

/**
 * HTTP adapter from Servlet calls to JSON resource calls. This class can be
 * used in any Servlet, just create a new instance and override the service()
 * method in your Servlet to delegate all those calls to this class's service()
 * method.
 * <p>
 * For example:
 *
 * <pre>
 * public class TestServlet extends javax.servlet.http.HttpServlet {
 *     private HttpServletAdapter adapter;
 *
 *     public void init() throws ServletException {
 *         super.init();
 *         RequestHandler handler = xxx;
 *         adapter = new HttpServletAdapter(getServletContext(), handler);
 *     }
 *
 *     protected void service(HttpServletRequest req, HttpServletResponse res)
 *             throws ServletException, IOException {
 *         adapter.service(req, res);
 *     }
 * }
 * </pre>
 *
 * Note that this adapter does not provide implementations for the HTTP HEAD,
 * OPTIONS, or TRACE methods. A simpler approach is to use the
 * {@link HttpServlet} class contained within this package to build HTTP
 * Servlets since it provides support for these HTTP methods.
 *
 * @see HttpServlet
 */
public final class HttpServletAdapter {

    private final ServletApiVersionAdapter syncFactory;
    private final ConnectionFactory connectionFactory;
    private final HttpServletContextFactory contextFactory;

    /**
     * Creates a new servlet adapter with the provided connection factory and a
     * context factory the {@link SecurityContextFactory}.
     *
     * @param servletContext
     *            The servlet context.
     * @param connectionFactory
     *            The connection factory.
     * @throws ServletException
     *             If the servlet container does not support Servlet 2.x or
     *             beyond.
     */
    public HttpServletAdapter(final ServletContext servletContext,
            final ConnectionFactory connectionFactory) throws ServletException {
        this(servletContext, connectionFactory, (HttpServletContextFactory) null);
    }

    /**
     * Creates a new servlet adapter with the provided connection factory and
     * parent request context.
     *
     * @param servletContext
     *            The servlet context.
     * @param connectionFactory
     *            The connection factory.
     * @param parentContext
     *            The parent request context which should be used as the parent
     *            context of each request context.
     * @throws ServletException
     *             If the servlet container does not support Servlet 2.x or
     *             beyond.
     */
    public HttpServletAdapter(final ServletContext servletContext,
            final ConnectionFactory connectionFactory, final Context parentContext)
            throws ServletException {
        this(servletContext, connectionFactory, new HttpServletContextFactory() {

            @Override
            public Context createContext(final HttpServletRequest request) {
                return parentContext;
            }
        });
    }

    /**
     * Creates a new servlet adapter with the provided connection factory and
     * context factory.
     *
     * @param servletContext
     *            The servlet context.
     * @param connectionFactory
     *            The connection factory.
     * @param contextFactory
     *            The context factory which will be used to obtain the parent
     *            context of each request context, or {@code null} if the
     *            {@link SecurityContextFactory} should be used.
     * @throws ServletException
     *             If the servlet container does not support Servlet 2.x or
     *             beyond.
     */
    public HttpServletAdapter(final ServletContext servletContext,
            final ConnectionFactory connectionFactory,
            final HttpServletContextFactory contextFactory) throws ServletException {
        /*
         * The servletContext field was removed as part of fix for CREST-146.
         * The constructor parameter remains for API compatibility.
         */
        this.contextFactory =
                contextFactory != null ? contextFactory : SecurityContextFactory
                        .getHttpServletContextFactory();
        this.connectionFactory = checkNotNull(connectionFactory);
        this.syncFactory = ServletApiVersionAdapter.getInstance(servletContext);
    }

    /**
     * Services the provided HTTP servlet request.
     *
     * @param req
     *            The HTTP servlet request.
     * @param resp
     *            The HTTP servlet response.
     * @throws IOException
     *             If an unexpected IO error occurred while sending the
     *             response.
     */
    public void service(final HttpServletRequest req, final HttpServletResponse resp)
            throws IOException {

        // Dispatch the request based on method, taking into account \
        // method override header.
        final String method = getMethod(req);
        if (METHOD_DELETE.equals(method)) {
            doDelete(req, resp);
        } else if (METHOD_GET.equals(method)) {
            doGet(req, resp);
        } else if (METHOD_PATCH.equals(method)) {
            doPatch(req, resp);
        } else if (METHOD_POST.equals(method)) {
            doPost(req, resp);
        } else if (METHOD_PUT.equals(method)) {
            doPut(req, resp);
        } else {
            // TODO: i18n
            fail(req, resp, new NotSupportedException("Method " + method + " not supported"));
        }
    }

    void doDelete(final HttpServletRequest req, final HttpServletResponse resp) {
        try {
            // Parse out the required API versions.
            final AcceptAPIVersion acceptVersion = parseAcceptAPIVersion(req);

            // Prepare response.
            prepareResponse(req, resp);

            // Validate request.
            preprocessRequest(req);
            rejectIfNoneMatch(req);

            final Map<String, String[]> parameters = req.getParameterMap();
            final DeleteRequest request = RequestUtil.buildDeleteRequest(parameters, getResourceName(req), getIfMatch(req));
            doRequest(req, resp, acceptVersion, request);
        } catch (final Exception e) {
            fail(req, resp, e);
        }
    }

    void doGet(final HttpServletRequest req, final HttpServletResponse resp) {
        try {
            // Parse out the required API versions.
            final AcceptAPIVersion acceptVersion = parseAcceptAPIVersion(req);

            // Prepare response.
            prepareResponse(req, resp);

            // Validate request.
            preprocessRequest(req);
            rejectIfMatch(req);

            final Map<String, String[]> parameters = req.getParameterMap();
            if (hasParameter(req, PARAM_QUERY_ID) || hasParameter(req, PARAM_QUERY_EXPRESSION)
                    || hasParameter(req, PARAM_QUERY_FILTER)) {
                // Additional pre-validation for queries.
                rejectIfNoneMatch(req);

                // Query against collection.
                final QueryRequest request = RequestUtil.buildQueryRequest(parameters, getResourceName(req));
                doRequest(req, resp, acceptVersion, request);
            } else {
                // Read of instance within collection or singleton.
                final String rev = getIfNoneMatch(req);
                if (ETAG_ANY.equals(rev)) {
                    // FIXME: i18n
                    throw new PreconditionFailedException("If-None-Match * not appropriate for "
                            + getMethod(req) + " requests");
                }

                final ReadRequest request = RequestUtil.buildReadRequest(parameters, getResourceName(req));
                doRequest(req, resp, acceptVersion, request);
            }
        } catch (final Exception e) {
            fail(req, resp, e);
        }
    }

    void doPatch(final HttpServletRequest req, final HttpServletResponse resp) {
        try {
            // Parse out the required API versions.
            final AcceptAPIVersion acceptVersion = parseAcceptAPIVersion(req);

            // Prepare response.
            prepareResponse(req, resp);

            // Validate request.
            preprocessRequest(req);
            if (req.getHeader(HEADER_IF_NONE_MATCH) != null) {
                // FIXME: i18n
                throw new PreconditionFailedException(
                        "Use of If-None-Match not supported for PATCH requests");
            }

            final Map<String, String[]> parameters = HttpUtils.isMultiPartRequest(req.getContentType())
                    ? new HashMap<String, String[]>() : req.getParameterMap();
            final PatchRequest request = RequestUtil.buildPatchRequest(parameters, getResourceName(req),
                    getIfMatch(req), new JsonValue(HttpUtils.parseJsonBody(req, false)));
            doRequest(req, resp, acceptVersion, request);
        } catch (final Exception e) {
            fail(req, resp, e);
        }
    }

    void doPost(final HttpServletRequest req, final HttpServletResponse resp) {
        try {
            // Parse out the required API versions.
            final AcceptAPIVersion acceptVersion = parseAcceptAPIVersion(req);

            // Prepare response.
            prepareResponse(req, resp);

            // Validate request.
            preprocessRequest(req);
            rejectIfNoneMatch(req);
            rejectIfMatch(req);

            final Map<String, String[]> parameters = HttpUtils.isMultiPartRequest(req.getContentType())
                    ? new HashMap<String, String[]>() : req.getParameterMap();
            final String action = RequestUtil.asSingleValue(PARAM_ACTION, getParameter(req, PARAM_ACTION));
            if (action.equalsIgnoreCase(ACTION_ID_CREATE)) {
                final JsonValue content = getJsonContent(req);
                final CreateRequest request = RequestUtil.buildCreateRequest(parameters, getResourceName(req), content);
                doRequest(req, resp, acceptVersion, request);
            } else {
                // Action request.
                final JsonValue content = getJsonActionContent(req);
                final ActionRequest request = RequestUtil.buildActionRequest(parameters, getResourceName(req), action,
                        content);
                doRequest(req, resp, acceptVersion, request);
            }
        } catch (final Exception e) {
            fail(req, resp, e);
        }
    }

    void doPut(final HttpServletRequest req, final HttpServletResponse resp) {
        try {
            // Parse out the required API versions.
            final AcceptAPIVersion acceptVersion = parseAcceptAPIVersion(req);

            // Prepare response.
            prepareResponse(req, resp);

            // Validate request.
            preprocessRequest(req);

            if (req.getHeader(HEADER_IF_MATCH) != null
                    && req.getHeader(HEADER_IF_NONE_MATCH) != null) {
                // FIXME: i18n
                throw new PreconditionFailedException(
                        "Simultaneous use of If-Match and If-None-Match not "
                                + "supported for PUT requests");
            }

            final Map<String, String[]> parameters = HttpUtils.isMultiPartRequest(req.getContentType())
                    ? new HashMap<String, String[]>() : req.getParameterMap();
            final JsonValue content = getJsonContent(req);

            final String rev = getIfNoneMatch(req);
            if (ETAG_ANY.equals(rev)) {
                // This is a create with a user provided resource ID: split the
                // path into the parent resource name and resource ID.
                final ResourceName resourceName = getResourceName(req);
                if (resourceName.isEmpty()) {
                    // FIXME: i18n.
                    throw new BadRequestException("No new resource ID in HTTP PUT request");
                }

                final CreateRequest request = RequestUtil.buildCreateRequest(parameters, resourceName, content,
                        resourceName.leaf());
                doRequest(req, resp, acceptVersion, request);
            } else {
                final UpdateRequest request = RequestUtil.buildUpdateRequest(parameters, getResourceName(req), content,
                        getIfMatch(req));
                doRequest(req, resp, acceptVersion, request);
            }
        } catch (final Exception e) {
            fail(req, resp, e);
        }
    }

    private void doRequest(final HttpServletRequest req, final HttpServletResponse resp,
                           final AcceptAPIVersion acceptVersion, final Request request)
            throws ResourceException, Exception {
        final Context context = newRequestContext(req, acceptVersion);
        final ServletSynchronizer sync = syncFactory.createServletSynchronizer(req, resp);
        final RequestRunner runner = new RequestRunner(context, request, req, resp, sync);
        connectionFactory.getConnectionAsync(runner);
        sync.awaitIfNeeded(); // Only blocks when async is not supported.
    }

    /**
     * Gets the raw (still url-encoded) resource name from the request. Removes leading and trailing forward slashes.
     */
    private ResourceName getResourceName(final HttpServletRequest req) throws ResourceException {
        String resourceName = HttpUtils.getRawPathInfo(req);
        // Treat null path info as root resource.
        if (resourceName == null) {
            return ResourceName.empty();
        }
        try {
            return ResourceName.valueOf(resourceName);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private Context newRequestContext(final HttpServletRequest req, final AcceptAPIVersion acceptVersion)
           throws ResourceException {
        final Context root = contextFactory.createContext(req);
        return new AdviceContext(
                new AcceptAPIVersionContext(
                        new HttpContext(root, req), PROTOCOL_NAME, acceptVersion),
                RESTRICTED_HEADER_NAMES);
    }

    private void preprocessRequest(final HttpServletRequest req) throws ResourceException {
        // TODO: check Accept (including charset parameter) and Accept-Charset headers

        // Check content-type.
        final String contentType = req.getContentType();
        if (!req.getMethod().equalsIgnoreCase(HttpUtils.METHOD_GET) && contentType != null
                && !CONTENT_TYPE_REGEX.matcher(contentType).matches()
                && !HttpUtils.isMultiPartRequest(contentType)) {
            // TODO: i18n
            throw new BadRequestException(
                    "The request could not be processed because it specified the content-type '"
                            + req.getContentType() + "' when only the content-type '"
                            + MIME_TYPE_APPLICATION_JSON + "' and '"
                            + MIME_TYPE_MULTIPART_FORM_DATA + "' are supported");
        }

        if (req.getHeader("If-Modified-Since") != null) {
            // TODO: i18n
            throw new ConflictException("Header If-Modified-Since not supported");
        }

        if (req.getHeader("If-Unmodified-Since") != null) {
            // TODO: i18n
            throw new ConflictException("Header If-Unmodified-Since not supported");
        }
    }

    /**
     * Attempts to parse the version header and return a corresponding {@link AcceptAPIVersion} representation.
     * Further validates that the specified versions are valid. That being not in the future and no earlier
     * that the current major version.
     *
     * @param req
     *         The HTTP servlet request
     *
     * @return A non-null {@link AcceptAPIVersion} instance
     *
     * @throws BadRequestException
     *         If an invalid version is requested
     */
    private AcceptAPIVersion parseAcceptAPIVersion(final HttpServletRequest req) throws BadRequestException {
        // Extract out the protocol and resource versions.
        final String versionString = req.getHeader(ACCEPT_API_VERSION);

        final AcceptAPIVersion acceptAPIVersion = AcceptAPIVersion
                .newBuilder(versionString)
                .withDefaultProtocolVersion(PROTOCOL_VERSION)
                .expectsProtocolVersion()
                .build();

        final Version protocolVersion = acceptAPIVersion.getProtocolVersion();

        if (protocolVersion.getMajor() != PROTOCOL_VERSION.getMajor()) {
            throw new BadRequestException("Unsupported major version: " + protocolVersion);
        }

        if (protocolVersion.getMinor() > PROTOCOL_VERSION.getMinor()) {
            throw new BadRequestException("Unsupported minor version: " + protocolVersion);
        }

        return acceptAPIVersion;
    }

}
