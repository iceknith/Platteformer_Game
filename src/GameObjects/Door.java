package GameObjects;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class Door extends GameObject2D{

    ArrayList<BufferedImage> closed;
    final int closedAnimSpeed = 1000;
    ArrayList<BufferedImage> opening;
    final double openingAnimSpeed = 1;
    ArrayList<BufferedImage> opened;
    final int openedAnimSpeed = 1000;

    boolean isOpen = false;
    boolean isPlanedToOpen = false;

    Door(int x, int y, int w, int h, String id, String subLvl) throws IOException {
        super(x, y, w, h, subLvl);
        type = "Door";
        name = type + id;

        closed = getAnimationList("Door", "closed", 0);
        opening = getAnimationList("Door", "opening", 18);
        opened = getAnimationList("Door", "opened", 0);
        sprite = new Sprite(closed.get(0), hitbox);
        setAnimation(opening, openedAnimSpeed);
    }

    Door(Door d) {
        super(d);

        closed = d.closed;
        opening = d.opening;
        opened = d.opened;
        isOpen = d.isOpen;
        isPlanedToOpen = d.isPlanedToOpen;
    }

    @Override
    public void update() throws Exception {
        super.update();

        animate();

        if (hasPhysicalCollisions && isOpen && getAnimation().equals(opened)){
            hasPhysicalCollisions = false;
        }
    }

    public void setOpen(){
        if (isOpen) return;

        isOpen = true;
        setAnimation(opening, openingAnimSpeed);
        setNextAnimation(opened, openedAnimSpeed);
    }

    @Override
    public void reset() throws Exception {
        super.reset();

        isOpen = false;
        isPlanedToOpen = false;
        hasPhysicalCollisions = true;
        setAnimation(closed, closedAnimSpeed);
        nextAnimation = null;
    }

    @Override
    public GameObject2D copy() throws IOException {
        return new Door(this);
    }

    @Override
    public double getFriction() {
        return 2.5;
    }

    @Override
    public Door getThisDoor(){
        return this;
    }
}
