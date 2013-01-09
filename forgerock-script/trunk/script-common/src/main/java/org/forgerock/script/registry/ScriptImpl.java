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

package org.forgerock.script.registry;

import javax.script.Bindings;
import javax.script.ScriptException;

import org.forgerock.json.resource.Context;
import org.forgerock.script.Script;
import org.forgerock.script.engine.CompiledScript;
import org.forgerock.script.engine.ScriptEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Each thread MUST use a new instance of this.
 * 
 * @author Laszlo Hordos
 * @see org.forgerock.script.ScriptEntry#getScript(org.forgerock.json.resource.Context)
 */
abstract class ScriptImpl extends ScopeHolder implements Script {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptImpl.class);

    private final CompiledScript target;
    private final Context context;
    private Bindings compiledBindings = null;

    ScriptImpl(final Context context, final CompiledScript target) {
        this.target = target;
        this.context = context;
    }

    public Object eval(Context context, Bindings bindings) throws ScriptException {
        try {
            if (null == compiledBindings)       {
            compiledBindings =  getScriptEngine().compileBindings(context, bindings,
                    getServiceBindings(), getGlobalBindings()); }
            return target.eval(context, compiledBindings);
        } catch (ScriptException e) {
            throw e;
        } catch (Throwable t) {
            LOGGER.error("Script invocation error", t);
            throw new ScriptException(t.getMessage());
        }
    }

    public Object eval(Bindings bindings) throws ScriptException {
        return eval(context, bindings);
    }

    public Object eval() throws ScriptException {
        return eval(getBindings());
    }

    protected abstract ScriptEngine getScriptEngine() throws ScriptException;

    protected abstract Bindings getGlobalBindings();

    protected abstract Bindings getServiceBindings();



    public void flush() {
        compiledBindings = null;
        super.flush();
    }
}
