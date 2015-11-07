package com.mauriciogiordano.travell.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.mauriciogiordano.travell.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class ImagesAdapter extends PagerAdapter {

    private List<String> dataList;

    public ImagesAdapter() {
        dataList = new ArrayList<>();
    }

    public void setDataList(List<String> _dataList) {
        dataList = _dataList;
        notifyDataSetChanged();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = (ImageView) LayoutInflater.from(container.getContext())
                .inflate(R.layout.adapter_image, container, false);

        Glide.with(container.getContext())
                .load(dataList.get(position))
                .centerCrop()
                .crossFade()
                .into(imageView);

        container.addView(imageView);

        return imageView;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
