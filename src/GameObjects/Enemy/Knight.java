package GameObjects.Enemy;

import GameObjects.*;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

public class Knight extends Entity {

    ArrayList<BufferedImage> idle;
    final int idleAnimSpeed = 3;
    ArrayList<BufferedImage> run;
    final int defaultWalkAnimSpeed = 2;
    final int defaultRunAnimSpeed = 1;
    int runAnimSpeed = defaultWalkAnimSpeed;
    ArrayList<BufferedImage> attack;
    final int attackAnimSpeed = 1;
    ArrayList<BufferedImage> damageAnim;
    final double damageAnimSpeed = 1;
    ArrayList<BufferedImage> dying;
    final double dyingAnimSpeed = 1;
    ArrayList<BufferedImage> dead;
    final  int deadAnimSpeed = 100;

    final int offsetX = 0, offsetY = 5;

    int initialPosX, initialPosY, initDirection = 0;

    final double gravity = 2.5;
    final int maxYSpeed = 200;
    final double runAcceleration = 3;
    final double walkAcceleration = 1;
    double acceleration = walkAcceleration;
    final int runSpeed = 35;
    final int walkSpeed = 15;
    int maxSpeed = walkSpeed;
    final  int maxHealth = 75;
    final int knockBackForce = 20;
    boolean isChasing = false;
    boolean isAttacking = false;
    final int atkWidth = 125;
    final int atkHeight = 7;
    boolean hasAttacked;
    final double maxAtkCooldown = 0.3;
    double atkCooldown = 0;

    int direction = 1;

    final int detectionRangeX = 400;

    final int turnTime = 3; //3s
    double turnTimer = 0;

    boolean isVulnerable = true;
    boolean isDead = false;

    ParticleGenerator exclamationMark;

    public Knight(int x, int y, String id, String subLvl) throws IOException {
        super(x, y, 35, 105, subLvl);
        type = "Knight";
        name = type+id;
        isEnemy = true;
        doesDamage = true;

        hasHP = true;
        hp = maxHealth;

        initialPosX = x;
        initialPosY = y;

        idle = getAnimationList("Enemy/Knight", "idle", 2);
        run = getAnimationList("Enemy/Knight", "run", 7);
        damageAnim = getAnimationList("Enemy/Knight", "damage", 2);
        dying = getAnimationList("Enemy/Knight", "dying", 8);
        dead = getAnimationList("Enemy/Knight", "dead", 0);
        attack = getAnimationList("Enemy/Knight", "attack", 6);

        sprite = new Sprite(idle.get(0), 3.5);
        sprite.setDirection(-direction);
        setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
    }

