package com.mauriciogiordano.travell;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mauriciogiordano.travell.adapter.ImagesAdapter;
import com.mauriciogiordano.travell.model.Place;

import java.util.List;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class PlaceActivity extends ActionBarActivity {

    public Place place;
    private PlaceActivity activity;
    private GoogleMap mMap;
    private SupportMapFragment mapF;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_place);

        String placeId = getIntent().getStringExtra("placeId");

        if (placeId == null || placeId.equals("")) {
            finish();
            return;
        }

        place = Place.find(placeId, this);

        if (place == null) {
            finish();
            return;
        }

        ViewPager imagePager = (ViewPager) findViewById(R.id.imagePager);
        View reviewLayout = findViewById(R.id.reviewLayout);

        ImagesAdapter adapter = new ImagesAdapter();
        List<String> images = place.getImages();
        images.add("http://blog.encontresuaviagem.com.br/wp-content/uploads/2015/05/Rio-de-Janeiro.jpg");
        adapter.setDataList(images);

        imagePager.setAdapter(adapter);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setTitle(place.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(place.getName());

        TextView reviewText = (TextView) findViewById(R.id.reviewText);
        ImageView reviewImage = (ImageView) findViewById(R.id.reviewImage);

        reviewText.setText(place.getReview_text());

        Glide.with(this)
                .load(place.getReview_image())
                .asBitmap()
                .centerCrop()
                .into(reviewImage);

        FragmentManager fm = getSupportFragmentManager();
        mapF = (SupportMapFragment) fm.findFragmentById(R.id.map);

        if (mapF == null) {
            mapF = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapF).commit();
        }

        if (place.getReview_text().equals("")) {
            reviewLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mMap == null) {
            mMap = mapF.getMap();

            if (mMap != null) {
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(place.getLatitude()),
                                Double.parseDouble(place.getLongitude())))
                        .title(place.getName()));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(Double.parseDouble(place.getLatitude()),
                                Double.parseDouble(place.getLongitude())), 15.0f));
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("placeId", place.getId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
