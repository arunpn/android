package com.mauriciogiordano.travell.api;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public abstract class Delegate {
    public abstract void requestResults(boolean hasInternet, HttpResponse response, JSONObject result);
}
