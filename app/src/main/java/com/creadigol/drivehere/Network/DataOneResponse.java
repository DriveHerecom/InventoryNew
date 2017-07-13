package com.creadigol.drivehere.Network;

import com.creadigol.drivehere.Model.BaseObject;
import com.creadigol.drivehere.Model.Car;
import com.creadigol.drivehere.dataone.model.DecoderMessages;
import com.creadigol.drivehere.dataone.model.QueryResponces;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class DataOneResponse extends BaseObject {

    public QueryResponces query_responses;
    public DecoderMessages decoder_messages;

    public static DataOneResponse parseJSON(String response) {
        Gson gson = new Gson();
        DataOneResponse orm = gson.fromJson(response.toString(), DataOneResponse.class);
        return orm;

    }
}
