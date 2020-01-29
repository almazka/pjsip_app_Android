package com.telefon.ufanet;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.echo.holographlibrary.Bar;
import com.echo.holographlibrary.BarGraph;
import com.echo.holographlibrary.HoloGraphAnimate;
import com.echo.holographlibrary.PieGraph;
import com.echo.holographlibrary.PieSlice;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


public class StatisticFragment extends Fragment implements AdapterView.OnItemSelectedListener  {

    TextView first, second, textinfo1, textinfo2,textinfo3,textinfo4,textinfo5,textinfo6, timeaverage, percent;
    Button primenit, button_ok, button_ok2;
    Calendar mCirrentDate, today, today2;
    int day,month,year;
    BarGraph bg;
    PieGraph pg;
    PieGraph pg1;
    BarGraph barGraph;
    String body, token;
    ProgressBar progress_statistic;
    LinearLayout main_layout;
    Dialog dialog, dialog2;
    DatePicker datePicker, datePicker2;
    Resources resources;
    String[] date;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_statistic, null);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        resources = getResources();
        dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.date_dialog_layout);
        dialog2 = new Dialog(getActivity());
        dialog2.setContentView(R.layout.date_dialog_layout);


        datePicker = dialog.findViewById(R.id.datePicker) ;
        pg = view.findViewById(R.id.piegraph);
        pg1 = view.findViewById(R.id.piegraph2);
        barGraph = view.findViewById(R.id.bargraph);
        progress_statistic = view.findViewById(R.id.progress_statistic);
        main_layout= view.findViewById(R.id.main_layout);
        primenit = view.findViewById(R.id.prim);
        first = view.findViewById(R.id.firstdate_statistic);
        second = view.findViewById(R.id.seconddate_statistic);
        button_ok = dialog.findViewById(R.id.button_ok);
        datePicker2  = dialog2.findViewById(R.id.datePicker) ;
        button_ok2 = dialog2.findViewById(R.id.button_ok);
        textinfo1 = view.findViewById(R.id.text_info1);
        textinfo2 = view.findViewById(R.id.text_info2);
        textinfo3 = view.findViewById(R.id.text_info3);
        textinfo4 = view.findViewById(R.id.text_info4);
        textinfo5 = view.findViewById(R.id.text_info5);
        textinfo6 = view.findViewById(R.id.text_info6);
        timeaverage = view.findViewById(R.id.time_average);
        percent = view.findViewById(R.id.text_percent);


        button_ok.setOnClickListener(button_ok_onclick_listenner);
        button_ok2.setOnClickListener(button_ok2_onclick_listenner);
        first.setOnClickListener(first_onclick_listenner);
        second.setOnClickListener(second_onclick_listenner);
        primenit.setOnClickListener(primenit_onclick_listenner);


        bg = barGraph;
        token = MainApp.token;

        //// Инициализация календаря
        mCirrentDate = Calendar.getInstance();
        day = mCirrentDate.get(Calendar.DAY_OF_MONTH);
        month = mCirrentDate.get(Calendar.MONTH);
        year = mCirrentDate.get(Calendar.YEAR);
        month = month +1;

        date = new String[1];

        if (month < 10 && day <10) {
            first.setText(year + "-0"+month+"-0"+day + " 00:00:00");
            second.setText(year + "-0"+month+"-0"+day + " 23:59:59");
        }
        else if (month < 10 && day> 9) {
            first.setText(year + "-0"+month+"-"+day + " 00:00:00");
            second.setText(year + "-0"+month+"-"+day + " 23:59:59");
        }
        else if (month > 9 && day < 10) {
            first.setText(year + "-"+month+"-0"+day + " 00:00:00");
            second.setText(year + "-"+month+"-0"+day + " 23:59:59");
        }
        else {first.setText(year + "-"+month+"-"+day + " 00:00:00");
            second.setText(year + "-"+month+"-"+day + " 23:59:59");}


        today = Calendar.getInstance();
        datePicker.init(today.get(Calendar.YEAR), today.get(Calendar.MONTH),
                today.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear+1;
                        if (monthOfYear < 10 && dayOfMonth <10) {
                            date[0] = year + "-0" +monthOfYear + "-0" + dayOfMonth + " 00:00:00";
                        }
                        else if (monthOfYear < 10 && dayOfMonth> 9) {
                            date[0] = year + "-0" +monthOfYear + "-" + dayOfMonth + " 00:00:00";
                        }
                        else if (monthOfYear > 9 && dayOfMonth < 10) {
                            date[0] = year + "-" +monthOfYear + "-0" + dayOfMonth + " 00:00:00";
                        }
                        else  date[0] = year + "-" +monthOfYear + "-" + dayOfMonth + " 00:00:00"; }
                });


        today2 = Calendar.getInstance();
        datePicker2.init(today2.get(Calendar.YEAR), today2.get(Calendar.MONTH),
                today2.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        monthOfYear = monthOfYear+1;
                        if (monthOfYear < 10 && dayOfMonth <10) {
                            date[0] = year + "-0" +monthOfYear + "-0" + dayOfMonth + " 23:59:59";
                        }
                        else if (monthOfYear < 10 && dayOfMonth> 9) {
                            date[0] = year + "-0" +monthOfYear + "-" + dayOfMonth + " 23:59:59";
                        }
                        else if (monthOfYear > 9 && dayOfMonth < 10) {
                            date[0] = year + "-" +monthOfYear + "-0" + dayOfMonth + " 23:59:59";
                        }
                        else  date[0] = year + "-" +monthOfYear + "-" + dayOfMonth + " 23:59:59"; }
                });


        /// Иницилизация графиков
        PieSlice slice = new PieSlice();
        slice.setColor(resources.getColor(R.color.green_light));
        slice.setSelectedColor(resources.getColor(R.color.transparent_orange));
        slice.setValue(2);
        slice.setTitle("first");
        pg.addSlice(slice);
        slice = new PieSlice();
        slice.setColor(resources.getColor(R.color.orange));
        slice.setValue(3);
        pg.addSlice(slice);

        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
        pg.setBackgroundBitmap(b);
        pg.setInnerCircleRatio(10);
        pg.setPadding(1);

        PieSlice slice2 = new PieSlice();
        slice2.setColor(resources.getColor(R.color.green_light));
        slice2.setSelectedColor(resources.getColor(R.color.transparent_orange));
        slice2.setValue(2);
        slice2.setTitle("first");
        pg1.addSlice(slice2);
        slice2 = new PieSlice();
        slice2.setColor(resources.getColor(R.color.orange));
        slice2.setValue(3);
        pg1.addSlice(slice2);

        Bitmap b2 = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background);
        pg1.setBackgroundBitmap(b2);
        pg1.setInnerCircleRatio(10);
        pg1.setPadding(1);

        ArrayList<Bar> aBars = new ArrayList<Bar>();
        Bar bar = new Bar();
        bar.setColor(resources.getColor(R.color.orange));
        bar.setSelectedColor(resources.getColor(R.color.transparent_orange));
        bar.setName("Входящие");
        bar.setValue(0);
        bar.setValueString("0");
        aBars.add(bar);
        bar = new Bar();
        bar.setColor(resources.getColor(R.color.purple));
        bar.setName("Исходящие");
        bar.setValue(0);
        bar.setValueString("0");
        aBars.add(bar);
        barGraph.setBars(aBars);


    }

    private View.OnClickListener button_ok_onclick_listenner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String d = Arrays.toString(date);
            d = d.substring(1, d.length()-1);
            first.setText(d);
            dialog.dismiss();
        }
    };

    private View.OnClickListener button_ok2_onclick_listenner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String d1 = Arrays.toString(date);
            d1 = d1.substring(1, d1.length()-1);
            second.setText(d1);
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


    private  View.OnClickListener primenit_onclick_listenner = new View.OnClickListener() {
        @SuppressLint("StaticFieldLeak")
        @Override
        public void onClick(View view) {
            if (first.length() < 5 || second.length() < 5) {
                Toast.makeText(getContext(), "Пожалуйста, укажите период", Toast.LENGTH_LONG).show();
            }
            else {
                final String finalDataRecord = first.getText().toString().substring(0, 10);
                final String finalData2Record = second.getText().toString().substring(0, 10);
                progress_statistic.setVisibility(View.VISIBLE);

                final String json = formatDataAsJSON();

                /// Запрос к Api Home telefon.ufanet.ru
                new AsyncTask<Void, Integer, String>() {
                    @Override
                    protected String doInBackground(Void... voids) {
                        publishProgress();
                        return getServerResponse(json, token, finalDataRecord, finalData2Record);

                    }
                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        super.onProgressUpdate(values);
                    }
                    @Override
                    protected void onPostExecute(String result) {
                        try {
                            JSONArray parentArray = new JSONArray(result);
                            for (int i = 0; i < parentArray.length(); i++) {
                                JSONObject parentObject = parentArray.getJSONObject(i);
                                JSONObject lastObject = parentObject.getJSONObject("phoneNumber");
                                String number_alias = lastObject.getString("alias");
                                int callsCount = parentObject.getInt("callsCount"); //Входящие всего
                                int incomingSucces = parentObject.getInt("incomingSucces"); //Входящие успешные
                                int percentSuccess = parentObject.getInt("percentIncSucces"); // Процент дозвона
                                int wait = parentObject.getInt("averageWaitDuration"); //Среднее время дозвона
                                int textcolor = ContextCompat.getColor(getContext(), R.color.BLACK);
                                percent.setText(percentSuccess + "%");
                                timeaverage.setText(wait + "c");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        /////   Запрос к Api StatisticFragment telefon.ufanet.ru
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
                                    final JSONArray parentArray = new JSONArray(result);
                                    body = result;
                                    body = body.substring(1, body.length()-1);

                                    new AsyncTask<Void, Integer, String>() {
                                        @Override
                                        protected String doInBackground(Void... voids) {
                                            publishProgress();
                                            return getStatistics(body, token, finalDataRecord, finalData2Record);
                                        }

                                        @Override
                                        protected void onProgressUpdate(Integer... values) {
                                            super.onProgressUpdate(values);
                                        }

                                        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                        @Override
                                        protected void onPostExecute(final String result) {
                                            progress_statistic.setVisibility(View.GONE);
                                            main_layout.setVisibility(View.VISIBLE);
                                            try {
                                                JSONObject parentObject = new JSONObject(result);
                                                int outgoing_total = parentObject.getInt("outgoing_total");
                                                int outgoing_success = parentObject.getInt("outgoing_success");
                                                int outgoing_fail = parentObject.getInt("outgoing_fail");
                                                int out_duration_avg = parentObject.getInt("out_duration_avg");
                                                int incoming_unique = parentObject.getInt("inc_duration_avg");
                                                int incoming_success = parentObject.getInt("incoming_success");
                                                int incoming_total = parentObject.getInt("incoming_total");
                                                int incoming_fail = parentObject.getInt("incoming_fail");
                                                textinfo1.setText("Успешные: "+ outgoing_success );
                                                textinfo2.setText("Неуспешные: " + outgoing_fail);
                                                textinfo3.setText("Всего: "+ outgoing_total);
                                                textinfo5.setText("Успешные: "+ incoming_success);
                                                textinfo6.setText("Неуспешные: "+ incoming_fail);
                                                textinfo4.setText("Всего: "+ incoming_total);
                                                for (int j = 0; j< pg.getSlices().size(); j++) {
                                                    PieSlice s = pg.getSlice(j);
                                                    if (j == 0) {s.setGoalValue(outgoing_success);}
                                                    else s.setGoalValue(outgoing_fail);

                                                }
                                                pg.setDuration(1000);
                                                pg.setInterpolator(new AccelerateDecelerateInterpolator());//default if unspecified is linear; constant speed
                                                pg.setAnimationListener(getAnimationListener());
                                                pg.animateToGoalValues();//animation will always overwrite. Pass true to call the onAnimationCancel Listener with onAnimationEnd

                                                for (int a =0; a< barGraph.getBars().size(); a++){
                                                    Bar b = barGraph.getBars().get(a);
                                                    if (a == 0) {
                                                        b.setGoalValue(incoming_unique);}
                                                    else b.setGoalValue(out_duration_avg);

                                                }
                                                barGraph.setDuration(1200);//default if unspecified is 300 ms
                                                barGraph.setInterpolator(new AccelerateDecelerateInterpolator());//Only use over/undershoot  when not inserting/deleting
                                                barGraph.setAnimationListener(getAnimationListener1());
                                                barGraph.animateToGoalValues();
                                                for (int k = 0; k< pg1.getSlices().size(); k++) {
                                                    PieSlice sk = pg1.getSlice(k);
                                                    if (k == 0) {sk.setGoalValue(incoming_success);}
                                                    else sk.setGoalValue(incoming_fail);

                                                }
                                                pg1.setDuration(1000);
                                                pg1.setInterpolator(new AccelerateDecelerateInterpolator());//default if unspecified is linear; constant speed
                                                pg1.setAnimationListener(getAnimationListener());
                                                pg1.animateToGoalValues();//animation will always overwrite. Pass true to call the onAnimationCancel Listener with onAnimationEnd
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
                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            }
        }
    };


    private String getStatistics(String json, String token, String date1, String date2 ) {
        HttpPost post = new HttpPost("https://telefon.ufanet.ru/api/Statistics?date1="+date1+"&date2="+date2);
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

    private String getNumberResponse(String json, String token) {

        HttpGet post = new HttpGet("https://telefon.ufanet.ru/api/Statistics/GetNumbers");
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





    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public Animator.AnimatorListener getAnimationListener(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
            return new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    Log.d("piefrag", "anim end");
                }

                @Override
                public void onAnimationCancel(Animator animation) {//you might want to call slice.setvalue(slice.getGoalValue)
                    Log.d("piefrag", "anim cancel");
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            };
        else return null;

    }

    @SuppressLint("ObsoleteSdkInt")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
    public Animator.AnimatorListener getAnimationListener1(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1)
            return new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ArrayList<Bar> newBars = new ArrayList<Bar>();
                    //Keep bars that were not deleted
                    for (Bar b : bg.getBars()){
                        if (b.mAnimateSpecial != HoloGraphAnimate.ANIMATE_DELETE){
                            b.mAnimateSpecial = HoloGraphAnimate.ANIMATE_NORMAL;
                            newBars.add(b);
                        }
                    }
                    bg.setBars(newBars);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            };
        else return null;

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String te = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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

    private String getServerResponse(String json, String token, String data1, String data2) {

        HttpGet post = new HttpGet("https://telefon.ufanet.ru/api/Home?date1="+data1+"%2000:00:00&date2="+data2+"%2023:59:59");
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
        return "Доступно";
    }

}
