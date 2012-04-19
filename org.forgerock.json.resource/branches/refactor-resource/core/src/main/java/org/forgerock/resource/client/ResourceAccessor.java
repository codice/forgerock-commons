package org.forgerock.resource.client;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.resource.exception.ResourceException;

/**
 * Provides access to resources
 * 
 * @author aegloff
 *
 */
public interface ResourceAccessor {

    /**
     * Creates a resource.
     *
     * @param id the requested identifier for the newly created resource.
     * @param value the value of the resource to create.
     * @throws ResourceException if there is an exception handling the request.
     */
    JsonValue create(String id, JsonValue value) throws ResourceException;

    /**
     * Reads a resource.
     *
     * @param id the identifier of the resource to read.
     * @throws ResourceException if there is an exception handling the request.
     */
    JsonValue read(String id) throws ResourceException;

    /**
     * Updates a resource.
     *
     * @param id the identifier of the resource to update.
     * @param rev the current version of the resource, or {@code null} if not provided.
     * @param value the new value to update the resource to.
     * @throws ResourceException if there is an exception handling the request.
     */
    JsonValue update(String id, String rev, JsonValue value)
            throws ResourceException;

    /**
     * Deletes a resource.
     *
     * @param id the identifier of the resource to delete.
     * @param rev the current version of the resource, or {@code null} if not provided.
     * @throws ResourceException if there is an exception handling the request.
     */
    JsonValue delete(String id, String rev) throws ResourceException;

    /**
     * Applies a set of patches to a resource.
     *
     * @param id the identifier of the resource to patch.
     * @param rev the current version of the resource, or {@code null} if not provided.
     * @param value the patch document to apply to the resource.
     * @throws ResourceException if there is an exception handling the request.
     */
    JsonValue patch(String id, String rev, JsonValue value)
            throws ResourceException;

    /**
     * Performs a query on a resource.
     *
     * @param id the identifier of the resource to query.
     * @param params the parameters to supply to the query.
     * @throws ResourceException if there is an exception handling the request.
     */
    JsonValue query(String id, JsonValue params) throws ResourceException;

    /**
     * Performs an action on a resource.
     *
     * @param id the identifier of the resource to perform the action on.
     * @param params the parameters to supply to the action.
     * @param value the value to supply to the action; or {@code null} if no value.
     * @throws ResourceException if there is an exception handling the request.
     */
    JsonValue action(String id, JsonValue params, JsonValue value)
            throws ResourceException;

}