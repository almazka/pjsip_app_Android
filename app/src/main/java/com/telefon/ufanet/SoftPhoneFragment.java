package com.telefon.ufanet;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.telefon.ufanet.MVP.VOIP.PJSIPAccount;
import com.telefon.ufanet.MVP.VOIP.PJSIPCall;
import com.telefon.ufanet.MVP.VOIP.Service;

import org.pjsip.pjsua2.CallOpParam;

/**
 * Created by sadretdinov_a1109 on 21.12.2018.
 */

public class SoftPhoneFragment extends Fragment {

    TextView number_to_call;
    Button delete_button, dial_one, dial_two, dial_three, dial_four, dial_five, dial_six, dial_seven, dial_eight, dial_nine, dial_nol, dial_star, dial_resh, call;
    Animation animAlpha;
    public static PJSIPAccount account = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_softphone, container, false);

    }

    @SuppressLint({"WrongViewCast", "ClickableViewAccessibility"})
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        number_to_call = view.findViewById(R.id.et_number);
        delete_button = view.findViewById(R.id.delete);
        dial_one = view.findViewById(R.id.dig_one);
        dial_two = view.findViewById(R.id.dig_two);
        dial_three = view.findViewById(R.id.dig_three);
        dial_four = view.findViewById(R.id.dig_four);
        dial_five = view.findViewById(R.id.dig_five);
        dial_six = view.findViewById(R.id.dig_six);
        dial_seven = view.findViewById(R.id.dig_seven);
        dial_eight = view.findViewById(R.id.dig_eight);
        dial_nine = view.findViewById(R.id.dig_nine);
        dial_nol = view.findViewById(R.id.dig_noll);
        dial_star = view.findViewById(R.id.dig_star);
        dial_resh = view.findViewById(R.id.dig_pound);
        call = view.findViewById(R.id.make_call);


        animAlpha = AnimationUtils.loadAnimation(getContext(), R.anim.alpha);
        number_to_call.addTextChangedListener(number_change_listenner);
        dial_one.setOnClickListener(dial_one_listenner);
        dial_two.setOnClickListener(dial_two_listenner);
        dial_three.setOnClickListener(dial_three_listenner);
        dial_four.setOnClickListener(dial_four_listenner);
        dial_five.setOnClickListener(dial_five_listenner);
        dial_six.setOnClickListener(dial_six_listenner);
        dial_seven.setOnClickListener(dial_seven_listenner);
        dial_eight.setOnClickListener(dial_eight_listenner);
        dial_nine.setOnClickListener(dial_nine_listenner);
        dial_nol.setOnClickListener(dial_nol_listenner);
        dial_star.setOnClickListener(dial_star_listenner);
        dial_resh.setOnClickListener(dial_resh_listenner);
        delete_button.setOnTouchListener(delete_botton_listenner);
        call.setOnClickListener(call_button_listenner);

    }


    // Процедура нажатия на клавишу 1
    private View.OnClickListener dial_one_listenner = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            dial_one.startAnimation(animAlpha);
            if (number_to_call.length() < 15) {
                number_to_call.setText(number_to_call.getText().toString() + "1");
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);
            }
        }
    };
    // Процедура нажатия на клавишу 2
    private View.OnClickListener dial_two_listenner = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            dial_two.startAnimation(animAlpha);
            if (number_to_call.length() < 15) {
                number_to_call.setText(number_to_call.getText().toString() + "2");
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);
            }
        }
    };
    // Процедура нажатия на клавишу 3
    private View.OnClickListener dial_three_listenner = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            dial_three.startAnimation(animAlpha);
            if (number_to_call.length() < 15) {
                number_to_call.setText(number_to_call.getText().toString() + "3");
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);
            }
        }
    };

    // Процедура нажатия на клавишу 4
    private View.OnClickListener dial_four_listenner = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            dial_four.startAnimation(animAlpha);
            if (number_to_call.length() < 15) {
                number_to_call.setText(number_to_call.getText().toString() + "4");
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);
            }
        }
    };

    // Процедура нажатия на клавишу 5
    private View.OnClickListener dial_five_listenner = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            dial_five.startAnimation(animAlpha);
            if (number_to_call.length() < 15) {
                number_to_call.setText(number_to_call.getText().toString() + "5");
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);
            }
        }
    };

    // Процедура нажатия на клавишу 6
    private View.OnClickListener dial_six_listenner = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            dial_six.startAnimation(animAlpha);
            if (number_to_call.length() < 15) {
                number_to_call.setText(number_to_call.getText().toString() + "6");
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);
            }
        }
    };

    // Процедура нажатия на клавишу 7
    private View.OnClickListener dial_seven_listenner = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            dial_seven.startAnimation(animAlpha);
            if (number_to_call.length() < 15) {
                number_to_call.setText(number_to_call.getText().toString() + "7");
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);
            }
        }
    };

    // Процедура нажатия на клавишу 8
    private View.OnClickListener dial_eight_listenner = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            dial_eight.startAnimation(animAlpha);
            if (number_to_call.length() < 15) {
                number_to_call.setText(number_to_call.getText().toString() + "8");
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);
            }
        }
    };

    // Процедура нажатия на клавишу 9
    private View.OnClickListener dial_nine_listenner = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            dial_nine.startAnimation(animAlpha);
            if (number_to_call.length() < 15) {
                number_to_call.setText(number_to_call.getText().toString() + "9");
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);
            }
        }
    };

    // Процедура нажатия на клавишу 0
    private View.OnClickListener dial_nol_listenner = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            dial_nol.startAnimation(animAlpha);
            if (number_to_call.length() < 15) {
                number_to_call.setText(number_to_call.getText().toString() + "0");
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);

            }
        }
    };

    // Процедура нажатия на клавишу *
    private View.OnClickListener dial_star_listenner = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            dial_star.startAnimation(animAlpha);
            if (number_to_call.length() < 15) {
                number_to_call.setText(number_to_call.getText().toString() + "*");
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);
            }
        }
    };

    // Процедура нажатия на клавишу #
    private View.OnClickListener dial_resh_listenner = new View.OnClickListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onClick(View view) {
            dial_resh.startAnimation(animAlpha);
            if (number_to_call.length() < 15) {
                number_to_call.setText(number_to_call.getText().toString() + "#");
                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                vibrator.vibrate(40);
            }
        }
    };

    private  View.OnClickListener call_button_listenner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (Service.msg_str != null) {
                if (Service.msg_str.length() == 0) {
                    Toast.makeText(getContext(), "Необходима SIP регистрация", Toast.LENGTH_LONG).show();
                } else if (Service.msg_str.length() == 23) {
                    
                    if (number_to_call.length() == 0) {
                        Toast.makeText(getContext(), "Введите номер телефона", Toast.LENGTH_LONG).show();
                    } else {
                        account = Service.account;
                        PJSIPCall call = new PJSIPCall(account, -1);
                        CallOpParam prm = new CallOpParam(false);
                        try {
                            call.makeCall("sip:" + number_to_call.getText().toString() + "@92.50.152.146:5401", prm);

                        } catch (Exception e) {
                            call.delete();
                            return;
                        }

                        Service.currentCall = call;
                        showCallActivity();
                    }

                } else {
                    Toast.makeText(getContext(), Service.msg_str, Toast.LENGTH_LONG).show();
                }
            }
        }
    };

    private void showCallActivity() {
        Intent intent = new Intent(getContext(), CallActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("from", "main");
        startActivity(intent);
    }

    // Слушатель за количеством символов в текстовом поле
    private TextWatcher number_change_listenner = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }
        @Override
        public void afterTextChanged(Editable editable) {
            if (number_to_call.length() != 0) {
                delete_button.setVisibility(View.VISIBLE);
            }
            else {
                delete_button.setVisibility(View.GONE);
            }
        }
    };

    // Слушатель нажатия на клавишу удалить
    private View.OnTouchListener delete_botton_listenner = new View.OnTouchListener() {
        private Handler mHandler;
        private boolean downWithoutUp = false;
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    //включаем Handler
                    if (mHandler != null) return true;
                    mHandler = new Handler();
                    mHandler.postDelayed(runnable, 500);//период удержания после которого сработает Handler в UP (ms)
                    return true;
                case MotionEvent.ACTION_UP:
                    //отключаем Handler
                    if (mHandler == null) return true;
                    mHandler.removeCallbacks(runnable);
                    mHandler = null;
                    if (!downWithoutUp) {//если UP не сработал
                        if (number_to_call.length() == 0) {
                        } else {
                                String a = number_to_call.getText().toString().substring(0,number_to_call.length()-1);
                                number_to_call.setText(a);
                                delete_button.startAnimation(animAlpha);
                                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(25);
                        }
                    }
                    downWithoutUp = false;
                    return true;
            }
            return false;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                downWithoutUp = true;//устанавливаем флагу true, сработал UP
                if (number_to_call.length() == 0) {
                } else {
                    String a = number_to_call.getText().toString().substring(0,number_to_call.length()-1);
                    number_to_call.setText(a);
                    Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                    vibrator.vibrate(25);
                }
                mHandler.postDelayed(this, 100);
            }
        };
    };
}
