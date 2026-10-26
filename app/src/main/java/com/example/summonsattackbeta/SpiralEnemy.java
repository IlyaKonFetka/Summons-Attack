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
public class SpiralEnemy extends View implements Enemy {
    float positionX;
    float positionY;
    float wayX;
    float wayY;
    Paint paintForBitmaps;
    public Paint paintForHPBar;
    public Paint paintForHPBarStroke;

    static Bitmap bitmap;
    static Bitmap[] bitmaps;
    Rect hitBox;
    final int spaceOf = 7;

    float frameWidth;
    float frameHeight;
    static int cntOfFrameColumns = 10;
    static int cntOfFrameLines = 1;
    static int cntOfFrames;
    int currentNumberOfFrame;

    //Characteristics
    final float HP_MAX_VALUE = 100;
    private float HP;
    float velocity;
    float damage;
    public static final long CONST_SPAWN_KD = 15000;
    public static long SPAWN_KD;

    //Спиральная атака
    long previousAttackTime;
    long previousShotTime;
    static long KD_AttackTime = 5000;
    static long betweenShotsTime = 100;
    static int countShots = 50;
    int numberCurrentShot;
    boolean isAttack;
    int pathsCount = 10;
    float[] angles;
    static float angleShift = (float) (Math.PI / 18);

    //Награда за убийство
    private static final double COIN_SPAWN_RADIUS = WelcomeActivity.maxX / 8.0f;
    private static final int COIN_SPAWN_MIN = 5;
    private static final int COIN_SPAWN_MAX = 13;

    ArrayList<Bullet> bulletList;
    float bulletVelocity;
    float bulletRadius;
    float bulletDamage;
    int bulletColor;

    public SpiralEnemy(Context context, double spawnX, double spawnY, long realTime) {
        super(context);
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.spiral_enemy);
        bitmaps = Methods.splitBitmap(bitmap, cntOfFrameColumns, cntOfFrameLines);
        cntOfFrames = cntOfFrameColumns * cntOfFrameLines;
        this.currentNumberOfFrame = 0;
        this.HP = HP_MAX_VALUE;

        this.frameWidth = bitmap.getWidth() / cntOfFrameColumns;
        this.frameHeight = bitmap.getHeight() / cntOfFrameLines;

        this.positionX = (float) (spawnX - frameWidth / 2.0);
        this.positionY = (float) (spawnY - frameHeight / 2.0);

        this.angles = new float[pathsCount];
        for (double i = 0; i < pathsCount; i++) {
            angles[(int) i] = (float) (i / pathsCount * 2.0 * Math.PI);
        }
        this.previousAttackTime = realTime;
        this.previousShotTime = realTime;

        this.velocity = 4;
        this.damage = 15;

        this.bulletColor = Color.WHITE;
        this.bulletRadius = 8;
        this.bulletVelocity = 5;
        this.bulletDamage = 5;
        this.bulletList = new ArrayList<>();

        this.paintForBitmaps = new Paint();
        this.paintForBitmaps.setAntiAlias(true);

        this.paintForHPBar = new Paint();
        this.paintForHPBar.setAntiAlias(true);
        this.paintForHPBar.setColor(Color.rgb(0, 153, 0));
        this.paintForHPBar.setStyle(Paint.Style.FILL);

        this.paintForHPBarStroke = new Paint();
        this.paintForHPBarStroke.setAntiAlias(true);
        this.paintForHPBarStroke.setColor(Color.BLACK);
        this.paintForHPBarStroke.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void update(Canvas canvas, MainPerson mainPerson, double rotateX, double rotateY) {
        positionX -= rotateX;
        positionY -= rotateY;

        wayX = (float) (positionX + frameWidth / 2.0 - (mainPerson.positionX + mainPerson.frameWidth / 2.0));
        wayY = (float) (positionY + frameHeight / 2.0 - (mainPerson.positionY + mainPerson.frameHeight / 2.0));

        hitBox = new Rect((int) positionX + spaceOf, (int) positionY + spaceOf, (int) (positionX + frameWidth) - spaceOf, (int) (positionY + frameHeight) - spaceOf);
        canvas.drawBitmap(bitmaps[currentNumberOfFrame], positionX, positionY, paintForBitmaps);
        currentNumberOfFrame = (currentNumberOfFrame + 1) % cntOfFrames;

        //ПОЛОСКА ЗДОРОВЬЯ
        canvas.drawRect(positionX,
                positionY + frameHeight,
                positionX + HP / HP_MAX_VALUE * frameWidth,
                positionY + frameHeight + 10,
                paintForHPBar);
        canvas.drawRect(positionX,
                positionY + frameHeight,
                positionX + frameWidth,
                positionY + frameHeight + 10,
                paintForHPBarStroke);
    }

    @Override
    public boolean checkForAttack(MainPerson mainPerson) {
        return this.hitBox.intersect(mainPerson.hitBox);
    }

    public void deathAndReward(List<Coin> coinList, Context context, long realTime) {
        int spawnCoinCount = (int) (Math.random() * COIN_SPAWN_MAX);
        double x = positionX;
        double y = positionY;
        for (int i = 0; i < spawnCoinCount; i++) {
            double angle = Math.random() * 2.0 * Math.PI - Math.PI;
            coinList.add(new Coin(context,
                    (float) (Math.cos(angle) * COIN_SPAWN_RADIUS + x),
                    (float) (Math.sin(angle) * COIN_SPAWN_RADIUS + y),
                    (int) realTime));
        }
    }

    public void updateBullets(Canvas canvas, double rotateX, double rotateY, long realTime) {
        //НАЧАЛО АТАКИ
        if (!isAttack && realTime - previousAttackTime >= KD_AttackTime) {
            numberCurrentShot = 0;
            isAttack = true;
        }
        //ВЫСТРЕЛЫ ПО СПИРАЛИ
        if (isAttack) {
            if (realTime - previousShotTime > betweenShotsTime) {
                for (int i = 0; i < pathsCount; i++) {
                    bulletList.add(new Bullet(positionX + frameWidth / 2.0, positionY + frameHeight / 2.0,
                            bulletRadius, bulletVelocity, bulletDamage, bulletColor, realTime, angles[i]));
                    angles[i] += angleShift;
                }
                previousShotTime = realTime;
                numberCurrentShot++;
            }
            if (numberCurrentShot >= countShots) {
                isAttack = false;
                previousAttackTime = realTime;
            }
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

    @Override
    public double getDistanceFor() {
        return Math.sqrt(wayX * wayX + wayY * wayY);
    }

    @Override
    public double getAngleFor() {
        return Math.atan2(wayY, wayX);
    }

    public void takeDamage(double damage) {
        HP -= damage;
    }

    public double getHP() {
        return HP;
    }
}
