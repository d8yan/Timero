package com.conestoga.timero;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.core.splashscreen.SplashScreen;

import android.content.ClipData;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.DragEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedPreferences;
    private ImageView bookImage;
    private FloatingActionButton fab;
    private boolean saveTimer;
    private boolean creatingActivity = false;
    //drag drop
    private TextView breakTime;
    private TextView studyTime;
    private TextView dropText;
    private LinearLayout dropTarget;

    private TimeroReceiver timeroReceiver;

    //timer
    private static final long START_BREAK_TIME_IN_MILLIS = 300000; // 5 mins can test it by using 5000 which is for 5 secs
    private static final long START_STUDY_TIME_IN_MILLIS = 1500000; // 25 mins can test it by using 5000 which is for 5 secs
    private CountDownTimer countDownTimer;
    private boolean timerRunning;
    private long breakTimeLeftInMills = START_BREAK_TIME_IN_MILLIS;
    private long studyTimeLeftInMills = START_STUDY_TIME_IN_MILLIS;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        creatingActivity = true;
        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        boolean isDarkTheme = sharedPreferences.getBoolean("theme_value", false);
        if (isDarkTheme) {
            setTheme(R.style.Theme_Timero_Dark);
        }
        else {
            setTheme(R.style.Theme_Timero);
        }


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());

        bookImage = (ImageView) findViewById(R.id.imageViewBook);
        breakTime = (TextView) findViewById(R.id.break_Time);
        studyTime = (TextView) findViewById(R.id.study_Time);
        dropText = (TextView) findViewById(R.id.drop_text);
        dropTarget = (LinearLayout) findViewById(R.id.linearLayout_Target);


        //animation
        bookImage.startAnimation(AnimationUtils.loadAnimation(
                getApplicationContext(),R.anim.zoom_in

        ));
        //drag drop
        setUpDragDrop();

        //floating action button reset
        fab = (FloatingActionButton)findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetTimer();
                Snackbar.make(view,R.string.fb_reset, Snackbar.LENGTH_LONG).show();
            }
        });

        timeroReceiver = new TimeroReceiver();
    }
    private void setUpDragDrop(){
        final View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                boolean handled = true;
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    View.DragShadowBuilder dragShadowBuilder = new View.DragShadowBuilder(view);
                    view.startDragAndDrop(null,dragShadowBuilder,view,0);

                    handled = true;
                }
                return handled;
            }
        };
        final View.OnDragListener onDragListener = new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent dragEvent) {

                final View timerDragged = (View) dragEvent.getLocalState();
                if(dragEvent.getAction() == DragEvent.ACTION_DROP){


                    if(dropText.getText().toString().isEmpty() &&
                            timerDragged.getId() == R.id.break_Time &&
                            view.getId()==R.id.linearLayout_Target)
                    {
                        breakTime.setVisibility(view.INVISIBLE);
                        startBreakTimer();
                    }else if (dropText.getText().toString().isEmpty() &&
                            timerDragged.getId() == R.id.study_Time &&
                            view.getId()==R.id.linearLayout_Target)
                    {

                        studyTime.setVisibility(view.INVISIBLE);
                        startStudyTimer();
                    }
                }
                return true;
            }
        };

        breakTime.setOnTouchListener(onTouchListener);
        studyTime.setOnTouchListener(onTouchListener);
        dropTarget.setOnDragListener(onDragListener);

    }

    private void startBreakTimer(){
        countDownTimer = new CountDownTimer(breakTimeLeftInMills,1000) {
            @Override
            public void onTick(long l) {
                breakTimeLeftInMills = l;
                updateBreakCountDownText();
            }

            @Override
            public void onFinish() {
                resetTimer();
                timerRunning = false;
            }
        }.start();
        timerRunning = true;
    }
    private void startStudyTimer(){
        countDownTimer = new CountDownTimer(studyTimeLeftInMills,1000) {
            @Override
            public void onTick(long l) {
                studyTimeLeftInMills = l;
                updateStudyCountDownText();
            }

            @Override
            public void onFinish() {
                resetTimer();
                timerRunning = false;

            }
        }.start();
        timerRunning = true;
    }

    private void resetTimer(){
        breakTimeLeftInMills = START_BREAK_TIME_IN_MILLIS;
        studyTimeLeftInMills = START_STUDY_TIME_IN_MILLIS;
        breakTime.setText(breakTime.getText().toString());
        breakTime.setVisibility(View.VISIBLE);
        studyTime.setText(studyTime.getText().toString());
        studyTime.setVisibility(View.VISIBLE);

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        dropText.setText("");

    }
    private void updateBreakCountDownText() {
        int mins = (int) (breakTimeLeftInMills / 1000) / 60;
        int secs = (int) (breakTimeLeftInMills / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",mins,secs);
        dropText.setText(timeLeftFormatted);
    }
    private void updateStudyCountDownText() {
        int mins = (int) (studyTimeLeftInMills / 1000) / 60;
        int secs = (int) (studyTimeLeftInMills / 1000) % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d",mins,secs);
        dropText.setText(timeLeftFormatted);
    }
    private void updateDisplayTimer() {
        if (timerRunning) {
            if (breakTimeLeftInMills < START_BREAK_TIME_IN_MILLIS) {
                breakTime.setVisibility(View.INVISIBLE);
                studyTime.setVisibility(View.VISIBLE);
            }
            if (studyTimeLeftInMills < START_STUDY_TIME_IN_MILLIS) {
                studyTime.setVisibility(View.INVISIBLE);
                breakTime.setVisibility(View.VISIBLE);
            }
        }else {
            if (breakTimeLeftInMills == START_BREAK_TIME_IN_MILLIS) {
                breakTime.setVisibility(View.VISIBLE);
            }
            if (studyTimeLeftInMills == START_STUDY_TIME_IN_MILLIS) {
                studyTime.setVisibility(View.VISIBLE);
            }
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("breakTimeLeft",breakTimeLeftInMills);
        outState.putLong("studyTimeLeft",studyTimeLeftInMills);
        outState.putBoolean("timeRunning",timerRunning);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        breakTimeLeftInMills = savedInstanceState.getLong("breakTimeLeft");
        studyTimeLeftInMills = savedInstanceState.getLong("studyTimeLeft");
        timerRunning = savedInstanceState.getBoolean("timeRunning");
        updateDisplayTimer();


            if (breakTimeLeftInMills < START_BREAK_TIME_IN_MILLIS) {
                updateBreakCountDownText();
                if (timerRunning) {
                    startBreakTimer();
                }
            }

            if (studyTimeLeftInMills < START_STUDY_TIME_IN_MILLIS) {
                updateStudyCountDownText();
                if (timerRunning) {
                    startStudyTimer();
                }
            }

    }

    @Override
    protected void onPause() {
        SharedPreferences.Editor ed = sharedPreferences.edit();
        ed.putLong("breakTimeLeft",breakTimeLeftInMills);
        ed.putLong("studyTimeLeft",studyTimeLeftInMills);
        ed.putBoolean("timeRunning",timerRunning);
        ed.commit();
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();

//        boolean isDarkTheme = sharedPreferences.getBoolean("theme_value", false);
//        if (isDarkTheme) {
//            setTheme(R.style.Theme_Timero_Dark);
//        }
//        else {
//            setTheme(R.style.Theme_Timero);
//        }
        saveTimer = sharedPreferences.getBoolean("save_timer", false);
        breakTimeLeftInMills = sharedPreferences.getLong("breakTimeLeft",START_BREAK_TIME_IN_MILLIS);
        studyTimeLeftInMills = sharedPreferences.getLong("studyTimeLeft",START_STUDY_TIME_IN_MILLIS);
        timerRunning = sharedPreferences.getBoolean("timeRunning",false);

      if (saveTimer || !creatingActivity){
          updateDisplayTimer();
            if (breakTimeLeftInMills < START_BREAK_TIME_IN_MILLIS) {
                updateBreakCountDownText();
                if (timerRunning) {
                    startBreakTimer();
                }
            }

            if (studyTimeLeftInMills < START_STUDY_TIME_IN_MILLIS) {
                updateStudyCountDownText();
                if (timerRunning) {
                    startStudyTimer();
                }
            }
        }
      creatingActivity = false;
    }

    @Override
    protected void onStart() {
        registerReceiver(timeroReceiver, new IntentFilter(Intent.ACTION_BATTERY_LOW));
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        finish();
        startActivity(getIntent());
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.timero_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean ret = true;

        switch (item.getItemId()) {
            case R.id.menu_settings:
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                break;
            case R.id.menu_tasks:
                startActivity(new Intent(getApplicationContext(), TasksActivity.class));
                break;
            default:
                ret = super.onOptionsItemSelected(item);
                break;
        }

        return ret;
    }
}