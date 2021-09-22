package edu.highpoint.ethansweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;

/**
 * Ethan Shealey
 * Ethan's Weather
 * September 21, 2021
 *
 * Mobile App that displays the requested
 * location current conditions and temperature
 */

public class MainActivity extends AppCompatActivity {

    /**
     * Declare widgets
     */
    EditText locationSearch;
    TextView location;
    TextView date;
    TextView country;
    Button searchBtn;
    TextView temp;
    TextView condition;
    ImageView icon;

    /**
     * Declare object used to store API information
     */
    Weather weather;

    /**
     * On app start
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /**
         * Init widgets
         */
        locationSearch = (EditText) findViewById(R.id.locationSearch);
        location = (TextView) findViewById(R.id.location);
        country = (TextView) findViewById(R.id.country);
        date = (TextView) findViewById(R.id.date);
        searchBtn = (Button) findViewById(R.id.search);
        temp = (TextView) findViewById(R.id.temp);
        condition = (TextView) findViewById(R.id.condition);
        icon = (ImageView) findViewById(R.id.weatherIcon);
        weather = new Weather();
        weather.obj = null;

        /**
         * Set default values to search bar and weather data
         */
        locationSearch.setText("High Point, NC");
        callAPI("High Point, NC", weather);
        try {
            setData();
        } catch (InterruptedException | JSONException | ParseException e) {
            e.printStackTrace();
        }

        /**
         * onClick listener for search button
         */
        searchBtn.setOnClickListener(view -> {
            // get text from location search bar
            String query = locationSearch.getText().toString();
            // hide the keyboard
            hideKeyboard(view);
            // reset the API object to null
            weather.obj = null;
            // call the API and set the data
            callAPI(query, weather);
            try {
                setData();
            } catch (InterruptedException | JSONException | ParseException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * getWeather - call the API from weatherapi.com
     *
     * @param loc - the user requested location
     * @param w - the API object used to store the data
     * @throws IOException
     * @throws JSONException
     */
    public static void getWeather(String loc, Weather w) throws IOException, JSONException {
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
                JSONObject obj = new JSONObject((res.toString()));
                // save the data to the weather object passed in
                w.obj = obj;
            }
            catch(JSONException e) {
                System.out.println(e);
            }

        }
    }

    /**
     * callAPI -- wrapper function for getWeather to run
     *            on another thread
     * @param loc - the user requested location
     * @param w - the API object used to store the data
     */
    public static void callAPI(String loc, Weather w) {
        new Thread(() -> {
            try {
                getWeather(loc, w);
            }
            catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * setData -- waits until the weather object has been
     *            filled then sets the widgets to the new
     *            data
     * @throws InterruptedException
     * @throws JSONException
     * @throws ParseException
     */
    public void setData() throws InterruptedException, JSONException, ParseException {
        while(weather.obj == null) {
            Thread.sleep(10);
        }
        temp.setText(weather.getCurrentTemp() + "°F");
        location.setText(weather.getLocation());
        country.setText(weather.getCountry());
        condition.setText(weather.getCondition());
        date.setText(weather.getDate());
        System.out.println(weather.getIcon());
        Picasso.get().load(weather.getIcon()).into(icon);
    }

    /**
     * hideKeyboard - hides the keyboard after use clicks search
     * @param v
     */
    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }
}
