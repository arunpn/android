package com.mauriciogiordano.travell;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import com.lorentzos.flingswipe.SwipeFlingAdapterView;
import com.mauriciogiordano.travell.adapter.SwipeAdapter;
import com.mauriciogiordano.travell.model.Place;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class SwipeActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_swipe);

        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);

        final SwipeAdapter adapter = new SwipeAdapter();

        List<Place> dataList = new ArrayList<>();
        dataList.add(new Place("São Paulo", "http://claritur.com.br/site/wp-content/uploads/2013/09/sp_groupon.jpg"));
        dataList.add(new Place("Rio de Janeiro", "http://blog.encontresuaviagem.com.br/wp-content/uploads/2015/05/Rio-de-Janeiro.jpg"));
        dataList.add(new Place("Santa Catarina", "http://blog.encontresuaviagem.com.br/wp-content/uploads/2015/05/Rio-de-Janeiro.jpg"));
        dataList.add(new Place("São Pedro", "http://blog.encontresuaviagem.com.br/wp-content/uploads/2015/05/Rio-de-Janeiro.jpg"));

        adapter.setDataList(dataList);

        flingContainer.setAdapter(adapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.d("LIST", "removed object!");
                adapter.remove(0);
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                //Do something on the left!
                //You also have access to the original object.
                //If you want to use it just cast it (String) dataObject
                Toast.makeText(SwipeActivity.this, "Left!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Toast.makeText(SwipeActivity.this, "Right!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here

            }

            @Override
            public void onScroll(float v) {
                
            }

        });

        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(SwipeActivity.this ,"Clicked!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
