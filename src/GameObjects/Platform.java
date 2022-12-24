package GameObjects;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Platform extends GameObject2D{

    public Platform(int w, int h, int x, int y, String textureName, String id) throws IOException {
        type = "Platform_" + textureName;
        name = type+id;

        hitbox = new Rectangle(x, y, w, h);
        sprite = new Sprite(ImageIO.read(new File("assets/Platform/"+textureName+"/0.png")), hitbox);
    }

    @Override
    public void update(){

    }

    public void collision(Entity e){

    }
}
