package com.telefon.ufanet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Calendar;

public class DetatizationActivity extends AppCompatActivity {

    TextView action_bar_title;
    ImageView img_back;

    TextView tv, tv1, detail_count, tv_info;
    Dialog dialog, dialog2;
    DatePicker datePicker, datePicker2;
    Calendar today, today2;
    Spinner spinner;
    Spinner spinner_reason;
    Spinner spinner_type;
    Button submit, button_ok, button_ok2;
    int call_status, reason;
    ProgressBar progress_detail;
    String body, call_type, token;
    CardView cw_count;
    ImageView img_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detatization);

        // ActionBar Settings
        ActionBar bar = getSupportActionBar();
        bar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        bar.setCustomView(R.layout.action_bar_with_backbutton);
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#670094")));
        getSupportActionBar().setElevation(0);
        action_bar_title = (TextView) findViewById(R.id.action_bar_title1);
        img_back = (ImageView) findViewById(R.id.image_back);
        action_bar_title.setText("Детализация звонков");
        img_back.setOnClickListener(img_back_click_listenner);

        tv = (TextView) findViewById(R.id.firstdate);
        tv1 = (TextView) findViewById(R.id.seconddate);

        tv.setText(MainApp.global_date1[0]);
        tv1.setText(MainApp.global_date2[0]);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.date_dialog_layout);
        dialog2 = new Dialog(this);
        dialog2.setContentView(R.layout.date_dialog_layout);
        datePicker = dialog.findViewById(R.id.datePicker);
        button_ok = dialog.findViewById(R.id.button_ok);
        datePicker2 = dialog2.findViewById(R.id.datePicker);
        button_ok2 = dialog2.findViewById(R.id.button_ok);
        spinner = (Spinner) findViewById(R.id.status_call);
        spinner_reason = (Spinner) findViewById(R.id.sp1);
        spinner_type = (Spinner) findViewById(R.id.type_call);
        submit = (Button) findViewById(R.id.submit);
        progress_detail = (ProgressBar) findViewById(R.id.progress_detail);
        cw_count = (CardView) findViewById(R.id.count_detail);
        detail_count = (TextView) findViewById(R.id.detail_count);
        tv_info = (TextView) findViewById(R.id.text_info);
        img_info = (ImageView) findViewById(R.id.image_info);

        button_ok.setOnClickListener(button_ok_onclick_listenner);
        button_ok2.setOnClickListener(button_ok2_onclick_listenner);
        tv.setOnClickListener(first_onclick_listenner);
        tv1.setOnClickListener(second_onclick_listenner);

        token = MainApp.token;
        call_type = "any";
        call_status = 0;
        reason = 0;

        today = Calendar.getInstance();
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear + 1;
                        if (monthOfYear < 10 && dayOfMonth < 10) {
                            MainApp.global_date1[0] = year + "-0" + monthOfYear + "-0" + dayOfMonth + " 00:00:00";
                        } else if (monthOfYear < 10 && dayOfMonth > 9) {
                            MainApp.global_date1[0] = year + "-0" + monthOfYear + "-" + dayOfMonth + " 00:00:00";
                        } else if (monthOfYear > 9 && dayOfMonth < 10) {
                            MainApp.global_date1[0] = year + "-" + monthOfYear + "-0" + dayOfMonth + " 00:00:00";
                        } else
                            MainApp.global_date1[0] = year + "-" + monthOfYear + "-" + dayOfMonth + " 00:00:00";
                    }
                });


        today2 = Calendar.getInstance();
        datePicker2.init(today2.get(Calendar.YEAR), today2.get(Calendar.MONTH),
                today2.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        monthOfYear = monthOfYear + 1;
                        if (monthOfYear < 10 && dayOfMonth < 10) {
                            MainApp.global_date2[0] = year + "-0" + monthOfYear + "-0" + dayOfMonth + " 23:59:59";
                        } else if (monthOfYear < 10 && dayOfMonth > 9) {
                            MainApp.global_date2[0] = year + "-0" + monthOfYear + "-" + dayOfMonth + " 23:59:59";
                        } else if (monthOfYear > 9 && dayOfMonth < 10) {
                            MainApp.global_date2[0] = year + "-" + monthOfYear + "-0" + dayOfMonth + " 23:59:59";
                        } else
                            MainApp.global_date2[0] = year + "-" + monthOfYear + "-" + dayOfMonth + " 23:59:59";
                    }
                });


        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        call_status = 0;
                        break;
                    case 1:
                        call_status = 1;
                        break;
                    case 2:
                        call_status = 2;
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_reason.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:
                        reason = 0;
                        break;
                    case 1:
                        reason = 1;
                        break;
                    case 2:
                        reason = 2;
                        break;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        call_type = "any";
                        break;
                    case 1:
                        call_type = "inc";
                        break;
                    case 2:
                        call_type = "out";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        submit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {

                if (tv.length() < 5 || tv1.length() < 5) {
                    Toast.makeText(getApplicationContext(), "Пожалуйста, укажите период", Toast.LENGTH_LONG).show();
                } else {
                    img_info.setVisibility(View.GONE);
                    tv_info.setVisibility(View.GONE);
                    final String finalDataDetail = tv.getText().toString().substring(0, 10);
                    final String finalData2Detail = tv1.getText().toString().substring(0, 10);
                    progress_detail.setVisibility(View.VISIBLE);

                    new AsyncTask<Void, Integer, String>() {

                        @Override
                        protected String doInBackground(Void... voids) {
                            publishProgress();
                            return getNumberResponse("", token);

                        }

                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            super.onProgressUpdate(values);
                        }

                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        protected void onPostExecute(final String result) {
                            try {
                                final JSONArray array = new JSONArray(result);
                                body = result;
                                JSONObject object = array.getJSONObject(0);
                                final String number = object.getString("number");

                                new AsyncTask<Void, Integer, String>() {

                                    @Override
                                    protected String doInBackground(Void... voids) {
                                        publishProgress();
                                        return getDetail(body, token, finalDataDetail, finalData2Detail, number);

                                    }

                                    @Override
                                    protected void onProgressUpdate(Integer... values) {
                                        LinearLayout mainL = (LinearLayout) findViewById(R.id.content);
                                        mainL.removeAllViews();
                                        super.onProgressUpdate(values);
                                    }

                                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                    @Override
                                    protected void onPostExecute(final String result) {
                                        progress_detail.setVisibility(View.GONE);
                                        try {
                                            JSONObject jsonObject = new JSONObject(result);
                                            LinearLayout mainL = (LinearLayout) findViewById(R.id.content);
                                            String count = jsonObject.getString("count");
                                            detail_count.setText(count);
                                            cw_count.setVisibility(View.VISIBLE);

                                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                                            for (int i = 0; i < jsonArray.length(); i++) {

                                                JSONObject parentObject = jsonArray.getJSONObject(i);

                                                String src_num = parentObject.getString("src_num");
                                                String dst_num = parentObject.getString("dst_num");
                                                String date_start = parentObject.getString("date_start");
                                                String duration = parentObject.getString("duration");

                                                int textcolor = ContextCompat.getColor(DetatizationActivity.this, R.color.BLACK);


                                                // Создание элементов для отображения


                                                LinearLayout layout = new LinearLayout(DetatizationActivity.this);
                                                layout.setOrientation(LinearLayout.VERTICAL);
                                                layout.setLayoutParams(new ViewGroup.LayoutParams
                                                        (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                                layout.setBackground(getResources().getDrawable(R.drawable.et_search_gray));
                                                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                                layoutParams.setMargins(15, 15, 25, 5);
                                                layout.setLayoutParams(layoutParams);


                                                TextView num = new TextView(DetatizationActivity.this);
                                                num.setPadding(20, 5, 0, 0);
                                                num.setTextSize(15);
                                                num.setTextColor(textcolor);
                                                num.setTypeface(num.getTypeface(), Typeface.BOLD);
                                                num.setText("C номера: " + src_num);

                                                layout.addView(num);


                                                TextView dst = new TextView(DetatizationActivity.this);
                                                dst.setPadding(20, 5, 0, 0);
                                                dst.setTextSize(15);
                                                dst.setTypeface(dst.getTypeface(), Typeface.BOLD);
                                                dst.setText("На номер: " + dst_num);

                                                layout.addView(dst);


                                                String month = date_start.substring(5, 7);
                                                String day = date_start.substring(8, 10);

                                                switch (month) {
                                                    case "01":
                                                        month = "Января";
                                                        break;
                                                    case "02":
                                                        month = "Февраля";
                                                        break;
                                                    case "03":
                                                        month = "Марта";
                                                        break;
                                                    case "04":
                                                        month = "Апреля";
                                                        break;
                                                    case "05":
                                                        month = "Мая";
                                                        break;
                                                    case "06":
                                                        month = "Июня";
                                                        break;
                                                    case "07":
                                                        month = "Июля";
                                                        break;
                                                    case "08":
                                                        month = "Августа";
                                                        break;
                                                    case "09":
                                                        month = "Сентября";
                                                        break;
                                                    case "10":
                                                        month = "Октября";
                                                        break;
                                                    case "11":
                                                        month = "Ноября";
                                                        break;
                                                    case "12":
                                                        month = "Декабря";
                                                        break;
                                                }

                                                String hhmm = date_start.substring(11, 16);


                                                TextView data = new TextView(DetatizationActivity.this);
                                                data.setPadding(20, 5, 0, 0);
                                                data.setTextSize(15);
                                                data.setText("Дата: " + day + " " + month + " " + hhmm);

                                                layout.addView(data);

                                                int time = Integer.valueOf(duration);
                                                int minute = time / 60;
                                                int sec = time % 60;

                                                TextView dur = new TextView(DetatizationActivity.this);
                                                dur.setPadding(20, 5, 0, 10);
                                                dur.setTextSize(15);
                                                if (minute > 0) {
                                                    dur.setText("Длительность: " + minute + " мин " + sec + " сек");
                                                } else {
                                                    dur.setText("Длительность: " + sec + " сек");
                                                }

                                                layout.addView(dur);


                                                mainL.addView(layout);


                                            }


                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }

                                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                }
            }
        });
    }

    private String getNumberResponse(String json, String token) {
        HttpGet post = new HttpGet("https://telefon.ufanet.ru/api/Calls/GetNumbers");
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

        return "Что-то пошло не так";
    }

    private String getDetail(String json, String token, String date1, String date2, String num ) {


        HttpPost post = new HttpPost("https://telefon.ufanet.ru/api/Calls?date1="+date1+"%2000:00:00&date2="+date2+"%2023:59:59&search_num="+num+"&call_status="+call_status+ "&call_type="+ call_type+"&reason="+reason+ "&group_by_src=false");
        try {
            StringEntity entity = new StringEntity(json, "utf-8");
            entity.setContentEncoding("UTF-8");

            post.setEntity(entity);
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

        return "Что-то пошло не так";
    }

    private View.OnClickListener button_ok_onclick_listenner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String d = Arrays.toString(MainApp.global_date1);
            d = d.substring(1, d.length()-1);
            tv.setText(d);
            dialog.dismiss();
        }
    };

    private View.OnClickListener button_ok2_onclick_listenner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String d1 = Arrays.toString(MainApp.global_date2);
            d1 = d1.substring(1, d1.length()-1);
            tv1.setText(d1);
            dialog2.dismiss();
        }
    };

    private View.OnClickListener first_onclick_listenner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog.show();
        }
    };

    private View.OnClickListener second_onclick_listenner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog2.show();
        }
    };


    private View.OnClickListener img_back_click_listenner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            finish();
        }
    };
}
