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

package org.forgerock.resource.exception;

import org.forgerock.resource.exception.ResourceException;

/**
 * An exception that indicates that a failure is 
 * not directly known to the system, and hence requires 
 * out-of-band knowledge or enhancements to determine 
 * if a failure should be categorized as temporary or permanent.
 * 
 * @see RetryableException for failures that are temporary instead.
 * @see PermanentException for failures that are permanent instead.
 *
 * @author aegloff
 */
public class UncategorizedException extends ResourceException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with the specified detail message and cause.
     * @param message
     * @param cause
     */
    public UncategorizedException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

}
