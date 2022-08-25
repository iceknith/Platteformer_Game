package GameObjects;

import java.awt.*;

import handlers.KeyHandler;
import main.GamePanel;

public class Player extends Entity{

    int oldX;
    int oldY;

    public Player() {
        int width = 50;
        int height = 75;
        int x = 100;
        int y = 100;
        hitbox = new Rectangle(x, y, width, height);

        velocityY = 0;
        velocityX = 0;

        speed = 40;
        jumpForce = 65;
        gravity = 2.5;
        friction = 5;

        color = Color.white;
    }

    public void updatePlayer(){

        //movement
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
        if (KeyHandler.isJumpPressed && isOnGround){
            jump();
        }
        if (!isOnGround){
            fall();
        }

        move();

        //System.out.println("X:" + getX() + " V: " + velocityX);
        //System.out.println("Y:" + getY() + " V: " + velocityY);
    }
}
