package GameObjects;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CheckPoint extends GameObject2D{

    ArrayList<BufferedImage> no_flag;
    double no_flagAnimationSpeed = 1;

    ArrayList<BufferedImage> flag_appears;
    double flag_appearsAnimationSpeed = 1;

    ArrayList<BufferedImage> flag;
    double flagAnimationSpeed = 1.5;

    boolean isActivated;

    public CheckPoint(int x, int y, String id) throws IOException {
        type = "Checkpoint";
        name = type + id;

        int width = 20;
        int height = 75;

        hasPhysicalCollisions = false;
        isActivated = false;

        hitbox = new Rectangle(x, y, width, height);

        no_flag = getAnimationList("Checkpoint", "no_flag", 0);
        flag_appears = getAnimationList("Checkpoint", "flag_appears", 7);
        flag = getAnimationList("Checkpoint", "flag", 8);

        sprite = new Sprite(ImageIO.read(new File("assets/Checkpoint/no_flag/0.png")), 2.5);
        setAnimation(no_flag, no_flagAnimationSpeed);
    }

    @Override
    public void collision(Entity e){
        if (e.getType().equals("Player") && !isActivated){
            isActivated = true;
            GamePanel.camera.addActivatedCheckpoint(this);

            GamePanel.player.setSpawnPointPos(getX() - getWidth()/2, getY() - getHeight()/2);

            setAnimation(flag_appears, flag_appearsAnimationSpeed);
            setNextAnimation(flag, flagAnimationSpeed);

        }
    }

    @Override
    public void update(){
        animate();
    }
}