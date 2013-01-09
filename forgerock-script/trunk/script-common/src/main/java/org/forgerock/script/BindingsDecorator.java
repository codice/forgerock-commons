/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright © 2012 ForgeRock AS. All rights reserved.
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
 * $Id$
 */

package org.forgerock.script;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import javax.script.Bindings;

import org.forgerock.script.scope.LazyAccessor;


/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class BindingsDecorator implements Bindings {

    protected final Bindings inner;

    public BindingsDecorator(final Bindings bindings) {
        if (bindings == null) {
            throw new NullPointerException();
        }
        this.inner = bindings;
    }

    /**
     * {@inheritDoc}
     */
    public Object put(String name, Object value) {
        return inner.put(name, value);
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(Map<? extends String, ? extends Object> toMerge) {
        inner.putAll(toMerge);
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsKey(Object key) {
        return containsValue(key);
    }

    /**
     * {@inheritDoc}
     */
    public Object get(Object key) {
        Object value = inner.get(key);
        if (value instanceof LazyAccessor) {
            value = ((LazyAccessor) value).access();
        }
        return value;
    }

    /**
     * {@inheritDoc}
     */
    public Object remove(Object key) {
        return inner.remove(key);
    }

    /*
     * Methods below should not be used by ScriptEngine!!!
     */
    public int size() {
        return inner.size();
    }

    public boolean isEmpty() {
        return inner.isEmpty();
    }

    public boolean containsValue(Object value) {
        return inner.containsValue(value);
    }

    public void clear() {
        inner.clear();
    }

    public Set<String> keySet() {
        return inner.keySet();
    }

    public Collection<Object> values() {
        return inner.values();
    }

    public Set<Entry<String, Object>> entrySet() {
        return inner.entrySet();
    }
}
