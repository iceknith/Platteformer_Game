package GameObjects;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GameObject2D{

    String name;
    String type;

    Rectangle hitbox;

    public boolean hasPhysicalCollisions = true;

    Sprite sprite;

    ArrayList<BufferedImage> currentAnimation;
    ArrayList<BufferedImage> nextAnimation;

    int animationIndex;
    double animationSpeed;
    double nextAnimationSpeed;
    double animateTime = 0;
    int animationPriority;

    public int getX() {
        return hitbox.x;
    }

    public int getY() {
        return hitbox.y;
    }

    public int getPreviousX(){return hitbox.x;}

    public int getPreviousY(){return hitbox.y;}

    public double getVelocityX(){return 0;}

    public double getVelocityY(){return 0;}


    public int getWidth() {
        return hitbox.width;
    }

    public int getHeight() {
        return hitbox.height;
    }

    public Rectangle getHitbox(){
        return hitbox;
    }

    public Sprite getSprite(){return sprite;}

    public ArrayList<BufferedImage> getAnimation(){return currentAnimation;}

    public ArrayList<BufferedImage> getAnimationList(String objName, String animName, int framesCount) throws IOException {
        int i = -1;
        ArrayList<BufferedImage> result = new ArrayList<>();

        while(i < framesCount){
            i++;
            File f = new File("assets/"+objName+"/"+animName+"/"+i+".png");
            result.add(ImageIO.read(f));
        }
        return result;
    }

    public String getName(){return name;}

    public String getType(){return type;}


    void setX(int x){
        hitbox.x = x;
    }

    void setY(int y){
        hitbox.y = y;
    }

    void setAnimation(ArrayList<BufferedImage> animation, double animSpeed, int animPrio){
        if (animationPriority <= animPrio) {
            currentAnimation = animation;
            animationSpeed = animSpeed;
            animationIndex = 0;
            animationPriority = animPrio;
            sprite.setImage(getAnimation().get(animationIndex));
        }
    }

    void setAnimation(ArrayList<BufferedImage> animation, double animSpeed){
        if (animationPriority <= 0) {
            currentAnimation = animation;
            animationSpeed = animSpeed;
            animationIndex = 0;
            sprite.setImage(getAnimation().get(animationIndex));
        }
    }

    void setNextAnimation(ArrayList<BufferedImage> animation, double animSpeed){
        nextAnimation = animation;
        nextAnimationSpeed = animSpeed;
    }

    void animate(){
        animateTime += GamePanel.deltaTime;

        if(animateTime >= animationSpeed){
            animateTime = 0;

            animationIndex++;

            if (animationIndex >= getAnimation().size()){
                if (nextAnimation != null){
                    currentAnimation = nextAnimation;
                    animationSpeed = nextAnimationSpeed;
                    animationPriority = 0;
                    nextAnimation = null;
                    nextAnimationSpeed = 0;
                }
                animationIndex = 0;
            }

            sprite.setImage(getAnimation().get(animationIndex));
        }

    }

    public void update()throws IOException{
        //is overwritten after
    }

    public void collision(Entity e) throws IOException {
        //is overwritten after in more specific context
    }

}
