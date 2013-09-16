package org.forgerock.xacml.reference.combiner;

import com.sun.identity.entitlement.xacml3.core.DecisionType;
import org.forgerock.xacml.core.v3.engine.XACML3Decision;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: allan
 * Date: 9/7/13
 * Time: 1:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class PermitUnlessDeny extends CombinerBase {
    private XACML3Decision currentResult = new XACML3Decision(DecisionType.PERMIT);

    public void add(XACML3Decision dec) {
        if (dec.getDecision().value().equalsIgnoreCase(DecisionType.INDETERMINATE.value())) {
            return;
        }
        if (dec.getDecision().value().equalsIgnoreCase(DecisionType.DENY.value())) {
            currentResult = dec;
            done = true;
            return;
        }
        if (dec.getDecision().value().equalsIgnoreCase(DecisionType.PERMIT.value())) {
            currentResult = dec;
        }
    }

    public List<XACML3Decision> getResult() {
        List<XACML3Decision> result = new ArrayList<XACML3Decision>();
        result.add(currentResult);
        return result;
    }
    public void register() {
        register("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:permit-unless-deny",this.getClass().getName());
        register("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:permit-unless-deny",this.getClass().getName());
        register("urn:oasis:names:tc:xacml:3.0:rule-combining-algorithm:ordered-permit-unless-deny",this.getClass().getName());
        register("urn:oasis:names:tc:xacml:3.0:policy-combining-algorithm:ordered-permit-unless-deny",this.getClass().getName());
    }

}
