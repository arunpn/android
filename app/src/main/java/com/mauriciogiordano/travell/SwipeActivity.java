package com.mauriciogiordano.travell;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.mauriciogiordano.travell.adapter.SwipeAdapter;
import com.mauriciogiordano.travell.api.Delegate;
import com.mauriciogiordano.travell.model.Destination;
import com.mauriciogiordano.travell.model.Place;

import org.apache.http.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
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
    private View actions;
    private SwipeFlingAdapterView flingContainer;
    private SwipeAdapter adapter;
    private List<Place> dataList;
    private int dataListSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_swipe);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(R.color.status_bar_background));
        }

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
        actions = findViewById(R.id.actions);
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

        getSupportActionBar().setTitle(destination.getCity());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void loadPlaces() {
        progressBar.setVisibility(View.VISIBLE);
        flingContainer.setVisibility(View.GONE);
        actions.setVisibility(View.GONE);

        destination.getTopPlaces(new Delegate() {
            @Override
            public void requestResults(boolean hasInternet, HttpResponse response, JSONObject result) {
                progressBar.setVisibility(View.GONE);
                flingContainer.setVisibility(View.VISIBLE);
                actions.setVisibility(View.VISIBLE);

                if (hasInternet) {
                    if (response.getStatusLine().getStatusCode() == 200) {
                        try {
                            JSONArray jsonArray = result.getJSONArray("data");

                            dataList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                Place d = new Place(jsonArray.getJSONObject(i), SwipeActivity.this);

                                Log.d("PLACEID", "" + d.getId().hashCode());

                                dataList.add(d);
                            }

                            dataListSize = dataList.size();

                            adapter.setDataList(dataList);

                            for (Place place : dataList) {
                                try {
                                    FutureTarget<File> future = Glide.with(SwipeActivity.this)
                                        .load(place.getImages().get(0))
                                        .downloadOnly(400, 400);

                                    File cacheFile = future.get();
                                } catch (Exception e) {

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
