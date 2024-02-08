package GameObjects;

import main.GamePanel;

import java.io.IOException;
import java.util.ArrayList;

public class Entity extends GameObject2D{
    protected double velocityY;
    protected double velocityX;

    protected int prevX;
    protected int prevY;

    public boolean hasHP = false;
    public double hp = 0;

    protected Entity(int x, int y, int w, int h, String subLvl){
        super(x, y, w, h, subLvl);

        prevX = x;
        prevY = y;
        isEntity = true;
    }

    protected Entity(Entity e){
        super(e);

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

    protected void move() throws Exception {
        GamePanel.camera.deleteGOInGrid(this, false);
        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round(velocityX * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round(velocityY * GamePanel.deltaTime)));
        GamePanel.camera.addGOInGrid(this, false);

        if (GamePanel.camera.isInVisibleRange(this) && !GamePanel.camera.visible.contains(this)){
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
            return 1;
        }
        if(getY() <= go.getY() + go.getHeight() && getPreviousY() > go.getPreviousY() + go.getHeight()){
            if (go.hasPhysicalCollisions){
                setY(go.getY() + go.getHeight() + 1);
                velocityY = go.getVelocityY();
            }
            go.collision(this);
            return 2;
        }
        if(getX() + getWidth() >= go.getX() && getPreviousX() + getWidth() < go.getPreviousX()){
            if (go.hasPhysicalCollisions){
                setX(go.getX() - getWidth() - 1);
                velocityX = go.getVelocityX();
            }
            go.collision(this);
            return 3;
        }
        if(getX() <= go.getX() + go.getWidth() && getPreviousX() > go.getPreviousX() + go.getWidth()){
            if (go.hasPhysicalCollisions){
                setX(go.getX() + go.getWidth() + 1);
                velocityX = go.getVelocityX();
            }
            go.collision(this);
            return 4;
        }
        return 0;
    }

    public void killThisEntity(){
        GamePanel.camera.deleteGOInGrid(this);
        GamePanel.camera.bufferUpdateGrid = true;
    }

    @Override
    public Entity getThisEntity() throws Exception {
        return this;
    }
}
