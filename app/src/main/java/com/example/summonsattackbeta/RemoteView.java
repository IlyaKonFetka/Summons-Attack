package com.example.summonsattackbeta;


import static android.graphics.Color.BLUE;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import androidx.annotation.Nullable;

public class RemoteView extends View {
    Paint backPaint = new Paint();// Кисть фона
    Paint bubblePaint = new Paint();// Пузырьковая кисть
    Paint rectfPaint = new Paint();

    float backX, backY;

    float bubbleX, bubbleY;

    int radiusBack = 200, radiusBubble = 100;

    RectF mRectF;

    Context mContext;

    String orientation="STOP";

    public RemoteView(Context context, float x, float y//, @Nullable AttributeSet attrs
    ) {
        super(context//, attrs
        );
        this.backX = x;
        this.backY = y;
        bubbleX = backX;
        bubbleY = backY;
        mRectF = new RectF(backX-radiusBack,backY-radiusBack,backX+radiusBack,backY+radiusBack);
        this.mContext = context;
    }
    public RemoteView(Context context//, @Nullable AttributeSet attrs
    ) {
        super(context//, attrs
        );
        backX = 600;
        backY = 600;
        bubbleX = backX;
        bubbleY = backY;
        mRectF = new RectF(backX-radiusBack,backY-radiusBack,backX+radiusBack,backY+radiusBack);
        this.mContext = context;
    }

    public void onJoystick(Canvas canvas) {

        initPaint();

        backPaint.setColor(Color.argb(70,0,0,0));
        bubblePaint.setColor(Color.argb(100,0,0,0));
        rectfPaint.setColor(Color.argb(150,0,0,0));

        //canvas.drawARGB(250, 127, 199, 255);
        canvas.drawCircle(backX, backY, radiusBack, backPaint);

        switch (orientation) {
            case "GO":
                canvas.drawArc(mRectF, -45, -90, true, rectfPaint);
                break;
            case "RETURN":
                canvas.drawArc(mRectF, 45, 90, true, rectfPaint);
                break;
            case "LEFT":
                canvas.drawArc(mRectF, 135, 90, true, rectfPaint);
                break;
            case "RIGHT":
                canvas.drawArc(mRectF, -45, 90, true, rectfPaint);
                break;
            case "STOP":
                rectfPaint.setAlpha(0);
                canvas.drawArc(mRectF, -90, 360, true, rectfPaint);
                break;
            default:
                break;
        }

        canvas.drawCircle(bubbleX, bubbleY, radiusBubble, bubblePaint);

    }

    private void initPaint() {
        backPaint.setAntiAlias(true);
        backPaint.setColor(Color.parseColor("#60ffffff"));

        bubblePaint.setAntiAlias(true);
        bubblePaint.setColor(Color.parseColor("#90ffffff"));

        rectfPaint.setAntiAlias(true);
        rectfPaint.setColor(Color.parseColor("#ffffff"));
        rectfPaint.setAlpha(144);
    }


    public boolean joyTouchEvent(MotionEvent event, int index) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
                float x = (int) event.getX(index);
                float y = (int) event.getY(index);

