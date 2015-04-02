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
 * information: "Portions Copyright [year] [name of copyright owner]".
 *
 * Copyright 2015 ForgeRock AS.
 */
package org.forgerock.json.resource;

import org.forgerock.json.fluent.JsonValue;

import java.util.Map;

/**
 * A utility class to instantiate and configure {@link Request} objects.
 */
public class RequestUtil {
    /** the HTTP request parameter to request pretty printing. */
    public static final String PARAM_PRETTY_PRINT = "_prettyPrint";
    /** the HTTP request parameter to request a certain mimetype for a filed. */
    public static final String PARAM_MIME_TYPE = "mimeType";

    private static final String FIELDS_DELIMITER = ",";
    private static final String SORT_KEYS_DELIMITER = ",";

    public static DeleteRequest buildDeleteRequest(Map<String, String[]> parameters, ResourceName resourceName,
                                                   String revision) throws ResourceException {
        final DeleteRequest request = Requests.newDeleteRequest(ResourceName.urlDecode(resourceName))
                .setRevision(revision);
        for (final Map.Entry<String, String[]> p : parameters.entrySet()) {
            final String name = p.getKey();
            final String[] values = p.getValue();
            if (parseCommonParameter(name, values, request)) {
                continue;
            } else {
                request.setAdditionalParameter(name, asSingleValue(name, values));
            }
        }
        return request;
    }

    public static QueryRequest buildQueryRequest(Map<String, String[]> parameters, ResourceName resourceName)
            throws ResourceException {
        QueryRequest request = Requests.newQueryRequest(ResourceName.urlDecode(resourceName));

        for (final Map.Entry<String, String[]> p : parameters.entrySet()) {
            final String name = p.getKey();
            final String[] values = p.getValue();

            if (parseCommonParameter(name, values, request)) {
                continue;
            } else if (name.equalsIgnoreCase(QueryRequest.FIELD_SORT_KEYS)) {
                for (final String s : values) {
                    try {
                        request.addSortKey(s.split(SORT_KEYS_DELIMITER));
                    } catch (final IllegalArgumentException e) {
                        // FIXME: i18n.
                        throw new BadRequestException("The value '" + s
                                + "' for parameter '" + name
                                + "' could not be parsed as a comma "
                                + "separated list of sort keys");
                    }
                }
            } else if (name.equalsIgnoreCase(QueryRequest.FIELD_QUERY_ID)) {
                request.setQueryId(asSingleValue(name, values));
            } else if (name.equalsIgnoreCase(QueryRequest.FIELD_QUERY_EXPRESSION)) {
                request.setQueryExpression(asSingleValue(name, values));
            } else if (name.equalsIgnoreCase(QueryRequest.FIELD_PAGED_RESULTS_COOKIE)) {
                request.setPagedResultsCookie(asSingleValue(name, values));
            } else if (name.equalsIgnoreCase(QueryRequest.FIELD_PAGED_RESULTS_OFFSET)) {
                request.setPagedResultsOffset(asIntValue(name, values));
            } else if (name.equalsIgnoreCase(QueryRequest.FIELD_PAGE_SIZE)) {
                request.setPageSize(asIntValue(name, values));
            } else if (name.equalsIgnoreCase(QueryRequest.FIELD_QUERY_FILTER)) {
                final String s = asSingleValue(name, values);
                try {
                    request.setQueryFilter(QueryFilter.valueOf(s));
                } catch (final IllegalArgumentException e) {
                    // FIXME: i18n.
                    throw new BadRequestException("The value '" + s + "' for parameter '"
                            + name + "' could not be parsed as a valid query filter");
                }
            } else {
                request.setAdditionalParameter(name, asSingleValue(name, values));
            }
        }

        // Check for incompatible arguments.
        if (request.getQueryId() != null && request.getQueryFilter() != null) {
            // FIXME: i18n.
            throw new BadRequestException("The parameters " + QueryRequest.FIELD_QUERY_ID + " and "
                    + QueryRequest.FIELD_QUERY_EXPRESSION + " are mutually exclusive");
        }

        if (request.getQueryId() != null && request.getQueryExpression() != null) {
            // FIXME: i18n.
            throw new BadRequestException("The parameters " + QueryRequest.FIELD_QUERY_ID + " and "
                    + QueryRequest.FIELD_QUERY_EXPRESSION + " are mutually exclusive");
        }

        if (request.getQueryFilter() != null && request.getQueryExpression() != null) {
            // FIXME: i18n.
            throw new BadRequestException("The parameters " + QueryRequest.FIELD_QUERY_FILTER + " and "
                    + QueryRequest.FIELD_QUERY_EXPRESSION + " are mutually exclusive");
        }

        return request;
    }

    public static ReadRequest buildReadRequest(Map<String, String[]> parameters, ResourceName resourceName)
            throws ResourceException {
        final ReadRequest request = Requests.newReadRequest(ResourceName.urlDecode(resourceName));
        for (final Map.Entry<String, String[]> p : parameters.entrySet()) {
            final String name = p.getKey();
            final String[] values = p.getValue();
            if (parseCommonParameter(name, values, request)) {
                continue;
            } else if (PARAM_MIME_TYPE.equalsIgnoreCase(name)) {
                if (values.length != 1 || values[0].split(FIELDS_DELIMITER).length > 1) {
                    // FIXME: i18n.
                    throw new BadRequestException("Only one mime type value allowed");
                }
                if (parameters.get(Request.FIELD_FIELDS).length != 1) {
                    // FIXME: i18n.
                    throw new BadRequestException("The mime type parameter requires only 1 field to be specified");
                }
            } else {
                request.setAdditionalParameter(name, asSingleValue(name, values));
            }
        }
        return request;
    }

