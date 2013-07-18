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
 * Copyright 2013 ForgeRock Inc.
 */

package org.forgerock.jaspi.authz;

import org.forgerock.jaspi.common.AuditLogger;
import org.forgerock.jaspi.common.Configuration;
import org.forgerock.jaspi.common.DebugLogger;
import org.forgerock.json.resource.JsonResourceException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthZFilter implements Filter {

    private Configuration configuration;
    private AuthorizationFilter authorizationFilter;

    @Override
    @SuppressWarnings("unchecked")
    public void init(FilterConfig filterConfig) throws ServletException {
        String configurationClassName = filterConfig.getInitParameter("configurationImpl");

        try {
            Class<? extends Configuration> configurationClass =
                    (Class<? extends Configuration>) Class.forName(configurationClassName);

            configuration = configurationClass.newInstance();

            AuditLogger auditLogger = configuration.getAuditLogger();
            DebugLogger debugLogger = configuration.getDebugLogger();

            authorizationFilter = configuration.getAuthorizationFilter();
            authorizationFilter.initialise(auditLogger, debugLogger);

        } catch (ClassNotFoundException e) {
            //TODO log
            throw new ServletException(e);
        } catch (InstantiationException e) {
            //TODO log
            throw new ServletException(e);
        } catch (IllegalAccessException e) {
            //TODO log
            throw new ServletException(e);
        }
    }

    @Override
    public final void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        if (authorizationFilter.authorize(servletRequest, servletResponse)) {
            filterChain.doFilter(servletRequest, servletResponse);
        } else {
            handleUnauthorisedException(servletResponse);
        }
    }

    private void handleUnauthorisedException(ServletResponse servletResponse) throws ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        JsonResourceException jre = new JsonResourceException(403, "Access denied");
        try {
            response.getWriter().write(jre.toJsonValue().toString());
            response.setContentType("application/json");
        } catch (IOException e) {
            handleServerException(servletResponse);
        }
    }

    private void handleServerException(ServletResponse servletResponse) throws ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        JsonResourceException jre = new JsonResourceException(500, "Server Error");
        try {
            response.getWriter().write(jre.toJsonValue().toString());
            response.setContentType("application/json");
        } catch (IOException e) {
            //TODO log
            throw new ServletException(e);
        }
    }

    @Override
    public void destroy() {
        configuration = null;
        authorizationFilter = null;
    }
}
