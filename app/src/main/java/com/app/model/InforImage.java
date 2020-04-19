package com.app.model;

public class InforImage {
    public int likeCounts;
    public String idImage;

    public InforImage() {
        likeCounts = 0;
    }

    public InforImage(int likeCounts, String idImage) {
        this.likeCounts = likeCounts;
        this.idImage = idImage;
    }


    public int getLikeCounts() {
        return likeCounts;
    }

    public void setLikeCounts(int likeCounts) {
        this.likeCounts = likeCounts;
    }

    public String getIdImage() {
        return idImage;
    }

    public void setIdImage(String idImage) {
        this.idImage = idImage;
    }
}
