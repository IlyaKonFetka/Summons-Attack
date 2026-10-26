package com.example.summonsattackbeta;

import android.graphics.Canvas;

public interface Enemy {
    void update(Canvas canvas, MainPerson mainPerson, double rotateX, double rotateY);

    boolean checkForAttack(MainPerson mainPerson);

    double getDistanceFor();

    double getAngleFor();
}
