package GameObjects;

import java.awt.*;

import handlers.KeyHandler;
import main.GamePanel;

public class Player extends GameObject2D{

    final double speed = 40;
    final double jumpForce = 13;

    double velocityY;
    double velocityX;

    boolean isOnGround;

    public Player() {
        int width = 50;
        int height = 75;
        int x = 100;
        int y = 100;
        hitbox = new Rectangle(x, y, width, height);
        velocityY = 0;
        velocityX = 0;
        color = Color.white;
    }

    void move(int direction, long time){
        if (time < Math.PI / 2){
            velocityX = speed * Math.sin(direction * time) * GamePanel.deltaTime;
        }
        else{
            velocityX = speed * direction * GamePanel.deltaTime;
        }
    }

    void stop(int direction, long time){
        velocityX -= speed * Math.sin(direction * time / 5f) * GamePanel.deltaTime;
        if (velocityX *direction < 0){ //if forceX has crossed 0 since we stopped moving
            velocityX = 0;
        }
    }

    void jump(){ velocityY += jumpForce;
    isOnGround = false;}

    void playerCollision(Rectangle r){

        Rectangle intersection = hitbox.intersection(r);
        int directionX = (int) Math.signum(intersection.getCenterX() - (getX() + getWidth()/2f) );
        int directionY = (int) Math.signum(intersection.getCenterY() - (getY() + getHeight()/2f) );


        if (intersection.equals(hitbox)){
            System.out.println("OH SHIT, WE ARE INSIDE A PLATFORM");
            return;
        }
        if (intersection.width > intersection.height + 5){ //5 is to prioritize width collision, so that the player doesn't bump up every corner he encounters
            velocityY = 0;
            setY(getY() - directionY * intersection.height);
            if (directionY > 0){
                isOnGround = true;
            }
        }
        else {
            setX(getX() - directionX * intersection.width);
            //re-initializing values
            velocityX = 0;
            if(KeyHandler.isLeftPressed){
                KeyHandler.leftPressedTime = System.nanoTime();
            }
            if (KeyHandler.isRightPressed){
                KeyHandler.rightPressedTime = System.nanoTime();
            }
        }


    }

    public void updatePlayer(){

        //movement
        if (KeyHandler.isRightPressed){
            move(1,(System.nanoTime() - KeyHandler.rightPressedTime) / 100000000);
        }
        if (KeyHandler.isLeftPressed){
            move(-1,(System.nanoTime() - KeyHandler.leftPressedTime) / 100000000);
        }
        if (!KeyHandler.isRightPressed && !KeyHandler.isLeftPressed && velocityX != 0){
            int direction = (int) (velocityX / Math.abs(velocityX));
            stop(direction, (System.nanoTime() - KeyHandler.noMovementTime) / 100000000);
        }
        if (KeyHandler.isJumpPressed && isOnGround){
            jump();
        }
        if (!isOnGround){
            velocityY -= 0.5;
        }

        setY((int) (getY()- velocityY));
        setX((int) (getX()+ velocityX));


        //collision detection
        isOnGround = false;

        for (Platform p : Platform.visiblePlatforms) {
            if (hitbox.intersects(p.hitbox)){
                playerCollision(p.hitbox);
            }
        }

        //System.out.println("X:" + getX() + " V: " + velocityX);
        //System.out.println("Y:" + getY() + "V" + velocityY);
    }
}
