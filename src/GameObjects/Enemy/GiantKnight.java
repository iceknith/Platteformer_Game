package GameObjects.Enemy;

import GameObjects.*;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

public class GiantKnight extends Entity {

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

    final int offsetX = 25, offsetY = 130;

    int initialPosX, initialPosY, initDirection = 0;

    final double gravity = 2.5;
    final int maxYSpeed = 200;
    final double runAcceleration = 3;
    final double walkAcceleration = 1;
    double acceleration = walkAcceleration;
    final int runSpeed = 30;
    final int walkSpeed = 12;
    int maxSpeed = walkSpeed;
    final  int maxHealth = 200;
    final int knockBackForce = 20;
    boolean isChasing = false;
    boolean isAttacking = false;
    final int atkWidth = 214, atkHeight = 402, atkOffsetX = 506, atkOffsetY = 200;
    boolean hasAttacked;
    final double maxAtkCooldown = 3, minAtkCooldown = 1.5;
    double atkCooldown = 0;

    int direction = 1;

    final int detectionRangeX = 1500;

    final int turnTime = 3; //3s
    double turnTimer = 0;

    boolean isVulnerable = true;
    boolean isDead = false;

    final int bossBarWidth = 431 * 2;
    final int bossBarHeight = 47 * 2;
    final Font bossBarFont = new Font("Eight Bit Dragon", Font.PLAIN, 35);
    final int bossTxtX = GamePanel.camera.getScreenWidth()/2;
    final int bossBarX = bossTxtX - bossBarWidth/2;
    final int bossTxtY = 50;
    final String bossName = "Giant Knight";
    BufferedImage bossBarOverlay, bossBarUnderlay;

    ParticleGenerator exclamationMark;

    public GiantKnight(int x, int y, String id, String subLvl) throws IOException {
        super(x, y, 130, 287, subLvl);
        type = "GiantKnight";
        name = type+id;
        isEnemy = true;
        doesDamage = true;

        hasHP = true;
        hp = maxHealth;

        initialPosX = x;
        initialPosY = y;

        idle = getAnimationList("Enemy/GiantKnight", "idle", 7);
        run = getAnimationList("Enemy/GiantKnight", "run", 7);
        damageAnim = getAnimationList("Enemy/GiantKnight", "damage", 5);
        dying = getAnimationList("Enemy/GiantKnight", "dying", 6);
        dead = getAnimationList("Enemy/GiantKnight", "dead", 0);
        attack = getAnimationList("Enemy/GiantKnight", "attack", 10);
        bossBarOverlay = readImageBuffered("assets/Enemy/BossBar/boss_bar_overlay.png");
        bossBarUnderlay = readImageBuffered("assets/Enemy/BossBar/boss_bar_underlay.png");

        sprite = new Sprite(idle.get(0), 3.5);
        sprite.setDirection(-direction);
        setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
    }

    public GiantKnight(GiantKnight k) {
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
        bossBarOverlay = k.bossBarOverlay;
        bossBarUnderlay = k.bossBarUnderlay;
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
        if (isChasing && !GamePanel.camera.getVisible().contains(this)){
            GamePanel.camera.getVisible().add(this);
        }
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
        if (isDead) return;
        if (exclamationMark != null) exclamationMark.draw(g2D, IO);

        //Draw boss Bar
        final int bossTxtX = this.bossTxtX - g2D.getFontMetrics(bossBarFont).stringWidth(bossName)/2;
        final int bossBarY = bossTxtY + g2D.getFontMetrics(bossBarFont).getHeight()/2 - 15;

        g2D.setFont(bossBarFont);
        g2D.setColor(Color.black);
        g2D.drawString(bossName, bossTxtX + 5, bossTxtY + 5);
        g2D.setColor(Color.decode("#AD2A3C"));
        g2D.drawString(bossName, bossTxtX, bossTxtY);

        g2D.drawImage(bossBarUnderlay, bossBarX, bossBarY, (int) (bossBarWidth * hp/maxHealth), bossBarHeight, IO);
        g2D.drawImage(bossBarOverlay, bossBarX, bossBarY, bossBarWidth, bossBarHeight, IO);
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
            int posYMin = getY();
            int posYMax = getY() + getHeight();

            int playerPosX = GameObject2D.getPlayer().getX() + GameObject2D.getPlayer().getWidth()/2;
            int playerPosY = GameObject2D.getPlayer().getY() + GameObject2D.getPlayer().getHeight()/2;

            if(posXMin < playerPosX && playerPosX < posXMax &&
                    posYMin < playerPosY && playerPosY < posYMax){
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
                final int posX = getSprite().getOffsetX(hitbox) - getDirection() * atkOffsetX - atkWidth * (getDirection() + 1)/2;
                final int posY = getSprite().getOffsetY(hitbox) + atkOffsetY;

                for (GameObject2D go : getInBox(posX, posY, atkWidth, atkHeight)){
                    if (go.type.equals("Player") || (go.type.equals("IceBlock") && go.hasPhysicalCollisions)){
                        setAnimation(attack, attackAnimSpeed);
                        setNextAnimation(idle, idleAnimSpeed);
                        isAttacking = true;
                        atkCooldown = minAtkCooldown + (maxAtkCooldown - minAtkCooldown) * Math.random();
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
        if (isAttacking && !hasAttacked && getAnimationIndex() == 6){
            final int posX = getSprite().getOffsetX(hitbox) - getDirection() * atkOffsetX - atkWidth * (getDirection() + 1)/2;
            final int posY = getSprite().getOffsetY(hitbox) + atkOffsetY;

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
            if (isSafeGround(-(maxSpeed+getWidth()/2)*direction) && !isWall(-(maxSpeed+getWidth()/2)*direction, false)) {
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
            if (hp <= 0) setAnimation(dying, dyingAnimSpeed, offsetX, offsetY);
            else {
                setAnimation(damageAnim, damageAnimSpeed, offsetX, offsetY);
                if (isChasing) setNextAnimation(run, runAnimSpeed, offsetX, offsetY);
                else setNextAnimation(idle, idleAnimSpeed, offsetX, offsetY);
            }
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
        return new GiantKnight(this);
    }
}

