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
    double idleAnimationSpeed = 3;

    ArrayList<BufferedImage> run;
    double runAnimationSpeed = 0.9;

    ArrayList<BufferedImage> jump;
    double jumpAnimationSpeed = 1;

    ArrayList<BufferedImage> fall;
    double fallAnimationSpeed = 1;

    ArrayList<BufferedImage> fallFast;
    double fallFastAnimationSpeed = 1;

    ArrayList<BufferedImage> land;
    double landAnimationSpeed = 1.5;

    ArrayList<BufferedImage> death;
    double deathAnimationSpeed = 1;

    boolean wasJumping;
    boolean isDying;

    int[] spawnPointPos;

    public Player(int posX, int posY, String id) throws IOException {
        type = "Player";
        name = type + id;

        int width = 42;
        int height = 81;
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
        gravity = 2.25;

        maxJumps = 1000000000;

        spawnPointPos = new int[] {posX, posY};

        speedConversionPercent = 35;

        sprite = new Sprite(ImageIO.read(new File("assets/Player/idle/0.png")), 2.5);

        idle = getAnimationList("Player", "idle", 3);
        run = getAnimationList("Player", "run", 7);
        jump = getAnimationList("Player", "jump_up", 0);
        fall = getAnimationList("Player", "fall", 0);
        fallFast = getAnimationList("Player", "fall_fast", 0);
        land = getAnimationList("Player", "land", 1);
        death = getAnimationList("Player", "death", 6);

        setAnimation(idle, idleAnimationSpeed);
    }

    void movementHandler() throws IOException {
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
            sprite.setDirection(1);
            if (getAnimation() == idle){
                setAnimation(run, runAnimationSpeed);
            }
        }

        if (KeyHandler.isLeftPressed){
            walk(-1, s, a, speedConversionPercent);
            sprite.setDirection(-1);
            if (getAnimation() == idle){
                setAnimation(run, runAnimationSpeed);
            }
        }

        if (!KeyHandler.isRightPressed && !KeyHandler.isLeftPressed && velocityX != 0){
            int direction = (int) Math.signum(velocityX);
            stop(direction, f);
            if (isOnGround){
                setAnimation(idle, idleAnimationSpeed);
            }
        }

        if(isOnGround && (getAnimation() == fall || getAnimation() == fallFast || getAnimation() == jump)){
            setAnimation(land, landAnimationSpeed);
            setNextAnimation(idle, idleAnimationSpeed);
        }

        if (KeyHandler.isJumpPressed && jumps > 0 && !wasJumping){
            wasJumping= true;
            isJumping = true;
            jumps -= 1;
            jumpingTime = 0;
            }

        if(isJumping){
            jump(jumpForce);
            setAnimation(jump, jumpAnimationSpeed);
        }

        if (!KeyHandler.isJumpPressed){
            if (isJumping){
                velocityY /= 2;
                isJumping = false;
            }
            wasJumping = false;
        }

        if (!isJumping){
            fall(gravity);
            if (velocityY < -60){
                setAnimation(fallFast, fallFastAnimationSpeed);
            }else{
                if (velocityY < -10){
                    setAnimation(fall, fallAnimationSpeed);
                }
            }
        }

        if (getY() + GamePanel.camera.getY() > 1000 || KeyHandler.isSuicideKeyPressed || isDying){
            death(spawnPointPos);
        }

    }

    void death(int[] spawnPoint) throws IOException {
        velocityY = 0;
        velocityX = 0;

        if (!isDying){
            isDying = true;
            setAnimation(death, deathAnimationSpeed, 1);
            setNextAnimation(idle, idleAnimationSpeed);
        }
        else{
            if (getAnimation() != death){
                GamePanel.camera.move(spawnPoint[0] - GamePanel.camera.width/2, spawnPoint[1] - GamePanel.camera.height/2);
                setX(spawnPoint[0]);
                setY(spawnPoint[1]);

                isDying = false;
            }}
    }

    public void setSpawnPointPos(int posX, int posY){spawnPointPos = new int[] {posX, posY};}

    @Override
    public void update() throws IOException {

        animate();
        movementHandler();
        move();

        //System.out.println("X:" + getX() + " V: " + velocityX);
        //System.out.println("Y:" + getY() + " V: " + velocityY);
    }
}
