package com.mauriciogiordano.travell.model;

import android.content.Context;

import com.mauriciogiordano.easydb.bean.Model;
import com.mauriciogiordano.travell.api.API;
import com.mauriciogiordano.travell.api.Client;
import com.mauriciogiordano.travell.api.Delegate;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class Destination extends Model<Destination> {

    @ModelField
    private String weight;
    @ModelField
    private String reference;
    @ModelField
    private String city;
    @ModelField
    private String country;
    @ModelField
    private String image;

    public Destination() {
        super(Destination.class, true);
    }

    public Destination(Context context) {
        super(Destination.class, true, context);
    }

    public Destination(JSONObject jsonObject, Context context) {
        super(Destination.class, true, context);

        try {
            weight = jsonObject.getString("weight");
            reference = jsonObject.getString("reference");
            city = jsonObject.getString("city");
            country = jsonObject.getString("country");
            image = jsonObject.getString("image");
        } catch (JSONException ignore) {}
    }

    public Destination(String city, String image, Context context) {
        super(Destination.class, true, context);

        this.city = city;
        this.country = "";
        this.image = image;
    }

    public static Destination find(String id, Context context) {
        Destination dummy = new Destination(context);

        return dummy.find(id);
    }

    public static void getTopDestinations(Delegate delegate) {
        Client client = new Client("/destinations/top");

        client.addParamForGet("limit", "5");

        new API(delegate).request(client, API.GET, delegate);
    }

    public void getTopPlaces(Delegate delegate) {
        Client client = new Client("/destination");

        client.addParamForGet("limit", "10");
        client.addParamForGet("location", getReference());

        new API(delegate).request(client, API.GET, delegate);
    }

    @Override
    public String getId() {
        return reference;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
