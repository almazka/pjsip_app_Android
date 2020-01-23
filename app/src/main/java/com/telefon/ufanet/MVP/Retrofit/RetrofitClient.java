package com.telefon.ufanet.MVP.Retrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit = null;

    public static Retrofit getClient(){
        if (retrofit == null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("https://telefon.ufanet.ru/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            return  retrofit;
        }
        return retrofit;
    }
}
