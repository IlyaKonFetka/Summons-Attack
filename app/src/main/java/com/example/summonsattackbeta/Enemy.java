package com.example.summonsattackbeta;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;

@SuppressLint("ViewConstructor")
public class Enemy extends View {
    double positionX;
    double positionY;
    public double wayX;
    public double wayY;
    Paint paintForBitmaps;


    Bitmap bitmap;
    Rect destination;
    int framesColumn;
    int framesLine;
    double frameWidth;
    double frameHeight;
    int cntOfFrameColumns;
    int cntOfFrameLines;

    static double velocity = 6;
    static int KD = 500;
    static double damage = 0.1;
    static int SPAWN_KD = 1000;

    long previousAttackTime = 0;

    public Enemy(Context context, float spawnX, float spawnY) {
        super(context);
        this.bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.enemyartweavertwo);
        this.cntOfFrameColumns = 5;
        this.cntOfFrameLines = 3;
        this.frameWidth = bitmap.getWidth() / (double)cntOfFrameColumns;
        this.frameHeight = bitmap.getHeight() / (double)cntOfFrameLines;

        this.positionX = spawnX - frameWidth/2.0;
        this.positionY = spawnY - frameHeight/2.0;

        this.paintForBitmaps = new Paint();
        this.paintForBitmaps.setAntiAlias(true);
    }

    public void update(Canvas canvas, MainPerson mainPerson, double ratioX, double ratioY){

        wayX = this.positionX + this.frameWidth/2.0 - (mainPerson.positionX + mainPerson.frameWidth/2.0);
        wayY = this.positionY + this.frameHeight/2.0 - (mainPerson.positionY + mainPerson.frameHeight/2.0);


        double v = 0.000000001d + Math.abs(wayX) + Math.abs(wayY);

        positionX -= ((velocity*60.0/(float)GameView.FPS)*(wayX / v) + (mainPerson.velocity*60.0/(float)GameView.FPS) * ratioX) + Math.random()*20-10;
        positionY -= ((velocity*60.0/(float)GameView.FPS)*(wayY / v) + (mainPerson.velocity*60.0/(float)GameView.FPS) * ratioY) + Math.random()*20-10;

        destination = new Rect((int)positionX, (int)positionY, (int)(positionX + frameWidth), (int)(positionY + frameHeight));
        canvas.drawBitmap(bitmap, new Rect(
                (int)(frameWidth* framesColumn),
                (int)(frameHeight* framesLine),
                (int)(frameWidth* framesColumn +frameWidth),
                (int)(frameHeight* framesLine +frameHeight)), destination,  paintForBitmaps);
        framesColumn = (framesColumn +1)%cntOfFrameColumns;
        if(framesColumn ==0) framesLine = (framesLine +1)%cntOfFrameLines;
    }
    public boolean checkForAttack(MainPerson mainPerson){
        return this.destination.intersect(mainPerson.hitBox);
    }

    @NonNull
    @Override
    public String toString() {
        return "Enemy{" +
                "positionX=" + positionX +
                ", positionY=" + positionY +
                ", wayX=" + wayX +
                ", wayY=" + wayY +
                ", destination=" + destination +
                '}';
    }
}
