package edu.highpoint.ethansweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import java.util.Arrays;
import java.util.List;

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
    AutoCompleteTextView locationSearch;
    TextView location;
    TextView date;
    TextView country;
    Button searchBtn;
    Button reset;
    TextView temp;
    TextView condition;
    ImageView icon;

    /**
     * Declare object used to store API information
     */
    Weather weather;
    Places places;

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
        locationSearch = (AutoCompleteTextView) findViewById(R.id.locationSearch);
        location = (TextView) findViewById(R.id.location);
        country = (TextView) findViewById(R.id.country);
        date = (TextView) findViewById(R.id.date);
        searchBtn = (Button) findViewById(R.id.search);
        reset = (Button) findViewById(R.id.resetLocation);
        temp = (TextView) findViewById(R.id.temp);
        condition = (TextView) findViewById(R.id.condition);
        icon = (ImageView) findViewById(R.id.weatherIcon);
        weather = new Weather();
        places = new Places();

        // test stuff
        String[] test = new String[] {"1", "2"};
        System.out.println(Arrays.asList(test).indexOf("3"));


        /**
         * Set default values to search bar and weather data
         */
        locationSearch.setText("High Point, NC");
        weather.getWeather("High Point, NC");
        try {
            setWeatherData();
        } catch (InterruptedException | JSONException | ParseException e) {
            e.printStackTrace();
        }

        /**
         * onClick listener for search button
         */
        searchBtn.setOnClickListener(view -> {
            // get text from location search bar
            String query = locationSearch.getText().toString().replaceAll("'", "");
            // hide the keyboard
            hideKeyboard(view);
            // call the API and set the data
            weather.getWeather(query);
            try {
                setWeatherData();
            } catch (InterruptedException | JSONException | ParseException e) {
                e.printStackTrace();
            }
        });

        /**
         * onClick for resetButton
         */
        reset.setOnClickListener(view -> {
            locationSearch.setText("");
        });

        /**
         * onChange for locationSearch
         */
        locationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
		    // Generate new list of places based on current search text
                    String[] placeList = places.load(locationSearch.getText().toString());
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, placeList);
                    locationSearch.setAdapter(adapter);
		    // Enable/Disable search and reset box if search is empty
		    if(locationSearch.getText().toString().matches("")) {
		       searchBtn.setEnabled(false);
		       reset.setEnabled(false);
		    }
		    else {
		       searchBtn.setEnabled(true);
		       reset.setEnabled(true);
	   	    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        /**
         * validateField
        
        locationSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(locationSearch.getText().toString().matches("")) {
                    searchBtn.setEnabled(false);
                    reset.setEnabled(false);
                }
                else {
                    searchBtn.setEnabled(true);
                    reset.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });
    }*/

    /**
     * setData -- waits until the weather object has been
     *            filled then sets the widgets to the new
     *            data
     * @throws InterruptedException
     * @throws JSONException
     * @throws ParseException
     */
    public void setWeatherData() throws InterruptedException, JSONException, ParseException {
        while(weather.isLoading()) {
            Thread.sleep(10);
        }
        temp.setText(weather.getCurrentTemp() + "°F");
        location.setText(weather.getLocation());
        country.setText(weather.getCountry());
        condition.setText(weather.getCondition());
        date.setText(weather.getDate());
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
