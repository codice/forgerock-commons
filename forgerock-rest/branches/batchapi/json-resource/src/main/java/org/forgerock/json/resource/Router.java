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
 * Copyright 2012-2014 ForgeRock AS.
 */
package org.forgerock.json.resource;

import static org.forgerock.json.fluent.JsonValue.array;
import static org.forgerock.json.fluent.JsonValue.field;
import static org.forgerock.json.fluent.JsonValue.json;
import static org.forgerock.json.fluent.JsonValue.object;
import static org.forgerock.json.resource.Requests.copyOfActionRequest;
import static org.forgerock.json.resource.Requests.copyOfCreateRequest;
import static org.forgerock.json.resource.Requests.copyOfDeleteRequest;
import static org.forgerock.json.resource.Requests.copyOfPatchRequest;
import static org.forgerock.json.resource.Requests.copyOfQueryRequest;
import static org.forgerock.json.resource.Requests.copyOfReadRequest;
import static org.forgerock.json.resource.Requests.copyOfUpdateRequest;
import static org.forgerock.json.resource.Resources.newCollection;
import static org.forgerock.json.resource.Resources.newSingleton;
import static org.forgerock.json.resource.RoutingMode.EQUALS;
import static org.forgerock.json.resource.RoutingMode.STARTS_WITH;
import static org.forgerock.json.resource.ResourceException.FIELD_CODE;
import static org.forgerock.json.resource.ResourceException.FIELD_REASON;
import static org.forgerock.json.resource.ResourceException.FIELD_DETAIL;
import static org.forgerock.json.resource.Resource.FIELD_CONTENT;
import static org.forgerock.json.resource.QueryResult.FIELD_PAGED_RESULTS_COOKIE;
import static org.forgerock.json.resource.QueryResult.FIELD_REMAINING_PAGED_RESULTS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.forgerock.json.fluent.JsonPointer;
import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.UriRoute.RouteMatcher;

/**
 * A request handler which routes requests using URI template matching against
 * the request's resource name. Examples of valid URI templates include:
 *
 * <pre>
 * users
 * users/{userId}
 * users/{userId}/devices
 * users/{userId}/devices/{deviceId}
 * </pre>
 *
 * Routes may be added and removed from a router as follows:
 *
 * <pre>
 * RequestHandler users = ...;
 * Router router = new Router();
 * Route r1 = router.addRoute(EQUALS, &quot;users&quot;, users);
 * Route r2 = router.addRoute(EQUALS, &quot;users/{userId}&quot;, users);
 *
 * // Deregister a route.
 * router.removeRoute(r1, r2);
 * </pre>
 *
 * A request handler receiving a routed request may access the associated
 * route's URI template variables via
 * {@link RouterContext#getUriTemplateVariables()}. For example, a request
 * handler processing requests for the route users/{userId} may obtain the value
 * of {@code userId} as follows:
 *
 * <pre>
 * String userId = context.asContext(RouterContext.class).getUriTemplateVariables().get(&quot;userId&quot;);
 * </pre>
 *
 * During routing resource names are "relativized" by removing the leading path
 * components which matched the template. See the documentation for
 * {@link RouterContext} for more information.
 * <p>
 * <b>NOTE:</b> for simplicity this implementation only supports a small sub-set
 * of the functionality described in RFC 6570.
 *
 * @see RouterContext
 * @see Router
 * @see <a href="http://tools.ietf.org/html/rfc6570">RFC 6570 - URI Template
 *      </a>
 */
public final class Router implements RequestHandler, BatchRequestHandler {

    private volatile RequestHandler defaultRoute = null;
    private final Set<UriRoute> routes = new CopyOnWriteArraySet<UriRoute>();

    /**
     * Creates a new router with no routes defined.
     */
    public Router() {
        // Nothing to do.
    }

    /**
     * Creates a new router containing the same routes and default route as the
     * provided router. Changes to the returned router's routing table will not
     * impact the provided router.
     *
     * @param router
     *            The router to be copied.
     */
    public Router(final Router router) {
        this.defaultRoute = router.defaultRoute;
        this.routes.addAll(router.routes);
    }

