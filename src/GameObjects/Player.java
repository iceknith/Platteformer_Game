package GameObjects;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import handlers.KeyHandler;
import main.GamePanel;

import javax.imageio.ImageIO;

public class Player extends Entity{

    double speedThreshold;
    double earlySpeed;
    double maxSpeed;
    double earlyAcceleration;
    double lateAcceleration;
    double friction;

    double airSpeedThreshold;
    double airEarlySpeed;
    double airMaxSpeed;
    double airEarlyAcceleration;
    double airLateAcceleration;
    double airFriction;

    double speedConversionPercent;

    double jumpForce;
    double jumpingTime;
    double gravity;

    public double groundVelocityX;
    public double groundVelocityY;

    int jumps;
    int maxJumps;
    double maxJumpingTime;
    boolean isOnGround;
    boolean wasOnGround;
    boolean isJumping;
    boolean wasJumping;

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

    boolean wasJumpPressed;

    int[] spawnPointPos;

    final int jumpBubblesDistance = 100;
    final int jumpBubblesRadius = 15;
    final Color jumpBublesOutlineColor = new Color(.07f, .09f, .11f, .75f);
    final Color jumpBublesColor = new Color(0.64f, .88f, 1f, .5f);

    public Player(int posX, int posY, String id, String subLvlName) throws IOException {
        super(posX,posY,42,81, subLvlName);

        type = "Player";
        name = type + id;

        prevX = getX();
        prevY = getY();

        velocityY = 0;
        velocityX = 0;

        speedThreshold = 5;
        earlySpeed = 45;
        maxSpeed = 55;
        earlyAcceleration = 3;
        lateAcceleration = 0.005;
        friction = 2.5;

        airSpeedThreshold = 15;
        airEarlySpeed = 35;
        airMaxSpeed = 45;
        airEarlyAcceleration = 1.5;
        airLateAcceleration = 0.01;
        airFriction = 1;

        jumpForce = 3;
        gravity = 2.25;

        maxJumps = 0;
        maxJumpingTime = 1.2;

        spawnPointPos = new int[] {posX, posY};

        speedConversionPercent = 35;

        sprite = new Sprite(ImageIO.read(new File("assets/Player/idle/0.png")), 2.5);
        sprite.offsetY -= 1; //make it so that the player is visually in the ground

        idle = getAnimationList("Player", "idle", 3);
        run = getAnimationList("Player", "run", 7);
        jump = getAnimationList("Player", "jump_up", 0);
        fall = getAnimationList("Player", "fall", 0);
        fallFast = getAnimationList("Player", "fall_fast", 0);
        land = getAnimationList("Player", "land", 1);
        death = getAnimationList("Player", "death", 6);

        setAnimation(idle, idleAnimationSpeed);
    }

    Player(Player p) throws IOException {
        super(p);

        type = p.type;
        name = p.name;

        velocityY = p.velocityY;
        velocityX = p.velocityX;

        speedThreshold = p.speedThreshold;
        earlySpeed = p.earlySpeed;
        maxSpeed = p.maxSpeed;
        lateAcceleration = p.lateAcceleration;
        earlyAcceleration = p.earlyAcceleration;
        friction = p.friction;

        airSpeedThreshold = p.airSpeedThreshold;
        airEarlySpeed = p.airEarlySpeed;
        airMaxSpeed = p.airMaxSpeed;
        airLateAcceleration = p.airLateAcceleration;
        airEarlyAcceleration = p.airEarlyAcceleration;
        airFriction = p.airFriction;

        jumpForce = p.jumpForce;
        jumpingTime = p.jumpingTime;
        gravity = p.gravity;

        jumps = p.jumps;
        maxJumps = p.maxJumps;
        maxJumpingTime = p.maxJumpingTime;

        spawnPointPos = p.spawnPointPos;
        isOnGround = p.isOnGround;
        wasOnGround = p.wasOnGround;
        isJumping = p.isJumping;
        wasJumping = p.wasJumping;

        speedConversionPercent = p.speedConversionPercent;

        sprite = new Sprite(ImageIO.read(new File("assets/Player/idle/0.png")), 2.5);

        idle = p.idle;
        run = p.run;
        jump = p.jump;
        fall = p.fall;
        fallFast = p.fallFast;
        land = p.land;
        death = p.death;

        idleAnimationSpeed = p.idleAnimationSpeed;
        runAnimationSpeed = p.runAnimationSpeed;
        jumpAnimationSpeed = p.jumpAnimationSpeed;
        fallAnimationSpeed = p.fallAnimationSpeed;
        fallFastAnimationSpeed = p.fallFastAnimationSpeed;
        landAnimationSpeed = p.landAnimationSpeed;
        deathAnimationSpeed = p.deathAnimationSpeed;

        setAnimation(idle, idleAnimationSpeed);

        isDying = p.isDying;

        wasJumpPressed = p.wasJumpPressed;

    }

