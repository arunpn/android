package com.mauriciogiordano.travell;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.mauriciogiordano.travell.adapter.ImagesAdapter;
import com.mauriciogiordano.travell.model.Place;

import java.util.List;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class PlaceActivity extends ActionBarActivity {

    private Place place;

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

        TextView name = (TextView) findViewById(R.id.name);
        ViewPager imagePager = (ViewPager) findViewById(R.id.imagePager);

        name.setText(place.getName());

        ImagesAdapter adapter = new ImagesAdapter();
        List<String> images = place.getImages();
        images.add("http://blog.encontresuaviagem.com.br/wp-content/uploads/2015/05/Rio-de-Janeiro.jpg");
        adapter.setDataList(images);

        imagePager.setAdapter(adapter);

        getSupportActionBar().setTitle(place.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
