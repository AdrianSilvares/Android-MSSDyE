package com.asilvaresdmartinez.practicassdye.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.asilvaresdmartinez.practicassdye.Modelos.FinalData.AirData;
import com.asilvaresdmartinez.practicassdye.R;
import com.asilvaresdmartinez.practicassdye.RestServices.IRestService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements  OnMapReadyCallback{

    private static final String API_KEY = "YOUR_API_KEY_HERE";

    private static final String LOG_TAG = "SSDyE";

    private static final String API_BASE_URL = "https://api.airvisual.com";
    private IRestService apiService;

    ArrayList<String> geolocation = new ArrayList<>();
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intentPrev = getIntent();
        geolocation = intentPrev.getExtras().getStringArrayList("geolocation");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(IRestService.class);
        ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync(this);
    }

    public void obtenerInfoPollution(View v) {
        Intent intent = new Intent(this, PollutionActivity.class);
        intent.putExtra("geolocation", geolocation);
        startActivity(intent);
    }

    public void chooseLocation(View v){
        Intent intent = new Intent(this, GetCountryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() == 0;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Call<AirData> call_async;
        call_async = apiService.getAirData_city(geolocation.get(2), geolocation.get(1), geolocation.get(0), API_KEY);
        call_async.enqueue(new Callback<AirData>() {
            @Override
            public void onResponse(Call<AirData> call, Response<AirData> response) {
                int statusCode = response.code();
                AirData airData = response.body();

                if (null != airData) {

                    List<Double> coordinates = airData.getData().getLocation().getCoordinates();
                    LatLng location = new LatLng(coordinates.get(1), coordinates.get(0));

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));  //move camera to location
                    if (mMap != null) {
                        Marker marker = mMap.addMarker(new MarkerOptions().position(location));
                    }
                    Log.i(LOG_TAG, "setCoordinates: Success");
                } else {
                    Log.i(LOG_TAG, "setCoordinates: null");
                    Toast.makeText(
                        getApplicationContext(),
                        "No data for this city",
                        Toast.LENGTH_LONG
                    ).show();
                }

            }

            @Override
            public void onFailure(Call<AirData> call, Throwable t) {
                Log.e(LOG_TAG, "setCoordinates: error. \n" + t.getMessage());
                Toast.makeText(
                        getApplicationContext(),
                        "ERROR: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
