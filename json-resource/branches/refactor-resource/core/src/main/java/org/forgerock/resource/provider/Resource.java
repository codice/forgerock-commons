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
 * Copyright © 2012 ForgeRock AS. All rights reserved.
 */

package org.forgerock.resource.provider;

import java.util.Map;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.fluent.JsonValueException;

import org.forgerock.resource.exception.BadRequestException;
import org.forgerock.resource.exception.ConflictException;
import org.forgerock.resource.exception.ForbiddenException;
import org.forgerock.resource.exception.NotFoundException;
import org.forgerock.resource.exception.NotSupportedException;
import org.forgerock.resource.exception.PreconditionFailedException;
import org.forgerock.resource.exception.PreconditionRequiredException;

/**
 * Represents the contract with a set of resources. 
 * 
 * The structure and depth of the (potentially nested) resource set it deals with is up to the implementation.
 * It can choose to just deal with one level, and hand off to another resource implementation, 
 * or it can choose to handle multiple levels. 
 * 
 * As an example, taking an id of level1/level2/leaf, 
 * and assuming that the resource implementation registers to handle level1 with the router;
 * the implementation could choose to hand off processing to other implementations for level2 (or leaf)
 * or it could handle all operations down to leaf.
 * 
 * Supports either synchronous or asynchronous internal processing, 
 * i.e. it does not have to block the request thread
 * until a response becomes available.
 * 
 * For synchronous internal processing, directly call setResult on the result handler in the method, e.g.
 * 
 * {@code out.setResult(id, rev);}
 * 
 * For asynchronous internal processing, either the reference to the result handler directly, 
 * or the callback id of the result handler can be used for a later call-back, 
 * potentially on a different thread, e.g.
 * 
 * {@code
 * String callbackId = out.getCallbackId();
 * <forward the request and return>
 * ...
 * CreateResultHandler.get(callbackId).setResult(id, rev);
 * }
 *
 * Asynchronous implementations must take care to eventually set a result 
 * or a failure on the result handler;
 * i.e. they must catch all exceptions and set a failure.
 *
 * @author aegloff
 */
public interface Resource {

    /**
     * Handles the creation of a resource.
     * <p>
     * On completion, the id and revision (if optimistic concurrency is supported) 
     * must be set on the result handler.
     * On failure, an exception must be set with setFailure on the result handler.
     *
     * @param request Access to the request details, such as identifier and value
     * @param context Access to the request context, such as associated principal
     * @param out the result handler to set success or failure details on
     * 
     * Create expects failure exceptions as follows
     * {@code ForbiddenException} if access to the resource is forbidden.
     * {@code NotSupportedException} if the requested functionality is not implemented/supported
     * {@code PreconditionFailedException} if a resource with the same ID already exists.
     * {@code BadRequestException} if the passed identifier or value is invalid
     * {@code NotFoundException} if the specified id could not be resolved, 
     * for example when an intermediate resource in the hierarchy does not exist.
     */
    void create(CreateRequest request, Context context, CreateResultHandler out);

    
    /**
     * Handles the read of a resource.
     * <p>
     * On completion, the id, revision (if optimistic concurrency is supported) 
     * and entity body must be set on the result handler.
     * On failure, an exception must be set with setFailure on the result handler.
     *
     * @param request Access to the request details, such as identifier and value
     * @param context Access to the request context, such as associated principal
     * @param out the result handler to set success or failure details on
     * 
     * Read expects failure exceptions as follows
     * {@code ForbiddenException} if access to the resource is forbidden.
     * {@code NotSupportedException} if the requested functionality is not implemented/supported
     * {@code BadRequestException} if the passed identifier or filter is invalid
     * {@code NotFoundException} if the specified resource could not be found
     */
    void read(ReadRequest request, Context context, ReadResultHandler out);

    
    /**
     * Handles the update of an existing resource.
     * <p>
     * On completion, the id, revision (if optimistic concurrency is supported) 
     * must be set on the result handler.
     * On failure, an exception must be set with setFailure on the result handler.
     *
     * @param request Access to the request details, such as identifier and value
     * @param context Access to the request context, such as associated principal
     * @param out the result handler to set success or failure details on
     * 
     * Update expects failure exceptions as follows
     * {@code ForbiddenException} if access to the resource is forbidden.
     * {@code NotSupportedException} if the requested functionality is not implemented/supported
     * {@code PreconditionRequiredException if version is required, but is {@code null}.
     * {@code PreconditionFailedException if version did not match the existing resource.
     * {@code BadRequestException} if the passed identifier or filter is invalid
     * {@code NotFoundException} if the specified resource could not be found
     */
    void update(UpdateRequest request, Context context, UpdateResultHandler out);

