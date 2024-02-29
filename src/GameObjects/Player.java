package GameObjects;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

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

    boolean isDying;

    boolean wasJumpPressed;

    public int[] spawnPointPos;
    public boolean hasTakenCheckpoint = false;

    final int jumpBubblesDistance = 90;
    final int jumpBubblesRadius = 15;
    final Color jumpBublesOutlineColor = new Color(.07f, .09f, .11f, .85f);
    final Color jumpBublesColor = new Color(0.64f, .88f, 1f, .6f);

    public int snowflakeCount = 0;
    final int snowflakesDistance = jumpBubblesDistance + 25;
    final int snowflakesSpacing = 20;
    final int snowflakeSize = 15;
    BufferedImage snowflakeImage;
    public final int iceBlockPlacingDistanceX = 96;
    public final int iceBlockPlacingDistanceY = 96;
    int iceBlockDirectionX;
    int iceBlockDirectionY;
    int nextIceBlockDirectionX;
    int nextIceBlockDirectionY;
    double iceBlockPosXTimer;
    double iceBlockPosYTimer;

    public boolean isPlacingIceBlock;
    public IceBlock currentIceBlock;
    int iceBlockID = 0;

    public ArrayList<KeyObject> keys = new ArrayList<>();

    public Player(int posX, int posY, String id, String subLvlName) throws IOException {
        super(posX,posY,39,96, subLvlName);

        hasHP = true;
        hp = 1;

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
        airEarlySpeed = 45;
        airMaxSpeed = 60;
        airEarlyAcceleration = 3.2;
        airLateAcceleration = 0.002;
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

        snowflakeImage = readImageBuffered("assets/Player/snowflake_overlay/0.png");

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
        deathAnimationSpeed = 0.13;

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

        isDying = p.isDying;
        spawnPointPos = p.spawnPointPos;
        hasTakenCheckpoint = p.hasTakenCheckpoint;
        isOnGround = p.isOnGround;
        wasOnGround = p.wasOnGround;
        isJumping = p.isJumping;
        wasJumping = p.wasJumping;
        friction = p.friction;
        ground = p.ground;
        doGroundVelocityXCount = p.doGroundVelocityXCount;

        speedConversionPercent = p.speedConversionPercent;

        deathYLine = p.deathYLine;

        sprite = new Sprite(readImageBuffered("assets/Player/idle/0.png"), 3);

        idle = p.idle;
        idleAnimationSpeed = p.idleAnimationSpeed;
        idleOffsetX = p.idleOffsetX;
        idleOffsetY = p.idleOffsetY;

        run = p.run;
        runAnimationSpeed = p.runAnimationSpeed;
        runOffsetX = p.runOffsetX;
        runOffsetY = p.runOffsetY;

        jump = p.jump;
        jumpAnimationSpeed = p.jumpAnimationSpeed;
        jumpOffsetX = p.jumpOffsetX;
        jumpOffsetY = p.jumpOffsetY;

        fall = p.fall;
        fallAnimationSpeed = p.fallAnimationSpeed;
        fallOffsetX = p.fallOffsetX;
        fallOffsetY = p.fallOffsetY;

        fallFast = p.fallFast;
        fallFastAnimationSpeed = p.fallFastAnimationSpeed;
        fastFallOffsetX = p.fastFallOffsetX;
        fastFallOffsetY = p.fastFallOffsetY;

        land = p.land;
        landAnimationSpeed = p.landAnimationSpeed;
        landOffsetX = p.landOffsetX;
        landOffsetY = p.landOffsetY;

        death = p.death;
        deathAnimationSpeed = p.deathAnimationSpeed;
        deathOffsetX = p.deathOffsetX;
        deathOffsetY = p.deathOffsetY;

        setAnimation(idle, idleAnimationSpeed, idleOffsetX, idleOffsetY);

        wasJumpPressed = p.wasJumpPressed;
        snowflakeCount = p.snowflakeCount;
        snowflakeImage = p.snowflakeImage;
        iceBlockDirectionX = p.iceBlockDirectionX;
        iceBlockDirectionY = p.iceBlockDirectionY;
        nextIceBlockDirectionX = p.nextIceBlockDirectionX;
        nextIceBlockDirectionY = p.nextIceBlockDirectionY;
        iceBlockPosXTimer = p.iceBlockPosXTimer;
        iceBlockPosYTimer = p.iceBlockPosYTimer;
        isPlacingIceBlock = p.isPlacingIceBlock;
        currentIceBlock = p.currentIceBlock;
    }

    void playerHandler() throws Exception {
        double sTh = speedThreshold;
        double eS = earlySpeed;
        double s = maxSpeed;
        double eAcc = earlyAcceleration;
        double lAcc = lateAcceleration;

        if(isOnGround){
            jumps = maxJumps;
        }
        else{
            sTh = airSpeedThreshold;
            eS = airEarlySpeed;
            s = airMaxSpeed;
            eAcc = airEarlyAcceleration;
            lAcc = airLateAcceleration;
        }

        //killing player
        if (getY() > deathYLine || KeyHandler.isSuicideKeyPressed || isDying){
            death(spawnPointPos);
            return;
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
            if (gravity != 0.97*defaultGravity){
                gravity = 0.97*defaultGravity;
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
        }
        else {
            if (velocityY < -10) {
                setAnimation(fall, fallAnimationSpeed, fallOffsetX, fallOffsetY);
            }
        }

        // Reset Level
        if (KeyHandler.isResetKeyPressed){
            GamePanel.camera.setNextLevel(GamePanel.camera.level.getLevelName());
        }

        iceBlockHandler();
    }

    void iceBlockHandler() throws Exception {
        //Place Ice Block
        if (!KeyHandler.isPlacePressed && !KeyHandler.isPlaceDownPressed && isPlacingIceBlock){
            if (currentIceBlock.canBePlaced) snowflakeCount -= 1;
            isPlacingIceBlock = false;
            currentIceBlock.isPlaced = true;
            currentIceBlock = null;
        }

        if ((KeyHandler.isPlacePressed || KeyHandler.isPlaceDownPressed) && snowflakeCount > 0 && !isPlacingIceBlock){
            isPlacingIceBlock = true;

            //creating Ice Block with temporary coordinates
            currentIceBlock = new IceBlock(getX(), getY(), "main", iceBlockID++);
            GamePanel.camera.addGOInGrid(currentIceBlock, true);
            GamePanel.camera.updateGrid();
        }

        //Ice Block logic
        if (isPlacingIceBlock){

            if (KeyHandler.isRightPressed) iceBlockDirectionX = 1;
            else if (KeyHandler.isLeftPressed) iceBlockDirectionX = -1;
            else iceBlockDirectionX = 0;

            if (KeyHandler.isDownPressed || KeyHandler.isPlaceDownPressed) iceBlockDirectionY = 1;
            else if (KeyHandler.isUpPressed) iceBlockDirectionY = -1;
            else iceBlockDirectionY = 0;

            if (iceBlockDirectionX == 0 && iceBlockDirectionY == 0) iceBlockDirectionX = sprite.direction;

            final int newX = getX()
                    + (1+iceBlockDirectionX)*getWidth()/2
                    + (iceBlockDirectionX-1)*currentIceBlock.getWidth()/2
                    + iceBlockDirectionX*iceBlockPlacingDistanceX;

            int newY = getY()
                    + (1+iceBlockDirectionY)*getHeight()/2
                    + (iceBlockDirectionY-1)*currentIceBlock.getHeight()/2
                    + iceBlockDirectionY*iceBlockPlacingDistanceY;

            //special case
            if (iceBlockDirectionX == 0 && iceBlockDirectionY == 1)
                newY = getY()
                    + (1+iceBlockDirectionY)*getHeight()/2
                    + (iceBlockDirectionY-1)*currentIceBlock.getHeight()/2
                        +10 - (int)((velocityY+getGroundVelocityY())*4*GamePanel.deltaTime);

            currentIceBlock.move(newX, newY);
            currentIceBlock.setPlayerCenter(getX()+getWidth()/2, getY()+getHeight()/2);
        }
    }

    public void death(int[] spawnPoint) throws Exception {
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
                GamePanel.camera.deleteGOInGrid(this, true);
                setX(spawnPoint[0]);
                setY(spawnPoint[1]);
                prevX = getX();
                prevY = getY();
                GamePanel.camera.addGOInGrid(this, true);

                // reset all objects
                GamePanel.camera.level.resetAll = true;

                // reset timer if no checkpoints were taken
                if (!hasTakenCheckpoint){
                    GamePanel.inGameTimer = 0;
                }

                isDying = false;

                jumps = 0;
                snowflakeCount = 0;
                isPlacingIceBlock = false;
                currentIceBlock = null;
            }
        }
    }

    public void setSpawnPointPos(int posX, int posY){
        spawnPointPos = new int[] {posX, posY};
    }

    @Override
    public void damage(int damage) throws Exception {
        death(spawnPointPos);
    }

    @Override
    public void update() throws Exception {
        animate();
        playerHandler();
        move();
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO){

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

        //drawing snowflakes
        int posX = getX() + getWidth()/2 - snowflakesSpacing*(snowflakeCount-1)/2  - GamePanel.camera.getScreenX();
        int posY = getY() + getHeight()/2  - GamePanel.camera.getScreenY();

        if (maxJumps == 0) posY -= jumpBubblesDistance;
        else posY -= snowflakesDistance;

        for (int i = 0; i < snowflakeCount; i++){
            g2D.drawImage(snowflakeImage, posX, posY, snowflakeSize, snowflakeSize, IO);
            posX += snowflakesSpacing;
        }

        super.draw(g2D, IO);
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

    void checkGround(GameObject2D go) throws Exception {
        if (wasOnGround && go.hasPhysicalCollisions && (!go.isEntity || !go.getThisEntity().isEnemy)){
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
                go.collision(this);
                velocityY = 0;
                friction = go.getFriction();
            }
        }
    }

    @Override
    protected void move() throws Exception {
        GamePanel.camera.deleteGOInGrid(this, false);
        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round((velocityX + getGroundVelocityX()) * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round((velocityY + getGroundVelocityY()) * GamePanel.deltaTime)));

        wasOnGround = isOnGround;
        isOnGround = false;
        for (GameObject2D go: getNear()){
            checkGround(go);
            int didCollide = didCollide(go);
            if (didCollide == 1 && go.hasPhysicalCollisions) isOnGround = true;
        }
        if (!isOnGround){
            velocityX += getGroundVelocityX();
            velocityY += getGroundVelocityY();
            ground = null;
            friction = airFriction;
        }

        GamePanel.camera.addGOInGrid(this, false);
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
