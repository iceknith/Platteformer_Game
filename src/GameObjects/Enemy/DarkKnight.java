package GameObjects.Enemy;

import GameObjects.*;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

public class DarkKnight extends Entity {

    ArrayList<BufferedImage> idle;
    final int idleAnimSpeed = 3;
    ArrayList<BufferedImage> run;
    final int defaultWalkAnimSpeed = 2;
    final int defaultRunAnimSpeed = 1;
    int runAnimSpeed = defaultWalkAnimSpeed;
    ArrayList<BufferedImage> attack;
    final int attackAnimSpeed = 1;
    ArrayList<BufferedImage> jump;
    ArrayList<BufferedImage> fallSlow;
    ArrayList<BufferedImage> fallNormal;
    ArrayList<BufferedImage> fallFast;
    ArrayList<BufferedImage> land;
    final int landAnimSpeed = 2;
    ArrayList<BufferedImage> damageAnim;
    final double damageAnimSpeed = 1;
    ArrayList<BufferedImage> dying;
    final double dyingAnimSpeed = 1;
    ArrayList<BufferedImage> dead;
    final  int imageAnimSpeed = 100;


    final int offsetX = -8, offsetY = 32;

    int initialPosX, initialPosY, initDirection = 0;

    final double gravity = 2.5;
    final int maxYSpeed = 200;
    final int jumpForce = 100;
    final double runAcceleration = 3;
    final double walkAcceleration = 1;
    double acceleration = walkAcceleration;
    final int runSpeed = 35;
    final int walkSpeed = 15;
    int maxSpeed = walkSpeed;
    final  int maxHealth = 100;
    final int knockBackForce = 20;
    boolean isChasing = false;
    boolean isAttacking = false;
    int lastAttack = 0;
    final double maxJumpCooldown = 0.5;
    double jumpCooldown = 0;

    int direction = 1;
    int lastDamage = 0;

    final int detectionRangeX = 400;

    final int turnTime = 3; //3s
    double turnTimer = 0;

    boolean isVulnerable = true;
    boolean isDead = false;
    boolean isJumping = false;

    ParticleGenerator exclamationMark;
    ParticleGenerator groundImpact;

    public DarkKnight(int x, int y, String id, String subLvl) throws IOException {
        super(x, y, 32, 96, subLvl);
        type = "DarkKnight";
        name = type+id;
        isEnemy = true;
        doesDamage = true;

        hasHP = true;
        hp = maxHealth;

        initialPosX = x;
        initialPosY = y;

        idle = getAnimationList("Enemy/DarkKnight", "idle", 8);
        run = getAnimationList("Enemy/DarkKnight", "run", 10);
        damageAnim = getAnimationList("Enemy/DarkKnight", "damage", 0);
        dying = getAnimationList("Enemy/DarkKnight", "dying", 12);
        dead = getAnimationList("Enemy/DarkKnight", "dead", 0);
        attack = getAnimationList("Enemy/DarkKnight", "attack", 3);
        jump = getAnimationList("Enemy/DarkKnight", "jump", 0);
        fallSlow = getAnimationList("Enemy/DarkKnight", "fall slow", 0);
        fallNormal = getAnimationList("Enemy/DarkKnight", "fall normal", 0);
        fallFast = getAnimationList("Enemy/DarkKnight", "fall fast", 0);
        land = getAnimationList("Enemy/DarkKnight", "land", 0);

        sprite = new Sprite(idle.get(0), 4);
        sprite.setDirection(-direction);
        setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
    }

