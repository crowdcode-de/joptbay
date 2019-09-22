package io.crowdcode.jopt.joptbay.model;


import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * @author Ingo Dueppe (CROWDCODE)
 */
@MappedSuperclass
public class AbstractEntity<ID extends Serializable> implements Identifiable<ID> {

    @Id
    @GeneratedValue
    private ID id;

    @Override
    public ID getId() {
        return id;
    }

    @Override
    public void setId(ID id) {
        this.id = id;
    }

}
