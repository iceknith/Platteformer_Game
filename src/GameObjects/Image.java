package GameObjects;

import java.io.IOException;

public class Image extends GameObject2D{

    public Image(int w, int h, int x, int y, String textureName, String id, String subLvlName) throws IOException {
        super(x-w/2,y-h/2,w,h,subLvlName);

        type = "Image_" + textureName;
        name = type+id;

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(readImageBuffered("assets/Image/"+textureName+"/0.png"), hitbox);
    }
}
