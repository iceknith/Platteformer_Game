package GameObjects;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Platform extends GameObject2D{

    public char utilType;
    final double animSpeed = 5;
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

        sprite = new Sprite(ImageIO.read(new File("assets/Platform/"+animName+"/0.png")), hitbox);
        setAnimation(getAnimationList("Platform",animName, framesCount), animSpeed);
    }

    public Platform(Platform p){
        super(p);
        utilType = p.utilType;
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

    public void collision(Entity e){

        switch (utilType){
            case 'w' -> GamePanel.camera.level.openSubLevel("win", false, true);
            case 'k' -> GameObject2D.getPlayer().death(GameObject2D.getPlayer().spawnPointPos);
        }
    }

    @Override
    public GameObject2D copy() {
        return new Platform(this);
    }
}
