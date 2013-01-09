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

import java.lang.ref.WeakReference;

import org.forgerock.json.resource.Connection;
import org.forgerock.json.resource.ConnectionFactory;
import org.forgerock.json.resource.Context;
import org.forgerock.json.resource.InternalServerErrorException;
import org.forgerock.json.resource.ResourceException;

/**
 * A NAME does ...
 *
 * @author Laszlo Hordos
 */
public class OperationParameter {

    private final Context context;

    private final ConnectionFactory connectionFactory;

    public OperationParameter(final Context context, final ConnectionFactory connectionFactory) {
        this.context = context;
        this.connectionFactory = connectionFactory;
    }

    /**
     * Copy constructor.
     *
     * @param parameter
     */
    public OperationParameter(OperationParameter parameter) {
        context = parameter.getContext();
        connectionFactory = parameter.getConnectionFactory();
    }

    public Context getContext() {
        return context;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public Connection getConnection() throws ResourceException {
        final ConnectionFactory fc = getConnectionFactory();
        if (null != fc) {
            return fc.getConnection();
        }
        // TODO: i18n
        throw new InternalServerErrorException("ConnectionFactory is not set");
    }

    /**
     * This method intended to be overwritten.
     * @return
     */
    public Function getCallbackFunction(){
        return null;
    }

    public WeakReference<OperationParameter> getSelfReference(){
        return new WeakReference<OperationParameter>(this);
    }
}
