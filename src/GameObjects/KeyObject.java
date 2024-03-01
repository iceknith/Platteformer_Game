package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

public class KeyObject extends Entity{

    public boolean isOnPlayer = false;
    final double distanceToPlayer = 150;
    final double turnSpeed = 0.1;
    final double angleIncrement = Math.PI*2/25;
    double angle;
    boolean isPermanent;
    boolean isUsed = false;
    int initialPosX, initialPosY;

    ArrayList<BufferedImage> keyAnimation;
    final double animSpeed = 2;


    public KeyObject(int x, int y, boolean wasSpawned, String id, String subLvl) throws IOException {
        super(x, y, 64, 64, subLvl);
        type = "Key";
        name = type + id;
        hasPhysicalCollisions = false;
        hasHP = false;
        isEntity = true;
        isPermanent = !wasSpawned;

        if (isPermanent){
            initialPosX = x;
            initialPosY = y;
        }

        keyAnimation = getAnimationList("Key", "spin", 23);
        sprite = new Sprite(keyAnimation.get(0), hitbox);
        setAnimation(keyAnimation, animSpeed);
    }

    protected KeyObject(KeyObject k) {
        super(k);

        keyAnimation = k.keyAnimation;
        isOnPlayer = k.isOnPlayer;
        angle = k.angle;
        isPermanent = k.isPermanent;
        isUsed = k.isUsed;
        initialPosX = k.initialPosX;
        initialPosY = k.initialPosY;
    }

    @Override
    public void update() throws Exception {
        if (isUsed) return;

        super.update();
        animate();

        if (isOnPlayer){
            angle += GamePanel.deltaTime*turnSpeed;

            final int nextX = (int) (getPlayer().getX() + getPlayer().getWidth()/2 - getWidth()/2 + Math.cos(angle)*distanceToPlayer);
            final int nextY = (int) (getPlayer().getY() + getPlayer().getHeight()/4 - getHeight()/2 + Math.sin(angle)*distanceToPlayer);
            prevX = getX();
            prevY = getY();

            GamePanel.camera.deleteGOInGrid(this, false);
            setX(nextX);
            setY(nextY);
            GamePanel.camera.addGOInGrid(this, false);

            for (GameObject2D go : getInBox(player.getX(), player.getY(), 150, 150)){
                if (go.type.equals("Door") && !go.getThisDoor().isOpen){
                    go.getThisDoor().setOpen();
                    getPlayer().keys.remove(this);
                    isUsed = true;
                    break;
                }
            }
        }
    }
    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {
        if (isUsed) return;
        super.draw(g2D, IO);
    }

    @Override
    public void collision(Entity e) throws Exception {
        if (isUsed) return;
        super.collision(e);

        if (e.type.equals("Player")){
            isOnPlayer = true;
            ArrayList<KeyObject> keys = GameObject2D.getPlayer().keys;
            if (keys.isEmpty()){
                angle = 0;
            }
            else {
                angle = keys.get(keys.size()-1).angle + angleIncrement;
                animationIndex = (keys.get(keys.size()-1).animationIndex + 1)%keyAnimation.size();
            }
            keys.add(this);
        }
    }

    @Override
    public void reset() throws Exception {
        if (isPermanent){
            isOnPlayer = false;
            isUsed = false;
            animationIndex = 0;

            GamePanel.camera.deleteGOInGrid(this, true);
            setX(initialPosX);
            setY(initialPosY);
            prevX = getX();
            prevY = getY();
            GamePanel.camera.addGOInGrid(this, true);
        }
        else{
            killThisEntity();
        }
    }

    @Override
    public GameObject2D copy() throws IOException {
        return new KeyObject(this);
    }
}
