package com.example.summonsattackbeta;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class Traps {
//
//
}
class Laser extends Traps{
    Paint smallLaserPaint;
    Paint bigLaserPaint;
    int size;
    int distance;
    boolean route; //true = ↑ | false = →
    final float inProportion = (float)1;
    boolean isCreate;
    long timeStart;

    //
    static double damage = 0.1;
    static int time = 2000;
    //

    public Laser() {
        this.isCreate = false;

        this.smallLaserPaint = new Paint();
        this.bigLaserPaint = new Paint();

        this.smallLaserPaint.setAntiAlias(true);
        this.smallLaserPaint.setColor(Color.argb(50, 20,0,150));
        this.smallLaserPaint.setStyle(Paint.Style.FILL);
        this.bigLaserPaint.setAntiAlias(true);
        this.bigLaserPaint.setColor(Color.argb(200, 150,0,150));
        this.bigLaserPaint.setStyle(Paint.Style.FILL);

    }
    public void createLaser(Canvas canvas, long rTime){
        this.isCreate = true;
        this.timeStart = rTime;

        this.size = (int)(Math.random()*100)+100;
        this.route = Math.random() > 0.5;
        if(route){
            this.distance = (int)(canvas.getWidth()*Math.random());
        }
        if(!route){
            this.distance = (int)(canvas.getHeight()*Math.random());
        }
    }

    public void destroyLaser(){
        this.isCreate = false;
    }
    public void spawnLaser(Canvas canvas, int scX, int scY, long rTime, double ratioX, double ratioY, MainPerson mp){
        if (route){ //↑
            distance -= (mp.velocity*60.0/(float)GameView.FPS) * ratioX;
            canvas.drawRect(distance, 0, distance+size, scY, smallLaserPaint);
            canvas.drawRect(distance+size/2.0f-(inProportion/2*size)*(rTime-timeStart)/time,
                    0,
                    distance+size/2.0f+(inProportion/2*size)*(rTime-timeStart)/time,
                    scY,
                    bigLaserPaint);
        }
        else      { //→
            distance -= (mp.velocity*60.0/(float)GameView.FPS) * ratioY;
            canvas.drawRect(0, distance, scX, distance + size, smallLaserPaint);
            canvas.drawRect(0,
                    distance+size/2.0f-(inProportion/2*size)*(rTime-timeStart)/time,
                    scX,
                    distance+size/2.0f+(inProportion/2*size)*(rTime-timeStart)/time,
                    bigLaserPaint);
        }
    }
    public void damageLaser(Canvas canvas, int scX, int scY, long rTime, double ratioX, double ratioY, MainPerson mp){
        Rect r;
        if (route){ //↑
            distance -= (mp.velocity*60.0/(float)GameView.FPS) * ratioX;
            r = new Rect(distance, 0, distance + size, scY);
        }
        else      { //→
            distance -= (mp.velocity*60.0/(float)GameView.FPS) * ratioY;
            r = new Rect(0, distance, scX, distance + size);
        }
        canvas.drawRect(r, bigLaserPaint);
        if (mp.hitBox.intersect(r)){
            GameActivity.vibrator.vibrate(15);
            mp.HP -= damage;
            this.destroyLaser();
        }
    }
}
class Bomb extends Traps{
    Paint smallBombPaint;
    Paint bigBombPaint;
    int size = 150;
    int distanceX;
    int distanceY;
    final float inProportion = (float)1;
    boolean isCreate;
    long timeStart;

    //
    static double damage = 0.2;
    static int time = 2000;
    //

    public Bomb() {
        this.isCreate = false;

        this.smallBombPaint = new Paint();
        this.bigBombPaint = new Paint();

        this.smallBombPaint.setAntiAlias(true);
        this.smallBombPaint.setColor(Color.argb(50, 255,0,0));
        this.smallBombPaint.setStyle(Paint.Style.FILL);
        this.bigBombPaint.setAntiAlias(true);
        this.bigBombPaint.setColor(Color.argb(200, 255,0,0));
        this.bigBombPaint.setStyle(Paint.Style.FILL);

    }
    public void createBomb(Canvas canvas, long rTime){
        this.isCreate = true;
        this.timeStart = rTime;

        this.distanceX = (int)((canvas.getWidth()*Math.random()*1.3)-canvas.getWidth()*0.3);
        this.distanceY = (int)((canvas.getHeight()*Math.random()*1.3)-canvas.getHeight()*0.3);
    }

    public void destroyBomb(){
        this.isCreate = false;
    }

    public void spawnBomb(Canvas canvas, int scX, int scY, long rTime, double ratioX, double ratioY, MainPerson mp){
        distanceX -= ratioX;
        distanceY -= ratioY;
        float delta = (inProportion/2*size)*(rTime-timeStart)/time;
        canvas.drawRect(distanceX, distanceY, distanceX + size, distanceY + size, smallBombPaint);
        canvas.drawRect(distanceX+size/2-delta, distanceY+size/2-delta, distanceX+size/2+delta, distanceY+size/2+delta, bigBombPaint);
    }

    public void damageBomb(Canvas canvas, int scX, int scY, long rTime, double ratioX, double ratioY, MainPerson mp){
        Rect r;
        distanceX -= ratioX;
        distanceY -= ratioY;
        r = new Rect(distanceX, distanceY, distanceX + size, distanceY + size);
        canvas.drawRect(r, bigBombPaint);
        if (mp.destination.intersect(r)){
            GameActivity.vibrator.vibrate(15);
            mp.HP -= damage;
            this.destroyBomb();
        }
    }
}