/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013 ForgeRock AS. All Rights Reserved
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

import org.forgerock.script.engine.CompilationHandler;
import org.forgerock.script.engine.CompiledScript;
import org.forgerock.script.source.ScriptSource;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class DelegatedCompilationHandler implements CompilationHandler {

    private final CompilationHandler delegate;

    public DelegatedCompilationHandler(CompilationHandler delegate) {
        this.delegate = delegate;
    }

    public ScriptSource getScriptSource() {
        return delegate.getScriptSource();
    }

    public ClassLoader getParentClassLoader() {
        return delegate.getParentClassLoader();
    }

    public void setClassLoader(ClassLoader classLoader) {
        delegate.setClassLoader(classLoader);
    }

    public void setCompiledScript(CompiledScript compiledScript) {
        delegate.setCompiledScript(compiledScript);
    }

    public void handleException(Throwable throwable) {
        delegate.handleException(throwable);
    }
}
