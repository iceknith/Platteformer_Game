package GameObjects.Enemy;

import GameObjects.*;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

public class Dragon extends Entity {

    ArrayList<BufferedImage> idle;
    final int idleAnimSpeed = 3;
    ArrayList<BufferedImage> run;
    final int defaultWalkAnimSpeed = 2;
    final int defaultRunAnimSpeed = 1;
    int runAnimSpeed = defaultWalkAnimSpeed;
    ArrayList<BufferedImage> takeOff;
    final int takeOffAnimSpeed = 1;
    ArrayList<BufferedImage> fly;
    final int flyAnimSpeed = 1;
    ArrayList<BufferedImage> landing;
    final int landingAnimSpeed = 1;
    ArrayList<BufferedImage> attackGround;
    final int attackAnimSpeed = 1;
    ArrayList<BufferedImage> attackAir;
    final int summonAnimSpeed = 1;

    ArrayList<BufferedImage> damageGroundAnim, damageAirAnim;
    final double damageAnimSpeed = 1;
    ArrayList<BufferedImage> dying;
    final double dyingAnimSpeed = 1;
    ArrayList<BufferedImage> dead;
    final  int deadAnimSpeed = 100;

    final int groundOffsetX = 8, groundOffsetY = 138, groundWidth = 246, groundHeight = 108;
    final int airOffsetX = -54, airOffsetY = 18, airWidth = 90, airHeight = 186;
    int offsetX = groundOffsetX, offsetY = groundOffsetY;

    int initialPosX, initialPosY, initDirection = 0;

    final double groundGravity = 2.5, flyGravity = 1;
    final int maxYSpeed = 200;
    final double runAcceleration = 3;
    final double walkAcceleration = 1;
    double acceleration = walkAcceleration;
    final int runSpeed = 25;
    final int walkSpeed = 10;
    int maxSpeed = walkSpeed;
    final  int maxHealth = 450;
    final int knockBackForce = 20;
    final double flyForce = 20;
    boolean isChasing = false;
    boolean isSummoning = false;
    boolean hasSummoned = false;
    boolean isLaunchingFireBall = false;
    boolean hasLaunchedFireBall = false;
    final int fireBallWidth = 66;
    final int fireBallHeight = 66;
    final int fireBallOffsetX = 54;
    final int fireBallOffsetY = 120;
    final int fireBallSpeed = 65;
    final double maxFireBallCooldown = 4, minFireBallCooldown = 2.5;
    double fireBalCooldown = 0;

    int direction = 1;

    final int detectionRangeX = 2500, flyDistanceY = 350;

    final int turnTime = 3; //3s
    double turnTimer = 0;

    boolean isVulnerable = true;
    boolean isDead = false;
    boolean isFlying = false;
    boolean hasHitboxAdjusted;

    ParticleGenerator exclamationMark;

    final int bossBarWidth = 431 * 2;
    final int bossBarHeight = 47 * 2;
    final Font bossBarFont = new Font("Eight Bit Dragon", Font.PLAIN, 35);
    final int bossTxtX = GamePanel.camera.getScreenWidth()/2;
    final int bossBarX = bossTxtX - bossBarWidth/2;
    final int bossTxtY = 50;
    final String bossName = "Great Dragon";
    BufferedImage bossBarOverlay, bossBarUnderlay;

