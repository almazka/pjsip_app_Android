package com.telefon.ufanet;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class PlayerActivity extends AppCompatActivity {

    Button playBtn;
    SeekBar positionBar;
    TextView elapsedTimeLabel;
    TextView remainingTimeLabel;
    MediaPlayer mp = new MediaPlayer();
    int totalTime;
    public String result;
    public Intent intent;

    ProgressBar pb;
    Dialog dialog;
    int totalSize = 0;
    String dwnload_file_path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_activity);
        intent = getIntent();
        result = intent.getStringExtra("result");

        dwnload_file_path = "https://calltracking.ufanet.ru/app/"+result+".mp3";

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        showProgress(dwnload_file_path);

        new Thread(new Runnable() {
            public void run() {
                downloadFile();
            }
        }).start();

        playBtn = (Button) findViewById(R.id.playBtn);
        elapsedTimeLabel = (TextView) findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = (TextView) findViewById(R.id.remainingTimeLabel);
        positionBar = (SeekBar) findViewById(R.id.positionBar);


        // Media Player
        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser) {
                            mp.seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );

        // Thread (Update positionBar & timeLabel)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mp != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mp.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();



    }

    void downloadFile(){

        try {
            URL url = new URL(dwnload_file_path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setRequestMethod("GET");
            urlConnection.setDoOutput(true);

            //connect
            urlConnection.connect();

            //set the path where we want to save the file
            File SDCardRoot = Environment.getExternalStorageDirectory();
            //create a new file, to save the downloaded file
            File file = new File(SDCardRoot,"sample.mp3");

            FileOutputStream fileOutput = new FileOutputStream(file);

            //Stream used for reading the data from the internet
            InputStream inputStream = urlConnection.getInputStream();

            //this is the total size of the file which we are downloading
            totalSize = urlConnection.getContentLength();

            runOnUiThread(new Runnable() {
                public void run() {
                }
            });

            //create a buffer...
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                fileOutput.write(buffer, 0, bufferLength);
                // update the progressbar //
                runOnUiThread(new Runnable() {
                    public void run() {

                    }
                });
            }
            //close the output stream when complete //
            fileOutput.close();
            runOnUiThread(new Runnable() {
                public void run() {

                    mp = MediaPlayer.create(getApplicationContext(), Uri.parse(Environment.getExternalStorageDirectory().getPath() + "/sample.mp3"));
                  //  mp.prepareAsync();

                    if (mp != null) {
                        mp.setLooping(false);
                        mp.seekTo(0);
                        mp.setVolume(0.5f, 0.5f);

                        mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            public void onPrepared(final MediaPlayer mp) {
                                totalTime = mp.getDuration();
                                positionBar.setMax(totalTime);
                                Handler handler1 = new Handler();
                                handler1.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                        elapsedTimeLabel.setVisibility(View.VISIBLE);
                                        remainingTimeLabel.setVisibility(View.VISIBLE);
                                        mp.start();
                                        playBtn.setBackgroundResource(R.drawable.stop);


                                    }
                                }, 1000);

                            }

                        });


                        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                            public void onCompletion(MediaPlayer arg0) {
                                playBtn.setBackgroundResource(R.drawable.play);

                            }

                        });
                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Ошибка сервера", Toast.LENGTH_LONG).show();
                    }

                }
            });

        } catch (final MalformedURLException e) {
            showError("Error : MalformedURLException " + e);
            e.printStackTrace();
        } catch (final IOException e) {
            showError("Error : IOException " + e);
            e.printStackTrace();
        }
        catch (final Exception e) {
            showError("Error : Please check your internet connection " + e);
        }
    }

    void showError(final String err){
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getApplicationContext(), err, Toast.LENGTH_LONG).show();
            }
        });
    }

    void showProgress(String file_path){
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_dialog);
        dialog.setTitle("Download Progress");

        dialog.show();

        pb = (ProgressBar)dialog.findViewById(R.id.progress_bar);
        pb.setProgress(0);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update positionBar.
            positionBar.setProgress(currentPosition);

            // Update Labels.
            String elapsedTime = createTimeLabel(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeLabel(totalTime - currentPosition);
            remainingTimeLabel.setText("- " + remainingTime);
        }
    };

    public String createTimeLabel(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if (sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

    public void playBtnClick(View view) {


            if (!mp.isPlaying()) {
                // Stopping
                mp.start();
                playBtn.setBackgroundResource(R.drawable.stop);

            } else {
                // Playing
                mp.pause();
                playBtn.setBackgroundResource(R.drawable.play);
            }

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mp != null) {
            mp.stop();
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        File file = new File(Environment.getExternalStorageDirectory().getPath().toString() + "/sample.mp3");
        file.delete();
    }
}