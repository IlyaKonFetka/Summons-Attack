package com.example.summonsattackbeta;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
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
    TextView textViewAppName;

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
    //Button addButton;
    //Button showButton;
    //Button deleteButton;
    static ProgressDataBaseDDD DBHelper;
    static SQLiteDatabase database;
    @SuppressLint({"Recycle", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
/*
        textViewAppName = findViewById(R.id.nameOfApp);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.i);
        Shader shader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        textViewAppName.getPaint().setShader(shader);
*/
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

        DBHelper = new ProgressDataBaseDDD(this);
        database = DBHelper.getWritableDatabase();

        @SuppressLint("Recycle")
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

            cursor = database.query("Sessions",new String[]{DBHelper.COLUMN_DATE, "MAX(Time) as Time", DBHelper.COLUMN_SCORE},null,null,null,null,null);
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

        /*
        addButton = findViewById(R.id.addDBbutton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                Date date = new Date();
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                contentValues.put(DBHelper.COLUMN_DATE, dateFormat.format(date) + "T" + timeFormat.format(date));
                contentValues.put(DBHelper.COLUMN_TIME, "999");
                contentValues.put(DBHelper.COLUMN_SCORE, "999");
                database.insert(DBHelper.TABLE_NAME, null, contentValues);
            }
        });
        showButton = findViewById(R.id.showDBbutton);
        showButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = database.query(DBHelper.TABLE_NAME,null,null,null,null,null,null);
                if(cursor.moveToFirst()){
                    int idIndex = cursor.getColumnIndex(DBHelper.COLUMN_ID);
                    int idDate = cursor.getColumnIndex(DBHelper.COLUMN_DATE);
                    int idTime = cursor.getColumnIndex(DBHelper.COLUMN_TIME);
                    int idScore = cursor.getColumnIndex(DBHelper.COLUMN_SCORE);
                    do{
                        Log.d("mLog", + cursor.getInt(idIndex) + " " +
                                                  cursor.getString(idDate) + " " +
                                                  cursor.getString(idTime) + " " +
                                                  cursor.getString(idScore) + " "

                                  );
                    }while (cursor.moveToNext());
                    Log.d("mLog", "\n__________________________________________________");
                }else{
                    Log.d("mLog", "netu");
                }
                cursor.close();
            }
        });
        deleteButton = findViewById(R.id.clearDBbutton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.delete(DBHelper.TABLE_NAME,null,null);
            }
        });
        */




        startButton = (ImageButton)findViewById(R.id.startbutton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this, GameActivity.class));
            }

        });
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