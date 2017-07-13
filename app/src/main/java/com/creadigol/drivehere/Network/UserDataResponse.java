package com.creadigol.drivehere.Network;

import com.creadigol.drivehere.Model.BaseObject;
import com.creadigol.drivehere.Model.UserItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class UserDataResponse extends BaseObject {

    int count;

    @SerializedName("userData")
    UserItem userItem;

    public UserItem getUserItem() {
        return userItem;
    }

    public int getCount() {
        return count;
    }

    public static UserDataResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        UserDataResponse scanListResponse = gson.fromJson(response, UserDataResponse.class);
        return scanListResponse;
    }
}
