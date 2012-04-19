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

import org.forgerock.resource.exception.ResourceException;

/**
 * Handles the result processing of operations on a Resource.
 * 
 * Result handler implementations support either synchronous or asynchronous 
 * internal processing, i.e. it does not have to block the request thread
 * until a response becomes available.
 * 
 * For synchronous internal processing, directly call setResult 
 * (exact signature for a given operation is specified by sub-types of this interface)
 * on the result handler in the method implementation, e.g.
 * 
 * {@code out.setResult(id, rev);}
 * 
 * For asynchronous internal processing, keep track of the result handler reference
 * which can be used for a later call-back, potentially on a different thread.
 * 
 * @author aegloff
 */
public interface ResultHandler {

    
    /**
     * Handle the failure result of an operation
     * 
     * @param ex the failure details
     */
    void setFailure(ResourceException ex);

}
