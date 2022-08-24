package GameObjects;
import java.awt.*;

public class GameObject2D{

    Rectangle hitbox;
    Color color;

    public int getX() {
        return hitbox.x;
    }

    public int getY() {
        return hitbox.y;
    }

    public int getWidth() {
        return hitbox.width;
    }

    public int getHeight() {
        return hitbox.height;
    }

    public Rectangle getHitbox(){
        return hitbox;
    }

    void setX(int x){
        hitbox.x = x;
    }

    void setY(int y){
        hitbox.y = y;
    }



    public Color getColor() {
        return color;
    }

}
