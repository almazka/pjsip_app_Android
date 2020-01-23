package com.telefon.ufanet.MVP.View;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ufanet.myapplication.ItemContacts;
import com.example.ufanet.myapplication.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.telefon.ufanet.MVP.Interfaces.IMainAppActivity;
import com.telefon.ufanet.SoftPhoneFragment;

import java.util.ArrayList;
import java.util.Calendar;

public class MainAppActivity extends AppCompatActivity implements IMainAppActivity {

    // Variables and View Elements
    int for_anim = 0;
    int selected_id = 0;
    public static String token, name, sip_log, sip_pass;
    public static String[] global_date1;
    public static String[] global_date2;
    BottomNavigationView bottomnav;
    TextView action_bar_title;

    public static ArrayList<ItemContacts> contacts;
    Calendar mCirrentDate;
    int day, month, year;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("Tag", "MainApp OnCreate");
        init();
    }

    @Override
    public void init() {
        // Global Variables
        for_anim = 3;
        selected_id = 3;
        //AuthorizeData userData = AuthorizeData.getInstance();
        //token = userData.getUser_token();
       // name = userData.getName();
       // sip_log = userData.getSip_user();
       // sip_pass = userData.getSip_password();
       // contacts = new ArrayList<>();
        global_date1 =  new String[1];
        global_date2 = new String[1];

        // ActionBar Settings
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.actionbar);
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#670094")));
        getSupportActionBar().setElevation(0);
        action_bar_title = (TextView) findViewById(R.id.action_bar_title);
        action_bar_title.setText("Набор номера");

        // BottomNavigation Settings
        bottomnav = findViewById(R.id.bottom_navigation);
        bottomnav.getMenu().getItem(2).setChecked(true);

        // Default Fragment on Application load
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new SoftPhoneFragment()).commit();

        // Загрузка текущей даты для глобальной переменной
        mCirrentDate = Calendar.getInstance();
        day = mCirrentDate.get(Calendar.DAY_OF_MONTH);
        month = mCirrentDate.get(Calendar.MONTH);
        year = mCirrentDate.get(Calendar.YEAR);
        month = month + 1;

        if (month < 10 && day < 10) {
            global_date1[0] = year + "-0" + month + "-0" + day + " 00:00:00";
            global_date2[0] = year + "-0" + month + "-0" + day + " 23:59:59";
        } else if (month < 10 && day > 9) {
            global_date1[0] = year + "-0" + month + "-" + day + " 00:00:00";
            global_date2[0] = year + "-0" + month + "-" + day + " 23:59:59";
        } else if (month > 9 && day < 10) {
            global_date1[0] = year + "-" + month + "-0" + day + " 00:00:00";
            global_date2[0] = year + "-" + month + "-0" + day + " 23:59:59";
        } else {
            global_date1[0] = year + "-" + month + "-" + day + " 00:00:00";
            global_date2[0] = year + "-" + month + "-" + day + " 23:59:59";
        }


    }
}
