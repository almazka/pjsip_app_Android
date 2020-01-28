package com.telefon.ufanet;

import android.annotation.SuppressLint;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.telefon.ufanet.MVP.Data.AuthorizeData;

import org.pjsip.pjsua2.Endpoint;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class MainApp extends AppCompatActivity {

    // Variables and View Elements
    int for_anim = 0;
    int selected_id = 0;
    public static String token, name, sip_log, sip_pass;
    public static String[] global_date1;
    public static String[] global_date2;
    public static String vatsChecked, connectionChecked;
    public static AsyncTask task;
    public static String status_task;
    BottomNavigationView bottomnav;
    TextView action_bar_title;
    public static ArrayList<ItemCalls> callList;
    ContentValues cv;
    DBHelper dbhelper;
    public static SQLiteDatabase database;
    public static ArrayList<ItemContacts> contacts;
    public static ArrayList<String> array_names = new ArrayList<String>();
    public static ArrayList<String> array_phones = new ArrayList<String>();
    SharedPreferences sPref;
    Calendar mCirrentDate;
    int day, month, year;


    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Global Variables
        for_anim = 3;
        selected_id = 3;
        AuthorizeData userData = AuthorizeData.getInstance();
        token = userData.getUser_token();
        name = userData.getName();
        sip_log = userData.getSip_user();
        sip_pass = userData.getSip_password();
        contacts = new ArrayList<>();
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
        bottomnav.setOnNavigationItemSelectedListener(navListenner);
        bottomnav.getMenu().getItem(2).setChecked(true);

        // Default Fragment on Application load
        getSupportFragmentManager().beginTransaction().replace(R.id.content, new SoftPhoneFragment()).commit();

        loadText();


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

        ///////// Модуль подгрузки контактов
        task = new AsyncTask<Void, Integer, ArrayList<ItemContacts>>() {
            @Override
            protected ArrayList<ItemContacts> doInBackground(Void... voids) {
                publishProgress();
                status_task = "Running";
                contacts = getContactNames();
                return contacts;

            }
            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(ArrayList<ItemContacts> s) {
                status_task = "Finished";
            }
        }.execute();



        ////// Работа с базой данных SQLite
        dbhelper = new DBHelper(this);
        database = dbhelper.getWritableDatabase();
        cv = new ContentValues();
        Cursor c = database.query("StarContacts", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int numberColIndex = c.getColumnIndex("number");
            do {
                Log.d("DataBase", "ID = " + c.getInt(idColIndex) +
                        ", name = " + c.getString(nameColIndex) + ", number = " + c.getString(numberColIndex));
            } while (c.moveToNext());

        } else {
            Log.d("DataBase", "0 записпей в базе");
        }
        c.close();

       GetRecentCalls();
    }

    ///////// Процедура подгрузки контактов
    private ArrayList<ItemContacts> getContactNames() {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                int hasPhone = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhone > 0) {
                    Cursor cursorNumber = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "= ?",
                            new String[]{id}, null);
                    if (cursorNumber != null && cursorNumber.getCount() > 0) {
                        cursorNumber.moveToFirst();
                        String phone = cursorNumber.getString(cursorNumber.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        contacts.add(new ItemContacts(name, phone));
                        array_names.add(name);
                        array_phones.add(phone);
                        cursorNumber.close();
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return contacts;
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }



    private void loadText() {
        sPref = getPreferences(MODE_PRIVATE);
        vatsChecked = sPref.getString("vats_checked", "false");
        connectionChecked = sPref.getString("connection_checked", "false");


        if (Objects.equals(String.valueOf(MainApp.connectionChecked.length()), "4")) {
            try {
                Endpoint.instance().codecSetPriority("PCMA/8000", (short) 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Endpoint.instance().codecSetPriority("speex/16000", (short) 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Endpoint.instance().codecSetPriority("speex/8000", (short) 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Endpoint.instance().codecSetPriority("speex/32000", (short) 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Endpoint.instance().codecSetPriority("GSM/8000", (short) 0);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                Endpoint.instance().codecSetPriority("PCMU/8000", (short) 0);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        else if (Objects.equals(String.valueOf(MainApp.connectionChecked.length()), "5")) {
            try {
                Endpoint.instance().codecSetPriority("PCMA/8000", (short) 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Endpoint.instance().codecSetPriority("PCMU/8000", (short) 2);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListenner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment selectedFragment = null;
                    switch (item.getItemId()) {
                        case R.id.nav_recent:
                            action_bar_title.setText("Недавние вызовы");
                            selected_id = 1;
                            selectedFragment = new RecentFragment();
                            if (selected_id == for_anim) {
                            } else {
                                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_left_in, R.anim.anim_left_out).replace(R.id.content, selectedFragment, "detailFragment").commit();
                                for_anim = 1;
                            }
                            break;


                        case R.id.nav_contacts:
                            action_bar_title.setText("Контакты");
                            selected_id = 2;
                            selectedFragment = new ContactFragment();
                            if (selected_id > for_anim) {
                                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_right_in, R.anim.anim_right_out).replace(R.id.content, selectedFragment, "detailFragment").commit();
                            }
                            if (selected_id == for_anim) {
                            } else
                                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_left_in, R.anim.anim_left_out).replace(R.id.content, selectedFragment, "detailFragment").commit();
                            for_anim = 2;
                            break;


                        case R.id.nav_softphone:
                            action_bar_title.setText("Набор номера");
                            selected_id = 3;
                            selectedFragment = new SoftPhoneFragment();
                            if (selected_id > for_anim) {
                                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_right_in, R.anim.anim_right_out).replace(R.id.content, selectedFragment, "detailFragment").commit();
                            }
                            if (selected_id == for_anim) {
                            } else
                                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_left_in, R.anim.anim_left_out).replace(R.id.content, selectedFragment, "detailFragment").commit();
                            for_anim = 3;
                            break;


                        case R.id.nav_statistic:
                            action_bar_title.setText("Статистика");
                            selected_id = 4;
                            selectedFragment = new StatisticFragment();
                            if (selected_id > for_anim) {
                                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_right_in, R.anim.anim_right_out).replace(R.id.content, selectedFragment, "detailFragment").commit();
                            }
                            if (selected_id == for_anim) {
                            } else
                                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_left_in, R.anim.anim_left_out).replace(R.id.content, selectedFragment, "detailFragment").commit();
                            for_anim = 4;
                            break;


                        case R.id.nav_user:
                            action_bar_title.setText("Профиль");
                            selected_id = 5;
                            selectedFragment = new ProfileFragment();
                            if (selected_id > for_anim) {
                                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_right_in, R.anim.anim_right_out).replace(R.id.content, selectedFragment, "detailFragment").commit();
                            }
                            if (selected_id == for_anim) {
                            } else
                                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.anim_left_in, R.anim.anim_left_out).replace(R.id.content, selectedFragment, "detailFragment").commit();
                            for_anim = 5;
                            break;
                    }

                    return true;
                }
            };


    ///////// Модуль подгрузки вызовов
    @SuppressLint("StaticFieldLeak")
    public void GetRecentCalls() {
        callList = new ArrayList<ItemCalls>();
        Cursor c = database.query("RecentCalls", null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int nameColIndex = c.getColumnIndex("name");
            int numberColIndex = c.getColumnIndex("number");
            int type = c.getColumnIndex("type");
            int date = c.getColumnIndex("date");
            int duration = c.getColumnIndex("duration");
            do {
                callList.add(new ItemCalls(c.getString(numberColIndex),c.getString(type), c.getString(nameColIndex), c.getString(date) + " | " + c.getString(type)+ " | " + c.getString(duration)));
            } while (c.moveToNext());
        } else {
            Log.d("DataBase", "0 записпей в базе");
        }
        c.close();
        Handler handler11 = new Handler();
        handler11.postDelayed(new Runnable() {
            @Override
            public void run() {
            }
        }, 400);

    }

    // Класс создания базы данных SQLite
    class DBHelper extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, "MyDB", null, 3);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("create table StarContacts (id integer primary key autoincrement, name text, number text);");
            cv = new ContentValues();
            cv.put("name", "Уфанет");
            cv.put("number", "83472900405");
            db.insert("StarContacts", null, cv);
            db.execSQL("create table RecentCalls (id integer primary key autoincrement, name text, number text, type text, date text, duration text);");
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("create table RecentCalls (id integer primary key autoincrement, name text, number text, type text, date text, duration text);");
        }

    }

    // BottomNavigation Settings class
    static class BottomNavigationViewHelper {
        @SuppressLint("RestrictedApi")
        public static void disableShiftMode(BottomNavigationView view) {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) view.getChildAt(0);
            try {
                Field shiftingMode = menuView.getClass().getDeclaredField("mShiftingMode");
                shiftingMode.setAccessible(true);
                shiftingMode.setBoolean(menuView, false);
                shiftingMode.setAccessible(false);
                for (int i = 0; i < menuView.getChildCount(); i++) {
                    BottomNavigationItemView item = (BottomNavigationItemView) menuView.getChildAt(i);
                    //noinspection RestrictedApi
                    item.setPadding(0, 0, 0, 0);
                    // set once again checked value, so view will be updated
                    //noinspection RestrictedApi
                    item.setChecked(item.getItemData().isChecked());
                }
            } catch (NoSuchFieldException e) {
                Log.e("BNVHelper", "Unable to get shift mode field", e);
            } catch (IllegalAccessException e) {
                Log.e("BNVHelper", "Unable to change value of shift mode", e);
            }
        }
    }


    @Override
    public void onBackPressed() {
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
    }
}




