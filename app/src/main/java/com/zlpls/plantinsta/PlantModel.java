package com.zlpls.plantinsta;

public class PlantModel {


    private String plantImage;
    private String plantName;
    private String plantComment;
    private String plantAvatar;

    public Boolean getPlantFavorite() {
        return plantFavorite;
    }

    private String plantFirstDate;

    public PlantModel(Boolean plantFavorite) {
        this.plantFavorite = plantFavorite;
    }

    private Boolean plantFavorite;

    public String getPlantImage() {
        return plantImage;
    }

    public void setPlantImage(String plantImage) {
        this.plantImage = plantImage;
    }

    public String getPlantComment() {
        return plantComment;
    }

    public void setPlantComment(String plantComment) {
        this.plantComment = plantComment;
    }

    public String getPlantPostCount() {
        return plantPostCount;
    }

    public void setPlantPostCount(String plantPostCount) {
        this.plantPostCount = plantPostCount;
    }

    private  String plantUserMail;

    public PlantModel(String plantImage, String plantName, String plantComment, String plantAvatar, String plantFirstDate, String plantUserMail, String plantPostCount) {
        this.plantImage = plantImage;
        this.plantName = plantName;
        this.plantComment = plantComment;
        this.plantAvatar = plantAvatar;
        this.plantFirstDate = plantFirstDate;
        this.plantUserMail = plantUserMail;
        this.plantPostCount = plantPostCount;
    }

    private String plantPostCount;

    public PlantModel (){

    }



    public String getPlantAvatar() {
        return plantAvatar;
    }

    public void setPlantAvatar(String plantAvatar) {
        this.plantAvatar = plantAvatar;
    }

    public String getplantFirstDate() {
        return plantFirstDate;
    }

    public void setplantFirstDate(String plantFirstDate) {
        this.plantFirstDate = plantFirstDate;
    }

    public String getPlantUserMail() {
        return plantUserMail;
    }

    public void setPlantUserMail(String plantUserMail) {
        this.plantUserMail = plantUserMail;
    }

    public String getPlantName() {
        return plantName;
    }

    public void setPlantName(String plantName) {
        this.plantName = plantName;
    }



}

