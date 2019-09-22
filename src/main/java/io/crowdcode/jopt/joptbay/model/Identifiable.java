package io.crowdcode.jopt.joptbay.model;

import java.io.Serializable;

/**
 * @author Ingo Dueppe (CROWDCODE)
 */
public interface Identifiable<ID extends Serializable> {

    ID getId();

    void setId(ID id);

}
