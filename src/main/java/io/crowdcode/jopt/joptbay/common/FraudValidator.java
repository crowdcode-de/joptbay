package io.crowdcode.jopt.joptbay.common;

import io.crowdcode.jopt.joptbay.model.Bid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Ingo Dueppe (CROWDCODE)
 */
@Service
public class FraudValidator {

	@Value("${shouldThrowExceptions:false}")
	private boolean shouldThrowExceptions = false;
	private int counter;

	public void validateBid(String productUuid, Bid bid) {
		// To do everthing
		if (shouldThrowExceptions) {
			try {
				counter++;
				if (counter % 4 == 0) {
					throw new NumberFormatException("ups");
				}
			} catch (NumberFormatException e) {
				// do something else
			}
		}
	}
}
