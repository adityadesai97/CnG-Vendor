package com.example.ana.cngvendor.Objects;

/**
 * Created by adityadesai on 11/02/17.
 */

public class Industry {

    private String industryName;
    private String industryId;

    public Industry(){}

    public Industry(String name, String id){
        industryName=name;
        industryId=id;
    }

    public String getName(){
        return industryName;
    }
    public String getId(){
        return industryId;
    }
}
