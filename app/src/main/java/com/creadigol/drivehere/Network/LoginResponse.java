package com.creadigol.drivehere.Network;

import com.creadigol.drivehere.Model.BaseObject;
import com.creadigol.drivehere.Model.ScanHistoryItem;
import com.creadigol.drivehere.Model.UserItem;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class LoginResponse extends BaseObject {

    int count;

    @SerializedName("user")
    UserItem user;

    public UserItem getUser() {
        return user;
    }

    public int getCount() {
        return count;
    }

    public static LoginResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        LoginResponse loginResponse = gson.fromJson(response, LoginResponse.class);
        return loginResponse;
    }
}
