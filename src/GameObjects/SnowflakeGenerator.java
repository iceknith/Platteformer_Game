package GameObjects;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SnowflakeGenerator extends GameObject2D{

    ArrayList<BufferedImage> stable;
    double stableAnimationSpeed;
    ArrayList<BufferedImage> recovery;
    double recoveryAnimationSpeed;
    ArrayList<BufferedImage> explosion;
    double explosionAnimationSpeed;

    int snowFlakeCount;
    final List<Integer> validSnowflakeCounts = Arrays.asList(1,2,3,5,10);
    boolean isStable = true;

    ParticleGenerator snowParticle;

    public SnowflakeGenerator(int x, int y, int snowflakeCnt, String id, String subLvlName) throws IOException {
        super(x,y,50,50, subLvlName);

        snowFlakeCount = snowflakeCnt;
        type = "SnowflakeGenerator" + snowFlakeCount;
        name = type + id;

        hasPhysicalCollisions = false;

        stableAnimationSpeed = 10;
        recoveryAnimationSpeed = 5;
        explosionAnimationSpeed = 1.5;
        loadAnimation();

        sprite = new Sprite(stable.get(0), 4);
        setAnimation(stable, stableAnimationSpeed);

        snowParticle = new ParticleGenerator(
                getX() + getWidth()/2, getY() + getHeight()/2, -1, getWidth()/2, 5,
                15, 15, 15, 15,
                -0.2, 0.2, -0.2, 0.2,
                0.9995, 1.0005, 0.02,0.02,0.05,
                100, "snow", 0, "#-1", "main");
    }

    public SnowflakeGenerator(SnowflakeGenerator s){
        super(s);

        snowFlakeCount = s.snowFlakeCount;
        isStable = s.isStable;

        stable = s.stable;
        stableAnimationSpeed = s.stableAnimationSpeed;
        recovery = s.recovery;
        recoveryAnimationSpeed = s.recoveryAnimationSpeed;
        explosion = s.explosion;
        explosionAnimationSpeed = s.explosionAnimationSpeed;

        snowParticle = s.snowParticle;
    }

    public void setSnowFlakeCount(int snowflakeCnt) throws IOException {
        if (validSnowflakeCounts.contains(snowflakeCnt)){
            snowFlakeCount = snowflakeCnt;
            loadAnimation();
        }
    }

    public void loadAnimation() throws IOException {
        stable = getAnimationList("SnowflakeGenerator","stable"+snowFlakeCount, 0);
        recovery = getAnimationList("SnowflakeGenerator","recovering"+snowFlakeCount, 9);
        explosion = getAnimationList("SnowflakeGenerator","explosion"+snowFlakeCount, 3);
    }

    @Override
    public void update() throws Exception {
        super.update();
        snowParticle.update();

        animate();

        if (isStable){
            for(GameObject2D go: getNear()){
                if (intersects(go) && go.isEntity){
                    collision(go.getThisEntity());
                }
            }
        }
        // recovery process
        else {
            if (getAnimation().equals(stable)){
                isStable = true;
                snowParticle.isInactive = false;
            }
            else if (getAnimation().equals(recovery) && nextAnimation == null){
                setNextAnimation(stable, stableAnimationSpeed);
            }
        }

    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO){
        snowParticle.draw(g2D, IO);
        super.draw(g2D, IO);
    }

    @Override
    public void collision(Entity e) throws Exception {
        super.collision(e);

        //use the generator
        if (e.type.equals("Player")){
            Player p = e.getThisPlayer();

            if (isStable && p.snowflakeCount < snowFlakeCount) {
                p.snowflakeCount = snowFlakeCount;

                isStable = false;
                snowParticle.isInactive = true;
                setAnimation(explosion, explosionAnimationSpeed);
                setNextAnimation(recovery, recoveryAnimationSpeed);
            }
        }
    }

    @Override
    public GameObject2D copy() throws IOException {
        return new SnowflakeGenerator(this);
    }

    @Override
    public SnowflakeGenerator getThisSnowflakeGenerator(){
        return this;
    }

    @Override
    public void reset() {
        isStable = true;
        snowParticle.isInactive = false;
        setAnimation(stable, stableAnimationSpeed);
        nextAnimation = null;
    }
}
