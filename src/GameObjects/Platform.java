package GameObjects;

import main.GamePanel;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class Platform extends GameObject2D{
    double animSpeed;
    double friction;

    public Platform(int w, int h, int x, int y, char uType, String animName, int framesCount, String id, String subLvlName) throws IOException {
        super(x,y,w,h,subLvlName);

        utilType = uType;
        type = "Platform_" + utilType + animName;
        name = type+id;

        switch (utilType){
            case 'i' -> friction = 0.5;
            default -> friction = 2.5;
        }

        switch (animName){
            case "industrial/saw" -> animSpeed = 1;
            default ->  animSpeed = 5;
        }

        BufferedImage img = readImageBuffered("assets/Platform/"+animName+"/0.png");
        sprite = new Sprite(img, hitbox);

        if (utilType == 's'){
            // Set the sprite to a fixed size
            Sprite sprite2 = new Sprite(img, 1);
            sprite = new Sprite(img, (double) sprite.getWidth() /sprite2.getWidth());

            // Change the size of the hitbox (half of the original)
            hitbox.setBounds(
                    (int) (getX()+getWidth()*0.25),
                    (int) (getY()+getHeight()*0.25),
                    (int) (getWidth()*0.5),
                    (int) (getHeight()*0.5));
        }
        setAnimation(getAnimationList("Platform",animName, framesCount), animSpeed);
    }

    public Platform(Platform p){
        super(p);
        friction = p.friction;
        animSpeed = p.animSpeed;
        setAnimation(p.currentAnimation, animSpeed);
    }

    @Override
    public double getFriction(){
        return friction;
    }

    @Override
    public void update(){
        animate();
    }

    public void collision(Entity e) throws Exception {
        if (e.type.equals("Player") && utilType == 'w'){
            GamePanel.camera.level.openSubLevel("win", false, true);
        }
        else if (utilType == 'k' || utilType == 's'){
            e.damage(25);
        }
    }

    @Override
    public GameObject2D copy() {
        return new Platform(this);
    }
}
