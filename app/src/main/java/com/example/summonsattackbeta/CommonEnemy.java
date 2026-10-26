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
public class CommonEnemy extends View implements Enemy {
    double positionX;
    double positionY;
    double wayX;
    double wayY;
    static Paint paintForBitmaps;

    static Bitmap bitmap;
    static Bitmap[] bitmaps;
    Rect destination;
    Rect hitBox;
    final int spaceOf = 4;

    int framesColumn;
    int framesLine;
    double frameWidth;
    double frameHeight;
    static int cntOfFrameColumns = 5;
    static int cntOfFrameLines = 3;
    static int cntOfFrames;
    int currentNumberOfFrame;

    static double velocity = 6;
    static double KD = 500;
    static double damage = 10;
    public static final double CONST_SPAWN_KD = 5000;
    static double SPAWN_KD;

    final double HP_MAX_VALUE = 15;
    long previousAttackTime = 0;
    private double HP = HP_MAX_VALUE;

    public CommonEnemy(Context context, float spawnX, float spawnY) {
        super(context);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.common_enemy);
        bitmaps = Methods.splitBitmap(bitmap, cntOfFrameColumns, cntOfFrameLines);
        cntOfFrames = cntOfFrameColumns * cntOfFrameLines;
        this.currentNumberOfFrame = 0;

        this.frameWidth = bitmap.getWidth() / cntOfFrameColumns;
        this.frameHeight = bitmap.getHeight() / cntOfFrameLines;

        this.positionX = spawnX - frameWidth / 2.0;
        this.positionY = spawnY - frameHeight / 2.0;

        paintForBitmaps = new Paint();
        paintForBitmaps.setAntiAlias(true);
    }

    @Override
    public void update(Canvas canvas, MainPerson mainPerson, double rotateX, double rotateY) {
        wayX = this.positionX + this.frameWidth / 2.0 - (mainPerson.positionX + mainPerson.frameWidth / 2.0);
        wayY = this.positionY + this.frameHeight / 2.0 - (mainPerson.positionY + mainPerson.frameHeight / 2.0);

        double v = Math.abs(wayX) + 0.000000001d + Math.abs(wayY);

        positionX -= (velocity * 60.0 / (float) GameView.averageFPS) * (wayX / v) + rotateX;
        positionY -= (velocity * 60.0 / (float) GameView.averageFPS) * (wayY / v) + rotateY;

        hitBox = new Rect((int) positionX + spaceOf, (int) positionY + spaceOf, (int) (positionX + frameWidth) - spaceOf, (int) (positionY + frameHeight) - spaceOf);
        canvas.drawBitmap(bitmaps[currentNumberOfFrame], (float) positionX, (float) positionY, paintForBitmaps);
        currentNumberOfFrame = (currentNumberOfFrame + 1) % cntOfFrames;
    }

    @Override
    public boolean checkForAttack(MainPerson mainPerson) {
        return this.hitBox.intersect(mainPerson.hitBox);
    }

    @Override
    public double getDistanceFor() {
        return Math.sqrt(wayX * wayX + wayY * wayY);
    }

    @Override
    public double getAngleFor() {
        return Math.atan2(wayY, wayX);
    }

    @NonNull
    @Override
    public String toString() {
        return "CommonEnemy{" +
                "positionX=" + positionX +
                ", positionY=" + positionY +
                ", wayX=" + wayX +
                ", wayY=" + wayY +
                ", destination=" + destination +
                '}';
    }

    public void takeDamage(double damage) {
        HP -= damage;
    }

    public double getHP() {
        return HP;
    }
}
