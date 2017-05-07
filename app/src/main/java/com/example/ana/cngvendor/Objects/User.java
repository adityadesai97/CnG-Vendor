package com.example.ana.cngvendor.Objects;

/**
 * Created by USER on 29-03-2017.
 */

public class User {
    private String name;
    private String uid;
    private String mail_id;
    private String phone;
    private String profile_url;

    public User(String name, String uid, String mail_id,String phone, String profile_url) {
        this.name = name;
        this.uid = uid;
        this.mail_id = mail_id;
        this.phone = phone;
        this.profile_url = profile_url;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getMail_id() {
        return mail_id;
    }

    public String getProfile_url() {
        return profile_url;
    }

    public String getPhone() {
        return phone;
    }
}
