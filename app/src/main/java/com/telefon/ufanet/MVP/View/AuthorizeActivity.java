package com.telefon.ufanet.MVP.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.telefon.ufanet.MVP.Data.AuthorizeData;
import com.telefon.ufanet.MVP.Data.PrefManager;
import com.telefon.ufanet.MVP.Interfaces.IAuthActivity;
import com.telefon.ufanet.MVP.Model.AuthorizeModel;
import com.telefon.ufanet.MVP.Presenter.AuthorizePresenter;
import com.telefon.ufanet.R;



public class AuthorizeActivity extends AppCompatActivity implements IAuthActivity {

    private static String LOG_TAG = "AuthorizeActivity";


    private EditText editTextUsername;
    private EditText editTextPassword;
    private ProgressBar progressbar;

    CheckBox checkBox;
    Button buttonLogin;

    RelativeLayout relative_logo, relative_info, relative_main;


    private AuthorizePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorize_activity);
        Log.d(LOG_TAG, "OnCreate");
        init();
    }

    @SuppressLint("CheckResult")
    @Override
    public void init() {
        androidx.appcompat.app.ActionBar bar = getSupportActionBar();
        bar.hide();
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        PrefManager manager = PrefManager.getInstance();
        manager.initPref(getApplication());
        editTextUsername = (EditText) findViewById(R.id.et_login);
        editTextPassword = (EditText) findViewById(R.id.et_pass);
        buttonLogin = (Button) findViewById(R.id.login_btn);
        relative_logo = (RelativeLayout) findViewById(R.id.relative_logo);
        relative_info = (RelativeLayout) findViewById(R.id.relative_info);
        relative_main = (RelativeLayout) findViewById(R.id.relative_main);
        checkBox = (CheckBox) findViewById(R.id.check);
        progressbar = (ProgressBar) findViewById(R.id.progressbar);

        Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                relative_logo.setVisibility(View.VISIBLE);
                relative_info.setVisibility(View.VISIBLE);
            }
        };
        handler.postDelayed(runnable, 1500);

        AuthorizeModel usersModel = new AuthorizeModel();
        presenter = new AuthorizePresenter(usersModel);
        presenter.attachView(this);
        presenter.viewIsReady(this);

        relative_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.hideKeyboard(v);
            }
        });
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.Login();
            }
        });

    }


    @Override
    public AuthorizeData getUserData() {
        AuthorizeData userData = AuthorizeData.getInstance();
        userData.setName(editTextUsername.getText().toString());
        userData.setPassword(editTextPassword.getText().toString());
        userData.setChecked(checkBox.isChecked());
        return userData;
    }

    @Override
    public void setUserData(AuthorizeData userData) {
        editTextUsername.setText(userData.getName());
        editTextPassword.setText(userData.getPassword());
    }

    @Override
    public void showProgress() {
        progressbar.setVisibility(View.VISIBLE);
        buttonLogin.setEnabled(false);
    }

    @Override
    public void hideProgress() {
        progressbar.setVisibility(View.INVISIBLE);
        buttonLogin.setEnabled(true);
    }

    @Override
    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showExplanation(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("принять", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        presenter.requestPermission();
                    }
                })
                .setNegativeButton("отказаться", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        builder.create().show();
    }

    @Override
    public void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }
}
