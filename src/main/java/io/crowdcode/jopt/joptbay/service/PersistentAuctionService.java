package io.crowdcode.jopt.joptbay.service;

import io.crowdcode.jopt.joptbay.dto.AuctionSummary;
import io.crowdcode.jopt.joptbay.dto.CreateAuction;
import io.crowdcode.jopt.joptbay.dto.ProductInfo;
import io.crowdcode.jopt.joptbay.exceptions.BidTooLowException;
import io.crowdcode.jopt.joptbay.exceptions.InvalidAuctionStateException;
import io.crowdcode.jopt.joptbay.exceptions.ProductNotFoundException;
import io.crowdcode.jopt.joptbay.model.Auction;
import io.crowdcode.jopt.joptbay.model.Bid;
import io.crowdcode.jopt.joptbay.model.Product;
import io.crowdcode.jopt.joptbay.repository.AuctionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Ingo Dueppe (CROWDCODE)
 */
@Slf4j
@Service
@Transactional
@Profile("usedb")
public class PersistentAuctionService implements AuctionService {

	protected Map<String, Auction> activeAuctions = new HashMap<>();

	private AuctionRepository auctionRepository;

	public PersistentAuctionService(AuctionRepository auctionRepository) {
		this.auctionRepository = auctionRepository;
	}

	@Override
	public String startAuction(CreateAuction createAuction) {
		String uuid = UUID.randomUUID().toString();
		synchronized (activeAuctions) {
			Product product = new Product()
					.setTitle(createAuction.getProductTitle())
					.setDescription(createAuction.getProductDescription())
					.setProductUuid(uuid);
			Auction auction = new Auction()
					.setBeginDateTime(LocalDateTime.now())
					.setExpireDateTime(LocalDateTime.now().plusSeconds(createAuction.getSecondsToRun()))
					.setProduct(product);

			activeAuctions.put(uuid, auction);
			auctionRepository.save(auction);
		}
		return uuid;
	}

	@Override
	public List<ProductInfo> findProductsByTitleOrDescription(String searchTerm) {
		List<ProductInfo> matchingProducts = new ArrayList<>();
		if (searchTerm != null && searchTerm.length() > 0) {
			synchronized (activeAuctions) {
				executeSearch(searchTerm, matchingProducts);
			}
		}
		return matchingProducts;
	}

	protected void executeSearch(String searchTerm, List<ProductInfo> matchingProducts) {
		matchingProducts.addAll(activeAuctions.values()
				.stream()
				.filter(Auction::isRunning)
				.map(Auction::getProduct)
				.filter((p) -> matchesTitleAndDescriptionOfProduct(searchTerm, p))
				.map(this::mapToProductInfo)
				.collect(Collectors.toList()));
	}

	private boolean matchesTitleAndDescriptionOfProduct(String searchTerm, Product product) {
		return matchesSearch(product.getTitle(), searchTerm)
				|| matchesSearch(product.getDescription(), searchTerm);
	}

	private ProductInfo mapToProductInfo(Product product) {
		return new ProductInfo()
				.setProductTitle(product.getTitle())
				.setProductUuid(product.getProductUuid());
	}

	protected boolean matchesSearch(String value, String searchTerm) {
		if (value != null && value.trim().length() > 0) {
			if (value.trim().toUpperCase().contains(searchTerm.toUpperCase().trim())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public Bid getHighestBidForProduct(String productUuid) throws ProductNotFoundException {
		return auctionRepository
				.findByProductProductUuid(productUuid)
				.orElseThrow(ProductNotFoundException::new)
				.getHighestBid();
	}

	@Override
	public AuctionSummary getAuctionSummary(String productUuid) throws ProductNotFoundException {
		return mapToSummary(retrieveAuction(productUuid));
	}

	private Auction retrieveAuction(String productUuid) throws ProductNotFoundException {
		Auction auction = activeAuctions.get(productUuid);
		if (auction == null) {
			auction = auctionRepository.findByProductProductUuid(productUuid)
					.orElseThrow(ProductNotFoundException::new);
		}
		return auction;

//		Need Java 9 or higher
//		return Optional.ofNullable(activeAuctions.get(productUuid))
//				.or(() -> auctionRepository.findByProductProductUuid(productUuid))
//				.orElseThrow(ProductNotFoundException::new);
	}

	private AuctionSummary mapToSummary(Auction auction) {
		return new AuctionSummary()
				.setExpiresAt(auction.getExpireDateTime())
				.setHighestBid(auction.getHighestBid())
				.setProductTitle(auction.getProduct().getTitle())
				.setProductUuid(auction.getProduct().getProductUuid());
	}

	@Override
	public synchronized void placeBidOnProduct(String productUiid, Bid bid) throws ProductNotFoundException, BidTooLowException, InvalidAuctionStateException {
		Auction auction = retrieveAuction(productUiid);
		synchronized (activeAuctions) {
			auction.addBid(bid);
			auctionRepository.save(auction);
		}
	}

	@Override
	@Scheduled(fixedRate = 5_000)
	public void handleExpiredAuctions() {
		log.info("Removing expired auctions");
		activeAuctions.values().removeIf(Auction::isExpired);
	}

	@Override
	public int activeAuctionCount() {
		return activeAuctions.size();
	}
}
