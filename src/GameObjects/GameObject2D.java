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

    GameObject2D(GameObject2D go){
        name = go.name;
        type = go.getType();
        subLevelName = go.subLevelName;
        hitbox = (Rectangle) go.hitbox.clone();

        hasPhysicalCollisions = go.hasPhysicalCollisions;
        isGUI = go.isGUI;

        sprite = go.getSprite().copy();

        if (go.currentAnimation != null) currentAnimation = new ArrayList<>(go.currentAnimation);
        else currentAnimation = null;
        if (go.nextAnimation != null) nextAnimation = new ArrayList<>(go.nextAnimation);
        else nextAnimation = null;

        animationIndex = go.animationIndex;
        animationSpeed = go.animationSpeed;
        nextAnimationSpeed = go.nextAnimationSpeed;
        animateTime = go.animateTime;
        animationPriority = go.animationPriority;
    }

    String name;
    String type;

    String subLevelName;

    Rectangle hitbox;

    public boolean hasPhysicalCollisions = true;
    public boolean isGUI = false;

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
        animateTime += GamePanel.deltaTime;

        if(animateTime >= animationSpeed){
            animateTime = 0;

            animationIndex++;

            if (animationIndex >= getAnimation().size()){
                if (nextAnimation != null){
                    currentAnimation = new ArrayList<>(nextAnimation);
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

    boolean pointIsIn(int x, int y){
        if (isGUI){
            return getX() <= x && x <= getX() + getWidth() &&
                    getY() <= y && y <= getY() + getHeight();
        }
        Camera camera = GamePanel.camera;
        return getX() - camera.getX() <= x && x <= getX() + getWidth() - camera.getX() &&
                getY() - camera.getY() <= y && y <= getY() + getHeight() - camera.getY();
    }

    public void draw(Graphics2D g2D, ImageObserver IO){
        if (isGUI){
            g2D.drawImage(getSprite().getImage(),
                    getSprite().getOffsetX(getHitbox()),
                    getSprite().getOffsetY(getHitbox()),
                    getSprite().getWidth(),getSprite().getHeight(), IO);
        }else{
            g2D.drawImage(getSprite().getImage(),
                    getSprite().getOffsetX(getHitbox()) - GamePanel.camera.getX() ,
                    getSprite().getOffsetY(getHitbox()) - GamePanel.camera.getY(),
                    getSprite().getWidth(), getSprite().getHeight(), IO);
        }
    }

    public void update() throws IOException, FontFormatException {
        //is overwritten after
    }

    public void collision(Entity e) {
        //is overwritten after in more specific context
    }

    public GameObject2D copy() throws IOException {
        //returns a deep copy of this GameObject2D
        //is overwritten in children class
        return new GameObject2D(this);
    }

    public String getDebugInfos(){
        return "InfoDebug " +name + ": x:" + getX() + ",y:" + getY() + ",w:" + getWidth() + ",h:" + getHeight() + ",Sprite:" + getSprite().toString();
    }

    //type specific methods
    public Button getButton() throws Exception {
        throw new Exception("Method used on a non-button GameObject");
    }

    public Player getThisPlayer(){
        return null;
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
