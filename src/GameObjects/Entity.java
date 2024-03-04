package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class Entity extends GameObject2D{
    protected double velocityY;
    protected double velocityX;

    protected int prevX;
    protected int prevY;

    public boolean hasHP = false;
    public double hp = 0;
    public boolean isEnemy = false;
    public boolean dropsKey = false; //Useful only for enemies

    protected Entity(int x, int y, int w, int h, String subLvl){
        super(x, y, w, h, subLvl);

        prevX = x;
        prevY = y;
        isEntity = true;
    }

    protected Entity(Entity e){
        super(e);

        isEnemy = e.isEnemy;
        dropsKey = e.dropsKey;
        velocityY = e.velocityY;
        velocityX = e.velocityX;
        prevX = e.prevX;
        prevY = e.prevY;
        hasHP = e.hasHP;
        hp = e.hp;
    }

    @Override
    public GameObject2D copy() throws IOException {
        return new Entity(this);
    }

    @Override
    public void update() throws Exception {
        super.update();

        if (hasHP && hp < 0){
            killThisEntity();
        }
    }

    public double getVelocityX(){return velocityX;}

    public double getVelocityY(){return velocityY;}

    public int getPreviousX(){return prevX;}

    public int getPreviousY(){return prevY;}

    protected ArrayList<GameObject2D> getInBox(int rectWidth, int rectHeight){
        return getInBox(getX() + getWidth()/2 - rectWidth/2, getY() + getHeight()/2 - rectHeight/2, rectWidth, rectHeight);
    }

    protected ArrayList<GameObject2D> getInBox(int rX, int rY, int rWidth, int rHeight){
        Entity placeholder = new Entity(rX, rY, rWidth, rHeight, "None");
        placeholder.type = "Placeholder";
        placeholder.name = "Placeholder";
        ArrayList<int[]> thisEntityGridCells = GamePanel.camera.findRectPosInGrid(placeholder);
        ArrayList<GameObject2D> result = new ArrayList<>();

        for ( int[] pos: thisEntityGridCells) {

            ArrayList<GameObject2D> cell = GamePanel.camera.getCellContent(pos[0], pos[1]);

            for (GameObject2D object: cell) {
                if (!result.contains(object) && object != this && placeholder.intersects(object)){
                    result.add(object);
                }
            }
        }
        return result;
    }

    protected double getDistance(GameObject2D go){
        final int x = (getX() + getWidth()/2) - (go.getX() + go.getWidth()/2);
        final int y = (getY() + getHeight()/2) - (go.getY() + go.getHeight()/2);
        return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
    }

    protected double getDistance(int x0, int y0, int x1, int y1){
        return Math.sqrt(Math.pow(x0-x1, 2) + Math.pow(y0 - y1, 2));
    }

    protected void move() throws Exception {
        GamePanel.camera.deleteGOInGrid(this, false);
        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round(velocityX * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round(velocityY * GamePanel.deltaTime)));
        GamePanel.camera.addGOInGrid(this, false);

        if (GamePanel.camera.isInVisibleRange(this) && !GamePanel.camera.getVisible().contains(this)){
            System.out.println(getDebugInfos());
            GamePanel.camera.visible.add(this);
        }
    }

    protected int didCollide(GameObject2D go) throws Exception {

        if (getY() + getHeight() < go.getY() || getY() > go.getY() + go.getHeight() ||
                getX() > go.getX() + go.getWidth() || getX() + getWidth() < go.getX()){
            return 0;
        }
        if(getY() + getHeight() >= go.getY() && getPreviousY() + getHeight() <= go.getPreviousY()){
            if (go.hasPhysicalCollisions){
                setY(go.getY() - getHeight() - 1);
                velocityY = go.getVelocityY();
            }
            go.collision(this);
            collision(go);
            return 1;
        }
        if(getY() <= go.getY() + go.getHeight() && getPreviousY() > go.getPreviousY() + go.getHeight()){
            if (go.hasPhysicalCollisions){
                setY(go.getY() + go.getHeight() + 1);
                velocityY = go.getVelocityY();
            }
            go.collision(this);
            collision(go);
            return 2;
        }
        if(getX() + getWidth() >= go.getX() && getPreviousX() + getWidth() < go.getPreviousX()){
            if (go.hasPhysicalCollisions){
                setX(go.getX() - getWidth() - 1);
                velocityX = go.getVelocityX();
            }
            go.collision(this);
            collision(go);
            return 3;
        }
        if(getX() <= go.getX() + go.getWidth() && getPreviousX() > go.getPreviousX() + go.getWidth()){
            if (go.hasPhysicalCollisions){
                setX(go.getX() + go.getWidth() + 1);
                velocityX = go.getVelocityX();
            }
            go.collision(this);
            collision(go);
            return 4;
        }
        return 0;
    }

    protected void collision(GameObject2D go) throws Exception {
        if (go.isEntity) collision(go.getThisEntity());
    }

    protected boolean isSafeGround(int distance){
        for (GameObject2D go: getInBox(getX() + getWidth()/2 + distance, getY() + getHeight() + 3, 1, 1)){
            if (go.hasPhysicalCollisions && !go.doesDamage) return true;
        }
        return false;
    }

    protected boolean isWall(int distance, boolean ignoreIceBlock){
        for (GameObject2D go: getInBox(getX() + getWidth()/2 + distance, getY(), 1, getHeight())){
            if (go.hasPhysicalCollisions && !go.getType().equals("Player") && (!go.getType().equals("IceBlock") || ignoreIceBlock)) return true;
        }
        return false;
    }

    public void damage(int damage) throws Exception {
        if (hasHP){
            hp -= damage;
        }
    }

    public void killThisEntity() throws Exception {
        GamePanel.camera.deleteGOInGrid(this);
        GamePanel.camera.bufferUpdateGrid = true;
    }

    @Override
    public Entity getThisEntity() throws Exception {
        return this;
    }
}