    public Dragon(int x, int y, String id, String subLvl) throws IOException {
        super(x, y, 246, 108, subLvl);
        type = "Dragon";
        name = type+id;
        isEnemy = true;
        doesDamage = true;

        hasHP = true;
        hp = maxHealth;

        initialPosX = x;
        initialPosY = y;

        idle = getAnimationList("Enemy/Dragon", "idle", 4);
        run = getAnimationList("Enemy/Dragon", "run", 4);
        takeOff = getAnimationList("Enemy/Dragon", "takeoff", 2);
        fly = getAnimationList("Enemy/Dragon", "fly", 6);
        landing = getAnimationList("Enemy/Dragon", "landing", 1);
        damageGroundAnim = getAnimationList("Enemy/Dragon", "damage_ground", 0);
        damageAirAnim = getAnimationList("Enemy/Dragon", "damage_air", 0);
        dying = getAnimationList("Enemy/Dragon", "dying", 16);
        dead = getAnimationList("Enemy/Dragon", "dead", 0);
        attackGround = getAnimationList("Enemy/Dragon", "attack_ground", 14);
        attackAir = getAnimationList("Enemy/Dragon", "attack_air", 9);
        bossBarOverlay = readImageBuffered("assets/Enemy/BossBar/boss_bar_overlay.png");
        bossBarUnderlay = readImageBuffered("assets/Enemy/BossBar/boss_bar_underlay.png");

        sprite = new Sprite(idle.get(0), 6);
        sprite.setDirection(-direction);
        setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
    }
    public Dragon(Dragon s) {
        super(s);

        run = s.run;
        idle = s.idle;
        damageGroundAnim = s.damageGroundAnim;
        dying = s.dying;
        dead = s.dead;
        attackGround = s.attackGround;
        attackAir = s.attackAir;
        bossBarOverlay = s.bossBarOverlay;
        bossBarUnderlay = s.bossBarUnderlay;

        runAnimSpeed = s.runAnimSpeed;
        acceleration = s.acceleration;
        maxSpeed = s.maxSpeed;

        isChasing = s.isChasing;
        isLaunchingFireBall = s.isLaunchingFireBall;
        isSummoning = s.isSummoning;
        hasLaunchedFireBall = s.hasLaunchedFireBall;
        hasSummoned = s.hasSummoned;
        fireBalCooldown = s.fireBalCooldown;
        direction = s.direction;
        turnTimer = s.turnTimer;
        initialPosX = s.initialPosX;
        initialPosY = s.initialPosY;
        initDirection = s.initDirection;
        isVulnerable = s.isVulnerable;
        isDead = s.isDead;

        exclamationMark = s.exclamationMark;
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
            if (ragdolPhysics(groundGravity) && getAnimation().equals(dying) && animationSpeed == 100000){
                animationSpeed = dyingAnimSpeed;
            }
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
            if (!getAnimation().equals(damageGroundAnim)) isVulnerable = true;
            else return;
        }

        if (isChasing){
            chaseLogic();
        }

        //patrolling
        else{
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
    }

    void chaseLogic() throws Exception {
        //following the player
        if (!isLaunchingFireBall){
            setDirection((int) Math.signum(getX() + (float) getWidth() /2 - getPlayer().getX() - (float) getPlayer().getWidth() /2));
        }

        if (maxSpeed != runSpeed){
            maxSpeed = runSpeed;
            acceleration = runAcceleration;
            runAnimSpeed = defaultRunAnimSpeed;
        }

        if (hp <= maxHealth/2){
            if (!isFlying){
                isFlying = true;
                hasHitboxAdjusted = true;
                setAnimation(takeOff, takeOffAnimSpeed, offsetX, offsetY);
                setNextAnimation(fly, flyAnimSpeed, offsetX, offsetY);
            }

            airChaseLogic();
        }
        else{
            groundChaseLogic();
        }
    }

