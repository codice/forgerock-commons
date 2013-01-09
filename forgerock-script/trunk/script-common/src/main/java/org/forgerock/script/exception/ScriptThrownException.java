/*
 * DO NOT REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 ForgeRock Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms
 * of the Common Development and Distribution License
 * (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at
 * http://forgerock.org/license/CDDLv1.0.html
 * See the License for the specific language governing
 * permission and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL
 * Header Notice in each file and include the License file
 * at http://forgerock.org/license/CDDLv1.0.html
 * If applicable, add the following below the CDDL Header,
 * with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 */

package org.forgerock.script.exception;

import javax.script.ScriptException;


/**
 * An exception that is thrown to indicate that an executed script encountered
 * an exception.
 * 
 * @author Paul C. Bryan
 */

public class ScriptThrownException extends ScriptException {

    /** Serializable class a version number. */
    private static final long serialVersionUID = -517416087837241796L;

    /** Value that was thrown by the script. */
    private Object value;

    /**
     * Constructs a new exception with the specified value and {@code null} as
     * its detail message.
     */

    /**
     * Constructs a new exception with the specified value and detail message.
     */
    public ScriptThrownException(Object value, String message) {
        super(message);
        this.value = value;
    }

    /**
     * Constructs a new exception with the specified value and cause.
     */
    public ScriptThrownException(Object value, Exception exception) {
        super(exception);
        this.value = value;
    }

    public ScriptThrownException(Object value, String message, String fileName, int lineNumber) {
        super(message, fileName, lineNumber);
        this.value = value;
    }

    public ScriptThrownException(Object value, String message, String fileName, int lineNumber,
            int columnNumber) {
        super(message, fileName, lineNumber, columnNumber);
        this.value = value;
    }

    /**
     * Returns the value that was thrown from the script.
     */
    public Object getValue() {
        return value;
    }
}
