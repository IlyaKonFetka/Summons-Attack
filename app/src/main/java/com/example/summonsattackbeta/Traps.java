package com.example.summonsattackbeta;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

class Laser {
    Paint paintForBigRect;
    Paint paintForSmallRect;
    Rect hitBox;
    double positionX;
    double positionY;
    boolean isVertical; //true = ↑ | false = →

    double startTime;
    static double LIFE_TIME_MAX_VALUE = 2000;
    static double LIFE_TIME = 2000;
    double lifeTime = LIFE_TIME;
    double size = Math.random() * 50 + 150;
    double smallSize;

    //
    static double damage = 10;
    //

    public Laser(double realTime, Canvas canvas) {
        this.startTime = realTime;
        this.isVertical = new Random().nextBoolean();
        if (isVertical) { //↑
            this.positionX = Math.random() * (canvas.getWidth() - size);
            this.positionY = canvas.getHeight();
            this.hitBox = new Rect((int) positionX, 0, (int) (positionX + size), (int) positionY);
        } else {          //→
            this.positionX = 0;
            this.positionY = Math.random() * (canvas.getHeight() - size) + size;
            this.hitBox = new Rect(0, (int) (positionY - size), canvas.getWidth(), (int) positionY);
        }

        this.paintForBigRect = new Paint();
        this.paintForBigRect.setAntiAlias(true);
        this.paintForBigRect.setColor(Color.argb(50, 102, 0, 255));

        this.paintForSmallRect = new Paint();
        this.paintForSmallRect.setAntiAlias(true);
        this.paintForSmallRect.setColor(Color.argb(220, 102, 0, 255));
    }

    public void update(Canvas canvas, double rotateX, double rotateY, double realTime) {
        smallSize = (realTime - startTime) / lifeTime * size;
        if (isVertical) { //↑
            positionX -= rotateX;
            hitBox = new Rect((int) positionX, 0, (int) (positionX + size), (int) positionY);
            canvas.drawRect(hitBox, paintForBigRect);
            canvas.drawRect((float) (positionX + (size - smallSize) / 2.0),
                    0,
                    (float) (positionX + (size + smallSize) / 2.0),
                    (float) positionY,
                    paintForSmallRect);
        } else {          //→
            positionY -= rotateY;
            hitBox = new Rect(0, (int) (positionY - size), canvas.getWidth(), (int) positionY);
            canvas.drawRect(hitBox, paintForBigRect);
            canvas.drawRect(0,
                    (float) (positionY - (size + smallSize) / 2.0),
                    canvas.getWidth(),
                    (float) (positionY - (size - smallSize) / 2.0),
                    paintForSmallRect);
        }
    }
}

class Bomb {
    Paint paintForBigRect;
    Paint paintForSmallRect;
    Rect hitBox;
    double positionX;
    double positionY;

    double startTime;
    static double LIFE_TIME_MAX_VALUE = 1500;
    static double LIFE_TIME = 1500;
    double lifeTime = LIFE_TIME;
    double size = Math.random() * 100 + 150;
    double smallSize;

    //
    static double damage = 15;
    //

    public Bomb(double realTime, Canvas canvas) {
        this.startTime = realTime;
        this.positionX = Math.random() * (canvas.getWidth() - size);
        this.positionY = Math.random() * (canvas.getWidth() - size) + size;
        this.hitBox = new Rect((int) positionX, (int) (positionY - size), (int) (positionX + size), (int) positionY);

        this.paintForBigRect = new Paint();
        this.paintForBigRect.setAntiAlias(true);
        this.paintForBigRect.setColor(Color.argb(50, 255, 3, 62));

        this.paintForSmallRect = new Paint();
        this.paintForSmallRect.setAntiAlias(true);
        this.paintForSmallRect.setColor(Color.argb(220, 255, 3, 62));
    }

    public void update(Canvas canvas, double rotateX, double rotateY, double realTime) {
        positionX -= rotateX;
        positionY -= rotateY;
        hitBox = new Rect((int) positionX, (int) (positionY - size), (int) (positionX + size), (int) positionY);
        canvas.drawRect(hitBox, paintForBigRect);
        smallSize = (realTime - startTime) / lifeTime * size;
        canvas.drawRect((float) (positionX + (size - smallSize) / 2.0),
                (float) (positionY - (size + smallSize) / 2.0),
                (float) (positionX + (size + smallSize) / 2.0),
                (float) (positionY - (size - smallSize) / 2.0),
                paintForSmallRect);
    }
}
