package GameObjects;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Platform extends GameObject2D{

    public Platform(int w, int h, int x, int y, String textureName, String id, String subLvlName) throws IOException {
        super(x,y,w,h,subLvlName);

        type = "Platform_" + textureName;
        name = type+id;

        sprite = new Sprite(ImageIO.read(new File("assets/Platform/"+textureName+"/0.png")), hitbox);
    }

    @Override
    public void update(){

    }

    public void collision(Entity e){

    }
}
