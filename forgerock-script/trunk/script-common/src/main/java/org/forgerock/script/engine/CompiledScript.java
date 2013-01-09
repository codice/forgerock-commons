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

package org.forgerock.script.engine;

import javax.script.Bindings;
import javax.script.ScriptException;

import org.forgerock.json.resource.Context;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public interface CompiledScript {

    /**
     * Executes the program stored in this <code>CompiledScript</code> object.
     * 
     * @param bindings
     *            A <code>ScriptContext</code> that is used in the same way as
     *            the <code>ScriptContext</code> passed to the <code>eval</code>
     *            methods of <code>ScriptEngine</code>.
     * 
     * @return The value returned by the script execution, if any. Should return
     *         <code>null</code> if no value is returned by the script
     *         execution.
     * 
     * @throws javax.script.ScriptException
     *             if an error occurs.
     * @throws NullPointerException
     *             if bindings is null.
     */

    public Object eval(Context context, Bindings bindings) throws ScriptException;

}
