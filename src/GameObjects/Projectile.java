package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

public class Projectile extends Entity{

    ArrayList<BufferedImage> startAnimation;
    ArrayList<BufferedImage> baseAnimation;
    double speedX, speedY, maxSpeedX, maxSpeedY, duration;
    int damage, animationSpeed;
    boolean ignoresEnemy, kill = false;
    ParticleGenerator particles;


    public Projectile(int x, int y, int w, int h, int resizeFact, int offsetX, int offsetY, int direction,
                      double speedX, double speedY, double maxSpeedX, double maxSpeedY,
                      double duration, int damage, boolean ignoresEnemy,
                      String animationName, int startAnimFrameCnt, int baseAnimFrameCnt,
                      int animationSpeed, String subLvl) throws IOException {
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
        startAnimation = getAnimationList("Projectile", animationName + "/start", startAnimFrameCnt);
        baseAnimation = getAnimationList("Projectile", animationName + "/base", baseAnimFrameCnt);
        this.animationSpeed = animationSpeed;

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
        duration -= GamePanel.deltaTime/10;
        if (duration <= 0) kill = true;
        if (kill) {
            killThisEntity();
            return;
        }

        super.update();
        animate();
        move();
    }

    @Override
    public void move() throws Exception{

        velocityX = Math.max(-maxSpeedX, Math.min(maxSpeedX, velocityX + speedX));
        velocityY = Math.max(-maxSpeedY, Math.min(maxSpeedY, velocityY + speedY));

        GamePanel.camera.deleteGOInGrid(this, false);
        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round(velocityX * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round(velocityY * GamePanel.deltaTime)));

        for (GameObject2D go: getNear()){
            if ((ignoresEnemy && go.isEntity && go.getThisEntity().isEnemy) || !go.hasPhysicalCollisions) {
                continue;
            }

            int didCollide = didCollide(go);

            if (didCollide != 0){
                if (go.isEntity) go.getThisEntity().damage(damage);

                kill = true;
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

        if (e.isEnemy) return;

        e.damage(damage);
        kill = true;
    }

    @Override
    public void reset() throws Exception {
        super.reset();

        kill = true;
    }
}
