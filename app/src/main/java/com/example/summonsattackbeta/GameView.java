package com.example.summonsattackbeta;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    static GameThread gameThread;
    RemoteView joystickMove;
    Context mContext;

    Boolean screenTouch;
    float screenX;
    float screenY;
    static MainPerson mainPerson;
    private double FPS;
    public static double averageFPS;
    private int cntFPS;
    public double TIME;
    public int SCORE;

    List<CommonEnemy> commonEnemyList;
    List<SpiralEnemy> spiralEnemyList;
    List<Coin> coinList;

    List<Laser> laserList;
    long prevLaserSpawnTime;
    final long KD_LaserSpawnTime_MAX_VALUE;
    long KD_LaserSpawnTime;

    List<Bomb> bombList;
    long prevBombSpawnTime;
    final long KD_BombSpawnTime_MAX_VALUE;
    long KD_BombSpawnTime;

    LinkedList<LinkedList<Rect>> linkedBackgroundList;
    float movingX;
    float movingY;
    int cntFiguresOnWidth;
    int cntFiguresOnHeight;
    float interval;
    float figureSide;
    float maxRotateForBackground;
    int rotateX;
    int rotateY;
    protected double localRatioX;
    protected double localRatioY;

    public GameView(Context context) {
        super(context);
        mContext = context;
        GameActivity.activeGameKey = true;
        screenTouch = false;
        getHolder().addCallback(this);
        FPS = 60;
        averageFPS = 60;
        cntFPS = 1;
        mainPerson = new MainPerson(getContext());  /*ONLY ONE*/

        commonEnemyList = new ArrayList<>();
        spiralEnemyList = new ArrayList<>();

        laserList = new ArrayList<>();
        prevLaserSpawnTime = 0;
        KD_LaserSpawnTime_MAX_VALUE = 2000;
        KD_LaserSpawnTime = 2000;

        bombList = new ArrayList<>();
        prevBombSpawnTime = 0;
        KD_BombSpawnTime_MAX_VALUE = 1500;
        KD_BombSpawnTime = 1500;

        coinList = new ArrayList<>();

        interval = WelcomeActivity.maxX / 22.0f;
        figureSide = interval * 6.0f;
        maxRotateForBackground = interval + figureSide;

        int plusLineAndColumn = 2;

        cntFiguresOnWidth = 3 + 2 + plusLineAndColumn * 2;//additional left & right
        cntFiguresOnHeight = (int)(WelcomeActivity.maxY / (interval + figureSide)) + 1 + 2 + plusLineAndColumn * 2;//additional up & down
        linkedBackgroundList = new LinkedList<>();
        for(int i = 0; i < cntFiguresOnHeight; i++){
            LinkedList<Rect>list = new LinkedList<>();
            for(int j = 0; j < cntFiguresOnWidth;j++){
                list.add(new Rect((int)(interval + (interval + figureSide) * (j-1-plusLineAndColumn)),
                                  (int)(interval + (interval + figureSide) * (i-1-plusLineAndColumn)),
                                  (int)((interval + figureSide) * (j-plusLineAndColumn)),
                                  (int)((interval + figureSide) * (i-plusLineAndColumn))
                                  )
                );
            }
            linkedBackgroundList.add(list);
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        gameThread = new GameThread(surfaceHolder);
        gameThread.start();

        if (!WelcomeActivity.firstEnter) {
            try {
                Toast.makeText(getContext(), "Wait please...", Toast.LENGTH_SHORT).show();
                WelcomeActivity.firstEnter = true;
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    class GameThread extends Thread{
        final SurfaceHolder surfaceHolder;
        Paint paintForTime;
        Paint paintForScore;
        Paint paintForSystemInfo;
        Paint paintForHP;
        Paint paintForBackground;
        int backColor;

        long startTime;  //время начала сессии////////
        long prevTime;   //время предыдущей анимации//
        private final static int frameTime = 16;//ВРЕМЯ КАДРА//
        long realTime;

        Canvas canvas;

        public int currentFPS;
        long prevFpsTime;
        long prevCommonEnemySpawnTime;
        long prevSpiralEnemySpawnTime;

        public GameThread(SurfaceHolder holder) {
            surfaceHolder = holder;

            paintForTime = new Paint();
            paintForTime.setAntiAlias(true);
            paintForTime.setColor(Color.WHITE);
            paintForTime.setTextSize(100);

            paintForScore = new Paint();
            paintForScore.setAntiAlias(true);
            paintForScore.setColor(Color.rgb(209, 171, 0));
            paintForScore.setTextSize(90);

            paintForSystemInfo = new Paint();
            paintForSystemInfo.setAntiAlias(true);
            paintForSystemInfo.setColor(Color.BLUE);
            paintForSystemInfo.setTextSize(50);

            paintForHP = new Paint();
            paintForHP.setAntiAlias(true);
            paintForHP.setColor(Color.RED);
            paintForHP.setTextSize(50);

            paintForBackground = new Paint();
            paintForBackground.setAntiAlias(true);
            paintForBackground.setStyle(Paint.Style.FILL);

            //BACK COLOR
            paintForBackground.setColor(Color.rgb(42,94,3));

            backColor = Color.rgb(0, 66, 32);
            //
        }

        @Override
        public void run() {
            startTime = System.nanoTime()/(long)Math.pow(10 ,6);
            prevTime = startTime;
            prevFpsTime = startTime;
            currentFPS = 0;
            prevCommonEnemySpawnTime = startTime;
            prevSpiralEnemySpawnTime = startTime;
            mainPerson.stopAttackTime = startTime + 1000;

            while (GameActivity.activeGameKey){
                realTime = System.nanoTime()/(long)Math.pow(10 ,6);
                if(realTime - prevTime >= frameTime){
                    canvas = surfaceHolder.lockCanvas();
                    screenX = canvas.getWidth();
                    screenY = canvas.getHeight();
                    synchronized (surfaceHolder) {
                        currentFPS++;
                        if (realTime - prevFpsTime >= 1000) {
                            FPS = currentFPS;
                            averageFPS = (averageFPS * cntFPS + FPS) / ++cntFPS;
                            currentFPS = 0;
                            prevFpsTime = realTime;
                        }
                        TIME = (System.nanoTime() / Math.pow(10, 6) - startTime) / 1000;
                        canvas.drawColor(backColor);

                        //УСЛОЖНЕНИЕ
                        double koef = Math.pow(TIME * 0.5 / 60.0 + 0.9, -0.75);
                        Coin.spawn_KD = Coin.CONST_SPAWN_KD * koef;
                        SpiralEnemy.SPAWN_KD = (long) (SpiralEnemy.CONST_SPAWN_KD * koef);
                        CommonEnemy.SPAWN_KD = CommonEnemy.CONST_SPAWN_KD;
                        KD_LaserSpawnTime = (long) (KD_LaserSpawnTime_MAX_VALUE * koef);
                        KD_BombSpawnTime = (long) (KD_BombSpawnTime_MAX_VALUE * koef);

                        //ДЖОЙСТИК
                        if (!screenTouch) {
                            joystickMove = new RemoteView(getContext(), screenX / 2, getHeight() - 300);
                        }
                        localRatioX = joystickMove.ratioX();
                        localRatioY = joystickMove.ratioY();
                        rotateX = (int) ((mainPerson.velocity * 60.0 / (float) averageFPS) * localRatioX);
                        rotateY = (int) ((mainPerson.velocity * 60.0 / (float) averageFPS) * localRatioY);

                        //ФОР ПО ФОНОВЫМ КВАДРАТАМ
                        if (!joystickMove.orientation.equals("STOP")) {
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

                        //СПАВН МОНЕТ
                        if (realTime - Coin.prev_spawn_time > Coin.spawn_KD) {
                            Coin.prev_spawn_time = (int) realTime;
                            if (!joystickMove.orientation.equals("STOP")) {
                                double spawnRadius = screenY / 2.0;
                                double angle = Math.random() * 2.0 * Math.PI - Math.PI;
                                Coin coin = new Coin(getContext(),
                                        (int) (screenX / 2.0 + Math.sin(angle) * spawnRadius),
                                        (int) (screenY / 2.0 + Math.cos(angle) * spawnRadius),
                                        (int) realTime);
                                coinList.add(coin);
                            }
                        }

                        //МОНЕТЫ
                        for (int i = 0; i < coinList.size(); i++) {
                            Coin currentCoin = coinList.get(i);
                            currentCoin.update(canvas, mainPerson, rotateX, rotateY);
                            if (mainPerson.hitBox.intersect(currentCoin.hitBox)) {
                                SCORE++;
                                coinList.remove(currentCoin);
                                if (WelcomeActivity.isSound) {
                                    new GameActivity.BonkThread(mContext).run();
                                }
                            } else if (realTime - currentCoin.startLifeTime > Coin.LIFE_TIME) {
                                coinList.remove(currentCoin);
                            }
                        }

                        //ГЛАВНЫЙ ПЕРСОНАЖ
                        mainPerson.update(canvas, (screenX - mainPerson.frameWidth) / 2, (screenY - mainPerson.frameHeight) / 2);
                        mainPerson.updateBullets(canvas, rotateX, rotateY, realTime, commonEnemyList.isEmpty() && spiralEnemyList.isEmpty());

                        //СПАВН ВРАГОВ
                        if (realTime - prevCommonEnemySpawnTime >= CommonEnemy.SPAWN_KD) {
                            if (Methods.probability(50)) {
                                double spawnRadius = screenY / 2.0;
                                double angle = Math.random() * 2.0 * Math.PI - Math.PI;
                                CommonEnemy commonEnemy = new CommonEnemy(getContext(),
                                        (int) (screenX / 2.0 + Math.sin(angle) * spawnRadius),
                                        (int) (screenY / 2.0 + Math.cos(angle) * spawnRadius));
                                commonEnemyList.add(commonEnemy);
                            }
                            prevCommonEnemySpawnTime = realTime;
                        }
                        if (realTime - prevSpiralEnemySpawnTime >= SpiralEnemy.SPAWN_KD) {
                            prevSpiralEnemySpawnTime = realTime;
                            if (Methods.probability(75)) {
                                double spawnRadius = screenY / 2.0;
                                double angle = Math.random() * 2.0 * Math.PI - Math.PI;
                                spiralEnemyList.add(new SpiralEnemy(getContext(),
                                        screenX / 2.0 + Math.sin(angle) * spawnRadius,
                                        screenY / 2.0 + Math.cos(angle) * spawnRadius,
                                        realTime));
                            }
                        }

                        //ЛАЗЕРЫ
                        if (realTime - prevLaserSpawnTime >= KD_LaserSpawnTime) {
                            laserList.add(new Laser(realTime, canvas));
                            prevLaserSpawnTime = realTime;
                        }
                        for (int i = 0; i < laserList.size(); i++) {
                            Laser currentLaser = laserList.get(i);
                            currentLaser.update(canvas, rotateX, rotateY, realTime);
                            if (realTime - currentLaser.startTime >= currentLaser.lifeTime) {
                                if (Rect.intersects(currentLaser.hitBox, mainPerson.hitBox)) {
                                    mainPerson.takeDamage(Laser.damage);
                                }
                                laserList.remove(currentLaser);
                                i--;
                            }
                        }

                        //БОМБЫ
                        if (realTime - prevBombSpawnTime >= KD_BombSpawnTime) {
                            bombList.add(new Bomb(realTime, canvas));
                            prevBombSpawnTime = realTime;
                        }
                        for (int i = 0; i < bombList.size(); i++) {
                            Bomb currentBomb = bombList.get(i);
                            currentBomb.update(canvas, rotateX, rotateY, realTime);
                            if (realTime - currentBomb.startTime >= currentBomb.lifeTime) {
                                if (Rect.intersects(currentBomb.hitBox, mainPerson.hitBox)) {
                                    mainPerson.takeDamage(Bomb.damage);
                                }
                                bombList.remove(currentBomb);
                                i--;
                            }
                        }

                        //ПОИСК БЛИЖАЙШЕГО ВРАГА
                        double totalWay = Double.MAX_VALUE;
                        for (int i = 0; i < commonEnemyList.size(); i++) {
                            Enemy currentCommonEnemy = commonEnemyList.get(i);
                            double localWay = currentCommonEnemy.getDistanceFor();
                            if (localWay < totalWay) {
                                totalWay = localWay;
                                mainPerson.nearbyEnemy = currentCommonEnemy;
                            }
                        }
                        for (int i = 0; i < spiralEnemyList.size(); i++) {
                            Enemy currentSpiralEnemy = spiralEnemyList.get(i);
                            double localWay = currentSpiralEnemy.getDistanceFor();
                            if (localWay < totalWay) {
                                totalWay = localWay;
                                mainPerson.nearbyEnemy = currentSpiralEnemy;
                            }
                        }

                        //_________________                        __________________________//
                        //                 МЕСТО ДЛЯ ФОРА ПО ВРАГАМ                          //
                        for (int i = 0; i < commonEnemyList.size(); i++) {
                            CommonEnemy currentCommonEnemy = commonEnemyList.get(i);
                            currentCommonEnemy.update(canvas, mainPerson, rotateX, rotateY);

                            //ПЕРЕСЕЧЕНИЕ ВРАГА И ПЕРСОНАЖА
                            if (currentCommonEnemy.checkForAttack(mainPerson) && CommonEnemy.KD <= realTime - currentCommonEnemy.previousAttackTime) {
                                mainPerson.takeDamage(CommonEnemy.damage);
                                currentCommonEnemy.previousAttackTime = realTime;
                            }

                            //ПЕРЕСЕЧЕНИЕ ВРАГА И ПУЛИ
                            for (int j = 0; j < mainPerson.bulletList.size(); j++) {
                                Bullet currentBullet = mainPerson.bulletList.get(j);
                                if (currentBullet.hitBox.intersect(currentCommonEnemy.hitBox)) {
                                    currentCommonEnemy.takeDamage(currentBullet.damage);
                                    mainPerson.bulletList.remove(currentBullet);
                                    j--;
                                    if (currentCommonEnemy.getHP() <= 0) {
                                        commonEnemyList.remove(currentCommonEnemy);
                                        i--;
                                        break;
                                    }
                                }
                            }
                        }
                        for (int j = 0; j < spiralEnemyList.size(); j++) {
                            SpiralEnemy currentSpiralEnemy = spiralEnemyList.get(j);
                            currentSpiralEnemy.update(canvas, mainPerson, rotateX, rotateY);
                            currentSpiralEnemy.updateBullets(canvas, rotateX, rotateY, realTime);

                            //ПЕРЕСЕЧЕНИЕ ПУЛИ ВРАГА И ПЕРСОНАЖА
                            for (int i = 0; i < currentSpiralEnemy.bulletList.size(); i++) {
                                Bullet currentBullet = currentSpiralEnemy.bulletList.get(i);
                                if (currentBullet.hitBox.intersect(mainPerson.hitBox)) {
                                    mainPerson.takeDamage(currentBullet.damage);
                                    currentSpiralEnemy.bulletList.remove(currentBullet);
                                    i--;
                                }
                            }

                            //ПЕРЕСЕЧЕНИЕ ВРАГА И ПУЛИ
                            for (int i = 0; i < mainPerson.bulletList.size(); i++) {
                                Bullet currentBullet = mainPerson.bulletList.get(i);
                                if (currentBullet.hitBox.intersect(currentSpiralEnemy.hitBox)) {
                                    currentSpiralEnemy.takeDamage(currentBullet.damage);
                                    mainPerson.bulletList.remove(currentBullet);
                                    i--;
                                    if (currentSpiralEnemy.getHP() <= 0) {
                                        currentSpiralEnemy.deathAndReward(coinList, getContext(), realTime);
                                        spiralEnemyList.remove(currentSpiralEnemy);
                                        j--;
                                        break;
                                    }
                                }
                            }
                        }//____________КОНЕЦ ФОРА ПО ВРАГАМ____________________//
                         //                                                    //

                        //СМЕРТЬ
                        if (mainPerson.getHP() <= 0) {
                            if (GameActivity.activeGameKey) {
                                WelcomeActivity.addEntry((int) TIME, SCORE);
                            }
                            GameActivity.activeGameKey = false;
                            ((GameActivity) mContext).openDeathActivity((int) TIME, SCORE);
                        }

                        canvas.drawText((int) TIME / 600 + "" + (int) TIME % 600 / 60 + ":" + (int) TIME % 60 / 10 + "" + (int) TIME % 10, 20, 110, paintForTime);
                        canvas.drawText(SCORE + " coins", 20, 230, paintForScore);
                        if (WelcomeActivity.isShowSystemInfo) {
                            canvas.drawText((int) TIME + " TIME", canvas.getWidth() - 200, 300, paintForSystemInfo);
                            canvas.drawText(String.format("%.2f", averageFPS) + " FPS", canvas.getWidth() - 200, 400, paintForSystemInfo);
                            canvas.drawText(FPS + " FPS", canvas.getWidth() - 200, 500, paintForSystemInfo);
                        }
                    }////////////////////////////////////////////////////////////////////////////////////////////////////////////////
                    surfaceHolder.unlockCanvasAndPost(canvas);
                    prevTime = realTime;
                }
            }
        }
    }
}
