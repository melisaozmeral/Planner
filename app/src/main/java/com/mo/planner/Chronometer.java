package com.mo.planner;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Chronometer extends AppCompatActivity {
    FirebaseAuth mAuth;
    private long pauseOffset=0;
    MediaPlayer rain,clock;
    SesseionManager sesseionManager;
    SimpleDateFormat format;
    String currentTime;
    @Override
    protected void onStop() {
        super.onStop();
        rain.stop();
        clock.stop();
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chronometer_layout);

        Button play=findViewById(R.id.start);
        Button pause=findViewById(R.id.pause);
        Button resume=findViewById(R.id.resume);
        Button finish=findViewById(R.id.finish);
        Button musicMenu=findViewById(R.id.musicLibrary);
        Button pomoButton=findViewById(R.id.pomoButton);

        rain=MediaPlayer.create(Chronometer.this,R.raw.rain);
        clock=MediaPlayer.create(Chronometer.this,R.raw.clock);

        mAuth = FirebaseAuth.getInstance();
        TextView textPause=findViewById(R.id.textPause);
        android.widget.Chronometer chronometer=findViewById(R.id.chronometer);

        sesseionManager=new SesseionManager(getApplicationContext());
        format=new SimpleDateFormat("hh:mm:ss aa");

        currentTime=format.format(new Date());




        BottomNavigationView bottomNavigationView=findViewById(R.id.navigation);
        bottomNavigationView.setSelectedItemId(R.id.timer);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.activity:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
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


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPause.setVisibility(View.INVISIBLE);
                play.setVisibility(v.INVISIBLE);
                pause.setVisibility(v.VISIBLE);
                pause.setClickable(true);
                play.setClickable(false);
                chronometer.setBase(SystemClock.elapsedRealtime()-pauseOffset);
                chronometer.start();
            }
        });
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPause.setVisibility(View.VISIBLE);
                pause.setVisibility(v.INVISIBLE);
                resume.setVisibility(v.VISIBLE);
                finish.setVisibility(v.VISIBLE);
                pause.setClickable(false);
                resume.setClickable(true);
                finish.setClickable(true);
                chronometer.stop();
                pauseOffset=SystemClock.elapsedRealtime()-chronometer.getBase();
            }
        });

        resume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPause.setVisibility(View.INVISIBLE);
                pause.setVisibility(v.VISIBLE);
                resume.setVisibility(v.INVISIBLE);
                finish.setVisibility(v.INVISIBLE);
                pause.setClickable(true);
                resume.setClickable(false);
                finish.setClickable(false);
                chronometer.setBase(SystemClock.elapsedRealtime()-pauseOffset);
                chronometer.start();


            }
        });
        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textPause.setVisibility(View.INVISIBLE);
                resume.setVisibility(v.INVISIBLE);
                finish.setVisibility(v.INVISIBLE);
                play.setVisibility(v.VISIBLE);
                resume.setClickable(false);
                finish.setClickable(false);
                play.setClickable(true);
                chronometer.setBase(SystemClock.elapsedRealtime());
                pauseOffset=0;
                if(rain.isPlaying()){
                    rain.pause();
                }
                if(clock.isPlaying()) {
                    clock.pause();
                }

            }
        });

        pomoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Chronometer.this, Timer.class);
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
        musicMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu=new PopupMenu(Chronometer.this,musicMenu);
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
                menu.getMenuInflater().inflate(R.menu.music_menu,menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.none:{
                                if(rain.isPlaying()){
                                    rain.pause();
                                }
                                if(clock.isPlaying()) {
                                    clock.pause();
                                }
                                break;
                            }
                            case R.id.clock:{
                                if(rain.isPlaying()){
                                    rain.pause();
                                }
                                clock.start();
                                clock.setLooping(true);
                                break;
                            }
                            case R.id.rain:{
                                if(clock.isPlaying()) {
                                    clock.pause();
                                }
                                rain.start();
                                rain.setLooping(true);
                                break;
                            }
                        }
                        return true;
                    }
                });
                menu.show();
            }

        });
    }

}
