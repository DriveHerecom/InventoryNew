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

public class BasicResponse extends BaseObject {

    int current_version_code=0;

    public int getCurrent_version_code() {
        return current_version_code;
    }

    public static BasicResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        BasicResponse basicResponse = gson.fromJson(response, BasicResponse.class);
        return basicResponse;
    }
}
