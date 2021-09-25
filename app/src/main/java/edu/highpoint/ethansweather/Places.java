package edu.highpoint.ethansweather;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Places {

    /**
     * Create JSON array of places and flag
     */
    JSONArray places = new JSONArray();
    protected boolean searching = false;

    /**
     * getData
     * Retrieves the data from the API and stores it in JSONArray
     * @param q - Current value of search box
     */
    protected void getData(String q) throws IOException, JSONException {
        searching = true;
        /**
         * Create a new URL object of the API call
         */
        URL url = new URL("https://api.weatherapi.com/v1/search.json?key=a60212c2ecf94da9a2101045212705&q=" + q);
        String nl = null;
        /**
         * Create an HTTP connection and set method to GET
         */
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        /**
         * Find the response code -- 200 OK is what we want
         */
        int resCode = con.getResponseCode();
        /**
         * if response code is what we want ->
         */
        if (resCode == HttpURLConnection.HTTP_OK) {
            //Read in the data from the API
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuffer res = new StringBuffer();
            while ((nl = in.readLine()) != null) {
                res.append(nl);
            }
            in.close();
            places = new JSONArray(res.toString());
        }
        searching = false;
    }

    /**
     * getPlaces
     * Wrapper to call API in new thread
     * @param q - current value of search box
     */
    public void getPlaces(String q) {
        new Thread(() -> {
            try {
                getData(q);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * load
     * Returns the list of strings
     */
    public String[] load(String q) throws JSONException {
        searching = true;
        getPlaces(q);
        List<String> locations = new ArrayList<String>();
        for(int i = 0; i < places.length(); i++) {
            locations.add((String) places.getJSONObject(i).get("name"));
        }
        searching = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return locations.stream().toArray(String[]::new);
        }
        return new String[0];
    }

    /**
     * isSearching
     * Tells program if the class is
     * still searching the API
     * @return state of searching
     */
    public boolean isSearching() {
        return searching;
    }

}
