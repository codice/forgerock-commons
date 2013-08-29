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
 * information: "Portions Copyrighted [year] [name of copyright owner]".
 *
 * Copyright © 2011 ForgeRock AS. All rights reserved.
 */

package org.forgerock.restlet;

// Java SE
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// Java Servlet
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

// Restlet API
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.restlet.routing.Template;

// Restlet Servlet Extension
import org.restlet.ext.servlet.ServletAdapter;

/**
 * TODO: Description.
 *
 * @author Paul C. Bryan
 */
public class RestletRouterServlet extends HttpServlet {

    /** TODO: Description. */
    private final Application application;

    /** TODO: Description. */
    private final Router router = new Router();

    /** TODO: Description. */
    private ServletAdapter adapter;

    /**
     * TODO: Description.
     */
    public RestletRouterServlet() {
        application = new Application();
        application.getTunnelService().setQueryTunnel(false); // query string purism
        application.setInboundRoot(router);
    }

    /**
     * TODO: Description.
     */
    public Application getApplication() {
        return application;
    }

    /**
     * TODO: Description.
     */
    public Router getRouter() {
        return router;
    }

    /**
     * Attaches a target Restlet to the router based on a given URI prefix.
     *
     * @param path TODO.
     * @param restlet the restlet to route to if path matches.
     * @throws IllegalArgumentException if path does not begin with a '/' character.
     */
    public void attach(String path, Restlet restlet) {
        if (!path.startsWith("/")) {
            throw new IllegalArgumentException("Illegal path");
        }
        restlet.setContext(application.getContext());
        router.attach(path, restlet, Template.MODE_EQUALS);
        router.attach(path + (path.equals("/") ? "" : "/"), restlet, Template.MODE_STARTS_WITH);
    }
    
    /**
     * TODO: Description.
     *
     * @param restlet TODO.
     */
    public void detach(Restlet restlet) {
        router.detach(restlet); // all routes to restlet are removed
    }

    /**
     * Called by the servlet container to indicate that a servlet is being placed into service.
     * This implementation initializes a new servlet adapter to adapt servlet calls into
     * restlet calls.
     *
     * @throws ServletException if an exception occurs which interferes with normal operation.
     */
    @Override
    public void init() throws ServletException {
        super.init();
        adapter = new ServletAdapter(getServletContext(), application);
    }

    /**
     * Called by the servlet container to indicate that the servlet is being taken out of
     * service. This implementation dereferences the servlet adapter that was created during
     * initialization.
     */
    @Override
    public void destroy() {
        adapter = null;
        super.destroy();
    }

    /**
     * Called by the servlet container to allow the servlet to respond to a request.
     *
     * @param request the client's request.
     * @param response the servlet's response.
     * @throws ServletException if an exception occurs that interferes with normal operation.
     * @throws IOException if an input/output exception occurs.
     */
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setHeader("Cache-Control", "no-cache");
        adapter.service(request, response);
    }
}
