package com.example.summonsattackbeta;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;

public class GameActivity extends AppCompatActivity {
    Button escButt;
    ConstraintLayout gameLayout;
    GameView gameView;

    static MediaPlayer main_theme;
    static MediaPlayer coin_bonk;

    static Vibrator vibrator;

    public static boolean activeGameKey;

    static class BonkThread extends Thread {
        MediaPlayer music;

        public BonkThread(Context context) {
            music = MediaPlayer.create(context, R.raw.coin_bonk);
        }

        @Override
        public void run() {
            music.start();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        main_theme = MediaPlayer.create(this, R.raw.bit);
        main_theme.setLooping(true);
        if (WelcomeActivity.isMusic) {
            main_theme.start();
        }
        coin_bonk = MediaPlayer.create(this, R.raw.coin_bonk);

        gameView = new GameView(this);
        setContentView(R.layout.activity_game);
        gameLayout = findViewById(R.id.gameLayout);
        gameLayout.addView(gameView);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        escButt = findViewById(R.id.esc);
        escButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (activeGameKey) {
                    WelcomeActivity.addEntry((int) gameView.TIME, gameView.SCORE);
                }
                activeGameKey = false;
                openDeathActivity((int) gameView.TIME, gameView.SCORE);
            }
        });
    }

    public void openDeathActivity(int TIME, int SCORE) {
        main_theme.pause();
        activeGameKey = false;
        Intent wayToLose = new Intent(this, LoseActivity.class);
        wayToLose.putExtra("time", TIME);
        wayToLose.putExtra("score", SCORE);
        finish();
        startActivity(wayToLose);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        main_theme.pause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        main_theme.pause();
        if (activeGameKey) {
            WelcomeActivity.addEntry((int) gameView.TIME, gameView.SCORE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        main_theme.pause();
    }
}
