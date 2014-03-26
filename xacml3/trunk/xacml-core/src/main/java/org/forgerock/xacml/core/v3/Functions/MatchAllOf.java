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
package org.forgerock.xacml.core.v3.Functions;

import com.sun.identity.entitlement.xacml3.core.Match;
import com.sun.identity.entitlement.xacml3.core.ObjectFactory;
import com.sun.identity.entitlement.xacml3.core.XACMLRootElement;
import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.model.FunctionArgument;
import org.forgerock.xacml.core.v3.model.XACMLFunction;

import javax.xml.bind.JAXBElement;
import java.util.List;

/**
 * ForgeRock Specific
 * urn:forgerock:xacml:1.0:function:MatchAllOf
 */
public class MatchAllOf extends XACMLFunction {

    public FunctionArgument evaluate( XACMLEvalContext pip) throws XACML3EntitlementException {

        FunctionArgument retVal = FunctionArgument.trueObject;

        for (int i = 0; i < getArgCount(); i++) {
            FunctionArgument res = getArg(i).doEvaluate(pip);
            if (!res.isTrue()) {
                retVal = FunctionArgument.falseObject;
            }
        }
        return retVal;
    }

    public XACMLRootElement getXACMLRoot() {
        com.sun.identity.entitlement.xacml3.core.AllOf ma = new   com.sun.identity.entitlement.xacml3.core.AllOf();

        List<Match> mall = ma.getMatch();
        for (FunctionArgument arg : arguments) {
            try {
                Match matchFunc = (Match)arg.getXACMLMatch();
                mall.add(matchFunc);
            } catch (XACML3EntitlementException ex) {

            }
        }
        return ma;

    }
    public JAXBElement<?> getXACML() {

        JAXBElement<?>  retVal;
        ObjectFactory objectFactory = new ObjectFactory();

        retVal = objectFactory.createAllOf((com.sun.identity.entitlement.xacml3.core.AllOf)getXACMLRoot());
        return retVal;
    }

    public String asJSONExpression() {

        int args = arguments.size();
        FunctionArgument f = arguments.get(0);
        String retVal = f.asJSONExpression() + " " ;

        if (args > 1) { retVal = retVal + " AND " ; }
        for (int i = 1; i<args;i++) {
            f = arguments.get(i);
            retVal = retVal  + f.asJSONExpression();
            if (i < args -1) retVal = retVal + " AND ";
        }

        return retVal + "\n";
    }


}
