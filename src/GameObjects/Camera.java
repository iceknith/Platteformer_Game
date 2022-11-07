package GameObjects;

import main.GamePanel;

import java.io.IOException;

public class Camera extends GameGrid {

    double speedX;
    double speedY;

    double stoppingSpeedX;
    double stoppingSpeedY;

    double velocityX;
    double velocityY;

    int hardBorderX;
    int hardBorderY;

    int softBorderX;
    int softBorderY;

    public Camera(int screenW, int screenH, int posX, int posY) throws IOException {
        super(screenW, screenH, posX, posY);

        speedX = 0.25;
        speedY = 0.2;

        stoppingSpeedX = 0.5;
        stoppingSpeedY = 5;

        velocityX = 0;
        velocityY = 0;

        hardBorderX = (int) (0.4 * screenW);
        hardBorderY = (int) (0.4 * screenH);

        softBorderX = 100;
        softBorderY = 200;
    }

    public void update() throws IOException {

        boolean intersectX = false;
        boolean intersectY = false;

        Player p = GamePanel.player;

        int variationX = getX() + width/2 - p.getX() - p.getWidth()/2;
        int variationY = getY() + height/2 - p.getY() - p.getHeight()/2;

        if (Math.abs(variationX) > hardBorderX){
            velocityX = -p.getVelocityX();
            intersectX = true;
        }

        if (Math.abs(variationY) > hardBorderY){
            velocityY = p.getVelocityY();
            intersectY = true;
        }

        if(!intersectX){
            if (Math.abs(variationX) > softBorderX){
                movementX(variationX);
                intersectX = true;
            }
        }
        if(!intersectY){
            if (Math.abs(variationY) > softBorderY){
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
        }
        move();
    }

    public int getHardBorderX(){return hardBorderX;}

    public int getHardBorderY(){return hardBorderY;}

    public int getSoftBorderX(){return softBorderX;}

    public int getSoftBorderY(){return softBorderY;}

    void movementX(int movement){
        velocityX = movement * speedX;
    }

    void movementY(int movement){
        velocityY = movement * speedY;
    }

    void move() throws IOException {
        x -= Math.round(velocityX * GamePanel.deltaTime);
        y -= Math.round(velocityY * GamePanel.deltaTime);
        updateGrid();
    }

    public void move(int posX, int posY){
        x = posX;
        y = posY;
    }

    void stopMovementX(){
        if(Math.abs(velocityX) >= stoppingSpeedX){
            velocityX -= stoppingSpeedX * Math.signum(velocityX);
        }else {
            velocityX = 0;
        }
    }

    void stopMovementY(){
        if(Math.abs(velocityY) >= stoppingSpeedY){
            velocityY -= stoppingSpeedY * Math.signum(velocityY);
        }else {
            velocityY = 0;
        }
    }
}
