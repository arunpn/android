package com.mauriciogiordano.travell.model;

import android.content.Context;

import com.mauriciogiordano.easydb.bean.Model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class City extends Model<City> {

    @ModelField
    private String name;
    @ModelField
    private String image;

    public City() {
        super(City.class, true);
    }

    public City(Context context) {
        super(City.class, true, context);
    }

    public City(JSONObject jsonObject, Context context) {
        super(City.class, true, context);

        try {
            name = jsonObject.getString("name");
            image = jsonObject.getString("image");
        } catch (JSONException ignore) {}
    }

    public City(String name, String image, Context context) {
        super(City.class, true, context);

        this.name = name;
        this.image = image;
    }

    public static City find(String id, Context context) {
        City dummy = new City(context);

        return dummy.find(id);
    }

    @Override
    public String getId() {
        return String.valueOf((name + image).hashCode());
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
