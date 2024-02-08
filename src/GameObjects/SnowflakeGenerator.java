package GameObjects;

import java.awt.image.BufferedImage;
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
        animate();

        // recovery process
        if (!isStable) {
            if (getAnimation().equals(stable)){
                isStable = true;
            }
            else if (getAnimation().equals(recovery) && nextAnimation == null){
                setNextAnimation(stable, stableAnimationSpeed);
            }
        }
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
        setAnimation(stable, stableAnimationSpeed);
        nextAnimation = null;
    }
}
