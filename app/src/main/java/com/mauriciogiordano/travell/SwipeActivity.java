package com.mauriciogiordano.travell;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.mauriciogiordano.travell.adapter.SwipeAdapter;
import com.mauriciogiordano.travell.api.Delegate;
import com.mauriciogiordano.travell.model.Destination;
import com.mauriciogiordano.travell.model.Place;

import org.apache.http.HttpResponse;
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
public class SwipeActivity extends ActionBarActivity {

    private Destination destination;
    private View progressBar;
    private SwipeFlingAdapterView flingContainer;
    private SwipeAdapter adapter;
    private List<Place> dataList;
    private int dataListSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_swipe);

        String destinationId = getIntent().getStringExtra("destinationId");

        if (destinationId == null || destinationId.equals("")) {
            finish();
            return;
        }

        destination = Destination.find(destinationId, this);

        if (destination == null) {
            finish();
            return;
        }

        dataList = new ArrayList<>();

        RelativeLayout like = (RelativeLayout) findViewById(R.id.like);
        RelativeLayout dislike = (RelativeLayout) findViewById(R.id.dislike);
        flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
        progressBar = findViewById(R.id.progressBar);
        adapter = new SwipeAdapter();

        loadPlaces();

        flingContainer.setAdapter(adapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                adapter.remove(0);
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Place place = (Place) dataObject;
                place.setContext(SwipeActivity.this);
                place.remove();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Place place = (Place) dataObject;
                place.setContext(SwipeActivity.this);
                place.save();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                if (itemsInAdapter == 0 && dataListSize > 0) {
                    Intent intent = new Intent(SwipeActivity.this, ItineraryActivity.class);
                    intent.putExtra("destinationId", destination.getId());
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onScroll(float scrollProgressPercent) {

                View view = flingContainer.getSelectedView();
                view.findViewById(R.id.item_swipe_left).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_right).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
                view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }

        });

        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getCount() == 0) return;
                Place place = adapter.getItem(0);
                place.setContext(SwipeActivity.this);
                place.save();
                adapter.remove(0);
            }
        });

        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (adapter.getCount() == 0) return;
                Place place = adapter.getItem(0);
                place.setContext(SwipeActivity.this);
                place.remove();
                adapter.remove(0);
            }
        });

        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Place place = (Place) dataObject;
                place.save();

                Intent intent = new Intent(SwipeActivity.this, PlaceActivity.class);
                intent.putExtra("placeId", place.getId());
                startActivity(intent);
            }
        });
    }

    private void loadPlaces() {
        progressBar.setVisibility(View.VISIBLE);
        flingContainer.setVisibility(View.GONE);

        destination.getTopPlaces(new Delegate() {
            @Override
            public void requestResults(boolean hasInternet, HttpResponse response, JSONObject result) {
                progressBar.setVisibility(View.GONE);
                flingContainer.setVisibility(View.VISIBLE);

                if (hasInternet) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        try {
                            JSONArray jsonArray = result.getJSONArray("data");

                            dataList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Place d = new Place(destination, jsonArray.getJSONObject(i), SwipeActivity.this);

                                dataList.add(d);
                            }

                            dataListSize = dataList.size();

                            adapter.setDataList(dataList);
                        } catch (JSONException e) {

                        }
                    } else {
                        Toast.makeText(SwipeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SwipeActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("destinationId", destination.getId());
    }
}
