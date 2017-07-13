package com.creadigol.drivehere.Network;

import com.creadigol.drivehere.Model.AuctionReport;
import com.creadigol.drivehere.Model.BaseObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class AuctionReportResponse extends BaseObject {

    @SerializedName("auctionList")
    List<AuctionReport> auctionReportList;

    public List<AuctionReport> getAuctionReportList() {
        return auctionReportList;
    }

    public static AuctionReportResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        AuctionReportResponse carListResponse = gson.fromJson(response, AuctionReportResponse.class);
        return carListResponse;
    }
}
