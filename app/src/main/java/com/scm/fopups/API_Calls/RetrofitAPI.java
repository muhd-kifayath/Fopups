package com.scm.fopups.API_Calls;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RetrofitAPI {
    @POST("tasks")

        //on below line we are creating a method to post our data.
    Call<DataModel> createPost(@Body DataModel dataModal);
}
