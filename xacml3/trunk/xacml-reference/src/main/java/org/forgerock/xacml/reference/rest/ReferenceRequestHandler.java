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
import com.sun.identity.entitlement.xacml3.core.Policy;
import com.sun.identity.entitlement.xacml3.core.PolicySet;
import com.sun.identity.shared.debug.Debug;
import org.forgerock.xacml.core.v3.ImplementationManagers.PolicyStoreManager;
import org.forgerock.xacml.core.v3.engine.XACML3PolicyItem;
import org.forgerock.xacml.core.v3.interfaces.PolicyStore;

import javax.ws.rs.*;
import javax.xml.bind.JAXBElement;
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
    @Path("/exportSet/{policyname}")
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

    @GET
    @Consumes({"application/xml", "application/xacml+xml", "application/json", "application/xacml+json"})
    @Produces({"application/xml", "application/xacml+xml", "application/json", "application/xacml+json"})
    @Path("/export/{policyname}")
    public JAXBElement<Policy> exportPolicy(@PathParam("policyname") String policyname) {

        final String methodName = "ReferenceRequestHandler.exportPolicy: ";
        Policy result =null;

        try {
            PolicyStore ps = PolicyStoreManager.getInstance();
            result = ps.exportPolicy(policyname);

        } catch (Exception exception) {
            DEBUG.error(methodName + "Exception Occurred: " + exception.getMessage() + ", Returning Indeterminate.",
                    exception);
        }
        ObjectFactory objectFactory = new ObjectFactory();
        return objectFactory.createPolicy(result);
    }

    @GET
    @Consumes({"application/xml", "application/xacml+xml", "application/json", "application/xacml+json"})
    @Produces({"application/xml", "application/xacml+xml", "application/json", "application/xacml+json"})
    @Path("/exportJSON/{policyname}")
    public String exportPolicyasJSON(@PathParam("policyname") String policyname) {

        final String methodName = "ReferenceRequestHandler.exportPolicy: ";
        String result = "";

        try {
            PolicyStore ps = PolicyStoreManager.getInstance();
            XACML3PolicyItem pol = ps.getPolicyForEval(policyname);
            result = pol.asJSONExpression();

        } catch (Exception exception) {
            DEBUG.error(methodName + "Exception Occurred: " + exception.getMessage() + ", Returning Indeterminate.",
                    exception);
        }
        return result;
    }

    @GET
    @Consumes({"application/xml", "application/xacml+xml", "application/json", "application/xacml+json"})
    @Produces({"application/xml", "application/xacml+xml", "application/json", "application/xacml+json"})
    @Path("/exportRPN/{policyname}")
    public String exportPolicyasRPN(@PathParam("policyname") String policyname) {

        final String methodName = "ReferenceRequestHandler.exportPolicy: ";
        String result = "";

        try {
            PolicyStore ps = PolicyStoreManager.getInstance();
            XACML3PolicyItem pol = ps.getPolicyForEval(policyname);
            result = pol.asRPNExpression();

        } catch (Exception exception) {
            DEBUG.error(methodName + "Exception Occurred: " + exception.getMessage() + ", Returning Indeterminate.",
                    exception);
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
    @Consumes
    @Produces({"application/xml", "application/xacml+xml", "application/json", "application/xacml+json","*/*"})
    @Path("/list")
    public String listPolicies() {

        final String methodName = "ReferenceRequestHandler.listPolicySet: ";
        Set<String> names = null;
        try {
            PolicyStore ps = PolicyStoreManager.getInstance();
            names = ps.listPolicies();

        } catch (Exception exception) {
            DEBUG.error(methodName + "Exception Occurred: " + exception.getMessage() + ", Returning Indeterminate.",
                    exception);
        }
        return names.toString();
    }
    @GET
    @Consumes
    @Produces({"application/xml", "application/xacml+xml", "application/json", "application/xacml+json","*/*"})
    @Path("/listsets")
    public String listPolicySets() {

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


                /*
@javax.xml.bind.annotation.XmlSchema(
       namespace = "urn:oasis:names:tc:xacml:3.0:core:schema:wd-17",
        xmlns = {
                @javax.xml.bind.annotation.XmlNs(prefix = "xacml",
                        namespaceURI = "classpath:xsd/xacml-core-v3-schema-wd-17.xsd"),
                @javax.xml.bind.annotation.XmlNs(prefix = "xacml3",
                        namespaceURI = "classpath:xsd/xacml-core-v3-schema-wd-17.xsd"),
                @javax.xml.bind.annotation.XmlNs(prefix = "xacml-context",
                        namespaceURI = "classpath:xsd/xacml-core-v3-schema-wd-17.xsd"),
                @javax.xml.bind.annotation.XmlNs(prefix = "xacml-ctx",
                        namespaceURI = "classpath:xsd/xacml-core-v3-schema-wd-17.xsd")
                },
        elementFormDefault = javax.xml.bind.annotation.XmlNsForm.QUALIFIED)
   */