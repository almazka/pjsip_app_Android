package com.telefon.ufanet.MVP.Model;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.telefon.ufanet.MVP.Data.AuthorizeData;
import com.telefon.ufanet.MVP.Data.Sip;
import com.telefon.ufanet.MVP.Data.Token;
import com.telefon.ufanet.MVP.Interfaces.IAuthModel;
import com.telefon.ufanet.MVP.Retrofit.IAPI;
import com.telefon.ufanet.MVP.Retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class AuthorizeModel extends MvpAppCompatActivity implements IAuthModel {

    private SharedPreferences sPref;
    private AuthorizeData userData = AuthorizeData.INSTANCE;

    @Override
    public void Authorize(final String username, String password, final CompleteCallback callback) {
        Retrofit retrofit = RetrofitClient.getClient();
        final IAPI api = retrofit.create(IAPI.class);
        Call<Token> call_usertoken = api.getaccess(username, password, "password");
        call_usertoken.enqueue(new Callback<Token>() {
            @Override
            public void onResponse(Call<Token> call, Response<Token> response) {
                Log.d("STATUSCODE", String.valueOf(response.code()));
                switch (response.code()) {
                    case 200 : {
                        String userToken = response.body().getToken_type() + " " + response.body().getAccess_token();
                        Log.d("USERTOKEN", userToken);
                        userData.setUser_token(userToken);
                        Call<Token> call_apitoken = api.getaccess("api_user", "gl8LQwNY89", "password");
                        call_apitoken.enqueue(new Callback<Token>() {
                            @Override
                            public void onResponse(Call<Token> call, Response<Token> response) {
                                switch (response.code()) {
                                    case 200: {
                                        String apiToken = response.body().getToken_type() + " " + response.body().getAccess_token();
                                        Log.d("APITOKEN", apiToken);
                                        userData.setApi_token(apiToken);
                                        Call<Sip> call_sip = api.getSip(apiToken, username);
                                        call_sip.enqueue(new Callback<Sip>() {
                                            @Override
                                            public void onResponse(Call<Sip> call, Response<Sip> response) {
                                                switch (response.code()) {
                                                    case 200: {
                                                        String sip_login = response.body().getSipLogin();
                                                        String sip_pass = response.body().getSipPass();
                                                        userData.setSip_user(sip_login);
                                                        userData.setSip_password(sip_pass);
                                                        Log.d("SIPLOGIN", sip_login);
                                                        Log.d("SIPPASSWORD", sip_pass);
                                                        callback.onComplete("Success","Авторизация успешна");
                                                        break;
                                                    }
                                                }
                                            }
                                            @Override
                                            public void onFailure(Call<Sip> call, Throwable t) {
                                                Log.d("TAG", "Failed");
                                                callback.onComplete( "Error","Error");

                                            }
                                        });
                                        break;
                                    }
                                }
                            }
                            @Override
                            public void onFailure(Call<Token> call, Throwable t) {
                                Log.d("TAG", "Failed");
                                callback.onComplete("Error" ,"Что-то пошло не так!");
                            }
                        });
                        break;
                    }
                    case 400 : {
                        Log.d("Error", "Неверное имя пользователя или пароль");
                        callback.onComplete( "Error","Неверное имя пользователя или пароль");
                        break;
                    }
                }
            }
            @Override
            public void onFailure(Call<Token> call, Throwable t) {
                Log.d("TAG", "Error");
                callback.onComplete("Error","Произошла ошибка");
            }
        });
    }

    @Override
    public boolean isOnline(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public AuthorizeData LoadData(Activity activity) {
        sPref = activity.getPreferences(Context.MODE_PRIVATE);
        userData.setName(sPref.getString("login",""));
        userData.setPassword(sPref.getString("password",""));
        return userData;
    }

    @Override
    public void SaveData(Activity activity, String login, String password) {
        sPref = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sPref.edit();
        editor.putString("login", login);
        editor.putString("password", password);
        editor.apply();
    }


    public interface CompleteCallback {
        void onComplete(String type, String result);
    }
}
