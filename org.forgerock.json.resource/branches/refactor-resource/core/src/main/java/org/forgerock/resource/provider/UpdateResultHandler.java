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

/**
 * Handles the result processing of update on a Resource.
 * 
 * Supports either synchronous or asynchronous internal processing, i.e. it does not have to block the request thread
 * until a response becomes available.
 * 
 * For synchronous internal processing, directly call setResult on the result handler in the create method, e.g.
 * 
 * {@code out.setResult(id, rev);}
 * 
 * For asynchronous internal processing, the callback id of the result handler can be used for a later call-back, 
 * potentially on a different thread, e.g.
 * 
 * String callbackId = out.getCallbackId();
 * <forward the request and return>
 * ...
 * CreateResultHandler.get(callbackId).setResult(id, rev);
 *
 * @author aegloff
 */
public interface UpdateResultHandler extends ResultHandler {

    /**
     * Handle the successful result of an update
     * 
     * @param id the identifier to report to the caller as updated.
     * @param rev the current revision of the updated object (if MVCC supported), 
     * or null if not supported
     */
    void setResult(String id, String rev);

}
