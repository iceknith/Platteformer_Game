package GameObjects;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GameObject2D{

    GameObject2D(int x, int y, int w, int h, String subLvl){
        hitbox = new Rectangle(x,y,w,h);
        subLevelName = subLvl;
    }

    String name;
    String type;

    String subLevelName;

    Rectangle hitbox;

    public boolean hasPhysicalCollisions = true;
    public boolean isGUI = false;

    Sprite sprite;

    ArrayList<BufferedImage> currentAnimation = new ArrayList<>();
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
        sprite.setX(x, hitbox);
    }

    void setY(int y){
        hitbox.y = y;
        sprite.setY(y, hitbox);
    }

    void setPhysicalX(int x){hitbox.x = x;}

    void setPhysicalY(int y){hitbox.y = y;}

    void setVisualX(int x) {sprite.setX(x,hitbox);}

    void setVisualY(int y) {sprite.setY(y,hitbox);}

    void setWidth(int w){hitbox.width = w;}

    void setHeight(int h){hitbox.height = h;}

    public void setSubLevelName(String name){subLevelName = name;}

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
        animateTime += GamePanel.getDeltaTime();

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

    public void draw(Graphics2D g2D, ImageObserver IO){
        if (isGUI){
            g2D.drawImage(getSprite().getImage(),
                    getSprite().getX(), getSprite().getY(),
                    getSprite().getWidth(),getSprite().getHeight(), IO);
        }else{
            g2D.drawImage(getSprite().getImage(),
                    getSprite().getX() - GamePanel.camera.getX() ,
                    getSprite().getY() - GamePanel.camera.getY(),
                    getSprite().getWidth(), getSprite().getHeight(), IO);
        }
    }

    public void update() throws IOException, FontFormatException {
        //is overwritten after
    }

    public void graphicalUpdate(){
        if (!currentAnimation.isEmpty()){
            animate();
        }
    }

    public void collision(Entity e) throws IOException {
        //is overwritten after in more specific context
    }

    public String getDebugInfos(){
        return "InfoDebug " +name + ": x:" + getX() + ",y:" + getY() + ",w:" + getWidth() + ",h:" + getHeight() + ",Sprite:" + getSprite().toString();
    }

    //static methods and variables
    static Player player;

    public static boolean hasNoPlayer(){
        return player == null;
    }

    public static Player getPlayer(){
        return player;
    }

    public static void setPlayer(Player p){
        player = p;
    }
}
