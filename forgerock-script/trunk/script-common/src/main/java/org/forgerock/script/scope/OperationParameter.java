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
import org.forgerock.json.resource.Connection;
import org.forgerock.json.resource.Context;
import org.forgerock.json.resource.InternalServerErrorException;
import org.forgerock.json.resource.PersistenceConfig;
import org.forgerock.json.resource.ResourceException;
import org.forgerock.json.resource.ServerContext;
import org.forgerock.json.resource.ServiceUnavailableException;

/**
 * A NAME does ...
 * 
 * @author Laszlo Hordos
 */
public class OperationParameter implements Parameter {

    protected final Context context;
    protected final String connectionId;
    protected final PersistenceConfig persistenceConfig;

    public OperationParameter(final Context context, final String connectionId,
            final PersistenceConfig persistenceConfig) {
        this.context = context;
        this.connectionId = connectionId;
        this.persistenceConfig = persistenceConfig;
    }

    /**
     * Returns the internal connection.
     * 
     * @return The internal connection.
     * @throws org.forgerock.json.resource.NotFoundException
     *             If no such connection exists.
     * @throws ResourceException
     *             If the connection could not be obtained for some other reason
     *             (e.g. due to a configuration or initialization problem).
     */
    public ServerContext getServerContext(JsonValue savedContext) throws ResourceException {
        if (null != savedContext) {
            return ServerContext.loadFromJson(savedContext, getPersistenceConfig());
        } else if (context instanceof ServerContext) {
            return (ServerContext) context;
        } else if (null != context) {
            return new ServerContext(context, getConnection());
        }
        throw new InternalServerErrorException("Failed to get ServerContext.");
    }

    /**
     * Returns the internal connection.
     * 
     * @return The internal connection.
     * @throws org.forgerock.json.resource.NotFoundException
     *             If no such connection exists.
     * @throws ResourceException
     *             If the connection could not be obtained for some other reason
     *             (e.g. due to a configuration or initialization problem).
     */
    public Connection getConnection() throws ResourceException {
        Connection connection =
                getPersistenceConfig().getConnectionProvider().getConnection(getConnectionId());
        if (null != connection) {
            return connection;
        }
        throw new ServiceUnavailableException("Failed to get Connection for id: "
                + getConnectionId());
    }

    public String getConnectionId() {
        return connectionId;
    }

    public PersistenceConfig getPersistenceConfig() {
        return persistenceConfig;
    }

}
