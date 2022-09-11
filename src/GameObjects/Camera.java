package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Camera extends GameGrid {

    double speedX = 0.25;
    double speedY = 0.2;

    double stoppingSpeedX = 0.5;
    double stoppingSpeedY = 5;

    double velocityX = 0;
    double velocityY = 0;

    public ArrayList<Rectangle> borders = new ArrayList<Rectangle>();

    public Camera(int screenW, int screenH, int posX, int posY) throws IOException {
        super(screenW, screenH, posX, posY);

        borders.add(new Rectangle((int) (- 0.2 * screenW) , 0, (int) (0.4 * screenW), screenH));

        borders.add(new Rectangle((int) (0.8 * screenW), 0, (int) (0.4 * screenW), screenH));

        borders.add(new Rectangle(0 , (int) (-0.1 * screenH), screenW, (int) (0.2 * screenH)));

        borders.add(new Rectangle(0, (int) (0.9 * screenH), screenW, (int) (0.2 * screenH)));
    }

    public void updateCamera() throws IOException {

        boolean intersectX = false;
        boolean intersectY = false;
        Player p = GamePanel.player;

        for (Rectangle b: borders) {
            if (b.intersects(p.getHitbox())) {

                int i = borders.indexOf(b);
                if (i - 2 < 0){
                    int m = (int) Math.signum((p.getX() + p.getWidth()/2f) - (b.x + b.width/2f));
                    movementX(m, b.intersection(p.getHitbox()));
                    intersectX = true;
                }
                else{
                    movementY((int) Math.signum(p.getY() - b.y), b.intersection(p.getHitbox()));
                    intersectY = true;
                }
            }
        }


        if(!intersectX){
            int variationX = getX() + width/2 - p.getX() - p.getWidth()/2;
            if (Math.abs(variationX) > 50){
                movementX(variationX);
                intersectX = true;
            }
        }
        if(!intersectY){
            int variationY = getY() + height/2 - p.getY() - p.getHeight()/2;
            if (Math.abs(variationY) > 100){
                movementY(variationY);
                intersectY = true;
            }
        }

        if(velocityX != 0 || velocityY != 0){
            if(!intersectX){
                stopMovementX();
            }
            if(!intersectY){
                stopMovementY();
            }
            move();
            updateGrid(velocityX, velocityY);
        }
    }

    void movementX(int movement, Rectangle intersection) {
        velocityX = movement*intersection.width/GamePanel.deltaTime;
    }

    void movementY(int movement, Rectangle intersection) {
        velocityY = movement*intersection.height/GamePanel.deltaTime;
    }

    void movementX(int movement){
        velocityX = movement * speedX;
    }

    void movementY(int movement){
        velocityY = movement * speedY;
    }

    void move(){
        x -= Math.round(velocityX * GamePanel.deltaTime);
        y -= Math.round(velocityY * GamePanel.deltaTime);
        for (Rectangle r: borders) {
            r.x -= Math.round(velocityX * GamePanel.deltaTime);
            r.y -= Math.round(velocityY * GamePanel.deltaTime);
        }
    }

    public void move(int posX, int posY){
        x = posX;
        y = posY;

        borders.clear();

        borders.add(new Rectangle((int) (- 0.2 * width) + x , y, (int) (0.4 * width), height));

        borders.add(new Rectangle((int) (0.8 * width) + x, y, (int) (0.4 * width), height));

        borders.add(new Rectangle(x , (int) (-0.1 * height) + y, width, (int) (0.2 * height)));

        borders.add(new Rectangle(x, (int) (0.7 * height) + y, width, (int) (0.6 * height)));
    }

    void stopMovementX(){
        if(Math.abs(velocityX) >= stoppingSpeedX){
            velocityX -= stoppingSpeedX * Math.signum(velocityX);
        }else {
            velocityX = 0;
        }
    }

    void stopMovementY(){
        System.out.println(velocityY);
        if(Math.abs(velocityY) >= stoppingSpeedY){
            velocityY -= stoppingSpeedY * Math.signum(velocityY);
        }else {
            velocityY = 0;
        }
    }
}
