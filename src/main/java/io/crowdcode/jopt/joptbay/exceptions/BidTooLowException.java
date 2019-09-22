package io.crowdcode.jopt.joptbay.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * @author Ingo Dueppe (CROWDCODE)
 */
@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
public class BidTooLowException extends Exception {

    public BidTooLowException() {
        super("bid too low");
    }
}
