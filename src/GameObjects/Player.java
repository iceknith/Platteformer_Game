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

        maxSpeed = 45;
        acceleration = 3;
        friction = 5;

        airMaxSpeed = 35;
        airAcceleration = 2;
        airFriction = 2.5;

        jumpForce = 70;
        gravity = 2.5;

        maxJumps = 1;

        speedConversionPercent = 35;

        color = Color.white;
    }

    public void updatePlayer(){

        //movement

        double s = maxSpeed;
        double a = acceleration;
        double f = friction;

        if(isOnGround){
            jumps = maxJumps;

        }else{
            s = airMaxSpeed;
            a = airAcceleration;
            f = airFriction;
        }

        if (KeyHandler.isRightPressed && KeyHandler.isLeftPressed){
            //making the last input the dominant one
            KeyHandler.isRightPressed = KeyHandler.rightPressedTime > KeyHandler.leftPressedTime;
            KeyHandler.isLeftPressed = !KeyHandler.isRightPressed;
        }

        if (KeyHandler.isRightPressed){
            walk(1, s, a, speedConversionPercent);
        }

        if (KeyHandler.isLeftPressed){
            walk(-1, s, a, speedConversionPercent);
        }

        if (!KeyHandler.isRightPressed && !KeyHandler.isLeftPressed && velocityX != 0){
            int direction = (int) Math.signum(velocityX);
            stop(direction, f);
        }

        if (KeyHandler.isJumpPressed && jumps > 0|| isJumping){
            jumps -= 1;
            jump(jumpForce);
        }

        if (!KeyHandler.isJumpPressed && isJumping){
            velocityY /= 2;
            isJumping = false;
        }

        if (!isJumping){
            fall(gravity);
        }

        move();

        //System.out.println("X:" + getX() + " V: " + velocityX);
        //System.out.println("Y:" + getY() + " V: " + velocityY);
    }
}
