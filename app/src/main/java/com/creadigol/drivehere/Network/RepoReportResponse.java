package com.creadigol.drivehere.Network;

import com.creadigol.drivehere.Model.BaseObject;
import com.creadigol.drivehere.Model.RepoReport;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class RepoReportResponse extends BaseObject {

    @SerializedName("carList")
    List<RepoReport> repoReportList;

    public List<RepoReport> getRepoReportList() {
        return repoReportList;
    }

    public static RepoReportResponse parseJSON(String response) {
        Gson gson = new GsonBuilder().create();
        RepoReportResponse carListResponse = gson.fromJson(response, RepoReportResponse.class);
        return carListResponse;
    }
}
