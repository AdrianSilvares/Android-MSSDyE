package com.asilvaresdmartinez.practicassdye.RestServices;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import com.asilvaresdmartinez.practicassdye.Modelos.FinalData.AirData;
import com.asilvaresdmartinez.practicassdye.Modelos.GetCities.CitiesData;
import com.asilvaresdmartinez.practicassdye.Modelos.GetCountries.CountriesData;
import com.asilvaresdmartinez.practicassdye.Modelos.GetStates.StatesData;

public interface IRestService {
    // Request method and URL specified in the annotation
    // Callback for the parsed response is the last parameter

    @GET("/v2/city")
    Call<AirData> getAirData_city(
            @Query("city") String city,
            @Query("state") String state,
            @Query("country") String country,
            @Query("key") String key
    );

    @GET("/v2/countries")
    Call<CountriesData> getCountries(@Query("key") String key);

    @GET("/v2/states")
    Call<StatesData> getStates(@Query("country") String country,
                               @Query("key") String key);

    @GET("/v2/cities")
    Call<CitiesData> getCities(@Query("state") String state,
                               @Query("country") String country,
                               @Query("key") String key);

    @GET("/v2/cities")
    Call<AirData> getFinalData(@Query("city") String city,
                                 @Query("state") String state,
                                 @Query("country") String country,
                                 @Query("key") String key);

}

