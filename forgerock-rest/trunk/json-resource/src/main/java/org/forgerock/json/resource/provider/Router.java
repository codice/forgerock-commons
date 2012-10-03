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
 * Copyright 2012 ForgeRock AS.
 */

package org.forgerock.json.resource.provider;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.ActionRequest;
import org.forgerock.json.resource.Context;
import org.forgerock.json.resource.ContextAttribute;
import org.forgerock.json.resource.CreateRequest;
import org.forgerock.json.resource.DeleteRequest;
import org.forgerock.json.resource.PatchRequest;
import org.forgerock.json.resource.QueryRequest;
import org.forgerock.json.resource.QueryResultHandler;
import org.forgerock.json.resource.ReadRequest;
import org.forgerock.json.resource.Request;
import org.forgerock.json.resource.Resource;
import org.forgerock.json.resource.ResultHandler;
import org.forgerock.json.resource.UpdateRequest;
import org.forgerock.json.resource.exception.NotFoundException;
import org.forgerock.json.resource.exception.ResourceException;
import org.forgerock.json.resource.provider.Route.RouteMatcher;

/**
 * A request handler which routes requests using URI template matching against
 * the request's resource name. Examples of valid URI templates include:
 * 
 * <pre>
 * /users
 * /users/{userId}
 * /users/{userId}/devices
 * /users/{userId}/devices/{deviceId}
 * </pre>
 * 
 * Routes may be added and removed from a router as follows:
 * 
 * <pre>
 * RequestHandler users = ...;
 * Router router = new Router();
 * Route r1 = router.addRoute(EQUALS, &quot;/users&quot;, users);
 * Route r2 = router.addRoute(EQUALS, &quot;/users/{userId}&quot;, users);
 * 
 * // Deregister a route.
 * router.removeRoute(r1, r2);
 * </pre>
 * 
 * A request handler receiving a routed request may access the associated
 * route's URI template variables via the {@link Router#URI_TEMPLATE_VARIABLES}
 * context attribute. For example, a request handler processing requests for the
 * route /user/{userId} may obtain the value of {@code userId} as follows:
 * 
 * <pre>
 * String userId = URI_TEMPLATE_VARIABLES.get(context).get(&quot;userId&quot;);
 * </pre>
 * <p>
 * <b>NOTE:</b> for simplicity this implementation only supports a small sub-set
 * of the functionality described in RFC 6570.
 * 
 * @see <a href="http://tools.ietf.org/html/rfc6570">RFC 6570 - URI Template
 *      </a>
 */
public final class Router implements RequestHandler {
    /**
     * The context attribute {@code uri-template-variables} whose value is a
     * {@code Map} containing the parsed URI template variables, keyed on the
     * URI template variable name. This context attribute will be added to the
     * context once a request has been routed.
     */
    // @formatter:off
    public static final ContextAttribute<Map<String, String>> URI_TEMPLATE_VARIABLES =
            new ContextAttribute<Map<String, String>>("uri-template-variables",
                    Collections.<String, String> emptyMap());
    // @formatter:on

    private volatile RequestHandler defaultRoute = null;
    private final Set<Route> routes = new CopyOnWriteArraySet<Route>();

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
     * resource instances. In addition, the URI template must not contain a
     * {@code id} template variable since this will be implicitly added to the
     * template in order for matching against resource instances. For example:
     * 
     * <pre>
     * CollectionResourceProvider users = ...;
     * Router router = new Router();
     * 
     * // This is valid usage: the template matches the resource collection.
     * router.addRoute(EQUALS, "/users", users);
     * 
     * // This is invalid usage: the template matches resource instances.
     * router.addRoute(EQUALS, "/users/{userId}", users);
     * </pre>
     * 
     * @param mode
     *            Indicates how the URI template should be matched against
     *            resource names.
     * @param uriTemplate
     *            The URI template which request resource names must match.
     * @param provider
     *            The collection resource provider to which matching requests
     *            will be routed.
     * @return An opaque handle for the route which may be used for removing the
     *         route later.
     */
    public Route addRoute(final RoutingMode mode, final String uriTemplate,
            final CollectionResourceProvider provider) {
        return RequestHandlers.addCollectionRoutes(this, mode, uriTemplate, provider);
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
        return addRoute(new Route(mode, uriTemplate, handler, null));
    }

    /**
     * Adds a new route to this router for the provided singleton resource
     * provider. New routes may be added while this router is processing
     * requests.
     * 
     * @param mode
     *            Indicates how the URI template should be matched against
     *            resource names.
     * @param uriTemplate
     *            The URI template which request resource names must match.
     * @param provider
     *            The singleton resource provider to which matching requests
     *            will be routed.
     * @return An opaque handle for the route which may be used for removing the
     *         route later.
     */
    public Route addRoute(final RoutingMode mode, final String uriTemplate,
            final SingletonResourceProvider provider) {
        return addRoute(mode, uriTemplate, RequestHandlers.newSingleton(provider));
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
    public void handleAction(final Context context, final ActionRequest request,
            final ResultHandler<JsonValue> handler) {
        try {
            getBestRoute(context, request).handleAction(context, request, handler);
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
    }

    @Override
    public void handleCreate(final Context context, final CreateRequest request,
            final ResultHandler<Resource> handler) {
        try {
            getBestRoute(context, request).handleCreate(context, request, handler);
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
    }

    @Override
    public void handleDelete(final Context context, final DeleteRequest request,
            final ResultHandler<Resource> handler) {
        try {
            getBestRoute(context, request).handleDelete(context, request, handler);
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
    }

    @Override
    public void handlePatch(final Context context, final PatchRequest request,
            final ResultHandler<Resource> handler) {
        try {
            getBestRoute(context, request).handlePatch(context, request, handler);
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
    }

    @Override
    public void handleQuery(final Context context, final QueryRequest request,
            final QueryResultHandler handler) {
        try {
            getBestRoute(context, request).handleQuery(context, request, handler);
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
    }

    @Override
    public void handleRead(final Context context, final ReadRequest request,
            final ResultHandler<Resource> handler) {
        try {
            getBestRoute(context, request).handleRead(context, request, handler);
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
    }

    @Override
    public void handleUpdate(final Context context, final UpdateRequest request,
            final ResultHandler<Resource> handler) {
        try {
            getBestRoute(context, request).handleUpdate(context, request, handler);
        } catch (final ResourceException e) {
            handler.handleError(e);
        }
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

            // Remove the sub-route if present (e.g. for collections).
            if (route.getSubRoute() != null) {
                this.routes.remove(route.getSubRoute());
            }
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

    Route addRoute(final Route route) {
        routes.add(route);
        return route;
    }

    private RequestHandler getBestRoute(final Context context, final Request request)
            throws ResourceException {
        RouteMatcher bestMatcher = null;
        for (final Route route : routes) {
            final RouteMatcher matcher = route.getRouteMatcher(request);
            if (matcher != null && matcher.isBetterMatchThan(bestMatcher)) {
                bestMatcher = matcher;
            }
        }
        if (bestMatcher != null) {
            URI_TEMPLATE_VARIABLES.set(context, bestMatcher.variables());
            return bestMatcher.getRoute().getRequestHandler();
        }
        final RequestHandler handler = defaultRoute;
        if (handler != null) {
            return handler;
        }
        // TODO: i18n
        throw new NotFoundException(String.format("Resource '%s' not found", request
                .getResourceName()));
    }
}
