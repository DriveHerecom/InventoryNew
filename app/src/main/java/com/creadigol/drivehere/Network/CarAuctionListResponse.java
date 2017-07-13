package com.creadigol.drivehere.Network;

import com.creadigol.drivehere.Model.BaseObject;
import com.creadigol.drivehere.Model.Car;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class CarAuctionListResponse extends BaseObject {

    @SerializedName("carDetailCount")
    int count;

    @SerializedName("carDetail")
    List<Car> carList;

    @SerializedName("auctionDetail")
    Car auctionDetail;
    public List<Car> getCarList() {
        return carList;
    }

    public int getCount() {
        return count;
    }

    public static CarAuctionListResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        CarAuctionListResponse carListResponse = gson.fromJson(response, CarAuctionListResponse.class);
        return carListResponse;
    }
}
