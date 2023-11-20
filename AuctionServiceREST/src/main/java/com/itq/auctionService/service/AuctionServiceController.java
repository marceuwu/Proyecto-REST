package com.itq.auctionService.service;

import java.util.List;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.itq.auctionService.business.AuctionBusiness;
import com.itq.auctionService.dto.Ack;
import com.itq.auctionService.dto.Auction;
import com.itq.auctionService.dto.Bid;

@RestController
@Validated
public class AuctionServiceController {

    @Autowired
    private AuctionBusiness auctionBusiness;

    @PostMapping(value = "/auction", consumes = "application/json", produces = "application/json")
    public Ack createAuction(@RequestBody @Valid Auction auction) {
    	Ack ack = new Ack();

        if(auctionBusiness.createAuction(auction)) {
        	ack.setCode(200);
        	ack.setDescription("Auction created successfully");
        } else {
        	ack.setCode(400);
        	ack.setDescription("Auction not created");
        }
        return ack;
    }

    @PutMapping(value = "/auction/{auctionId}", consumes = "application/json", produces = "application/json")
    public Ack updateAuction(@PathVariable("auctionId") int auctionId, @Valid @RequestBody Auction auction) {
    	Ack ack = new Ack();

        if(auctionBusiness.updateAuction(auctionId, auction)) {
        	ack.setCode(200);
        	ack.setDescription("Auction updated successfully");
        } else {
        	ack.setCode(400);
        	ack.setDescription("Auction not updated");
        }
        return ack;
    }

    @DeleteMapping(value = "/auction/{auctionId}", produces = "application/json")
    public Ack deleteAuction(@PathVariable("auctionId") int auctionId) {
    	Ack ack = new Ack();

        if(auctionBusiness.deleteAuction(auctionId)) {
        	ack.setCode(200);
        	ack.setDescription("Auction deleted successfully");
        } else {
        	ack.setCode(400);
        	ack.setDescription("Auction not deleted");
        }
        return ack;
    }

    @GetMapping(value = "/auction/{auctionId}", produces = "application/json")
    public Auction getAuctionById(@PathVariable("auctionId") int auctionId) {
        return auctionBusiness.getAuctionById(auctionId);
    }

    @GetMapping(value = "/auctions", produces = "application/json")
    public List<Auction> getAllAuctions() {
        return auctionBusiness.getAllAuctions();
    }

    @GetMapping(value = "/auctions/provider/{providerId}", produces = "application/json")
    public List<Auction> getAuctionsByProviderId(@PathVariable("providerId") int providerId) {
        return auctionBusiness.getAuctionsByProviderId(providerId);
    }

    @GetMapping(value = "/auctions/user/{userId}", produces = "application/json")
    public List<Auction> getAuctionsByUserId(@PathVariable("userId") int userId) {
        return auctionBusiness.getAuctionsByUserId(userId);
    }

    @GetMapping(value = "/auction/{auctionId}/bids", produces = "application/json")
    public List<Bid> getBidsByAuctionId(@PathVariable("auctionId") int auctionId) {
        return auctionBusiness.getBidsByAuctionId(auctionId);
    }

    @GetMapping(value = "/auction/{auctionId}/winner", produces = "application/json")
    public Ack getPriceWinner(@PathVariable("auctionId") int auctionId) {
        Ack ack = new Ack();
        Bid bid = auctionBusiness.getPriceWinner(auctionId);

        if(bid != null) {
        	ack.setCode(200);
        	ack.setDescription("The id of the winner is: {" + bid.getClientId() + "}, with a price of: $" + bid.getPrice());
        } else {
        	ack.setCode(400);
        	ack.setDescription("There is no winner");
        }
        return ack;
    }

    @PostMapping(value = "/auction/{auctionId}/bid", consumes = "application/json", produces = "application/json")
    public Ack createBid(@PathVariable("auctionId") int auctionId, @RequestBody @Valid Bid bid) {
    	Ack ack = new Ack();

        if(auctionBusiness.createBid(auctionId, bid)) {
        	ack.setCode(200);
        	ack.setDescription("Bid created successfully");
        } else {
        	ack.setCode(400);
        	ack.setDescription("Bid not created");
        }
        return ack;
    }

}
