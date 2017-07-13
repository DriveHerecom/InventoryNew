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

public class CarFindResponse extends BaseObject {

    @SerializedName("car")
    Car car;

    public Car getCar() {
        return car;
    }

    public static CarFindResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        CarFindResponse carListResponse = gson.fromJson(response, CarFindResponse.class);
        return carListResponse;
    }
}
