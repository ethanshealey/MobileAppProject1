package edu.highpoint.ethansweather;

import android.os.Build;

import androidx.annotation.RequiresApi;

import org.json.JSONException;
import org.json.JSONObject;

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
        String loc = obj.getJSONObject("location").get("name").toString() + ", " + obj.getJSONObject("location").get("region").toString();
        String[] loc_arr = loc.split(",");
        return loc_arr[0] + "," + loc_arr[1];
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
