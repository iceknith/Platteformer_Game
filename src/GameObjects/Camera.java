package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.io.FileNotFoundException;
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

    int softBorderXoffset;
    int softBorderYoffset;

    public Camera(int screenW, int screenH, int posX, int posY) throws IOException {
        super(screenW, screenH, posX, posY);

        speedX = 0.2;
        speedY = 0.3;

        stoppingSpeedX = 3;
        stoppingSpeedY = 30;

        velocityX = 0;
        velocityY = 0;

        hardBorderX = (int) (0.4 * screenW);
        hardBorderY = (int) (0.4 * screenH);

        softBorderX = 250;
        softBorderY = 100;

        softBorderXoffset = 0;
        softBorderYoffset = 100;

        visible = new ArrayList<>();
    }

    public void updateAll() throws IOException, FontFormatException {
        //update every game object
        level.update();

        //update itself
        update();
    }

    void update(){
        //do not update camera if no player
        if (level.hasNoPlayer()){
            //updateGrid();
            return;
        }

        Player p = GameObject2D.getPlayer();

        //variation = the difference between the center of the screen and the furthest corner of the player
        int variationX;
        if (Math.abs(getX() + width/2 - p.getX()) > Math.abs(getX() + width/2 - p.getX() - p.getWidth())){
            variationX = getX() + width/2 - p.getX();
        }else{
            variationX = getX() + width/2 - p.getX() - p.getWidth();
        }

        int variationY;
        if (Math.abs(getY() + height/2 - p.getY()) > Math.abs(getY() + height/2 - p.getY() - p.getHeight())){
            variationY = getY() + height/2 - p.getY();
        }else{
            variationY = getY() + height/2 - p.getY() - p.getHeight();
        }

        //x tests
        if (Math.abs(variationX) > softBorderX){//soft borders
            movementX(variationX); //set velocity to relative pos of player to soft border

            if (Math.abs(variationX) > hardBorderX){ //hard borders
                if (velocityX < 0){ //logic to assure a smooth transition between soft and har borders
                    velocityX = Math.min(velocityX,p.getVelocityX());
                }else{
                    velocityX = Math.max(velocityX,p.getVelocityX());
                }
            }
        }else{stopMovementX();}


        //y tests
        if (Math.abs(variationY) > softBorderY){ //soft border
            movementY(variationY); //set velocity to relative pos of player to soft border

            if (Math.abs(variationY) > hardBorderY){ //hard border
                if (velocityY < 0){
                    velocityY = Math.min(velocityY,p.getVelocityY());
                }else{
                    velocityY = Math.max(velocityY,p.getVelocityY());
                }
            }
        }else{stopMovementY();}


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
        int movX = (int) Math.round(velocityX * GamePanel.deltaTime);
        int movY = (int) Math.round(velocityY * GamePanel.deltaTime);

        //little logic to make sure that the camera moves, even when it moves slowly
        if (movX == 0 && velocityX != 0){
            x -= (int) Math.signum(velocityX);
        }else{x -= movX;}

        if (movY == 0 && velocityY != 0){
            y -= (int) Math.signum(velocityY);
        }else{y -= movY;}


        if (velocityX != 0 || velocityY != 0){
            updateGrid();
        }
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

    @Override
    public void loadNextLevel() throws FileNotFoundException {
        GameObject2D.setPlayer(null);
        velocityX = 0;
        velocityY = 0;
        super.loadNextLevel();
    }

}
