package com.creadigol.drivehere.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class BaseObject implements Serializable {

    @SerializedName("status_code")
    int statusCode;

    @SerializedName("message")
    String message;

    @SerializedName("dataOneInformation")
    String dataOneInfo;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public String getDataOneInfo() {
        return dataOneInfo;
    }
}
