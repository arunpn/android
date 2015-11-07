package com.mauriciogiordano.travell.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class Place {

    private String name;
    private String image;
    private String reference;
    private String biography;
    private String latitude;
    private String longitude;
    private String telephone;

    public Place(JSONObject jsonObject) {
        try {
            name = jsonObject.getString("name");
            image = jsonObject.getString("image");
            reference = jsonObject.getString("reference");
            biography = jsonObject.getString("biography");
            latitude = jsonObject.getString("latitude");
            longitude = jsonObject.getString("longitude");
            telephone = jsonObject.getString("telephone");
        } catch (JSONException ignore) {}
    }

    public Place(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
