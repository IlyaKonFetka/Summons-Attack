package com.example.summonsattackbeta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class LoseActivity extends AppCompatActivity {
    ImageButton exitButt;
    TextView scoreTV;
    TextView timeTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose);

        exitButt = findViewById(R.id.exit);
        scoreTV = findViewById(R.id.score_text);
        timeTV = findViewById(R.id.time_text);

        int time = getIntent().getIntExtra("time", 0);
        int score = getIntent().getIntExtra("score", 0);
        timeTV.setText(timeTV.getText().toString() + time / 60 + "m" + time % 60 + "s");
        scoreTV.setText(scoreTV.getText().toString() + score);

        exitButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
