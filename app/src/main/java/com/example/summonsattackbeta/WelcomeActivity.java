package com.example.summonsattackbeta;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Display;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
░██████╗██╗░░░██╗███╗░░░███╗███╗░░░███╗░█████╗░███╗░░██╗░██████╗  ░█████╗░████████╗████████╗░█████╗░░█████╗░██╗░░██╗
██╔════╝██║░░░██║████╗░████║████╗░████║██╔══██╗████╗░██║██╔════╝  ██╔══██╗╚══██╔══╝╚══██╔══╝██╔══██╗██╔══██╗██║░██╔╝
╚█████╗░██║░░░██║██╔████╔██║██╔████╔██║██║░░██║██╔██╗██║╚█████╗░  ███████║░░░██║░░░░░░██║░░░███████║██║░░╚═╝█████═╝░
░╚═══██╗██║░░░██║██║╚██╔╝██║██║╚██╔╝██║██║░░██║██║╚████║░╚═══██╗  ██╔══██║░░░██║░░░░░░██║░░░██╔══██║██║░░██╗██╔═██╗░
██████╔╝╚██████╔╝██║░╚═╝░██║██║░╚═╝░██║╚█████╔╝██║░╚███║██████╔╝  ██║░░██║░░░██║░░░░░░██║░░░██║░░██║╚█████╔╝██║░╚██╗
╚═════╝░░╚═════╝░╚═╝░░░░░╚═╝╚═╝░░░░░╚═╝░╚════╝░╚═╝░░╚══╝╚═════╝░  ╚═╝░░╚═╝░░░╚═╝░░░░░░╚═╝░░░╚═╝░░╚═╝░╚════╝░╚═╝░░╚═╝
!!Designed by Ilya & Rinat together with Google, California
*/
public class WelcomeActivity extends AppCompatActivity {
    TextView a1;
    TextView b1;
    TextView c1;

    TextView a2;
    TextView b2;
    TextView c2;

    TextView a3;
    TextView b3;
    TextView c3;

    TextView A4;
    TextView B4;
    TextView C4;

    ImageButton startButton;
    ImageButton infoButton;

    ToggleButton toggleMusic;
    ToggleButton toggleSound;
    ToggleButton toggleSystemInfo;
    static Boolean isMusic = true;
    static Boolean isSound = true;
    static Boolean isShowSystemInfo = false;

    static float maxX, maxY;
    static boolean firstEnter;
    static Vibrator vibrator;

    static ProgressDataBaseDDD DBHelper;
    static SQLiteDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        a1 = findViewById(R.id.a1);
        b1 = findViewById(R.id.b1);
        c1 = findViewById(R.id.c1);

        a2 = findViewById(R.id.a2);
        b2 = findViewById(R.id.b2);
        c2 = findViewById(R.id.c2);

        a3 = findViewById(R.id.a3);
        b3 = findViewById(R.id.b3);
        c3 = findViewById(R.id.c3);

        A4 = findViewById(R.id.A);
        B4 = findViewById(R.id.B);
        C4 = findViewById(R.id.C);

        toggleMusic = findViewById(R.id.toggleMusic);
        toggleSound = findViewById(R.id.toggleSounds);
        toggleSystemInfo = findViewById(R.id.toggleSystemInfo);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        //
        Display display = getWindowManager().getDefaultDisplay();
        Point size;
        size = new Point();
        display.getSize(size);
        maxX = size.x;
        maxY = size.y;
        //
        DBHelper = new ProgressDataBaseDDD(this);
        database = DBHelper.getWritableDatabase();
        showDataBase();

