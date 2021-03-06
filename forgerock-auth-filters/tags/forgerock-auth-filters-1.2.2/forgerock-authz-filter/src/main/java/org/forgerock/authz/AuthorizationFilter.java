/*
 * The contents of this file are subject to the terms of the Common Development and
 * Distribution License (the License). You may not use this file except in compliance with the
 * License.
 *
 * You can obtain a copy of the License at legal/CDDLv1.0.txt. See the License for the
 * specific language governing permission and limitations under the License.
 *
 * When distributing Covered Software, include this CDDL Header Notice in each file and include
 * the License file at legal/CDDLv1.0.txt. If applicable, add the following below the CDDL
 * Header, with the fields enclosed by brackets [] replaced by your own identifying
 * information: "Portions copyright [year] [name of copyright owner]".
 *
 * Copyright 2013 ForgeRock AS.
 */

package org.forgerock.authz;

import org.forgerock.auth.common.AuditLogger;
import org.forgerock.auth.common.LoggingConfigurator;
import org.forgerock.auth.common.DebugLogger;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Base interface for all Authorization Filter implementations, which will contain the logic required
 * to determine if the request is authorized to proceed or not.
 *
 * @author Phill Cunnington
 * @since 1.0.0
 */
public interface AuthorizationFilter {

    /**
     * Initialises the Authorization Filter by providing it with the Configurator that was used to set up the
     * AuthZFilter and the two Logging instances.
     *
     * @param configuration The Configurator used to set up the AuthZFilter.
     * @param auditLogger The AuditLogger instance.
     * @param debugLogger The DebugLogger instance.
     */
    void initialise(LoggingConfigurator configuration, AuditLogger auditLogger, DebugLogger debugLogger);

    /**
     * Determines whether the request is authorized to proceed or not.
     *
     * @param servletRequest The HttpServletRequest.
     * @param servletResponse The HttpServletResponse.
     * @return <code>true</code> if the request is authorized to proceed, otherwise <code>false</code>.
     */
    boolean authorize(HttpServletRequest servletRequest, HttpServletResponse servletResponse);
}
