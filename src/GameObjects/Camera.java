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

    Rectangle softBorder;
    int minSoftBorderX;
    int centerSoftBorderX;
    int maxSoftBorderX;
    int minSoftBorderY;
    int centerSoftBorderY;
    int maxSoftBorderY;

    boolean noUpdate = false;
    boolean bufferUpdateGrid = false;

    public Camera(int screenW, int screenH, int posX, int posY) throws IOException {
        super(screenW, screenH, posX, posY);

        speedX = 0.001;
        speedY = 0.001;

        stoppingSpeedX = 3;
        stoppingSpeedY = 30;

        velocityX = 0;
        velocityY = 0;

        hardBorderX = (int) (0.1 * screenW);
        hardBorderY = (int) (0.1 * screenH);

        int softBorderW = screenWidth/2;
        int softBorderH = screenHeight/2;

        minSoftBorderX = screenWidth/10;
        centerSoftBorderX = screenWidth/2 - softBorderW/2;
        maxSoftBorderX = screenWidth - minSoftBorderX - softBorderW;

        minSoftBorderY = screenHeight/5;
        centerSoftBorderY = screenHeight/2 - softBorderH/2;
        maxSoftBorderY = screenHeight - minSoftBorderY;

        softBorder = new Rectangle(centerSoftBorderX, centerSoftBorderY, softBorderW, softBorderH);


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

        //x tests
        if (p.getX() - screenX < softBorder.x) { //player is to the left of the soft border

            velocityX = Math.pow(softBorder.x - p.getX() + screenX, 2) * speedX;
            softBorder.x = (int) Math.min(softBorder.x + velocityX * 0.1, maxSoftBorderX);

            if (p.getX() - screenX < hardBorderX){ //hard border left
                velocityX = Math.max(velocityX, - p.getVelocityX());
            }

        }
        else if (p.getX() + p.getWidth() - screenX > softBorder.x + softBorder.width) { //player is to the right of the soft border

            velocityX = - Math.pow(p.getX() + p.getWidth() - screenX - softBorder.x - softBorder.width , 2) * speedX;
            softBorder.x = (int) Math.max(softBorder.x + velocityX * 0.1, minSoftBorderX);

            if (p.getX() + p.getWidth() - screenX > screenWidth - hardBorderX){ //hard borders right
                velocityX = Math.min(velocityX, - p.getVelocityX());
            }
        }
        else {
            stopMovementX();
        }

        if (Math.signum(p.velocityX) != Math.signum(centerSoftBorderX - softBorder.x)){ //recenter the soft border
            int screenVelocity = 10; //(int) Math.abs(p.getX() + (double) p.getWidth() /2 - softBorderCenter);
            softBorder.x = Math.max(Math.min(softBorder.x + screenVelocity, centerSoftBorderX), softBorder.x - screenVelocity); //replacing the soft border in the center of the screen
        }

        //y tests
        if (p.getY() - screenY < softBorder.y){ //player is over the soft border

            velocityY = Math.pow(softBorder.y - p.getY() + screenY, 2) * speedY;
            softBorder.y = (int) Math.max(softBorder.y + velocityY * 0.1, minSoftBorderY);

            if (p.getY() - screenY < hardBorderY){ //hard border up
                velocityY = Math.max(velocityY, - p.getVelocityY());
            }
        }
        else if(p.getY() + p.getHeight() - screenY > softBorder.y + softBorder.height){ //player is under the soft border

            velocityY = - Math.pow(p.getY() + p.getHeight() - screenY - softBorder.y - softBorder.height, 2) * speedY;
            softBorder.y = (int) Math.min(softBorder.y + velocityY * 0.1, maxSoftBorderY);

            if (p.getY() + p.getHeight() - screenY > screenHeight - hardBorderY){ //hard border down
                velocityY = Math.min(velocityY, - p.getVelocityY());
            }

        }
        else{
            stopMovementY();
        }

        if (Math.signum(p.velocityY) != Math.signum(centerSoftBorderY - softBorder.y)){ //recenter the soft border
            int screenVelocity = 10;
            softBorder.y = Math.max(Math.min(softBorder.y + screenVelocity, centerSoftBorderY), softBorder.y - screenVelocity); //replacing the soft border in the center of the screen
        }

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

    public Rectangle getSoftBorder() {
        return softBorder;
    }

    public int getSoftBorderX(){return softBorder.x;}

    public int getSoftBorderY(){return softBorder.y;}

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

    void stopMovementY(){
        if(Math.abs(velocityY) >= stoppingSpeedY){
            velocityY -= stoppingSpeedY * Math.signum(velocityY) * GamePanel.deltaTime;
        }else {
            velocityY = 0;
        }
    }

    void stopMovementX(){
        if(Math.abs(velocityX) >= stoppingSpeedX){
            velocityX -= stoppingSpeedX * Math.signum(velocityX) * GamePanel.deltaTime;
        }else {
            velocityX = 0;
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
