package com.creadigol.drivehere.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class Car implements Serializable {

    String stage;
    String carId;
    String vin;
    String rfid;
    String stockNumber;
    @SerializedName("scannDate")
    String scanDate;
    String modelYear;
    String modelNumber;
    String model;
    String make;
    String price;
    String lotCode;
    String note = "";
    @SerializedName("vehicle_status")
    String Status;
    String vacancy;
    String problem = "";
    List<Image> images;
    int imageCount;
    @SerializedName("at_auction")
    String atAuction;
    @SerializedName("repoDetail")
    RepoDetail repoDetail;
    AuctionDetails auctionDetail;

    String miles;
    String hasTitle= "";
    String color = "";
    String titleLocation;
    String titleImage;

    public String getHasTitle() {
        return hasTitle;
    }

    public String getColor() {
        return color;
    }

    public String getMiles() {
        return miles;
    }

    public RepoDetail getRepoDetail() {
        return repoDetail;
    }
    public AuctionDetails getAuctionDetails() {
        return auctionDetail;
    }
    public String getCarId() {
        return carId;
    }

    public String getVin() {
        return vin;
    }

    public String getRfid() {
        return rfid;
    }

    public String getStockNumber() {
        return stockNumber;
    }

    public String getScanDate() {
        return scanDate;
    }

    public String getModelYear() {
        return modelYear;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public String getModel() {
        return model;
    }

    public String getMake() {
        return make;
    }

    public String getPrice() {
        return price;
    }

    public String getLotCode() {
        return lotCode;
    }

    public String getNote() {
        return note;
    }

    public String getStage() {
        return stage;
    }

    public int getImageCount() {
        return imageCount;
    }

    public String getStatus() {
        return Status;
    }

    public String getVacancy() {
        return vacancy;
    }

    public String getProblem() {
        return problem;
    }

    public String getAtAuction() {
        return atAuction;
    }

    public List<Image> getImages() {
        return images;
    }

    public void addImages(List<Image> images) {
        this.images.addAll(images);
    }

    public void setImageCount(int imageCount){
        this.imageCount = imageCount;
    }

    public class Image implements Serializable {

        @SerializedName("imagePath")
        public String image;
        public int priority;

        public String getImage() {
            return image;
        }

        public int getPriority() {
            return priority;
        }
    }

    public class RepoDetail implements Serializable {
        @SerializedName("note")
        String note;
        @SerializedName("voluntary")
        String isVoluntary;
        @SerializedName("repo_company")
        String company;
        @SerializedName("assignedDate")
        String assignedDate;
        @SerializedName("deliveredDate")
        String deliveredDate;
        @SerializedName("status")
        String status;

        public String getStatus() {
            return status;
        }

        public String getAssignedDate() {
            return assignedDate;
        }

        public String getDeliveredDate() {
            return deliveredDate;
        }

        public String getNote() {
            return note;
        }

        public String getIsVoluntary() {
            return isVoluntary;
        }

        public String getCompany() {
            return company;
        }
    }

    public class AuctionDetails implements Serializable{
        @SerializedName("auctionName")
        String auctionName;

        @SerializedName("auctionNote")
        String auctionNote;

        @SerializedName("auctiondate")
        String auctiondate;

        @SerializedName("carReady")
        String carReady;

        @SerializedName("floorPrice")
        String floorPrice;
        @SerializedName("condition")
        String condition;
        @SerializedName("auctionMile")
        String auctionMile;
        @SerializedName("carAtAuction")
        String carAtAuction;

        public String getFloorPrice() {
            return floorPrice;
        }

        public String getCondition() {
            return condition;
        }

        public String getAuctionMile() {
            return auctionMile;
        }

        public String getCarAtAuction() {
            return carAtAuction;
        }

        public String getAuctionName() {
            return auctionName;
        }

        public String getAuctionNote() {
            return auctionNote;
        }

        public String getAuctiondate() {
            return auctiondate;
        }

        public String getCarReady() {
            return carReady;
        }
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public void setVacancy(String vacancy) {
        this.vacancy = vacancy;
    }

    public void setProblem(String problem) {
        this.problem = problem;
    }
}
