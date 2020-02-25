package com.telefon.ufanet.MVP.Presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.telefon.ufanet.MVP.VOIP.Service;
import com.telefon.ufanet.MVP.Data.AuthorizeData;
import com.telefon.ufanet.MVP.Interfaces.IAuthPresenter;
import com.telefon.ufanet.MVP.Model.AuthorizeModel;
import com.telefon.ufanet.MVP.View.AuthorizeActivity;
import com.telefon.ufanet.MVP.Interfaces.IAuthActivity;
import com.telefon.ufanet.MVP.View.MainAppActivity;


public class AuthorizePresenter implements IAuthPresenter {
    private static String LOG_TAG = "AuthorizePresenter";
    private IAuthActivity view;
    private Activity activity;

    private final AuthorizeModel model;

    public AuthorizePresenter(AuthorizeModel model) {
        this.model = model;
    }

    @Override
    public void attachView(AuthorizeActivity authorizeActivity) {
        view = authorizeActivity;
    }

    @Override
    public void Login() {
        final AuthorizeData userData = view.getUserData();
        if (TextUtils.isEmpty(userData.getName()) || TextUtils.isEmpty(userData.getPassword())) {
            view.showToast( "Логин или пароль не могут быть пустыми");
            return;
        }
        if (userData.isChecked()) {
            model.SaveData(userData.getName(), userData.getPassword());
        }
        else {
            model.SaveData("", "");
        }
        view.showProgress();
        model.Authorize(userData.getName(), userData.getPassword(), new AuthorizeModel.CompleteCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(String type, String msg) {
                view.hideProgress();
                if (type == "Success") {
                    Intent serviceIntent = new Intent(activity.getApplicationContext(), Service.class);
                    serviceIntent.putExtra("sip_login", userData.getSip_user());
                    serviceIntent.putExtra("sip_pass", userData.getSip_password());
                    serviceIntent.putExtra("token", userData.getUser_token());
                    serviceIntent.putExtra("name", userData.getName());
                    activity.startService(serviceIntent);


                    Intent intent = new Intent(activity.getApplicationContext(), MainAppActivity.class);
                    activity.startActivity(intent);
                }
                else {
                    view.showToast(msg);
                }
            }
        });
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void viewIsReady(Activity activity) {
        this.activity = activity;
        view.setUserData(model.LoadData());
        Boolean connection = model.isOnline(activity);
        if (!connection) {
            view.showToast( "Отсутствует соединение с интернетом");
        }
        final int permissionStatus = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.READ_CONTACTS);
        final int permissionStatus2 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        final int permissionStatus3 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.CALL_PHONE);
        final int permissionStatus4 = ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionStatus != PackageManager.PERMISSION_GRANTED || permissionStatus2 != PackageManager.PERMISSION_GRANTED || permissionStatus3 != PackageManager.PERMISSION_GRANTED || permissionStatus4 != PackageManager.PERMISSION_GRANTED  ) {
            view.showExplanation("Соглашение", "Для работы приложения необходимо предоставить разрешения: \n 1) Доступ к контактам; \n 2) Управление телефонными вызовами; \n 3) Использование микрофона; \n 4) Доступ к файлам на устройстве");
        }
        Log.d(LOG_TAG, "viewIsReady");
    }

    @Override
    public void requestPermission() {
        ActivityCompat.requestPermissions(this.activity, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
    }

    @Override
    public void hideKeyboard(View v) {
        view.hideKeyboard(v);
    }
}
