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

public class CarImageAddResponse extends BaseObject {

    List<Car.Image> images;

    public List<Car.Image> getImages() {
        return images;
    }

    public static CarImageAddResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        CarImageAddResponse carListResponse = gson.fromJson(response, CarImageAddResponse.class);
        return carListResponse;
    }
}
