package com.creadigol.drivehere.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class AuctionReport implements Serializable {

    String auctionName;

    @SerializedName("count")
    int totalCars;

    @SerializedName("result")
    List<LotCodeAuction> lotCodeAuctions;

    boolean isDetailVisible = false;

    public void setDetailVisible(boolean detailVisible) {
        isDetailVisible = detailVisible;
    }

    public boolean isDetailVisible() {
        return isDetailVisible;
    }

    public String getAuctionName() {
        return auctionName;
    }

    public int getTotalCars() {
        return totalCars;
    }

    public List<LotCodeAuction> getLotCodeAuctions() {
        return lotCodeAuctions;
    }

    public class LotCodeAuction implements Serializable {

        @SerializedName("lotcode")
        String lotCode;

        @SerializedName("total")
        int totalCars;

        public String getLotCode() {
            return lotCode;
        }

        public int getTotalCars() {
            return totalCars;
        }
    }
}
