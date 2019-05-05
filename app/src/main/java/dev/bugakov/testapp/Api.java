package dev.bugakov.testapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

    //запросы к API

    @GET("questions")
    Call<StackApiResponse> getAnswers(@Query("page") int page, @Query("pagesize") int pagesize, @Query("fromdate") long fromdate, @Query("order") String order, @Query("sort") String sort, @Query("tagged") String tagged, @Query("site") String site);

    @GET("questions")
    Call<StackApiResponse> getAnswersMain(@Query("page") int page, @Query("pagesize") int pagesize, @Query("order") String order, @Query("sort") String sort, @Query("tagged") String tagged, @Query("site") String site);

}