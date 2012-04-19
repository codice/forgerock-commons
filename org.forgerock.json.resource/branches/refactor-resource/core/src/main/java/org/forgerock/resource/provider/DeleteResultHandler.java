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
 * Handles the result processing of delete on a Resource.
 * 
 * Supports either synchronous or asynchronous internal processing,  
 * @see ResultHandler for details.
 *
 * @author aegloff
 */
public interface DeleteResultHandler extends ResultHandler {

    /**
     * Handle the successful result of a delete
     * 
     * @param id the identifier to report to the caller as deleted.
     */
    void setResult(String id);

}
