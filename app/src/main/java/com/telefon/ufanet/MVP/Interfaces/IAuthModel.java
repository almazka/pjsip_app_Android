package com.telefon.ufanet.MVP.Interfaces;

import android.app.Activity;

import com.telefon.ufanet.MVP.Data.AuthorizeData;
import com.telefon.ufanet.MVP.Model.AuthorizeModel;

public interface IAuthModel {

    void Authorize(final String username, String password, final AuthorizeModel.CompleteCallback callback);
    boolean isOnline(Activity activity);
    AuthorizeData LoadData();
    void SaveData(String login, String password);
}
