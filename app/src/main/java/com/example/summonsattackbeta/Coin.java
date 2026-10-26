package com.example.summonsattackbeta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

@SuppressLint("ViewConstructor")
public class Coin extends View {
    float positionX;
    float positionY;
    Paint paintForBitmaps;

    static Bitmap bitmap;
    static Bitmap[] bitmaps;
    Rect hitBox;
    int spaceOf = 3;

    int framesColumn;
    int framesLine;
    double frameWidth;
    double frameHeight;
    int cntOfFrameColumns = 10;
    int cntOfFrameLines = 6;
    static int cntOfFrames;
    int currentNumberOfFrame;

    int startLifeTime;
    static final int LIFE_TIME = 20000;
    public static final double CONST_SPAWN_KD = 1000;
    public static double spawn_KD;
    public static int prev_spawn_time = 1000;

    public Coin(Context context, float spawnX, float spawnY, int startLifeTime) {
        super(context);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.coin);
        bitmaps = Methods.splitBitmap(bitmap, cntOfFrameColumns, cntOfFrameLines);
        cntOfFrames = cntOfFrameColumns * cntOfFrameLines;
        this.currentNumberOfFrame = 0;

        this.frameWidth = bitmap.getWidth() / cntOfFrameColumns;
        this.frameHeight = bitmap.getHeight() / cntOfFrameLines;

        this.startLifeTime = startLifeTime;
        this.positionX = (float) (spawnX - frameWidth / 2.0);
        this.positionY = (float) (spawnY - frameHeight / 2.0);

        this.paintForBitmaps = new Paint();
        this.paintForBitmaps.setAntiAlias(true);
    }

    public void update(Canvas canvas, MainPerson mainPerson, double rotateX, double rotateY) {
        positionX -= rotateX;
        positionY -= rotateY;
        hitBox = new Rect((int) positionX + spaceOf, (int) positionY + spaceOf, (int) (positionX + frameWidth) - spaceOf, (int) (positionY + frameHeight) - spaceOf);
        canvas.drawBitmap(bitmaps[currentNumberOfFrame], positionX, positionY, paintForBitmaps);
        currentNumberOfFrame = (currentNumberOfFrame + 1) % cntOfFrames;
        framesColumn = (framesColumn + 1) % cntOfFrameColumns;
        if (framesColumn == 0) framesLine = (framesLine + 1) % cntOfFrameLines;
    }
}
