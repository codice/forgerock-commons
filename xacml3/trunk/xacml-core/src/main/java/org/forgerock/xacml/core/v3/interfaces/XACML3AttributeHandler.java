package org.forgerock.xacml.core.v3.interfaces;


import org.forgerock.xacml.core.v3.engine.XACML3EntitlementException;
import org.forgerock.xacml.core.v3.model.DataBag;
import org.forgerock.xacml.core.v3.model.DataDesignator;
import org.forgerock.xacml.core.v3.engine.XACML3Request;

import java.util.List;

/*
    This interface is the superclass for all Policy Attribute handlers
    Essentially a new handler would be implemented for each Profile that is supported.

    It should be able to return a value for any supported attribute type.

    It is the authoritive source,  and can use the request context, if needed.
 */
public interface XACML3AttributeHandler {
    /*
    return the string representing the prefix for any category that
    this handler will support.
     */
    public List<String> getProfileAttributes();
    public DataBag      resolve(DataDesignator designator, XACML3Request req) throws XACML3EntitlementException;

}
