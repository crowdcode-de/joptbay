package io.crowdcode.jopt.joptbay.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Ingo Dueppe (CROWDCODE)
 */
@Data
@Accessors(chain = true)
public class ProductInfo {

    private String productUuid;
    private String productTitle;

}
