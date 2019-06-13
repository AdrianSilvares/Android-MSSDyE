package com.asilvaresdmartinez.practicassdye.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asilvaresdmartinez.practicassdye.Modelos.FinalData.*;
import com.asilvaresdmartinez.practicassdye.R;
import com.asilvaresdmartinez.practicassdye.RestServices.ILampService;
import com.asilvaresdmartinez.practicassdye.RestServices.IRestService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PollutionActivity extends AppCompatActivity {

    private static final String LOG_TAG = "SSDyE";
    private static final String API_BASE_URL = "https://api.airvisual.com";
    private static final String LAMP_API_URL = "192.168.0.102";

    private IRestService apiService;
    private ILampService lampService;

    private TextView tvCity;
    private TextView tvAQI;
    private TextView tvMainPollutant;
    private TextView tvTemperature;
    private ImageView iconWeather;
    private ImageView iconAQI;

    private View line1;
    private View line2;

    ArrayList<String> geolocation = new ArrayList<>();

    private static final String API_KEY = "YOUR_API_KEY_HERE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pollution);

        Intent intentPrev = getIntent();
        geolocation = intentPrev.getExtras().getStringArrayList("geolocation");

        tvCity = findViewById(R.id.tvCity);
        tvAQI = findViewById(R.id.tvAQI);
        tvMainPollutant = findViewById(R.id.tvMainPollutant);
        tvTemperature = findViewById(R.id.tvTemperature);
        iconWeather = findViewById(R.id.weatherIcon);
        iconAQI = findViewById(R.id.iconAQI);

        line1 = findViewById(R.id.div_line);
        line2 = findViewById(R.id.div_line2);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(IRestService.class);

        obtenerInfoPollution();
    }

    private void obtenerInfoPollution() {
        tvCity.setText(geolocation.get(2));
        tvTemperature.setText("");
        tvAQI.setText("");
        tvMainPollutant.setText("");

        Log.i(LOG_TAG, "obtenerInfoPollution: Started");

        Call<AirData> call_async;
        call_async = apiService.getAirData_city(geolocation.get(2), geolocation.get(1), geolocation.get(0), API_KEY);

        call_async.enqueue(new Callback<AirData>() {
            @Override
            public void onResponse(Call<AirData> call, Response<AirData> response) {
                int statusCode = response.code();
                AirData airData = response.body();

                if (null != airData) {

                    if(airData.getData().getCurrent().getWeather().getTp() != null)
                        tvTemperature.append(airData.getData().getCurrent().getWeather().getTp().toString() + "ºC");

                    if(airData.getData().getCurrent().getPollution().getAqius() != null)
                        tvAQI.append(airData.getData().getCurrent().getPollution().getAqius().toString());

                    if(airData.getData().getCurrent().getPollution().getMainus() != null){
                        String main_pollutant_code = airData.getData().getCurrent().getPollution().getMainus();
                        String main_pollutant = getMainPollutantByCode(main_pollutant_code);
                        tvMainPollutant.append(main_pollutant);
                    }

                    if(airData.getData().getCurrent().getWeather().getIc() != null){
                        String weather_code = airData.getData().getCurrent().getWeather().getIc();
                        int ic_weather = getWeatherIcon(weather_code);
                        iconWeather.setImageResource(ic_weather);
                    }


                    if(airData.getData().getCurrent().getPollution().getAqius() != null){
                        int aqi = airData.getData().getCurrent().getPollution().getAqius();
                        int ic_aqi = getAqiLevelIcon(aqi);
                        iconAQI.setImageResource(ic_aqi);
                    }

                    Log.i(LOG_TAG, "obtenerInfoPollution: Success");
                } else {
                    tvAQI.setText("ERROR");
                    Log.e(LOG_TAG, "obtenerInfoPollution: null");
                    Toast.makeText(
                            getApplicationContext(),
                            "No data for this city",
                            Toast.LENGTH_LONG
                    ).show();
                    goBack();
                }

            }

            @Override
            public void onFailure(Call<AirData> call, Throwable t) {
                Log.e(LOG_TAG, "obtenerInfoPollution: error. \n" + t.getMessage());
                Toast.makeText(
                        getApplicationContext(),
                        "ERROR: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
                goBack();
            }
        });
    }

    public void goBack(){
        final Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("geolocation", geolocation);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    public void pressBack(View v) {
        goBack();
    }

    private String getMainPollutantByCode(String main_pollutant_code) {
        String main_pollutant = "";

        switch (main_pollutant_code) {
            case "p2":
                main_pollutant = "pm2.5";
                break;
            case "p1":
                main_pollutant = "pm10";
                break;
            case "o3":
                main_pollutant = "Ozone O3";
                break;
            case "n2":
                main_pollutant = "Nitrogen dioxide NO2";
                break;
            case "s2":
                main_pollutant = "Sulfur dioxide SO2";
                break;
            case "co":
                main_pollutant = "Carbon monoxide CO";
                break;
            default:
                break;
        }

        return main_pollutant;
    }

    private int getWeatherIcon(String weather_code) {
        int ic_weather = -1;

        switch (weather_code) {
            case "01d":
                ic_weather = R.drawable.icon01d;
                break;
            case "01n":
                ic_weather = R.drawable.icon01n;
                break;
            case "02d":
                ic_weather = R.drawable.icon02d;
                break;
            case "02n":
                ic_weather = R.drawable.icon02n;
                break;
            case "03d":
                ic_weather = R.drawable.icon03d;
                break;
            case "04d":
                ic_weather = R.drawable.icon04d;
                break;
            case "09d":
                ic_weather = R.drawable.icon09d;
                break;
            case "10d":
                ic_weather = R.drawable.icon10d;
                break;
            case "10n":
                ic_weather = R.drawable.icon10n;
                break;
            case "11d":
                ic_weather = R.drawable.icon11d;
                break;
            case "13d":
                ic_weather = R.drawable.icon13d;
                break;
            case "50d":
                ic_weather = R.drawable.icon50d;
                break;
            default:
                break;
        }

        return ic_weather;
    }

    private int getAqiLevelIcon(int aqi) {
        int ic_aqi;
        String color = "";

        if ((aqi >= 0) && (aqi <= 50)) {
            ic_aqi = R.drawable.ic_aqi_good;
            line1.setBackgroundColor(Color.parseColor("#4CAF50"));
            line2.setBackgroundColor(Color.parseColor("#4CAF50"));
            color = "{\"r\":76, \"g\":175,\"b\":80}";

        } else if ((aqi >= 51) && (aqi <= 100)) {
            ic_aqi = R.drawable.ic_aqi_moderate;
            line1.setBackgroundColor(Color.parseColor("#FFEB3B"));
            line2.setBackgroundColor(Color.parseColor("#FFEB3B"));
            color = "{\"r\":255, \"g\":235,\"b\":59}";

        } else if ((aqi >= 101) && (aqi <= 150)) {
            ic_aqi = R.drawable.ic_aqi_unhealthy_for_sensitive_groups;
            line1.setBackgroundColor(Color.parseColor("#FF9800"));
            line2.setBackgroundColor(Color.parseColor("#FF9800"));
            color = "{\"r\":255, \"g\":152,\"b\":0}";

        } else if ((aqi >= 151) && (aqi <= 200)) {
            ic_aqi = R.drawable.ic_aqi_unhealthy;
            line1.setBackgroundColor(Color.parseColor("#E92315"));
            line2.setBackgroundColor(Color.parseColor("#E92315"));
            color = "{\"r\":233, \"g\":35,\"b\":21}";

        } else if ((aqi >= 201) && (aqi <= 300)) {
            ic_aqi = R.drawable.ic_aqi_very_unhealthy;
            line1.setBackgroundColor(Color.parseColor("#B31ACD"));
            line2.setBackgroundColor(Color.parseColor("#B31ACD"));
            color = "{\"r\":179, \"g\":26,\"b\":205}";

        } else if ((aqi >= 301) && (aqi <= 500)) {
            ic_aqi = R.drawable.ic_aqi_dangerous;
            line1.setBackgroundColor(Color.parseColor("#7E0023"));
            line2.setBackgroundColor(Color.parseColor("#7E0023"));
            color = "{\"r\":126, \"g\":0,\"b\":35}";

        }
        else ic_aqi = -1;

        if(color != "")
            turnOnLamp(color);

        return ic_aqi;
    }

    public void turnOnLamp(String color){
        try{
            Retrofit retrofit_lamp = new Retrofit.Builder()
                    .baseUrl(LAMP_API_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            lampService = retrofit_lamp.create(ILampService.class);
            Call<String> call_async;
            call_async = lampService.putColor(color);

            call_async.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    int statusCode = response.code();
                    Log.i(LOG_TAG, "turnOnLamp: Success");
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Log.e(LOG_TAG, "turnOnLamp: error. \n" + t.getMessage());
                }
            });
        }
        catch (IllegalArgumentException e){
            Log.e(LOG_TAG, "URL invalid");
            Toast.makeText(
                    getApplicationContext(),
                    "URL invalid",
                    Toast.LENGTH_LONG
            ).show();
        }
    }
}