                if (getDistance(x, y, backX, backY) < radiusBack) {
                    bubbleX = x;
                    bubbleY = y;
                    getOrientation(x,y);
                } else if (getDistance(x, y, backX, backY) >= radiusBack) {
                    float[] xAndy;
                    xAndy = getXY(x, y, backX, backY, getDistance(x, y, backX, backY));
                    bubbleX = xAndy[0];
                    bubbleY = xAndy[1];
                    getOrientation(x,y);
                }
                break;
            default:
                bubbleX = backX;
                bubbleY = backY;
                orientation="STOP";
                break;
        }
        invalidate();
        return true;
    }
    public boolean joyTouchEvent(MotionEvent event) {
        Log.d("logg","detected");

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float x = (int) event.getX();
                float y = (int) event.getY();

                if (getDistance(x, y, backX, backY) < radiusBack) {
                    bubbleX = x;
                    bubbleY = y;
                    getOrientation(x,y);
                } else if (getDistance(x, y, backX, backY) >= radiusBack) {
                    float[] xAndy;
                    xAndy = getXY(x, y, backX, backY, getDistance(x, y, backX, backY));
                    bubbleX = xAndy[0];
                    bubbleY = xAndy[1];
                    getOrientation(x,y);
                }
                break;
            case MotionEvent.ACTION_UP:
                bubbleX = backX;
                bubbleY = backY;
                orientation="STOP";
                break;
        }
        invalidate();
        return true;
    }

    public double ratioX() {
        switch (orientation) {
            case "STOP":
                return 0;
            default:
                double deltaX = bubbleX - backX;
                double deltaY = bubbleY - backY;
                double angle = Math.atan2(deltaY, deltaX);
                return Math.cos(angle);
        }
    }
    public double ratioY(){
        switch (orientation) {
            case "STOP":
                return 0;
            default:
                double deltaX = bubbleX - backX;
                double deltaY = bubbleY - backY;
                double angle = Math.atan2(deltaY, deltaX);
                return Math.sin(angle);
        }
    }

    private float getDistance(float x1, float y1, float x2, float y2) {
        float dis;
        dis = (float) Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
        return dis;
    }
    private float[] getXY(float x1, float y1, float x2, float y2, float dis) {
        float[] xAndy = new float[2];
        float scaleDis;
        float xDis;
        float yDis;


        /**
         * Значения в первом квадранте
         */
        if (x1 > x2 && y1 < y2) {
            scaleDis = radiusBack / dis;
            xDis = Math.abs(x1 - x2);
            yDis = Math.abs(y1 - y2);
            xAndy[0] = x2 + xDis * scaleDis;
            xAndy[1] = y2 - yDis * scaleDis;

        }
        /**
         * Значения во втором квадранте
         */
        else if (x1 < x2 && y1 < y2) {
            scaleDis = radiusBack / dis;
            xDis = Math.abs(x1 - x2);
            yDis = Math.abs(y1 - y2);
            xAndy[0] = x2 - xDis * scaleDis;
            xAndy[1] = y2 - yDis * scaleDis;
        }
        /**
         * Указано в третьем квадранте
         */
        else if (x1 < x2 && y1 > y2) {
            scaleDis = radiusBack / dis;
            xDis = Math.abs(x1 - x2);
            yDis = Math.abs(y1 - y2);
            xAndy[0] = x2 - xDis * scaleDis;
            xAndy[1] = y2 + yDis * scaleDis;
        }

        /**
         * Средние значения в четвертом квадранте
         */
        else if (x1 > x2 && y1 > y2) {
            scaleDis = radiusBack / dis;
            xDis = Math.abs(x1 - x2);
            yDis = Math.abs(y1 - y2);
            xAndy[0] = x2 + xDis * scaleDis;
            xAndy[1] = y2 + yDis * scaleDis;
        }

        /**
         * Угол равен нулю градусов
         */
        else if (x1 > x2 && y1 == y2) {
            xAndy[0] = x2 + radiusBack;
            xAndy[1] = y2;
        }

        /**
         * Угол 90 градусов
         */
        else if (x1 == x2 && y1 < y2) {
            xAndy[0] = x2;
            xAndy[1] = y2 - radiusBack;
        }

        /**
         * Угол 180 градусов
         */
        else if (x1 < x2 && y1 == y2) {
            xAndy[0] = x2 - radiusBack;
            xAndy[1] = y2;
        }

        /**
         * Представлено как 270 градусов
         */
        else if (x1 == x2 && y1 > y2) {
            xAndy[0] = x2;
            xAndy[1] = y2 + radiusBack;
        }
        return xAndy;
    }
    private void getOrientation(float x,float y){
        //y<backY&&(x<backX+backX*0.707&&x>backY-backY*0.707)
        float xx = x - backX;
        float yy = backY - y;
        if (Math.abs(xx/yy)<1&&yy>0){
            orientation = "GO";
        }else if (Math.abs(yy/xx)<1&&xx>0){
            orientation="RIGHT";
        }else if (Math.abs(xx/yy)<1&&yy<0){
            orientation="RETURN";
        }else if (Math.abs(yy/xx)<1&&xx<0){
            orientation="LEFT";
        }else {
            orientation="STOP";
        }

    }
}