    public DarkKnight(DarkKnight k) {
        super(k);

        run = k.run;
        idle = k.idle;
        damageAnim = k.damageAnim;
        dying = k.dying;
        dead = k.dead;
        attack = k.attack;
        fallSlow = k.fallSlow;
        fallNormal = k.fallNormal;
        fallFast = k.fallFast;
        jump = k.jump;
        land = k.land;
        groundImpact = k.groundImpact;

        runAnimSpeed = k.runAnimSpeed;
        acceleration = k.acceleration;
        maxSpeed = k.maxSpeed;

        isChasing = k.isChasing;
        isAttacking = k.isAttacking;
        isJumping = k.isJumping;
        lastAttack = k.lastAttack;
        jumpCooldown = k.jumpCooldown;
        lastDamage = k.lastDamage;
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
        if (groundImpact != null) groundImpact.update();

        animate();

        if (isDead) {
            ragdolPhysics(gravity);
            return;
        }

        move();
        iaLogic();
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {
        super.draw(g2D, IO);
        if (exclamationMark != null) exclamationMark.draw(g2D, IO);
        if (groundImpact != null) groundImpact.draw(g2D, IO);
    }

    public void iaLogic() throws Exception {

        if (hp <= 0){
            if (getAnimation().equals(dying)){
                hasPhysicalCollisions = false;
                hasHP = false;
                isDead = true;
                setNextAnimation(dead, imageAnimSpeed, offsetX, offsetY);
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
            setDirection((int) Math.signum(getX() + (float) getWidth() /2 - getPlayer().getX() - (float) getPlayer().getWidth() /2));

            if (maxSpeed != runSpeed){
                maxSpeed = runSpeed;
                acceleration = runAcceleration;
                runAnimSpeed = defaultRunAnimSpeed;
            }

            //jumping
            final int distX = Math.abs(getX() + getWidth()/2 - getPlayer().getX() - getPlayer().getWidth()/2);
            final int distY = getPlayer().getY() - getY() - getHeight();
            if (!isJumping && jumpCooldown <= 0){
                if ((distY <= getPlayer().getHeight() && distX <= detectionRangeX) || velocityX == 0) {
                    isJumping = true;
                    velocityY += jumpForce;
                    setAnimation(run, runAnimSpeed);
                }
            }
            else if (!isJumping) jumpCooldown -= GamePanel.deltaTime/10;

            //launch Attack
            if (isJumping && !isAttacking && distX <= 100 && distY <= 150 && velocityY < 0){
                isAttacking = true;
                setAnimation(attack, attackAnimSpeed);
                setNextAnimation(run, runAnimSpeed);
            }

            //effectue the damages
            if (isAttacking) {
                if (getAnimationIndex() == 1 && lastAttack != 1){
                    final int atkX = 40, atkY = 90, atkW = 120, atkH = 88;

                    final int posX = getSprite().getOffsetX(hitbox) - getDirection() * atkX - atkW * (getDirection() + 1)/2;
                    final int posY = getSprite().getOffsetY(hitbox) + atkY;
                    DamageArea d =  new DamageArea(posX, posY, atkW, atkH, 0.2, 25, true, subLevelName);
                    GamePanel.camera.addGOInGrid(d);
                    d.velocityX = velocityX;
                    d.velocityY = velocityY;
                    GamePanel.camera.bufferUpdateGrid = true;
                    lastAttack = 1;
                }
            }

            //reset attack
            if (isAttacking && !getAnimation().equals(attack)){
                isAttacking = false;
                lastAttack = 0;
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
            }
        }
    }

    @Override
    public void move() throws Exception {
        //x movement
        if (getAnimation().equals(land)) stop();

        if (isVulnerable && !isJumping && !getAnimation().equals(land)){
            if (isSafeGround(-2*maxSpeed*direction) && !isWall(-2*maxSpeed*direction, true)) {
                velocityX = Math.min(maxSpeed, Math.max(-maxSpeed, velocityX-acceleration*direction));

                if ((!getAnimation().equals(run) || animationSpeed != runAnimSpeed) && !isAttacking)
                    setAnimation(run, runAnimSpeed, offsetX, offsetY);
            }
            else {
                velocityX = 0;

                if (!getAnimation().equals(idle) && !isAttacking) setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
            }
        }
        else if (isJumping) velocityX = Math.min(maxSpeed, Math.max(-maxSpeed, velocityX-acceleration*direction/2));

        //y movement
        velocityY = Math.max(-maxYSpeed, velocityY - (gravity * GamePanel.deltaTime * 6));

        if (!isAttacking && isVulnerable){
            if (velocityY < -40)
                setAnimation(fallFast, imageAnimSpeed);
            else if (velocityY < -20){
                setAnimation(fallNormal, imageAnimSpeed);
            }
            else if (velocityY < -10)
                setAnimation(fallSlow, imageAnimSpeed);
        }

        GamePanel.camera.deleteGOInGrid(this, false);
        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round(velocityX * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round(velocityY * GamePanel.deltaTime)));

        for (GameObject2D go: getNear()){
            if (go.isEntity && go.getThisEntity().isEnemy && !go.getType().equals(type)) continue;

            int didCollide = didCollide(go);

            if (didCollide != 0 && didCollide != 1 && go.isEntity && !go.getThisEntity().isEnemy) {
                go.getThisEntity().damage(100);
            }
            if (didCollide == 1) {
                if (prevY - getY() < -20*GamePanel.deltaTime && go.hasPhysicalCollisions){
                    isJumping = false;
                    isAttacking = false;
                    lastAttack = 0;
                    jumpCooldown = maxJumpCooldown;
                    setAnimation(land, landAnimSpeed);
                    setNextAnimation(run, runAnimSpeed);

                    final int atkX = -21, atkY = 148, atkW = 180, atkH = 16;

                    final int posX = getSprite().getOffsetX(hitbox) - getDirection() * atkX - atkW * (getDirection() + 1)/2;
                    final int posY = getSprite().getOffsetY(hitbox) + atkY;
                    DamageArea d =  new DamageArea(posX, posY, atkW, atkH, 0.2, 25, true, subLevelName);
                    GamePanel.camera.addGOInGrid(d);
                    GamePanel.camera.bufferUpdateGrid = true;

                    groundImpact = new ParticleGenerator(getX() + getWidth()/2 - 112, getY() + getHeight() - 60, 1, 0, 0.5,
                            225, 225, 69, 69,
                            0, 0, 0, 0,
                            0, 0, 0,0,0,
                            0.1, "ground_impact", 5, "#-1", "main");
                }
                else if (go.doesDamage && go.hasPhysicalCollisions && !(go.isEntity && go.getThisEntity().isEnemy)){
                    hp = Math.min(maxHealth, lastDamage + hp);
                    isJumping = true;
                    velocityY += jumpForce;
                    setAnimation(run, runAnimSpeed);
                }
            }
        }
        GamePanel.camera.addGOInGrid(this, false);

        if (GamePanel.camera.isInVisibleRange(this) && !GamePanel.camera.getVisible().contains(this)){
            GamePanel.camera.getVisible().add(this);
        }
    }

    @Override
    public void collision(Entity e) throws Exception {
        super.collision(e);

        final boolean verticalCollision = getY() + getHeight() >= e.getY() && getPreviousY() + getHeight() <= e.getPreviousY();
        if (!isDead && !e.isEnemy && (!verticalCollision || e.getType().equals("Player"))) {
            e.damage(100);
        }
    }

    @Override
    public void damage(int damage) throws Exception {
        if (isVulnerable && hasHP){
            hp -= damage;
            lastDamage = damage;

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
        isAttacking = false;
        isJumping = false;
        lastAttack = 0;
        lastDamage = 0;
        jumpCooldown = 0;
        runAnimSpeed = defaultWalkAnimSpeed;
        maxSpeed = walkSpeed;
        acceleration = walkAcceleration;


        turnTimer = 0;
        setDirection(initDirection);
        setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
        setNextAnimation(null, 0);
        exclamationMark = null;
        groundImpact = null;

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
        return new DarkKnight(this);
    }
}

