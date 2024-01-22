package GameObjects;

import main.GamePanel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

        isPlaced = i.isPlaced;
    }


    @Override
    public GameObject2D copy(){
        return new IceBlock(this);
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

                killThisEntity();
            }

            //break handler
            hp -= 2*GamePanel.deltaTime; //normally dies in 5s

            if (hp < 17) setAnimation(breaking4, breakingAnimationSpeed);
            else if (hp < 34) setAnimation(breaking3, breakingAnimationSpeed);
            else if (hp < 51) setAnimation(breaking2, breakingAnimationSpeed);
            else if (hp < 68) setAnimation(breaking1, breakingAnimationSpeed);
            else if (hp < 85) setAnimation(breaking0, breakingAnimationSpeed);
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
    public void killThisEntity() {
        if (getAnimation().equals(end)){
            super.killThisEntity();
        }
        else if (!getAnimation().equals(broken)){
            hasPhysicalCollisions = false;
            setAnimation(broken, brokenAnimationSpeed);
            setNextAnimation(end, 1);
        }
    }
}
