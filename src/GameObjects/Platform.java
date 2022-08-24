package GameObjects;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class Platform extends GameObject2D{

    public static ArrayList<Platform> visiblePlatforms = new ArrayList<>();

    public Platform(String shape, int w, int h, int x, int y, Color c){
        if (Objects.equals(shape, "rectangle")){
            hitbox = new Rectangle(x, y, w, h);
            color = c;
            visiblePlatforms.add(this);
        }
    }

}
