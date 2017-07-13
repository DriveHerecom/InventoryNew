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

public class CarListResponse extends BaseObject {

    int count;

    @SerializedName("cars")
    List<Car> carList;

    public List<Car> getCarList() {
        return carList;
    }

    public int getCount() {
        return count;
    }

    public static CarListResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        CarListResponse carListResponse = gson.fromJson(response, CarListResponse.class);

        return carListResponse;
    }
}
