package com.mauriciogiordano.travell.adapter;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mauriciogiordano.travell.R;
import com.mauriciogiordano.travell.model.Place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Mauricio Giordano on 11/8/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class PlacesAdapter extends BaseAdapter {

    private List<Place> dataList;
    private Map<Integer, Integer> colors;

    public void setDataList(List<Place> _dataList) {
        dataList = _dataList;
        colors = new HashMap<>();
        notifyDataSetChanged();
    }

    public void remove(int i) {
        dataList.remove(i);
        colors = new HashMap<>();
        notifyDataSetChanged();
    }

    public PlacesAdapter() {
        dataList = new ArrayList<>();
        colors = new HashMap<>();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(viewGroup.getContext())
                            .inflate(R.layout.adapter_itinerary, viewGroup, false);
        }

        View layoutChip = view.findViewById(R.id.layoutChip);
        ImageView placeImage = (ImageView) view.findViewById(R.id.placeImage);
        TextView placeName = (TextView) view.findViewById(R.id.placeName);
        RatingBar placeRating = (RatingBar) view.findViewById(R.id.placeRating);

        Place place = dataList.get(i);

        if (place.getImages().size() > 0) {
            Glide.with(viewGroup.getContext())
                    .load(place.getImages().get(0))
                    .asBitmap()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .centerCrop()
                    .into(placeImage);
        }

        if (place.getColor() == 0) {
            layoutChip.setBackgroundColor(viewGroup.getContext().getResources().getColor(R.color.action_bar_background));
        } else {
            layoutChip.setBackgroundColor(place.getColor());
        }

        placeName.setText(place.getName());

        placeRating.setRating(place.getRating());

        LayerDrawable stars = (LayerDrawable) placeRating.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(viewGroup.getContext().getResources()
                .getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);



        return view;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Place getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return dataList.get(i).getId().hashCode();
    }
}
