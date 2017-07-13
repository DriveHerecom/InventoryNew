package com.creadigol.drivehere.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class RepoReport implements Serializable {

    @SerializedName("repoCompanyName")
    String companyName;

    @SerializedName("count")
    int totalCars;

    @SerializedName("result")
    List<LotCodeRepo> lotCodeRepos;

    boolean isDetailVisible = false;

    public void setDetailVisible(boolean detailVisible) {
        isDetailVisible = detailVisible;
    }

    public boolean isDetailVisible() {
        return isDetailVisible;
    }

    public String getCompanyName() {
        return companyName;
    }

    public int getTotalCars() {
        return totalCars;
    }

    public List<LotCodeRepo> getLotCodeRepos() {
        return lotCodeRepos;
    }

    public class LotCodeRepo implements Serializable {

        @SerializedName("lotcode")
        String lotCode;

        @SerializedName("total")
        int totalCars;

        public String getLotCode() {
            return lotCode;
        }

        public int getTotalCars() {
            return totalCars;
        }
    }
}