    /**
     * Adds all of the routes defined in the provided router to this router. New
     * routes may be added while this router is processing requests.
     *
     * @param router
     *            The router whose routes are to be copied into this router.
     * @return This router.
     */
    public Router addAllRoutes(final Router router) {
        if (this != router) {
            routes.addAll(router.routes);
        }
        return this;
    }

    /**
     * Adds a new route to this router for the provided collection resource
     * provider. New routes may be added while this router is processing
     * requests.
     * <p>
     * The provided URI template must match the resource collection itself, not
     * resource instances. For example:
     *
     * <pre>
     * CollectionResourceProvider users = ...;
     * Router router = new Router();
     *
     * // This is valid usage: the template matches the resource collection.
     * router.addRoute("users", users);
     *
     * // This is invalid usage: the template matches resource instances.
     * router.addRoute("users/{userId}", users);
     * </pre>
     *
     * @param uriTemplate
     *            The URI template which request resource names must match.
     * @param provider
     *            The collection resource provider to which matching requests
     *            will be routed.
     * @return An opaque handle for the route which may be used for removing the
     *         route later.
     */
    public Route addRoute(final String uriTemplate,
            final CollectionResourceProvider provider) {
        return addRoute(STARTS_WITH, uriTemplate, newCollection(provider));
    }

    /**
     * Adds a new route to this router for the provided request handler. New
     * routes may be added while this router is processing requests.
     *
     * @param mode
     *            Indicates how the URI template should be matched against
     *            resource names.
     * @param uriTemplate
     *            The URI template which request resource names must match.
     * @param handler
     *            The request handler to which matching requests will be routed.
     * @return An opaque handle for the route which may be used for removing the
     *         route later.
     */
    public Route addRoute(final RoutingMode mode, final String uriTemplate,
            final RequestHandler handler) {
        return addRoute(new UriRoute(mode, uriTemplate, handler));
    }

    /**
     * Adds a new route to this router for the provided singleton resource
     * provider. New routes may be added while this router is processing
     * requests.
     *
     * @param uriTemplate
     *            The URI template which request resource names must match.
     * @param provider
     *            The singleton resource provider to which matching requests
     *            will be routed.
     * @return An opaque handle for the route which may be used for removing the
     *         route later.
     */
    public Route addRoute(final String uriTemplate,
            final SingletonResourceProvider provider) {
        return addRoute(EQUALS, uriTemplate, newSingleton(provider));
    }

    /**
     * Returns the request handler to be used as the default route for requests
     * which do not match any of the other defined routes.
     *
     * @return The request handler to be used as the default route.
     */
    public RequestHandler getDefaultRoute() {
        return defaultRoute;
    }

