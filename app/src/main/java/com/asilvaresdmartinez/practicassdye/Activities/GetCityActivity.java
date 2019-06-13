package com.asilvaresdmartinez.practicassdye.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.asilvaresdmartinez.practicassdye.Adapters.MyAdapter;
import com.asilvaresdmartinez.practicassdye.Modelos.GetCities.CitiesData;
import com.asilvaresdmartinez.practicassdye.Modelos.GetCities.Datum;
import com.asilvaresdmartinez.practicassdye.R;
import com.asilvaresdmartinez.practicassdye.RestServices.IRestService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetCityActivity extends AppCompatActivity {

    private static final String API_BASE_URL = "https://api.airvisual.com";

    private static final String API_KEY = "YOUR_API_KEY_HERE";

    private IRestService apiService;

    private static final String LOG_TAG = "SSDyE";

    private List<HashMap<String, String>> datos = new ArrayList<>();
    private SimpleAdapter sa;

    List<Datum> city_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_city);

        Intent intentPrev = getIntent();
        final ArrayList<String> geolocation = intentPrev.getExtras().getStringArrayList("geolocation");

        final ListView listViewCities = findViewById(R.id.city_list_layout);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(IRestService.class);

        Call<CitiesData> call_async;

        call_async = apiService.getCities(geolocation.get(1), geolocation.get(0), API_KEY);

        call_async.enqueue(new Callback<CitiesData>() {
            @Override
            public void onResponse(Call<CitiesData> call, Response<CitiesData> response) {
                int statusCode = response.code();
                final CitiesData citiesData = response.body();

                if (null != citiesData) {
                    Log.i(LOG_TAG, "Getting data for cities");
                    city_data = citiesData.getData();

                    for (int i = 0; i < city_data.size(); i++) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("nombre", city_data.get(i).getCity());
                        datos.add(hashMap);
                    }

                    sa = new MyAdapter(
                            getApplicationContext(),
                            datos,
                            R.layout.item_city,
                            new String[]{"nombre"},
                            new int[]{R.id.city_name_layout}
                    );

                    listViewCities.setAdapter(sa);

                    listViewCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Toast.makeText(getApplicationContext(), city_data.get(i).getCity(), Toast.LENGTH_LONG).show();//show the selected image in toast according to position

                            String country = geolocation.get(0);
                            String state = geolocation.get(1);

                            geolocation.clear();

                            geolocation.add(country);
                            geolocation.add(state);
                            geolocation.add(city_data.get(i).getCity());

                            Intent intent = new Intent(GetCityActivity.this, MainActivity.class);
                            intent.putExtra("geolocation", geolocation);
                            startActivity(intent);
                        }
                    });

                } else {
                    Log.e(LOG_TAG, "No data for cities");
                    Toast.makeText(
                            getApplicationContext(),
                            "No data for cities",
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(GetCityActivity.this, GetStatesActivity.class);
                    intent.putExtra("geolocation", geolocation);
                    startActivity(intent);
                }

            }

            @Override
            public void onFailure(Call<CitiesData> call, Throwable t) {
                Log.i(LOG_TAG, "Error \n" + t.getMessage());
                Toast.makeText(
                        getApplicationContext(),
                        "ERROR: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
