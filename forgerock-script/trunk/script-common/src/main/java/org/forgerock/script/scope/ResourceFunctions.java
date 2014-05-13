/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012-2014 ForgeRock AS. All Rights Reserved
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */

package org.forgerock.script.scope;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.ActionRequest;
import org.forgerock.json.resource.BadRequestException;
import org.forgerock.json.resource.CreateRequest;
import org.forgerock.json.resource.DeleteRequest;
import org.forgerock.json.resource.FutureResult;
import org.forgerock.json.resource.NotFoundException;
import org.forgerock.json.resource.PatchOperation;
import org.forgerock.json.resource.PatchRequest;
import org.forgerock.json.resource.QueryFilter;
import org.forgerock.json.resource.QueryRequest;
import org.forgerock.json.resource.QueryResult;
import org.forgerock.json.resource.QueryResultHandler;
import org.forgerock.json.resource.ReadRequest;
import org.forgerock.json.resource.Request;
import org.forgerock.json.resource.Requests;
import org.forgerock.json.resource.Resource;
import org.forgerock.json.resource.ResourceException;
import org.forgerock.json.resource.ResultHandler;
import org.forgerock.json.resource.ServerContext;
import org.forgerock.json.resource.ServiceUnavailableException;
import org.forgerock.json.resource.UpdateRequest;

/**
 * Exposes a function that can be provided to a script to invoke.
 *
 * @author Laszlo Hordos
 */
public final class ResourceFunctions {

    private ResourceFunctions() {
    }

    public static final CreateFunction CREATE = new CreateFunction();

    /**
     * <pre>
     * create(String resourceContainer, String newResourceId, Map content[, Map params][, List fieldFilter][, Map context])
     * </pre>
     */
    public static final class CreateFunction extends AbstractFunction {

        /** Serializable class a version number. */
        static final long serialVersionUID = 1L;

        private CreateFunction() {
        }

        @Override
        public JsonValue call(Parameter scope, Function<?> callback, Object... arguments)
                throws ResourceException, NoSuchMethodException {
            String resourceContainer = null;
            String newResourceId = null;
            JsonValue content = null;
            JsonValue params = new JsonValue(null);
            List<Object> fieldFilter = null;
            JsonValue context = null;

            if (arguments.length < 3) {
                throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage("create",
                        arguments));
            }

