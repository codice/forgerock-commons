package org.forgerock.xacml.core.v3.ImplementationManagers;

import org.forgerock.xacml.core.v3.engine.XACML3Decision;
import org.forgerock.xacml.core.v3.interfaces.EntitlementCombiner;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: allan
 * Date: 9/5/13
 * Time: 7:45 AM
 * To change this template use File | Settings | File Templates.
 */
public class CombinerManager implements EntitlementCombiner {
    private static Map<String,String> combiners = new HashMap<String,String>();
    private List<XACML3Decision> decisions;

    public CombinerManager() {
        decisions = new ArrayList<XACML3Decision>();
    }
    public static void registerHandler(String type, String clazzName) {
        combiners.put(type,clazzName);
    }

    public static EntitlementCombiner getInstance(String type) {

        EntitlementCombiner retval;
        String sName = combiners.get(type);
        if (sName == null) {
            retval = new CombinerManager();
        } else {
            try {
                retval = (EntitlementCombiner)Class.forName(sName).newInstance();
            } catch (Exception ex) {
                retval = new CombinerManager();
            }
        }
        return retval;
    }

    public void add(XACML3Decision dec) {
        decisions.add(dec);

    }
    public void addAll(Collection<XACML3Decision> c) {
        decisions.addAll(c);

    }
    public void addAll(EntitlementCombiner c)  {
        this.addAll(c.getResult());
    }
    public List<XACML3Decision> getResult() {
         return decisions;
    }
    public boolean isDone() {
        return false;
    }
    public void register() {
    }

}









