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
package org.forgerock.xacml.reference.Resolver;

import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACML3Request;
import org.forgerock.xacml.core.v3.interfaces.XACML3AttributeHandler;
import org.forgerock.xacml.core.v3.model.DataBag;
import org.forgerock.xacml.core.v3.model.DataDesignator;
import org.forgerock.xacml.core.v3.model.NotApplicableException;

import java.util.ArrayList;
import java.util.List;

public class AttributeResolver implements XACML3AttributeHandler{

    public DataBag resolve(DataDesignator designator,XACML3Request req) throws XACML3EntitlementException {

        DataBag bag = null;
        bag = req.getReqData(designator);

        if ((bag == null) && designator.mustExist()) {
            throw new NotApplicableException("Required attribute not present");
        }
        return bag;
    }

    public List<String> getProfileAttributes() {
        return new ArrayList<String>();
    }

}
