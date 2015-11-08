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
    private String id;
    @ModelField
    private String destinationId;
    @ModelField
    private String name;
    @ModelField
    private List<String> images;
    @ModelField
    private int color;
    @ModelField
    private int rating;
    @ModelField
    private String review_text;
    @ModelField
    private String review_image;
    @ModelField
    private int review_count;
    @ModelField
    private String phone;
    @ModelField
    private String latitude;
    @ModelField
    private String longitude;

    public Place() {
        super(Place.class, false);
        color = 0;
    }

    public Place(Context context) {
        super(Place.class, false, context);
        color = 0;
    }

    public Place(JSONObject jsonObject, Context context) {
        super(Place.class, false, context);

        try {
            color = 0;
            id = "" + jsonObject.optString("place", "").hashCode();
            destinationId = jsonObject.optString("destinationId", "").hashCode() + "";
            name = jsonObject.optString("name", "");
            images = new ArrayList<>();
            rating = jsonObject.optInt("rating", 1);
            review_text = jsonObject.optString("review_text", "");
            review_image = jsonObject.optString("review_image", "");
            review_count = jsonObject.optInt("review_count", 1);
            phone = jsonObject.optString("phone", "");
            latitude = jsonObject.optString("latitude", "");
            longitude = jsonObject.optString("longitude", "");

            JSONArray jsonArray = jsonObject.getJSONArray("images");

            for (int i=0; i<jsonArray.length(); i++) {
                images.add(jsonArray.getJSONObject(i).getString("url"));
            }
        } catch (JSONException ignore) {
            ignore.printStackTrace();
        }
    }

    public Place(Destination destination, String name, String image, Context context) {
        super(Place.class, false, context);

        this.destinationId = destination.getId();
        this.name = name;
        this.images = new ArrayList<>();
        this.images.add(image);
        color = 0;
    }

    public static Place find(String id, Context context) {
        Place dummy = new Place(context);

        return dummy.find(id);
    }

    public static List<Place> findForDestination(String id, Context context) {
        Place dummy = new Place(context);

        List<Place> places = dummy.findAll();
        List<Place> curatedPlaces = new ArrayList<>();

        for (Place place : places) {
            if (place.destinationId.equals(id)) {
                curatedPlaces.add(place);
            }
        }

        return curatedPlaces;
    }

    @Override
    public String getId() {
        return id;
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

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getPlace() {
        return id;
    }

    public void setPlace(String place) {
        this.id = place;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getDestinationId() {
        return destinationId;
    }

    public void setDestinationId(String destinationId) {
        this.destinationId = destinationId;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getReview_text() {
        return review_text;
    }

    public void setReview_text(String review_text) {
        this.review_text = review_text;
    }

    public String getReview_image() {
        return review_image;
    }

    public void setReview_image(String review_image) {
        this.review_image = review_image;
    }

    public int getReview_count() {
        return review_count;
    }

    public void setReview_count(int review_count) {
        this.review_count = review_count;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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
}
