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

/*
urn:oasis:names:tc:xacml:1.0:function:integer-equal
This function SHALL take two arguments of data-type “http://www.w3.org/2001/XMLSchema#integer”
 and SHALL return an “http://www.w3.org/2001/XMLSchema#boolean”.
 The function SHALL return “True” if and only if the two arguments represent the same number.
 */

import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.engine.XACMLEvalContext;
import org.forgerock.xacml.core.v3.model.FunctionArgument;
import org.forgerock.xacml.core.v3.model.XACMLFunction;

/**
 * urn:oasis:names:tc:xacml:1.0:function:integer-equal
 */
public class IntegerEqual extends XACMLFunction {

    public IntegerEqual()  {
    }
    public FunctionArgument evaluate( XACMLEvalContext pip) throws XACML3EntitlementException {

        FunctionArgument retVal =  FunctionArgument.falseObject;

        if ( getArgCount() != 2) {
            return retVal;
        }

        Integer arg0 = getArg(0).asInteger(pip);
        Integer arg1 = getArg(1).asInteger(pip);

        if (arg0.intValue() == arg1.intValue()) {
            retVal = FunctionArgument.trueObject;
        } else {
            retVal = FunctionArgument.falseObject;
        }
        return retVal;
    }
}
