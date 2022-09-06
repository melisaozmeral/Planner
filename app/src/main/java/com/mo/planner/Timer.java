package com.mo.planner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.CountDownTimer;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

public class Timer extends AppCompatActivity {

        private EditText mEditTextInput;
        private TextView mTextViewCountDown;
        private Button mButtonSet;
        private Button mButtonStartPause;
        private Button mButtonReset;

       private MediaPlayer rainP, clockP;
       Button musicMenu,pomoButton,chronometerButton;
       FirebaseAuth mAuth;

        private CountDownTimer mCountDownTimer;

        private boolean mTimerRunning;

        private long mStartTimeInMillis;
        private long mTimeLeftInMillis;
        private long mEndTime;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.timer);

            pomoButton=findViewById(R.id.pomoButton);
            chronometerButton=findViewById(R.id.chronometerButton);
            musicMenu=findViewById(R.id.musicLibrary);
            rainP= MediaPlayer.create(Timer.this,R.raw.rain);
            clockP =MediaPlayer.create(Timer.this,R.raw.clock);

            chronometerButton.setCompoundDrawableTintList(AppCompatResources.getColorStateList(chronometerButton.getContext(),R.color.grey));
            pomoButton.setCompoundDrawableTintList(AppCompatResources.getColorStateList(pomoButton.getContext(),R.color.blue));

            mAuth = FirebaseAuth.getInstance();

            BottomNavigationView bottomNavigationView=findViewById(R.id.navigation);
            bottomNavigationView.setSelectedItemId(R.id.timer);
            bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.activity:
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                            overridePendingTransition(0,0);
                            return true;
                        case R.id.timer:
                            return false;
                        case R.id.step:
                            startActivity(new Intent(getApplicationContext(),StepCounter.class));
                            finish();
                            overridePendingTransition(0,0);
                            return true;
                        case R.id.inbox:
                            startActivity(new Intent(getApplicationContext(), Category.class));
                            finish();
                            overridePendingTransition(0,0);
                            return true;
                        case R.id.exit_app:
                            mAuth.signOut();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                            return true;

                    }
                    return false;
                }
            });

            musicMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu menu = new PopupMenu(Timer.this, musicMenu);
                    try {
                        Field[] fields = menu.getClass().getDeclaredFields();
                        for (Field field : fields) {
                            if ("mPopup".equals(field.getName())) {
                                field.setAccessible(true);
                                Object menuPopupHelper = field.get(menu);
                                Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                                Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                                setForceIcons.invoke(menuPopupHelper, true);
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    menu.getMenuInflater().inflate(R.menu.music_menu, menu.getMenu());
                    menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.none: {
                                    if (rainP.isPlaying()) {
                                        rainP.pause();
                                    }
                                    if (clockP.isPlaying()) {
                                        clockP.pause();
                                    }
                                    break;
                                }
                                case R.id.clock: {
                                    if (rainP.isPlaying()) {
                                        rainP.pause();
                                    }
                                    clockP.start();
                                    clockP.setLooping(true);
                                    break;
                                }
                                case R.id.rain: {
                                    if (clockP.isPlaying()) {
                                        clockP.pause();
                                    }
                                    rainP.start();
                                    rainP.setLooping(true);
                                    break;
                                }
                            }
                            return true; }
                    });
                    menu.show();
                }
            });
            chronometerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Timer.this, Chronometer.class);
                    startActivity(intent);
                    overridePendingTransition(0,0);

                }
            });

            mEditTextInput = findViewById(R.id.edit_text_input);
            mTextViewCountDown = findViewById(R.id.text_view_countdown);

            mButtonSet=findViewById(R.id.button_set);
            mButtonStartPause = findViewById(R.id.button_start_pause);
            mButtonReset = findViewById(R.id.button_reset);

            mButtonSet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String input = mEditTextInput.getText().toString();
                    if (input.length() == 0) {
                        Toast.makeText(Timer.this, "Dakika giriniz", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long millisInput = Long.parseLong(input) * 60000;
                    if (millisInput == 0) {
                        Toast.makeText(Timer.this, "Pozitif bir sayı giriniz", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    setTime(millisInput);
                    mEditTextInput.setText("");
                }
            });

            mButtonStartPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mTimerRunning) {
                        pauseTimer();
                    } else {
                        startTimer();
                    }
                }
            });

            mButtonReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    resetTimer();
                }
            });
        }

        private void setTime(long milliseconds) {
            mStartTimeInMillis = milliseconds;
            resetTimer();
            closeKeyboard();
        }

        private void startTimer() {
            mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

            mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTimeLeftInMillis = millisUntilFinished;
                    updateCountDownText();
                }

                @Override
                public void onFinish() {
                    mTimerRunning = false;
                    updateWatchInterface();
                }
            }.start();

            mTimerRunning = true;
            updateWatchInterface();
        }

        private void pauseTimer() {
            mCountDownTimer.cancel();
            mTimerRunning = false;
            updateWatchInterface();
        }

        private void resetTimer() {
            mTimeLeftInMillis = mStartTimeInMillis;
            updateCountDownText();
            updateWatchInterface();
        }

        private void updateCountDownText() {
            int hours = (int) (mTimeLeftInMillis / 1000) / 3600;
            int minutes = (int) ((mTimeLeftInMillis / 1000) % 3600) / 60;
            int seconds = (int) (mTimeLeftInMillis / 1000) % 60;

            String timeLeftFormatted;
            if (hours > 0) {
                timeLeftFormatted = String.format(Locale.getDefault(),
                        "%d:%02d:%02d", hours, minutes, seconds);
            } else {
                timeLeftFormatted = String.format(Locale.getDefault(),
                        "%02d:%02d", minutes, seconds);
            }

            mTextViewCountDown.setText(timeLeftFormatted);
        }

        private void updateWatchInterface() {
            Drawable play=getResources().getDrawable(R.drawable.ic_baseline_play_circle_filled_24);
            play.setBounds( 0, 0,play.getIntrinsicWidth(), play.getIntrinsicHeight());
            Drawable stop=getResources().getDrawable(R.drawable.ic_baseline_stop_circle_24);
            stop.setBounds( 0, 0, stop.getIntrinsicWidth(), stop.getIntrinsicHeight());

            if (mTimerRunning) {
                mEditTextInput.setVisibility(View.INVISIBLE);
                mButtonSet.setVisibility(View.INVISIBLE);
                mButtonReset.setVisibility(View.INVISIBLE);
                mButtonStartPause.setCompoundDrawables(null,stop,null,null);
            } else {
                mEditTextInput.setVisibility(View.VISIBLE);
                mButtonSet.setVisibility(View.VISIBLE);
                mButtonStartPause.setCompoundDrawables(null,play,null,null);
            }
                if (mTimeLeftInMillis < 1000) {
                    mButtonStartPause.setVisibility(View.INVISIBLE);
                } else {
                    mButtonStartPause.setVisibility(View.VISIBLE);
                }

                if (mTimeLeftInMillis < mStartTimeInMillis) {
                    mButtonReset.setVisibility(View.VISIBLE);
                } else {
                    mButtonReset.setVisibility(View.INVISIBLE);
                }
        }


        private void closeKeyboard() {
            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }

        @Override
        protected void onStop() {
            super.onStop();

            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            editor.putLong("startTimeInMillis", mStartTimeInMillis);
            editor.putLong("millisLeft", mTimeLeftInMillis);
            editor.putBoolean("timerRunning", mTimerRunning);
            editor.putLong("endTime", mEndTime);

            editor.apply();

            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
            }
            if(rainP.isPlaying()){
                rainP.pause();
            }
            if(clockP.isPlaying()) {
                clockP.pause();
            }
        }

        @Override
        protected void onStart() {
            super.onStart();

            SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);

            mStartTimeInMillis = prefs.getLong("startTimeInMillis", 600000);
            mTimeLeftInMillis = prefs.getLong("millisLeft", mStartTimeInMillis);
            mTimerRunning = prefs.getBoolean("timerRunning", false);

            updateCountDownText();
            updateWatchInterface();

            if (mTimerRunning) {
                mEndTime = prefs.getLong("endTime", 0);
                mTimeLeftInMillis = mEndTime - System.currentTimeMillis();

                if (mTimeLeftInMillis < 0) {
                    mTimeLeftInMillis = 0;
                    mTimerRunning = false;
                    updateCountDownText();
                    updateWatchInterface();
                } else {
                    startTimer();
                }
            }
        }

    }
