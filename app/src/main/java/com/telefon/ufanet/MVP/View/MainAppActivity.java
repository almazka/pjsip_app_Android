package com.telefon.ufanet.MVP.View;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.telefon.ufanet.ItemContacts;
import com.example.ufanet.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.telefon.ufanet.MVP.Data.AuthorizeData;
import com.telefon.ufanet.MVP.Interfaces.IMainAppActivity;
import com.telefon.ufanet.SoftPhoneFragment;


import java.util.ArrayList;
import java.util.Calendar;

public class MainAppActivity extends AppCompatActivity implements IMainAppActivity {

    // Variables and View Elements
    int for_anim = 0;
    int selected_id = 0;
    public static String token, name, sip_log, sip_pass;
    public static String[] date_from;
    public static String[] date_to;
    BottomNavigationView bottom_navigation;
    TextView action_bar_title;

    public static ArrayList<ItemContacts> contacts;
    Calendar calendar;
    int day, month, year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainApp_Activity", "MainApp OnCreate");
        init();
    }

    @Override
    public void init() {
        // Global Variables
        for_anim = 3;
        selected_id = 3;
        AuthorizeData userData = AuthorizeData.getInstance();
        token = userData.getUser_token();
        name = userData.getName();
        sip_log = userData.getSip_user();
        sip_pass = userData.getSip_password();
        contacts = new ArrayList<>();
        date_from =  new String[1];
        date_to = new String[1];

        // ActionBar Settings
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar);
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#670094")));
        getSupportActionBar().setElevation(0);
        action_bar_title = (TextView) findViewById(R.id.action_bar_title);
        action_bar_title.setText("Набор номера");

        // BottomNavigation Settings
        bottom_navigation = findViewById(R.id.bottom_navigation_menu);
        bottom_navigation.getMenu().getItem(2).setChecked(true);

        // Default Fragment on Application load
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new SoftPhoneFragment()).commit();

        // Загрузка текущей даты для глобальной переменной
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
        month = month + 1;

        if (month < 10 && day < 10) {
            date_from[0] = year + "-0" + month + "-0" + day + " 00:00:00";
            date_to[0] = year + "-0" + month + "-0" + day + " 23:59:59";
        } else if (month < 10 && day > 9) {
            date_from[0] = year + "-0" + month + "-" + day + " 00:00:00";
            date_to[0] = year + "-0" + month + "-" + day + " 23:59:59";
        } else if (month > 9 && day < 10) {
            date_from[0] = year + "-" + month + "-0" + day + " 00:00:00";
            date_to[0] = year + "-" + month + "-0" + day + " 23:59:59";
        } else {
            date_from[0] = year + "-" + month + "-" + day + " 00:00:00";
            date_to[0] = year + "-" + month + "-" + day + " 23:59:59";
        }


    }
}
