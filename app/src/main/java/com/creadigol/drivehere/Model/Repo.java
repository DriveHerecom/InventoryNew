package com.creadigol.drivehere.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class Repo implements Serializable {

    @SerializedName("assigned_date")
    String assignedDate;
    @SerializedName("delivered_date")
    String deliveredDate;
    @SerializedName("voluntary")
    String isVoluntary;
    @SerializedName("repo_company")
    String repoCompany;
    String note;
    String status = "";
    String lotCode = "";
    List<Image> images;

    public String getLotCode() {
        return lotCode;
    }

    public String getStatus() {
        return status;
    }

    public String getAssignedDate() {
        return assignedDate;
    }

    public String getDeliveredDate() {
        return deliveredDate;
    }

    public String getIsVoluntary() {
        return isVoluntary;
    }

    public String getRepoCompany() {
        return repoCompany;
    }

    public String getNote() {
        return note;
    }

    public List<Image> getImages() {
        return images;
    }

    public class Image{
        String imagePath;

        public String getImagePath() {
            return imagePath;
        }
    }
}
