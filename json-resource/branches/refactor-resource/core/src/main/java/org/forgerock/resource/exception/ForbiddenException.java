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
 * An exception that is thrown when access to a resource is forbidden 
 * during an operation on an resource.
 *
 * @author Paul C. Bryan
 * @author aegloff
 */
public class ForbiddenException extends ResourceException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public ForbiddenException() {
        super(ResourceException.FORBIDDEN);
    }
    
    /**
     * Constructs a new exception with the specified detail message.
     */
    public ForbiddenException(String message) {
        super(ResourceException.FORBIDDEN, message);
    }
    
    /**
     * Constructs a new exception with the specified cause.
     */
    public ForbiddenException(Throwable cause) {
        super(ResourceException.FORBIDDEN, cause);
    }

    /**
     * Constructs a new exception with the specified detail message and cause.
     */
    public ForbiddenException(String message, Throwable cause) {
        super(ResourceException.FORBIDDEN, message, cause);
    }
}
