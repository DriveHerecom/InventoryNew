package com.creadigol.drivehere.Network;

import com.creadigol.drivehere.Model.BaseObject;
import com.creadigol.drivehere.Model.Car;
import com.creadigol.drivehere.Model.CarAdd;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class CarDetailResponse extends BaseObject {

    @SerializedName("detail")
    CarAdd carDetail;

    public CarAdd getCarDetail() {
        return carDetail;
    }

    public static CarDetailResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        CarDetailResponse carListResponse = gson.fromJson(response, CarDetailResponse.class);
        return carListResponse;
    }
}
