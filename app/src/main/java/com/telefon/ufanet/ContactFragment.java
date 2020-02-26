package com.telefon.ufanet;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.telefon.ufanet.MVP.VOIP.MyAccount;
import com.telefon.ufanet.MVP.VOIP.MyCall;
import com.telefon.ufanet.MVP.VOIP.MyService;
import com.example.ufanet.myapplication.R;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pjsip.pjsua2.CallOpParam;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class ContactFragment extends Fragment {

    Button button_star, button_private, button_worked;
    EditText et_search;
    SwipeMenuListView lv_star, lv_сontact, lv_workers;
    LinearLayout linear_contacts, linear_star, linear_workers, linear_not_found, linear_not_found_contact, linear_search;
    ProgressBar progress_load_contacts, progress_load_workers;
    ArrayList<ItemContactsWorkers> contactListWorker;
    ArrayList<ItemContacts> contactList, contactList_star;
    String name_to_context_menu, number_to_context_menu;
    AsyncTask<Void, Integer, ArrayList<ItemContacts>> task;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmen_contact, container, false);
    }

    @SuppressLint({"StaticFieldLeak", "ResourceAsColor", "WrongViewCast", "CutPasteId"})
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        button_star = view.findViewById(R.id.star_contacts);
        button_private = view.findViewById(R.id.private_contacts);
        button_worked = view.findViewById(R.id.worked_contacts);
        et_search = view.findViewById(R.id.et_search);
        linear_contacts = view.findViewById(R.id.linear_contact);
        linear_not_found = view.findViewById(R.id.linear_not_found);
        linear_star = view.findViewById(R.id.linear_star);
        linear_workers = view.findViewById(R.id.linear_workers);
        lv_star = view.findViewById(R.id.list_star);
        lv_сontact = view.findViewById(R.id.list_contact);
        progress_load_contacts = view.findViewById(R.id.progress_load_contacts);
        linear_search = view.findViewById(R.id.layout_search);
        lv_workers = view.findViewById(R.id.list_workers);
        progress_load_workers = view.findViewById(R.id.progressBarWorkers);
        linear_not_found_contact = view.findViewById(R.id.linear_not_found_contact);


        button_star.setOnClickListener(button_star_listenner);
        button_worked.setOnClickListener(button_worked_listenner);
        lv_star.setOnMenuItemClickListener(list_star_itemmenu_listenner);
        lv_star.setOnItemClickListener(list_star_itemclick_listenner);
        button_private.setOnClickListener(button_private_listenner);
        lv_сontact.setOnItemClickListener(list_contact_itemclick_listenner);

        lv_workers.setOnItemClickListener(list_workers_itemclick_listenner);

        button_star.setBackgroundColor(getResources().getColor(R.color.background));
        button_worked.setBackgroundColor(getResources().getColor(R.color.background));
        button_private.setBackgroundResource(R.drawable.background_shadow_bottom);
        button_private.setTextColor(R.color.purple);
        button_worked.setTextColor(R.color.BLACK);
        button_star.setTextColor(R.color.BLACK);

        contactListWorker = new ArrayList<ItemContactsWorkers>();


        /////Создание свайп меню с одним вариантом "Удалить"
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {
                SwipeMenuItem openItem = new SwipeMenuItem(
                        getContext());
                openItem.setBackground(getActivity().getDrawable(R.color.red));
                openItem.setWidth(130);
                openItem.setTitle("Удалить");
                openItem.setTitleSize(15);
                openItem.setTitleColor(Color.WHITE);
                menu.addMenuItem(openItem);
            }
        };
        lv_star.setMenuCreator(creator);


        ///////// Модуль подгрузки контактов
        task = new AsyncTask<Void, Integer, ArrayList<ItemContacts>>() {
            @Override
            protected ArrayList<ItemContacts> doInBackground(Void... voids) {
                publishProgress();
                contactList = getContactNames();
                return contactList;

            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @Override
            protected void onPostExecute(ArrayList<ItemContacts> s) {
                progress_load_contacts.setVisibility(View.GONE);
                linear_search.setVisibility(View.VISIBLE);
                lv_сontact.setAdapter(new AdapterContacts(getContext(), contactList));
                registerForContextMenu(lv_сontact);
            }
        };

        if (MainApp.contacts.size() == 0) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
        else {
            contactList = MainApp.contacts;
            progress_load_contacts.setVisibility(View.GONE);
            linear_search.setVisibility(View.VISIBLE);
            lv_сontact.setAdapter(new AdapterContacts(getContext(), contactList));
            registerForContextMenu(lv_сontact);
        }


        ////// Процедура поиска контактов
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                update();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void update() {

        contactList = new ArrayList<ItemContacts>();

        Log.d ("size", String.valueOf(MainApp.array_names.size()));
        for (int i = 0; i < MainApp.array_names.size(); i++) {
            if (MainApp.array_names.get(i).toString().contains(et_search.getText().toString())) {
                contactList.add(new ItemContacts(MainApp.array_names.get(i).toString(), MainApp.array_phones.get(i).toString()));
            }
        }
        lv_сontact.setAdapter(new AdapterContacts(getContext(), contactList));

        if (contactList.size() != 0) {
            linear_not_found_contact.setVisibility(View.GONE);
            lv_сontact.setVisibility(View.VISIBLE);
        }
        else {
                lv_сontact.setVisibility(View.GONE);
                linear_not_found_contact.setVisibility(View.VISIBLE);
            }
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list_contact) {
            menu.add("Добавить в избранное");
            int c = ((AdapterView.AdapterContextMenuInfo) menuInfo).position;
            name_to_context_menu = contactList.get(c).getHeader();
            number_to_context_menu = contactList.get(c).getSubHeader();
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
                    ContentValues cv = new ContentValues();
                    cv.put("name", name_to_context_menu);
                    cv.put("number", number_to_context_menu);
                    MainApp.database.insert("StarContacts", null, cv);
                    Toast.makeText(getContext(), "Добавлено", Toast.LENGTH_SHORT).show();

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (task.getStatus() == AsyncTask.Status.FINISHED ) {
        }
        else {
            task.cancel(true);
        }
    }

    ///////// Процедура нажатия на элемент списка личных контактов
    private AdapterView.OnItemClickListener list_contact_itemclick_listenner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            Handler handler1 = new Handler();
            handler1.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String contact_number = contactList.get(position).getSubHeader().toString();
                    contact_number = contact_number.replace("+7", "8");
                    contact_number = contact_number.replaceAll("[^0-9]", "");
                    String call_contact_name = contactList.get(position).getHeader().toString();
                    if (MyService.msg_str.length() == 0) {
                        Toast.makeText(getContext(), "Необходима SIP регистрация", Toast.LENGTH_LONG).show();
                    } else if (MyService.msg_str.length() == 23) {

                        if (contact_number.length() == 0) {
                            Toast.makeText(getContext(), "Введите номер телефона", Toast.LENGTH_LONG).show();
                        } else {
                            MyAccount account = MyService.account;
                            MyCall call = new MyCall(account, -1);
                            CallOpParam prm = new CallOpParam(false);
                            try {
                                switch (MainApp.vatsChecked.length()) {
                                    case 4:  call.makeCall("sip:9" + contact_number.toString() + "@92.50.152.146:5401", prm);
                                        break;
                                    case 5:  call.makeCall("sip:" + contact_number.toString() + "@92.50.152.146:5401", prm);
                                        break;
                                }

                            } catch (Exception e) {
                                call.delete();
                                return;
                            }
                            MyService.currentCall = call;
                            showCallActivity();
                        }
                    } else {
                        Toast.makeText(getContext(), MyService.msg_str, Toast.LENGTH_LONG).show();
                    }

                }
            }, 5);
        }
    };

    ///////// Процедура подгрузки контактов
    private ArrayList<ItemContacts> getContactNames() {
        ContentResolver cr = getActivity().getContentResolver();
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
                        MainApp.contacts.add(new ItemContacts(name, phone));
                        MainApp.array_names.add(name);
                        MainApp.array_phones.add(phone);
                        cursorNumber.close();
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return MainApp.contacts;
    }

    /////  Процедура отображения личных контактов
    private View.OnClickListener button_private_listenner = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            linear_star.setVisibility(View.GONE);
            linear_workers.setVisibility(View.GONE);
            linear_not_found.setVisibility(View.GONE);
            button_star.setBackgroundColor(getResources().getColor(R.color.background));
            button_worked.setBackgroundColor(getResources().getColor(R.color.background));
            button_private.setBackgroundResource(R.drawable.background_shadow_bottom);
            button_private.setTextColor(R.color.purple);
            button_worked.setTextColor(R.color.BLACK);
            button_star.setTextColor(R.color.BLACK);
            linear_contacts.setVisibility(View.VISIBLE);
        }
    };


    ///// Процедура нажатия на элемент списка рабочие контакты
     private  AdapterView.OnItemClickListener list_workers_itemclick_listenner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String contact_number_worker = contactListWorker.get(position).getnum().toString();
                    contact_number_worker = contact_number_worker.replace("+7", "8");
                    contact_number_worker = contact_number_worker.replaceAll("[^0-9]", "");
                    String call_contact_name = contactListWorker.get(position).getHeader().toString();

                    if (MyService.msg_str.length() == 0) {
                        Toast.makeText(getContext(), "Необходима SIP регистрация", Toast.LENGTH_LONG).show();
                    } else if (MyService.msg_str.length() == 23) {

                        if (contact_number_worker.length() == 0) {
                            Toast.makeText(getContext(), "Введите номер телефона", Toast.LENGTH_LONG).show();
                        } else {
                            MyAccount account = MyService.account;
                            MyCall call = new MyCall(account, -1);
                            CallOpParam prm = new CallOpParam(false);
                            try {
                                call.makeCall("sip:" + contact_number_worker.toString() + "@92.50.152.146:5401", prm);
                            } catch (Exception e) {
                                call.delete();
                                return;
                            }
                            MyService.currentCall = call;
                            showCallActivity();
                        }

                    } else {
                        Toast.makeText(getContext(), MyService.msg_str, Toast.LENGTH_LONG).show();
                    }

                }
            }, 5);
        }
    };

    ///// Процедура свайпа слево элемента ListView для удаления
    private SwipeMenuListView.OnMenuItemClickListener list_star_itemmenu_listenner = new SwipeMenuListView.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
            switch (index) {
                case 0:
                    int c1 = position;
                    String name_to_context_menu = contactList_star.get(c1).getHeader();
                    String number_to_context_menu = contactList_star.get(c1).getSubHeader();
                    MainApp.database.delete("StarContacts", "name = '" + name_to_context_menu + "'", null);
                    Toast.makeText(getContext(), "Удалено", Toast.LENGTH_SHORT).show();
                    contactList_star = new ArrayList<ItemContacts>();

                    Cursor c = MainApp.database.query("StarContacts", null, null, null, null, null, null);

                    if (c.moveToFirst()) {
                        int idColIndex = c.getColumnIndex("id");
                        int nameColIndex = c.getColumnIndex("name");
                        int numberColIndex = c.getColumnIndex("number");
                        do {
                            contactList_star.add(new ItemContacts(c.getString(nameColIndex), c.getString(numberColIndex)));
                        } while (c.moveToNext());

                    } else {
                        linear_star.setVisibility(View.GONE);
                        linear_not_found.setVisibility(View.VISIBLE);
                        Log.d("DataBase", "0 записпей в базе");
                    }
                    c.close();
                    lv_star.setAdapter(new AdapterContactsStar(getContext(), contactList_star));
                    break;
            }
            return false;
        }
    };

    //// Загрузка избранных контактов из базы SQLite
    private View.OnClickListener button_star_listenner = new View.OnClickListener() {
        @SuppressLint("ResourceAsColor")
        @Override
        public void onClick(View view) {
            button_private.setBackgroundColor(getResources().getColor(R.color.background));
            button_worked.setBackgroundColor(getResources().getColor(R.color.background));
            button_star.setBackgroundResource(R.drawable.background_shadow_bottom);
            button_star.setTextColor(R.color.purple);
            button_private.setTextColor(R.color.BLACK);
            button_worked.setTextColor(R.color.BLACK);
            contactList_star = new ArrayList<ItemContacts>();

            Cursor c = MainApp.database.query("StarContacts", null, null, null, null, null, null);

            if (c.moveToFirst()) {
                int nameColIndex = c.getColumnIndex("name");
                int numberColIndex = c.getColumnIndex("number");
                do {
                    contactList_star.add(new ItemContacts(c.getString(nameColIndex), c.getString(numberColIndex)));
                } while (c.moveToNext());
                linear_contacts.setVisibility(View.GONE);
                linear_not_found.setVisibility(View.GONE);
                linear_workers.setVisibility(View.GONE);
                linear_star.setVisibility(View.VISIBLE);
            } else {
                linear_contacts.setVisibility(View.GONE);
                linear_star.setVisibility(View.GONE);
                linear_workers.setVisibility(View.GONE);
                linear_not_found.setVisibility(View.VISIBLE);
                Log.d("DataBase", "0 записпей в базе");
            }
            c.close();
            Handler handler11 = new Handler();
            handler11.postDelayed(new Runnable() {
                @Override
                public void run() {
                    lv_star.setAdapter(new AdapterContactsStar(getContext(), contactList_star));
                }
            }, 400);
        }
    };

    /// Процедура нажатия на элемент списка избранных контактов
    private AdapterView.OnItemClickListener list_star_itemclick_listenner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            Handler handler2 = new Handler();
            handler2.postDelayed(new Runnable() {
                @Override
                public void run() {
                    String contact_number_star = contactList_star.get(position).getSubHeader().toString();
                    contact_number_star = contact_number_star.replace("+7", "8");
                    contact_number_star = contact_number_star.replaceAll("[^0-9]", "");
                    String call_contact_name = contactList_star.get(position).getHeader().toString();

                    if (MyService.msg_str.length() == 0) {
                        Toast.makeText(getContext(), "Необходима SIP регистрация", Toast.LENGTH_LONG).show();
                    } else if (MyService.msg_str.length() == 23) {
                        if (contact_number_star.length() == 0) {
                            Toast.makeText(getContext(), "Введите номер телефона", Toast.LENGTH_LONG).show();
                        } else {
                            MyAccount account = MyService.account;
                            MyCall call = new MyCall(account, -1);
                            CallOpParam prm = new CallOpParam(false);
                            try {
                                call.makeCall("sip:" + contact_number_star.toString() + "@92.50.152.146:5401", prm);
                            } catch (Exception e) {
                                call.delete();
                                return;
                            }
                            MyService.currentCall = call;
                            showCallActivity();
                        }

                    } else {
                        Toast.makeText(getContext(), MyService.msg_str, Toast.LENGTH_LONG).show();
                    }
                }
            }, 5);

        }
    };


    /// Загрузка рабочих контактов с API Telefon Ufanet
    private View.OnClickListener button_worked_listenner = new View.OnClickListener() {
        @SuppressLint({"StaticFieldLeak", "ResourceAsColor"})
        @Override
        public void onClick(View view) {
            button_private.setBackgroundColor(getResources().getColor(R.color.background));
            button_star.setBackgroundColor(getResources().getColor(R.color.background));
            button_worked.setBackgroundResource(R.drawable.background_shadow_bottom);
            button_star.setTextColor(R.color.BLACK);
            button_private.setTextColor(R.color.BLACK);
            button_worked.setTextColor(R.color.purple);
            linear_not_found.setVisibility(View.GONE);
            linear_star.setVisibility(View.GONE);
            linear_contacts.setVisibility(View.GONE);
            linear_workers.setVisibility(View.VISIBLE);
            MyService.GetContacts();
            progress_load_workers.setVisibility(View.VISIBLE);


            //////////// Модуль подгрузки контактов с портала
            new AsyncTask<Void, Integer, String>() {

                @Override
                protected String doInBackground(Void... voids) {
                    publishProgress();
                    return getServerResponse(MainApp.token);
                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    super.onProgressUpdate(values);
                }

                @Override
                protected void onPostExecute(String result) {
                    contactListWorker.clear();
                    try {
                        progress_load_workers.setVisibility(View.GONE);
                        JSONArray parentArray = new JSONArray(result);
                        for (int i = 0; i < parentArray.length(); i++) {
                            JSONObject parentObject = parentArray.getJSONObject(i);
                            String name = parentObject.getString("firstName");
                            String last_name = parentObject.getString("lastName");
                            String company = parentObject.getString("company");
                            String position = parentObject.getString("position");
                            String email = parentObject.getString("email");

                            JSONArray parentArrayFinal = parentObject.getJSONArray("phoneNumbers");
                            JSONObject parentObjectFinal = parentArrayFinal.getJSONObject(0);
                            String phone_num = parentObjectFinal.getString("phoneNumber");
                            contactListWorker.add(new ItemContactsWorkers(name + " " + last_name, company + ", " + position + ", " + email , company, phone_num));
                        }
                        if (contactListWorker.size() != 0) {
                            lv_workers.setAdapter(new AdapterContactsWorkers(getContext(), contactListWorker));
                            lv_workers.setVisibility(View.VISIBLE);
                        }
                        else {
                            linear_workers.setVisibility(View.GONE);
                            linear_not_found.setVisibility(View.VISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };

    public void showCallActivity() {
        Intent intent = new Intent(getContext(), CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", "main");
        startActivity(intent);
    }

    private String getServerResponse(String token) {
        HttpGet post = new HttpGet("https://telefon.ufanet.ru/api/Contacts");
        try {
            post.setHeader("Content-Type", "application/json");
            post.setHeader("Authorization", token);

            DefaultHttpClient client = new DefaultHttpClient();
            BasicResponseHandler handler = new BasicResponseHandler();

            String response = client.execute(post, handler);

            return response;
        } catch (UnsupportedEncodingException e) {
            Log.d("JWP", e.toString());
        } catch (ClientProtocolException e) {
            Log.d("JWP", e.toString());
        } catch (IOException e) {
            Log.d("JWP", e.toString());
        }
        return "";
    }
}


