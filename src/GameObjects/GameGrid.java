package GameObjects;


import java.io.*;
import java.util.ArrayList;

public class GameGrid {

    ArrayList<ArrayList<ArrayList<GameObject2D>>> grid;

    ArrayList<GameObject2D> visible = new ArrayList<>();
    Level level = new Level();

    int x;
    int y;

    int cellXNum = 10;
    int cellYNum = 10;

    int cellWidth;
    int cellHeight;

    int width;
    int height;

    public boolean isOperational = false;

    String nextLevel;

    public GameGrid(int w, int h, int gridX, int gridY) {

        width = w;
        height = h;

        cellWidth = width / cellXNum;
        cellHeight = height / cellYNum;

        x = gridX;
        y = gridY;

        grid = new ArrayList<>();
    }

    public ArrayList<GameObject2D> getCellContent(int cellX, int cellY){
        return grid.get(cellX).get(cellY);
    }

    public int getX() {return x;}

    public int getY() {return y;}

    public int getWidth() {return width;}

    public int getHeight() {return height;}

    public ArrayList<GameObject2D> getUpdatable(){return level.getUpdatable();}

    public ArrayList<GameObject2D> getVisible(){return visible;}


    public void setX(int posX) {x = posX;}

    public void setY(int posY) {y = posY;}

    public void initialise(double w, double h){
        for (int x = 0; x < w; x ++){
            grid.add(new ArrayList<>());
            for (int y = 0; y < h; y ++){
                grid.get(x).add(new ArrayList<>());
            }
        }
    }

    public void deleteGOInGrid(GameObject2D r){
        ArrayList<int[]> cellPos = findRectPosInGrid(r);

        for (int[] pos: cellPos) {
            grid.get(pos[0]).get(pos[1]).remove(r);
        }
    }

    public void addGOInGrid(GameObject2D r){

        ArrayList<int[]> cellPos = findRectPosInGrid(r);

        for (int[] pos: cellPos) {
            grid.get(pos[0]).get(pos[1]).add(r);
        }
        //visualiseGrid();
    }

    public ArrayList<int[]> findRectPosInGrid(GameObject2D r){

        int x1 = (r.getX() - getX()) / cellWidth;
        int y1 = (r.getY() - getY()) / cellHeight;
        int x2 = (r.getX() - getX() + r.getWidth()) / cellWidth;
        int y2 = (r.getY() - getY() + r.getHeight()) / cellHeight;


        ArrayList<int[]> result = new ArrayList<>();
        for (int x = x1; x <= x2; x ++){
            if (x >= cellXNum || x < 0){
                continue;
            }
            for (int y = y1; y <= y2; y ++){
                if (y >= cellYNum || y < 0){
                    continue;
                }
                result.add(new int[] {x, y});
            }
        }
        return result;
    }

    public boolean isInGrid(GameObject2D go){

        int posX = go.getSprite().getOffsetPosX(go.getHitbox());
        int posY = go.getSprite().getOffsetPosY(go.getHitbox());

        //if one rect is on the left of the other
        if(x > posX + go.getSprite().getWidth() || posX > x + width){
            return false;
        }

        //if one rect is above the other
        return y <= posY + go.getSprite().getHeight() && posY <= y + height;
    }

    public void resetGrid(){
        visible.clear();
        level.clearUpdatable();
        grid.clear();
        initialise(cellXNum, cellYNum);
    }

    public void loadVisible(){
        resetGrid();

        for (GameObject2D go: level.getDisplayedObjects()) {
            if (isInGrid(go) || go.isGUI){
                visible.add(go);
                level.addUpdatable(go);
                addGOInGrid(go);
            }
        }
    }

    public void setNextLevel(String lvl){
        nextLevel = lvl;
    }

    public void deleteNextLevel(){
        nextLevel = null;
    }

    public boolean hasNextLevel(){return nextLevel != null;}

    public String getNextLevel(){
        return nextLevel;
    }


    public void loadNextLevel() throws FileNotFoundException {
        resetGrid();
        setX(0);
        setY(0);

        level = new Level(nextLevel);
        deleteNextLevel();

        loadVisible();
        isOperational = true;
    }

    public void updateGrid() {
        loadVisible();
    }

    public void visualiseGrid(){
        int j = 0;
        for (ArrayList<GameObject2D> ignored : grid.get(0)) {
            int i = 0;
            for (ArrayList<ArrayList<GameObject2D>> ignored1 : grid){
                System.out.print(grid.get(i).get(j) + " ");
                i++;
            }
            System.out.println();
            j++;
        }
        System.out.println();
    }
}
