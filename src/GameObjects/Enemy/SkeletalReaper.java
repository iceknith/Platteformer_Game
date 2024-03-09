package GameObjects.Enemy;

import GameObjects.*;
import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

public class SkeletalReaper extends Entity {

    ArrayList<BufferedImage> idle;
    final int idleAnimSpeed = 3;
    ArrayList<BufferedImage> run;
    final int defaultWalkAnimSpeed = 2;
    final int defaultRunAnimSpeed = 1;
    int runAnimSpeed = defaultWalkAnimSpeed;
    ArrayList<BufferedImage> attack;
    final int attackAnimSpeed = 1;
    ArrayList<BufferedImage> summon;
    final int summonAnimSpeed = 1;

    ArrayList<BufferedImage> damageAnim;
    final double damageAnimSpeed = 1;
    ArrayList<BufferedImage> dying;
    final double dyingAnimSpeed = 1;
    ArrayList<BufferedImage> dead;
    final  int deadAnimSpeed = 100;

    final int offsetX = -130, offsetY = 80;

    int initialPosX, initialPosY, initDirection = 0;

    final double gravity = 2.5;
    final int maxYSpeed = 200;
    final double runAcceleration = 3;
    final double walkAcceleration = 1;
    double acceleration = walkAcceleration;
    final int runSpeed = 35;
    final int walkSpeed = 15;
    int maxSpeed = walkSpeed;
    final  int maxHealth = 350;
    final int knockBackForce = 20;
    boolean isChasing = false;
    boolean isSummoning = false;
    boolean hasSummoned = false;
    boolean isAttacking = false;
    boolean hasAttacked = false;
    final int atkWidth = 370;
    final int atkHeight = 255;
    final int atkRelativeX = 175;
    final int atkRelativeY = 65;
    final int minSummonRange = 0;
    final int maxSummonRange = 2500;
    final double maxAtkCooldown = 3;
    double atkCooldown = 0;
    final double maxSummonCooldown = 4;
    double summonCooldown = maxSummonCooldown;
    int summonPosX, summonPosY;

    int direction = 1;

    final int detectionRangeX = 2500;

    final int turnTime = 3; //3s
    double turnTimer = 0;

    boolean isVulnerable = true;
    boolean isDead = false;

    ParticleGenerator exclamationMark;

    final int bossBarWidth = 431 * 2;
    final int bossBarHeight = 47 * 2;
    final Font bossBarFont = new Font("Eight Bit Dragon", Font.PLAIN, 35);
    final int bossTxtX = GamePanel.camera.getScreenWidth()/2;
    final int bossBarX = bossTxtX - bossBarWidth/2;
    final int bossTxtY = 50;
    final String bossName = "Dark Reaper Of Hell";
    BufferedImage bossBarOverlay, bossBarUnderlay;