    @Override
    public void handleAction(final ServerContext context, final ActionRequest request,
            final ResultHandler<JsonValue> handler) {
        try {
            if (request.getAction().equals("batch")) {
                handleBatch(context, request, handler);
            } else {
                final RouteMatcher bestMatch = getBestRoute(context, request);
                final ActionRequest routedRequest = bestMatch.wasRouted()
                        ? copyOfActionRequest(request).setResourceName(bestMatch.getRemaining())
                        : request;
                bestMatch.getRequestHandler().handleAction(bestMatch.getServerContext(), routedRequest, handler);
            }
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
    }

    @Override
    public void handleCreate(final ServerContext context, final CreateRequest request,
            final ResultHandler<Resource> handler) {
        try {
            final RouteMatcher bestMatch = getBestRoute(context, request);
            final CreateRequest routedRequest = bestMatch.wasRouted()
                    ? copyOfCreateRequest(request).setResourceName(bestMatch.getRemaining())
                    : request;
            bestMatch.getRequestHandler().handleCreate(bestMatch.getServerContext(), routedRequest, handler);
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
    }

    @Override
    public void handleDelete(final ServerContext context, final DeleteRequest request,
            final ResultHandler<Resource> handler) {
        try {
            final RouteMatcher bestMatch = getBestRoute(context, request);
            final DeleteRequest routedRequest = bestMatch.wasRouted()
                    ? copyOfDeleteRequest(request).setResourceName(bestMatch.getRemaining())
                    : request;
            bestMatch.getRequestHandler().handleDelete(bestMatch.getServerContext(), routedRequest, handler);
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
    }

    @Override
    public void handlePatch(final ServerContext context, final PatchRequest request,
            final ResultHandler<Resource> handler) {
        try {
            final RouteMatcher bestMatch = getBestRoute(context, request);
            final PatchRequest routedRequest = bestMatch.wasRouted()
                    ? copyOfPatchRequest(request).setResourceName(bestMatch.getRemaining())
                    : request;
            bestMatch.getRequestHandler().handlePatch(bestMatch.getServerContext(), routedRequest, handler);
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
    }

    @Override
    public void handleQuery(final ServerContext context, final QueryRequest request,
            final QueryResultHandler handler) {
        try {
            final RouteMatcher bestMatch = getBestRoute(context, request);
            final QueryRequest routedRequest = bestMatch.wasRouted()
                    ? copyOfQueryRequest(request).setResourceName(bestMatch.getRemaining())
                    : request;
            bestMatch.getRequestHandler().handleQuery(bestMatch.getServerContext(), routedRequest, handler);
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
    }

    @Override
    public void handleRead(final ServerContext context, final ReadRequest request,
            final ResultHandler<Resource> handler) {
        try {
            final RouteMatcher bestMatch = getBestRoute(context, request);
            final ReadRequest routedRequest = bestMatch.wasRouted()
                    ? copyOfReadRequest(request).setResourceName(bestMatch.getRemaining())
                    : request;
            bestMatch.getRequestHandler().handleRead(bestMatch.getServerContext(), routedRequest, handler);
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
    }

    @Override
    public void handleUpdate(final ServerContext context, final UpdateRequest request,
            final ResultHandler<Resource> handler) {
        try {
            final RouteMatcher bestMatch = getBestRoute(context, request);
            final UpdateRequest routedRequest = bestMatch.wasRouted()
                    ? copyOfUpdateRequest(request).setResourceName(bestMatch.getRemaining())
                    : request;
            bestMatch.getRequestHandler().handleUpdate(bestMatch.getServerContext(), routedRequest, handler);
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
    }

    @Override
    public void handleBatch(final ServerContext context, final ActionRequest request,
                               final ResultHandler<JsonValue> handler) {
        final JsonValue results = json(array());

        boolean failOnError = Boolean.valueOf(request.getAdditionalParameters().get("failOnError"));
        final AtomicBoolean failed = new AtomicBoolean(false);

        // Sub-handlers to aggregate results for a single call to our handler.

        ResultHandler<JsonValue> subJsonHandler = new ResultHandler<JsonValue>() {
            @Override
            public void handleError(ResourceException error) {
                results.add(error.toJsonValue().getObject());
                failed.set(true);
            }

            @Override
            public void handleResult(JsonValue result) {
                if (result.isList()) {
                    for (JsonValue resultObj : result) {
                        results.add(resultObj.getObject());
                    }
                } else {
                    results.add(result.getObject());
                }
            }
        };

        ResultHandler<Resource> subResourceHandler = new ResultHandler<Resource>() {
            @Override
            public void handleError(ResourceException error) {
                results.add(error.toJsonValue().getObject());
                failed.set(true);
            }

            @Override
            public void handleResult(Resource result) {
                results.add(result.getContent().getObject());
            }
        };

        QueryResultHandler subQueryResultHandler = new QueryResultHandler() {
            final JsonValue resources = json(array());

            @Override
            public void handleError(ResourceException error) {
                results.add(error.toJsonValue().getObject());
                failed.set(true);
            }

            @Override
            public boolean handleResource(Resource resource) {
                resources.add(resource.getContent().getObject());
                return true;
            }

            @Override
            public void handleResult(QueryResult result) {
                results.add(object(
                        field(FIELD_PAGED_RESULTS_COOKIE, result.getPagedResultsCookie()),
                        field(FIELD_REMAINING_PAGED_RESULTS, result.getRemainingPagedResults()),
                        field(FIELD_CONTENT, resources)
                ));
                resources.clear();
            }
        };

        // Group batched requests where possible (based on matched route where the routed resource
        // supports batching), otherwise handle individually.
        RouteMatcher lastMatch = null;
        LinkedList<Request> subbatch = new LinkedList<Request>();
        for (Request batchedReq : parseBatchRequests(request)) {
            RouteMatcher bestMatch;
            try {
                bestMatch = getBestRoute(context, batchedReq);
            } catch (ResourceException e) {
                subJsonHandler.handleError(e);
                if (failOnError) {
                    break;
                } else {
                    continue;
                }
            }

            if (!(bestMatch.getRequestHandler() instanceof BatchRequestHandler)) {
                // Found a non-batch handler, send any pending sub-batched requests
                if (!subbatch.isEmpty() && lastMatch != null) {
                    ActionRequest batchedRequest = Requests.newActionRequest(batchedReq.getResourceName(), "batch")
                            .setContent(new JsonValue(subbatch));
                    ((BatchRequestHandler) lastMatch.getRequestHandler()).handleBatch(
                            lastMatch.getServerContext(), batchedRequest, subJsonHandler);

                    subbatch.clear();
                }

                // Deal with this non-sub-batched request.
                if (batchedReq instanceof ActionRequest) {
                    handleAction(context, (ActionRequest) batchedReq, subJsonHandler);
                } else if (batchedReq instanceof CreateRequest) {
                    handleCreate(context, (CreateRequest) batchedReq, subResourceHandler);
                } else if (batchedReq instanceof DeleteRequest) {
                    handleDelete(context, (DeleteRequest) batchedReq, subResourceHandler);
                } else if (batchedReq instanceof PatchRequest) {
                    handlePatch(context, (PatchRequest) batchedReq, subResourceHandler);
                } else if (batchedReq instanceof QueryRequest) {
                    handleQuery(context, (QueryRequest) batchedReq, subQueryResultHandler);
                } else if (batchedReq instanceof ReadRequest) {
                    handleRead(context, (ReadRequest) batchedReq, subResourceHandler);
                } else if (batchedReq instanceof UpdateRequest) {
                    handleUpdate(context, (UpdateRequest) batchedReq, subResourceHandler);
                }
            } else if (bestMatch.equals(lastMatch) || lastMatch == null) {
                // Another request for the sub-batch.
                subbatch.add(batchedReq);
            } else {
                // Found a sub-batch-able request that does not match the existing sub-batch. Send the
                // existing sub-batch and start a new one.
                ActionRequest batchedRequest = Requests.newActionRequest(batchedReq.getResourceName(), "batch")
                        .setContent(new JsonValue(subbatch));
                ((BatchRequestHandler) lastMatch.getRequestHandler()).handleBatch(
                        lastMatch.getServerContext(), batchedRequest, subJsonHandler);

                subbatch.clear();
                subbatch.add(batchedReq);
            }
            lastMatch = bestMatch;

            if (failOnError && failed.get()) {
                break;
            }
        }

        // Handle "leftover" batched requests
        if (!subbatch.isEmpty() && lastMatch != null &&
                !(failOnError && Boolean.valueOf(failed.get()).equals(Boolean.TRUE))) {
            ActionRequest batchedRequest = Requests.newActionRequest(subbatch.get(0).getResourceName(), "batch")
                    .setContent(new JsonValue(subbatch));
            ((BatchRequestHandler) lastMatch.getRequestHandler()).handleBatch(
                    lastMatch.getServerContext(), batchedRequest, subJsonHandler);
        }

        handler.handleResult(new JsonValue(results));
    }

    private LinkedList<Request> parseBatchRequests(ActionRequest request) {
        LinkedList<Request> requests = new LinkedList<Request>();

        final String strRequestType = "requestType";
        final JsonPointer ptrRequestTypeDetail = new JsonPointer("/requestType/detail");
        final String strParameters = "parameters";
        final String strResource = "resource";
        final JsonPointer ptrResourceId = new JsonPointer("/resource/id");
        final String strRevision = "revision";
        final String strContent = "content";

        final String SORT_KEYS_DELIMITER = ",";

        for (JsonValue object : request.getContent()) {
            Request newRequest = null;

            Map<String, String[]> parameters = parseParameters(object.get(strParameters));
            ResourceName resourceName = ResourceName.valueOf(object.get(strResource).asString());
            String revision = object.get(strRevision).asString();
            JsonValue content = object.get(strContent);

            try {
                switch(object.get(strRequestType).asEnum(RequestType.class)) {
                    case ACTION:
                        newRequest = RequestUtil.buildActionRequest(parameters, resourceName,
                                object.get(ptrRequestTypeDetail).asString(), content);
                        break;
                    case CREATE:
                        newRequest = RequestUtil.buildCreateRequest(parameters, resourceName, content,
                                object.get(ptrResourceId).asString());
                        break;
                    case DELETE:
                        newRequest = RequestUtil.buildDeleteRequest(parameters, resourceName, revision);
                        break;
                    case PATCH:
                        newRequest = RequestUtil.buildPatchRequest(parameters, resourceName, revision, content);
                        break;
                    case QUERY:
                        newRequest = RequestUtil.buildQueryRequest(parameters, resourceName);
                        break;
                    case READ:
                        newRequest = RequestUtil.buildReadRequest(parameters, resourceName);
                        break;
                    case UPDATE:
                        newRequest = RequestUtil.buildUpdateRequest(parameters, resourceName, content, revision);
                        break;
                }

                if (newRequest != null) {
                    requests.add(newRequest);
                }
            } catch (BadRequestException e) {
                // TODO handle
            } catch (ResourceException e) {
                // TODO handle
            }
        }

        return requests;
    }

    private Map<String, String[]> parseParameters(JsonValue object) {
        Map<String, String[]> parameters = new HashMap<String, String[]>();
        for (String key : object.keys()) {
            JsonValue value = object.get(key);
            List<String> values = new ArrayList<String>();
            if (value.isList()) {
                values.addAll(value.asList(String.class));
            } else {
                values.add(value.asString());
            }
            parameters.put(key, values.toArray(new String[values.size()]));
        }
        return parameters;
    }

    /**
     * Removes all of the routes from this router. Routes may be removed while
     * this router is processing requests.
     *
     * @return This router.
     */
    public Router removeAllRoutes() {
        routes.clear();
        return this;
    }

    /**
     * Removes one or more routes from this router. Routes may be removed while
     * this router is processing requests.
     *
     * @param routes
     *            The routes to be removed.
     * @return {@code true} if at least one of the routes was found and removed.
     */
    public boolean removeRoute(final Route... routes) {
        boolean isModified = false;
        for (final Route route : routes) {
            isModified |= this.routes.remove(route);
        }
        return isModified;
    }

    /**
     * Sets the request handler to be used as the default route for requests
     * which do not match any of the other defined routes.
     *
     * @param handler
     *            The request handler to be used as the default route.
     * @return This router.
     */
    public Router setDefaultRoute(final RequestHandler handler) {
        this.defaultRoute = handler;
        return this;
    }

    Route addRoute(final UriRoute route) {
        routes.add(route);
        return route;
    }

    private RouteMatcher getBestRoute(final ServerContext context, final Request request)
            throws ResourceException {
        RouteMatcher bestMatcher = null;
        for (final UriRoute route : routes) {
            final RouteMatcher matcher = route.getRouteMatcher(context, request);
            if (matcher != null && matcher.isBetterMatchThan(bestMatcher)) {
                bestMatcher = matcher;
            }
        }
        if (bestMatcher != null) {
            return bestMatcher;
        }
        final RequestHandler handler = defaultRoute;

        /*
        * Passing the resourceName through explicitly means if an incorrect version was requested the error returned
        * is specific to the endpoint requested.
        */
        if (handler != null) {
            return new RouteMatcher(context, handler, request.getResourceName());
        }

        // TODO: i18n
        throw new NotFoundException(String.format("Resource '%s' not found", request
                .getResourceName()));
    }
}
