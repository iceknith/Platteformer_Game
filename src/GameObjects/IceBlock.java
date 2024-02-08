package GameObjects;

import main.GamePanel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class IceBlock extends Entity{

    ArrayList<BufferedImage> placingInvalid;
    ArrayList<BufferedImage> placingValid;
    double placingAnimationSpeed;

    ArrayList<BufferedImage> stable;
    double stableAnimationSpeed;

    ArrayList<BufferedImage> breaking0;
    ArrayList<BufferedImage> breaking1;
    ArrayList<BufferedImage> breaking2;
    ArrayList<BufferedImage> breaking3;
    ArrayList<BufferedImage> breaking4;
    double breakingAnimationSpeed;

    ArrayList<BufferedImage> broken;
    double brokenAnimationSpeed;
    ArrayList<BufferedImage> end;


    boolean tempCanBePlaced;
    public boolean canBePlaced;
    public boolean isPlaced;

    final int damageAmount = 25;

    double friction;

    IceBlock(int x, int y, String subLvl, int id) throws IOException {
        super(x, y, 64, 64, subLvl);

        type = "IceBlock ";
        name = type + id;

        isPlaced = false;
        hasPhysicalCollisions = false;
        hp = 100;
        friction = 0.5;

        placingValid = getAnimationList("IceBlock","preview_valid",0);
        placingInvalid = getAnimationList("IceBlock","preview_invalid",0);
        placingAnimationSpeed = 1;
        stable = getAnimationList("IceBlock","stable",0);
        stableAnimationSpeed = 1;

        breaking0 = getAnimationList("IceBlock","breaking0",0);
        breaking1 = getAnimationList("IceBlock","breaking1",0);
        breaking2 = getAnimationList("IceBlock","breaking2",0);
        breaking3 = getAnimationList("IceBlock","breaking3",0);
        breaking4 = getAnimationList("IceBlock","breaking4",0);
        breakingAnimationSpeed = 1;

        broken = getAnimationList("IceBlock","broken",4);
        brokenAnimationSpeed = 0.75;
        end = new ArrayList<>(Collections.singletonList(readImageBuffered("assets/placeholder.png")));

        sprite = new Sprite(placingInvalid.get(0), this.getHitbox());
        setAnimation(placingInvalid, placingAnimationSpeed);
    }

    IceBlock(IceBlock i) {
        super(i);

        placingInvalid = i.placingInvalid;
        placingValid = i.placingValid;
        placingAnimationSpeed = i.placingAnimationSpeed;
        stable = i.stable;
        stableAnimationSpeed = i.stableAnimationSpeed;
        breaking0 = i.breaking0;
        breaking1 = i.breaking1;
        breaking2 = i.breaking2;
        breaking3 = i.breaking3;
        breaking4 = i.breaking4;
        breakingAnimationSpeed = i.breakingAnimationSpeed;
        broken = i.broken;
        brokenAnimationSpeed = i.brokenAnimationSpeed;
        end = i.end;
        tempCanBePlaced = i.tempCanBePlaced;
        canBePlaced = i.canBePlaced;
        isPlaced = i.isPlaced;
        friction = i.friction;
    }


    @Override
    public void update() throws Exception {
        super.update();
        animate();

        if (hp < 0) return;

        //place block
        if (isPlaced){
            if (getAnimation().equals(placingValid)){
                setAnimation(stable, stableAnimationSpeed);
                hasPhysicalCollisions = true;
                hasHP = true;

                velocityX = 0;
                velocityY = 0;
                prevX = getX();
                prevY = getY();
            }
            else if (getAnimation().equals(placingInvalid)){

                super.killThisEntity();
            }

            //break handler
            hp -= 2*GamePanel.deltaTime; //normally dies in 5s

            if (hp < 16.67) setAnimation(breaking4, breakingAnimationSpeed);
            else if (hp < 33.34) setAnimation(breaking3, breakingAnimationSpeed);
            else if (hp < 50.01) setAnimation(breaking2, breakingAnimationSpeed);
            else if (hp < 66.68) setAnimation(breaking1, breakingAnimationSpeed);
            else if (hp < 83.35) setAnimation(breaking0, breakingAnimationSpeed);
        }

        //not placed handler
        if (!isPlaced){

            for (GameObject2D go: getNear()){
                if (go.hasPhysicalCollisions && intersects(go)){
                    tempCanBePlaced = false;
                    break;
                }
            }

            // placing display handler
            canBePlaced = tempCanBePlaced;
            tempCanBePlaced = true;

            if (canBePlaced) setAnimation(placingValid, placingAnimationSpeed);
            else setAnimation(placingInvalid, placingAnimationSpeed);
        }
    }

    public void move(int nextX, int nextY){
        GamePanel.camera.deleteGOInGrid(this, false);
        prevX = getX();
        prevY = getY();
        setX(nextX);
        setY(nextY);
        GamePanel.camera.addGOInGrid(this, false);
    }

    @Override
    public void collision(Entity e) throws Exception {
        super.collision(e);

        if (!isPlaced && e.hasPhysicalCollisions){
            tempCanBePlaced = false;
        }
    }

    @Override
    public double getFriction() {
        return friction;
    }

    @Override
    public void killThisEntity() throws Exception {
        if (getAnimation().equals(end)){
            super.killThisEntity();
        }
        else if (!getAnimation().equals(broken)){
            hasPhysicalCollisions = false;
            setAnimation(broken, brokenAnimationSpeed);
            setNextAnimation(end, 1);

            for (GameObject2D go : getInBox(100, 100)){
                if (go.isEntity && getDistance(go) <= 100 && !go.getType().equals("Player")){
                    go.getThisEntity().damage(damageAmount);
                }
            }
        }
    }

    @Override
    public void reset() throws Exception {
        super.killThisEntity();
    }

    @Override
    public GameObject2D copy(){
        return new IceBlock(this);
    }
}
