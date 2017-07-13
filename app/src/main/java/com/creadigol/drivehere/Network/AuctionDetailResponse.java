package com.creadigol.drivehere.Network;

import com.creadigol.drivehere.Model.Auction;
import com.creadigol.drivehere.Model.BaseObject;
import com.creadigol.drivehere.Model.Repo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class AuctionDetailResponse extends BaseObject {

    @SerializedName("auction")
    Auction auction;

    public Auction getAuction() {
        return auction;
    }

    public static AuctionDetailResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        AuctionDetailResponse auctionDetailResponse = gson.fromJson(response, AuctionDetailResponse.class);
        return auctionDetailResponse;
    }
}
