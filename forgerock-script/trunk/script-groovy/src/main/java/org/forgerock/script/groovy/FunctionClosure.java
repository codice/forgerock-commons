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

package org.forgerock.script.groovy;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.script.scope.Function;
import org.forgerock.script.scope.OperationParameter;

import groovy.lang.Closure;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class FunctionClosure extends Closure<Object> {

    private static final long serialVersionUID = -8234912264889627793L;

    /** TODO: Description. */
    private final Function function;
    /** TODO: Description. */
    private final OperationParameter parameter;

    public FunctionClosure(Object owner, Function method) {
        super(owner);
        this.function = method;
        this.parameter = null;
    }

    public FunctionClosure(Object owner, final OperationParameter parameter, Function function) {
        super(owner);
        this.function = function;
        this.parameter = parameter;
    }

    @SuppressWarnings("unchecked")
    public Object doCall(Object... args) {
        try {
            // TODO unwrap the callback
            // TODO wrap the result if necessary
            Object result = function.call(parameter, args);
            if (result instanceof JsonValue) {
                return ((JsonValue) result).getObject();
            }
            return  result;
        } catch (Throwable e) {
            return throwRuntimeException(e);
        }
    }
}