    void groundChaseLogic() throws Exception {
        if (fireBalCooldown <= 0 && !isLaunchingFireBall){
            //launch attack
            isLaunchingFireBall = true;
            setAnimation(attackGround, attackAnimSpeed, offsetX, offsetY);
            setNextAnimation(idle, idleAnimSpeed, offsetX, offsetY);
        }
        else fireBalCooldown -= GamePanel.deltaTime/10;

        if (isLaunchingFireBall){
            if (!getAnimation().equals(attackGround)){
                isLaunchingFireBall = false;
                hasLaunchedFireBall = false;
                fireBalCooldown = minFireBallCooldown + (maxFireBallCooldown - minFireBallCooldown) * Math.random();
            }
            //launch attack
            else if (!hasLaunchedFireBall && getAnimationIndex() == 9){
                hasLaunchedFireBall = true;

                final int posX = getX() + getWidth()/2 - fireBallOffsetX*getDirection() - fireBallWidth/2;
                final int posY = getY() + getHeight()/2 - fireBallOffsetY - fireBallHeight/2;
                final int diffX = getPlayer().getX() + getPlayer().getWidth()/2 - posX - fireBallWidth/2;
                final int diffY = getPlayer().getY() + getPlayer().getHeight()/2 - posY - fireBallHeight/2;
                double slope;

                for (double i = -Math.PI/10; i <= Math.PI/10; i += Math.PI/10){
                    slope = Math.atan((double) diffY/diffX) + i;

                    if (diffX <= 0){
                        if (diffY >= 0) slope -= Math.PI;
                        else slope += Math.PI;
                    }

                    GamePanel.camera.addGOInGrid(
                            new Projectile(posX, posY, fireBallWidth, fireBallHeight,
                                    2, 46, 54, (int) Math.signum((float) diffX /diffY),
                                    fireBallSpeed*Math.cos(slope)/10, - fireBallSpeed*Math.sin(slope)/10,
                                    fireBallSpeed*Math.cos(slope), fireBallSpeed*Math.sin(slope),
                                    5, 100, true, false,
                                    "Fire_Ball", 0, 5, 0,
                                    1, subLevelName),
                            true
                    );
                }
                GamePanel.camera.bufferUpdateGrid = true;
            }
        }
    }

    void airChaseLogic() throws Exception {

        //fire ball
        if (fireBalCooldown <= 0 && !isLaunchingFireBall){
            //launch attack
            isLaunchingFireBall = true;
            setAnimation(attackAir, attackAnimSpeed, offsetX, offsetY);
            setNextAnimation(fly, flyAnimSpeed, offsetX, offsetY);
        }
        else fireBalCooldown -= GamePanel.deltaTime/10;

        if (isLaunchingFireBall){
            if (!getAnimation().equals(attackAir)){
                isLaunchingFireBall = false;
                hasLaunchedFireBall = false;
                fireBalCooldown = minFireBallCooldown + (maxFireBallCooldown - minFireBallCooldown) * Math.random();
            }
            //launch attack
            else if (!hasLaunchedFireBall && getAnimationIndex() == 5){
                hasLaunchedFireBall = true;

                final int posX = getX() + getWidth()/2 - fireBallOffsetX*getDirection() - fireBallWidth/2;
                final int posY = getY() + getHeight()/2 - fireBallOffsetY - fireBallHeight/2;
                final int diffX = getPlayer().getX() + getPlayer().getWidth()/2 - posX - fireBallWidth/2;
                final int diffY = getPlayer().getY() + getPlayer().getHeight()/2 - posY - fireBallHeight/2;
                double slope;

                for (double i = -Math.PI/5; i <= Math.PI/5; i += Math.PI/10){
                    slope = Math.atan((double) diffY/diffX) + i;

                    if (diffX <= 0){
                        if (diffY >= 0) slope -= Math.PI;
                        else slope += Math.PI;
                    }

                    GamePanel.camera.addGOInGrid(
                            new Projectile(posX, posY, fireBallWidth, fireBallHeight,
                                    2, 46, 54, (int) Math.signum((float) diffX /diffY),
                                    fireBallSpeed*Math.cos(slope)/10, - fireBallSpeed*Math.sin(slope)/10,
                                    fireBallSpeed*Math.cos(slope), fireBallSpeed*Math.sin(slope),
                                    5, 100, true, false,
                                    "Fire_Ball", 0, 5, 0,
                                    1, subLevelName),
                            true
                    );
                }
                GamePanel.camera.bufferUpdateGrid = true;
            }
        }
    }

