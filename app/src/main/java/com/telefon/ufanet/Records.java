package com.telefon.ufanet;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ufanet.myapplication.R;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;

/**
 * Created by Sadretdinov_a1109 on 08.05.2018.
 */

public class Records extends Fragment {



    TextView tv, tv1;
    Calendar mCirrentDate;
    int day, month, year;
    Spinner spinner;
    Button submit_records;
    String status_call, tel;
    RelativeLayout rel_record;
    LinearLayout linear_noaction;
    String body;
    ProgressBar progressBar;
    Dialog dialog_progress;
    String token;

    public Records() {
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.record_fragment, null);


    }

    @SuppressLint({"SetTextI18n", "StaticFieldLeak"})
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        rel_record = view.findViewById(R.id.relative_records);
        rel_record.setVisibility(View.GONE);
        linear_noaction = view.findViewById(R.id.no_action);
        Bundle bundle = this.getArguments();
        token = bundle.getString("token");
        Log.d("Test", token);

        progressBar = view.findViewById(R.id.progress);


        tv = view.findViewById(R.id.firstdate_records);
        tv1 = view.findViewById(R.id.seconddate_records);
        mCirrentDate = Calendar.getInstance();
        day = mCirrentDate.get(Calendar.DAY_OF_MONTH);
        month = mCirrentDate.get(Calendar.MONTH);
        year = mCirrentDate.get(Calendar.YEAR);
        month = month + 1;
        final String[] date = new String[1];

        if (month < 10 && day < 10) {
            tv.setText(year + "-0" + month + "-0" + day + " 00:00:00");
            tv1.setText(year + "-0" + month + "-0" + day + " 23:59:59");
        } else if (month < 10 && day > 9) {
            tv.setText(year + "-0" + month + "-" + day + " 00:00:00");
            tv1.setText(year + "-0" + month + "-" + day + " 23:59:59");
        } else if (month > 9 && day < 10) {
            tv.setText(year + "-" + month + "-0" + day + " 00:00:00");
            tv1.setText(year + "-" + month + "-0" + day + " 23:59:59");
        } else {
            tv.setText(year + "-" + month + "-" + day + " 00:00:00");
            tv1.setText(year + "-" + month + "-" + day + " 23:59:59");
        }


        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.date_dialog_layout);
        final Dialog dialog1 = new Dialog(getActivity());
        dialog1.setContentView(R.layout.date_dialog_layout);


        final DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.dp);

        final Button button = (Button) dialog.findViewById(R.id.button_ok);
        final Calendar today = Calendar.getInstance();
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        monthOfYear = monthOfYear + 1;
                        if (monthOfYear < 10 && dayOfMonth < 10) {
                            date[0] = year + "-0" + monthOfYear + "-0" + dayOfMonth + " 00:00:00";
                        } else if (monthOfYear < 10 && dayOfMonth > 9) {
                            date[0] = year + "-0" + monthOfYear + "-" + dayOfMonth + " 00:00:00";
                        } else if (monthOfYear > 9 && dayOfMonth < 10) {
                            date[0] = year + "-" + monthOfYear + "-0" + dayOfMonth + " 00:00:00";
                        } else date[0] = year + "-" + monthOfYear + "-" + dayOfMonth + " 00:00:00";
                    }
                });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d = Arrays.toString(date);
                d = d.substring(1, d.length() - 1);
                tv.setText(d);
                dialog.dismiss();
            }
        });


        final DatePicker datePicker1 = (DatePicker) dialog1.findViewById(R.id.dp);

        final Button button1 = (Button) dialog1.findViewById(R.id.button_ok);
        Calendar today1 = Calendar.getInstance();
        datePicker1.init(today1.get(Calendar.YEAR), today1.get(Calendar.MONTH),
                today1.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        monthOfYear = monthOfYear + 1;
                        if (monthOfYear < 10 && dayOfMonth < 10) {
                            date[0] = year + "-0" + monthOfYear + "-0" + dayOfMonth + " 23:59:59";
                        } else if (monthOfYear < 10 && dayOfMonth > 9) {
                            date[0] = year + "-0" + monthOfYear + "-" + dayOfMonth + " 23:59:59";
                        } else if (monthOfYear > 9 && dayOfMonth < 10) {
                            date[0] = year + "-" + monthOfYear + "-0" + dayOfMonth + " 23:59:59";
                        } else date[0] = year + "-" + monthOfYear + "-" + dayOfMonth + " 23:59:59";
                    }
                });

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d1 = Arrays.toString(date);
                d1 = d1.substring(1, d1.length() - 1);
                tv1.setText(d1);
                dialog1.dismiss();
            }
        });


        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog1.show();
            }
        });

        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });

        spinner = view.findViewById(R.id.status_call);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        status_call = "0";
                        break;
                    case 1:
                        status_call = "1";
                        break;
                    case 2:
                        status_call = "2";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        submit_records = view.findViewById(R.id.submit_records);
        submit_records.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onClick(View v) {


                final String[] call_id = new String[30000];
                final String[] city = new String[30000];
                final Integer[] duration = new Integer[30000];

                if (tv.length() <5 || tv1.length()<5) {
                    Toast.makeText(getContext(),"Пожалуйста, укажите период", Toast.LENGTH_LONG).show();
                }
                else {

                    final String finalDataRecord = tv.getText().toString().substring(0, 10);
                    final String finalData2Record = tv1.getText().toString().substring(0, 10);
                    new AsyncTask<Void, Integer, String>() {

                        @Override
                        protected String doInBackground(Void... voids) {
                            publishProgress();
                            return getRecords(body, token, finalDataRecord, finalData2Record, tel, status_call);
                        }


                        @Override
                        protected void onProgressUpdate(Integer... values) {
                            super.onProgressUpdate(values);
                            progressBar.setVisibility(View.VISIBLE);
                            progressBar.setProgress(0);
                            LinearLayout main = view.findViewById(R.id.content_records);
                            main.removeAllViews();
                        }


                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                        @Override
                        protected void onPostExecute(String result) {
                            progressBar.setVisibility(View.GONE);
                            try {
                                JSONObject parentobject = new JSONObject(result);
                                LinearLayout mainL = view.findViewById(R.id.content_records);

                                JSONArray parentArray = parentobject.getJSONArray("data");

                                for (int i = 0; i < parentArray.length(); i++) {

                                    JSONObject parentObject1 = parentArray.getJSONObject(i);
                                    call_id[i] = parentObject1.getString("call_id");
                                    city [i] = parentObject1.getString("city");
                                    duration[i] =  parentObject1.getInt("duration");

                                    String src_num = parentObject1.getString("src_num");
                                    String dst_num = parentObject1.getString("dst_num");
                                    String date_start = parentObject1.getString("date_start");
                                    String date_end = parentObject1.getString("date_end");


                                    int color_black = ContextCompat.getColor(getContext(), R.color.BLACK);
                                    int color_gray = ContextCompat.getColor(getContext(), R.color.gray);
                                    int color_orange = ContextCompat.getColor(getContext(), R.color.gradStop);

                                    // Создание элементов для отображения

                                    LinearLayout lin_content = new LinearLayout(getContext());
                                    lin_content.setOrientation(LinearLayout.VERTICAL);
                                    lin_content.setLayoutParams(new LayoutParams
                                            (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                                    lin_content.setTag(i);
                                    lin_content.setBackground(getResources().getDrawable(R.drawable.bg_numbers));
                                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    layoutParams.setMargins(15,15,25,5);

                                    lin_content.setLayoutParams(layoutParams);

                                    LinearLayout lin_horizont = new LinearLayout(getContext());
                                    lin_horizont.setOrientation(LinearLayout.HORIZONTAL);
                                    lin_horizont.setLayoutParams(new LayoutParams
                                            (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));


                                    LinearLayout lin_image= new LinearLayout(getContext());
                                    lin_image.setOrientation(LinearLayout.VERTICAL);
                                    lin_image.setPadding(10, 15, 10, 10);
                                    lin_image.setLayoutParams(new LayoutParams
                                            (LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));

                                    ImageView img_play = new ImageView(getContext());
                                    img_play.setImageResource(R.drawable.circle_play);
                                    img_play.setLayoutParams(new LayoutParams
                                            (50, 50));

                                    ImageView img_play1 = new ImageView(getContext());
                                    img_play1.setLayoutParams(new LayoutParams
                                            (50, 50));



                                    if (duration[i] != 0) {
                                        lin_image.addView(img_play);
                                    }

                                    else {
                                        lin_image.addView(img_play1);
                                    }


                                    lin_horizont.addView(lin_image);


                                    LinearLayout lin_dst_num = new LinearLayout(getContext());
                                    lin_dst_num.setOrientation(LinearLayout.VERTICAL);
                                    lin_dst_num.setLayoutParams(new LayoutParams
                                            (LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

                                    TextView tv_dst_num1 = new TextView(getContext());
                                    tv_dst_num1.setPadding(5, 0, 0, 0);
                                    tv_dst_num1.setTextSize(16);
                                    tv_dst_num1.setTypeface(tv_dst_num1.getTypeface(), Typeface.NORMAL);
                                    tv_dst_num1.setText(dst_num);

                                    lin_dst_num.addView(tv_dst_num1);


                                    String month = date_start.substring(5, 7);
                                    String day  =date_start.substring(8, 10);

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

                                    int time = duration[i];
                                    int minute = time / 60;
                                    int sec = time % 60;


                                    TextView tv_date_start1 = new TextView(getContext());
                                    tv_date_start1.setPadding(5, 0, 0, 0);
                                    tv_date_start1.setTextSize(14);
                                    tv_date_start1.setTextColor(color_gray);
                                    tv_date_start1.setTypeface(tv_date_start1.getTypeface(), Typeface.NORMAL);
                                    if (minute == 0) {
                                        tv_date_start1.setText(day + " "+ month+ " "+ hhmm + " | "+sec +"c");
                                    }
                                    else {
                                        tv_date_start1.setText(day + " "+ month+ " "+ hhmm + " | " + minute + "мин " + sec +"c"); }

                                    lin_dst_num.addView(tv_date_start1);

                                    lin_horizont.addView(lin_dst_num);

                                    lin_content.addView(lin_horizont);

                                    mainL.addView(lin_content);


                                    lin_content.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            int s = Integer.parseInt(v.getTag().toString());

                                            if (duration[s] == 0) {
                                                Toast.makeText(getContext(), "Запись отсутствует", Toast.LENGTH_LONG).show();
                                            }

                                            else {
                                                String id = call_id[s];
                                                String gorod = city[s];
                                                new MakeNetworkCall().execute("https://calltracking.ufanet.ru/includes/getmusic.php?call_id=" + id + "&city=" + gorod + "&token=" + token , "Get");
                                               // new MakeNetworkCall().execute("https://calltracking.ufanet.ru/includes/getmusic.php?call_id=101150885&city=ufa&token=" + token, "Get");
                                            }

                                        }
                                    });

                                }



                            }

                            catch (JSONException e) {
                                e.printStackTrace();
                            }


                        }
                    }.execute();
                }

            }
        });

        /// Проверка на наличие услуги и получение списка номеров

        final String json = formatDataAsJSON();

        new AsyncTask<Void, Integer, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                publishProgress();
                return getServerResponse(json, token);

            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            protected void onPostExecute(final String result) {
                if (result.length() <10) {
                    linear_noaction.setVisibility(View.VISIBLE);
                }
                else {
                    rel_record.setVisibility(View.VISIBLE);
                    Log.d ("Test", result);
                    try {
                        final JSONArray parentArray = new JSONArray(result);
                        body = result;
                        for (int i = 0; i < parentArray.length(); i++) {

                            JSONObject parentObject = parentArray.getJSONObject(i);
                            tel = parentObject.getString("number"); //number
                            Log.d ("Test", tel);
                        }
                    }catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

        }.execute();

    }



    private class MakeNetworkCall extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            dialog_progress = new Dialog(getContext());
            dialog_progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog_progress.setContentView(R.layout.progress_dialog);
            dialog_progress.show();
        }

        @Override
        protected String doInBackground(String... arg) {

            InputStream is = null;
            String URL = arg[0];
            String res = "";

            is = ByGetMethod(URL);

            if (is != null) {
                res = ConvertStreamToString(is);
            } else {
                res = "Не удалось";
            }
            return res;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog_progress.dismiss();
            if (result.length() != 5) {
                Intent intent = new Intent(getContext(), PlayerActivity.class);
                intent.putExtra("result", result);
                startActivity(intent);

            }
            else {
                Toast.makeText(getContext(),result, Toast.LENGTH_LONG).show();
            }
        }
    }


    InputStream ByGetMethod(String ServerURL) {

        InputStream DataInputStream = null;
        try {

            URL url = new URL(ServerURL);
            HttpURLConnection cc = (HttpURLConnection)
                    url.openConnection();
            //set timeout for reading InputStream
            cc.setReadTimeout(5000);
            // set timeout for connection
            cc.setConnectTimeout(5000);
            //set HTTP method to GET
            cc.setRequestMethod("GET");
            //set it to true as we are connecting for input
            cc.setDoInput(true);

            //reading HTTP response code
            int response = cc.getResponseCode();

            //if response code is 200 / OK then read Inputstream
            if (response == HttpURLConnection.HTTP_OK) {
                DataInputStream = cc.getInputStream();
            }

        } catch (Exception e) {
        }
        return DataInputStream;

    }

    String ConvertStreamToString(InputStream stream) {

        InputStreamReader isr = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(isr);
        StringBuilder response = new StringBuilder();

        String line = null;
        try {

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

        } catch (IOException e) {
        } catch (Exception e) {
        } finally {

            try {
                stream.close();

            } catch (IOException e) {
            } catch (Exception e) {
            }
        }
        return response.toString();


    }

    private String formatDataAsJSON() {

        final JSONObject root = new JSONObject();
        try {
            root.put("username", "qwerty1");
            root.put("password", "Qwerty1");

            return root.toString();
        } catch (JSONException e) {
            Log.d("JWP", "Не удалось преобразовать в JSON");
        }
        return null;
    }

    private String getServerResponse(String json, String token) {


        HttpGet post = new HttpGet("https://telefon.ufanet.ru/api/Records/GetNumbers");
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


    private String getRecords(String json, String token, String date1, String date2, String src_num, String call_status ) {


        HttpPost post = new HttpPost("https://telefon.ufanet.ru/api/Records?date1="+date1+"%2000:00:00&date2="+date2+"%2023:59:59&src_num="+src_num+"&dst_num&call_status="+call_status);
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
}




