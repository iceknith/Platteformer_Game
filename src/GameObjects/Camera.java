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

    double smoothnessX;
    double smoothnessY;
    double centeringSmoothnessY;

    double speedX;
    double speedY;
    double directionX;

    double stoppingSpeedX;
    double stoppingSpeedY;

    double velocityX;
    double velocityY;

    Rectangle softBorder;
    Rectangle hardBorder;

    int softBorderXPosLeft;
    int softBorderXPosRight;
    int centeredPosY;

    boolean noUpdate = false;
    boolean bufferUpdateGrid = false;

    public Camera(int screenW, int screenH, int posX, int posY) throws IOException {
        super(screenW, screenH, posX, posY);

        smoothnessX = 0.5;
        smoothnessY = 0.04;
        centeringSmoothnessY = 0.07;

        speedX = 0.4;
        speedY = 0.25;

        stoppingSpeedX = (double) 1 /30;
        stoppingSpeedY = (double) 1 /30;

        velocityX = 0;
        velocityY = 0;

        centeredPosY = screenHeight * 5/8;

        int softBorderW = (int) (screenWidth * 0.13);
        int softBorderH = (int) (screenHeight * 0.4);

        directionX = 0;

        softBorderXPosLeft = (int) (screenWidth * 0.3 - softBorderW/2);
        softBorderXPosRight = (int) (screenWidth * 0.7 - softBorderW/2);

        softBorder = new Rectangle(screenWidth/2 - softBorderW/2, screenHeight/2 - softBorderH/2, softBorderW, softBorderH);

        int hardBorderW = (int) (screenWidth * 0.7);
        int hardBorderH = (int) (screenHeight * 0.6);
        hardBorder = new Rectangle(screenWidth/2 - hardBorderW/2, screenHeight/2 - hardBorderH/2, hardBorderW, hardBorderH);

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

    void update() throws Exception {

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

        //-----x camera-----//

        if (p.getX() - screenX < softBorder.x) { //player is to the left of the soft border

            velocityX = lerp(velocityX, (softBorder.x - p.getX() + screenX) * speedX, smoothnessX);

            // change position of the softBorder if it isn't where the player looks
            if (p.sprite.getDirection() != directionX && p.sprite.getDirection() == -1){
                directionX = p.sprite.getDirection();
                softBorder.x = softBorderXPosRight;
            }

            if (p.getX() - screenX < hardBorder.x){ //hard border left
                velocityX = Math.max(velocityX, - p.getVelocityX() - p.getGroundVelocityX());
            }
        }
        else if (p.getX() + p.getWidth() - screenX > softBorder.x + softBorder.width) { //player is to the right of the soft border

            velocityX = lerp(velocityX, (softBorder.x + softBorder.width - p.getX() - p.getWidth() + screenX) * speedX, smoothnessX);

            // change position of the softBorder if it isn't where the player looks
            if (p.sprite.getDirection() != directionX && p.sprite.getDirection() == 1){
                directionX = p.sprite.getDirection();
                softBorder.x = softBorderXPosLeft;
            }

            if (p.getX() + p.getWidth() - screenX > hardBorder.x + hardBorder.width){ //hard borders right
                velocityX = Math.min(velocityX, - p.getVelocityX() - p.getGroundVelocityX());
            }
        }
        else {
            stopMovementX();
        }

        //-----y camera-----//

        if (p.getY() - screenY < softBorder.y){ //player is over the soft border

            if (p.velocityY > 0){ //if player is going up
                velocityY = lerp(velocityY, p.velocityY, smoothnessY);
            }
            else{ //if player and camera are going down
                velocityY = lerp(velocityY, (softBorder.y - p.getY() + screenY) * speedY, smoothnessY);
            }

            if (p.getY() - screenY < hardBorder.y){ //hard border up
                velocityY = Math.max(velocityY, p.getVelocityY() + p.getGroundVelocityY());
            }
        }
        else if(p.getY() + p.getHeight() - screenY > softBorder.y + softBorder.height){ //player is under the soft border

            if (p.velocityY < 0){ //if player is going down
                velocityY = lerp(velocityY, p.velocityY, smoothnessY);
            }
            else{ //if player and camera are going up
                velocityY = lerp(velocityY, (softBorder.y + softBorder.height - p.getY() - p.getHeight() + screenY) * speedY, smoothnessY);
            }

            if (p.getY() + p.getHeight() - screenY > hardBorder.y + hardBorder.height){ //hard border down
                velocityY = Math.min(velocityY, p.getVelocityY() + p.getGroundVelocityY());
            }
        }
        else if (p.isOnGround && Math.abs(velocityY - (centeredPosY - p.getY() - (double) p.getHeight() /2 + screenY)) >= 1) {
            velocityY = lerp(velocityY, (centeredPosY - p.getY() - (double) p.getHeight() /2 + screenY) * speedY, centeringSmoothnessY);
        }
        else {
            stopMovementY();
        }


        move();
    }

    void levelMakerCameraUpdate() throws Exception {

        if (!LevelMaker.cameraCanMove) return;
        boolean doMove = false;

        //movement X
        if (MouseHandler.getX() <= screenWidth /10){
            velocityX = Math.max((double) screenWidth /10 - MouseHandler.getX(), -50);
            doMove = true;
        }
        else if (MouseHandler.getX() >= screenWidth *9/10){
            velocityX = Math.min((double) screenWidth *9/10 - MouseHandler.getX(), 50);
            doMove = true;

        }
        else velocityX = 0;

        //movement Y
        if (MouseHandler.getY() <= screenHeight /10){
            velocityY = Math.max((double) screenHeight /10 - MouseHandler.getY(), -50);
            doMove = true;

        }
        else if (MouseHandler.getY() >= screenHeight *9/10){
            velocityY = Math.min((double) screenHeight *9/10 - MouseHandler.getY(), 50);
            doMove = true;

        }
        else velocityY = 0;

        if (doMove) move();
    }


    public Rectangle getSoftBorder() {
        return softBorder;
    }

    public int getSoftBorderX(){return softBorder.x;}

    public int getSoftBorderY(){return softBorder.y;}

    public Rectangle getHardBorder(){
        return hardBorder;
    }

    public int getHardBorderX(){return hardBorder.x;}

    public int getHardBorderY(){return hardBorder.y;}

    void move() throws Exception {
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

    double lerp(double val1, double val2, double speed){
        return (1 - speed) * val1 + speed * val2;
    }

    public void move(int posX, int posY){
        screenX = Math.min(32767, Math.max(-32767, posX));
        screenY = Math.min(32767, Math.max(-32767, posY));

        bufferUpdateGrid = true;
    }

    void stopMovementY(){
        if(Math.abs(velocityY) >= 0.1){
            velocityY *= stoppingSpeedY * GamePanel.deltaTime;
        }else {
            velocityY = 0;
        }
    }

    void stopMovementX(){
        if(Math.abs(velocityX) >= 0.1){
            velocityX *= stoppingSpeedX *  GamePanel.deltaTime;
        }else {
            velocityX = 0;
        }
    }
    public double getVelocityX(){return velocityX;}

    public double getVelocityY(){return velocityY;}

    @Override
    public void loadNextLevel() throws Exception {
        GameObject2D.setPlayer(null);
        velocityX = 0;
        velocityY = 0;
        super.loadNextLevel();
    }

}
