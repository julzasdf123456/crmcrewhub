package com.lopez.julz.crmcrewhub.api;

import com.lopez.julz.crmcrewhub.classes.Barangays;
import com.lopez.julz.crmcrewhub.classes.Login;
import com.lopez.julz.crmcrewhub.classes.Towns;
import com.lopez.julz.crmcrewhub.database.ServiceConnectionInspections;
import com.lopez.julz.crmcrewhub.database.ServiceConnections;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RequestPlaceHolder {
    @POST("login")
    Call<Login> login(@Body Login login);

    @GET("get-for-energization-data")
    Call<List<ServiceConnections>> getForEnergizationData();

    @GET("get-towns")
    Call<List<Towns>> getTowns();

    @GET("get-barangays")
    Call<List<Barangays>> getBarangays();

    @GET("get-inspections-for-energization-data")
    Call<List<ServiceConnectionInspections>> getInspectionsForEnergizationData();
}
