package com.mauriciogiordano.travell.model;

import android.content.Context;

import com.mauriciogiordano.easydb.bean.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class Place extends Model<Place> {

    @ModelField
    private String name;
    @ModelField
    private List<String> images;
    @ModelField
    private String reference;
    @ModelField
    private String biography;
    @ModelField
    private String latitude;
    @ModelField
    private String longitude;
    @ModelField
    private String telephone;

    public Place() {
        super(Place.class, true);
    }

    public Place(Context context) {
        super(Place.class, true, context);
    }

    public Place(JSONObject jsonObject, Context context) {
        super(Place.class, true, context);

        try {
            name = jsonObject.getString("name");
            images = new ArrayList<>();
            reference = jsonObject.getString("reference");
            biography = jsonObject.getString("biography");
            latitude = jsonObject.getString("latitude");
            longitude = jsonObject.getString("longitude");
            telephone = jsonObject.getString("telephone");

            JSONArray jsonArray = jsonObject.getJSONArray("images");

            for (int i=0; i<jsonArray.length(); i++) {
                images.add(jsonArray.getString(i));
            }
        } catch (JSONException ignore) {}
    }

    public Place(String name, String image, Context context) {
        super(Place.class, true, context);

        this.name = name;
        this.images = new ArrayList<>();
        this.images.add(image);
    }

    public static Place find(String id, Context context) {
        Place dummy = new Place(context);

        return dummy.find(id);
    }

    @Override
    public String getId() {
        return String.valueOf((name + reference).hashCode());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
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
