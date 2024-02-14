package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

public class ParticleGenerator extends Entity{

    ArrayList<BufferedImage> animation;
    ArrayList<Particle> particles = new ArrayList<>();
    int particleGenerationNum, spawnRadius;
    double spawnTime, animSpeed, particleMaxLifeTime, timeSinceGeneration = 0,
            defaultMinWidth, defaultMinHeight, defaultMaxWidth, defaultMaxHeight,
            minXVel, maxXVel, minYVel, maxYVel, xAcceleration, yAcceleration, shrinkXSpeed, shrinkYSpeed;
    boolean isInactive;

    public ParticleGenerator(int x, int y, int particleGenNum, int particleSpawnRadius, double particlesLifeTime,
                             double particleMinWidth, double particleMaxWidth,
                             double particleMinHeight, double particleMaxHeight,
                             double particleMinXVelocity, double particleMaxXVelocity,
                             double particleMinYVelocity, double particleMaxYVelocity,
                             double particleXAcceleration, double particleYAcceleration,
                             double particleShrinkXSpeed, double particleShrinkYSpeed,
                             double spawningTime, double animationSpeed,
                             String animName, int frameCount, String id, String subLvl) throws IOException {
        super(x, y, 0, 0, subLvl);
        type = "ParticleGenerator";
        name = type + id;

        particleGenerationNum = particleGenNum;
        spawnRadius = particleSpawnRadius;
        particleMaxLifeTime = particlesLifeTime;
        minXVel = particleMinXVelocity;
        minYVel = particleMinYVelocity;
        maxXVel = particleMaxXVelocity;
        maxYVel = particleMaxYVelocity;
        xAcceleration = particleXAcceleration;
        yAcceleration = particleYAcceleration;
        spawnTime = spawningTime;
        animSpeed = animationSpeed;
        defaultMinWidth = particleMinWidth;
        defaultMaxWidth = particleMaxWidth;
        defaultMinHeight = particleMinHeight;
        defaultMaxHeight = particleMaxHeight;
        shrinkXSpeed = particleShrinkXSpeed;
        shrinkYSpeed = particleShrinkYSpeed;

        hasPhysicalCollisions = false;
        hasHP = false;

        animation = getAnimationList("Particles", animName, frameCount);
        sprite = new Sprite(readImageBuffered("assets/placeholder.png"), getHitbox());
    }

    @Override
    public void update() throws Exception {
        super.update();

        //generate particles
        if (!isInactive){
            timeSinceGeneration += GamePanel.deltaTime / 10;
            if (timeSinceGeneration > spawnTime){

                final double angle = Math.random()*2*Math.PI;
                final int x = getX() + (int) (Math.cos(angle)*spawnRadius);
                final int y = getY() + (int) (Math.sin(angle)*spawnRadius);
                final int width =(int) (Math.random()*(defaultMaxWidth - defaultMinWidth) + defaultMinWidth);
                final int height =(int) (Math.random()*(defaultMaxHeight - defaultMinHeight) + defaultMinHeight);
                final int velX = (int) (Math.random()*(maxXVel - minXVel) + minXVel);
                final int velY = (int) (Math.random()*(maxYVel - minYVel) + minYVel);
                particles.add(new Particle(x, y, width, height, velX, velY, 0, 0));
                particleGenerationNum--;
                timeSinceGeneration = 0;
            }
            if (particleGenerationNum == 0){
                killThisEntity();
            }
        }


        //update particles
        for (int i = 0; i < particles.size(); i++){
            Particle p = particles.get(i);
            p.update(animSpeed, animation.size(), xAcceleration, yAcceleration, shrinkXSpeed, shrinkYSpeed);
            if (p.lifeTime > particleMaxLifeTime){
                particles.remove(i);
                i -= 1;
            }
        }
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {

        for (Particle p : particles) {
            g2D.drawImage(
                    animation.get(p.currentFrame),
                    (int) p.x - GamePanel.camera.getScreenX(),
                    (int) p.y - GamePanel.camera.getScreenY(),
                    (int) p.width, (int) p.height, IO);
        }
    }

    public void move(int newX, int newY) throws Exception {
        if (isInactive) return;

        setX(newX); setY(newY);
    }

    @Override
    public void killThisEntity() throws Exception {
        isInactive = true;
        super.killThisEntity();
    }
}


class Particle {

    public int currentFrame;
    public double animateTime, lifeTime, x, y, width, height, velocityX, velocityY;

    public Particle(int posX, int posY, int w, int h){
        this(posX, posY, w, h, 0, 0, 0, 0);
    }

    public Particle(double posX, double posY, double w, double h, double velX, double velY, int firstFrame, double initialLifeTime){
        x = posX;
        y = posY;
        width = w;
        height = h;
        velocityX = velX;
        velocityY = velY;
        currentFrame = firstFrame;
        lifeTime = initialLifeTime;
    }

    void update(double animSpeed, int maxFrame,
                double accelerationX, double accelerationY,
                double shrinkXSpeed, double shrinkYSpeed){
        lifeTime += GamePanel.deltaTime / 10;
        velocityX *= accelerationX;
        velocityY *= accelerationY;
        x += velocityX;
        y += velocityY;
        width = Math.max(0, width - shrinkXSpeed);
        height = Math.max(0, height - shrinkYSpeed);
        animate(animSpeed, maxFrame);
    }

    void animate(double animSpeed, int maxFrame){
        animateTime += GamePanel.deltaTime / 10;
        if (animateTime > animSpeed){
            currentFrame = (currentFrame + 1)%maxFrame;
            animateTime = 0;
        }
    }
}