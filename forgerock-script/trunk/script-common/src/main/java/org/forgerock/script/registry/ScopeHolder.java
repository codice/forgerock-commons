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

import java.util.concurrent.ConcurrentHashMap;
import javax.script.Bindings;
import javax.script.SimpleBindings;

import org.forgerock.script.Scope;

/**
 * A ScopeHolder keeps the set of Bindings in a ScriptContext for given
 * {@code scope} level.
 * 
 * @author Laszlo Hordos
 */
abstract class ScopeHolder implements Scope {

    private Bindings bindings = null;

    public void put(String key, Object value) {
        if (null == bindings) {
            bindings = createBindings();
        }
        bindings.put(key, value);
    }

    public Object get(String key) {
        if (getBindings() != null) {
            return getBindings().get(key);
        }
        return null;
    }

    public Bindings getBindings() {
        return bindings;
    }

    public void setBindings(Bindings bindings) {
        this.bindings = bindings;
    }

    public Bindings createBindings() {
        return new SimpleBindings(new ConcurrentHashMap<String, Object>());
    }

    public void flush() {
        setBindings(null);
    }
}
