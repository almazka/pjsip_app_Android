package com.telefon.ufanet.MVP.Model;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.arellomobile.mvp.MvpAppCompatActivity;
import com.telefon.ufanet.MVP.Data.AuthorizeData;
import com.telefon.ufanet.MVP.Data.PrefManager;
import com.telefon.ufanet.MVP.Data.Sip;
import com.telefon.ufanet.MVP.Interfaces.IAuthModel;
import com.telefon.ufanet.MVP.Retrofit.IRetrofit;
import com.telefon.ufanet.MVP.Retrofit.RetrofitClient;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class AuthorizeModel extends MvpAppCompatActivity implements IAuthModel {

    PrefManager prefManager = PrefManager.getInstance();
    private AuthorizeData userData = AuthorizeData.INSTANCE;

    @SuppressLint("CheckResult")
    @Override
    public void Authorize(final String username, String password, final CompleteCallback callback) {
        Retrofit retrofit = RetrofitClient.getClient();
        final IRetrofit api = retrofit.create(IRetrofit.class);
        api.getaccess1(username, password, "password")
                .subscribeOn(Schedulers.io())
                .flatMap(token -> {
                    userData.setUser_token(token.getToken_type()+ " "+ token.getAccess_token());
                    return api.getaccess1("api_user", "gl8LQwNY89", "password");
                })
                .subscribeOn(Schedulers.io())
                .flatMap(token1 -> {
                    userData.setApi_token(token1.getToken_type()+ " "+ token1.getAccess_token());
                    return api.getSip1(token1.getToken_type()+ " "+ token1.getAccess_token(), username);
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Sip>() {
                                   @Override
                                   public void onNext(Sip sip) {
                                       userData.setSip_user(sip.getSipLogin());
                                       userData.setSip_password(sip.getSipPass());
                                   }
                                   @Override
                                   public void onError(Throwable e) {
                                       callback.onComplete("Error", "Неверное имя пользователя или пароль");
                                   }
                                   @Override
                                   public void onComplete() {
                                       callback.onComplete("Success", "");
                                   }
                               }
                );
    }

    @Override
    public boolean isOnline(Activity activity) {
        ConnectivityManager cm =
                (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public AuthorizeData LoadData() {
        userData.setName(prefManager.getString("login"));
        userData.setPassword(prefManager.getString("password"));
        return userData;
    }

    @Override
    public void SaveData(String login, String password) {
        prefManager.saveString("login",login);
        prefManager.saveString("password", password);
    }


    public interface CompleteCallback {
        void onComplete(String type, String msg);
    }
}
