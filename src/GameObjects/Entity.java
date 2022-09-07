package GameObjects;

import handlers.KeyHandler;
import main.GamePanel;

import java.awt.*;
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

    double velocityY;
    double velocityX;

    boolean isOnGround;
    boolean isJumping;

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

        if (isJumping){
            jumpingTime++;
            if (velocityY <= 0){
                isJumping = false;
            }
        }

        else{
            isJumping = true;
            jumpingTime = 0;
        }

        velocityY = jumpForce - jumpingTime * gravity;
    }

    void fall(double gravity){
        if (isOnGround){ //increases the gravity when player is likely to be on ground,
            gravity = 6; //to make the player stay on ground and not bump up and down
        }
        velocityY -= gravity;
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

        setY(getY() - direction * intersection.height);
        velocityY = 0;

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

    void move(){

        //x movement
        GamePanel.camera.deleteGOInGrid(this);

        setX((int) (getX() + velocityX * GamePanel.deltaTime));

        GamePanel.camera.addGOInGrid(this);

        for (GameObject2D r: getNear()) {
            if (this.hitbox.intersects(r.hitbox)){
                collisionX(this.hitbox.intersection(r.hitbox));
            }
        }


        //y movement
        GamePanel.camera.deleteGOInGrid(this);

        setY((int) (getY() - velocityY * GamePanel.deltaTime));
        isOnGround = false;

        GamePanel.camera.addGOInGrid(this);

        for (GameObject2D r: getNear()) {
            if (this.hitbox.intersects(r.hitbox)){
                collisionY(this.hitbox.intersection(r.hitbox));
            }
        }
    }

}
