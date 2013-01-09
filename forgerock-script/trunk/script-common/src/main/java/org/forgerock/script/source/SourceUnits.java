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

package org.forgerock.script.source;

import java.util.UUID;

import org.forgerock.json.fluent.JsonValue;
import org.forgerock.script.ScriptName;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public final class SourceUnits {

    public static SourceUnit valueOf(final JsonValue value) {
        if (null == value) {
            throw new NullPointerException("Null scriptValue");
        }
        JsonValue name = value.get("name");
        JsonValue type = value.get("type");
        ScriptName scriptName =
                new ScriptName(name.isNull() || !name.isString() ? UUID.randomUUID().toString()
                        : name.asString(),
                        type.isNull() || !type.isString() ? SourceUnit.AUTO_DETECT : name
                                .asString());

        // TODO custom parameters

        // TODO source
        JsonValue source = value.get("source");

        // TODO Visibility

        return new EmbeddedScriptSource("", scriptName);
    }
}
