package GameObjects;

import java.awt.*;

import handlers.KeyHandler;

public class Player extends Entity{


    public Player() {
        int width = 50;
        int height = 75;
        int x = 100;
        int y = 100;
        hitbox = new Rectangle(x, y, width, height);

        velocityY = 0;
        velocityX = 0;

        maxSpeed = 40;
        acceleration = 6;
        friction = 7.5;

        jumpForce = 45;
        maxJumpTime = 10;
        gravity = 2.5;

        maxJumps = 1;

        speedConversionPercent = 50;

        color = Color.white;
    }

    public void updatePlayer(){

        //movement
        if (KeyHandler.isRightPressed && KeyHandler.isLeftPressed){
            //making the last input the dominant one
            KeyHandler.isRightPressed = KeyHandler.rightPressedTime > KeyHandler.leftPressedTime;
            KeyHandler.isLeftPressed = !KeyHandler.isRightPressed;
        }
        if (KeyHandler.isRightPressed){
            walk(1,(System.nanoTime() - KeyHandler.rightPressedTime) / 100000000);
        }
        if (KeyHandler.isLeftPressed){
            walk(-1,(System.nanoTime() - KeyHandler.leftPressedTime) / 100000000);
        }
        if (!KeyHandler.isRightPressed && !KeyHandler.isLeftPressed && velocityX != 0){
            int direction = (int) Math.signum(velocityX);
            stop(direction, (System.nanoTime() - KeyHandler.noMovementTime) / 100000000);
        }
        if (KeyHandler.isJumpPressed && jumps > 0|| isJumping){
            jumps -= 1;
            jump();
        }
        if(isOnGround){
            jumps = maxJumps;
        }
        if (!KeyHandler.isJumpPressed){
            isJumping = false;
        }
        if (!isOnGround && !isJumping){
            fall();
        }

        move();

        //System.out.println("X:" + getX() + " V: " + velocityX);
        //System.out.println("Y:" + getY() + " V: " + velocityY);
    }
}
