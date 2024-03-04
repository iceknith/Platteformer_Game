package GameObjects;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameObject2D{

    public String name;
    public String type;

    public String subLevelName;

    public Rectangle hitbox;

    public boolean hasPhysicalCollisions = true;
    public boolean isGUI = false;

    public Sprite sprite;

    ArrayList<BufferedImage> currentAnimation;
    ArrayList<BufferedImage> nextAnimation;

    int animationIndex;
    protected double animationSpeed;
    double nextAnimationSpeed;
    int nextAnimationOffsetX;
    int nextAnimationOffsetY;
    double animateTime = 0;
    int animationPriority;

    public char utilType;
    public boolean doesDamage = false;
    public boolean isEntity = false;

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
        nextAnimationOffsetX = go.nextAnimationOffsetX;
        nextAnimationOffsetY = go.nextAnimationOffsetY;
        animateTime = go.animateTime;
        animationPriority = go.animationPriority;

        utilType = go.utilType;
        isEntity = go.isEntity;
        doesDamage = go.doesDamage;
    }

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
    public int getAnimationIndex(){return animationIndex;}


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
            result.add(readImageBuffered("assets/"+objName+"/"+animName+"/"+i+".png"));
        }
        return result;
    }

    public String getName(){return name;}

    public String getType(){return type;}

    protected void setX(int x){
        hitbox.x = x;
    }

    protected void setY(int y){
        hitbox.y = y;
    }

    void setWidth(int w){hitbox.width = w;}

    void setHeight(int h){hitbox.height = h;}

    public void setSubLevelName(String name){subLevelName = name;}

    public void setAnimation(ArrayList<BufferedImage> animation, double animSpeed, int animPrio,  int offsetX, int offsetY){
        if (animationPriority <= animPrio) {
            currentAnimation = animation;
            animationSpeed = animSpeed;
            animationIndex = 0;
            animationPriority = animPrio;
            if (sprite.resizeFactor == 0) sprite.setImage(currentAnimation.get(animationIndex), hitbox);
            else sprite.setImage(currentAnimation.get(animationIndex));

            sprite.offsetX = offsetX;
            sprite.offsetY = offsetY;
        }
    }

    public void setAnimation(ArrayList<BufferedImage> animation, double animSpeed, int offsetX, int offsetY){
        setAnimation(animation, animSpeed, 0, offsetX, offsetY);
    }

    public void setAnimation(ArrayList<BufferedImage> animation, double animSpeed){
        setAnimation(animation, animSpeed, sprite.offsetX, sprite.offsetY);
    }

    public void setNextAnimation(ArrayList<BufferedImage> animation, double animSpeed, int offsetX, int offsetY){
        animateTime = 0;
        nextAnimation = animation;
        nextAnimationSpeed = animSpeed;
        nextAnimationOffsetX = offsetX;
        nextAnimationOffsetY = offsetY;
    }

    public void setNextAnimation(ArrayList<BufferedImage> animation, double animSpeed){
        setNextAnimation(animation, animSpeed, sprite.offsetX, sprite.offsetY);
    }

    public int getDirection(){return sprite.getDirection();}

    public void setDirection(int newDirection){
        sprite.setDirection(newDirection);
    }

    public void animate(){
        animateTime += GamePanel.deltaTime;

        if(animateTime >= animationSpeed){
            animateTime = 0;

            animationIndex+=1;

            if (animationIndex >= getAnimation().size()){
                if (nextAnimation != null){
                    currentAnimation = new ArrayList<>(nextAnimation);
                    animationSpeed = nextAnimationSpeed;
                    sprite.offsetX = nextAnimationOffsetX;
                    sprite.offsetY = nextAnimationOffsetY;

                    animationPriority = 0;
                    nextAnimation = null;
                    nextAnimationSpeed = 0;
                    nextAnimationOffsetX = 0;
                    nextAnimationOffsetY = 0;
                }
                animationIndex = 0;
            }

            if (sprite.resizeFactor == 0) sprite.setImage(currentAnimation.get(animationIndex), hitbox);
            else sprite.setImage(currentAnimation.get(animationIndex));
        }

    }

    boolean pointIsIn(int x, int y){
        if (isGUI){
            return getX() <= x && x <= getX() + getWidth() &&
                    getY() <= y && y <= getY() + getHeight();
        }
        Camera camera = GamePanel.camera;
        return getX() - camera.getScreenX() <= x && x <= getX() + getWidth() - camera.getScreenX() &&
                getY() - camera.getScreenY() <= y && y <= getY() + getHeight() - camera.getScreenY();
    }

    public void draw(Graphics2D g2D, ImageObserver IO){
        if (isGUI){
            g2D.drawImage(getSprite().getImage(),
                    getSprite().getOffsetX(getHitbox()),
                    getSprite().getOffsetY(getHitbox()),
                    getSprite().getWidth(),getSprite().getHeight(), IO);
        }else{
            g2D.drawImage(getSprite().getImage(),
                    getSprite().getOffsetX(getHitbox()) - GamePanel.camera.getScreenX() ,
                    getSprite().getOffsetY(getHitbox()) - GamePanel.camera.getScreenY(),
                    getSprite().getWidth(), getSprite().getHeight(), IO);
        }
    }

    public void update() throws Exception {
        //is overwritten after
    }

    public void collision(Entity e) throws Exception {
        //is overwritten after in more specific context
    }

    boolean intersects(GameObject2D go){
        //another way to do the collision, but without using the previous position
        if (getY() + getHeight() < go.getY() || getY() > go.getY() + go.getHeight() ||
                getX() > go.getX() + go.getWidth() || getX() + getWidth() < go.getX()){
            return false;
        }

        return (getX() + getWidth() >= go.getX()) ||
                (getX() <= go.getX() + go.getWidth()) ||
                (getY() + getHeight() >= go.getY()) ||
                (getY() <= go.getY() + go.getHeight());
    }

    protected ArrayList<GameObject2D> getNear(){

        ArrayList<int[]> thisEntityGridCells = GamePanel.camera.findRectPosInGrid(this, 0, 0, 0, 2);
        ArrayList<GameObject2D> result = new ArrayList<>();

        for ( int[] pos: thisEntityGridCells) {

            ArrayList<GameObject2D> cell = GamePanel.camera.getCellContent(pos[0], pos[1]);

            for (GameObject2D object: cell) {

                if (!result.contains(object) && object != this){

                    result.add(object);
                }
            }
        }
        return result;
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

    public Entity getThisEntity() throws Exception{
        throw  new Exception("Method used on a non-Entity GameObject");
    }

    public Button getThisButton() throws Exception {
        throw new Exception("Method used on a non-button GameObject");
    }

    public Background getThisBackground() throws Exception {
        throw new Exception("Method used on a non-Background GameObject");
    }
    public MovingPlatform getThisMovingPlatform() throws Exception{
        throw new Exception("Method used on a non-MovingPlatform GameObject");
    }

    public SnowflakeGenerator getThisSnowflakeGenerator() throws Exception{
        throw new Exception("Method used on a non-SnowflakeGenerator GameObject");
    }

    public TextObject getThisTextObject() throws Exception{
        throw new Exception("Method used on a non-TextObject GameObject");
    }

    public Door getThisDoor() throws Exception {
        throw new Exception("Method used on a non-Door GameObject");
    }

    public Player getThisPlayer() throws Exception{
        throw new Exception("Method used on a non-Player GameObject");
    }

    public double getFriction(){
        return 0;
    }

    public void reset() throws Exception {}

    //static methods and variables
    static Player player;
    static Map<String,BufferedImage> imageBuffer = new HashMap<>();

    public static boolean hasNoPlayer(){
        return player == null;
    }

    public static Player getPlayer(){
        return player;
    }

    public static void setPlayer(Player p){
        player = p;
    }

    public static BufferedImage readImageBuffered(String path) throws IOException {

        if (imageBuffer.containsKey(path)){
            return imageBuffer.get(path);
        }
        BufferedImage image = ImageIO.read(new File(path));
        imageBuffer.put(path, image);
        return image;
    }
    public static double lerp(double val1, double val2, double speed){
        return (1 - speed) * val1 + speed * val2;
    }
}
