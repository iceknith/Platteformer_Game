package GameObjects;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Platform extends GameObject2D{

    public static ArrayList<Platform> visiblePlatforms = new ArrayList<>();

    public Platform(int w, int h, int x, int y, String textureName, String id) throws IOException {
        type = "Platform_" + textureName;
        name = type+id;

        hitbox = new Rectangle(x, y, w, h);
        sprite = new Sprite(ImageIO.read(new File("assets/Platform/"+textureName+"/0.png")), hitbox);
    }

    @Override
    public void update(){
        if (GamePanel.camera.hasActivatedCheckpoints() && name.equals("Platform_win#32")){
            setY(-1650);
        }
    }

    public void collision(Entity e) throws IOException {

    }
}