        startButton = findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, GameActivity.class));
            }
        });

        infoButton = findViewById(R.id.info);
        infoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, InformationActivity.class));
            }
        });

        toggleMusic.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isMusic = isChecked;
                if (!isChecked) vibrator.vibrate(15);
            }
        });
        toggleSound.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isSound = isChecked;
                if (!isChecked) vibrator.vibrate(15);
            }
        });
        toggleSystemInfo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isShowSystemInfo = isChecked;
                if (!isChecked) vibrator.vibrate(15);
            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        showDataBase();
    }

    @SuppressLint("Recycle")
    public void showDataBase() {
        Cursor cursor = database.query(DBHelper.TABLE_NAME,null,null,null,null,null,null);
        if(cursor.moveToLast()){
            int idDate = cursor.getColumnIndex(DBHelper.COLUMN_DATE);
            int idTime = cursor.getColumnIndex(DBHelper.COLUMN_TIME);
            int idScore = cursor.getColumnIndex(DBHelper.COLUMN_SCORE);
            String score = cursor.getString(idScore);
            a3.setText(score);
            String time = cursor.getInt(idTime)/60 + "m" + cursor.getInt(idTime)%60 + "s";
            b3.setText(time);
            char[] date = cursor.getString(idDate).toCharArray();
            String strDate = date[8] +""+ date[9] + "." + date[5] +""+ date[6] + "." + date[2] +""+ date[3];
            c3.setText(strDate);

            cursor = database.query(DBHelper.TABLE_NAME,new String[]{DBHelper.COLUMN_DATE, DBHelper.COLUMN_TIME, "MAX(Score) as Score"},null,null,null,null,null);
            cursor.moveToFirst();
            idDate = cursor.getColumnIndex(DBHelper.COLUMN_DATE);
            idTime = cursor.getColumnIndex(DBHelper.COLUMN_TIME);
            idScore = cursor.getColumnIndex(DBHelper.COLUMN_SCORE);
            score = cursor.getString(idScore);
            time = cursor.getInt(idTime)/60 + "m" + cursor.getInt(idTime)%60 + "s";
            date = cursor.getString(idDate).toCharArray();
            strDate = date[8] +""+ date[9] + "." + date[5] +""+ date[6] + "." + date[2] +""+ date[3];

            A4.setText(score+"");
            B4.setText(time+"");
            C4.setText(strDate+"");

            cursor = database.query(DBHelper.TABLE_NAME,null,null,null,null,null,null);
            cursor.moveToLast();
        }
        if(cursor.moveToPrevious()){
            int idDate = cursor.getColumnIndex(DBHelper.COLUMN_DATE);
            int idTime = cursor.getColumnIndex(DBHelper.COLUMN_TIME);
            int idScore = cursor.getColumnIndex(DBHelper.COLUMN_SCORE);
            String score = cursor.getString(idScore);
            a2.setText(score);
            String time = cursor.getInt(idTime)/60 + "m" + cursor.getInt(idTime)%60 + "s";
            b2.setText(time);
            char[] date = cursor.getString(idDate).toCharArray();
            String strDate = date[8] +""+ date[9] + "." + date[5] +""+ date[6] + "." + date[2] +""+ date[3];
            c2.setText(strDate);
        }
        if(cursor.moveToPrevious()){
            int idDate = cursor.getColumnIndex(DBHelper.COLUMN_DATE);
            int idTime = cursor.getColumnIndex(DBHelper.COLUMN_TIME);
            int idScore = cursor.getColumnIndex(DBHelper.COLUMN_SCORE);
            String score = cursor.getString(idScore);
            a1.setText(score);
            String time = cursor.getInt(idTime)/60 + "m" + cursor.getInt(idTime)%60 + "s";
            b1.setText(time);
            char[] date = cursor.getString(idDate).toCharArray();
            String strDate = date[8] +""+ date[9] + "." + date[5] +""+ date[6] + "." + date[2] +""+ date[3];
            c1.setText(strDate);
        }
    }

    public static void addEntry(int TIME, int SCORE){
        Date date = new Date();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        String stringDate = dateFormat.format(date) + "T" + timeFormat.format(date);
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COLUMN_DATE, stringDate);
        contentValues.put(DBHelper.COLUMN_TIME, TIME);
        contentValues.put(DBHelper.COLUMN_SCORE, SCORE);
        database.insert(DBHelper.TABLE_NAME, null, contentValues);
    }
}
