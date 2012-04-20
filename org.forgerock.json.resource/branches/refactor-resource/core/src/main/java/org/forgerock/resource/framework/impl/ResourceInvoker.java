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

package org.forgerock.resource.framework.impl;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.fluent.JsonValueException;
import org.forgerock.resource.exception.BadRequestException;
import org.forgerock.resource.exception.InternalServerErrorException;
import org.forgerock.resource.exception.NotFoundException;
import org.forgerock.resource.exception.NotSupportedException;
import org.forgerock.resource.exception.ResourceException;
import org.forgerock.resource.framework.JsonResourceProvider;
import org.forgerock.resource.provider.Context;
import org.forgerock.resource.provider.FieldFilter;
import org.forgerock.resource.provider.Resource;
import org.forgerock.resource.provider.impl.ContextImpl;
import org.forgerock.resource.provider.impl.CreateRequestImpl;
import org.forgerock.resource.provider.impl.CreateResultHandlerImpl;
import org.forgerock.resource.provider.impl.ReadRequestImpl;
import org.forgerock.resource.provider.impl.ReadResultHandlerImpl;
import org.forgerock.resource.provider.impl.UpdateRequestImpl;
import org.forgerock.resource.provider.impl.UpdateResultHandlerImpl;

/**
 * Dispatches requests to specific methods.
 *
 * @author aegloff
 */
public class ResourceInvoker implements JsonResourceProvider {
    
    /** Standard JSON resource request methods. */
    public enum Method {
        create, read, update, delete, patch, query, action
    }

    // TODO: registration mechanism. Also consider if being a member is the best.
    public Resource resource;
    
    /**
     * Handles a JSON resource request by dispatching to the method corresponding with the
     * method member of the request. If the request method is not one of the standard JSON
     * resource request methods, a {@code JsonResourceException} is thrown. 
     * <p>
     * This method catches any thrown {@code JsonValueException}, and rethrows it as a
     * {@link ResourceException#BAD_REQUEST}. This allows the use of JsonValue methods
     * to validate the content of the request.
     *
     * @param request the JSON resource request.
     * @return the JSON resource response.
     * @throws if there is an exception handling the request.
     */ 
    @Override
    public JsonValue handle(JsonValue request) throws ResourceException {
        try {
            try {
                Context context = new ContextImpl(request);

                // TODO: implement filter mechanism
                FieldFilter filter = null; 

// THIS METHOD IS WORK IN PROGRESS!
                
                switch (request.get("method").required().asEnum(Method.class)) {
                case create: 
                    CreateRequestImpl createIn = new CreateRequestImpl();
                    createIn.setRequest(request);
                    createIn.setFieldFilter(filter);
                    CreateResultHandlerImpl createOut = new CreateResultHandlerImpl();
                    resource.create(createIn, context, createOut);
                    // This is a short-term solution until we support async processing throughout
                    return createOut.waitForResult();
                case read:
                    ReadRequestImpl readIn = new ReadRequestImpl();
                    readIn.setRequest(request);
                    readIn.setFieldFilter(filter);
                    ReadResultHandlerImpl readOut = new ReadResultHandlerImpl();
                    resource.read(readIn, context, readOut);
                    // This is a short-term solution until we support async processing throughout
                    return readOut.waitForResult();
                case update:
                    UpdateRequestImpl updateIn = new UpdateRequestImpl();
                    updateIn.setRequest(request);
                    updateIn.setFieldFilter(filter);
                    UpdateResultHandlerImpl updateOut = new UpdateResultHandlerImpl();
                    resource.update(updateIn, context, updateOut);
                    // This is a short-term solution until we support async processing throughout
                    return updateOut.waitForResult();
                case delete:
                case patch:
                case query:
                case action:
                    throw new NotSupportedException("need to implement this");
                default:
                    throw new BadRequestException("Method unsupported: " + request.get("method"));
                }
            } catch (JsonValueException jve) {
                throw new BadRequestException("Invalid request", jve);
            }
        } catch (Exception e1) {
            try {
                onException(e1); // give handler opportunity to throw its own exception
                throw e1;
            } catch (ResourceException ex) {
                throw ex; // can propagate as is
            } catch (Exception ex) {
                // need to rethrow as resource exception
                throw new InternalServerErrorException("Unexpected exception encountered", ex);
            }
        }
    }

    /**
     * Provides the ability to handle an exception by taking additional steps such as
     * logging, and optionally to override by throwing its own {@link ResourceException}.
     * This implementation does nothing; it is intended to be overridden by a subclass.
     *
     * @param exception the exception that was thrown.
     * @throws ResourceException an optional exception to be thrown instead.
     */
    protected void onException(Exception exception) throws ResourceException {
        // default implementation does nothing
    }
}
