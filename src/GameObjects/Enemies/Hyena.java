package GameObjects.Enemies;

import GameObjects.Entity;
import GameObjects.GameObject2D;
import GameObjects.Sprite;
import main.GamePanel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Hyena extends Entity {

    int speed;
    ArrayList<BufferedImage> idle;
    final int idleAnimSpeed = 3;
    ArrayList<BufferedImage> run;
    final int runAnimSpeed = 1;
    ArrayList<BufferedImage> damageAnim;
    final int damageAnimSpeed = 2;
    ArrayList<BufferedImage> dying;
    final  int dyingAnimSpeed = 1;
    ArrayList<BufferedImage> dead;
    final  int deadAnimSpeed = 100;

    final int offsetX = 15, offsetY = 50;

    int initialPosX, initialPosY;

    final double gravity = 2.25;
    final int maxYSpeed = 150;
    final double acceleration = 3;
    final int runSpeed = 50;
    boolean isChasing = false;
    boolean hadSideCollision = true;

    int direction = 1;

    final int detectionRangeX = 500;

    final int turnTime = 3; //3s
    double turnTimer = 0;

    boolean isVulnerable = true;
    boolean isDead = false;

    public Hyena(int x, int y, String id, String subLvl) throws IOException {
        super(x, y, 75, 40, subLvl);
        type = "Hyena";
        name = type+id;

        hasHP = true;
        hp  = 100;

        initialPosX = x;
        initialPosY = y;

        idle = getAnimationList("Enemy/Hyena", "idle", 3);
        run = getAnimationList("Enemy/Hyena", "run", 5);
        damageAnim = getAnimationList("Enemy/Hyena", "damage", 1);
        dying = getAnimationList("Enemy/Hyena", "dying", 5);
        dead = getAnimationList("Enemy/Hyena", "dead", 0);

        sprite = new Sprite(idle.get(0), 3);
        setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
    }

    public Hyena(Hyena h) {
        super(h);

        run = h.run;
        idle = h.idle;
        isChasing = h.isChasing;
        direction = h.direction;
        turnTimer = h.turnTimer;
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
                setNextAnimation(dead, deadAnimSpeed, offsetX, offsetY);
            }
            return;
        }

        if (!isVulnerable){
            if (getAnimation().equals(idle)) isVulnerable = true;
            else return;
        }

        if (isChasing){
            if (hadSideCollision){
                isChasing = false;
                hadSideCollision = false;

                direction = -direction;
                sprite.setDirection(direction);
                turnTimer = 0;
                setAnimation(idle, idleAnimSpeed, offsetX, offsetY);
            }
            velocityX = Math.min(runSpeed, Math.max(-runSpeed, velocityX-acceleration*direction));
        }

        else{
            if (velocityX != 0) stop();


            //turning
            turnTimer += GamePanel.deltaTime/10;
            if (turnTimer >= turnTime){
                turnTimer = 0;

                direction = -direction;

                sprite.setDirection(direction);
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
                setAnimation(run, runAnimSpeed, offsetX, offsetY);
                isChasing = true;
            }
        }
    }

    @Override
    public void move() throws Exception {
        velocityY = Math.max(-maxYSpeed, velocityY - (gravity * GamePanel.deltaTime * 6));

        GamePanel.camera.deleteGOInGrid(this, false);
        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round(velocityX * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round(velocityY * GamePanel.deltaTime)));

        hadSideCollision = false;
        for (GameObject2D go: getNear()){
            int didCollide = didCollide(go);

            if (didCollide != 0 && go.type.equals("Player")) GameObject2D.getPlayer().death(GameObject2D.getPlayer().spawnPointPos);
            else if ((didCollide == 3 || didCollide == 4) && go.hasPhysicalCollisions && isChasing) {
                hadSideCollision = true;
                if (go.isEntity && !go.type.equals("Hyena")) go.getThisEntity().damage(25);
            }
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

        if (!isDead && !e.type.equals("Hyena")) e.damage(25);
    }

    @Override
    public void damage(int damage) {
        if (isVulnerable && hasHP){
            hp -= damage;

            isChasing = false;
            isVulnerable = false;
            setAnimation(damageAnim, damageAnimSpeed, offsetX, offsetY);
            if (hp <= 0){
                setNextAnimation(dying, dyingAnimSpeed, offsetX, offsetY);
            }
            else {
                setNextAnimation(idle, idleAnimSpeed, offsetX, offsetY);
                if (isChasing) hadSideCollision = true;
            }
        }
    }

    @Override
    public void reset() throws Exception {
        super.reset();

        isDead = false;
        hasPhysicalCollisions = true;
        hasHP = true;
        hp = 100;

        isChasing = false;
        hadSideCollision = false;
        turnTimer = 0;
        direction = 1;
        sprite.setDirection(direction);
        setAnimation(idle, idleAnimSpeed, offsetX, offsetY);

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
        return new Hyena(this);
    }
}
