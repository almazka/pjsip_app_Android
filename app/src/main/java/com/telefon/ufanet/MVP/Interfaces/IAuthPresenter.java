package com.telefon.ufanet.MVP.Interfaces;

import android.app.Activity;
import android.view.View;

import com.telefon.ufanet.MVP.View.AuthorizeActivity;

public interface IAuthPresenter {
    void attachView(AuthorizeActivity authorizeActivity);
    void Login();
    void detachView();
    void viewIsReady(Activity activity);
    void requestPermission();
    void hideKeyboard(View v);
}
