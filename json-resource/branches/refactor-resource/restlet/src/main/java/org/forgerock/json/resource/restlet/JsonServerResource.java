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
 * information: "Portions Copyrighted [year] [name of copyright owner]".
 *
 * Copyright © 2011 ForgeRock AS. All rights reserved.
 */

package org.forgerock.json.resource.restlet;

// Java SE
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// Restlet
import org.restlet.data.Conditions;
import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.data.Status;
import org.restlet.data.Tag;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
//import org.restlet.resource.ResourceException;

// Jackson
import org.restlet.ext.jackson.JacksonRepresentation;

// Restlet Utilities
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.forgerock.restlet.ExtendedServerResource;

// JSON Fluent
import org.forgerock.json.fluent.JsonValue;

// JSON Resource
//import org.forgerock.json.resource.JsonResourceProvider;
import org.forgerock.resource.client.ResourceAccessor;
import org.forgerock.resource.client.impl.ResourceAccessorImpl;
import org.forgerock.resource.exception.BadRequestException;
import org.forgerock.resource.exception.InternalServerErrorException;
import org.forgerock.resource.exception.PreconditionFailedException;
import org.forgerock.resource.exception.ResourceException;


/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class JsonServerResource extends ExtendedServerResource {

    /** TODO: Description. */
    private JsonResourceRestlet restlet;

    /** TODO: Description. */
    private ResourceAccessor accessor;

    /** TODO: Description. */
    private Conditions conditions;

    /** TODO: Description. */
    private String id;

    /** TODO: Description. */
    private String rev;

    /** Current value of the resource (if it has been already read). */
    private JsonValue value;

    /** Reference to resource being accessed. */
    private Reference ref;

    /**
     * TODO: Description.
     *
     * @param value TODO.
     * @return TODO.
     */
    private Tag getTag(JsonValue value) {
        Object _rev = (value != null ? value.get("_rev").getObject() : null);
        return (_rev != null && _rev instanceof String ? new Tag((String)_rev, false) : null);
    }

    /**
     * TODO: Description.
     *
     * @param value TODO.
     * @return TODO.
     */
    private Representation toRepresentation(JsonValue value) {
        JacksonRepresentation<Object> result = null;
        if (value != null) {
            result = new JacksonRepresentation<Object>(value.getObject()) {
              /**
                 * {@inheritDoc}
                 */
                protected ObjectMapper createObjectMapper() {
                    ObjectMapper mapper = super.createObjectMapper();
                    if (isPrettyPrint()) {
                        mapper.configure(SerializationConfig.Feature.INDENT_OUTPUT, true);
                    }
                    return mapper;
                }
            };
            result.setObjectClass(Object.class); // probably superfluous
            result.setTag(getTag(value)); // set ETag, if _rev is in value
        }
        return result;
    }

    /**
     * Returns {@code true} if the client requested that JSON results should be
     * indented in order to be more human readable.
     *
     * @return {@code true} if the client requested that JSON results should be
     *         indented in order to be more human readable.
     */
    private boolean isPrettyPrint() {
        Form query = getQuery();
        if (query != null) {
            String s = query.getFirstValue("prettyprint", true);
            String ls = s != null ? s.toLowerCase(Locale.ENGLISH) : null;
            return ls != null && ls.equals("true");
        } else {
            return false;
        }
    }

    /**
     * Returns the request's query parameters as a JSON value.
     */
    private JsonValue getQueryParams() {
        Map<String, Object> map = new HashMap<String, Object>();
        Form query = getQuery();
        if (query != null) {
            map.putAll(query.getValuesMap()); // copy for isolation and mutability
        }
        return new JsonValue(map);
    }

    /**
     * Reads the JSON resource value. Enforces any preconditions provided in the incoming
     * request. Caches the read value in {@code value} to prevent multiple reads when
     * precondition(s) must be tested against current version of the resource.
     *
     * @return the value of the JSON resource.
     * @throws ResourceException if the JSON resource could not be read or a precondition failed.
     */
    private JsonValue read() throws ResourceException {
        if (this.value == null) {
            JsonValue value = accessor.read(this.id);
            Status status = conditions.getStatus(getMethod(), true, getTag(value), null);
            if (status != null && status.isError()) {
                throw new PreconditionFailedException(
                        status.getCode() + " "
                        + status.getName() + " "
                        + status.getDescription(),
                        status.getThrowable());
            }
            this.value = value; // cache to prevent multiple reads
        }
        return this.value;
    }

    /**
     * TODO: Description.
     *
     * @param id TODO.
     * @param value TODO.
     * @return TODO.
     * @throws ResourceException TODO.
     */
    private Representation create(String id, JsonValue value) throws ResourceException {
        JsonValue response = accessor.create(id, value);
        if (response == null) { // expect a response
            throw new InternalServerErrorException("Expected a response, but response was null");
        }
        JsonValue _id = response.get("_id");
        if (_id.isString()) {
            setLocationRef(new Reference(this.ref, _id.asString()));
        }
        setStatus(Status.SUCCESS_CREATED);
        return toRepresentation(response);
    }

    /**
     * TODO: Description.
     *
     * @param id TODO.
     * @param rev TODO.
     * @param value TODO.
     * @return TODO.
     * @throws ResourceException TODO.
     */
    private Representation update(String id, String rev, JsonValue value) throws ResourceException {
        JsonValue response = accessor.update(id, rev, value);
        if (response == null) { // expect a response
            throw new InternalServerErrorException("Expected a response, but response was null");
        }
        JsonValue _id = response.get("_id");
        if (_id.isString() && !id.equals(_id.getObject())) { // resource was moved
            setLocationRef(new Reference(this.ref, _id.asString()));
            setStatus(Status.SUCCESS_CREATED); // respond like WebDAV MOVE method would
        }
        return toRepresentation(response);
    }

    /**
     * Returns the entity as a JSON value, or {@code null} if there is no entity or if it
     * cannot be represented as a JSON value.
     *
     * @param entity the entity to be mapped to the JSON value.
     * @return the entity as a JSON value, or {@code null} if empty or does not exist.
     */
    private JsonValue entityValue(Representation entity) throws org.restlet.resource.ResourceException {
        JsonValue result = null;
        if (entity != null && !(entity instanceof EmptyRepresentation)) {
            JacksonRepresentation jr = (entity instanceof JacksonRepresentation ?
             (JacksonRepresentation)entity : new JacksonRepresentation<Object>(entity, Object.class));
            result = new JsonValue(jr.getObject());
        }
        return result;
    }

    /**
     * Throws a {@code ResourceException} if the specified entity value is {@code null}.
     * Otherwise, returns the value.
     */
    private JsonValue requireEntity(JsonValue entity) throws ResourceException {
        if (entity == null || entity.isNull()) {
            throw new BadRequestException("Malformed or missing entity body");
        }
        return entity;
    }

    /**
     * Initializes the state of the resource.
     */
    @Override
    public void doInit() {
        setAnnotated(false); // using method names, not annotations
        setNegotiated(false); // we shall speak all-JSON for now
        setConditional(false); // conditional requests handled in implementation
        this.conditions = getConditions();
        String remaining = getReference().getRemainingPart(false, false);
        if (remaining != null && remaining.length() > 0) {
            this.id = remaining; // default: null (resource itself is being operated on)
        }
        this.restlet = (JsonResourceRestlet)(getRequestAttributes().get(JsonResourceRestlet.class.getName()));
        this.accessor = new ResourceAccessorImpl(restlet.getResource(), restlet.newContext(getRequest()));
        this.ref = getOriginalRef();
    }

    /**
     * Handles a call without content negotiation of the response entity. Performs
     * operations common across all methods, prior to the call to the method-specific
     * handler.
     */
    @Override
    protected Representation doHandle() throws org.restlet.resource.ResourceException {
        List<Tag> match = conditions.getMatch();
        List<Tag> noneMatch = conditions.getNoneMatch();
        try {
            if (conditions.getModifiedSince() != null || conditions.getUnmodifiedSince() != null) {
                throw new PreconditionFailedException("Modified or unmodified since header conditions not supported");
            } else if (match.contains(null)) {
                throw new BadRequestException("Invalid If-Match tag");
            } else if (noneMatch.contains(null)) {
                throw new BadRequestException("Invalid If-None-Match tag");
            } else if (match.size() == 1 && noneMatch.size() == 0 && !Tag.ALL.equals(match.get(0))) {
                rev = match.get(0).getName(); // derive from request
            } else if (getMethod().equals(Method.PUT) && noneMatch.size() == 1
             && match.size() == 0 && Tag.ALL.equals(noneMatch.get(0))) {
                // unambiguous create; set no revision
            } else if (match.size() != 0 || noneMatch.size() != 0) {
                rev = getTag(read()).getName(); // derive from fetched resource
            }
        } catch (ResourceException jre) {
            throw new org.restlet.resource.ResourceException(jre);
        }
        return super.doHandle();
    }

    /**
     * Processes a GET request. The GET method can be used to read a resource, or perform a
     * query against the resource within.
     * <p>
     * If no query is included in the request, then the request is dispatched to the resource
     * with a {@code "read"} method. Otherwise, the request is dispatched to the resource
     * with a {@code "query"} method.
     *
     * @return TODO.
     * @throws org.restlet.resource.ResourceException TODO.
     */
    @Override
    public Representation get() throws org.restlet.resource.ResourceException {
        Representation representation;
        try {
            Form query = getQuery();
            if (query == null || query.size() == 0
                    || (query.size() == 1 && query.getFirstValue("prettyprint") != null)) { // read
                representation = toRepresentation(read());
            } else if (conditions.hasSome()) { // query w. precondition: automatic mismatch
                    throw new PreconditionFailedException("Query does not support preconditions, but precondition present.");
            } else { // query
                representation = toRepresentation(accessor.query(this.id, getQueryParams()));
            }
            if (representation == null || representation instanceof EmptyRepresentation) {
                throw new InternalServerErrorException("Expected a response, but response was null or empty");
            }
        } catch (ResourceException jre) {
            throw new org.restlet.resource.ResourceException(jre);
        }
        return representation;
    }

    /**
     * Processes a PUT request. The PUT method can be used to create an object or update an
     * existing object.
     * <p>
     * The request is unambigously interpreted as a create if the {@code If-None-Match}
     * header has the single value that is: {@code *}. The request is unambiguously interpeted
     * as an update if the {@code If-Match} header has a single value that is not: {@code *}.
     * If the request is ambiguous, then this implementation first attempts to update the
     * object, and the update fails with a {@code NotFoundException}, then attempts to create
     * the object.
     *
     * @param entity TODO.
     * @return TODO.
     * @throws org.restlet.resource.ResourceException TODO.
     */
    @Override
    public Representation put(Representation entity) throws org.restlet.resource.ResourceException {
        Representation representation;
        List<Tag> match = conditions.getMatch();
        List<Tag> noneMatch = conditions.getNoneMatch();
        try {
            JsonValue value = requireEntity(entityValue(entity));
            if (match.size() == 0 && noneMatch.size() == 1 && noneMatch.get(0).equals(Tag.ALL)) { // unambiguous create
                representation = create(this.id, value);
            } else if (noneMatch.size() == 0 && match.size() == 1 && !match.get(0).equals(Tag.ALL)) { // unambiguous update
                representation = update(this.id, this.rev, value);
            } else { // ambiguous whether object is being created or updated
                try { // try update first
                    representation = update(this.id, this.rev, value);
                } catch (ResourceException jre) {
                    if (jre.getCode() == ResourceException.NOT_FOUND) { // nothing to update; fallback to create
                        representation = create(this.id, value);
                    } else {
                        throw jre;
                    }
                }
            }
        } catch (ResourceException jre) {
            throw new org.restlet.resource.ResourceException(jre);
        }
        return representation;
    }

    /**
     * Processes a POST request. The POST method is used to perform a number of different
     * functions, including: object creation, HTTP method override and initiation of object set
     * actions.
     * <p>
     * By default, the Restlet tunnel service enables support for the
     * {@code X-HTTP-Method-Override} header. If this header is set in an incoming request,
     * a different method will be automatically invoked.
     * <p>
     * If a query parameter named "{@code _action}" is included, and contains the value
     * "{@code create}", then the request is dispatched to the resource with a "{@code create}"
     * method. Otherwise, the request is dispatched to the resource with an "{@code action}"
     * method.
     *
     * @param entity TODO.
     * @return TODO.
     * @throws org.restlet.resource.ResourceException TODO.
     */
    @Override
    public Representation post(Representation entity) throws org.restlet.resource.ResourceException {
        Representation representation;
        Form query = getQuery();
        String _action = query.getFirstValue("_action");
        try {
            if ("create".equals(_action)) {
                JsonValue value = entityValue(entity);
                if (this.id != null && this.id.charAt(this.id.length() - 1) != '/') {
                    this.ref.setPath(this.ref.getPath() + '/');
                    this.id = this.id + '/'; // create new resource within collection
                }
                representation = create(this.id, requireEntity(entityValue(entity)));
            } else { // action
                representation = toRepresentation(accessor.action(this.id, getQueryParams(), entityValue(entity)));
                if (representation == null || representation instanceof EmptyRepresentation) {
                    setStatus(Status.SUCCESS_NO_CONTENT);
                }
            }
        } catch (ResourceException jre) {
            throw new org.restlet.resource.ResourceException(jre);
        }
        return representation;
    }

    /**
     * Processes a DELETE request. The request is dispatched to the JSON resource with a
     * {@code "delete"} method.
     */
    @Override
    public Representation delete() throws org.restlet.resource.ResourceException {
        try {
            accessor.delete(this.id, this.rev);
        } catch (ResourceException jre) {
            throw new org.restlet.resource.ResourceException(jre);
        }
        setStatus(Status.SUCCESS_NO_CONTENT);
        return null; // no content
    }

    /**
     * Processes a PATCH request. The request is dispatched to the JSON resource with a
     * {@code "patch"} method.
     *
     * @return TODO.
     * @throws org.restlet.resource.ResourceException TODO.
     */
    @Override
    public Representation patch(Representation entity) throws org.restlet.resource.ResourceException {
        Representation representation = null;
        try {
            representation = toRepresentation(accessor.patch(this.id, this.rev, requireEntity(entityValue(entity))));
            if (representation == null) { // expect a response to the patch
                throw new InternalServerErrorException("Expected a response, but response was null");
            }
        } catch (ResourceException jre) {
            throw new org.restlet.resource.ResourceException(jre);
        }
        return representation;
    }

    /**
     * Overrides the response to provide a JSON error structure in the entity if a
     * {@code org.restlet.resource.ResourceException} is being thrown.
     */
    @Override
    protected void doCatch(Throwable throwable) {
        ResourceException jre = null;
        if (throwable instanceof org.restlet.resource.ResourceException) {
            Throwable cause = throwable.getCause();
            if (cause != null && cause instanceof ResourceException) {
                jre = (ResourceException)cause;
            }
        }
        if (jre == null) {
            jre = new InternalServerErrorException("Unexpected failure", throwable);
        }
        int code = jre.getCode();
        if (code < 400 || code > 599) { // not an HTTP error status code
            code = 500; // force internal server error
        }
        String reason = jre.getReason();
        if (reason == null) {
            reason = "Internal Server Error";
        }
        setStatus(new Status(code, throwable, reason, jre.getMessage(), null));
        getResponse().setEntity(toRepresentation(jre.toJsonValue()));
    }
}
