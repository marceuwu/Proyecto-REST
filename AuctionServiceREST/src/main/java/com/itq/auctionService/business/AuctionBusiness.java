package com.itq.auctionService.business;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import com.itq.auctionService.dao.AuctionDao;
import com.itq.auctionService.dto.Auction;
import com.itq.auctionService.dto.Bid;

@Repository
public class AuctionBusiness {

    @Autowired
    private AuctionDao auctionDao;

    public boolean createAuction(Auction auction) {
        return auctionDao.createAuction(auction);
    }

    public Auction getAuctionById(int auctionId) {
        return auctionDao.getAuctionById(auctionId);
    }

    public boolean updateAuction(int auctionId, Auction auction) {
        return auctionDao.updateAuction(auctionId, auction);
    }

    public boolean deleteAuction(int auctionId) {
        return auctionDao.deleteAuction(auctionId);
    }

    public List<Auction> getAllAuctions() {
        return auctionDao.getAllAuctions();
    }

    public List<Auction> getAuctionsByProviderId(int providerId) {
        return auctionDao.getAuctionsByProviderId(providerId);
    }

    public List<Auction> getAuctionsByUserId(int userId) {
        return auctionDao.getAuctionsByUserId(userId);
    }

    // methods for the bids
    public boolean createBid(int auctionId, Bid bid) {
        return auctionDao.createBid(auctionId, bid);
    }

    public List<Bid> getBidsByAuctionId(int auctionId) {
        return auctionDao.getBidsByAuctionId(auctionId);
    }

    public Bid getPriceWinner(int auctionId) {
        return auctionDao.getHighestBidByAuctionId(auctionId);
    }
}