            for (int i = 0; i < arguments.length; i++) {
                Object value = arguments[i];
                switch (i) {
                case 0:
                    if (value instanceof String) {
                        resourceContainer = (String) value;
                    } else {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "create", arguments));
                    }
                    break;
                case 1:
                    if (value instanceof String) {
                        newResourceId = (String) value;
                    } else if (null != value) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "create", arguments));
                    }
                    break;
                case 2:
                    if (value instanceof Map) {
                        content = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        content = (JsonValue) value;
                    } else {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "create", arguments));
                    }
                    break;
                case 3:
                    if (value instanceof Map) {
                        params = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        params = (JsonValue) value;
                    } else if (null != value && arguments.length > 4) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "create", arguments));
                    }
                    break;
                case 4:
                    if (value instanceof List) {
                        fieldFilter = (List<Object>) value;
                        break;
                    } else if (value instanceof JsonValue && ((JsonValue) value).isList()) {
                        fieldFilter = ((JsonValue) value).asList();
                        break;
                    } else if (null != value && arguments.length > 5) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "create", arguments));
                    }
                case 5:
                    if (value instanceof Map) {
                        context = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        context = (JsonValue) value;
                    } else if (null != value) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "create", arguments));
                    }
                    break;
                default: // TODO log unused arguments
                }
            }

            return create(scope, resourceContainer, newResourceId, content, params, fieldFilter, context,
                    callback).getContent();
        }

        public Resource create(final Parameter scope, String resourceContainer,
                String newResourceId, JsonValue content, JsonValue params, List<Object> fieldFilter,
                JsonValue context, final Function<?> callback) throws ResourceException {
            CreateRequest cr =
                    Requests.newCreateRequest(resourceContainer, newResourceId, new JsonValue(
                            content));
            // add fieldFilter
            cr.addField(fetchFields(fieldFilter));
            for (String name : params.keys()) {
                setAdditionalParameter(cr, name, params.get(name));
            }

            final ServerContext serverContext = scope.getServerContext(context);
            final FutureResult<Resource> future =
                    scope.getConnection().createAsync(serverContext, cr,
                            this.<Resource> getResultHandler(scope, callback));
            try {
                return future.get();
            } catch (final InterruptedException e) {
                throw interrupted(e);
            } finally {
                // Cancel the request if it hasn't completed.
                future.cancel(false);
            }
        }

    }

    public static final ReadFunction READ = new ReadFunction();

    /**
     * <pre>
     * read(String resourceName[, Map params][, List fieldFilter][,Map context])
     * </pre>
     */
    public static final class ReadFunction extends AbstractFunction {

        /** Serializable class a version number. */
        static final long serialVersionUID = 1L;

        private ReadFunction() {
        }

        @Override
        public JsonValue call(final Parameter scope, final Function<?> callback,
                Object... arguments) throws ResourceException, NoSuchMethodException {
            String resourceName = null;
            List<Object> fieldFilter = null;
            JsonValue params = new JsonValue(null);
            JsonValue context = null;
            if (arguments.length < 1) {
                throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage("read",
                        arguments));
            }

            for (int i = 0; i < arguments.length; i++) {
                Object value = arguments[i];
                switch (i) {
                case 0:
                    if (value instanceof String) {
                        resourceName = (String) value;
                    } else {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "read", arguments));
                    }
                    break;
                case 1:
                    if (value instanceof Map) {
                        params = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        params = (JsonValue) value;
                    } else if (null != value && arguments.length > 2) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "read", arguments));
                    }
                    break;
                case 2:
                    if (value instanceof List) {
                        fieldFilter = (List<Object>) value;
                        break;
                    } else if (value instanceof JsonValue && ((JsonValue) value).isList()) {
                        fieldFilter = ((JsonValue) value).asList();
                        break;
                    } else if (null != value && arguments.length > 3) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "read", arguments));
                    }
                case 3:
                    if (value instanceof Map) {
                        context = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        context = (JsonValue) value;
                    } else if (null != value) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "read", arguments));
                    }
                    break;
                default: // TODO log unused arguments
                }
            }

            JsonValue result = null;
			try {
				result = read(scope, resourceName, params, fieldFilter, context, callback).getContent();
			} catch (NotFoundException e) {
				// indicates no such record without throwing exception
				return null;
			}
            return result;
        }

        public Resource read(final Parameter parameter, String resourceName, JsonValue params,
                List<Object> fieldFilter, JsonValue context, final Function<?> callback)
                throws ResourceException {

            ReadRequest rr = Requests.newReadRequest(resourceName);
            // add fieldFilter
            rr.addField(fetchFields(fieldFilter));
            for (String name : params.keys()) {
                setAdditionalParameter(rr, name, params.get(name));
            }

            final ServerContext serverContext = parameter.getServerContext(context);
            final FutureResult<Resource> future =
                    parameter.getConnection().readAsync(serverContext, rr,
                            this.<Resource> getResultHandler(parameter, callback));
            try {
                return future.get();
            } catch (final InterruptedException e) {
                throw interrupted(e);
            } finally {
                // Cancel the request if it hasn't completed.
                future.cancel(false);
            }
        }
    }

    public static final UpdateFunction UPDATE = new UpdateFunction();

    /**
     * <pre>
     * update(String resourceName, String revision, Map content [, Map params][, List fieldFilter][,Map context])
     * </pre>
     */
    public static final class UpdateFunction extends AbstractFunction {

        /** Serializable class a version number. */
        static final long serialVersionUID = 1L;

        private UpdateFunction() {
        }

        @Override
        public JsonValue call(final Parameter scope, final Function<?> callback,
                final Object... arguments) throws ResourceException, NoSuchMethodException {

            String resourceName = null;
            String revision = null;
            JsonValue content = null;
            JsonValue params = new JsonValue(null);
            List<Object> fieldFilter = null;
            JsonValue context = null;

            if (arguments.length < 3) {
                throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage("update",
                        arguments));
            }

            for (int i = 0; i < arguments.length; i++) {
                Object value = arguments[i];
                switch (i) {
                case 0:
                    if (value instanceof String) {
                        resourceName = (String) value;
                    } else {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "update", arguments));
                    }
                    break;
                case 1:
                    if (value instanceof String) {
                        revision = (String) value;
                    } else if (null != value) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "update", arguments));
                    }
                    break;
                case 2:
                    if (value instanceof Map) {
                        content = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        content = (JsonValue) value;
                    } else {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "update", arguments));
                    }
                    break;
                case 3:
                    if (value instanceof Map) {
                        params = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        params = (JsonValue) value;
                    } else if (null != value && arguments.length > 4) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "update", arguments));
                    }
                    break;
                case 4:
                    if (value instanceof List) {
                        fieldFilter = (List<Object>) value;
                        break;
                    } else if (value instanceof JsonValue && ((JsonValue) value).isList()) {
                        fieldFilter = ((JsonValue) value).asList();
                        break;
                    } else if (null != value && arguments.length > 5) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "update", arguments));
                    }
                case 5:
                    if (value instanceof Map) {
                        context = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        context = (JsonValue) value;
                    } else if (null != value) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "update", arguments));
                    }
                    break;
                default: // TODO log unused arguments
                }
            }

            return update(scope, resourceName, revision, content, params, fieldFilter, context, callback)
                    .getContent();
        }

        private final Resource update(final Parameter scope, String resourceName, String revision,
                JsonValue content, JsonValue params, List<Object> fieldFilter, JsonValue context,
                final Function<?> callback) throws ResourceException {

            UpdateRequest ur = Requests.newUpdateRequest(resourceName, content);
            // add fieldFilter
            ur.addField(fetchFields(fieldFilter));
            // set revision
            ur.setRevision(revision);
            // set additional parameters
            for (String name : params.keys()) {
                setAdditionalParameter(ur, name, params.get(name));
            }

            final ServerContext serverContext = scope.getServerContext(context);
            final FutureResult<Resource> future =
                    scope.getConnection().updateAsync(serverContext, ur,
                            this.<Resource> getResultHandler(scope, callback));
            try {
                return future.get();
            } catch (final InterruptedException e) {
                throw interrupted(e);
            } finally {
                // Cancel the request if it hasn't completed.
                future.cancel(false);
            }
        }
    }

    public static final PatchFunction PATCH = new PatchFunction();

    /**
     * <pre>
     * patch(String resourceName, String revision, Map patch[, Map params][, List fieldFilter][,Map context])
     * </pre>
     */
    public static final class PatchFunction extends AbstractFunction {

        /** Serializable class a version number. */
        static final long serialVersionUID = 1L;

        private PatchFunction() {
        }

        @Override
        public JsonValue call(Parameter scope, Function<?> callback, Object... arguments)
                throws ResourceException, NoSuchMethodException {
            String resourceName = null;
            String revision = null;
            JsonValue patch = null;
            JsonValue params = new JsonValue(null);
            List<Object> fieldFilter = null;
            JsonValue context = null;

            if (arguments.length < 3) {
                throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage("patch",
                        arguments));
            }

            for (int i = 0; i < arguments.length; i++) {
                Object value = arguments[i];
                switch (i) {
                case 0:
                    if (value instanceof String) {
                        resourceName = (String) value;
                    } else {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "patch", arguments));
                    }
                    break;
                case 1:
                    if (value instanceof String) {
                        revision = (String) value;
                    } else if (null != value) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "patch", arguments));
                    }
                    break;
                case 2:
                    if (value instanceof List) {
                        patch = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isList()) {
                        patch = (JsonValue) value;
                    } else {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "patch", arguments));
                    }
                    break;
                case 3:
                    if (value instanceof Map) {
                        params = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        params = (JsonValue) value;
                    } else if (null != value && arguments.length > 4) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "patch", arguments));
                    }
                    break;
                case 4:
                    if (value instanceof List) {
                        fieldFilter = (List<Object>) value;
                        break;
                    } else if (value instanceof JsonValue && ((JsonValue) value).isList()) {
                        fieldFilter = ((JsonValue) value).asList();
                        break;
                    } else if (null != value && arguments.length > 5) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "patch", arguments));
                    }
                case 5:
                    if (value instanceof Map) {
                        context = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        context = (JsonValue) value;
                    } else if (null != value) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "patch", arguments));
                    }
                    break;
                default: // TODO log unused arguments
                }
            }

            return patch(scope, resourceName, revision, patch, params, fieldFilter, context, callback).getContent();
        }

        private final Resource patch(Parameter scope, String resourceName, String revision,
                JsonValue patch, JsonValue params, List<Object> fieldFilter, JsonValue context,
                final Function<?> callback) throws ResourceException {
            // create the request
            PatchRequest pr = Requests.newPatchRequest(resourceName);
            // add operations
            List<PatchOperation> ops = PatchOperation.valueOfList(patch);
            pr.addPatchOperation(ops.toArray(new PatchOperation[ops.size()]));
            // add fieldFilter
            pr.addField(fetchFields(fieldFilter));
            // set revision
            pr.setRevision(revision);
            // set additional params
            for (String name : params.keys()) {
                setAdditionalParameter(pr, name, params.get(name));
            }

            final ServerContext serverContext = scope.getServerContext(context);
            final FutureResult<Resource> future =
                    scope.getConnection().patchAsync(serverContext, pr,
                            this.<Resource> getResultHandler(scope, callback));
            try {
                return future.get();
            } catch (final InterruptedException e) {
                throw interrupted(e);
            } finally {
                // Cancel the request if it hasn't completed.
                future.cancel(false);
            }
        }
    }

    public static final QueryFunction QUERY = new QueryFunction();

    /**
     * <pre>
     * query(String resourceContainer, Map params [, List fieldFilter][,Map context])
     * </pre>
     */
    public static final class QueryFunction extends AbstractFunction {

        /** Serializable class a version number. */
        static final long serialVersionUID = 1L;

        private QueryFunction() {
        }

        @Override
        public JsonValue call(Parameter scope, final Function<?> callback, Object... arguments)
                throws ResourceException, NoSuchMethodException {

            String resourceContainer = null;
            JsonValue params = new JsonValue(null);
            List<Object> fieldFilter = null;
            JsonValue context = null;

            if (arguments.length < 2) {
                throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage("query",
                        arguments));
            }

            for (int i = 0; i < arguments.length; i++) {
                Object value = arguments[i];
                switch (i) {
                case 0:
                    if (value instanceof String) {
                        resourceContainer = (String) value;
                    } else {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "query", arguments));
                    }
                    break;
                case 1:
                    if (value instanceof Map) {
                        params = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        params = (JsonValue) value;
                    } else {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "query", arguments));
                    }
                    break;
                case 2:
                    if (value instanceof List) {
                        fieldFilter = (List<Object>) value;
                        break;
                    } else if (value instanceof JsonValue && ((JsonValue) value).isList()) {
                        fieldFilter = ((JsonValue) value).asList();
                        break;
                    } else if (null != value && arguments.length > 3) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "query", arguments));
                    }
                case 3:
                    if (value instanceof Map) {
                        context = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        context = (JsonValue) value;
                    } else if (null != value) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "query", arguments));
                    }
                    break;
                default: // TODO log unused arguments
                }
            }

            // warning: if you dont use poll or peek and only iterator()
            // (+.remove()) it will leak memory.
            LinkedList<Object> results =
                    null != callback ? null : new LinkedList<Object>();

            QueryResult queryResult =
                    query(scope, resourceContainer, params, fieldFilter, context, results, callback);

            JsonValue result = new JsonValue(new LinkedHashMap<String, Object>(3));
            if (null != queryResult) {
                result.put("pagedResultsCookie", queryResult.getPagedResultsCookie());
                result.put("remainingPagedResults", queryResult.getRemainingPagedResults());
            }
            if (null != results) {
                result.put("result", results);
            }
            return result;
        }

        private final QueryResult query(final Parameter scope, String resourceContainer,
                JsonValue params, List<Object> fieldFilter, JsonValue context,
                final Collection<Object> results, final Function<?> callback)
                throws ResourceException {
            if (params.isDefined("_queryId") ^ params.isDefined("_queryExpression")
                    ^ params.isDefined("_queryFilter")) {

                QueryRequest qr = Requests.newQueryRequest(resourceContainer);
                // add fieldFilter
                qr.addField(fetchFields(fieldFilter));
                for (String name : params.keys()) {
                    if (name.equalsIgnoreCase("_fields")
                            && (null == fieldFilter || fieldFilter.isEmpty())) {
                        JsonValue fields = params.get(name);
                        if (fields.isString()) {
                            try {
                                qr.addSortKey(fields.asString().split(","));
                            } catch (final IllegalArgumentException e) {
                                // FIXME: i18n.
                                throw new BadRequestException(
                                        "The value '"
                                                + fields
                                                + "' for parameter '"
                                                + name
                                                + "' could not be parsed as a comma separated list of JSON pointers");
                            }
                        } else if (fields.isList()) {
                            qr.addSortKey(fields.asList().toArray(new String[fields.size()]));
                        }
                    } else if (name.equalsIgnoreCase("_sortKeys")) {
                        JsonValue sortKey = params.get(name);
                        if (sortKey.isString()) {
                            try {
                                qr.addSortKey(sortKey.asString().split(","));
                            } catch (final IllegalArgumentException e) {
                                // FIXME: i18n.
                                throw new BadRequestException("The value '" + sortKey
                                        + "' for parameter '" + name
                                        + "' could not be parsed as a comma "
                                        + "separated list of sort keys");
                            }
                        } else if (sortKey.isList()) {
                            qr.addSortKey(sortKey.asList().toArray(new String[sortKey.size()]));
                        }
                    } else if (name.equalsIgnoreCase("_queryId")) {
                        qr.setQueryId(params.get(name).required().asString());
                    } else if (name.equalsIgnoreCase("_queryExpression")) {
                        qr.setQueryExpression(params.get(name).required().asString());
                    } else if (name.equalsIgnoreCase("_pagedResultsCookie")) {
                        qr.setPagedResultsCookie(params.get(name).required().asString());
                    } else if (name.equalsIgnoreCase("_pagedResultsOffset")) {
                        qr.setPagedResultsOffset(params.get(name).required().asInteger());
                    } else if (name.equalsIgnoreCase("_pageSize")) {
                        qr.setPageSize(params.get(name).required().asInteger());
                    } else if (name.equalsIgnoreCase("_queryFilter")) {
                        final String s = params.get(name).required().asString();
                        try {
                            qr.setQueryFilter(QueryFilter.valueOf(s));
                        } catch (final IllegalArgumentException e) {
                            // FIXME: i18n.
                            throw new BadRequestException("The value '" + s + "' for parameter '"
                                    + name + "' could not be parsed as a valid query filter");

                        }
                    } else {
                        setAdditionalParameter(qr, name, params.get(name));
                    }
                }

                final ServerContext serverContext = scope.getServerContext(context);
                final FutureResult<QueryResult> future =
                        scope.getConnection().queryAsync(serverContext, qr,
                                new QueryResultHandler() {
                                    @Override
                                    public void handleError(final ResourceException error) {
                                        if (null != callback) {
                                            try {
                                                callback.call(scope, null, null, error
                                                        .toJsonValue().asMap());
                                            } catch (ResourceException e) {
                                                // TODO log
                                            } catch (NoSuchMethodException e) {
                                                // TODO log
                                            }
                                        }
                                    }

                                    @Override
                                    public boolean handleResource(final Resource resource) {
                                        if (null != callback) {
                                            try {
                                                callback.call(scope, null, null, resource
                                                        .getContent().getObject());
                                            } catch (ResourceException e) {
                                                // TODO log
                                                return false;
                                            } catch (NoSuchMethodException e) {
                                                // TODO log
                                                return false;
                                            }
                                        } else {
                                            results.add(resource.getContent().getObject());
                                        }
                                        return true;
                                    }

                                    @Override
                                    public void handleResult(final QueryResult result) {
                                        // TODO We don't need this
                                        if (null != callback) {
                                            JsonValue queryResult = null;
                                            if (null != result) {
                                                queryResult =
                                                        new JsonValue(
                                                                new LinkedHashMap<String, Object>(2));
                                                queryResult.put("pagedResultsCookie", result
                                                        .getPagedResultsCookie());
                                                queryResult.put("remainingPagedResults", result
                                                        .getRemainingPagedResults());
                                            }
                                            try {
                                                callback.call(scope, null, queryResult.asMap());
                                            } catch (ResourceException e) {
                                                // TODO log
                                            } catch (NoSuchMethodException e) {
                                                // TODO log
                                            }
                                        }
                                    }
                                });
                try {
                    return future.get();
                } catch (final InterruptedException e) {
                    throw interrupted(e);
                } finally {
                    // Cancel the request if it hasn't completed.
                    future.cancel(false);
                }

            } else {
                throw new BadRequestException(
                        "Only one of [_queryId, _queryExpression, _queryFilter] is supported; multiple detected");
            }
        }
    }

    public static final DeleteFunction DELETE = new DeleteFunction();

    /**
     * <pre>
     * delete(String resourceName, String revision [, Map params][, List fieldFilter][,Map context])
     * </pre>
     */
    public static final class DeleteFunction extends AbstractFunction {

        /** Serializable class a version number. */
        static final long serialVersionUID = 1L;

        private DeleteFunction() {
        }

        @Override
        public JsonValue call(Parameter scope, Function<?> callback, Object... arguments)
                throws ResourceException, NoSuchMethodException {
            String resourceName = null;
            String revision = null;
            JsonValue params = new JsonValue(null);
            List<Object> fieldFilter = null;
            JsonValue context = null;

            if (arguments.length < 2) {
                throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage("delete",
                        arguments));
            }

            for (int i = 0; i < arguments.length; i++) {
                Object value = arguments[i];
                switch (i) {
                case 0:
                    if (value instanceof String) {
                        resourceName = (String) value;
                    } else {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "delete", arguments));
                    }
                    break;
                case 1:
                    if (value instanceof String) {
                        revision = (String) value;
                    } else if (null != value) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "delete", arguments));
                    }
                    break;
                case 2:
                    if (value instanceof Map) {
                        params = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        params = (JsonValue) value;
                    } else if (null != value && arguments.length > 4) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "patch", arguments));
                    }
                    break;
                case 3:
                    if (value instanceof List) {
                        fieldFilter = (List<Object>) value;
                        break;
                    } else if (value instanceof JsonValue && ((JsonValue) value).isList()) {
                        fieldFilter = ((JsonValue) value).asList();
                        break;
                    } else if (null != value && arguments.length > 4) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "delete", arguments));
                    }
                case 4:
                    if (value instanceof Map) {
                        context = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        context = (JsonValue) value;
                    } else if (null != value) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "delete", arguments));
                    }
                    break;
                default: // TODO log unused arguments
                }
            }
            return delete(scope, resourceName, revision, params, fieldFilter, context, callback)
                    .getContent();
        }

        private Resource delete(Parameter scope, String resourceName, String revision, JsonValue params,
                List<Object> fieldFilter, JsonValue context, final Function<?> callback)
                throws ResourceException {

            DeleteRequest dr = Requests.newDeleteRequest(resourceName);
            // add fieldFilter
            dr.addField(fetchFields(fieldFilter));
            // set revision
            dr.setRevision(revision);
            // set additional parameters
            for (String name : params.keys()) {
                setAdditionalParameter(dr, name, params.get(name));
            }

            final ServerContext serverContext = scope.getServerContext(context);
            final FutureResult<Resource> future =
                    scope.getConnection().deleteAsync(serverContext, dr,
                            this.<Resource> getResultHandler(scope, callback));
            try {
                return future.get();
            } catch (final InterruptedException e) {
                throw interrupted(e);
            } finally {
                // Cancel the request if it hasn't completed.
                future.cancel(false);
            }
        }
    }

    public static final ActionFunction ACTION = new ActionFunction();

    /**
     * <pre>
     * action(String resourceName, [String actionId,] Map content, Map params [, List fieldFilter][,Map context])
     * </pre>
     */
    public static final class ActionFunction extends AbstractFunction {

        /** Serializable class a version number. */
        static final long serialVersionUID = 1L;

        private ActionFunction() {
        }

        @Override
        public JsonValue call(Parameter scope, Function<?> callback, Object... arguments)
                throws ResourceException, NoSuchMethodException {

            String resourceName = null;
            String actionId = null;
            JsonValue content = null;
            JsonValue params = new JsonValue(null);
            List<Object> fieldFilter = null;
            JsonValue context = null;

            if (arguments.length < 3) {
                throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage("action",
                        arguments));
            }

            int pointer = 0;
            for (int i = 0; i < arguments.length; i++) {
                Object value = arguments[i];
                switch (pointer) {
                case 0:
                    if (value instanceof String) {
                        resourceName = (String) value;
                    } else {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "action", arguments));
                    }
                    break;
                case 1:
                    if (value instanceof String) {
                        actionId = (String) value;
                        break;
                    } else if (null == value) {
                        break;
                    } else if (!(value instanceof Map) && (value instanceof JsonValue && !((JsonValue) value).isMap())) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "action", arguments));
                    } else {
                        // shift the param pointer
                        pointer++;
                    }
                case 2:
                    if (value instanceof Map) {
                        content = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        content = (JsonValue) value;
                    } else if (null != value) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "action", arguments));
                    }
                    break;
                case 3:
                    if (value instanceof Map) {
                        params = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        params = (JsonValue) value;
                    } else {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "action", arguments));
                    }
                    break;
                case 4:
                    if (value instanceof List) {
                        fieldFilter = (List<Object>) value;
                        break;
                    } else if (value instanceof JsonValue && ((JsonValue) value).isList()) {
                        fieldFilter = ((JsonValue) value).asList();
                        break;
                    } else if (null != value && arguments.length > 5) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "action", arguments));
                    }
                case 5:
                    if (value instanceof Map) {
                        context = new JsonValue(value);
                    } else if (value instanceof JsonValue && ((JsonValue) value).isMap()) {
                        context = (JsonValue) value;
                    } else if (null != value) {
                        throw new NoSuchMethodException(FunctionFactory.getNoSuchMethodMessage(
                                "action", arguments));
                    }
                    break;
                default: // TODO log unused arguments
                }
                pointer++;
            }

            return action(scope, resourceName, actionId, content, params, fieldFilter, context,
                    callback);
        }

        public JsonValue action(final Parameter scope, String resourceName, String actionId,
                JsonValue content, JsonValue params, List<Object> fieldFilter, JsonValue context,
                final Function<?> callback) throws ResourceException {

            ActionRequest ar =
                    Requests.newActionRequest(resourceName,
                            null != actionId ? actionId : params.get("_action").required().asString());
            // add fieldFilter
            ar.addField(fetchFields(fieldFilter));
            // set additional parameters
            for (String name : params.keys()) {
                setAdditionalParameter(ar, name, params.get(name));
            }
            // set content
            ar.setContent(content);

            final ServerContext serverContext = scope.getServerContext(context);
            final FutureResult<JsonValue> future =
                    scope.getConnection().actionAsync(serverContext, ar,
                            this.<JsonValue> getResultHandler(scope, callback));
            try {
                return future.get();
            } catch (final InterruptedException e) {
                throw interrupted(e);
            } finally {
                // Cancel the request if it hasn't completed.
                future.cancel(false);
            }
        }
    }

    private static abstract class AbstractFunction implements Function<JsonValue> {

        /** Serializable class a version number. */
        static final long serialVersionUID = 1L;

        // private static abstract class AbstractFunction {

        protected <T> ResultHandler<T> getResultHandler(final Parameter scope,
                final Function<?> callback) {
            return null == callback ? null : new ResultHandler<T>() {
                @Override
                public void handleError(ResourceException error) {
                    try {
                        callback.call(scope, null, null, error.toJsonValue().asMap());
                    } catch (ResourceException e) {
                        // TODO log
                    } catch (NoSuchMethodException e) {
                        // TODO log
                    }
                }

                @Override
                public void handleResult(T result) {
                    try {
                        if (result instanceof JsonValue) {
                            callback.call(scope, null, ((JsonValue) result).getObject());
                        } else if (result instanceof Resource) {
                            callback.call(scope, null, ((Resource) result).getContent().getObject());
                        }
                    } catch (ResourceException e) {
                        // TODO log
                    } catch (NoSuchMethodException e) {
                        // TODO log
                    }
                }
            };
        }

        protected ResourceException interrupted(final InterruptedException e) {
            // TODO: i18n?
            return new ServiceUnavailableException("Client thread interrupted", e);
        }

        protected String[] fetchFields(List<Object> fields) {
            if (null != fields && !fields.isEmpty()) {
                int idx = 0;
                String[] checkedFields = new String[fields.size()];
                for (Object o : fields) {
                    if (o instanceof String) {
                        checkedFields[idx++] = (String) o;
                    }
                }
                return Arrays.copyOfRange(checkedFields, 0, idx);
            }
            return new String[0];
        }

        protected void setAdditionalParameter(Request request, String name, JsonValue value) throws BadRequestException {
            if (value.isNull()) {
                // ignore null values
            } else if (value.isString()) {
                request.setAdditionalParameter(name, value.asString());
            } else if (value.isNumber()) {
                request.setAdditionalParameter(name, String.valueOf(value.asNumber()));
            } else if (value.isBoolean()) {
                request.setAdditionalParameter(name, String.valueOf(value.asBoolean()));
            } else {
                throw new BadRequestException("The value '" + String.valueOf(value.getObject())
                        + "' for additional parameter '" + name
                        + "' is not of expected type String");
            }
        }
    }
}
