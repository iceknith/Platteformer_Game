package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class GameGrid {

    ArrayList<ArrayList<ArrayList<GameObject2D>>> grid;

    int x;
    int y;

    int cellXNum = 10;
    int cellYNum = 10;

    int cellWidth;
    int cellHeight;

    public GameGrid(int width, int height, int gridX, int gridY) throws FileNotFoundException {

        cellWidth = (int) (width / cellXNum);
        cellHeight = (int) (height / cellYNum);

        x = gridX;
        y = gridY;

        grid = new ArrayList<>();

        updateGrid();
    }


    public ArrayList<GameObject2D> getCellContent(int cellX, int cellY){
        return grid.get(cellX).get(cellY);
    }

    public int getX() {return x;}

    public int getY() {return y;}


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
        ArrayList<String> x = new ArrayList<>();
    }

    public void addGOInGrid(GameObject2D r){

        ArrayList<int[]> cellPos = findRectPosInGrid(r);

        for (int[] pos: cellPos) {
            grid.get(pos[0]).get(pos[1]).add(r);
        }
        //visualiseGrid();
    }

    public ArrayList<int[]> findRectPosInGrid(GameObject2D r){

        int x1 = r.getX() / cellWidth;
        int y1 = r.getY() / cellHeight;
        int x2 = (r.getX() + r.getWidth()) / cellWidth;
        int y2 = (r.getY() + r.getHeight()) / cellHeight;


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

    public ArrayList<GameObject2D> loadVisibleGO(String lvlName) throws FileNotFoundException {
        ArrayList<GameObject2D> result = new ArrayList<>();

        Scanner lvlReader = new Scanner(new File("assets/level/"+lvlName+".txt"));

        while (lvlReader.hasNextLine()) {

            String line = lvlReader.nextLine();
            String[] infoGO = line.split(";");

            if(Objects.equals(infoGO[0], "Platform")){

                //add conditions for optimisation
                Platform p = new Platform(Integer.parseInt(infoGO[1]), Integer.parseInt(infoGO[2]),
                        Integer.parseInt(infoGO[3]) - x, Integer.parseInt(infoGO[4]) - y,
                        Color.decode(infoGO[5]));

                result.add(p);

            }
        }

        lvlReader.close();

        return result;
    }

    public void updateGrid() throws FileNotFoundException {

        GameObject2D.resetVisible();
        grid.clear();

        initialise(cellXNum, cellYNum);

        //temporary
        ArrayList<GameObject2D> x = loadVisibleGO("1");

        for (GameObject2D obj: x) {
            addGOInGrid(obj);
        }
        addGOInGrid(GamePanel.player);


        //visualiseGrid();
    }

    public void visualiseGrid(){
        int i = 0;
        int j = 0;
        for (ArrayList<GameObject2D> y: grid.get(0)) {
            i = 0;
            for (ArrayList<ArrayList<GameObject2D>> x: grid){
                System.out.print(grid.get(i).get(j) + " ");
                i++;
            }
            System.out.println();
            j++;
        }
        System.out.println();
    }
}
