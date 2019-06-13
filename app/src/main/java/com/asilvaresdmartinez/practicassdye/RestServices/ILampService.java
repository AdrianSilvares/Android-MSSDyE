package com.asilvaresdmartinez.practicassdye.RestServices;

import retrofit2.Call;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ILampService {
    @PUT("/ring/color/{color}")
    Call<String> putColor(@Path("color") String color);
}
