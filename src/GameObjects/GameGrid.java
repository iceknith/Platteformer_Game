package GameObjects;


import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GameGrid {

    ArrayList<ArrayList<ArrayList<GameObject2D>>> grid;
    public ArrayList<GameObject2D> allGOInGrid;

    Vector<GameObject2D> visible = new Vector<>();
    public Level level = new Level();

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
        allGOInGrid = new ArrayList<>();
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

    public void deleteGOInGrid(GameObject2D r){deleteGOInGrid(r, true);}

    public void deleteGOInGrid(GameObject2D r, boolean permaRemove){
        allGOInGrid.remove(r);

        ArrayList<int[]> cellPos = findRectPosInGrid(r);

        //remove from permanent updated
        if (permaRemove &&
                (r.type.equals("Player") ||
                r.type.contains("MovingPlatform_") ||
                r.type.contains("IceBlock") ||
                r.type.contains("SnowflakeGenerator"))){
            level.permanentUpdatableRemoveBuffer.add(r);
        }

        for (int[] pos: cellPos) {
            grid.get(pos[0]).get(pos[1]).remove(r);
        }
    }

    public void addGOInGrid(GameObject2D r){addGOInGrid(r, true);}

    public void addGOInGrid(GameObject2D r, boolean firstAdd){

        //do not add gui in grid
        if (r.isGUI) return;

        allGOInGrid.add(r);

        //add to permanent updated
        if (firstAdd &&
                (r.type.equals("Player") ||
                r.type.contains("MovingPlatform_") ||
                r.type.contains("IceBlock") ||
                r.type.contains("SnowflakeGenerator"))){
            level.permanentUpdatableAddBuffer.add(r);
        }


        int rPosX = r.getSprite().getOffsetX(r.getHitbox());
        int rPosY = r.getSprite().getOffsetY(r.getHitbox());
        int rWidth = r.sprite.getWidth();
        int rHeight = r.sprite.getHeight();

        //if grid is empty
        if (grid.isEmpty()){
            grid = new ArrayList<>(List.of(new ArrayList<>(List.of(new ArrayList<>()))));
            x =  cellWidth * (rPosX/cellWidth);
            if (rPosX < 0) x -= cellWidth;
            y = cellHeight * (rPosY/cellHeight);
            if (rPosY < 0) y -= cellHeight;
        }

        if(!isInGrid(r)){
            ///--- Adjust Grid size, if r is not in it ---///

            //debug variables
            int oX = x;
            int oY = y;
            int oWidth = grid.size() * cellWidth;
            int oHeight = grid.get(0).size() * cellHeight;

            // if rectangle is on top of the current Grid
            if (rPosY < y){
                //adjusting the grid size to include the new Game Object
                int oldY = y;
                y = cellHeight * (rPosY/cellHeight);
                if (rPosY < 0) y -= cellHeight;

                //adding the empty lineY to grid
                for (ArrayList<ArrayList<GameObject2D>> lineX : grid){
                    for (int indexY = 0; indexY < Math.abs(oldY - y)/cellHeight; indexY++){
                        lineX.add(0, new ArrayList<>());
                    }
                }
            }

            // if rectangle is under the current Grid
            if (rPosY + rHeight > y + grid.get(0).size() * cellHeight){
                //adjusting the grid size to include the new Game Object
                int oldMaxY = y;
                int maxY = cellHeight * ((rPosY + rHeight)/cellHeight);
                if (rPosY + rHeight < 0) maxY -= cellHeight;
                else if ((rPosY + rHeight)/cellHeight == 0 && rPosX + rHeight > 0) maxY += cellHeight;

                //adding the empty lineY to grid
                for (ArrayList<ArrayList<GameObject2D>> lineX : grid){
                    for (int indexY = 0; indexY < Math.abs(oldMaxY - maxY)/cellHeight; indexY++){
                        lineX.add(new ArrayList<>());
                    }
                }
            }

            // if rectangle is on the left of the current Grid
            if (rPosX < x){
                //adjusting the grid size to include the new Game Object
                int oldX = x;
                x =  cellWidth * (rPosX/cellWidth);
                if (rPosX < 0) x -= cellWidth;

                //adding the empty lineX to grid
                for (int indexX = 0; indexX < Math.abs(oldX - x)/cellWidth; indexX++){
                    grid.add(0, new ArrayList<>());

                    for (int y = 0; y < grid.get(1).size(); y++){
                        grid.get(0).add(new ArrayList<>());
                    }
                }
            }

            // if rectangle is on the right of the current Grid
            if (rPosX + rWidth > x + grid.size() * cellWidth){
                //adjusting the grid size to include the new Game Object
                int oldMaxX = x;
                int maxX = cellWidth * ((rPosX + rWidth)/cellWidth);
                if (rPosX < 0) maxX -= cellWidth;
                else if ((rPosX + rWidth)/cellWidth == 0 && rPosX + rWidth > 0) maxX += cellWidth;

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
                System.out.println("Old Grid infos:");
                System.out.println("X: " + oX + ", Y: " + oY + ", Width : " + oWidth + ", Height : " + oHeight);
                System.out.println("New Grid infos:");
                System.out.println("X: " + x + ", Y: " + y + ", Width : " + grid.size() * cellWidth + ", Height : " + grid.get(0).size() * cellHeight);
                System.out.println("GO info:");
                System.out.println("X: " + rPosX + ", Y: " + rPosY+
                        ", Width : " + r.getWidth() + ", Height : " + r.getHeight() + ", Name : " + r.getName());
                System.out.println("\n----------\n");
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
        return findRectPosInGrid(r.getX(), r.getY(), r.getWidth(), r.getHeight(), leftOffset, rightOffset, upOffset, downOffset);
    }

    public ArrayList<int[]> findPointPosInGrid(int posX, int posY){
        return findRectPosInGrid(posX, posY, 0,0,0,0,0,0);
    }

    public ArrayList<int[]> findRectPosInGrid(int posX, int posY, int w, int h, int leftOffset, int rightOffset, int upOffset, int downOffset){

        int x1 = (posX - leftOffset - x) / cellWidth;
        int y1 = (posY - upOffset  - y) / cellHeight;
        int x2 = (posX + w + rightOffset - x) / cellWidth;
        int y2 = (posY + h + downOffset - y) / cellHeight;


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

        return x <= goPosX && goPosX + go.sprite.getWidth() <= x + cellWidth * grid.size() &&
                y <= goPosY && goPosY + go.sprite.getHeight() <= y + cellHeight * grid.get(0).size();
    }

    public void initialiseGrid(ArrayList<GameObject2D> levelObjects){
        visible.clear();
        level.clearUpdatable();
        grid.clear();
        allGOInGrid.clear();
        x = 0; y = 0;

        for (GameObject2D go : levelObjects){
            addGOInGrid(go);
        }

        //System.out.println("grid has been initialised :");
        //visualiseGrid();
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

            ArrayList<GameObject2D> lastDisplayed = new ArrayList<>();

            for (int indexX = startIndexX; indexX < endIndexX; indexX ++){

                for (int indexY = startIndexY; indexY < endIndexY; indexY ++){

                    if (!grid.get(indexX).get(indexY).isEmpty()){

                        for (GameObject2D go : grid.get(indexX).get(indexY)){
                            if (!visible.contains(go) && !lastDisplayed.contains(go)){

                                if (!go.type.equals("Player") &&
                                    !go.type.contains("MovingPlatform_")){
                                    level.addUpdatable(go);
                                }
                                if (go.isEntity){
                                    lastDisplayed.add(go);
                                }
                                else{
                                    visible.add(go);
                                }
                            }
                        }
                    }
                }
            }

            //add the last displayed objects
            visible.addAll(lastDisplayed);
        }

        //handle gui and background
        GameObject2D backgroundBuffer = null;
        for (GameObject2D go : level.getPermaDisplayed()){
            level.addUpdatable(go);
            if (go.type.contains("Background_")) {
                backgroundBuffer = go;
            }
            else{
                visible.add(go);
            }
        }
        if (backgroundBuffer != null){
            visible.insertElementAt(backgroundBuffer, 0);
        }
    }

    public boolean isInVisibleRange(GameObject2D go){
        int startIndexX = Math.max((screenX - x)/cellWidth - 1, 0);
        int endIndexX = Math.min((screenX + screenWidth - x)/cellWidth + 1, grid.size());
        int startIndexY = Math.max((screenY - y)/cellHeight - 1, 0);
        int endIndexY = Math.min((screenY + screenHeight - y)/cellHeight + 1, grid.get(0).size());

        ArrayList<int[]> positions = findRectPosInGrid(go);

        for (int[] pos : positions){
            if (startIndexX < pos[0] && pos[0] < endIndexX &&
                startIndexY < pos[1] && pos[1] < endIndexY)
                return true;
        }

        return false;
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
                    msg.append(go.name).append(" ");
                }
                msg.append("] ");
                System.out.print(msg);
            }
            System.out.println();
        }
        System.out.println();
    }
}
