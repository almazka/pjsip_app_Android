package com.telefon.ufanet.MVP.Interfaces;

import android.view.View;


import com.telefon.ufanet.MVP.Data.AuthorizeData;

public interface IAuthActivity {
    AuthorizeData getUserData();
    void setUserData(AuthorizeData userData);
    void showProgress();
    void hideProgress();
    void showSuccessToast(String msg);
    void showErrorToast(String msg);
    void showInfoToast(String msg);
    void showExplanation(String title, String message);
    void hideKeyboard(View v);
    void init();

}
