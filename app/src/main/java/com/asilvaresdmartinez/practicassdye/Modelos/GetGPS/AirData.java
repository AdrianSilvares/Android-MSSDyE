
package com.asilvaresdmartinez.practicassdye.Modelos.GetGPS;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AirData {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("data")
    @Expose
    private Data_ data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Data_ getData() {
        return data;
    }

    public void setData(Data_ data) {
        this.data = data;
    }

}
