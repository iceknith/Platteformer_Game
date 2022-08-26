package GameObjects;

import handlers.KeyHandler;
import main.GamePanel;

import java.awt.*;
import java.util.ArrayList;

public class Entity extends GameObject2D{
    double maxSpeed;
    double acceleration;
    double friction;

    double speedConversionPercent;

    double jumpForce;
    double maxJumpTime;
    double jumpingTime;
    double gravity;

    int jumps;
    int maxJumps;

    double velocityY;
    double velocityX;

    boolean isOnGround;
    boolean isJumping;

    void walk(int direction, long time){
        if (direction == - Math.signum(velocityX)){
            velocityX = -velocityX * speedConversionPercent / 100;
            return;
        }
        if (velocityX * direction < maxSpeed){
            velocityX += direction * acceleration;
        }
        else{
            velocityX = maxSpeed * direction;
        }
    }

    void stop(int direction, long time){
        velocityX -= direction * friction;
        if (velocityX * direction < 0){ //if forceX has crossed 0 since we stopped moving
            velocityX = 0;
        }
    }

    void jump(){
        if (isJumping){
            jumpingTime++;
            if (jumpingTime >= maxJumpTime){
                isJumping = false;
            }
        }
        else{
            isJumping = true;
            jumpingTime = 0;
        }
        velocityY = jumpForce;
    }

    void fall(){
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

        ArrayList<int[]> thisEntityGridCells = GamePanel.grid.findRectPosInGrid(this);
        ArrayList<GameObject2D> result = new ArrayList<>();

        for ( int[] pos: thisEntityGridCells) {

            ArrayList<GameObject2D> cell = GamePanel.grid.getCellContent(pos[0], pos[1]);

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
        setX((int) (getX() + velocityX * GamePanel.deltaTime));

        GamePanel.grid.deleteRectInGrid(this);
        GamePanel.grid.addRectInGrid(this);

        for (GameObject2D r: getNear()) {
            if (this.hitbox.intersects(r.hitbox)){
                collisionX(this.hitbox.intersection(r.hitbox));
            }
        }


        //y movement
        setY((int) (getY() - velocityY * GamePanel.deltaTime));
        isOnGround = false;

        GamePanel.grid.deleteRectInGrid(this);
        GamePanel.grid.addRectInGrid(this);

        for (GameObject2D r: getNear()) {
            if (this.hitbox.intersects(r.hitbox)){
                collisionY(this.hitbox.intersection(r.hitbox));
            }
        }
    }

}
