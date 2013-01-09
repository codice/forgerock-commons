/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 ForgeRock AS. All Rights Reserved
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.ActionRequest;
import org.forgerock.json.resource.Connection;
import org.forgerock.json.resource.ConnectionProvider;
import org.forgerock.json.resource.Context;
import org.forgerock.json.resource.PersistenceConfig;
import org.forgerock.json.resource.Requests;
import org.forgerock.json.resource.ResourceException;
import org.forgerock.json.resource.ResultHandler;
import org.forgerock.json.resource.RootContext;
import org.forgerock.json.resource.ServerContext;

/**
 * Exposes a function that can be provided to a script to invoke.
 *
 * @author Laszlo Hordos
 */
public enum ConnectionFunction implements Function {

    // public enum Operation implements ConnectionFunction {

    /**
     * <pre>
     * create(String endPoint[, String id], Map content[, List fieldFilter][,Map context])
     * </pre>
     */
    CREATE {
        private final JsonValue create(OperationParameter parameter, String endPoint, String id,
                                       Map<String, Object> content, List<String> fieldFilter,
                                       Map<String, Object> context) {
            return null;
        }
    },
    /**
     * <pre>
     * read(String endPoint[, String id][, List fieldFilter][,Map context])
     * </pre>
     */
    READ {
        public final JsonValue read(OperationParameter parameter, String endPoint) throws Exception {
            return read(parameter, endPoint, null, null, null);
        }

        public final JsonValue read(OperationParameter parameter, String endPoint, String id)
                throws Exception {
            return read(parameter, endPoint, id, null, null);
        }

        public final JsonValue read(OperationParameter parameter, String endPoint,
                                    List<String> fieldFilter) throws Exception {
            return null;
        }

        public final JsonValue read(OperationParameter parameter, String endPoint, String id,
                                    List<String> fieldFilter) throws Exception {
            return null;
        }

        public final JsonValue read(OperationParameter parameter, String endPoint,
                                    Map<String, Object> context) throws Exception {
            return null;
        }

        public final JsonValue read(OperationParameter parameter, String endPoint, String id,
                                    Map<String, Object> context) throws Exception {
            return null;
        }

        public final JsonValue read(OperationParameter parameter, String endPoint,
                                    List<String> fieldFilter, Map<String, Object> context) throws Exception {
            return null;
        }

        public JsonValue read(OperationParameter parameter, String endPoint, String id,
                              List<String> fieldFilter, Map<String, Object> context) throws Exception {
            JsonValue result = new JsonValue(new LinkedHashMap<String, Object>());
            result.put("_id", id != null ? endPoint + "/" + id : endPoint);
            return result;
        }
    },
    /**
     * <pre>
     * update(String endPoint[, String id], Map content [, String rev][, List fieldFilter][,Map context])
     * </pre>
     */
    UPDATE {
        private final JsonValue update(OperationParameter parameter, String endPoint, String id,
                                       Map<String, Object> content, String rev, List<String> fieldFilter,
                                       Map<String, Object> context) {
            return null;
        }
    },
    /**
     * <pre>
     * patch(String endPoint[, String id], Map content [, String rev][, List fieldFilter][,Map context])
     * </pre>
     */
    PATCH {
        private final JsonValue patch(OperationParameter parameter, String endPoint, String id,
                                      Map<String, Object> content, String rev, List<String> fieldFilter,
                                      Map<String, Object> context) {
            return null;
        }
    },
    /**
     * <pre>
     * query(String endPoint[, Map params][, String filter][, List fieldFilter][,Map context])
     * </pre>
     */
    QUERY {
        private final JsonValue query(OperationParameter parameter, String endPoint,
                                      Map<String, Object> params, String filter, List<String> fieldFilter,
                                      Map<String, Object> context) {
            return null;
        }
    },
    /**
     * <pre>
     * delete(String endPoint, String id[, String rev][, List fieldFilter][,Map context])
     * </pre>
     */
    DELETE {
        private final JsonValue delete(OperationParameter parameter, String endPoint, String id,
                                       String rev, List<String> fieldFilter, Map<String, Object> context) {
            return null;
        }
    },
    /**
     * <pre>
     * action(String endPoint[, String id], String type, Map params, Map content[, List fieldFilter][,Map context])
     * </pre>
     */
    ACTION {
        public Object action(final OperationParameter parameter, String endPoint, String type,
                             Map<String, Object> params, Map<String, Object> content) throws Exception {
            return action(parameter, endPoint, type, params, content, null, null);
        }


        public Object action(final OperationParameter parameter, String endPoint, String type,
                             Map<String, Object> params, Map<String, Object> content, List<String> fieldFilter)
                throws Exception {
            return action(parameter, endPoint, type, params, content, fieldFilter, null);
        }


        public Object action(final OperationParameter parameter, String endPoint, String type,
                             Map<String, Object> params, Map<String, Object> content, Map<String, Object> context)
                throws Exception {
            return action(parameter, endPoint, type, params, content, null, context);
        }


        public Object action(final OperationParameter parameter, String endPoint,
                             String type, Map<String, Object> params, Map<String, Object> content,
                             List<String> fieldFilter, Map<String, Object> context) throws Exception {
            ActionRequest request = Requests.newActionRequest(endPoint, type);
            request.setContent(new JsonValue(content));
            if (null != fieldFilter) {
                request.addField(fieldFilter.toArray(new String[fieldFilter.size()]));
            }
            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (null == entry.getValue()) {
                    continue;
                }
                if (entry.getValue() instanceof String) {
                    request.setAdditionalActionParameter(entry.getKey(), entry.getValue()
                            .toString());
                } else {

                }
            }
                      //TODO FIX the context
            if (null == content) {
                PersistenceConfig config = PersistenceConfig.builder().classLoader(null)
                        .connectionProvider(new ConnectionProvider() {
                            public Connection getConnection(String connectionId) throws ResourceException {
                                return parameter.getConnection();
                            }

                            public String getConnectionId(Connection connection) throws ResourceException {
                                return "";
                            }
                        }).build();

                ServerContext serverContext = ServerContext.loadFromJson(new JsonValue(context), config);

            }

            Connection connection = parameter.getConnection();
            try {
                if (null != parameter.getCallbackFunction()) {
                    //Copy the parameter without the callback function.
                    final OperationParameter parameterCopy = new OperationParameter(parameter);
                    return connection.actionAsync(getContext(parameter, context),
                            request, new ResultHandler<JsonValue>() {
                        public void handleError(ResourceException error) {
                            try {
                                parameter.getCallbackFunction().call(parameterCopy,
                                        new Object[]{error.toJsonValue()});
                            } catch (Exception e) {
                                /* TODO Handle exception */
                            }
                        }

                        public void handleResult(JsonValue result) {
                            try {
                                parameter.getCallbackFunction().call(parameterCopy,
                                        new Object[]{result.getObject()});
                            } catch (Exception e) {
                                /* TODO Handle exception */
                            }
                        }
                    });
                }

                return connection.action(getContext(parameter, context), request);
            } finally {
                //TODO InternalConnection can not be closed either!!
                // connection.close();
            }
        }
    };

    private static Context getContext(final OperationParameter parameter,
                                      Map<String, Object> context) {
        /*
        if (null != context) {
            return Context.valueOf(new JsonValue(context));
        } else if (null != parameter.getContext()) {
            return parameter.getContext();
        }
        return Context.newRootContext().newSubContext("resource");
        */
        return new RootContext();
    }

    /**
     * @param parameter
     * @param arguments
     *         could be a single value or a List of values
     * @return
     * @throws Exception
     * @throws NoSuchMethodException
     */
    public Object call(final OperationParameter parameter, Object[] arguments) throws Exception {
        if (null == arguments) {
            throw new IllegalArgumentException("Arguments are required");
        }

        Object[] checkedArguments = new Object[arguments.length + 1];
        Class<?>[] parameterTypes = new Class<?>[arguments.length + 1];
        parameterTypes[0] = OperationParameter.class;
        final Function[] callbackFunction = new Function[1];
        int index = 1;
        for (Object value : arguments) {
            if (value instanceof JsonValue) {
                value = ((JsonValue) value).getObject();
            }
            if (null == value) {
                parameterTypes[index] = Object.class;
            } else if (value instanceof Function) {
                if (null == callbackFunction) {
                    callbackFunction[0] = (Function) value;
                    continue;
                }
                throw new IllegalArgumentException("More then one callback function found");
            } else if (value instanceof Map) {
                parameterTypes[index] = Map.class;
            } else if (value instanceof List) {
                parameterTypes[index] = List.class;
            } else {
                parameterTypes[index] = value.getClass();
            }
            checkedArguments[index] = value;
            index++;
        }
        // Throws NoSuchMethodException
        Method method =
                getClass().getMethod(name().toLowerCase(), Arrays.copyOf(parameterTypes, index));

        try {
            if (callbackFunction.length > 0) {
                checkedArguments[0] = new OperationParameter(parameter) {
                    public Function getCallbackFunction() {

                        return callbackFunction[0];

                    }
                };
            } else {
                checkedArguments[0] = parameter;
            }
            return method.invoke(this, Arrays.copyOf(checkedArguments, index));
        } catch (IllegalAccessException e) {
            // Should not happen
            /* ignore */
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // Should not happen
            /* ignore */
            e.printStackTrace();
        }
        return null;
    }

    public <R, P> R accept(ScriptableVisitor<R, P> v, P p) {
        return v.visitFunction(p, this);
    }

    // }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @param connection
     * @param arguments
     *            could be a single value or a List of values
     * @return computed result
     * @throws Exception
     *             if unable to compute a result
     */
    // Object call(Context context, Connection connection, Object[] arguments)
    // throws Exception;

}
