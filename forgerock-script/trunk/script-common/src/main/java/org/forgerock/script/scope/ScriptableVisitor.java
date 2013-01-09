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

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.RequestVisitor;

/**
 * A visitor of {@code Request}s, in the style of the visitor design pattern.
 * <p>
 * Classes implementing this interface can perform actions based on the type of
 * a request in a type-safe manner. When a visitor is passed to a request's
 * accept method, the corresponding visit method associated with the type of the
 * request is invoked.
 * 
 * @param <R>
 *            The return type of this visitor's methods. Use
 *            {@link java.lang.Void} for visitors that do not need to return
 *            results.
 * @param <P>
 *            The type of the additional parameter to this visitor's methods.
 *            Use {@link java.lang.Void} for visitors that do not need an
 *            additional parameter.
 * 
 * @author Laszlo Hordos
 */
public interface ScriptableVisitor<R, P> extends RequestVisitor<R, P> {

    /**
     * Visits a function instance.
     * 
     * @param p
     *            A visitor specified parameter.
     * @param function
     *            The function instance.
     * @return Returns a visitor specified result.
     */
    public R visitFunction(P p, Function function);

//    /**
//     * Visits a function instance.
//     *
//     * @param p
//     *            A visitor specified parameter.
//     * @param function
//     *            The function instance.
//     * @return Returns a visitor specified result.
//     */
//    public R visitConnectionFunction(P p, ConnectionFunction function);

    /**
     * Visits a function instance.
     * 
     * @param p
     *            A visitor specified parameter.
     * @param value
     *            The JsonValue instance.
     * @return Returns a visitor specified result.
     */
    public R visitJsonValue(P p, JsonValue value);

    /**
     * Visits a function instance.
     * 
     * @param p
     *            A visitor specified parameter.
     * @param value
     *            Any Object instance.
     * @return Returns a visitor specified result.
     */
    public R visitObject(P p, Object value);

}
