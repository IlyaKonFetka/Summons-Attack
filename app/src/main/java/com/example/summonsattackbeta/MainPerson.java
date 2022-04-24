package com.example.summonsattackbeta;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class MainPerson extends View {

    double positionX;
    double positionY;

    public Paint paintForBitmaps;

    Bitmap bitmap;
    Bitmap bulletBitmap;

    Rect destination;
    Rect hitBox;
    final int spaceOf = 5;

    int framesColumn;
    int framesLine;
    double frameWidth;
    double frameHeight;
    int cntOfFrameColumns;
    int cntOfFrameLines;

    long startAttackTime;
    long stopAttackTime;
    long maxAttackTime;
    long KD_AttackTime;

    int bulletX;
    int bulletY;

    double bulletAngle;
    double bulletWidth;
    double bulletHeight;
    double bulletVelocity;
    boolean isAttack;
    Enemy nearbyEnemy;

    //Characteristics
    double HP;
    double velocity;
    float attackRange;

    public MainPerson(Context context) {
        super(context);
        this.HP = 1.0;
        this.attackRange = 500;
        this.velocity = 12;

        this.startAttackTime = 0;
        this.stopAttackTime = 0;
        this.maxAttackTime = 2000;
        this.KD_AttackTime = 1000;

        this.bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.norm_privedenie_60);
        this.cntOfFrameColumns = 10;
        this.cntOfFrameLines = 6;

        this.frameWidth = (float)bitmap.getWidth()/cntOfFrameColumns;
        this.frameHeight = (float)bitmap.getHeight()/cntOfFrameLines;
        this.positionX = 800;
        this.positionY = 300;


        this.bulletBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bullet);
        this.bulletX = 0;
        this.bulletY = 0;
        this.bulletWidth = bulletBitmap.getWidth();
        this.bulletHeight = bulletBitmap.getHeight();
        this.bulletVelocity = 20;

        this.paintForBitmaps = new Paint();
        this.paintForBitmaps.setAntiAlias(true);
    }

    public void update(Canvas canvas, double X, double Y){
        positionX = (int) X;
        positionY = (int) Y;
        destination = new Rect((int)positionX, (int)positionY, (int)(positionX + frameWidth), (int)(positionY + frameHeight));
        hitBox = new Rect((int)positionX + spaceOf, (int)positionY + spaceOf, (int)(positionX + frameWidth) - spaceOf, (int)(positionY + frameHeight) - spaceOf);
        canvas.drawBitmap(bitmap, new Rect((int)frameWidth* framesColumn,(int)frameHeight* framesLine,(int)frameWidth* framesColumn +(int)frameWidth,(int)frameHeight* framesLine +(int)frameHeight), destination,  paintForBitmaps);
        framesColumn = (framesColumn +1)%cntOfFrameColumns;
        if(framesColumn ==0) framesLine = (framesLine +1)%cntOfFrameLines;
    }
    public void simpleAttack(Canvas canvas, double ratioX, double ratioY){
        bulletX -= ((bulletVelocity*60.0/(float)GameView.FPS)*Math.cos(this.bulletAngle) + (this.velocity*60.0/(float)GameView.FPS) * ratioX);
        bulletY -= ((bulletVelocity*60.0/(float)GameView.FPS)*Math.sin(this.bulletAngle) + (this.velocity*60.0/(float)GameView.FPS) * ratioY);

        canvas.drawBitmap(bulletBitmap, bulletX, bulletY,  paintForBitmaps);
    }
}