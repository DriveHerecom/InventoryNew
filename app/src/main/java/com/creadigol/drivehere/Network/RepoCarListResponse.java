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

public class RepoCarListResponse extends BaseObject {

    @SerializedName("carDetailCount")
    int count;

    @SerializedName("carList")
    List<Car> carList;

    @SerializedName("repoDetail")
    Car repoDetail;

    public List<Car> getCarList() {
        return carList;
    }

    public int getCount() {
        return count;
    }

    public static RepoCarListResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        RepoCarListResponse carListResponse = gson.fromJson(response, RepoCarListResponse.class);
        return carListResponse;
    }
}
