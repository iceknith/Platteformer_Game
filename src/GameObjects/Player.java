package GameObjects;

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

    boolean isDying;

    int[] spawnPointPos;

    public Player(int posX, int posY, String id, String subLvlName) throws IOException {
        super(posX,posY,42,81, subLvlName);

        type = "Player";
        name = type + id;

        velocityY = 0;
        velocityX = 0;

        maxSpeed = 45;
        acceleration = 3;
        friction = 5;

        airMaxSpeed = 35;
        airAcceleration = 2;
        airFriction = 2.5;

        jumpForce = 3;
        gravity = 2.25;

        maxJumps = 0;
        maxJumpingTime = 1.2;

        spawnPointPos = new int[] {posX, posY};

        speedConversionPercent = 35;

        sprite = new Sprite(ImageIO.read(new File("assets/Player/idle/0.png")), 2.5, hitbox);

        idle = getAnimationList("Player", "idle", 3);
        run = getAnimationList("Player", "run", 7);
        jump = getAnimationList("Player", "jump_up", 0);
        fall = getAnimationList("Player", "fall", 0);
        fallFast = getAnimationList("Player", "fall_fast", 0);
        land = getAnimationList("Player", "land", 1);
        death = getAnimationList("Player", "death", 6);

        setAnimation(idle, idleAnimationSpeed);
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

        //right movement
        if (KeyHandler.isRightPressed){
            walk(1, s, a, speedConversionPercent);
            sprite.setDirection(1);
            if (getAnimation() == idle){
                setAnimation(run, runAnimationSpeed);
            }
        }

        //left movement
        if (KeyHandler.isLeftPressed){
            walk(-1, s, a, speedConversionPercent);
            sprite.setDirection(-1);
            if (getAnimation() == idle){
                setAnimation(run, runAnimationSpeed);
            }
        }

        //stopping
        if (!KeyHandler.isRightPressed && !KeyHandler.isLeftPressed && velocityX != 0){
            int direction = (int) Math.signum(velocityX);
            stop(direction, f);
            if (isOnGround && getAnimation() != idle){
                setAnimation(idle, idleAnimationSpeed);
            }
        }

        //landing
        if(isOnGround && (getAnimation() == fall || getAnimation() == fallFast || getAnimation() == jump)){
            setAnimation(land, landAnimationSpeed);
            setNextAnimation(idle, idleAnimationSpeed);
        }

        //jumping logic
        if (KeyHandler.isJumpPressed && jumps > 0 && !wasJumping){
            isJumping = true;
            wasJumping = true;
            jumps -= 1;
            jumpingTime = 0;
        }

        if(isJumping && KeyHandler.isJumpPressed){
            jump(jumpForce);
            setAnimation(jump, jumpAnimationSpeed);
        }

        if (wasJumping){
            if(!KeyHandler.isJumpPressed){
                isJumping = false;
                if (velocityY > 0){
                    velocityY /= 1.25;
                }else{
                    wasJumping = false;
                }
            }
        }

        //falling
        fall();
        if (velocityY < -60) {
            setAnimation(fallFast, fallFastAnimationSpeed);
        } else {
            if (velocityY < -10) {
                setAnimation(fall, fallAnimationSpeed);
            }
        }


        if (getY() + GamePanel.camera.getY() > 1000 || KeyHandler.isSuicideKeyPressed || isDying){
            death(spawnPointPos);
        }

    }

    void death(int[] spawnPoint){
        velocityY = 0;
        velocityX = 0;

        if (!isDying){
            isDying = true;
            setAnimation(death, deathAnimationSpeed, 1);
            setNextAnimation(idle, idleAnimationSpeed);
        }
        else{
            if (getAnimation() != death){
                GamePanel.camera.instantMove(spawnPoint[0] - GamePanel.camera.width/2, spawnPoint[1] - GamePanel.camera.height/2);
                setX(spawnPoint[0]);
                setY(spawnPoint[1]);

                isDying = false;
            }
        }
    }

    public void setSpawnPointPos(int posX, int posY){
        spawnPointPos = new int[] {posX, posY};
        //temporary mechanic
        maxJumps += 1;
    }

    @Override
    public void update() throws IOException {
        animate();
        movementHandler();
        move();
    }
}
