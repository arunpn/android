package com.mauriciogiordano.travell.fragment;

import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mauriciogiordano.travell.PlaceActivity;
import com.mauriciogiordano.travell.R;
import com.mauriciogiordano.travell.model.Place;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class ReviewFragment extends Fragment {

    private PlaceActivity activity;
    private Place place;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (PlaceActivity) getActivity();
        place = activity.place;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView reviewText = (TextView) view.findViewById(R.id.reviewText);
        ImageView reviewImage = (ImageView) view.findViewById(R.id.reviewImage);
        RatingBar ratingBar = (RatingBar) view.findViewById(R.id.ratingBar);

        reviewText.setText(place.getReview_text());

        Glide.with(this)
                .load(place.getReview_image())
                .centerCrop()
                .crossFade()
                .into(reviewImage);

        ratingBar.setRating(place.getRating());

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
    }
}