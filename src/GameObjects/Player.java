package GameObjects;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

import handlers.KeyHandler;
import main.GamePanel;

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
    double defaultGravity;
    double gravity;
    double defaultMaxYSpeed;
    double maxYSpeed;

    public GameObject2D ground;
    public boolean doGroundVelocityXCount;

    int jumps;
    int maxJumps;
    double maxJumpingTime;
    boolean isOnGround;
    boolean wasOnGround;
    boolean isJumping;
    boolean wasJumping;

    double deathYLine;

    ArrayList<BufferedImage> idle;
    double idleAnimationSpeed;
    int idleOffsetX;
    int idleOffsetY;

    ArrayList<BufferedImage> run;
    double runAnimationSpeed;
    int runOffsetX;
    int runOffsetY;

    ArrayList<BufferedImage> jump;
    double jumpAnimationSpeed;
    int jumpOffsetX;
    int jumpOffsetY;

    ArrayList<BufferedImage> fall;
    double fallAnimationSpeed;
    int fallOffsetX;
    int fallOffsetY;

    ArrayList<BufferedImage> fallFast;
    double fallFastAnimationSpeed;
    int fastFallOffsetX;
    int fastFallOffsetY;

    ArrayList<BufferedImage> land;
    double landAnimationSpeed;
    int landOffsetX;
    int landOffsetY;

    ArrayList<BufferedImage> death;
    double deathAnimationSpeed;
    int deathOffsetX;
    int deathOffsetY;

    int originalOffsetX;
    int originalOffsetY;

    boolean isDying;

    boolean wasJumpPressed;

    int[] spawnPointPos;
    public boolean hasTakenCheckpoint = false;

    final int jumpBubblesDistance = 100;
    final int jumpBubblesRadius = 15;
    final Color jumpBublesOutlineColor = new Color(.07f, .09f, .11f, .75f);
    final Color jumpBublesColor = new Color(0.64f, .88f, 1f, .5f);

    public Player(int posX, int posY, String id, String subLvlName) throws IOException {
        super(posX,posY,39,96, subLvlName);

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
        defaultGravity = 2.25;
        gravity = defaultGravity;
        defaultMaxYSpeed = 150;
        maxYSpeed = defaultMaxYSpeed;

        maxJumps = 0;
        maxJumpingTime = 1.2;

        spawnPointPos = new int[] {posX, posY};

        speedConversionPercent = 35;

        deathYLine = 2000;

        sprite = new Sprite(readImageBuffered("assets/Player/idle/0.png"), 3);

        idle = getAnimationList("Player", "idle", 3);
        idleOffsetX = -21;
        idleOffsetY = 3;
        idleAnimationSpeed = 3;

        run = getAnimationList("Player", "run", 7);
        runOffsetX = -24;
        runOffsetY = 3;
        runAnimationSpeed = 0.9;

        jump = getAnimationList("Player", "jump_up", 0);
        jumpOffsetX = -21;
        jumpOffsetY = 6;
        jumpAnimationSpeed = 1;

        fall = getAnimationList("Player", "fall", 0);
        fallOffsetX = 3;
        fallOffsetY = 3;
        fallAnimationSpeed = 1;

        fallFast = getAnimationList("Player", "fall_fast", 0);
        fastFallOffsetX = 6;
        fastFallOffsetY = 12;
        fallFastAnimationSpeed = 1;

        land = getAnimationList("Player", "land", 1);
        landOffsetX = -21;
        landOffsetY = 3;
        landAnimationSpeed = 2;

        death = getAnimationList("Player", "death", 33);
        deathOffsetX = 0;
        deathOffsetY = 0;
        deathAnimationSpeed = 0.2;

        setAnimation(idle, idleAnimationSpeed, idleOffsetX, idleOffsetY);
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

        airSpeedThreshold = p.airSpeedThreshold;
        airEarlySpeed = p.airEarlySpeed;
        airMaxSpeed = p.airMaxSpeed;
        airLateAcceleration = p.airLateAcceleration;
        airEarlyAcceleration = p.airEarlyAcceleration;
        airFriction = p.airFriction;

        jumpForce = p.jumpForce;
        jumpingTime = p.jumpingTime;
        defaultGravity = p.defaultGravity;
        gravity = p.gravity;
        defaultMaxYSpeed = p.defaultMaxYSpeed;
        maxYSpeed = p.maxYSpeed;

        jumps = p.jumps;
        maxJumps = p.maxJumps;
        maxJumpingTime = p.maxJumpingTime;

        spawnPointPos = p.spawnPointPos;
        hasTakenCheckpoint = p.hasTakenCheckpoint;
        isOnGround = p.isOnGround;
        wasOnGround = p.wasOnGround;
        isJumping = p.isJumping;
        wasJumping = p.wasJumping;

        speedConversionPercent = p.speedConversionPercent;

        deathYLine = p.deathYLine;

        sprite = new Sprite(readImageBuffered("assets/Player/idle/0.png"), 2.5);

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

        setAnimation(idle, idleAnimationSpeed, idleOffsetX, idleOffsetY);

        wasJumpPressed = p.wasJumpPressed;

    }

    void movementHandler() throws Exception {
        double sTh = speedThreshold;
        double eS = earlySpeed;
        double s = maxSpeed;
        double eAcc = earlyAcceleration;
        double lAcc = lateAcceleration;

        if(isOnGround){
            jumps = maxJumps;

        }else{
            sTh = airSpeedThreshold;
            eS = airEarlySpeed;
            s = airMaxSpeed;
            eAcc = airEarlyAcceleration;
            lAcc = airLateAcceleration;
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
                setAnimation(run, runAnimationSpeed, runOffsetX, runOffsetY);
            }
        }

        //left movement
        if (KeyHandler.isLeftPressed){
            walk(-1, eS, s, sTh, eAcc, lAcc, speedConversionPercent);
            sprite.setDirection(-1);
            if (getAnimation().equals(idle)){
                setAnimation(run, runAnimationSpeed, runOffsetX, runOffsetY);
            }
        }

        //stopping
        if (!KeyHandler.isRightPressed && !KeyHandler.isLeftPressed){
            if (velocityX != 0){
                int direction = (int) Math.signum(velocityX);
                stop(direction, friction);
            }
            if (isOnGround && getAnimation().equals(run)){
                setAnimation(idle, idleAnimationSpeed, idleOffsetX, idleOffsetY);
            }
        }

        //down movement
        if (KeyHandler.isDownPressed){
            if (gravity != 1.5*defaultGravity){
                gravity = 1.5*defaultGravity;
                maxYSpeed = 1.4*defaultMaxYSpeed;
            }
        }

        //up movement
        else if (KeyHandler.isUpPressed){
            if (gravity != 0.9*defaultGravity){
                gravity = 0.9*defaultGravity;
            }
        }

        //reset y movement to default settings
        else if (gravity != defaultGravity){
            gravity = defaultGravity;
            maxYSpeed = defaultMaxYSpeed;
        }

        //landing
        if(isOnGround && (getAnimation().equals(fall) || getAnimation().equals(fallFast) || getAnimation().equals(jump))){
            setAnimation(land, landAnimationSpeed, landOffsetX, landOffsetY);
            setNextAnimation(idle, idleAnimationSpeed, idleOffsetX, idleOffsetY);
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
            setAnimation(jump, jumpAnimationSpeed, jumpOffsetX, jumpOffsetY);
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
            setAnimation(fallFast, fallFastAnimationSpeed, fastFallOffsetX, fastFallOffsetY);
        } else {
            if (velocityY < -10) {
                setAnimation(fall, fallAnimationSpeed, fallOffsetX, fallOffsetY);
            }
        }


        if (getY() > deathYLine || KeyHandler.isSuicideKeyPressed || isDying){
            death(spawnPointPos);
        }

        // Reset Level
        if (KeyHandler.isResetKeyPressed){
            GamePanel.camera.setNextLevel(GamePanel.camera.level.getLevelName());
        }

    }

    void death(int[] spawnPoint) throws Exception {
        velocityY = 0;
        velocityX = 0;

        if (!isDying){
            isDying = true;
            setAnimation(death, deathAnimationSpeed, 1, deathOffsetX, deathOffsetY);
            setNextAnimation(idle, idleAnimationSpeed, idleOffsetX, idleOffsetY);
        }
        else{
            if (!getAnimation().equals(death)){
                GamePanel.camera.move(spawnPoint[0] - GamePanel.camera.screenWidth /2, spawnPoint[1] - GamePanel.camera.screenHeight /2);

                // reset player position
                setX(spawnPoint[0]);
                setY(spawnPoint[1]);

                // reset moving objects position
                for (GameObject2D go : GamePanel.camera.level.permanentUpdatable){
                    if (go.type.contains("MovingPlatform_")){
                        go.getThisMovingPlatform().resetPosition();
                    }
                }

                // reset timer if no checkpoints were taken
                if (!hasTakenCheckpoint){
                    GamePanel.inGameTimer = 0;
                }

                isDying = false;
            }
        }
    }

    public void setSpawnPointPos(int posX, int posY){
        spawnPointPos = new int[] {posX, posY};
    }

    @Override
    public void update() throws Exception {
        animate();
        movementHandler();
        move();

        //System.out.println(this.getDebugInfos());
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

    public double getGroundVelocityX() {
        if (ground == null){
            return 0;
        }
        return ground.getVelocityX();
    }

    public double getGroundVelocityY() {
        if (ground == null){
            return 0;
        }
        return ground.getVelocityY();
    }

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
            velocityY = Math.max(-maxYSpeed, velocityY - (gravity * GamePanel.deltaTime * 6));
        }
    }

    boolean collision(GameObject2D go) throws Exception {

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
                //If player is just on top of the ground
                doGroundVelocityXCount = (Math.abs(velocityX) < earlySpeed * 1.1);
                ground = go;
                velocityY = 0;
                friction = go.getFriction();
                return;
            }

            isOnGround = getY() + getHeight() >= go.getY() - 2 && getPreviousY() + getHeight() <= go.getPreviousY();
            if (isOnGround){
                //If player is in/on the ground
                doGroundVelocityXCount = (Math.abs(velocityX) < earlySpeed * 1.1);
                ground = go;
                velocityY = 0;
                friction = go.getFriction();
            }
        }
    }

    @Override
    public void move() throws Exception {
        GamePanel.camera.deleteGOInGrid(this, false);
        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round((velocityX + getGroundVelocityX()) * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round((velocityY + getGroundVelocityY()) * GamePanel.deltaTime)));
        GamePanel.camera.addGOInGrid(this, false);

        wasOnGround = isOnGround;
        isOnGround = false;
        for (GameObject2D go: getNear()){
            checkGround(go);
            collision(go);
        }
        if (!isOnGround){
            velocityX += getGroundVelocityX();
            velocityY += getGroundVelocityY();
            ground = null;
            friction = airFriction;
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
