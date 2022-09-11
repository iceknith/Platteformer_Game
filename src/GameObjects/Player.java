package GameObjects;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import handlers.KeyHandler;
import main.GamePanel;

import javax.imageio.ImageIO;

public class Player extends Entity{

    ArrayList<BufferedImage> idle;


    public Player(int posX, int posY) throws IOException {
        int width = 50;
        int height = 75;
        hitbox = new Rectangle(posX, posY, width, height);

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

        sprite = ImageIO.read(new File("assets/Player/idle/0.png"));
        idle = getAnimationList("Player", "idle", 17);
        setAnimation(idle);
    }

    void movementHandler(){
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

        if (getY() + GamePanel.camera.getY() > 1000){
            velocityY = 0;
            velocityX = 0;
            GamePanel.camera.move(0, 0);
            setX(200);
            setY(100);
        }
    }

    double animTime = 0;

    public void updatePlayer(){

        animate();
        movementHandler();
        move();

        //System.out.println("X:" + getX() + " V: " + velocityX);
        //System.out.println("Y:" + getY() + " V: " + velocityY);
    }
}
