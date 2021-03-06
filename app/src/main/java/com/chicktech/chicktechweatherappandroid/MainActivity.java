package com.chicktech.chicktechweatherappandroid;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String API_KEY = "85709eec804fd0f1065b210fa3dc2548";

    private static final String URL = "https://api.forecast.io/forecast/%s/%f,%f";

    private double latitude = 45.5200;

    private double longitude = -122.6819;

    private ImageView mIconImageView;

    private TextView mTemperatureTextView;

    private TextView mLocationTextView;

    private TextView mHumidityTextView;

    private TextView mPrecipitationTextView;

    private TextView mSummaryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mIconImageView = (ImageView) findViewById(R.id.icon);
        mTemperatureTextView = (TextView) findViewById(R.id.temperature);
        mLocationTextView = (TextView) findViewById(R.id.location);
        mHumidityTextView = (TextView) findViewById(R.id.humidity);
        mPrecipitationTextView = (TextView) findViewById(R.id.precip_chance);
        mSummaryTextView = (TextView) findViewById(R.id.summary);


        String forecastURL = String.format(URL, API_KEY, latitude, longitude);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(forecastURL).build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to get weather data", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                String body = response.body().string();

                Log.d(TAG, "onResponse: " + body);

                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(body);
                        final CurrentWeather currentWeather = new CurrentWeather(json);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                populateDate(currentWeather);
                            }
                        });
                    } catch (JSONException e) {
                        Log.e(TAG, "error parsing weather data", e);
                    }
                }
            }
        });

    }

    private void populateDate(CurrentWeather currentWeather) {
        mIconImageView.setImageResource(currentWeather.getIconResId());
        mTemperatureTextView.setText(currentWeather.getFormattedTemperature());
        mHumidityTextView.setText(currentWeather.getFormattedHumidity());
        mPrecipitationTextView.setText(currentWeather.getFormattedPrecipChance());
        mSummaryTextView.setText(currentWeather.getSummary());
    }
}
