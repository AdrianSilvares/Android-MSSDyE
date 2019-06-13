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
import com.asilvaresdmartinez.practicassdye.Modelos.GetCountries.CountriesData;
import com.asilvaresdmartinez.practicassdye.Modelos.GetCountries.Datum;
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

public class GetCountryActivity extends AppCompatActivity {

    private static final String API_BASE_URL = "https://api.airvisual.com";

    private static final String API_KEY = "YOUR_API_KEY_HERE";

    private IRestService apiService;

    private static final String LOG_TAG = "SSDyE";

    private List<HashMap<String, String>> datos = new ArrayList<>();
    private SimpleAdapter sa;

    List<Datum> country_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_country);

        final ArrayList<String> geolocation = new ArrayList<>();

        final ListView listViewCountries = findViewById(R.id.country_list_layout);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(IRestService.class);

        Call<CountriesData> call_async;

        call_async = apiService.getCountries(API_KEY);

        call_async.enqueue(new Callback<CountriesData>() {
            @Override
            public void onResponse(Call<CountriesData> call, Response<CountriesData> response) {
                int statusCode = response.code();
                final CountriesData countriesData = response.body();

                if (null != countriesData) {
                    Log.i(LOG_TAG, "Getting data for countries");
                    country_data = countriesData.getData();

                    for (int i = 0; i < country_data.size(); i++) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("nombre", country_data.get(i).getCountry());
                        datos.add(hashMap);
                    }

                    sa = new MyAdapter(
                            getApplicationContext(),
                            datos,
                            R.layout.item_country,
                            new String[]{"nombre"},
                            new int[]{R.id.country_name_layout}
                    );

                    listViewCountries.setAdapter(sa);

                    listViewCountries.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Toast.makeText(getApplicationContext(), country_data.get(i).getCountry(), Toast.LENGTH_LONG).show();//show the selected image in toast according to position
                            geolocation.clear();
                            geolocation.add(country_data.get(i).getCountry());

                            Intent intent = new Intent(GetCountryActivity.this, GetStatesActivity.class);
                            intent.putExtra("geolocation", geolocation);
                            startActivity(intent);
                        }
                    });

                } else {
                    Log.e(LOG_TAG, "No data for countries");
                }

            }

            @Override
            public void onFailure(Call<CountriesData> call, Throwable t) {
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
