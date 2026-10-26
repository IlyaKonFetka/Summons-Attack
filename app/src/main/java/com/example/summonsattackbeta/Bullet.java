package com.example.summonsattackbeta;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

public class Bullet {
    double centerX;
    double centerY;
    double radius;
    double velocity;
    double damage;
    double angle;
    Paint defaultPaint;
    Rect hitBox;

    long startTime;
    static int lifeTime = 3000;

    //Пуля летит в точку (targetCenterX, targetCenterY)
    public Bullet(double centerX, double centerY, double radius, double velocity, double damage, int color, long realTime, double targetCenterX, double targetCenterY) {
        this.centerX = centerX - radius;
        this.centerY = centerY - radius;
        this.radius = radius;
        this.velocity = velocity;

        this.defaultPaint = new Paint();
        this.defaultPaint.setAntiAlias(true);
        this.defaultPaint.setColor(color);

        this.damage = damage;
        this.angle = Math.atan2(targetCenterY - centerY, targetCenterX - centerX);
        this.startTime = realTime;
    }

    //Пуля летит под углом angle
    public Bullet(double centerX, double centerY, double radius, double velocity, double damage, int color, long realTime, double angle) {
        this.centerX = centerX - radius;
        this.centerY = centerY - radius;
        this.radius = radius;
        this.velocity = velocity;

        this.defaultPaint = new Paint();
        this.defaultPaint.setAntiAlias(true);
        this.defaultPaint.setColor(color);

        this.damage = damage;
        this.angle = angle;
        this.startTime = realTime;
    }

    public void update(Canvas canvas, double rotateX, double rotateY) {
        centerX += (velocity * 60.0 / (float) GameView.averageFPS) * Math.cos(angle) - rotateX;
        centerY += (velocity * 60.0 / (float) GameView.averageFPS) * Math.sin(angle) - rotateY;
        hitBox = new Rect((int) (centerX - radius), (int) (centerY - radius), (int) (centerX + radius), (int) (centerY + radius));
        canvas.drawCircle((float) centerX, (float) centerY, (float) radius, defaultPaint);
    }
}
