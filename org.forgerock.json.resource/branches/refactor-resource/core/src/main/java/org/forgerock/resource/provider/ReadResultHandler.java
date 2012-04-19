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

import org.forgerock.json.fluent.JsonValue;

/**
 * Handles the result processing of read on a Resource.
 * 
 * Supports either synchronous or asynchronous internal processing,  
 * @see ResultHandler for details.
 *
 * @author aegloff
 */
public interface ReadResultHandler extends ResultHandler {

    /**
     * Handle the successful result of a read
     * 
     * @param id the identifier to report to the caller as read.
     * @param rev the current revision of the read object (if MVCC supported), 
     * or null if not supported
     */
    void setResult(String id, String rev, JsonValue value);

}