    public static PatchRequest buildPatchRequest(Map<String, String[]> parameters, ResourceName resourceName,
            String revision, JsonValue patchContent) throws ResourceException {
        final PatchRequest request = Requests.newPatchRequest(ResourceName.urlDecode(resourceName))
                .setRevision(revision);
        request.getPatchOperations().addAll(PatchOperation.valueOfList(patchContent));
        for (final Map.Entry<String, String[]> p : parameters.entrySet()) {
            final String name = p.getKey();
            final String[] values = p.getValue();
            if (parseCommonParameter(name, values, request)) {
                continue;
            } else {
                request.setAdditionalParameter(name, asSingleValue(name, values));
            }
        }
        return request;
    }

    public static CreateRequest buildCreateRequest(Map<String, String[]> parameters, ResourceName resourceName,
            JsonValue content) throws ResourceException {
        return buildCreateRequest(parameters, resourceName, content, null);
    }

    public static CreateRequest buildCreateRequest(Map<String, String[]> parameters, ResourceName resourceName,
            JsonValue content, String newResourceId) throws ResourceException {
        final CreateRequest request = Requests.newCreateRequest(ResourceName.urlDecode(resourceName), content);
        if (newResourceId != null) {
            request.setNewResourceId(newResourceId);
        }
        for (final Map.Entry<String, String[]> p : parameters.entrySet()) {
            final String name = p.getKey();
            final String[] values = p.getValue();
            if (parseCommonParameter(name, values, request)) {
                continue;
            } else if (name.equalsIgnoreCase(ActionRequest.FIELD_ACTION)) {
                // Ignore - already handled.
            } else {
                request.setAdditionalParameter(name, asSingleValue(name, values));
            }
        }
        return request;
    }

    public static ActionRequest buildActionRequest(Map<String, String[]> parameters, ResourceName resourceName,
            String action, JsonValue content) throws ResourceException {
        final ActionRequest request = Requests.newActionRequest(ResourceName.urlDecode(resourceName), action)
                .setContent(content);
        for (final Map.Entry<String, String[]> p : parameters.entrySet()) {
            final String name = p.getKey();
            final String[] values = p.getValue();
            if (parseCommonParameter(name, values, request)) {
                continue;
            } else if (name.equalsIgnoreCase(ActionRequest.FIELD_ACTION)) {
                // Ignore - already handled.
            } else {
                request.setAdditionalParameter(name, asSingleValue(name, values));
            }
        }
        return request;
    }

    public static UpdateRequest buildUpdateRequest(Map<String, String[]> parameters, ResourceName resourceName,
            JsonValue content, String revision) throws ResourceException {
        final UpdateRequest request = Requests.newUpdateRequest(ResourceName.urlDecode(resourceName), content)
                .setRevision(revision);
        for (final Map.Entry<String, String[]> p : parameters.entrySet()) {
            final String name = p.getKey();
            final String[] values = p.getValue();
            if (parseCommonParameter(name, values, request)) {
                continue;
            } else {
                request.setAdditionalParameter(name, asSingleValue(name, values));
            }
        }
        return request;
    }

    /**
     * Parses a header or request parameter as a boolean value.
     *
     * @param name
     *            The name of the header or parameter.
     * @param values
     *            The header or parameter values.
     * @return The boolean value.
     * @throws ResourceException
     *             If the value could not be parsed as a boolean.
     */
    public static boolean asBooleanValue(final String name, final String[] values)
            throws ResourceException {
        final String value = asSingleValue(name, values);
        return Boolean.parseBoolean(value);
    }

    /**
     * Parses a header or request parameter as an integer value.
     *
     * @param name
     *            The name of the header or parameter.
     * @param values
     *            The header or parameter values.
     * @return The integer value.
     * @throws ResourceException
     *             If the value could not be parsed as a integer.
     */
    public static int asIntValue(final String name, final String[] values) throws ResourceException {
        final String value = asSingleValue(name, values);
        try {
            return Integer.parseInt(value);
        } catch (final NumberFormatException e) {
            // FIXME: i18n.
            throw new BadRequestException("The value \'" + value + "\' for parameter '" + name
                    + "' could not be parsed as a valid integer");
        }
    }

    /**
     * Parses a header or request parameter as a single string value.
     *
     * @param name
     *            The name of the header or parameter.
     * @param values
     *            The header or parameter values.
     * @return The single string value.
     * @throws ResourceException
     *             If the value could not be parsed as a single string.
     */
    public static String asSingleValue(final String name, final String[] values) throws ResourceException {
        if (values == null || values.length == 0) {
            // FIXME: i18n.
            throw new BadRequestException("No values provided for the request parameter \'" + name
                    + "\'");
        } else if (values.length > 1) {
            // FIXME: i18n.
            throw new BadRequestException(
                    "Multiple values provided for the single-valued request parameter \'" + name
                            + "\'");
        }
        return values[0];
    }

    private static boolean parseCommonParameter(final String name, final String[] values, final Request request)
            throws ResourceException {
        if (name.equalsIgnoreCase(Request.FIELD_FIELDS)) {
            for (final String s : values) {
                try {
                    request.addField(s.split(","));
                } catch (final IllegalArgumentException e) {
                    // FIXME: i18n.
                    throw new BadRequestException("The value '" + s + "' for parameter '" + name
                            + "' could not be parsed as a comma separated list of JSON pointers");
                }
            }
            return true;
        } else if (name.equalsIgnoreCase(PARAM_PRETTY_PRINT)) {
            // This will be handled by the completionHandlerFactory, so just validate.
            asBooleanValue(name, values);
            return true;
        } else {
            // Unrecognized - must be request specific.
            return false;
        }
    }
}