    void movementHandler(){
        double sTh = speedThreshold;
        double eS = earlySpeed;
        double s = maxSpeed;
        double eAcc = earlyAcceleration;
        double lAcc = lateAcceleration;
        double f = friction;

        if(isOnGround){
            jumps = maxJumps;

        }else{
            sTh = airSpeedThreshold;
            eS = airEarlySpeed;
            s = airMaxSpeed;
            eAcc = airEarlyAcceleration;
            lAcc = airLateAcceleration;
            f = airFriction;
        }


        if (KeyHandler.isRightPressed && KeyHandler.isLeftPressed){
            //making the last input the dominant one
            KeyHandler.isRightPressed = KeyHandler.rightPressedTime > KeyHandler.leftPressedTime;
            KeyHandler.isLeftPressed = !KeyHandler.isRightPressed;
        }

        //right movement
        if (KeyHandler.isRightPressed){
            walk(1, eS, s, sTh, eAcc, lAcc, speedConversionPercent);
            sprite.setDirection(1);
            if (getAnimation().equals(idle)){
                setAnimation(run, runAnimationSpeed);
            }
        }

        //left movement
        if (KeyHandler.isLeftPressed){
            walk(-1, eS, s, sTh, eAcc, lAcc, speedConversionPercent);
            sprite.setDirection(-1);
            if (getAnimation().equals(idle)){
                setAnimation(run, runAnimationSpeed);
            }
        }

        //stopping
        if (!KeyHandler.isRightPressed && !KeyHandler.isLeftPressed){
            if (velocityX != 0){
                int direction = (int) Math.signum(velocityX);
                stop(direction, f);
            }
            if (isOnGround && getAnimation().equals(run)){
                setAnimation(idle, idleAnimationSpeed);
            }
        }

        //landing
        if(isOnGround && (getAnimation().equals(fall) || getAnimation().equals(fallFast) || getAnimation().equals(jump))){
            setAnimation(land, landAnimationSpeed);
            setNextAnimation(idle, idleAnimationSpeed);
        }

        //jumping logic
        if (KeyHandler.isJumpPressed && jumps > 0 && !wasJumpPressed){
            wasJumpPressed = true;
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
            if(!KeyHandler.isJumpPressed) {
                wasJumpPressed = false;
                isJumping = false;
                if (velocityY > 0) {
                    velocityY /= 1.25;
                } else {
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


        if (getY() + GamePanel.camera.getScreenY() > 2000 || KeyHandler.isSuicideKeyPressed || isDying){
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
            if (!getAnimation().equals(death)){
                GamePanel.camera.move(spawnPoint[0] - GamePanel.camera.screenWidth /2, spawnPoint[1] - GamePanel.camera.screenHeight /2);

                setX(spawnPoint[0]);
                setY(spawnPoint[1]);

                isDying = false;
            }}
    }

    public void setSpawnPointPos(int posX, int posY){
        spawnPointPos = new int[] {posX, posY};
    }

    @Override
    public void update() throws IOException {
        animate();
        movementHandler();
        move();

        //System.out.println("X:" + getX() + " V: " + velocityX);
        //System.out.println("Y:" + getY() + " V: " + velocityY);
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO){
        super.draw(g2D, IO);

        //drawing jump bubbles
        for (int i = 0; i < maxJumps; i++){

            double angle = Math.PI * 2 - Math.PI/2 - (Math.PI/12) * (maxJumps - 1)/2 + Math.PI * i/12;
            int posX = (int) (Math.cos(angle) * jumpBubblesDistance + getX() + getWidth()/2 - GamePanel.camera.getScreenX());
            int posY = (int) (Math.sin(angle) * jumpBubblesDistance + getY() + getHeight()/2 - GamePanel.camera.getScreenY());

            if (i < jumps){
                g2D.setColor(jumpBublesColor);
                g2D.fillOval(posX, posY, jumpBubblesRadius, jumpBubblesRadius);
            }

            g2D.setColor(jumpBublesOutlineColor);
            g2D.drawOval(posX, posY, jumpBubblesRadius, jumpBubblesRadius);
        }
    }

    public boolean getOnGround() {return isOnGround;}

    void walk(int direction,
              double earlySpeed, double maxSpeed,
              double maxSpeedThreshold,
              double earlyAcceleration, double lateAcceleration,
              double speedConversion){

        //if you changed direction
        if (direction == - Math.signum(velocityX)){
            velocityX = -velocityX * speedConversion / 100;
            return;
        }
        //earlyAcceleration
        if (velocityX * direction < earlySpeed){
            velocityX += direction * earlyAcceleration;
        }
        //lateAcceleration
        else if (velocityX * direction < maxSpeed) {
            velocityX += direction * lateAcceleration;
        }
        //lateDeceleration (if your speed is greater than the maxSpeed + speedThreshold)
        else if (velocityX * direction > maxSpeed + maxSpeedThreshold){
            velocityX -= direction * lateAcceleration;
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
        if (! isOnGround){
            velocityY = Math.max(-150, velocityY - (gravity * GamePanel.deltaTime * 6));
        }
    }

    boolean collision(GameObject2D go) {

        if (getY() + getHeight() < go.getY() || getY() > go.getY() + go.getHeight() ||
                getX() > go.getX() + go.getWidth() || getX() + getWidth() < go.getX()){
            return false;
        }
        if(getX() + getWidth() >= go.getX() && getPreviousX() + getWidth() < go.getPreviousX()){
            if (go.hasPhysicalCollisions){
                setX(go.getX() - getWidth() - 1);
                velocityX = go.getVelocityX();
            }
            go.collision(this);
            return true;
        }

        if(getX() <= go.getX() + go.getWidth() && getPreviousX() > go.getPreviousX() + go.getWidth()){
            if (go.hasPhysicalCollisions){
                setX(go.getX() + go.getWidth() + 1);
                velocityX = go.getVelocityX();
            }
            go.collision(this);
            return true;
        }

        if(getY() + getHeight() >= go.getY() && getPreviousY() + getHeight() <= go.getPreviousY()){
            if (go.hasPhysicalCollisions){
                isOnGround = true;
                setY(go.getY() - getHeight() - 1);
                velocityY = go.getVelocityY();
            }
            go.collision(this);
            return true;
        }

        if(getY() <= go.getY() + go.getHeight() && getPreviousY() > go.getPreviousY() + go.getHeight()){
            if (go.hasPhysicalCollisions){
                setY(go.getY() + go.getHeight() + 1);
                velocityY = go.getVelocityY();
            }
            go.collision(this);
            return true;
        }
        return false;
    }

    void checkGround(GameObject2D go){
        if (wasOnGround && go.hasPhysicalCollisions){
            if (getY() + getHeight() < go.getY() - 2 || getY() > go.getY() + go.getHeight() ||
                    getX() > go.getX() + go.getWidth() || getX() + getWidth() < go.getX()){
                if (Math.abs(velocityX + groundVelocityX) < earlySpeed) groundVelocityX = go.getVelocityX();
                groundVelocityY = go.getVelocityY();
                return;
            }

            isOnGround = getY() + getHeight() >= go.getY() - 2 && getPreviousY() + getHeight() <= go.getPreviousY();
            if (isOnGround){
                if (Math.abs(velocityX + groundVelocityX) < earlySpeed) groundVelocityX = go.getVelocityX();
                groundVelocityY = go.getVelocityY();
            }
        }
    }

    @Override
    public void move(){
        GamePanel.camera.deleteGOInGrid(this, false);
        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round((velocityX + groundVelocityX) * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round((velocityY + groundVelocityY) * GamePanel.deltaTime)));
        GamePanel.camera.addGOInGrid(this, false);

        wasOnGround = isOnGround;
        isOnGround = false;
        for (GameObject2D go: getNear()){
            checkGround(go);
            collision(go);
        }
        if (!isOnGround){
            velocityX += groundVelocityX;
            velocityY += groundVelocityY;
            groundVelocityX = 0;
            groundVelocityY = 0;
        }
    }

    @Override
    public Player copy() throws IOException {
        return new Player(this);
    }

    @Override
    public Player getThisPlayer(){
        return this;
    }
}
