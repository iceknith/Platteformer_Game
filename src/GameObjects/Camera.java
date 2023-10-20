package GameObjects;

import handlers.MouseHandler;
import main.GamePanel;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

public class Camera extends GameGrid {

    public ArrayList<GameObject2D> displayableBuffer = new ArrayList<>();

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

    boolean noUpdate = false;
    boolean bufferUpdateGrid = false;

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

        visible = new Vector<>();
    }

    public void updateAll() throws Exception {


        //update every game object
        level.update();

        //buffered actions
        if (!displayableBuffer.isEmpty()){
            for (GameObject2D go : displayableBuffer){
                GamePanel.camera.addGOInGrid(go);
            }
            displayableBuffer = new ArrayList<>();
            updateGrid();

            if (level.hasLevelMaker) LevelMaker.objIsPlaced = true;
        }

        //update itself
        update();
    }

    void update(){

        if (noUpdate) return;

        if (bufferUpdateGrid){
            updateGrid();
            bufferUpdateGrid = false;
        }

        if (level.updateLevelMaker){
            levelMakerCameraUpdate();
            return;
        }

        //do not update camera if no player
        if (level.hasNoPlayer()) return;

        Player p = GameObject2D.player;

        //variation = the difference between the center of the screen and the furthest corner of the player
        int variationX;
        if (Math.abs(getScreenX() + screenWidth /2 - p.getX()) > Math.abs(getScreenX() + screenWidth /2 - p.getX() - p.getWidth())){
            variationX = getScreenX() + screenWidth /2 - p.getX();
        }else{
            variationX = getScreenX() + screenWidth /2 - p.getX() - p.getWidth();
        }

        int variationY;
        if (Math.abs(getScreenY() + screenHeight /2 - p.getY()) > Math.abs(getScreenY() + screenHeight /2 - p.getY() - p.getHeight())){
            variationY = getScreenY() + screenHeight /2 - p.getY();
        }else{
            variationY = getScreenY() + screenHeight /2 - p.getY() - p.getHeight();
        }

        //x tests
        if (Math.abs(variationX) > softBorderX) {//soft borders
            movementX(variationX); //set velocity to relative pos of player to soft border

            if (Math.abs(variationX) > hardBorderX){ //hard borders
                if (velocityX < 0){ //logic to assure a smooth transition between soft and har borders
                    velocityX = Math.min(velocityX,p.getVelocityX());
                }else{
                    velocityX = Math.max(velocityX,p.getVelocityX());
                }
            }
        }
        else {
            stopMovementX();
        }


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

    void levelMakerCameraUpdate(){

        if (!LevelMaker.cameraCanMove) return;
        boolean doMove = false;

        //movement X
        if (MouseHandler.getX() <= screenWidth /8){
            velocityX = Math.max((double) screenWidth /8 - MouseHandler.getX(), -50);
            doMove = true;
        }
        else if (MouseHandler.getX() >= screenWidth *7/8){
            velocityX = Math.min((double) screenWidth *7/8 - MouseHandler.getX(), 50);
            doMove = true;

        }
        else velocityX = 0;

        //movement Y
        if (MouseHandler.getY() <= screenHeight /8){
            velocityY = Math.max((double) screenHeight /8 - MouseHandler.getY(), -50);
            doMove = true;

        }
        else if (MouseHandler.getY() >= screenHeight *7/8){
            velocityY = Math.min((double) screenHeight *7/8 - MouseHandler.getY(), 50);
            doMove = true;

        }
        else velocityY = 0;

        if (doMove) move();
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

        //logic to make sure that the camera moves, even when it moves slowly
        if (movX == 0 && velocityX != 0){
            screenX -= (int) Math.signum(velocityX);
        }
        else {
            screenX -= movX;
        }

        if (movY == 0 && velocityY != 0) {
            screenY -= (int) Math.signum(velocityY);
        }
        else {
            screenY -= movY;
        }


        if (velocityX != 0 || velocityY != 0){
            screenX = Math.min(32767, Math.max(-32767, screenX));
            screenY = Math.min(32767, Math.max(-32767, screenY));
            updateGrid();
        }
    }

    public void move(int posX, int posY){
        screenX = Math.min(32767, Math.max(-32767, posX));
        screenY = Math.min(32767, Math.max(-32767, posY));

        bufferUpdateGrid = true;
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
