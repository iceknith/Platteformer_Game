package GameObjects;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GameObject2D{

    static ArrayList<GameObject2D> visible = new ArrayList<>();

    Rectangle hitbox;
    Color color;

    BufferedImage sprite;

    ArrayList<BufferedImage> currentAnimation;
    double animateTime = 0;

    public static ArrayList<GameObject2D> getVisible(){
        return visible;
    }

    public static void resetVisible(){
        visible = new ArrayList<>();
    }

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

    public Color getColor() {
        return color;
    }

    public Rectangle getHitbox(){
        return hitbox;
    }

    public BufferedImage getSprite(){return sprite;}

    public ArrayList<BufferedImage> getAnimation(){return currentAnimation;}

    public ArrayList<BufferedImage> getAnimationList(String objName, String animName, int framesCount) throws IOException {
        int i = 0;
        ArrayList<BufferedImage> result = new ArrayList<>();

        while(i < framesCount){
            File f = new File("assets/"+objName+"/"+animName+"/"+i+".png");
            result.add(ImageIO.read(f));
            i++;
        }
        return result;
    }



    void setX(int x){
        hitbox.x = x;
    }

    void setY(int y){
        hitbox.y = y;
    }

    void setSprite(BufferedImage newSprite){sprite = newSprite;}

    void setAnimation(ArrayList<BufferedImage> animation){currentAnimation = animation;}

    void animate(){
        animateTime += GamePanel.deltaTime;

        if(animateTime >= 1){
            animateTime = 0;

            int spriteNum = getAnimation().indexOf(getSprite());
            spriteNum++;

            if (spriteNum >= getAnimation().size()){
                spriteNum = 0;
            }

            setSprite(getAnimation().get(spriteNum));
        }

    }

}
