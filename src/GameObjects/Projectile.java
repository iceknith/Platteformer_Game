package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

public class Projectile extends Entity{

    ArrayList<BufferedImage> startAnimation, baseAnimation, endAnimation;
    double speedX, speedY, maxSpeedX, maxSpeedY, duration, animationSpeed;
    int damage, offsetX, offsetY;
    boolean ignoresEnemy, ignoresCollision, kill = false, destroy = false;
    ParticleGenerator particles;


    public Projectile(int x, int y, int w, int h, double resizeFact, int offsetX, int offsetY, int direction,
                      double speedX, double speedY, double maxSpeedX, double maxSpeedY,
                      double duration, int damage, boolean ignoresEnemy, boolean ignoresCollision,
                      String animationName, int startAnimFrameCnt, int baseAnimFrameCnt, int endAnimFrameCnt,
                      double animationSpeed, String subLvl) throws IOException {
        super(x, y, w, h, subLvl);
        type = "Projectile";
        name = type + animationName;

        hasPhysicalCollisions = false;
        hasHP = false;
        isEnemy = true;

        this.speedX = speedX;
        this.speedY = speedY;
        this.maxSpeedX = maxSpeedX;
        this.maxSpeedY = maxSpeedY;
        this.duration = duration;
        this.damage = damage;
        this.ignoresEnemy = ignoresEnemy;
        this.ignoresCollision = ignoresCollision;
        startAnimation = getAnimationList("Projectile", animationName + "/start", startAnimFrameCnt);
        baseAnimation = getAnimationList("Projectile", animationName + "/base", baseAnimFrameCnt);
        endAnimation = getAnimationList("Projectile", animationName + "/end", endAnimFrameCnt);
        this.animationSpeed = animationSpeed;
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        sprite = new Sprite(baseAnimation.get(0), resizeFact);
        sprite.setDirection(direction);
        setAnimation(startAnimation, animationSpeed, offsetX, offsetY);
        setNextAnimation(baseAnimation, animationSpeed, offsetX, offsetY);
    }

    protected Projectile(Projectile p) {
        super(p);
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {
        super.draw(g2D, IO);

        if (particles != null) particles.draw(g2D, IO);
    }

    @Override
    public void update() throws Exception {
        if (getAnimation().equals(baseAnimation)) duration -= GamePanel.deltaTime/10;
        if (duration <= 0 && !kill) {
            kill = true;
            setAnimation(endAnimation, animationSpeed, offsetX, offsetY);
            setNextAnimation(startAnimation, animationSpeed, offsetX, offsetY);
        }
        if (kill){
            if (!getAnimation().equals(endAnimation)) destroy = true;
            else {
                animate();
                return;
            }
        }
        if (destroy) {
            killThisEntity();
            return;
        }

        super.update();
        animate();
        move();
    }

    @Override
    public void move() throws Exception{

        velocityX = Math.max(-Math.abs(maxSpeedX), Math.min(Math.abs(maxSpeedX), velocityX + speedX));
        velocityY = Math.max(-Math.abs(maxSpeedY), Math.min(Math.abs(maxSpeedY), velocityY + speedY));

        GamePanel.camera.deleteGOInGrid(this, false);
        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round(velocityX * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round(velocityY * GamePanel.deltaTime)));

        if (getAnimation().equals(baseAnimation)){
            for (GameObject2D go: getNear()){
                if ((ignoresCollision && !go.getType().equals("Player"))
                        || (ignoresEnemy && go.isEntity && go.getThisEntity().isEnemy)
                        || !go.hasPhysicalCollisions) {
                    continue;
                }

                int didCollide = didCollide(go);

                if (didCollide != 0 || intersects(go)){
                    if (go.isEntity) {
                        go.getThisEntity().damage(damage);
                    }

                    kill = true;
                    setAnimation(endAnimation, animationSpeed, offsetX, offsetY);
                    setNextAnimation(startAnimation, animationSpeed, offsetX, offsetY);
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

        if ((ignoresEnemy && e.isEnemy)
                || (ignoresCollision && !e.getType().equals("Player"))
                || !getAnimation().equals(baseAnimation)){
            return;
        }

        e.damage(damage);
        kill = true;
        setAnimation(endAnimation, animationSpeed, offsetX, offsetY);
        setNextAnimation(startAnimation, animationSpeed, offsetX, offsetY);
    }

    @Override
    public void reset() throws Exception {
        super.reset();

        destroy = true;
    }
}
