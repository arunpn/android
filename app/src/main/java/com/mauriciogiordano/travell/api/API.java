package com.mauriciogiordano.travell.api;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.UnknownHostException;

/**
 * Created by Mauricio Giordano on 11/7/15.
 * Author: Mauricio Giordano (mauricio.c.giordano@gmail.com)
 * Copyright (c) by Travell, 2015 - All rights reserved.
 */
public class API {
    private static final String TAG = "DefAPI";

    private Delegate delegateReceiver;

    public final static String HOST = "10.0.3.2"; // inevent.us
    public final static String PATH = "/";
    public final static String URL = "http://10.0.3.2:3000";
    public final static int GET 	= 0;
    public final static int POST 	= 1;

    public API(Delegate delegateReceiver) {
        this.delegateReceiver = delegateReceiver;
    }

    //=============
    // BOILERPLATE
    //=============
    //
    //	public void personSignIn(String email, String password)
    //	{
    //    	HttpClientHelper client = new HttpClientHelper(HOST);  //Write your url here
    //
    //      client.addParamForGet("method", "doctor.signIn");
    //      client.addParamForGet("email", email);
    //      client.addParamForGet("password", password);
    //
    //		new InEventAPIRequest(client, GET, context).execute("");
    //	}

    public void request(Client c, int m, Delegate d) {
        if (d != null) {
            new InEventAPIRequest(c, m, d).execute("");
        } else {
            new InEventAPIRequest(c, m).execute("");
        }
    }

    public static class InEventAPIRequest extends AsyncTask<String, String, String> {
        private Client client = null;
        private int	method = 0;

        /*
         * 0: GET
         * 1: POST
         * 2: HYBRID (GET + POST)
         */
        private Delegate delegateReceiver		= null;
        private HttpResponse response			= null;
        private JSONObject result				= null;
        private boolean hasInternet				= true;

        public InEventAPIRequest(Client client, int method) {
            this.client 			= client;
            this.method 			= method;
        }

        public InEventAPIRequest(Client client, int method, Delegate delegateReceiver) {
            this.client 			= client;
            this.method 			= method;
            this.delegateReceiver 	= delegateReceiver;
        }

        @Override
        protected String doInBackground(String... data) {
            try {
                switch (this.method) {
                    case GET:
                        this.response = client.executeGet();
                        break;
                    case POST:
                        this.response = client.executePost();
                        break;
                    default:
                        this.response = client.executeGet();
                }

                HttpEntity entity = response.getEntity();

                try {
                    this.result = new JSONObject(EntityUtils.toString(entity));
                } catch (JSONException e) {
                    this.result = new JSONObject();
                    return null;
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            catch(Exception e) { e.printStackTrace(); }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (this.response == null) {
                this.hasInternet = false;
            }

            try {
                String error = "";

                try {
                    error = response.getFirstHeader("X-Error").getValue();
                } catch (NullPointerException e) {

                }

                Log.d(TAG, "URI: " + client.getURI() + "\nRESPONSE: " + response.getStatusLine().toString() + "; " + error);
                Log.d(TAG, "POST: "+ client.postParams().toString());

            } catch(NullPointerException e) {
                Log.d(TAG, "NO INTERNET CONNECTION!");
            }

            if (delegateReceiver != null)
                delegateReceiver.requestResults(this.hasInternet, this.response, this.result);
        }
    }
}