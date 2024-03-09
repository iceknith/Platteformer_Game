package GameObjects.Enemy;

import GameObjects.*;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

public class Magician extends Entity {

    ArrayList<BufferedImage> idle;
    final int idleAnimSpeed = 3;
    ArrayList<BufferedImage> walk;
    final int walkAnimSpeed = 1;
    ArrayList<BufferedImage> attack;
    final int attackAnimSpeed = 1;
    ArrayList<BufferedImage> damageAnim;
    final double damageAnimSpeed = 1;
    ArrayList<BufferedImage> dying;
    final double dyingAnimSpeed = 1;
    ArrayList<BufferedImage> dead;
    final  int deadAnimSpeed = 100;

    final int offsetX = 3, offsetY = 21;

    int initialPosX, initialPosY, initDirection = 0;

    final double gravity = 2.5;
    final int maxYSpeed = 200;
    final double walkAcceleration = 0.5;
    double acceleration = walkAcceleration;
    final int maxSpeed = 10;
    final  int maxHealth = 50;
    final int knockBackForce = 20;
    boolean isChasing = false;
    boolean isAttacking = false;
    boolean hasAttacked;
    final double maxAtkCooldown = 3, minAtkCooldown = 0.8;
    double atkCooldown = 0;

    int direction = 1;

    final int detectionRangeX = 600;
    final int atkRange = 1000;

    final int turnTime = 3; //3s
    double turnTimer = 0;

    boolean isVulnerable = true;
    boolean isDead = false;

    ParticleGenerator exclamationMark;

    public Magician(int x, int y, String id, String subLvl) throws IOException {
        super(x, y, 36, 120, subLvl);
        type = "Magician";
        name = type+id;
        isEnemy = true;
        doesDamage = true;

        hasHP = true;
        hp = maxHealth;

        initialPosX = x;
        initialPosY = y;

        idle = getAnimationList("Enemy/Magician", "idle", 2);
        walk = getAnimationList("Enemy/Magician", "walk", 3);
        damageAnim = getAnimationList("Enemy/Magician", "damage", 2);
        dying = getAnimationList("Enemy/Magician", "dying", 5);
        dead = getAnimationList("Enemy/Magician", "dead", 0);
        attack = getAnimationList("Enemy/Magician", "attack", 10);

        sprite = new Sprite(idle.get(0), 3);
        sprite.setDirection(-direction);
        setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
    }

    public Magician(Magician m) {
        super(m);

        walk = m.walk;
        idle = m.idle;
        damageAnim = m.damageAnim;
        dying = m.dying;
        dead = m.dead;
        attack = m.attack;

        acceleration = m.acceleration;

        isChasing = m.isChasing;
        isAttacking = m.isAttacking;
        hasAttacked = m.hasAttacked;
        atkCooldown = m.atkCooldown;
        direction = m.direction;
        turnTimer = m.turnTimer;
        initialPosX = m.initialPosX;
        initialPosY = m.initialPosY;
        initDirection = m.initDirection;
        isVulnerable = m.isVulnerable;
        isDead = m.isDead;

        exclamationMark = m.exclamationMark;
    }

    public void setDirection(int newDirection){
        direction = newDirection;
        sprite.setDirection(newDirection);

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
                setNextAnimation(dead, deadAnimSpeed, offsetX, offsetY - 6);
                if (dropsKey) {
                    KeyObject k = new KeyObject(getX()+getWidth()/2-32, getY()+getHeight()/2-32, true, "#-1", "");
                    GamePanel.camera.level.addToMainSubLevel(k);
                }
            }
            return;
        }
        if (!isVulnerable){
            if (getAnimation().equals(idle) || getAnimation().equals(walk)) isVulnerable = true;
            else return;
        }

        //chasing & attacking
        if (isChasing){
            final int distX = Math.abs(getX() + getWidth()/2 - getPlayer().getX() - getPlayer().getWidth()/2);

            if (!isAttacking){
                //launching atk
                if (atkCooldown <= 0){
                    if (distX <= atkRange){
                        isAttacking = true;
                        setAnimation(attack, attackAnimSpeed);
                        setNextAnimation(idle, idleAnimSpeed);
                    }
                }
                else atkCooldown -= GamePanel.deltaTime/10;

                //advancing to the player
                setDirection((int) Math.signum(getX() + (float) getWidth() /2 - getPlayer().getX() - (float) getPlayer().getWidth() /2));

                if (distX <= atkRange){
                    stop();
                }
            }

            //if is attacking
            else {
                stop();

                //launch projectile
                if (getAnimationIndex() == 4 && !hasAttacked) {
                    final int projX = 150, projY = 126, projW = 30, projH = 30;

                    final int posX = getSprite().getOffsetX(hitbox) + getDirection() * projX - projW * (getDirection() + 1)/2;
                    final int posY = getSprite().getOffsetY(hitbox) + projY;
                    GamePanel.camera.addGOInGrid(
                            new Projectile(posX, posY, projW, projH,
                                    3, 4, 1, -getDirection(),
                                    -2 * getDirection(), 0,
                                    75, 0,
                                    5, 50,
                                    true, false,
                                    "Fire_Spell", 1, 2, 0, 1,
                                    subLevelName),
                            true
                    );
                    GamePanel.camera.bufferUpdateGrid = true;
                    hasAttacked = true;
                }

                //stop the attack
                if (!getAnimation().equals(attack)) {
                    isAttacking = false;
                    hasAttacked = false;
                    atkCooldown = Math.max(maxAtkCooldown*Math.random(), minAtkCooldown);
                }
            }
        }

        //patrolling
        else{
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
                setAnimation(walk, walkAnimSpeed, offsetX, offsetY);
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
    }

    @Override
    public void move() throws Exception {
        //x movement
        if (isVulnerable && !isAttacking){
            if (isSafeGround(-maxSpeed*direction) && !isWall(-maxSpeed*direction, false)) {
                velocityX = Math.min(maxSpeed, Math.max(-maxSpeed, velocityX-acceleration*direction));

                if (Math.abs(velocityX) > 2*acceleration && !getAnimation().equals(walk)){
                    setAnimation(walk, walkAnimSpeed, offsetX, offsetY);
                }
                else if (Math.abs(velocityX) < 2*acceleration && !getAnimation().equals(idle)){
                    setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
                }
            }
            else {
                velocityX = 0;

                if (!getAnimation().equals(idle)) setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
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
            if (hp <= 0) setNextAnimation(dying, dyingAnimSpeed, offsetX, offsetY - 6);
            else if (isChasing) setNextAnimation(walk, walkAnimSpeed, offsetX, offsetY);
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
        return new Magician(this);
    }
}
