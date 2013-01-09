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

package org.forgerock.script.javascript;

// Java Standard Edition

import java.util.List;
import java.util.Map;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.script.scope.Function;
import org.forgerock.script.scope.ObjectConverter;
import org.forgerock.script.scope.OperationParameter;

// OpenIDM

/**
 * TODO: Description.
 * 
 * @author Paul C. Bryan
 */
public class ScriptableWrapper {

    /**
     * TODO: Description.
     * 
     * @param source
     *            TODO.
     * @return TODO.
     */
    public static final Object wrap(OperationParameter parameter, Object source) {
        Object value = source;
        if (value instanceof JsonValue) {
            value = ((JsonValue)value).getObject();
        }
        if (value == null) {
            return null;
        } else if (value instanceof Map) {
            return new ScriptableMap(parameter, (Map) value);
        } else if (value instanceof List) {
            return new ScriptableList(parameter, (List) value);
        } else if (value instanceof Function) {
            if (null != parameter) {
            return new ScriptableFunction(parameter, (Function) value);}
            else {
                return new ScriptableFunction((Function) value);}
        } else {
            return value;
        }
    }
}
