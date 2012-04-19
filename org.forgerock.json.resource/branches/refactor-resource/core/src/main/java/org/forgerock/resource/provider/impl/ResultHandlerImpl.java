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

package org.forgerock.resource.provider.impl;

import java.util.LinkedHashMap;
import java.util.concurrent.CountDownLatch;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.resource.exception.InternalServerErrorException;
import org.forgerock.resource.exception.ResourceException;
import org.forgerock.resource.provider.ResultHandler;

/**
 * Handles the result processing of a method invocation on a Resource.
 * 
 * @see ResultHandler for details
 *
 * @author aegloff
 */
public class ResultHandlerImpl implements ResultHandler {

    String id;
    String rev;
    JsonValue value;
    ResourceException ex;
    
    CountDownLatch doneSignal = new CountDownLatch(1);

    /**
     * {@inheritDoc}
     */
    public void setFailure(ResourceException ex) {
        this.ex = ex;
        doneSignal.countDown();
    }
    
    /**
     * Work-around until our whole framework supports async processing
     * @return the result in the format expected by the framework
     * @throws ResourceException with the exception set on setFailure
     */
    public JsonValue waitForResult() throws ResourceException {
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            throw new InternalServerErrorException("Aborted waiting for a result", e);
        }
        
        return buildResponse();
    }
    
    /**
     * Default implementation to take the result values and build the expected 
     * response. May be overriden by the specific result handler.
     * 
     * @return the response
     * @throws ResourceException
     */
    JsonValue buildResponse() throws ResourceException {
        if (ex != null) {
            throw ex;
        }
        JsonValue response = new JsonValue(new LinkedHashMap<String, Object>());
        response.put("_id", id);
        if (rev != null) {
            response.put("_rev", rev);
        }
// TODO: review         
        if (value != null) {
            response.put("value", value);
        }
        return response;
    }
}
