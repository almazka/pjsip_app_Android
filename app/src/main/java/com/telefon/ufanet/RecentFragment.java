package com.telefon.ufanet;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import org.pjsip.pjsua2.CallOpParam;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class RecentFragment extends Fragment {

    public static ListView lv;
    LinearLayout linear_not_found_recent;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragmen_recent, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MainApp.callList = new ArrayList<ItemCalls>();
        Cursor c = MainApp.database.query("RecentCalls", null, null, null, null, null, null);

        if (c.moveToLast()) {
            int nameColIndex = c.getColumnIndex("name");
            int numberColIndex = c.getColumnIndex("number");
            int typeColIndex = c.getColumnIndex("type");
            int dateColIndex = c.getColumnIndex("date");
            int durationColIndex = c.getColumnIndex("duration");
            do {
                if (c.getString(typeColIndex).toString().contains("Исходящий")) {
                    MainApp.callList.add(new ItemCalls(c.getString(numberColIndex), "Исходящий", c.getString(nameColIndex), c.getString(typeColIndex) + " | " + c.getString(dateColIndex) + " | " + c.getString(durationColIndex) + "c" ));
                }
                else if (c.getString(typeColIndex).contains("Входящий")){
                    MainApp.callList.add(new ItemCalls(c.getString(numberColIndex), "Входящий", c.getString(nameColIndex), c.getString(typeColIndex) + " | " + c.getString(dateColIndex) + " | " + c.getString(durationColIndex) + "c" ));
                }
                else if (c.getString(typeColIndex).contains("Пропущенный")) {
                    MainApp.callList.add(new ItemCalls(c.getString(numberColIndex), "Пропущенный", c.getString(nameColIndex), c.getString(typeColIndex) + " | " + c.getString(dateColIndex) + " | " + c.getString(durationColIndex) + "c" ));
                }
                else {
                    MainApp.callList.add(new ItemCalls(c.getString(numberColIndex), "Исходящий", c.getString(nameColIndex), c.getString(typeColIndex) + " | " + c.getString(dateColIndex) + " | " + c.getString(durationColIndex) + "c" ));
                }
            } while (c.moveToPrevious());
        } else {
            Log.d("DataBase", "0 записпей в базе");
        }
        c.close();
        lv = view.findViewById(R.id.list_calls);
        lv.setAdapter(new AdapterCalls(getContext(), MainApp.callList));
        lv.setOnItemClickListener(list_view_listenner);
        linear_not_found_recent = view.findViewById(R.id.linear_not_found_recent);
        if (MainApp.callList.size() == 0) {

        }
        else {
            linear_not_found_recent.setVisibility(View.GONE);
            lv.setVisibility(View.VISIBLE);
        }


    }

    public ArrayList<ItemCalls> update(){
        MainApp.callList = new ArrayList<ItemCalls>();
        ContentResolver cr = getActivity().getContentResolver();
        @SuppressLint("MissingPermission")
        Cursor mCursor = cr.query(CallLog.Calls.CONTENT_URI, null, null,
                null, CallLog.Calls.DATE + " DESC");

        int number_calls = mCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int date_calls = mCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration_calls = mCursor.getColumnIndex(CallLog.Calls.DURATION);

        int type_calls = mCursor.getColumnIndex(CallLog.Calls.TYPE);
        while (mCursor.moveToNext()) {
            String phnumber = mCursor.getString(number_calls);
            String callduration = mCursor.getString(duration_calls);
            String calltype = mCursor.getString(type_calls);
            String calldate = mCursor.getString(date_calls);
            Date d = new Date(Long.valueOf(calldate));
            String callname = mCursor.getString(mCursor.getColumnIndex(CallLog.Calls.CACHED_NAME));
            Format formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String s = formatter.format(d);
            String callTypeStr = "";
            switch (Integer.parseInt(calltype)) {
                case CallLog.Calls.OUTGOING_TYPE:
                    callTypeStr = "Исходящий";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    callTypeStr = "Входящий";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    callTypeStr = "Пропущенный";
                    break;
            }
            MainApp.callList.add(new ItemCalls(phnumber, callTypeStr, callname, s + " | " + callTypeStr + " | " + callduration + "c"));
        }
        return MainApp.callList;
    }

    private AdapterView.OnItemClickListener list_view_listenner = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            String call_number = MainApp.callList.get(position).getHeader();
            call_number = call_number.replace("+7", "8");
            call_number = call_number.replaceAll("[^0-9]", "");

            if (MyService.msg_str.length() == 0) {
                Toast.makeText(getContext(), "Необходима SIP регистрация", Toast.LENGTH_LONG).show();
            } else if (MyService.msg_str.length() == 23) {

                if (call_number.length() == 0) {
                    Toast.makeText(getContext(), "Введите номер телефона", Toast.LENGTH_LONG).show();
                } else {
                    SoftPhoneFragment.account = MyService.account;
                    MyCall call = new MyCall(SoftPhoneFragment.account, -1);
                    CallOpParam prm = new CallOpParam(false);
                    try {
                        switch (MainApp.vatsChecked.length()) {
                            case 4:  call.makeCall("sip:9" + call_number.toString() + "@92.50.152.146:5401", prm);
                                break;
                            case 5:  call.makeCall("sip:" + call_number.toString() + "@92.50.152.146:5401", prm);
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
    };

    public void showCallActivity() {
        Intent intent = new Intent(getContext(), CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", "main");
        startActivity(intent);
    }

}
