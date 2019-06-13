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
import com.asilvaresdmartinez.practicassdye.Modelos.GetStates.Datum;
import com.asilvaresdmartinez.practicassdye.Modelos.GetStates.StatesData;
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

public class GetStatesActivity extends AppCompatActivity {

    private static final String API_BASE_URL = "https://api.airvisual.com";

    private static final String API_KEY = "YOUR_API_KEY_HERE";

    private IRestService apiService;

    private static final String LOG_TAG = "SSDyE";

    private List<HashMap<String, String>> datos = new ArrayList<>();
    private SimpleAdapter sa;

    List<Datum> state_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_states);

        Intent intentPrev = getIntent();
        final ArrayList<String> geolocation = intentPrev.getExtras().getStringArrayList("geolocation");

        final ListView listViewStates = findViewById(R.id.state_list_layout);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(IRestService.class);

        Call<StatesData> call_async;

        call_async = apiService.getStates(geolocation.get(0), API_KEY);
        Log.e(LOG_TAG, geolocation.get(0));

        call_async.enqueue(new Callback<StatesData>() {
            @Override
            public void onResponse(Call<StatesData> call, Response<StatesData> response) {
                int statusCode = response.code();
                final StatesData statesData = response.body();

                if (null != statesData) {
                    Log.i(LOG_TAG, "Getting data for states");
                    state_data = statesData.getData();

                    for (int i = 0; i < state_data.size(); i++) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("nombre", state_data.get(i).getState());
                        datos.add(hashMap);
                    }

                    sa = new MyAdapter(
                            getApplicationContext(),
                            datos,
                            R.layout.item_state,
                            new String[]{"nombre"},
                            new int[]{R.id.state_name_layout}
                    );

                    listViewStates.setAdapter(sa);

                    listViewStates.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                            Toast.makeText(getApplicationContext(), state_data.get(i).getState(), Toast.LENGTH_LONG).show();//show the selected image in toast according to position

                            String country = geolocation.get(0);
                            geolocation.clear();
                            geolocation.add(country);
                            geolocation.add(state_data.get(i).getState());

                            Intent intent = new Intent(GetStatesActivity.this, GetCityActivity.class);
                            intent.putExtra("geolocation", geolocation);
                            startActivity(intent);
                        }
                    });

                } else {
                    Log.e(LOG_TAG, "No data for states");
                    Toast.makeText(
                            getApplicationContext(),
                            "No data for states",
                            Toast.LENGTH_LONG
                    ).show();

                    Intent intent = new Intent(GetStatesActivity.this, GetCountryActivity.class);
                    intent.putExtra("geolocation", geolocation);
                    startActivity(intent);
                }

            }

            @Override
            public void onFailure(Call<StatesData> call, Throwable t) {
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

