package GameObjects;

import handlers.KeyHandler;
import main.GamePanel;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Entity extends GameObject2D{
    double maxSpeed;
    double acceleration;
    double friction;

    double airMaxSpeed;
    double airAcceleration;
    double airFriction;

    double speedConversionPercent;

    double jumpForce;
    double jumpingTime;
    double gravity;

    int jumps;
    int maxJumps;
    double maxJumpingTime;

    double velocityY;
    double velocityX;

    boolean isOnGround;
    boolean isJumping;
    boolean wasJumping;

    public double getVelocityX(){return velocityX;}

    public double getVelocityY(){return velocityY;}

    public boolean getOnGround() {return isOnGround;}

    void walk(int direction, double maxSpeed, double acceleration, double speedConversion){
        if (direction == - Math.signum(velocityX)){
            velocityX = -velocityX * speedConversion / 100;
            return;
        }
        if (velocityX * direction < maxSpeed){
            velocityX += direction * acceleration;
        }
        else{
            velocityX = maxSpeed * direction;
        }
    }

    void stop(int direction, double friction){
        velocityX -= direction * friction;
        if (velocityX * direction <= 0){ //if forceX has crossed 0 since we stopped moving
            velocityX = 0;
        }
    }

    void jump(double jumpForce){

        jumpingTime += GamePanel.deltaTime;

        if (jumpingTime > maxJumpingTime){
            isJumping = false;
        }

        velocityY = jumpForce*20 - jumpingTime;
    }

    void fall(){
        if (isOnGround){
            velocityY = 0;
        }else{
            velocityY -= gravity * GamePanel.deltaTime * 6;
        }
    }

    void collisionX(Rectangle intersection){
        int direction = (int) Math.signum(intersection.getCenterX() - (getX() + getWidth()/2f) );
        setX(getX() - direction * intersection.width);
        if(!isOnGround){
            velocityX = 0;
            if(KeyHandler.isLeftPressed){
                KeyHandler.leftPressedTime = System.nanoTime();
            }
            if (KeyHandler.isRightPressed){
                KeyHandler.rightPressedTime = System.nanoTime();
            }
        }
    }

    void collisionY(Rectangle intersection){
        int direction = (int) Math.signum(intersection.getCenterY() - (getY() + getHeight()/2f) );
        velocityY = 0;
        if (! isOnGround){
            setY(getY() - direction * intersection.height);
        }
        if(direction == 1){ //if we are on ground
            isOnGround = true;
        }

    }

    ArrayList<GameObject2D> getNear(){

        ArrayList<int[]> thisEntityGridCells = GamePanel.camera.findRectPosInGrid(this);
        ArrayList<GameObject2D> result = new ArrayList<>();

        for ( int[] pos: thisEntityGridCells) {

            ArrayList<GameObject2D> cell = GamePanel.camera.getCellContent(pos[0], pos[1]);

            for (GameObject2D object: cell) {

                if (!result.contains(object) && object != this){

                    result.add(object);
                }
            }
        }
        return result;
    }

    void move() throws IOException {

        //x movement
        GamePanel.camera.deleteGOInGrid(this);

        setX((int) (getX() + Math.round(velocityX * GamePanel.deltaTime)));

        GamePanel.camera.addGOInGrid(this);

        for (GameObject2D go: getNear()) {
            if (this.hitbox.intersects(go.hitbox) ){
                if (go.hasPhysicalCollisions){
                    collisionX(this.hitbox.intersection(go.hitbox));
                }
                go.collision(this);
            }
        }


        //y movement
        GamePanel.camera.deleteGOInGrid(this);

        setY((int) (getY() - Math.round(velocityY * GamePanel.deltaTime)));
        isOnGround = false;
        GamePanel.camera.addGOInGrid(this);

        for (GameObject2D go: getNear()) {
            if (this.hitbox.intersects(go.hitbox)){
                if (go.hasPhysicalCollisions){
                    collisionY(this.hitbox.intersection(go.hitbox));
                }
                go.collision(this);
            }else{
                if(getY() + getHeight() >= go.getY() && getY() + getHeight() < go.getY() + 1 &&
                        getX() >= go.getX() && getX() <= go.getX() + go.getWidth() &&
                        go.hasPhysicalCollisions && !isJumping){
                    isOnGround = true;
                }
            }
        }
    }

}
