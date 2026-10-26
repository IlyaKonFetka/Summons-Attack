package com.example.summonsattackbeta;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

@SuppressLint("ViewConstructor")
public class MainPerson extends View {

    float positionX;
    float positionY;

    public Paint paintForBitmaps;
    public Paint paintForHPBar;
    public Paint paintForHPBarStroke;

    Bitmap bitmap;
    Bitmap[] bitmaps;

    Rect hitBox;
    final int spaceOf = 5;

    float frameWidth;
    float frameHeight;
    int cntOfFrameColumns;
    int cntOfFrameLines;
    int cntOfFrames;
    int currentNumberOfFrame;

    long startAttackTime;
    long stopAttackTime;
    int KD_AttackTime;
    float attackRadius;
    Enemy nearbyEnemy;

    List<Bullet> bulletList;
    float bulletVelocity;
    float bulletRadius;
    float bulletDamage;

    //Characteristics
    final double HP_MAX_VALUE = 100;
    private double HP;
    float velocity;

    public MainPerson(Context context) {
        super(context);
        this.HP = HP_MAX_VALUE;
        this.velocity = 12;

        this.startAttackTime = 0;
        this.stopAttackTime = 0;
        this.attackRadius = WelcomeActivity.maxX * 0.75f;
        this.KD_AttackTime = 800;

        this.cntOfFrameColumns = 10;
        this.cntOfFrameLines = 6;
        this.bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.norm_privedenie_60);
        this.bitmaps = Methods.splitBitmap(bitmap, cntOfFrameColumns, cntOfFrameLines);
        this.cntOfFrames = cntOfFrameColumns * cntOfFrameLines;
        this.currentNumberOfFrame = 0;

        this.frameWidth = bitmap.getWidth() / cntOfFrameColumns;
        this.frameHeight = bitmap.getHeight() / cntOfFrameLines;
        this.positionX = 0;
        this.positionY = 0;

        this.bulletList = new ArrayList<>();
        this.bulletVelocity = 17;
        this.bulletRadius = 12;
        this.bulletDamage = 10;

        this.paintForBitmaps = new Paint();
        this.paintForBitmaps.setAntiAlias(true);
        this.paintForBitmaps.setColor(Color.WHITE);

        this.paintForHPBar = new Paint();
        this.paintForHPBar.setAntiAlias(true);
        this.paintForHPBar.setColor(Color.rgb(0, 153, 0));
        this.paintForHPBar.setStyle(Paint.Style.FILL);

        this.paintForHPBarStroke = new Paint();
        this.paintForHPBarStroke.setAntiAlias(true);
        this.paintForHPBarStroke.setColor(Color.BLACK);
        this.paintForHPBarStroke.setStyle(Paint.Style.STROKE);
    }

    public void update(Canvas canvas, double X, double Y){
        positionX = (int) X;
        positionY = (int) Y;
        hitBox = new Rect((int)positionX + spaceOf, (int)positionY + spaceOf, (int)(positionX + frameWidth) - spaceOf, (int)(positionY + frameHeight) - spaceOf);
        canvas.drawBitmap(bitmaps[currentNumberOfFrame], positionX, positionY, paintForBitmaps);
        currentNumberOfFrame = (currentNumberOfFrame + 1) % cntOfFrames;

        //ПОЛОСКА ЗДОРОВЬЯ
        double percentHP = HP / HP_MAX_VALUE;
        if (percentHP > 0.6) paintForHPBar.setColor(Color.rgb(0, 153, 0));
        else if (percentHP <= 0.6 && percentHP > 0.2) paintForHPBar.setColor(Color.rgb(217, 170, 2));
        else if (percentHP <= 0.2 && percentHP > 0.0) paintForHPBar.setColor(Color.rgb(204, 6, 5));

        canvas.drawRect(positionX,
                positionY + frameHeight,
                (float) (positionX + HP / HP_MAX_VALUE * frameWidth),
                positionY + frameHeight + 10,
                paintForHPBar);
        canvas.drawRect(positionX,
                positionY + frameHeight,
                positionX + frameWidth,
                positionY + frameHeight + 10,
                paintForHPBarStroke);
    }

    public void updateBullets(Canvas canvas, double rotateX, double rotateY, long realTime, boolean enemyListIsEmpty){
        //ЕСЛИ ПРОШЛО КД АТАКИ, НА ПОЛЕ ЕСТЬ ВРАГ И ОН В РАДИУСЕ АТАКИ
        if (realTime - stopAttackTime > KD_AttackTime && !enemyListIsEmpty && nearbyEnemy.getDistanceFor() <= attackRadius) {
            bulletList.add(new Bullet(positionX + frameWidth / 2.0f, positionY + frameHeight / 2.0f,
                    bulletRadius, bulletVelocity, bulletDamage, Color.WHITE, realTime, nearbyEnemy.getAngleFor()));
            stopAttackTime = realTime;
        }
        for (int i = 0; i < bulletList.size(); i++) {
            Bullet currentBullet = bulletList.get(i);
            currentBullet.update(canvas, rotateX, rotateY);
            if (realTime - currentBullet.startTime >= Bullet.lifeTime) {
                bulletList.remove(currentBullet);
                i--;
            }
        }
    }

    public void takeDamage(double damage) {
        HP -= damage;
        GameActivity.vibrator.vibrate(12);
    }

    public double getHP() {
        return HP;
    }
}
