/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright (c) 2013-2014 ForgeRock AS. All Rights Reserved
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

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.json.resource.AbstractContext;
import org.forgerock.json.resource.Context;
import org.forgerock.json.resource.ContextName;
import org.forgerock.json.resource.PersistenceConfig;
import org.forgerock.json.resource.ResourceException;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class ScriptContext extends AbstractContext {

    /** the friendly name for this context */
    private static final ContextName CONTEXT_NAME = ContextName.valueOf("script");

    public static final String ATTR_TYPE = "type";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_REVISION = "revision";

    public ScriptContext(Context parent, String name, String type, String revision) {
        super(parent);
        data.put(ATTR_NAME, name);
        data.put(ATTR_TYPE, type);
        data.put(ATTR_REVISION, revision);
    }

    public ScriptContext(JsonValue savedContext, PersistenceConfig config) throws ResourceException {
        super(savedContext, config);

    }

    /**
     * Get this Context's {@link ContextName}.
     *
     * @return this object's ContextName
     */
    public ContextName getContextName() {
        return CONTEXT_NAME;
    }
}
