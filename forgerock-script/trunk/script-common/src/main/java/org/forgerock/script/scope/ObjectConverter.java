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

import java.util.Collection;
import java.util.Map;

import org.forgerock.json.resource.ConnectionFactory;
import org.forgerock.json.resource.Context;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
//TODO: Find better name
public abstract class ObjectConverter {

    private final OperationParameter parameter;

    protected ObjectConverter(OperationParameter parameter) {
        this.parameter = parameter;
        init();
    }

    protected abstract void init();

    public Context getContext() {
        return parameter.getContext();
    }

    public ConnectionFactory getConnectionFactory() {
        return parameter.getConnectionFactory();
    }

    public OperationParameter getOperationParameter(){
        return parameter;
    }

    public abstract Object handle(Object value);

}
