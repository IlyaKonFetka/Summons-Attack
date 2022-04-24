package com.example.summonsattackbeta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.LinkedList;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    static GameThread gameThread;
    RemoteView joystickMove;

    Boolean screenTouch;
    float screenX;
    float screenY;
    static MainPerson mainPerson;
    public static int FPS;
    public static double TIME;
    public static double SCORE;

    static ArrayList<Laser> laserList = new ArrayList<>();
    static ArrayList<Enemy> enemyArray = new ArrayList<>();
    static LinkedList<LinkedList<Rect>> linkedBackgroundList;
    static ArrayList<Bomb> bombList = new ArrayList<>();


    float movingX;
    float movingY;
    int cntFiguresOnWidth;
    int cntFiguresOnHeight;
    float interval;
    float figureSide;
    float maxRotateForBackground;
    int rotateX;
    int rotateY;

    public GameView(Context context) {
        super(context);
        screenTouch = false;
        getHolder().addCallback(this);
        mainPerson = new MainPerson(getContext());  /*ONLY ONE*/

        laserList.add(0, new Laser());
        laserList.add(1, new Laser());
        laserList.add(2, new Laser());

        for (int i = 0; i < 3; i++) {
            bombList.add(i, new Bomb());
        }

        interval = GameActivity.maxX / 22.0f;
        figureSide = interval * 6.0f;
        maxRotateForBackground = interval + figureSide;

        int plusLineAndColumn = 1;

        cntFiguresOnWidth = 3 + 2 + plusLineAndColumn * 2;//additional left & right
        cntFiguresOnHeight = (int)(GameActivity.maxY / (interval + figureSide)) + 1 + 2 + plusLineAndColumn * 2;//additional up & down
        linkedBackgroundList = new LinkedList<>();
        for(int i = 0; i < cntFiguresOnHeight; i++){
            LinkedList<Rect>list = new LinkedList<>();
            for(int j = 0; j < cntFiguresOnWidth;j++){
                list.addLast(new Rect((int)(interval + (interval + figureSide) * (j-1-plusLineAndColumn)),
                                      (int)(interval + (interval + figureSide) * (i-1-plusLineAndColumn)),
                                      (int)((interval + figureSide) * (j-plusLineAndColumn)),
                                      (int)((interval + figureSide) * (i-plusLineAndColumn))
                                      )
                );
            }
            linkedBackgroundList.addLast(list);
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        gameThread = new GameThread(surfaceHolder);
        gameThread.start();


        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                joystickMove = new RemoteView(getContext(),event.getX(),event.getY());
                joystickMove.joyTouchEvent(event);
                screenTouch = true;
                break;
            case MotionEvent.ACTION_MOVE:
                joystickMove.joyTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                screenTouch = false;
                break;
        }
        return true;
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @SuppressWarnings("InfiniteLoopStatement")
    class GameThread extends Thread{
        final SurfaceHolder surfaceHolder;
        Paint paintForSystemInfo;
        Paint paintForHP;
        Paint paintForHPBar;
        Paint paintForHPBarStroke;
        Paint paintForBackground;
        int backColor;

        long startTime;  //время начала сессии////////
        long prevTime;   //время предыдущей анимации//
        protected final static int frameTime = 16;//ВРЕМЯ КАДРА//
        long realTime;

        Canvas canvas;

        public int currentFPS;
        long prevFpsTime;
        long prevSpawnTime;

        public GameThread(SurfaceHolder holder) {
            surfaceHolder = holder;

            paintForSystemInfo = new Paint();
            paintForSystemInfo.setAntiAlias(true);
            paintForSystemInfo.setColor(Color.BLUE);
            paintForSystemInfo.setTextSize(50);

            paintForHP = new Paint();
            paintForHP.setAntiAlias(true);
            paintForHP.setColor(Color.RED);
            paintForHP.setTextSize(50);

            paintForHPBar = new Paint();
            paintForHPBar.setAntiAlias(true);
            paintForHPBar.setColor(Color.rgb(0,153,0));
            paintForHPBar.setStyle(Paint.Style.FILL);

            paintForHPBarStroke = new Paint();
            paintForHPBarStroke.setAntiAlias(true);
            paintForHPBarStroke.setColor(Color.BLACK);
            paintForHPBarStroke.setStyle(Paint.Style.STROKE);

            paintForBackground = new Paint();
            paintForBackground.setAntiAlias(true);
            paintForBackground.setStyle(Paint.Style.FILL);

            //BACK COLOR
            paintForBackground.setColor(Color.rgb(42,94,3));

            backColor = Color.rgb(0, 66, 32);
            //
        }
        @RequiresApi(api = Build.VERSION_CODES.R)
        @Override
        public void run() {
            startTime = System.nanoTime()/(long)Math.pow(10 ,6);
            prevTime = startTime;
            prevFpsTime = startTime;
            currentFPS = 0;
            prevSpawnTime = startTime;
            mainPerson.stopAttackTime = startTime + 1000;

            while (true){
                realTime = System.nanoTime()/(long)Math.pow(10 ,6);
                if(realTime - prevTime >= frameTime){
                    canvas = surfaceHolder.lockCanvas();
                    screenX = canvas.getWidth();
                    screenY = canvas.getHeight();
                    synchronized (surfaceHolder) {
                        currentFPS++;
                        if (realTime - prevFpsTime >= 1000) {
                            FPS = currentFPS;
                            currentFPS = 0;
                            prevFpsTime = realTime;
                        }
                        TIME = (System.nanoTime() / Math.pow(10, 6) - startTime) / 1000;
                        canvas.drawColor(backColor);

                        //ИФЫ ДЛЯ УСЛОЖНЕНИЯ
                        if(TIME>5){
                            Enemy.SPAWN_KD = 1000;
                            Laser.time = 1500;
                            Bomb.time = 1500;
                        }else if(TIME>10){
                            Enemy.SPAWN_KD = 900;
                            Laser.time = 1300;
                            Bomb.time = 1300;
                        }else if(TIME>15){
                            Enemy.SPAWN_KD = 800;
                            Laser.time = 1100;
                            Bomb.time = 1100;
                        }else if(TIME>30){
                            Enemy.SPAWN_KD = 700;
                            Laser.time = 1000;
                            Bomb.time = 1000;
                        }else if(TIME>45){
                            Enemy.SPAWN_KD = 600;
                            Laser.time = 800;
                            Bomb.time = 800;
                        }else if(TIME>55){
                            Enemy.SPAWN_KD = 550;
                            Laser.time = 750;
                            Bomb.time = 750;
                        }else if(TIME>65){
                            Enemy.SPAWN_KD = 500;
                            Laser.time = 700;
                            Bomb.time = 700;
                        }else if(TIME>75){
                            Enemy.SPAWN_KD = 500;
                            Laser.time = 600;
                            Bomb.time = 600;
                        }else if(TIME>100){
                            Enemy.SPAWN_KD = 450;
                            Laser.time = 550;
                            Bomb.time = 550;
                        }else if(TIME>110){
                            Enemy.SPAWN_KD = 400;
                            Laser.time = 500;
                            Bomb.time = 500;
                        }else if(TIME>115){
                            Enemy.SPAWN_KD = 350;
                            Laser.time = 450;
                            Bomb.time = 450;
                        }else if(TIME>120){
                            Enemy.SPAWN_KD = 300;
                            Laser.time = 400;
                            Bomb.time = 400;
                        }else if(TIME>130){
                            Enemy.SPAWN_KD = 300;
                            Laser.time = 300;
                            Bomb.time = 300;
                        }else if(TIME>150){
                            Enemy.SPAWN_KD = 200;
                            Laser.time = 200;
                            Bomb.time = 200;
                        }

                        //ДЖОЙСТИК
                        if (!screenTouch) {
                            joystickMove = new RemoteView(getContext(), screenX / 2, getHeight() - 300);
                        }

                        //ФОР ПО ФОНОВЫМ КВАДРАТАМ
                        if (!joystickMove.orientation.equals("STOP")) {
                            rotateX = (int) ((mainPerson.velocity * 60.0 / (float) FPS) * joystickMove.ratioX());
                            rotateY = (int) ((mainPerson.velocity * 60.0 / (float) FPS) * joystickMove.ratioY());
                            movingX += rotateX;
                            movingY += rotateY;
                            if (movingX <= -maxRotateForBackground) {
                                movingX += maxRotateForBackground;
                                for (int i = 0; i < cntFiguresOnHeight; i++) {
                                    LinkedList<Rect>currentLine = linkedBackgroundList.get(i);
                                    currentLine.getLast().left = (currentLine.getFirst().left - (int)maxRotateForBackground);
                                    currentLine.getLast().right = (currentLine.getFirst().right - (int)maxRotateForBackground);
                                    currentLine.addFirst(currentLine.pollLast());
                                }
                            }
                            else if(movingX >= maxRotateForBackground){
                                movingX -= maxRotateForBackground;
                                for (int i = 0; i < cntFiguresOnHeight; i++) {
                                    LinkedList<Rect>currentLine = linkedBackgroundList.get(i);
                                    currentLine.getFirst().left = (currentLine.getLast().left + (int)maxRotateForBackground);
                                    currentLine.getFirst().right = (currentLine.getLast().right + (int)maxRotateForBackground);
                                    currentLine.addLast(currentLine.pollFirst());
                                }
                            }
                            if(movingY <= -maxRotateForBackground){
                                movingY += maxRotateForBackground;
                                LinkedList<Rect>currentLine = linkedBackgroundList.getLast();
                                LinkedList<Rect>firstLine = linkedBackgroundList.getFirst();
                                for (int i = 0; i < cntFiguresOnWidth; i++) {
                                    currentLine.get(i).top = (firstLine.get(i).top - (int)maxRotateForBackground);
                                    currentLine.get(i).bottom = (firstLine.get(i).bottom - (int)maxRotateForBackground);
                                }
                                linkedBackgroundList.addFirst(linkedBackgroundList.pollLast());
                            }
                            else if(movingY >= maxRotateForBackground){
                                movingY -= maxRotateForBackground;
                                LinkedList<Rect>currentLine = linkedBackgroundList.getFirst();
                                LinkedList<Rect>lastLine = linkedBackgroundList.getLast();
                                for (int i = 0; i < cntFiguresOnWidth; i++) {
                                    currentLine.get(i).top = (lastLine.get(i).top + (int)maxRotateForBackground);
                                    currentLine.get(i).bottom = (lastLine.get(i).bottom + (int)maxRotateForBackground);
                                }
                                linkedBackgroundList.addLast(linkedBackgroundList.pollFirst());
                            }
                            for (int i = 0; i < cntFiguresOnHeight; i++) {
                                for (int j = 0; j < cntFiguresOnWidth; j++) {
                                    Rect currentRect = linkedBackgroundList.get(i).get(j);
                                    currentRect.left -= rotateX;
                                    currentRect.top -= rotateY;
                                    currentRect.right -= rotateX;
                                    currentRect.bottom -= rotateY;
                                }
                            }
                        }
                        for(int i = 0; i < cntFiguresOnHeight; i++) {
                            for (int j = 0; j < cntFiguresOnWidth; j++) {
                                canvas.drawRect(linkedBackgroundList.get(i).get(j),paintForBackground);
                            }
                        }

                        joystickMove.onJoystick(canvas);

                        //ГЛАВНЫЙ ПЕРСОНАЖ
                        mainPerson.update(canvas, (screenX - mainPerson.frameWidth) / 2, (screenY - mainPerson.frameHeight) / 2);
                        canvas.drawRect((float) ((screenX-mainPerson.frameWidth)/2.0),
                                (float) (mainPerson.positionY+mainPerson.frameHeight),
                                (float) ((screenX-mainPerson.frameWidth)/2.0+ mainPerson.HP*mainPerson.frameWidth),
                                (float) (mainPerson.positionY+mainPerson.frameHeight+10),
                                paintForHPBar);
                        canvas.drawRect((float) ((screenX-mainPerson.frameWidth)/2.0),
                                (float) (mainPerson.positionY+mainPerson.frameHeight),
                                (float) ((screenX+mainPerson.frameWidth)/2.0),
                                (float) (mainPerson.positionY+mainPerson.frameHeight+10),
                                paintForHPBarStroke);

                        //СПАВН ВРАГОВ
                        if(realTime-prevSpawnTime >= Enemy.SPAWN_KD) {
                            if (Methods.probability(50)) {
                                double spawnRadius = screenY/2.0;
                                double angle = Math.random() * 2.0 * Math.PI - Math.PI;
                                Enemy enemy = new Enemy(getContext(),
                                        (int) (screenX / 2.0 + spawnRadius * Math.sin(angle)),
                                        (int) (screenY / 2.0 + spawnRadius * Math.cos(angle)));
                                enemyArray.add(enemy);
                            }
                            prevSpawnTime = realTime;
                        }

                        //БОМБЫ
                        for (int i = 0; i < bombList.size(); i++) {
                            if (!bombList.get(i).isCreate) {
                                bombList.get(i).createBomb(canvas, realTime);
                            }
                            int localRotateX = joystickMove.orientation.equals("STOP")?0:rotateX;
                            int localRotateY = joystickMove.orientation.equals("STOP")?0:rotateY;
                            if (bombList.get(i).isCreate) {
                                if (realTime - bombList.get(i).timeStart <= Bomb.time) {
                                    bombList.get(i).spawnBomb(canvas, canvas.getWidth(), getHeight(), realTime, localRotateX, localRotateY, mainPerson);
                                }
                                else if (realTime - bombList.get(i).timeStart > Bomb.time) {
                                    bombList.get(i).damageBomb(canvas, canvas.getWidth(), getHeight(), realTime, localRotateX, localRotateY, mainPerson);
                                    bombList.get(i).destroyBomb();
                                }
                            }
                        }

                        //ЛАЗЕРЫ
                        for (int i = 0; i < 3; i++) {
                            if(!laserList.get(i).isCreate){
                                laserList.get(i).createLaser(canvas, realTime);
                            }
                            if(laserList.get(i).isCreate){
                                if(realTime-laserList.get(i).timeStart <= Laser.time){
                                    laserList.get(i).spawnLaser(canvas, canvas.getWidth(), getHeight(), realTime, joystickMove.ratioX(), joystickMove.ratioY(), mainPerson);
                                }
                                else if(realTime-laserList.get(i).timeStart > Laser.time){
                                    laserList.get(i).damageLaser(canvas, canvas.getWidth(), getHeight(), realTime, joystickMove.ratioX(), joystickMove.ratioY(), mainPerson);
                                    laserList.get(i).destroyLaser();
                                }
                            }
                        }


                        //_________________                        __________________________//
                        //                 МЕСТО ДЛЯ ФОРА ПО ВРАГАМ                          //
                        double totalWay = Double.MAX_VALUE;
                        for(int i = 0; i < enemyArray.size(); i++) {
                            Enemy currentEnemy = enemyArray.get(i);
                            currentEnemy.update(canvas, mainPerson, joystickMove.ratioX(), joystickMove.ratioY());

                            //НЕ В АТАКЕ
                            if (!mainPerson.isAttack) {
                                //ПОИСК БЛИЖАЙШЕГО ВРАГА
                                double localWay = currentEnemy.wayX * currentEnemy.wayX + currentEnemy.wayY * currentEnemy.wayY;
                                if (localWay < totalWay) {
                                    totalWay = localWay;
                                    mainPerson.nearbyEnemy = currentEnemy;
                                }
                            }
                            //В АТАКЕ
                            else {
                                //ПЕРЕСЕЧЕНИЕ ВРАГА И ПУЛИ
                                if (currentEnemy.destination.intersect(new Rect(mainPerson.bulletX,
                                                                                mainPerson.bulletY,
                                                                                mainPerson.bulletX+(int)mainPerson.bulletWidth,
                                                                                mainPerson.bulletY+(int)mainPerson.bulletHeight))) {
                                    GameView.enemyArray.remove(currentEnemy);
                                    SCORE++;
                                    mainPerson.stopAttackTime = realTime;
                                    mainPerson.isAttack = false;
                                }
                            }
                            //ПЕРЕСЕЧЕНИЕ ВРАГА И ПЕРСОНАЖА
                            if (currentEnemy.checkForAttack(mainPerson)) {
                                if (Enemy.KD <= realTime - currentEnemy.previousAttackTime) {
                                    mainPerson.HP -= Enemy.damage;
                                    GameActivity.vibrator.vibrate(15);
                                    currentEnemy.previousAttackTime = realTime;
                                }
                            }
                        }//____________КОНЕЦ ФОРА ПО ВРАГАМ____________________//
                         //                                                    //

                        //НЕ В АТАКЕ
                        if(!mainPerson.isAttack){
                            //ЕСЛИ ПРОШЛО КД АТАКИ
                            if (realTime - mainPerson.stopAttackTime >= mainPerson.KD_AttackTime) {
                                //ЕСЛИ НА ПОЛЕ ЕСТЬ ХОТЯ БЫ 1 ВРАГ
                                if(enemyArray.size()>0) {
                                    mainPerson.isAttack = true;
                                    mainPerson.startAttackTime = realTime;
                                    double wayX = mainPerson.positionX + mainPerson.frameWidth / 2.0 - (mainPerson.nearbyEnemy.positionX + mainPerson.nearbyEnemy.frameWidth / 2.0);
                                    double wayY = mainPerson.positionY + mainPerson.frameHeight / 2.0 - (mainPerson.nearbyEnemy.positionY + mainPerson.nearbyEnemy.frameHeight / 2.0);
                                    mainPerson.bulletAngle = Math.atan2(wayY, wayX);
                                    mainPerson.bulletX = (int) (mainPerson.positionX + (mainPerson.frameWidth - mainPerson.bulletWidth) / 2.0);
                                    mainPerson.bulletY = (int) (mainPerson.positionY + (mainPerson.frameHeight - mainPerson.bulletHeight) / 2.0);
                                }
                            }
                        }

                        //В АТАКЕ
                        else {
                            mainPerson.simpleAttack(canvas,joystickMove.ratioX(),joystickMove.ratioY());

                            //ЕСЛИ ВДРУГ АТАКА ЗАТЯНУЛАСЬ
                            if(realTime - mainPerson.startAttackTime >= mainPerson.maxAttackTime){
                                mainPerson.stopAttackTime = realTime;
                                mainPerson.isAttack = false;
                            }
                        }

                        if(mainPerson.HP <= 0.6 && mainPerson.HP > 0.2)paintForHPBar.setColor(Color.rgb(217,170,2));
                        else if(mainPerson.HP <= 0.2 && mainPerson.HP > 0.0)paintForHPBar.setColor(Color.rgb(204,6,5));
                        else if(mainPerson.HP <= 0.0) {
                            WelcomeActivity.addEntry((int)TIME,(int)SCORE);
                            gameThread.resume();
                            new GameActivity().finish();
                        }

                        canvas.drawText((int)TIME + " TIME", canvas.getWidth() - 200, 300, paintForSystemInfo);
                        canvas.drawText(FPS + " FPS", canvas.getWidth() - 200, 400, paintForSystemInfo);
                        //canvas.drawText((int)(mainPerson.HP*100) + " HP", canvas.getWidth() - 200, 500, paintForHP);
                    }////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    surfaceHolder.unlockCanvasAndPost(canvas);
                    prevTime = realTime;
                }
            }
        }
    }
    public static double getTIME() {
        return TIME;
    }

    public static double getSCORE() {
        return SCORE;
    }
}
/*469*/