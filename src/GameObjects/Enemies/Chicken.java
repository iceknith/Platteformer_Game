package GameObjects.Enemies;

import GameObjects.Entity;
import GameObjects.GameObject2D;
import GameObjects.Sprite;
import main.GamePanel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Chicken extends Entity {

    ArrayList<BufferedImage> idle;
    ArrayList<BufferedImage> run;
    ArrayList<BufferedImage> damageAnim;
    ArrayList<BufferedImage> dying;
    ArrayList<BufferedImage> dead;
    ArrayList<BufferedImage> fly;
    ArrayList<BufferedImage> glide;
    final int animSpeed = 1;

    final int defaultOffsetX = 0, defaultOffsetY = 10;
    final int deadOffsetX = 0, deadOffsetY = 5;

    int initialPosX, initialPosY;

    final double gravity = 2.25;
    final double flyGravity = 0.75;
    final int maxYSpeed = 150;
    final int maxYFlySpeed = 75;
    final double acceleration = 3;
    final int runSpeed = 50;
    final  int maxHealth = 50;
    boolean isChasing = false;
    boolean isFlying = false;
    boolean isJumping;
    boolean isOnGround;

    final double jumpForce = 1;
    final double jumpTimer = 0.1;
    double jumpingTime;

    int direction = 1;

    final int detectionRangeX = 750;

    final int turnTime = 3; //3s
    final double chaseTurnTime = 1; //0.1 s
    double turnTimer = 0;
    boolean isVulnerable = true;
    boolean isDead = false;

    public Chicken(int x, int y, String id, String subLvl) throws IOException {
        super(x, y, 45, 38, subLvl);
        type = "Chicken";
        name = type+id;
        isEnemy = true;
        doesDamage = true;

        hasHP = true;
        hp = maxHealth;

        initialPosX = x;
        initialPosY = y;

        idle = getAnimationList("Enemy/Chicken", "idle", 4);
        run = getAnimationList("Enemy/Chicken", "walking", 3);
        damageAnim = getAnimationList("Enemy/Chicken", "damage", 1);
        dying = getAnimationList("Enemy/Chicken", "dying", 3);
        dead = getAnimationList("Enemy/Chicken", "dead", 0);
        fly = getAnimationList("Enemy/Chicken", "fly", 4);
        glide = getAnimationList("Enemy/Chicken", "glide", 0);

        sprite = new Sprite(idle.get(0), 3);
        sprite.setDirection(-direction);
        setAnimation(idle, animSpeed, defaultOffsetX, defaultOffsetY);
    }

    public Chicken(Chicken c) {
        super(c);

        run = c.run;
        idle = c.idle;
        damageAnim = c.damageAnim;
        dying = c.dying;
        dead = c.dead;
        fly = c.fly;
        glide = c.glide;

        direction = c.direction;
        turnTimer = c.turnTimer;
        initialPosX = c.initialPosX;
        initialPosY = c.initialPosY;
        isVulnerable = c.isVulnerable;
        isDead = c.isDead;
        isFlying = c.isFlying;
        isChasing = c.isChasing;
        isOnGround = c.isOnGround;
        isJumping = c.isJumping;
        jumpingTime = c.jumpingTime;
    }

    @Override
    public void update() throws Exception {
        super.update();
        animate();

        if (isDead) return;

        iaLogic();
        move();
    }

    public void iaLogic(){

        if (hp <= 0){
            if (getAnimation().equals(dying)){
                hasPhysicalCollisions = false;
                hasHP = false;
                isDead = true;
                setNextAnimation(dead, animSpeed, deadOffsetX, deadOffsetY);
            }
            return;
        }
        if (!isVulnerable){
            if (getAnimation().equals(idle) || getAnimation().equals(glide)) isVulnerable = true;
            else return;
        }

        jumpHandler();
        flyHandler();

        if (isChasing) { //chasing
            if (getPlayer().getY() + getPlayer().getHeight() < getY()) isJumping = true;

            //"pathfinding" to the player
            int newDirection = (int) Math.signum(getX() + (float) getWidth() /2 - getPlayer().getX() - (float) getPlayer().getWidth() /2);

            if (newDirection != direction){
                turnTimer += GamePanel.deltaTime;
            }
            else turnTimer = 0;

            if (turnTimer > chaseTurnTime) {
                direction = newDirection;
                sprite.setDirection(-direction);
            }

            velocityX = Math.min(runSpeed, Math.max(-runSpeed, velocityX-acceleration*direction));

            //stopping & jumping if wall
            if (isWall(-runSpeed*direction, true)){
                velocityX = 0;
                isJumping = true;
            }

            //flying up if danger under
            if (velocityY <= 0){
                for (GameObject2D go: getInBox(getX(), getY() + getHeight() - (int) velocityY, getWidth(), 1)){
                    if (go.doesDamage || go.getType().equals("IceBlock")) {
                        isJumping = true;
                        break;
                    }
                }
            }

            //stop flying if spike over
            if (velocityY > 0){
                for (GameObject2D go: getInBox(getX(), getY() - (int) velocityY, getWidth(), 1)){
                    if (go.doesDamage || go.getType().equals("IceBlock")) {
                        isJumping = false;
                        velocityY = 0;
                        break;
                    }
                }
            }
        }
        else { //patrolling
            if (velocityX != 0) stop();

            //turning
            turnTimer += GamePanel.deltaTime/10;
            if (turnTimer >= turnTime){
                    turnTimer = 0;
                    direction = -direction;
                    sprite.setDirection(-direction);
                }

            //spotting the player
            int posXMin = Math.min(getX()+getWidth()/2, getX()+getWidth()/2-direction*detectionRangeX);
            int posXMax = Math.max(getX()+getWidth()/2, getX()+getWidth()/2-direction*detectionRangeX);
            int posY = getY();

            int playerPosX = GameObject2D.getPlayer().getX() + GameObject2D.getPlayer().getWidth()/2;
            int playerPosYMin = GameObject2D.getPlayer().getY();
            int playerPosYMax = GameObject2D.getPlayer().getY() + GameObject2D.getPlayer().getHeight();


            if(posXMin < playerPosX && playerPosX < posXMax &&
                    playerPosYMin < posY && posY < playerPosYMax){
                setAnimation(run, animSpeed, defaultOffsetX, defaultOffsetY);
                isChasing = true;
            }
        }
    }

    void flyHandler(){
        if (isJumping) return;
        if (!isFlying){
            velocityY = Math.max(-maxYSpeed, velocityY - (gravity * GamePanel.deltaTime * 6));
            if (velocityY < -10) {
                isFlying = true;
                setAnimation(glide, animSpeed, defaultOffsetX, defaultOffsetY);
            }
        }
        else{
            velocityY = Math.max(-maxYFlySpeed, velocityY - (flyGravity * GamePanel.deltaTime * 6));
            if (isOnGround){
                isFlying = false;
                setAnimation(idle, animSpeed, defaultOffsetX, defaultOffsetY);
            }
        }
    }

    void jumpHandler(){
        if (isJumping){
            jumpingTime += GamePanel.deltaTime/10;
            if (jumpingTime > jumpTimer) {
                isJumping = false;
                isFlying = true;
                jumpingTime = 0;
                setNextAnimation(glide, animSpeed, defaultOffsetX, defaultOffsetY);
            }
            else {
                if (!getAnimation().equals(fly)) setAnimation(fly, animSpeed, defaultOffsetX, defaultOffsetY);
                velocityY = jumpForce*20 - jumpingTime;
            }
        }
    }

    @Override
    public void move() throws Exception {

        GamePanel.camera.deleteGOInGrid(this, false);
        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round(velocityX * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round(velocityY * GamePanel.deltaTime)));

        isOnGround = false;
        for (GameObject2D go: getNear()){
            int didCollide = didCollide(go);

            //if (didCollide != 0 && go.isEntity && !go.getThisEntity().isEnemy) go.getThisEntity().damage(1);
            if (didCollide == 1 && go.hasPhysicalCollisions) isOnGround = true;
        }
        GamePanel.camera.addGOInGrid(this, false);

    }

    void stop(){
        if (velocityX > 1) velocityX /= 2;
        else velocityX = 0;
    }

    @Override
    public void collision(Entity e) throws Exception {
        super.collision(e);

        if (!isDead && !e.isEnemy) e.damage(25);
    }

    @Override
    public void damage(int damage) {
        if (isVulnerable && hasHP){
            hp -= damage;

            isVulnerable = false;
            setAnimation(damageAnim, animSpeed, defaultOffsetX, defaultOffsetY);
            if (hp <= 0) setNextAnimation(dying, animSpeed, deadOffsetX, deadOffsetY);
            else if (isOnGround) setNextAnimation(idle, animSpeed, defaultOffsetX, defaultOffsetY);
            else setNextAnimation(glide, animSpeed, defaultOffsetX, deadOffsetY);
        }
    }

    @Override
    public void reset() throws Exception {
        super.reset();

        isDead = false;
        hasPhysicalCollisions = true;
        hasHP = true;
        hp = maxHealth;

        isChasing = false;
        isFlying = false;
        isJumping = false;
        isOnGround = false;
        jumpingTime = 0;
        turnTimer = 0;
        direction = 1;
        sprite.setDirection(-direction);
        setAnimation(idle, animSpeed, defaultOffsetX, defaultOffsetY);
        setNextAnimation(null, 0);

        GamePanel.camera.deleteGOInGrid(this, true);
        setX(initialPosX);
        setY(initialPosY);
        prevX = getX();
        prevY = getY();
        velocityX = 0;
        velocityY = 0;
        GamePanel.camera.addGOInGrid(this, true);
    }

    @Override
    public GameObject2D copy() throws IOException {
        return new Chicken(this);
    }
}
