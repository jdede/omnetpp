package org.omnetpp.ned.model.interfaces;

import java.util.Map;

import org.omnetpp.ned.model.INEDElement;

/**
 * Interface for aquiring parameter lists for the object
 * @author rhornig
 */
public interface IHasParameters extends INEDElement {

    /**
     * @return The name / parameter node association. You SHOULD NOT modify the returned
     * map.
     */
    public Map<String, INEDElement> getParams();

    /**
     * @return The name / parameter node association where the last parameter - value
     * assignment was done. You SHOULD NOT modify the returned map.
     */
    public Map<String, INEDElement> getParamValues();

}
