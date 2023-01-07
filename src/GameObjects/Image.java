package GameObjects;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Image extends GameObject2D{

    public Image(int w, int h, int x, int y, String textureName, String id) throws IOException {
        type = "Image_" + textureName;
        name = type+id;

        hasPhysicalCollisions = false;
        isGUI = true;

        hitbox = new Rectangle(x, y, w, h);

        sprite = new Sprite(ImageIO.read(new File("assets/Image/"+textureName+"/0.png")), hitbox);
    }
}