    /**
     * Handles the delete of a resource.
     * <p>
     * On completion, the id of the deleted resource must be set on the result handler.
     * On failure, an exception must be set with setFailure on the result handler.
     *
     * @param request Access to the request details, such as identifier
     * @param context Access to the request context, such as associated principal
     * @param out the result handler to set success or failure details on
     * 
     * Read expects failure exceptions as follows
     * {@code ForbiddenException} if access to the resource is forbidden.
     * {@code NotSupportedException} if the requested functionality is not implemented/supported
     * {@code BadRequestException} if the passed identifier is invalid
     * {@code NotFoundException} if the specified resource could not be found
     * {@code PreconditionRequiredException if version is required, but is {@code null}.
     * {@code PreconditionFailedException if version did not match the existing resource.
     */
    void delete(DeleteRequest request, Context context, DeleteResultHandler out);
    
    /**
     * Handles the patching of an existing resource.
     * <p>
     * On completion, the id, revision (if optimistic concurrency is supported) 
     * must be set on the result handler.
     * On failure, an exception must be set with setFailure on the result handler.
     *
     * @param request Access to the request details, such as identifier and value
     * @param context Access to the request context, such as associated principal
     * @param out the result handler to set success or failure details on
     * 
     * Patch expects failure exceptions as follows
     * {@code ForbiddenException} if access to the resource is forbidden.
     * {@code NotSupportedException} if the requested functionality is not implemented/supported
     * {@code PreconditionRequiredException if version is required, but is {@code null}.
     * {@code PreconditionFailedException if version did not match the existing resource.
     * {@code BadRequestException} if the passed identifier or filter is invalid
     * {@code NotFoundException} if the specified resource could not be found
     * {@code ConflictException} if patch could not be applied for the given resource state
     */
    void patch(PatchRequest request, Context context, PatchResultHandler out);

    /**
     * Handles the query of a resource.
     * <p>
     * On completion, the query result must be set on the result handler.
     * On failure, an exception must be set with setFailure on the result handler.
     *
     * @param request Access to the request details, such as identifier and value
     * @param context Access to the request context, such as associated principal
     * @param out the result handler to set success or failure details on
     * 
     * Query expects failure exceptions as follows
     * {@code ForbiddenException} if access to the resource is forbidden.
     * {@code NotSupportedException} if the requested functionality is not implemented/supported
     * {@code BadRequestException} if the passed identifier, parameters or filter is invalid
     * {@code NotFoundException} if the specified resource could not be found
     */
    void query(QueryRequest request, Context context, QueryResultHandler out);

    
    /**
     * Handles performing an action on a resource, and optionally returns an associated
     * result. The execution of an action is allowed to incur side effects.
     * <p>
     * Actions are parametric; a set of named parameters is provided as input to the action.
     * The action result is a JSON object structure composed of basic Java types; its overall
     * structure is defined by a specific implementation.
     * <p>
     * On completion, the action result (or null) must be set on the result handler.
     * On failure, an exception must be set with setFailure on the result handler.
     *
     * @param request Access to the request details, such as identifier and value
     * @param context Access to the request context, such as associated principal
     * @param out the result handler to set success or failure details on
     * 
     * Action expects failure exceptions as follows
     * {@code ForbiddenException} if access to the resource is forbidden.
     * {@code NotSupportedException} if the requested functionality is not implemented/supported
     * {@code BadRequestException} if the passed identifier, parameters or filter is invalid
     * {@code NotFoundException} if the specified resource could not be found
     */
    void action(ActionRequest request, Context context, ActionResultHandler out);

}
