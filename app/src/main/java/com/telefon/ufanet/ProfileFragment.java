package com.telefon.ufanet;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.telefon.ufanet.MVP.VOIP.Service;
import com.telefon.ufanet.MVP.View.AuthorizeActivity;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.pjsip.pjsua2.Endpoint;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;


public class ProfileFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    Spinner spinner;
    LinearLayout linear_all_calls, linear_calls_delay;
    ProgressBar progress_redirect;
    Switch switch_vats, switch_connection;
    EditText et_redirect_all, et_redirect_delay_number, et_redirect_delay;
    CardView redirect, DetailFragment, MissedCallFragment, RecordsFragment;
    Button b_redirect_all, b_redirect_delay;
    JSONArray updateCFU, updateCFNR;
    ImageView image_exit, image_male;
    SharedPreferences sPref;
    TextView tvUser;

    @SuppressLint("StaticFieldLeak")
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner = view.findViewById(R.id.spinner_redirect);
        linear_all_calls = view.findViewById(R.id.lin_redirect_all);
        linear_calls_delay = view.findViewById(R.id.lin_redirect_delay);
        progress_redirect = view.findViewById(R.id.progress_redirect);
        et_redirect_all = view.findViewById(R.id.et_redirect_all);
        et_redirect_delay_number = view.findViewById(R.id.et_redirect_delay_number);
        et_redirect_delay = view.findViewById(R.id.et_redirect_delay);
        redirect = view.findViewById(R.id.redirect);
        b_redirect_all = view.findViewById(R.id.button_redirect_all);
        b_redirect_delay = view.findViewById(R.id.button_redirect_delay);
        image_exit = view.findViewById(R.id.image_exit);
        switch_vats = view.findViewById(R.id.switch_vats);
        switch_connection = view.findViewById(R.id.switch_connection);
        DetailFragment = view.findViewById(R.id.DetailFragment);
        MissedCallFragment = view.findViewById(R.id.MissedCallsFragment);
        RecordsFragment = view.findViewById(R.id.RecordsFragment);
        tvUser = view.findViewById(R.id.tv_user);
        image_male = view.findViewById(R.id.img_male);


        spinner.setOnItemSelectedListener(spinner_click_listenner);
        b_redirect_all.setOnClickListener(redirect_all_listenner);
        b_redirect_delay.setOnClickListener(redirect_delay_listenner);
        image_exit.setOnClickListener(image_exit_click_listenner.get());
        switch_vats.setOnClickListener(switch_vats_click_listenner);
        switch_connection.setOnClickListener(switch_connection_click_listenner);
        image_male.setOnClickListener(image_male_click_listenner);

        tvUser.setText(MainApp.name);


        DetailFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), DetatizationActivity.class);
                startActivity(i);
            }
        });

        MissedCallFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext(), MissedCallActivity.class);
                startActivity(i);
            }
        });

        RecordsFragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Intent i = new Intent(getContext(), RecordsActivity.class);
               // startActivity(i);
                Toast.makeText(getContext(), "Скоро!", Toast.LENGTH_LONG).show();
            }
        });

       if (Objects.equals(String.valueOf(MainApp.vatsChecked.length()), "4") || Objects.equals(String.valueOf(MainApp.vatsChecked.length()), "0")) {
           switch_vats.setChecked(true);
       }
       else if (Objects.equals(String.valueOf(MainApp.vatsChecked.length()), "5")) {
           switch_vats.setChecked(false);
       }

        if (Objects.equals(String.valueOf(MainApp.connectionChecked.length()), "4") || Objects.equals(String.valueOf(MainApp.connectionChecked.length()), "0")) {
            switch_connection.setChecked(true);
        }
        else if (Objects.equals(String.valueOf(MainApp.connectionChecked.length()), "5")) {
            switch_connection.setChecked(false);
        }


        progress_redirect.setVisibility(View.VISIBLE);

        final String json = formatDataAsJSON();

        new AsyncTask<Void, Integer, String>() {

            @Override
            protected String doInBackground(Void... voids) {
                publishProgress();
                return getServerResponse(json, MainApp.token);
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);

            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            protected void onPostExecute(final String result) {
                progress_redirect.setVisibility(View.GONE);
                try {
                    final JSONArray parentArray = new JSONArray(result);
                    updateCFU = new JSONArray(result);
                    updateCFNR = new JSONArray(result);
                    for (int i = 0; i < parentArray.length(); i++) {

                        JSONObject parentObject = parentArray.getJSONObject(i);
                        JSONObject lastObject = parentObject.getJSONObject("phoneNumber");

                        String number_alias = lastObject.getString("alias");
                        String cfUnumber = parentObject.getString("cfUnumber"); //cfUnumber
                        String cfnRnumber = parentObject.getString("cfnRnumber"); //cfnRnumber
                        String cfnRtimeout = parentObject.getString("cfnRtimeout"); // cfnRtimeout

                        et_redirect_all.setText(cfUnumber);
                        et_redirect_delay_number.setText(cfnRnumber);
                        et_redirect_delay.setText(cfnRtimeout);

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
             redirect.setVisibility(View.VISIBLE);


            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

    }



    /////// Cлушатель нажатия на иконку человечка
    View.OnClickListener image_male_click_listenner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            PopupMenu popup = new PopupMenu(getContext(), image_male);
            //Inflating the Popup using xml file
            popup.getMenuInflater()
                    .inflate(R.menu.popup_menu, popup.getMenu());
            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    Service.DeInit();
                    getActivity().stopService(new Intent(getContext(), Service.class));
                    getActivity().finishAffinity();
                    Intent i = new Intent(getContext(), AuthorizeActivity.class);
                    i.putExtra("unreg", "true");
                    startActivity(i);
                    return true;
                }
            });

            popup.show(); //showing popup_menu
        }
    };

    /////// Слушатель нажатия на switch качества сети
    View.OnClickListener switch_connection_click_listenner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            if (MainApp.connectionChecked.length() == 0 || MainApp.connectionChecked.length() == 5) {
                MainApp.connectionChecked = "true";
                ed.putString("connection_checked", "true");
                ed.commit();

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
            else {
                MainApp.connectionChecked = "false";
                ed.putString("connection_checked", "false");
                ed.commit();
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
    };

    /////// Слушатель нажатия на switch ВАТС
    View.OnClickListener switch_vats_click_listenner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            sPref = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor ed = sPref.edit();
            if (MainApp.vatsChecked.length() == 0 || MainApp.vatsChecked.length() == 5) {
                MainApp.vatsChecked = "true";
                ed.putString("vats_checked", "true");
                ed.commit();
            }
            else {
                MainApp.vatsChecked = "false";
                ed.putString("vats_checked", "false");
                ed.commit();
            }
        }
    };

    /////// Слушатель нажатия на кнопку выход
    private final ThreadLocal<View.OnClickListener> image_exit_click_listenner = new ThreadLocal<View.OnClickListener>() {
        @Override
        protected View.OnClickListener initialValue() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Service.DeInit();
                    getActivity().stopService(new Intent(getContext(), Service.class));
                    Handler handler3 = new Handler();
                    handler3.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getActivity().finishAffinity();
                            Process.killProcess(Process.myPid());
                        }
                    }, 10);
                }
            };
        }
    };

    /////// Слушатель нажатия на кнопку сохранить переадресацию всех вызовов
    private View.OnClickListener redirect_all_listenner = new View.OnClickListener() {
        @SuppressLint("StaticFieldLeak")
        @Override
        public void onClick(View view) {
            try {
                for (int i = 0; i < updateCFU.length(); i++) {

                    JSONObject parentObject = updateCFU.getJSONObject(i);
                    parentObject.put("cfUnumber", et_redirect_all.getText().toString()); //cfUnumber
                    }
                }
            catch (JSONException e) {
                e.printStackTrace();
            }


            new AsyncTask<Void, Integer, String>() {

                @Override
                    protected String doInBackground(Void... voids) {
                        publishProgress();
                        String FinalCFU = updateCFU.toString();
                        FinalCFU = FinalCFU.substring(1, FinalCFU.length() - 1);
                        return UpdateCFU(FinalCFU, MainApp.token);

                    }

                    @Override
                    protected void onProgressUpdate(Integer... values) {
                        super.onProgressUpdate(values);

                    }

                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    protected void onPostExecute(final String result) {
                        Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                    }

                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    };



    /////// Слушатель нажатия на кнопку сохранить переадресации по неответу
    private View.OnClickListener redirect_delay_listenner = new View.OnClickListener() {
        @SuppressLint("StaticFieldLeak")
        @Override
        public void onClick(View view) {
            try {
            for (int i = 0; i < updateCFNR.length(); i++) {

                JSONObject parentObject = updateCFNR.getJSONObject(i);

                parentObject.put("cfnRnumber", et_redirect_delay_number.getText().toString()); //cfNRnumber
                parentObject.put("cfnRtimeout", et_redirect_delay.getText().toString()); //CFNRTimeout
            }


            new AsyncTask<Void, Integer, String>() {

                @Override
                protected String doInBackground(Void... voids) {
                    publishProgress();
                    String FinalCFNR = updateCFNR.toString();
                    FinalCFNR = FinalCFNR.substring(1, FinalCFNR.length() - 1);
                    return UpdateCFNR(FinalCFNR, MainApp.token);

                }

                @Override
                protected void onProgressUpdate(Integer... values) {
                    super.onProgressUpdate(values);

                }

                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                protected void onPostExecute(final String result) {
                    Toast.makeText(getContext(), result, Toast.LENGTH_LONG).show();
                }

            }.execute();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };


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


        HttpGet post = new HttpGet("https://telefon.ufanet.ru/api/CallForwarding");
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


    private String UpdateCFU(String json, String token) {


        HttpPost post = new HttpPost("https://telefon.ufanet.ru/api/CallForwarding/UpdateCFU");
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

        return "Доступно";
    }

    private String UpdateCFNR(String json, String token) {


        HttpPost post = new HttpPost("https://telefon.ufanet.ru/api/CallForwarding/UpdateCFNR");
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

        return "Доступно";
    }

    private AdapterView.OnItemSelectedListener spinner_click_listenner = new AdapterView.OnItemSelectedListener() {


        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
            switch (position) {
                case 0:
                    linear_all_calls.setVisibility(View.VISIBLE);
                    linear_calls_delay.setVisibility(View.GONE);
                    break;
                case 1:
                    linear_all_calls.setVisibility(View.GONE);
                    linear_calls_delay.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }

    };

}

