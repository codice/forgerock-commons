package org.forgerock.xacml.core.v3.engine;

import com.sun.identity.entitlement.xacml3.core.XACMLRootElement;
import org.forgerock.xacml.core.v3.interfaces.EntitlementCombiner;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.JAXBElement;

public interface XACML3PolicyItem {
    public EntitlementCombiner evaluate(XACMLEvalContext pip);
    public JAXBElement<?> getXACML();
    public XACMLRootElement getXACMLRoot();
    public JSONObject toJSONObject() throws JSONException;
    public void init(JSONObject jo) throws JSONException;
    public String asJSONExpression ();
    public String asRPNExpression ();
}