    @Override
    public void move() throws Exception {
        //x movement
        if (isVulnerable){
            //Ground logic
            if (!isFlying){
                if (isSafeGround(-maxSpeed*3*direction) && !isWall(-maxSpeed*3*direction, false)) {
                    velocityX = Math.min(maxSpeed, Math.max(-maxSpeed, velocityX-acceleration*direction));

                    if ((!getAnimation().equals(run) || animationSpeed != runAnimSpeed) && !isLaunchingFireBall && !isSummoning)
                        setAnimation(run, runAnimSpeed, offsetX, offsetY);
                }
                else {
                    velocityX = 0;

                    if (!getAnimation().equals(idle) && !isLaunchingFireBall && !isSummoning) setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
                }
            }
            //air logic
            else{
                velocityX = Math.min(maxSpeed, Math.max(-maxSpeed, velocityX-acceleration*direction));
            }
        }

        //y movement
        if (!isFlying){
            velocityY = Math.max(-maxYSpeed, velocityY - (groundGravity * GamePanel.deltaTime * 6));
        }
        else {
            if (velocityY <= 0 && getPlayer().getY() < getY() + flyDistanceY) velocityY = flyForce;
            else {
                velocityY = Math.max(-maxYSpeed, velocityY - (flyGravity * GamePanel.deltaTime * 6));
            }
        }

        GamePanel.camera.deleteGOInGrid(this, false);

        if (hasHitboxAdjusted){
            hasHitboxAdjusted = false;
            if (isFlying){
                final int imgX = sprite.getOffsetX(hitbox);
                final int imgY = sprite.getOffsetY(hitbox);
                hitbox.width = airWidth;
                hitbox.height = airHeight;
                offsetX = airOffsetX;
                offsetY = airOffsetY;
                setAnimation(getAnimation(), animationSpeed, offsetX, offsetY);
                setNextAnimation(nextAnimation, nextAnimationSpeed, offsetX, offsetY);
                setX(imgX + offsetX*sprite.getDirection() - getWidth()/2 + sprite.getWidth()/2);
                setY(imgY + offsetY  -  getHeight()/2 + sprite.getHeight()/2);
            }
            else {
                final int imgX = sprite.getOffsetX(hitbox);
                final int imgY = sprite.getOffsetY(hitbox);
                hitbox.width = groundWidth;
                hitbox.height = groundHeight;
                offsetX = groundOffsetX;
                offsetY = groundOffsetY;
                setX(imgX + offsetX*sprite.getDirection() - getWidth()/2 + sprite.getWidth()/2);
                setY(imgY + offsetY  -  getHeight()/2 + sprite.getHeight()/2);
                setAnimation(getAnimation(), animationSpeed, offsetX, offsetY);
                setNextAnimation(nextAnimation, nextAnimationSpeed, offsetX, offsetY);
            }
        }

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
            if (isFlying) setAnimation(damageAirAnim, damageAnimSpeed, offsetX, offsetY);
            else setAnimation(damageGroundAnim, damageAnimSpeed, offsetX, offsetY);

            if (hp <= 0) {
                isFlying = false;
                hasHitboxAdjusted = true;
                setNextAnimation(dying, 100000, offsetX, offsetY);
                move();
            }
            else if (isChasing) {
                if (isFlying) setNextAnimation(fly, flyAnimSpeed, offsetX, offsetY);
                else setNextAnimation(run, runAnimSpeed, offsetX, offsetY);
            }
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

        fireBalCooldown = 0;
        isLaunchingFireBall = false;
        hasLaunchedFireBall = false;
        isSummoning = false;
        offsetX = groundOffsetX;
        offsetY = groundOffsetY;

        isFlying = false;
        hasHitboxAdjusted = false;

        turnTimer = 0;
        setDirection(initDirection);
        setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
        setNextAnimation(null, 0);
        exclamationMark = null;

        GamePanel.camera.deleteGOInGrid(this, true);
        hitbox.width = groundWidth;
        hitbox.height = groundHeight;
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
        return new Dragon(this);
    }
}

