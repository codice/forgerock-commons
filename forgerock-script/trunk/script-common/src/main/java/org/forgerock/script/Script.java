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

package org.forgerock.script;

import javax.script.Bindings;
import javax.script.ScriptException;

import org.forgerock.json.resource.Context;
import org.forgerock.script.engine.CompiledScript;

/**
 * Interface for all executable script.
 * 
 * @see javax.script.CompiledScript
 * 
 */
public interface Script extends Scope, CompiledScript {

    /**
     * Executes the program stored in the {@code Script} object using the
     * supplied Bindings of attributes as the ENGINE_SCOPE of the associated
     * ScriptEngine during script execution.
     * 
     * @param bindings
     *            The bindings of attributes used for the ENGINE_SCOPE.
     * @return The return value from the script execution.
     * @throws ScriptException
     *             if an exception occurred during execution of the script.
     */
    Object eval(Bindings bindings) throws ScriptException;

    /**
     * Executes the program stored in the {@code Script} object.
     * 
     * @return The return value from the script execution.
     * @throws ScriptException
     *             if an exception occurred during execution of the script.
     */
    Object eval() throws ScriptException;
}
