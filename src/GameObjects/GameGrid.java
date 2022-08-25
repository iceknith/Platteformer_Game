package GameObjects;

import main.GamePanel;

import java.util.ArrayList;

public class GameGrid {

    ArrayList<ArrayList<ArrayList<GameObject2D>>> grid;

    int x;
    int y;

    float cellXNum = 10;
    float cellYNum = 10;

    double cellWidth;
    double cellHeight;

    public GameGrid(int width, int height, int gridX, int gridY) {

        cellWidth = width / cellXNum;
        cellHeight = height / cellYNum;

        x = gridX;
        y = gridY;

        grid = new ArrayList<>();

        updateGrid();
    }

    public void initialise(double w, double h){
        for (int x = 0; x < w; x ++){
            grid.add(new ArrayList<>());
            for (int y = 0; y < h; y ++){
                grid.get(x).add(new ArrayList<>());
            }
        }
    }

    public ArrayList<GameObject2D> getCellContent(int cellX, int cellY){
        return grid.get(cellX).get(cellY);
    }

    public void deleteRectInGrid(GameObject2D r){
        ArrayList<int[]> cellPos = findRectPosInGrid(r);

        for (int[] pos: cellPos) {
            grid.get(pos[0]).get(pos[1]).remove(r);
        }
    }

    public void addRectInGrid(GameObject2D r){

        ArrayList<int[]> cellPos = findRectPosInGrid(r);

        for (int[] pos: cellPos) {
            grid.get(pos[0]).get(pos[1]).add(r);
        }
    }

    public ArrayList<int[]> findRectPosInGrid(GameObject2D r){

        int x1 = (int) (r.getX() / cellWidth);
        int y1 = (int) (r.getY() / cellHeight);
        int x2 = (int) ((r.getX() + r.getWidth()) / cellWidth);
        int y2 = (int) ((r.getY() + r.getHeight())/ cellHeight);

        ArrayList<int[]> result = new ArrayList<>();
        for (int x = x1; x <= x2; x ++){
            if (x >= cellXNum || x < 0){
                break;
            }
            for (int y = y1; y <= y2; y ++){
                if (y >= cellYNum || y < 0){
                    break;
                }
                result.add(new int[] {x, y});
            }
        }
        return result;
    }

    public void visualiseGrid(){
        int i = 0;
        int j = 0;
        for (ArrayList y: grid.get(0)) {
            i = 0;
            for (ArrayList x: grid){
                System.out.print(grid.get(i).get(j) + " ");
                i++;
            }
            System.out.println();
            j++;
        }
    }

    public void updateGrid(){
        grid.clear();

        initialise(cellXNum, cellYNum);

        //temporary
        for (GameObject2D p: Platform.visiblePlatforms) {
            addRectInGrid(p);
        }
        addRectInGrid(GamePanel.player);

        //visualiseGrid();
    }
}
