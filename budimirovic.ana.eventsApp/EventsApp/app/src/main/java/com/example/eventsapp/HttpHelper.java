package com.example.eventsapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpHelper {

    private static final int SUCCESS = HttpURLConnection.HTTP_OK;
    private static final String BASE_URL = "http://192.168.0.5:3000";

    public JSONObject getJSONObjectFromUrl(String urlString) throws IOException, JSONException{
        HttpURLConnection urlConnection = null;
        String completeUrl = HttpHelper.BASE_URL + urlString;
        java.net.URL url = new URL(completeUrl);
        urlConnection = (HttpURLConnection) url.openConnection();

        // header fields
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Accept", "application/json");  // types of answers that are accepted
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);

        // try to connect to server
        try{
            urlConnection.connect();
        }catch(IOException e){
            return null;
        }

        // url.OpenStream() opens direct stream of bytes
        // InputStreamReader converts from bytes to characters
        // BufferedReader to store incoming data
        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {    // read lines from BufferedReader
            sb.append(line + "\n");                // place into StringBuilder
        }
        br.close();

        String jsonString = sb.toString();
        Log.d("HTTP GET", "JSON obj- " + jsonString);

        int responseCode = urlConnection.getResponseCode();
        urlConnection.disconnect();

        return responseCode == SUCCESS ? new JSONObject(jsonString) : null;
    }

    public JSONObject postJSONObjectFromURL(String urlString, JSONObject jsonObject, String requestMethod) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        String completeUrl = HttpHelper.BASE_URL + urlString;
        java.net.URL url = new URL(completeUrl);
        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod(requestMethod);
        urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
        urlConnection.setRequestProperty("Accept", "application/json");

        /* needed when used POST or PUT methods */
        urlConnection.setDoOutput(true);      // allows sending body of the message
        urlConnection.setDoInput(true);

        urlConnection.connect();

        DataOutputStream os = new DataOutputStream(urlConnection.getOutputStream());
        /* write json object */
        os.writeBytes(jsonObject.toString());
        os.flush();
        os.close();

        int responseCode = urlConnection.getResponseCode();
        Log.i("STATUS", String.valueOf(responseCode));
        Log.i("MSG", urlConnection.getResponseMessage());

        JSONObject responseObj = null;
        BufferedReader br;

        if(responseCode == HttpURLConnection.HTTP_OK || responseCode == HttpURLConnection.HTTP_CREATED){
            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        }else{
            br = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));

        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        br.close();
        responseObj = new JSONObject(sb.toString());
        responseObj.put("http_status_code", responseCode);


        urlConnection.disconnect();
        return responseObj;
    }

    public JSONArray getJSONArrayFromUrl(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        String completeUrl = HttpHelper.BASE_URL + urlString;
        java.net.URL url = new URL(completeUrl);
        urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Accept", "application/json");
        urlConnection.setReadTimeout(10000);
        urlConnection.setConnectTimeout(15000);

        try {
            urlConnection.connect();
        } catch (IOException e) {
            return null;
        }

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line).append("\n");
        }
        br.close();

        String jsonString = sb.toString();
        Log.d("HTTP GET ARRAY", "JSON Array- " + jsonString);

        int responseCode = urlConnection.getResponseCode();
        urlConnection.disconnect();

        return responseCode == SUCCESS ? new JSONArray(jsonString) : null;
    }

}

