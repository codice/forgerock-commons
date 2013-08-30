/**
 *
 ~ DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 ~
 ~ Copyright (c) 2011-2013 ForgeRock AS. All Rights Reserved
 ~
 ~ The contents of this file are subject to the terms
 ~ of the Common Development and Distribution License
 ~ (the License). You may not use this file except in
 ~ compliance with the License.
 ~
 ~ You can obtain a copy of the License at
 ~ http://forgerock.org/license/CDDLv1.0.html
 ~ See the License for the specific language governing
 ~ permission and limitations under the License.
 ~
 ~ When distributing Covered Code, include this CDDL
 ~ Header Notice in each file and include the License file
 ~ at http://forgerock.org/license/CDDLv1.0.html
 ~ If applicable, add the following below the CDDL Header,
 ~ with the fields enclosed by brackets [] replaced by
 ~ your own identifying information:
 ~ "Portions Copyrighted [year] [name of copyright owner]"
 *
 */
package org.forgerock.xacml.reference.rest;

import com.sun.identity.entitlement.xacml3.core.ObjectFactory;
import javax.ws.rs.*;
import javax.xml.bind.JAXBElement;


import com.sun.identity.entitlement.xacml3.core.*;
import com.sun.identity.shared.debug.Debug;

import org.forgerock.xacml.core.v3.interfaces.PolicyStore;
import org.forgerock.xacml.core.v3.ImplementationManagers.PolicyStoreManager;

import java.util.Set;


@Path("/pap")
public class ReferenceRequestHandler {

    /**
     * Define our Static resource Bundle for our debugger.
     */
    private static Debug DEBUG = Debug.getInstance("xacml3");


    /**
     * POST
     * Handle XML Requests
     *
     * @param pSet JAXBElement<PolicySet>
     * @return String
     */
    @POST
    @Consumes({"application/xml", "application/xacml+xml", "application/json", "application/xacml+json"})
    @Produces({"application/xml", "application/xacml+xml", "application/json", "application/xacml+json"})
    @Path("/import")
    public String loadPolicySet(JAXBElement<PolicySet> pSet ) {

        final String methodName = "ReferenceRequestHandler.loadPolicySet: ";
        String result =  "Successfully Loaded";

        try {
            PolicySet polSet = pSet.getValue();

            PolicyStore ps = PolicyStoreManager.getInstance();
            ps.loadPolicySet(polSet);

        } catch (Exception exception) {
                DEBUG.error(methodName + "Exception Occurred: " + exception.getMessage() + ", Returning Indeterminate.",
                        exception);
            result = "LOAD FAILED";
        }

        return result;
    }

    /**
     * POST
     * Handle XML Requests
     *
     * @return JAXBElement<Response>
     */
    @GET
    @Consumes({"application/xml", "application/xacml+xml", "application/json", "application/xacml+json"})
    @Produces({"application/xml", "application/xacml+xml", "application/json", "application/xacml+json"})
    @Path("/export/{policyname}")
    public JAXBElement<PolicySet> exportPolicySet(@PathParam("policyname") String policyname) {

        final String methodName = "ReferenceRequestHandler.exportPolicySet: ";
        PolicySet result =null;

        try {
            PolicyStore ps = PolicyStoreManager.getInstance();
            result = ps.exportPolicySet(policyname);

        } catch (Exception exception) {
            DEBUG.error(methodName + "Exception Occurred: " + exception.getMessage() + ", Returning Indeterminate.",
                    exception);
        }
        ObjectFactory objectFactory = new ObjectFactory();
        return objectFactory.createPolicySet(result);
    }

    /**
     * POST
     * Handle XML Requests
     *
     * @return JAXBElement<Response>
     */
    @GET
    @Consumes
    @Produces({"application/xml", "application/xacml+xml", "application/json", "application/xacml+json","*/*"})
    @Path("/list")
    public String listPolicySet() {

        final String methodName = "ReferenceRequestHandler.listPolicySet: ";
        Set<String> names = null;
        try {
            PolicyStore ps = PolicyStoreManager.getInstance();
            names = ps.listPolicySets();

        } catch (Exception exception) {
            DEBUG.error(methodName + "Exception Occurred: " + exception.getMessage() + ", Returning Indeterminate.",
                    exception);
        }
        return names.toString();
    }
}

