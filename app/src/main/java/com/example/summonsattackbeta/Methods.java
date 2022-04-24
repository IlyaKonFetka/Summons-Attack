package com.example.summonsattackbeta;


import android.graphics.Rect;

import java.util.Random;

public abstract class Methods {
    public static boolean probability(double percent){ //ВЕРОЯТНОСТЬ
        return Math.random() * 100.0 <= percent;
    }
    public static boolean segmentIntersectRectangle(
            double rectangleMinX,
            double rectangleMinY,
            double rectangleMaxX,
            double rectangleMaxY,
            double p1X,
            double p1Y,
            double p2X,
            double p2Y)
    {
        // Find min and max X for the segment
        double minX = p1X;
        double maxX = p2X;

        if (p1X > p2X)
        {
            minX = p2X;
            maxX = p1X;
        }

        // Find the intersection of the segment's and rectangle's x-projections
        if (maxX > rectangleMaxX)
        {
            maxX = rectangleMaxX;
        }

        if (minX < rectangleMinX)
        {
            minX = rectangleMinX;
        }

        if (minX > maxX) // If their projections do not intersect return false
        {
            return false;
        }

        // Find corresponding min and max Y for min and max X we found before
        double minY = p1Y;
        double maxY = p2Y;

        double dx = p2X - p1X;

        if (Math.abs(dx) > 0.0000001)
        {
            double a = (p2Y - p1Y)/dx;
            double b = p1Y - a*p1X;
            minY = a*minX + b;
            maxY = a*maxX + b;
        }

        if (minY > maxY)
        {
            double tmp = maxY;
            maxY = minY;
            minY = tmp;
        }

        // Find the intersection of the segment's and rectangle's y-projections
        if (maxY > rectangleMaxY)
        {
            maxY = rectangleMaxY;
        }

        if (minY < rectangleMinY)
        {
            minY = rectangleMinY;
        }

        if (minY > maxY) // If Y-projections do not intersect return false
        {
            return false;
        }

        return true;
    }
    public static boolean pointIntersectRectangle(Rect r,int x, int y){
        return (r.left < x && x < r.right && r.top < y && y < r.bottom);
    }
    public static boolean pointIntersectRectangle(int left, int top, int right, int bottom,int x, int y){
        return (left < x && x < right && top < y && y < bottom);
    }
    public static int getRandomOneOrMinusOne() {
        Random random = new Random();
        return random.nextBoolean()?1:-1;
    }
}

//void addFirst(E obj): добавляет элемент в начало очереди
//void addLast(E obj): добавляет элемент obj в конец очереди
//E pollFirst(): возвращает с удалением элемент из начала очереди. Если очередь пуста, возвращает значение null
//E pollLast(): возвращает с удалением последний элемент очереди. Если очередь пуста, возвращает значение null

