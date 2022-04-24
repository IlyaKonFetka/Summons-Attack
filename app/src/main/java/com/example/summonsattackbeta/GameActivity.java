package com.example.summonsattackbeta;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class GameActivity extends AppCompatActivity {
    Button escButt;

    static float maxX, maxY;
    MediaPlayer music;

    static Vibrator vibrator;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        music = MediaPlayer.create(this, R.raw.bit);
        music.start();
        music.setLooping(true);
        //
        Display display = getWindowManager().getDefaultDisplay();
        Point size;
        size = new Point();
        display.getSize(size);
        maxX = size.x;
        maxY = size.y;
        //
        GameView gameView = new GameView(this);
        setContentView(R.layout.activity_game);
        ConstraintLayout gameLayout = findViewById(R.id.gameLayout);
        gameLayout.addView(gameView);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        escButt = findViewById(R.id.esc);
        escButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeActivity.addEntry((int)GameView.getTIME(),(int)GameView.getSCORE());
                GameView.gameThread.resume();
                finish();
            }
        });
    }
    @Override
    protected void onPause() {
        WelcomeActivity.addEntry((int)GameView.getTIME(),(int)GameView.getSCORE());
        super.onPause();
        music.pause();
    }
}
