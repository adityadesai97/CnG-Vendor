package com.example.ana.cngvendor.Objects;

import java.util.ArrayList;

/**
 * Created by adityadesai on 13/02/17.
 */

public class Shop {
    private String shopName;
    private String shopAddress;
    private String shopPhone;
    private String shop_id;
    private String industryName;
    private String shopUrl;
    private String ownerId;
    private ArrayList<String> offers;
    private String totalRatePoints;
    private String numRates;
    private ArrayList<String> coordinates;

    public Shop(String name, String address, String phone,String id, String industry, String shop_Url,String ownerId,ArrayList<String> offers,String totalRatePoints,String numRates, ArrayList<String> coordinates){
        shopName=name;
        shopAddress=address;
        shopPhone=phone;
        shop_id = id;
        industryName = industry;
        shopUrl = shop_Url;
        this.ownerId  = ownerId;
        this.offers = offers;
        this.totalRatePoints = totalRatePoints;
        this.numRates = numRates;
        this.coordinates = coordinates;
    }

    public String getShopName(){
        return shopName;
    }

    public String getShopAddress(){
        return shopAddress;
    }

    public String getShopPhone(){
        return shopPhone;
    }

    public String getShop_id() {return shop_id;}

    public String getIndustryName() {return industryName;}

    public String getShopUrl() {return shopUrl;}

    public String getOwnerId() {return ownerId;}

    public ArrayList<String> getOffers() {return offers;}

    public String getTotalRatePoints() {
        return totalRatePoints;
    }

    public String getNumRates() {
        return numRates;
    }

    public ArrayList<String> getCoordinates() {
        return coordinates;
    }
}
