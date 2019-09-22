package io.crowdcode.jopt.joptbay.controller;

import io.crowdcode.jopt.joptbay.common.FraudValidator;
import io.crowdcode.jopt.joptbay.dto.AuctionSummary;
import io.crowdcode.jopt.joptbay.dto.CreateAuction;
import io.crowdcode.jopt.joptbay.dto.ProductInfo;
import io.crowdcode.jopt.joptbay.dto.ProductUuid;
import io.crowdcode.jopt.joptbay.exceptions.BidTooLowException;
import io.crowdcode.jopt.joptbay.exceptions.InvalidAuctionStateException;
import io.crowdcode.jopt.joptbay.exceptions.ProductNotFoundException;
import io.crowdcode.jopt.joptbay.model.Bid;
import io.crowdcode.jopt.joptbay.service.AuctionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Ingo Dueppe (CROWDCODE)
 */
@RestController
@RequestMapping("/auctions")
public class AuctionController {

	private AuctionService auctionEngine;

	private FraudValidator fraudValidator;

	public AuctionController(AuctionService auctionEngine, FraudValidator fraudValidator) {
		this.auctionEngine = auctionEngine;
		this.fraudValidator = fraudValidator;
	}

	@PostMapping()
	public ResponseEntity<ProductUuid> startAuction(@RequestBody CreateAuction createAuction, UriComponentsBuilder uriComponentsBuilder) {
		String uuid = auctionEngine.startAuction(createAuction);
		Map<String, String> variables = new HashMap<>();
		variables.put("uuid", uuid);
        URI location = uriComponentsBuilder.pathSegment("{uuid}").build(variables);
        return ResponseEntity.created(location).body(new ProductUuid(uuid));
	}


	@PutMapping("/{productUuid}")
	public ResponseEntity<Void> placeBid(@PathVariable String productUuid, @RequestBody Bid bid) throws ProductNotFoundException, InvalidAuctionStateException, BidTooLowException {
		fraudValidator.validateBid(productUuid, bid);
		auctionEngine.placeBidOnProduct(productUuid, bid);
		return ResponseEntity.accepted().build();
	}

	@GetMapping("/{productUuid}")
	public AuctionSummary findProduct(@PathVariable String productUuid) throws ProductNotFoundException {
		return auctionEngine.getAuctionSummary(productUuid);
	}

	@GetMapping()
	public List<ProductInfo> findByTitle(@RequestParam(value = "search", required = false) String searchTerm) {
		return auctionEngine.findProductsByTitleOrDescription(searchTerm);
	}

}