    public SkeletalReaper(int x, int y, String id, String subLvl) throws IOException {
        super(x, y, 95, 160, subLvl);
        type = "SkeletalReaper";
        name = type+id;
        isEnemy = true;
        doesDamage = true;

        hasHP = true;
        hp = maxHealth;

        initialPosX = x;
        initialPosY = y;

        idle = getAnimationList("Enemy/SkeletalReaper", "idle", 3);
        run = getAnimationList("Enemy/SkeletalReaper", "run", 7);
        damageAnim = getAnimationList("Enemy/SkeletalReaper", "damage", 0);
        dying = getAnimationList("Enemy/SkeletalReaper", "dying", 13);
        dead = getAnimationList("Enemy/SkeletalReaper", "dead", 0);
        attack = getAnimationList("Enemy/SkeletalReaper", "attack", 13);
        summon = getAnimationList("Enemy/SkeletalReaper", "summon", 6);
        bossBarOverlay = readImageBuffered("assets/Enemy/BossBar/boss_bar_overlay.png");
        bossBarUnderlay = readImageBuffered("assets/Enemy/BossBar/boss_bar_underlay.png");

        sprite = new Sprite(idle.get(0), 5);
        sprite.setDirection(-direction);
        setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
    }
    public SkeletalReaper(SkeletalReaper s) {
        super(s);

        run = s.run;
        idle = s.idle;
        damageAnim = s.damageAnim;
        dying = s.dying;
        dead = s.dead;
        attack = s.attack;
        summon = s.summon;
        bossBarOverlay = s.bossBarOverlay;
        bossBarUnderlay = s.bossBarUnderlay;

        runAnimSpeed = s.runAnimSpeed;
        acceleration = s.acceleration;
        maxSpeed = s.maxSpeed;

        isChasing = s.isChasing;
        isAttacking = s.isAttacking;
        isSummoning = s.isSummoning;
        hasAttacked = s.hasAttacked;
        hasSummoned = s.hasSummoned;
        atkCooldown = s.atkCooldown;
        summonCooldown = s.summonCooldown;
        summonPosX = s.summonPosX;
        summonPosY = s.summonPosY;
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
        if (!GamePanel.camera.getVisible().contains(this)){
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

            //summon
            if (summonCooldown <= 0){
                final double distX = getDistance(getPlayer());

                if (!isSummoning && !isAttacking
                        && minSummonRange <= distX && distX <= maxSummonRange
                        && getPlayer().ground != null){
                    isSummoning = true;
                    setAnimation(summon, summonAnimSpeed);
                    setNextAnimation(run, runAnimSpeed);

                    summonPosX = getPlayer().getX() + getPlayer().getWidth()/2;
                    summonPosY = getPlayer().getY() + getPlayer().getHeight();
                }
            }
            else summonCooldown -= GamePanel.deltaTime/10;

            if (isSummoning){
                stop();

                if (!getAnimation().equals(summon)){
                    isSummoning = false;
                    hasSummoned = false;
                    summonCooldown = maxSummonCooldown;
                }
                else if (getAnimationIndex() == 0 && !hasSummoned) {
                    final int w = 20, h = 115, s = w*3;
                    final int x = summonPosX - w / 2;
                    final int y = summonPosY - h;
                    for (int i = -2*s; i <= 2*s; i+=s){
                        Projectile p = new Projectile(x + i, y, w, h,
                                5, -5, 10, -getDirection(),
                                0, 0, 0, 0,
                                0.5, 100,
                                true, true,
                                "DarkSpike", 4, 0, 1, 1,
                                subLevelName);
                        if (p.isSafeGround(0)){
                            GamePanel.camera.addGOInGrid(p, true);
                        }
                    }
                    GamePanel.camera.bufferUpdateGrid = true;
                    hasSummoned = true;
                }
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
        if (!isAttacking && !isSummoning){
            if (atkCooldown <= 0){
                final int posX = getSprite().getOffsetX(hitbox) - getDirection() * atkRelativeX - atkWidth * (getDirection() + 1)/2;
                final int posY = getSprite().getOffsetY(hitbox) + atkRelativeY;

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

        if (isAttacking){
            stop();
            if (!getAnimation().equals(attack)){
                isAttacking = false;
                hasAttacked = false;
            }
        }

        //effectue the damages
        if (isAttacking && !hasAttacked && getAnimationIndex() == 5){
            final int[] atkXList = {175, 430, 175},
                    atkYList = {65, 125, 180},
                    atkWList = {255, 70, 370},
                    atkHList = {255, 195, 140},
                    durationList = {1,1,2};

            for (int i = 0; i < atkXList.length; i++){
                final int atkX = atkXList[i], atkY = atkYList[i],
                        atkW = atkWList[i], atkH = atkHList[i],
                        duration = durationList[i];

                final int posX = getSprite().getOffsetX(hitbox) - getDirection() * atkX - atkW * (getDirection() + 1)/2;
                final int posY = getSprite().getOffsetY(hitbox) + atkY;
                GamePanel.camera.addGOInGrid(
                        new DamageArea(posX, posY, atkW, atkH,
                                (double) duration/10, 25,
                                true, subLevelName)
                );
            }
            GamePanel.camera.bufferUpdateGrid = true;
            hasAttacked = true;
        }
    }

    @Override
    public void move() throws Exception {
        //x movement
        if (isVulnerable){
            if (isSafeGround(-maxSpeed*3*direction) && !isWall(-maxSpeed*3*direction, false)) {
                velocityX = Math.min(maxSpeed, Math.max(-maxSpeed, velocityX-acceleration*direction));

                if ((!getAnimation().equals(run) || animationSpeed != runAnimSpeed) && !isAttacking && !isSummoning)
                    setAnimation(run, runAnimSpeed, offsetX, offsetY);
            }
            else {
                velocityX = 0;

                if (!getAnimation().equals(idle) && !isAttacking && !isSummoning) setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
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
        summonCooldown = maxSummonCooldown;
        summonPosX = 0;
        summonPosY = 0;
        isAttacking = false;
        hasAttacked = false;
        isSummoning = false;

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
        return new SkeletalReaper(this);
    }
}

