package com.mauriciogiordano.travell.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mauriciogiordano.travell.PlaceActivity;
import com.mauriciogiordano.travell.R;
import com.mauriciogiordano.travell.model.Place;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class LocationFragment extends Fragment {

    private PlaceActivity activity;
    private Place place;
    private GoogleMap mMap;
    private SupportMapFragment mapF;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (PlaceActivity) getActivity();
        place = activity.place;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentManager fm = getChildFragmentManager();
        mapF = (SupportMapFragment) fm.findFragmentById(R.id.map);

        if (mapF == null) {
            mapF = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map, mapF).commit();
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

}