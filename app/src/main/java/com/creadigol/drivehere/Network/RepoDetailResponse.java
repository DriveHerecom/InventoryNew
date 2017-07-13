package com.creadigol.drivehere.Network;

import com.creadigol.drivehere.Model.BaseObject;
import com.creadigol.drivehere.Model.CarAdd;
import com.creadigol.drivehere.Model.Repo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class RepoDetailResponse extends BaseObject {

    @SerializedName("repo")
    Repo repo;

    public Repo getRepo() {
        return repo;
    }

    public static RepoDetailResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        RepoDetailResponse repoDetailResponse = gson.fromJson(response, RepoDetailResponse.class);
        return repoDetailResponse;
    }
}
