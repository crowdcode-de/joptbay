package io.crowdcode.jopt.joptbay.dto;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Ingo Dueppe (CROWDCODE)
 */
@Data
@Accessors(chain = true)
public class CreateAuction {

    private String productTitle;
    private String productDescription;
    private int secondsToRun;

}
