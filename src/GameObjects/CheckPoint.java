package GameObjects;


import javax.imageio.ImageIO;
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

    public CheckPoint(int x, int y, String id, String subLvlName) throws IOException {
        super(x,y,20,75, subLvlName);

        type = "Checkpoint";
        name = type + id;

        hasPhysicalCollisions = false;
        isActivated = false;

        no_flag = getAnimationList("Checkpoint", "no_flag", 0);
        flag_appears = getAnimationList("Checkpoint", "flag_appears", 7);
        flag = getAnimationList("Checkpoint", "flag", 8);

        sprite = new Sprite(ImageIO.read(new File("assets/Checkpoint/no_flag/0.png")), 2.5);
        setAnimation(no_flag, no_flagAnimationSpeed);
    }

    CheckPoint(CheckPoint c){
        super(c);
        if (c.no_flag.isEmpty()) no_flag = null;
        else no_flag = new ArrayList<>(c.no_flag);
        no_flagAnimationSpeed = c.no_flagAnimationSpeed;

        if (c.no_flag.isEmpty()) flag_appears = null;
        else flag_appears = new ArrayList<>(c.flag_appears);
        flag_appearsAnimationSpeed = c.flag_appearsAnimationSpeed;

        if (c.flag.isEmpty()) flag = null;
        else flag = new ArrayList<>(c.flag);
        flagAnimationSpeed = c.flagAnimationSpeed;

        isActivated = c.isActivated;
    }

    @Override
    public void collision(Entity e){
        if (e.getType().equals("Player") && !isActivated){
            isActivated = true;

            GameObject2D.getPlayer().setSpawnPointPos(getX() - getWidth()/2, getY() - getHeight()/2);

            setAnimation(flag_appears, flag_appearsAnimationSpeed);
            setNextAnimation(flag, flagAnimationSpeed);

        }
    }

    @Override
    public void update(){
        animate();
    }

    @Override
    public CheckPoint copy() {
        return new CheckPoint(this);
    }
}