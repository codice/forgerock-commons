/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2012 ForgeRock AS. All Rights Reserved
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

package org.forgerock.script.scope;

/**
 * A LazyAccessor does ...
 * 
 * @author Laszlo Hordos
 */
public interface LazyAccessor<T> extends Element {

    /**
     * Accesses the lazy loaded object.
     * <p/>
     * This method is called when the lazy object needs to be loaded.
     * 
     * @return The value encapsulated or null.
     */
    public T access();

    /**
     * Returns <tt>true</tt> if this accessor loaded the object and it's null.
     * 
     * @return <tt>true</tt> if this accessor loaded the object and it's null
     */
    public boolean isNull();

    /**
     * Returns <tt>true</tt> if {@link #access()} method was called.
     * 
     * @return <tt>true</tt> if {@link #access()} method was called
     */
    public boolean isAccessed();

}
