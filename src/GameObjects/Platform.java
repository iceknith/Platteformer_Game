package GameObjects;

import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class Platform extends GameObject2D{

    public static ArrayList<Platform> visiblePlatforms = new ArrayList<>();

    public Platform(int w, int h, int x, int y, Color c){
        hitbox = new Rectangle(x, y, w, h);
        color = c;
    }
}
