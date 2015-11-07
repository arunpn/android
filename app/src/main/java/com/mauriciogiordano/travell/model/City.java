package com.mauriciogiordano.travell.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class City {

    private String name;
    private String image;

    public City(JSONObject jsonObject) {
        try {
            name = jsonObject.getString("name");
            image = jsonObject.getString("image");
        } catch (JSONException ignore) {}
    }

    public City(String name, String image) {
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
}
