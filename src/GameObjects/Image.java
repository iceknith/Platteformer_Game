package GameObjects;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Image extends GameObject2D{

    public Image(int w, int h, int x, int y, String textureName, String id, String subLvlName) throws IOException {
        super(x,y,w,h,subLvlName);

        type = "Image_" + textureName;
        name = type+id;

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(ImageIO.read(new File("assets/Image/"+textureName+"/0.png")), hitbox);
    }
}
