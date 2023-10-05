package GameObjects;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GameGrid {

    ArrayList<ArrayList<ArrayList<GameObject2D>>> grid;

    Vector<GameObject2D> visible = new Vector<>();
    Level level = new Level();

    int screenX;
    int screenY;

    int x;
    int y;

    int cellWidth;
    int cellHeight;

    int screenWidth;
    int screenHeight;

    public boolean isOperational = false;

    String nextLevel;

    public GameGrid(int w, int h, int gridX, int gridY) {

        screenWidth = w;
        screenHeight = h;

        cellWidth = 64;
        cellHeight = 64;

        screenX = gridX;
        screenY = gridY;

        grid = new ArrayList<>();
    }

    public ArrayList<GameObject2D> getCellContent(int cellX, int cellY){
        return grid.get(cellX).get(cellY);
    }

    public int getScreenX() {return screenX;}

    public int getScreenY() {return screenY;}

    public int getScreenWidth() {return screenWidth;}

    public int getScreenHeight() {return screenHeight;}

    public ArrayList<GameObject2D> getUpdatable(){return level.getUpdatable();}

    public Vector<GameObject2D> getVisible(){return visible;}


    public void setScreenX(int posX) {
        screenX = posX;}

    public void setScreenY(int posY) {
        screenY = posY;}

    public void deleteGOInGrid(GameObject2D r){
        ArrayList<int[]> cellPos = findRectPosInGrid(r);

        for (int[] pos: cellPos) {
            grid.get(pos[0]).get(pos[1]).remove(r);
        }
    }

    public void addGOInGrid(GameObject2D r){

        //do not add gui or player in grid
        if (r.isGUI || r.type.equals("Player")) return;

        //if grid is empty
        if (grid.isEmpty()){
            grid = new ArrayList<>(List.of(new ArrayList<>(List.of(new ArrayList<>()))));
            x =  cellWidth * (r.getX()/cellWidth);
            if (r.getX() < 0) x -= cellWidth;
            y = cellHeight * (r.getY()/cellHeight);
            if (r.getY() < 0) y -= cellHeight;
        }

        if(!isInGrid(r)){
            ///--- Adjust Grid size, if r is not in it ---///

            // if rectangle is on top of the current Grid
            if (r.getY() < y){
                //adjusting the grid size to include the new Game Object
                int oldY = y;
                y = cellHeight * (r.getY()/cellHeight);
                if (r.getY() < 0) y -= cellHeight;

                //adding the empty lineY to grid
                for (ArrayList<ArrayList<GameObject2D>> lineX : grid){
                    for (int indexY = 0; indexY < Math.abs(oldY - y)/cellHeight; indexY++){
                        lineX.add(0, new ArrayList<>());
                    }
                }
            }

            // if rectangle is under the current Grid
            if (r.getY() + r.getHeight() > y + grid.get(0).size() * cellHeight){
                //adjusting the grid size to include the new Game Object
                int oldMaxY = y;
                int maxY = cellHeight * ((r.getY() + r.getWidth())/cellHeight);
                if (r.getY() < 0) maxY -= cellHeight;

                //adding the empty lineY to grid
                for (ArrayList<ArrayList<GameObject2D>> lineX : grid){
                    for (int indexY = 0; indexY < Math.abs(oldMaxY - maxY)/cellHeight; indexY++){
                        lineX.add(new ArrayList<>());
                    }
                }
            }

            // if rectangle is on the left of the current Grid
            if (r.getX() < x){
                //adjusting the grid size to include the new Game Object
                int oldX = x;
                x =  cellWidth * (r.getX()/cellWidth);
                if (r.getX() < 0) x -= cellWidth;

                //adding the empty lineX to grid
                for (int indexX = 0; indexX < Math.abs(oldX - x)/cellWidth; indexX++){
                    grid.add(0, new ArrayList<>());

                    for (int y = 0; y < grid.get(1).size(); y++){
                        grid.get(0).add(new ArrayList<>());
                    }
                }
            }

            // if rectangle is on the right of the current Grid
            if (r.getX() + r.sprite.getWidth() > x + grid.size() * cellWidth){
                //adjusting the grid size to include the new Game Object
                int oldMaxX = x;
                int maxX = cellWidth * ((r.getX() + r.getWidth())/cellWidth);
                if (r.getX() < 0) maxX -= cellWidth;

                //adding the empty lineX to grid
                for (int indexX = 0; indexX < Math.abs(oldMaxX - maxX)/cellWidth; indexX++){
                    grid.add(new ArrayList<>());

                    for (int y = 0; y < grid.get(0).size(); y++){
                        grid.get(grid.size() - 1).add(new ArrayList<>());
                    }
                }
            }

            // verification check
            if (!isInGrid(r)) {
                System.out.println("Error, the grid adjustment is faulty:");
                System.out.println("Grid infos:");
                System.out.println("X: " + x + ", Y: " + y + ", Width : " + grid.size() * cellWidth + ", Height : " + grid.get(0).size() * cellHeight);
                System.out.println("GO info:\n" + r.getDebugInfos() + "\n----------\n");
                return;
            }

        }

        //add r to grid
        ArrayList<int[]> cellPos = findRectPosInGrid(r);

        for (int[] pos: cellPos) {
            grid.get(pos[0]).get(pos[1]).add(r);
        }
    }

    public ArrayList<int[]> findRectPosInGrid(GameObject2D r){
        return findRectPosInGrid(r, 0, 0, 0, 0);
    }

    public ArrayList<int[]> findRectPosInGrid(GameObject2D r, int leftOffset, int rightOffset, int upOffset, int downOffset){

        int x1 = (r.getX() - leftOffset - x) / cellWidth;
        int y1 = (r.getY() - upOffset  - y) / cellHeight;
        int x2 = (r.getX() + r.getWidth() + rightOffset - x) / cellWidth;
        int y2 = (r.getY() + r.getHeight() + downOffset - y) / cellHeight;


        ArrayList<int[]> result = new ArrayList<>();
        for (int x = x1; x <= x2; x ++){

            if (x >= grid.size() || x < 0) continue;

            for (int y = y1; y <= y2; y ++){

                if (y >= grid.get(0).size() || y < 0) continue;

                result.add(new int[] {x, y});
            }
        }
        return result;
    }

    public boolean isInGrid(GameObject2D go){
        int goPosX = go.getSprite().getOffsetX(go.getHitbox());
        int goPosY = go.getSprite().getOffsetY(go.getHitbox());

        return x <= goPosX && goPosX + go.getWidth() <= x + cellWidth * grid.size() &&
                y <= goPosY && goPosY + go.getHeight() <= y + cellHeight * grid.get(0).size();
    }

    public void initialiseGrid(ArrayList<GameObject2D> levelObjects){
        visible.clear();
        level.clearUpdatable();
        grid.clear();
        x = 0; y = 0;

        for (GameObject2D go : levelObjects){
            addGOInGrid(go);
        }

        System.out.println("grid has been initialised :");
        visualiseGrid();
    }

    public void loadVisible(){

        visible.clear();
        level.updatable.clear();

        //handle objects
        if (!grid.isEmpty()){
            int startIndexX = Math.max((screenX - x)/cellWidth - 1, 0);
            int endIndexX = Math.min((screenX + screenWidth - x)/cellWidth + 1, grid.size());
            int startIndexY = Math.max((screenY - y)/cellHeight - 1, 0);
            int endIndexY = Math.min((screenY + screenHeight - y)/cellHeight + 1, grid.get(0).size());

            for (int indexX = startIndexX; indexX < endIndexX; indexX ++){

                for (int indexY = startIndexY; indexY < endIndexY; indexY ++){

                    if (!grid.get(indexX).get(indexY).isEmpty()){

                        for (GameObject2D go : grid.get(indexX).get(indexY)){
                            level.addUpdatable(go);
                            visible.add(go);
                        }
                    }
                }
            }
        }

        //handle gui and background
        GameObject2D background = null;
        for (GameObject2D go : level.getPermaDisplayed()){
            level.addUpdatable(go);
            if (go.type.contains("Background_")) {
                background = go;
            }
            else{
                visible.add(go);
            }
        }
        if (background != null){
            visible.insertElementAt(background, 0);
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
        isOperational = false;
        setScreenX(0);
        setScreenY(0);

        level = new Level(nextLevel);
        deleteNextLevel();

        initialiseGrid(level.getSubLvl("main").objectList);
        loadVisible();
        isOperational = true;
    }

    public void updateGrid() {
        loadVisible();
    }

    public void visualiseGrid(){
        if (grid.isEmpty()) {
            System.out.println("The grid is empty");
            return;
        }
        System.out.println("Grid infos:");
        System.out.println("X: " + x + ", Y: " + y + ", Width : " + grid.size() * cellWidth + ", Height : " + grid.get(0).size() * cellHeight);
        for (int y = 0; y < grid.get(0).size(); y++) {
            for (ArrayList<ArrayList<GameObject2D>> x: grid){
                StringBuilder msg = new StringBuilder(" [ ");

                for (GameObject2D go : x.get(y)){
                    msg.append(go.name);
                }
                msg.append(" ] ");
                System.out.print(msg);
            }
            System.out.println();
        }
        System.out.println();
    }
}
