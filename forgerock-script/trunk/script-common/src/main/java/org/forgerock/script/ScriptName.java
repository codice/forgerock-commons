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

import org.forgerock.script.source.SourceUnit;

/**
 * A ScriptName identifies a {@code Script} object.
 * <p/>
 * Simple name can not identify a file in a Directory.
 * 
 * @author Laszlo Hordos
 */
public final class ScriptName {
    private final String name;
    private final String type;

    public ScriptName(String name, String type) {
        if (null == name || null == type || name.trim().isEmpty() || type.trim().isEmpty()) {
            throw new IllegalArgumentException("Required name and type");
        }
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        ScriptName that = (ScriptName) o;

        if (!name.equals(that.name))
            return false;
        if (!type.equals(that.type))
            return false;

        return true;
    }

    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }

    public String toString() {
        return name + ":" + type;
    }

    public static ScriptName forName(String name) {
        return new ScriptName(SourceUnit.AUTO_DETECT, name);
    }
}
