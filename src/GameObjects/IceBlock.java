package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
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
    public boolean isPlaced, isDead;

    int playerCenterX, playerCenterY, originalPosX, originalPosY;

    final int damageAmount = 25;
    final int explosionRange = 100;

    double friction;

    GameObject2D targetedBlock = null;
    int[] tempBlock = new int[] {};
    int[] centerCoos = new int[] {};

    IceBlock(int x, int y, String subLvl, int id) throws IOException {
        super(x, y, 64, 64, subLvl);

        type = "IceBlock";
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
        isDead = i.isDead;
        friction = i.friction;
    }


    @Override
    public void update() throws Exception {
        if (isDead) {
            super.killThisEntity();
            return;
        }

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
                return;
            }

            //break handler
            hp -= 2*GamePanel.deltaTime; //normally dies in 5s

            if (hp < 16.67) setAnimation(breaking4, breakingAnimationSpeed);
            else if (hp < 33.34) setAnimation(breaking3, breakingAnimationSpeed);
            else if (hp < 50.01) setAnimation(breaking2, breakingAnimationSpeed);
            else if (hp < 66.68) setAnimation(breaking1, breakingAnimationSpeed);
            else if (hp < 83.35) setAnimation(breaking0, breakingAnimationSpeed);

            //check if collision
            for (GameObject2D go : getInBox(getWidth() + 10, getHeight() + 10)){
                if (go.hasPhysicalCollisions && go.isEntity && go.getThisEntity().hasHP
                        && !go.getType().equals("IceBlock")
                        && !go.getType().equals("Player")){
                    killThisEntity();
                }
            }
        }

        //placing handler
        if (!isPlaced){

            canBePlaced = true;
            boolean modif = true;
            targetedBlock = null;
            centerCoos = new int[] {};
            tempBlock = new int[] {};
            while (modif){
                modif = false;

                for (GameObject2D go: getNear()){
                    if (go.hasPhysicalCollisions && intersects(go) && !go.getType().equals("Player")){
                        modif = placingCollisionHandler(go);
                        if (modif) break;
                    }
                }
            }

            if (!canBePlaced) move(originalPosX, originalPosY, false);

            // placing display handler
            if (canBePlaced) setAnimation(placingValid, placingAnimationSpeed);
            else setAnimation(placingInvalid, placingAnimationSpeed);
        }
    }

    public void move(int nextX, int nextY, boolean isOriginal) throws Exception {
        GamePanel.camera.deleteGOInGrid(this, false);
        prevX = getX();
        prevY = getY();
        if (isOriginal){
            originalPosX = nextX;
            originalPosY = nextY;
        }
        setX(nextX);
        setY(nextY);
        GamePanel.camera.addGOInGrid(this, false);
    }

    public void setPlayerCenter(int playerCX, int playerCY){
        playerCenterX = playerCX;
        playerCenterY = playerCY;
    }

    boolean placingCollisionHandler(GameObject2D go) throws Exception {
        //returns true if a modification occurred, else returns false

        final int centerX = getX() + getWidth()/2;
        final int centerY = getY() + getHeight()/2;

        int[] intersectionPoint;
        int[] tempNewPos;
        int[] nextPos = null;
        double minDist = getDistance(centerX, centerY, playerCenterX, playerCenterY);
        double distance;
        Entity temp = new Entity(0, 0, getWidth() + 4, getHeight() + 4, "TEMP");


        //right
        intersectionPoint = lineIntersectsVerticalLine(
                playerCenterX, playerCenterY, centerX, centerY,
                go.getX(), go.getY(), go.getY() + go.getHeight(), true);

        if (intersectionPoint.length == 2){
            tempNewPos = lineIntersectsRect(intersectionPoint[0], intersectionPoint[1], getWidth() + 2, getHeight() + 2,
                                        playerCenterX, playerCenterY, centerX, centerY);

            if (tempNewPos.length == 2){
                distance = getDistance(
                        tempNewPos[0] - getWidth()/2 + getWidth()/2,
                        tempNewPos[1] - getHeight()/2 + getHeight()/2,
                        playerCenterX, playerCenterY);
                temp.setX(tempNewPos[0] - getWidth()/2 - 2);
                temp.setY(tempNewPos[1] - getHeight()/2 - 2);

                if (distance < minDist && temp.intersects(go)){
                    nextPos = new int[] {tempNewPos[0] - getWidth()/2, tempNewPos[1] - getHeight()/2};
                    minDist = distance;

                    tempBlock = new int[] {intersectionPoint[0] - getWidth()/2 - 1, intersectionPoint[1] - getHeight()/2 - 1, getWidth() + 2, getHeight() + 2};
                }
            }
        }

        //left
        intersectionPoint = lineIntersectsVerticalLine(
                playerCenterX, playerCenterY, centerX, centerY,
                go.getX() + go.getWidth(), go.getY(), go.getY() + go.getHeight(), true);

        if (intersectionPoint.length == 2){
            tempNewPos = lineIntersectsRect(intersectionPoint[0], intersectionPoint[1], getWidth() + 2, getHeight() + 2,
                    playerCenterX, playerCenterY, centerX, centerY);

            if (tempNewPos.length == 2){
                distance = getDistance(
                        tempNewPos[0] - getWidth()/2 + getWidth()/2,
                        tempNewPos[1] - getHeight()/2 + getHeight()/2,
                        playerCenterX, playerCenterY);
                temp.setX(tempNewPos[0] - getWidth()/2 - 2);
                temp.setY(tempNewPos[1] - getHeight()/2 - 2);

                if (distance < minDist && temp.intersects(go)){
                    nextPos = new int[] {tempNewPos[0] - getWidth()/2, tempNewPos[1] - getHeight()/2};
                    minDist = distance;

                    tempBlock = new int[] {intersectionPoint[0] - getWidth()/2 - 1, intersectionPoint[1] - getHeight()/2 - 1, getWidth() + 2, getHeight() + 2};
                }
            }
        }

        //down
        intersectionPoint = lineIntersectsHorizontalLine(
                playerCenterX, playerCenterY, centerX, centerY,
                go.getX(), go.getX() + go.getWidth(), go.getY(), true);

        if (intersectionPoint.length == 2){
            tempNewPos = lineIntersectsRect(intersectionPoint[0], intersectionPoint[1], getWidth() + 2, getHeight() + 2,
                    playerCenterX, playerCenterY, centerX, centerY);

            if (tempNewPos.length == 2){
                distance = getDistance(tempNewPos[0], tempNewPos[1], playerCenterX, playerCenterY);
                temp.setX(tempNewPos[0] - getWidth()/2 - 2);
                temp.setY(tempNewPos[1] - getHeight()/2 - 2);


                if (distance < minDist && temp.intersects(go)){
                    nextPos = new int[] {tempNewPos[0] - getWidth()/2, tempNewPos[1] - getHeight()/2};

                    tempBlock = new int[] {intersectionPoint[0] - getWidth()/2 - 1, intersectionPoint[1] - getHeight()/2 - 1, getWidth() + 2, getHeight() + 2};
                }
            }
        }

        //up
        intersectionPoint = lineIntersectsHorizontalLine(
                playerCenterX, playerCenterY, centerX, centerY,
                go.getX(), go.getX() + go.getWidth(), go.getY() + go.getHeight(), true);

        if (intersectionPoint.length == 2){
            tempNewPos = lineIntersectsRect(intersectionPoint[0], intersectionPoint[1], getWidth() + 2, getHeight() + 2,
                    playerCenterX, playerCenterY, centerX, centerY);

            if (tempNewPos.length == 2){
                distance = getDistance(tempNewPos[0], tempNewPos[1], playerCenterX, playerCenterY);
                temp.setX(tempNewPos[0] - getWidth()/2 - 2);
                temp.setY(tempNewPos[1] - getHeight()/2 - 2);


                if (distance < minDist && temp.intersects(go)){
                    nextPos = new int[] {tempNewPos[0] - getWidth()/2, tempNewPos[1] - getHeight()/2};

                    tempBlock = new int[] {intersectionPoint[0] - getWidth()/2 - 1, intersectionPoint[1] - getHeight()/2 - 1, getWidth() + 2, getHeight() + 2};
                }
            }
        }

        //computing the results
        if (nextPos == null) {
            canBePlaced = false;
            return false;
        }
        else {
            move(nextPos[0], nextPos[1], false);
            canBePlaced = (!intersects(go) && !intersects(player));
            targetedBlock = go;
            centerCoos = new int[] {centerX, centerY};
            return true;
        }
    }

    int[] lineIntersectsRect(int rCX, int rCY, int w, int h, int lX0, int lY0, int lX1, int lY1){
        int[] tempPos;
        int[] bestPos = new int[] {};
        double tempDistance;
        double bestDistance = getDistance(lX0, lY0, lX1, lY1);

        //right
        tempPos = lineIntersectsVerticalLine(lX0, lY0, lX1, lY1, rCX - w/2, rCY - h/2, rCY + h/2, false);
        if (tempPos.length == 2 ){
            tempDistance = getDistance(lX0, lY0, tempPos[0], tempPos[1]);
            if (tempDistance < bestDistance){
                bestDistance = tempDistance;
                bestPos = tempPos.clone();
            }
        }

        //left
        tempPos = lineIntersectsVerticalLine(lX0, lY0, lX1, lY1, rCX + w/2, rCY - h/2, rCY + h/2, false);
        if (tempPos.length == 2 ){
            tempDistance = getDistance(lX0, lY0, tempPos[0], tempPos[1]);
            if (tempDistance < bestDistance){
                bestDistance = tempDistance;
                bestPos = tempPos.clone();
            }
        }

        //down
        tempPos = lineIntersectsHorizontalLine(lX0, lY0, lX1, lY1, rCX - w/2, rCX + w/2, rCY - h/2, false);
        if (tempPos.length == 2 ){
            tempDistance = getDistance(lX0, lY0, tempPos[0], tempPos[1]);
            if (tempDistance < bestDistance){
                bestDistance = tempDistance;
                bestPos = tempPos.clone();
            }
        }

        //down
        tempPos = lineIntersectsHorizontalLine(lX0, lY0, lX1, lY1, rCX - w/2, rCX + w/2, rCY + h/2, false);
        if (tempPos.length == 2 ){
            tempDistance = getDistance(lX0, lY0, tempPos[0], tempPos[1]);
            if (tempDistance < bestDistance){
                bestDistance = tempDistance;
                bestPos = tempPos.clone();
            }
        }

        return bestPos;
    }

    int[] lineIntersectsVerticalLine(int lX0, int lY0, int lX1, int lY1, int vX, int vY0, int vY1, boolean infiniteLine){
        //checks for intersection between a random line [l], and a vertical one [v]
        //returns {} if no intersection, and the intersection point if there is

        //Check if l is vertical
        if (lX0 == lX1) return new int[] {};

        final int iY = ((lY0 - lY1)*(lX0 - vX))/(lX1 - lX0) + lY0;

        if (infiniteLine ||
                (Math.min(lY0, lY1) <= iY && iY <= Math.max(lY0, lY1)
                && Math.min(vY0, vY1) <= iY && iY <= Math.max(vY0, vY1)))
            return new int[] {vX, iY};

        return new int[] {};
    }

    int[] lineIntersectsHorizontalLine(int lX0, int lY0, int lX1, int lY1, int hX0, int hX1, int hY, boolean infiniteLine) {
        //checks for intersection between a random line [l], and a horizontal one [h]
        //returns {} if no intersection, and the intersection point if there is

        //Check if l is horizontal
        if (lY0 == lY1) return new int[]{};

        final int iX = ((lX1 - lX0)*(lY0 - hY))/(lY0 - lY1) + lX0;

        if (infiniteLine ||
                (Math.min(lX0, lX1) <= iX && iX <= Math.max(lX0, lX1)
                && Math.min(hX0, hX1) <= iX && iX <= Math.max(hX0, hX1)))
            return new int[]{iX, hY};

        return new int[]{};
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
            hp  = -1;
            setAnimation(broken, brokenAnimationSpeed);
            setNextAnimation(end, 1);

            for (GameObject2D go : getInBox(explosionRange, explosionRange)){
                if (go.isEntity && !go.getType().equals("Player")){
                    go.getThisEntity().damage(damageAmount);
                }
            }
        }
    }

    @Override
    public void reset() throws Exception {
        isDead = true;
        if (player.currentIceBlock != null && player.currentIceBlock.equals(this)) player.currentIceBlock = null;
        super.killThisEntity();
    }

    @Override
    public GameObject2D copy(){
        return new IceBlock(this);
    }
}
