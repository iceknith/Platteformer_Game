package GameObjects;

import main.GamePanel;

import java.io.IOException;
import java.util.ArrayList;

public class Entity extends GameObject2D{
    double velocityY;
    double velocityX;

    int prevX;
    int prevY;

    Entity(int x, int y, int w, int h, String subLvl){
        super(x, y, w, h, subLvl);

        prevX = x;
        prevY = y;
    }

    Entity(Entity e){
        super(e);

        velocityY = e.velocityY;
        velocityX = e.velocityX;
        prevX = e.prevX;
        prevY = e.prevY;
    }

    @Override
    public GameObject2D copy() throws IOException {
        return new Entity(this);
    }

    public double getVelocityX(){return velocityX;}

    public double getVelocityY(){return velocityY;}

    public int getPreviousX(){return prevX;}

    public int getPreviousY(){return prevY;}

    boolean intersects(GameObject2D go){ //another way to do the collision, but without using the previous position
        if (getY() + getHeight() < go.getY() || getY() > go.getY() + go.getHeight() ||
                getX() > go.getX() + go.getWidth() || getX() + getWidth() < go.getX()){
            return false;
        }

        return (getX() + getWidth() >= go.getX()) ||
                (getX() <= go.getX() + go.getWidth()) ||
                (getY() + getHeight() >= go.getY()) ||
                (getY() <= go.getY() + go.getHeight());
    }

    ArrayList<GameObject2D> getNear(){

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

    void move() {
        GamePanel.camera.deleteGOInGrid(this);
        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round(velocityX * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round(velocityY * GamePanel.deltaTime)));
        GamePanel.camera.addGOInGrid(this);
    }
}
