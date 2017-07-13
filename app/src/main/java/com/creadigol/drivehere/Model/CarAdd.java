package com.creadigol.drivehere.Model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by ADMIN on 24-05-2017.
 */

public class CarAdd implements Serializable {

    String carId = "";

    String vin = "";
    String rfid = "";
    String lotCode = "";
    String miles = "";
    String vacancy = "";
    String stage = "";
    String stockNumber = "";
    @SerializedName("has_title")
    String hasTitle = "";
    @SerializedName("title_location")
    String titleLocation = "";
    ArrayList<String> titleLocalImages;
    ArrayList<String> localImages;
    ArrayList<Image> images;
    String dataOneBase64 = "";
    @SerializedName("titlePhoto")
    ArrayList<Image> titleImages;

    @SerializedName("gps")
    ArrayList<GPS> gpses;

    String deletePhotoLink = "";
    String color = "";
    String note = "";

    /********************* Data One ************************/

    boolean isDataOneFound = false;
    String modelYear = "", modelNumber = "", model = "", make = "", vehicleType = "", driveType = "",
            maxHp = "", maxTorque = "", cylinder = "", oilCapacity = "", fuelType = "";

    public String getDeletePhotoLink() {
        return deletePhotoLink;
    }

    public void setDeletePhotoLink(String deletePhotoLink) {
        if (deletePhotoLink.length() <= 0)
            this.deletePhotoLink = deletePhotoLink;
        else
            this.deletePhotoLink = this.deletePhotoLink + "," + deletePhotoLink;
    }

    public ArrayList<GPS> getGpses() {
        return gpses;
    }

    public void setGpses(ArrayList<GPS> gpses) {
        this.gpses = gpses;
    }

    /********************* Getter ************************/

    public String getNote() {
        return note;
    }

    /************************* Setters *************************/

    public void setNote(String note) {
        this.note = note;
    }

    public ArrayList<Image> getTitleImages() {
        return titleImages;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public ArrayList<Image> getImages() {
        return images;
    }

    public String getDataOneBase64() {
        return dataOneBase64;
    }

    public void setDataOneBase64(String dataOneBase64) {
        this.dataOneBase64 = dataOneBase64;
    }

    public String getMiles() {
        return miles;
    }

    public void setMiles(String miles) {
        this.miles = miles;
    }

    public String getHasTitle() {
        return hasTitle;
    }

    public void setHasTitle(String hasTitle) {
        this.hasTitle = hasTitle;
    }

    public String getTitleLocation() {
        return titleLocation;
    }

    public void setTitleLocation(String titleLocation) {
        this.titleLocation = titleLocation;
    }

    public ArrayList<String> getTitleLocalImages() {
        return titleLocalImages;
    }

    public void setTitleLocalImages(ArrayList<String> titleLocalImages) {
        this.titleLocalImages = titleLocalImages;
    }

    public ArrayList<String> getLocalImages() {
        return localImages;
    }

    public void setLocalImages(ArrayList<String> localImages) {
        this.localImages = localImages;
    }

    public boolean isDataOneFound() {
        return isDataOneFound;
    }

    public void setDataOneFound(boolean dataOneFound) {
        isDataOneFound = dataOneFound;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getDriveType() {
        return driveType;
    }

    public void setDriveType(String driveType) {
        this.driveType = driveType;
    }

    public String getMaxHp() {
        return maxHp;
    }

    public void setMaxHp(String maxHp) {
        this.maxHp = maxHp;
    }

    public String getMaxTorque() {
        return maxTorque;
    }

    public void setMaxTorque(String maxTorque) {
        this.maxTorque = maxTorque;
    }

    public String getCylinder() {
        return cylinder;
    }

    public void setCylinder(String cylinder) {
        this.cylinder = cylinder;
    }

    public String getOilCapacity() {
        return oilCapacity;
    }

    public void setOilCapacity(String oilCapacity) {
        this.oilCapacity = oilCapacity;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getRfid() {
        return rfid;
    }

    public void setRfid(String rfid) {
        this.rfid = rfid;
    }

    public String getStockNumber() {
        return stockNumber;
    }

    public void setStockNumber(String stockNumber) {
        this.stockNumber = stockNumber;
    }

    public String getModelYear() {
        return modelYear;
    }

    public void setModelYear(String modelYear) {
        this.modelYear = modelYear;
    }

    public String getModelNumber() {
        return modelNumber;
    }

    public void setModelNumber(String modelNumber) {
        this.modelNumber = modelNumber;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getLotCode() {
        return lotCode;
    }

    public void setLotCode(String lotCode) {
        this.lotCode = lotCode;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getVacancy() {
        return vacancy;
    }

    public void setVacancy(String vacancy) {
        this.vacancy = vacancy;
    }

    public void addTitleLocalImage(String titleLocalImage) {
        if (this.titleLocalImages == null) {
            titleLocalImages = new ArrayList<>();
        }
        titleLocalImages.add(titleLocalImage);
    }

    public void addLocalImage(String localImage) {
        if (this.localImages == null) {
            this.localImages = new ArrayList<>();
        }
        this.localImages.add(localImage);
    }

    public void addLocalImages(ArrayList<String> images) {
        this.localImages.addAll(images);
    }

    public void addImage(String image) {
        this.localImages.add(image);
    }

    public static class GPS implements Serializable {

        @SerializedName("imagePath")
        String image;
        String value;
        @SerializedName("technician")
        String technicianName;

        public String getTechnicianName() {
            return technicianName;
        }

        public void setTechnicianName(String technicianName) {
            this.technicianName = technicianName;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    public class Image implements Serializable {

        String imagePath;
        int priority;

        public String getImagePath() {
            return imagePath;
        }

        public int getPriority() {
            return priority;
        }
    }

}