    public Knight(Knight k) {
        super(k);

        run = k.run;
        idle = k.idle;
        damageAnim = k.damageAnim;
        dying = k.dying;
        dead = k.dead;
        attack = k.attack;

        runAnimSpeed = k.runAnimSpeed;
        acceleration = k.acceleration;
        maxSpeed = k.maxSpeed;

        isChasing = k.isChasing;
        isAttacking = k.isAttacking;
        hasAttacked = k.hasAttacked;
        atkCooldown = k.atkCooldown;
        direction = k.direction;
        turnTimer = k.turnTimer;
        initialPosX = k.initialPosX;
        initialPosY = k.initialPosY;
        initDirection = k.initDirection;
        isVulnerable = k.isVulnerable;
        isDead = k.isDead;

        exclamationMark = k.exclamationMark;
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
                setNextAnimation(dead, deadAnimSpeed, offsetX, offsetY);
                if (dropsKey) {
                    KeyObject k = new KeyObject(getX()+getWidth()/2-32, getY()+getHeight()/2-32, true, "#-1", "");
                    GamePanel.camera.level.addToMainSubLevel(k);
                }
            }
            return;
        }

        if (!isVulnerable){
            if (getAnimation().equals(idle) || getAnimation().equals(run)) isVulnerable = true;
            else return;
        }

        if (isChasing){
            //following the player
            if (!isAttacking){
                setDirection((int) Math.signum(getX() + (float) getWidth() /2 - getPlayer().getX() - (float) getPlayer().getWidth() /2));
            }

            if (maxSpeed != runSpeed){
                maxSpeed = runSpeed;
                acceleration = runAcceleration;
                runAnimSpeed = defaultRunAnimSpeed;
            }
        }

        else{
            //patrolling
            if (maxSpeed != walkSpeed){
                maxSpeed = walkSpeed;
                acceleration = walkAcceleration;
                runAnimSpeed = defaultWalkAnimSpeed;
            }

            turnTimer += GamePanel.deltaTime/10;
            if (turnTimer >= turnTime){
                turnTimer = 0;

                setDirection(-direction);
            }

            //spotting the player
            int posXMin = Math.min(getX()+getWidth()/2, getX()+getWidth()/2-direction*detectionRangeX);
            int posXMax = Math.max(getX()+getWidth()/2, getX()+getWidth()/2-direction*detectionRangeX);
            int posY = getY() + getHeight()/2;

            int playerPosX = GameObject2D.getPlayer().getX() + GameObject2D.getPlayer().getWidth()/2;
            int playerPosYMin = GameObject2D.getPlayer().getY();
            int playerPosYMax = GameObject2D.getPlayer().getY() + GameObject2D.getPlayer().getHeight();

            if(posXMin < playerPosX && playerPosX < posXMax &&
                    playerPosYMin < posY && posY < playerPosYMax){
                setAnimation(run, runAnimSpeed, offsetX, offsetY);
                isChasing = true;

                //particles
                exclamationMark = new ParticleGenerator(getX() + getWidth()/2 - 10, getY() - 50, 1, 0, 1,
                        25, 25, 65, 65,
                        0, 0, 0, 0,
                        0, 0, 0.25,0.65,0,
                        100, "exclamation_mark", 0, "#-1", "main");
                exclamationMark.move(getX() + getWidth()/2 - 5, getY() - 50);
            }
        }

        //launch attack
        if (!isAttacking){
            if (atkCooldown <= 0){
                final int posX = getX() - atkWidth *(getDirection() + 1)/2 - getWidth()*(getDirection() - 1)/2 ;
                final int posY = getY() + getHeight()/2 - 5;

                for (GameObject2D go : getInBox(posX, posY, atkWidth, atkHeight)){
                    if (go.type.equals("Player") || (go.type.equals("IceBlock") && go.hasPhysicalCollisions)){
                        setAnimation(attack, attackAnimSpeed);
                        setNextAnimation(idle, idleAnimSpeed);
                        isAttacking = true;
                        atkCooldown = maxAtkCooldown;
                        break;
                    }
                }
            }
            else atkCooldown -= GamePanel.deltaTime/10;
        }
        else{
            stop();
            if (!getAnimation().equals(attack)){

                isAttacking = false;
                hasAttacked = false;
            }
        }

        //effectue the damages
        if (isAttacking && !hasAttacked && getAnimationIndex() == 4){
            final int posX = getX() - atkWidth*(getDirection() + 1)/2 - getWidth()*(getDirection() - 1)/2 ;
            final int posY = getY() + getHeight()/2 - 5;
            GamePanel.camera.addGOInGrid(
                    new DamageArea(posX, posY, atkWidth, atkHeight,
                            0.1, 25, true, subLevelName)
            );
            GamePanel.camera.bufferUpdateGrid = true;
            hasAttacked = true;
        }
    }

    @Override
    public void move() throws Exception {
        //x movement
        if (isVulnerable){
            if (isSafeGround(-maxSpeed*direction) && !isWall(-maxSpeed*direction, false)) {
                velocityX = Math.min(maxSpeed, Math.max(-maxSpeed, velocityX-acceleration*direction));

                if ((!getAnimation().equals(run) || animationSpeed != runAnimSpeed) && !isAttacking)
                    setAnimation(run, runAnimSpeed, offsetX, offsetY);
            }
            else {
                velocityX = 0;

                if (!getAnimation().equals(idle) && !isAttacking) setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
            }
        }

        //y movement
        velocityY = Math.max(-maxYSpeed, velocityY - (gravity * GamePanel.deltaTime * 6));

        GamePanel.camera.deleteGOInGrid(this, false);
        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round(velocityX * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round(velocityY * GamePanel.deltaTime)));

        for (GameObject2D go: getNear()){
            if (go.isEntity && go.getThisEntity().isEnemy && !go.getType().equals(type)) continue;

            int didCollide = didCollide(go);

            if (didCollide != 0 && go.isEntity && !go.getThisEntity().isEnemy) go.getThisEntity().damage(100);
        }
        GamePanel.camera.addGOInGrid(this, false);

        if (GamePanel.camera.isInVisibleRange(this) && !GamePanel.camera.getVisible().contains(this)){
            GamePanel.camera.getVisible().add(this);
        }
    }

    @Override
    public void collision(Entity e) throws Exception {
        super.collision(e);

        if (!isDead && !e.isEnemy) e.damage(100);
    }

    @Override
    public void damage(int damage) throws Exception {
        if (isVulnerable && hasHP){
            hp -= damage;

            isVulnerable = false;
            velocityX += knockBackForce*direction;
            setAnimation(damageAnim, damageAnimSpeed, offsetX, offsetY);
            if (hp <= 0) setNextAnimation(dying, dyingAnimSpeed, offsetX, offsetY);
            else if (isChasing) setNextAnimation(run, runAnimSpeed, offsetX, offsetY);
            else setNextAnimation(idle, idleAnimSpeed, offsetX, offsetY);
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
        runAnimSpeed = defaultWalkAnimSpeed;
        maxSpeed = walkSpeed;
        acceleration = walkAcceleration;

        atkCooldown = 0;
        isAttacking = false;
        hasAttacked = false;

        turnTimer = 0;
        setDirection(initDirection);
        setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
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
        return new Knight(this);
    }
}

