package edu.highpoint.ethansweather;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Weather {

    /**
     * Object to hold the JSON recieved from API
     */
    JSONObject obj;

    /**
     * getData
     * Gets the data from weatherapi.com
     * @param loc - The user requested location
     */
    protected void getData(String loc) throws IOException {
        /**
         * Create a new URL object of the API call
         */
        URL url = new URL("https://api.weatherapi.com/v1/current.json?key=a60212c2ecf94da9a2101045212705&q=" + loc + "&aqi=no");
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
            try {
                JSONObject obj_ = new JSONObject((res.toString()));
                // save the data to the weather object passed in
                obj = obj_;
            }
            catch(JSONException e) {
                System.out.println(e);
            }

        }
    }

    /**
     * getWeather
     * Wrapper for the API call to run on new thread
     * @param loc - The user requested location
     */
    public void getWeather(String loc) {
        new Thread(() -> {
            try {
                getData(loc);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * getCurrentTemp
     * @return - gets the current temp in Fahrenheit from the API object
     * @throws JSONException
     */
    public String getCurrentTemp() throws JSONException {
        return obj.getJSONObject("current").get("temp_f").toString();
    }

    /**
     * getLocation
     * @return - gets the location from the API object and formats it
     * @throws JSONException
     */
    public String getLocation() throws JSONException {
        return obj.getJSONObject("location").get("name").toString() + ", " + obj.getJSONObject("location").get("region").toString();
    }

    /**
     * getCountry
     * @return - gets the country from the API object
     * @throws JSONException
     */
    public String getCountry() throws JSONException {
        return obj.getJSONObject("location").get("country").toString();
    }

    /**
     * getDate
     * @return - gets the date from the API object then converts it to EEEE, MMM dd format
     * @throws JSONException
     * @throws ParseException
     */
    public String getDate() throws JSONException, ParseException {
        String localtime = obj.getJSONObject("location").get("localtime").toString();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        DateFormat df = new SimpleDateFormat("EEEE, MMM dd");
        Date date = format.parse(localtime);
        return df.format(date);
    }

    /**
     * getCondition
     * @return - gets the current condition from the API object
     * @throws JSONException
     */
    public String getCondition() throws JSONException {
        return obj.getJSONObject("current").getJSONObject("condition").get("text").toString();
    }

    /**
     * getIcon
     * @return - gets the icon URL from the API object
     * @throws JSONException
     */
    public String getIcon() throws JSONException {
        return "https://" + obj.getJSONObject("current").getJSONObject("condition").get("icon").toString().substring(2);
    }

}
