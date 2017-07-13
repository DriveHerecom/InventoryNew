package com.creadigol.drivehere.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class Auction implements Serializable {

    String floorPrice;
    String auctionName;
    String condition;
    String miles;
    String auctionNote;
    String auctionDate;
    String carReady;
    String carAtAuction;

    List<Image> images;

    public String getFloorPrice() {
        return floorPrice;
    }

    public String getAuctionName() {
        return auctionName;
    }

    public String getCondition() {
        return condition;
    }

    public String getMiles() {
        return miles;
    }

    public String getAuctionNote() {
        return auctionNote;
    }

    public String getAuctionDate() {
        return auctionDate;
    }

    public String getCarReady() {
        return carReady;
    }

    public String getCarAtAuction() {
        return carAtAuction;
    }

    public List<Image> getImages() {
        return images;
    }

    public class Image implements Serializable {
        String imagePath;

        public String getImagePath() {
            return imagePath;
        }
    }
}
