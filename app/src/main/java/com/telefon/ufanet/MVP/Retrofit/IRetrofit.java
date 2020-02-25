package com.telefon.ufanet.MVP.Retrofit;


import com.telefon.ufanet.MVP.Data.Sip;
import com.telefon.ufanet.MVP.Data.Token;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface IRetrofit {

    @POST("/api/Token")
    @FormUrlEncoded
    Call<Token> getaccess(@Field("username") String username,
                          @Field("password") String secret,
                          @Field("grant_type") String grant_type);

    @GET("/api/Users/Get")
    @Headers("Content-type: application/json")
    Call<Sip> getSip(
            @Header("Authorization") String token,
            @Query(value = "username", encoded = true) String username);


    @POST("/api/Token")
    @FormUrlEncoded
    Observable<Token> getaccess1(@Field("username") String username,
                                 @Field("password") String secret,
                                 @Field("grant_type") String grant_type);

    @GET("/api/Users/Get/")
    @Headers("Content-type: application/json")
    Observable<Sip> getSip1(
            @Header("Authorization") String token,
            @Query(value = "username", encoded = true) String username);

}
