package com.hackerkernel.cashking.pojo;

/**
 * Created by QUT on 6/16/2016.
 */
public class DealsListPojo {
    private String imageUrl;
    private String dealName;
    private String dealDescription;
    private String DealOffer;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDealName() {
        return dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public String getDealDescription() {
        return dealDescription;
    }

    public void setDealDescription(String dealDescription) {
        this.dealDescription = dealDescription;
    }

    public String getDealOffer() {
        return DealOffer;
    }

    public void setDealOffer(String dealOffer) {
        DealOffer = dealOffer;
    }
}
