package GameObjects;

import main.GamePanel;

import java.io.IOException;
import java.util.ArrayList;

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

    public boolean isOperational;

    public Camera(int screenW, int screenH, int posX, int posY) throws IOException {
        super(screenW, screenH, posX, posY);

        speedX = 0.2;
        speedY = 0.3;

        stoppingSpeedX = 3;
        stoppingSpeedY = 30;

        velocityX = 0;
        velocityY = 0;

        hardBorderX = (int) (0.4 * screenW);
        hardBorderY = (int) (0.25 * screenH);

        softBorderX = 250;
        softBorderY = 100;

        visible = new ArrayList<>();
        isOperational = true;
    }

    public void update(){


        Player p = GamePanel.player;

        //variation = the difference between the center of the screen and the center of the player
        int variationX = getX() + width/2 - p.getX() - p.getWidth()/2;
        int variationY = getY() + height/2 - p.getY() - p.getHeight()/2;

        //x tests
        if (Math.abs(variationX) > hardBorderX){
            velocityX = variationX - hardBorderX * (int) Math.signum(variationX);
        }else{
            if (Math.abs(variationX) > softBorderX){
                movementX(variationX); //set velocity to relative pos of player to soft border
            }
            else{
                stopMovementX();
            }
        }


        //y tests
        if (Math.abs(variationY) > hardBorderY){ //hard border
            velocityY = variationY - hardBorderY * (int) Math.signum(variationY);
        }
        else{
            if (Math.abs(variationY) > softBorderY){ //soft border
                movementY(variationY); //set velocity to relative pos of player to soft border
            }
            else{
                stopMovementY();
            }
        }

        move();
    }

    public int getHardBorderX(){return hardBorderX;}

    public int getHardBorderY(){return hardBorderY;}

    public int getSoftBorderX(){return softBorderX;}

    public int getSoftBorderY(){return softBorderY;}

    void movementX(int playerPos){
        velocityX = (playerPos  - softBorderX * (int) Math.signum(playerPos)) * speedX;
    }

    void movementY(int playerPos){
        velocityY = (playerPos - softBorderY * (int) Math.signum(playerPos)) * speedY;
    }

    void move(){
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
            velocityX -= stoppingSpeedX * Math.signum(velocityX) * GamePanel.deltaTime;
        }else {
            velocityX = 0;
        }
    }

    void stopMovementY(){
        if(Math.abs(velocityY) >= stoppingSpeedY){
            velocityY -= stoppingSpeedY * Math.signum(velocityY) * GamePanel.deltaTime;
        }else {
            velocityY = 0;
        }
    }

    public double getVelocityX(){return velocityX;}

    public double getVelocityY(){return velocityY;}

}
