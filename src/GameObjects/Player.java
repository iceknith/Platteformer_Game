package GameObjects;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class Player extends Rectangle2D {
    double width = 50, height = 75, x = 100, y = 100;
    public final int speed =  5;

    public float forceY = 0;

    public Color color = Color.white;


    public void move(int direction){
        x = (x + direction * speed);
    }

    public void jump(){ forceY -= 0.5;}

    @Override
    public void setRect(double v, double v1, double v2, double v3) {
        x = v;
        y = v1;
        width = v2;
        height = v3;
    }

    @Override
    public int outcode(double v, double v1) {
        return 0;
    }

    @Override
    public Rectangle2D createIntersection(Rectangle2D rectangle2D) {
        return null;
    }

    @Override
    public Rectangle2D createUnion(Rectangle2D rectangle2D) {
        return null;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
