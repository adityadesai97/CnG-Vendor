package com.example.ana.cngvendor.Objects;

import java.util.ArrayList;

/**
 * Created by adityadesai on 17/02/17.
 */

public class ItemDetail{

    private String itemName;
    private ArrayList<String> itemPrice;
    private String itemDescription;
    private ArrayList<String> itemUrl;


    public ItemDetail(String type, ArrayList<String> price, String description, ArrayList<String> Url){

        itemName=type;
        itemPrice=price;
        itemDescription=description;
        itemUrl = Url;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public String getItemName() {
        return itemName;
    }

    public ArrayList<String> getItemPrice() {
        return itemPrice;
    }

    public ArrayList<String> getItemUrl() {return itemUrl;}
}
