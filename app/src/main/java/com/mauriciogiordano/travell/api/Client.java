package com.mauriciogiordano.travell.api;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class Client {

    private Integer PORT = 3000; // 443
    private String METHOD = "http"; // https
    private URI uri = null;
    private String path = API.PATH;

    private static DefaultHttpClient httpClient = null;

    public static HttpClient getNewHttpClient() {
        return httpClient = httpClient == null ? new DefaultHttpClient() : httpClient;
    }

    private List<NameValuePair> dataPost = new ArrayList<NameValuePair>();
    private List<NameValuePair> dataGet  = new ArrayList<NameValuePair>();
    private List<NameValuePair> dataHeader  = new ArrayList<NameValuePair>();

    private String url = API.HOST;

    public Client(String path) {
        this.path = path;
    }

    public void addParam(String key, String value)
    {
        dataPost.add(new BasicNameValuePair(key, value));
        dataGet.add(new BasicNameValuePair(key, value));
    }

    public void addParamForGet(String key, String value)
    {
        dataGet.add(new BasicNameValuePair(key, value));
    }

    public void addParamForPost(String key, String value)
    {
        dataPost.add(new BasicNameValuePair(key, value));
    }

    public HttpResponse executePost()
    {
        HttpClient httpClient = getNewHttpClient();

        HttpResponse response = null;

        try {
            uri = URIUtils.createURI(METHOD, url, PORT, path,
                    dataGet == null ? null : URLEncodedUtils.format(dataGet, "UTF-8"), null);

            HttpPost httpPost = new HttpPost(uri);

            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");

            httpPost.setEntity(new UrlEncodedFormEntity(dataPost, HTTP.UTF_8));

            for (NameValuePair header : dataHeader) {
                httpPost.addHeader(header.getName(), header.getValue());
            }

            response = httpClient.execute(httpPost);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return response;
    }

    public HttpResponse executeGet()
    { //If you want to use get method to hit server

        HttpClient httpClient = getNewHttpClient();

        try {
            uri = URIUtils.createURI(METHOD, url, PORT, path,
                    dataGet == null ? null : URLEncodedUtils.format(dataGet, "UTF-8"), null);
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        HttpGet httpget = new HttpGet(uri);

        for (NameValuePair nameValuePair : dataHeader) {
            httpget.addHeader(nameValuePair.getName(), nameValuePair.getValue());
        }

        HttpResponse response = null;

        try {
            response = httpClient.execute(httpget);
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;
    }

    public String getURI()
    {
        return uri.toString();
    }

    public List<NameValuePair> getParams()
    {
        return dataGet;
    }

    public List<NameValuePair> postParams()
    {
        return dataPost;
    }

}
