package GameObjects.Enemy;

import GameObjects.*;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
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

    int initialPosX, initialPosY, initDirection;

    final double gravity = 2.25;
    final double flyGravity = 0.75;
    final int maxYSpeed = 150;
    final int maxYFlySpeed = 75;
    final double acceleration = 3;
    final int runSpeed = 40;
    final  int maxHealth = 15;
    boolean isChasing = false;
    boolean isFlying = false;
    boolean isJumping;
    boolean isOnGround;

    final double jumpForce = 0.5;
    final double jumpTimer = 0.1;
    double jumpingTime;

    int direction = 1;

    final int detectionRangeX = 750;

    final int turnTime = 3; //3s
    final double chaseTurnTime = 1; //0.1 s
    double turnTimer = 0;
    boolean isVulnerable = true;
    boolean isDead = false;

    ParticleGenerator exclamationMark;

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
        initDirection = c.initDirection;
        isVulnerable = c.isVulnerable;
        isDead = c.isDead;
        isFlying = c.isFlying;
        isChasing = c.isChasing;
        isOnGround = c.isOnGround;
        isJumping = c.isJumping;
        jumpingTime = c.jumpingTime;

        exclamationMark = c.exclamationMark;
    }

    public void setDirection(int newDirection){
        direction = newDirection;
        sprite.setDirection(-newDirection);

        if (initDirection == 0) initDirection = direction;
    }

    public int getDirection(){
        return direction;
    }


    @Override
    public void update() throws Exception {
        super.update();
        if (exclamationMark != null) exclamationMark.update();

        animate();

        if (isDead) {
            ragdolPhysics(gravity);
            return;
        }

        iaLogic();
        move();
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {
        super.draw(g2D, IO);
        if (exclamationMark != null) exclamationMark.draw(g2D, IO);
    }

    public void iaLogic() throws Exception {

        if (hp <= 0){
            if (getAnimation().equals(dying)){
                hasPhysicalCollisions = false;
                hasHP = false;
                isDead = true;
                setNextAnimation(dead, animSpeed, deadOffsetX, deadOffsetY);
                if (dropsKey) {
                    KeyObject k = new KeyObject(getX()+getWidth()/2-32, getY()+getHeight()/2-32, true, "#-1", "");
                    GamePanel.camera.level.addToMainSubLevel(k);
                }
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
                setDirection(newDirection);
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

                //particles
                exclamationMark = new ParticleGenerator(getX() + getWidth()/2 - 10, getY() - 50, 1, 0, 1,
                        25, 25, 65, 65,
                        0, 0, 0, 0,
                        0, 0, 0.5,1.3,0,
                        100, "exclamation_mark", 0, "#-1", "main");
                exclamationMark.move(getX() + getWidth()/2 - 5, getY() - 50);
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
            if (go.isEntity && go.getThisEntity().isEnemy && !go.getType().equals(type)) continue;

            int didCollide = didCollide(go);

            if (didCollide != 0 && go.isEntity && !go.getThisEntity().isEnemy) go.getThisEntity().damage(1);
            if (didCollide == 1 && go.hasPhysicalCollisions) isOnGround = true;
        }
        GamePanel.camera.addGOInGrid(this, false);

        if (GamePanel.camera.isInVisibleRange(this) && !GamePanel.camera.getVisible().contains(this)){
            GamePanel.camera.getVisible().add(this);
        }
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
        setDirection(initDirection);
        setAnimation(idle, animSpeed, defaultOffsetX, defaultOffsetY);
        setNextAnimation(null, 0);
        exclamationMark = null;

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
