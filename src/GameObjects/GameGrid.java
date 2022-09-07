package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
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

    int width;
    int height;

    public GameGrid(int w, int h, int gridX, int gridY) throws IOException {

        width = w;
        height = h;

        cellWidth = (int) (width / cellXNum);
        cellHeight = (int) (height / cellYNum);

        x = gridX;
        y = gridY;

        grid = new ArrayList<>();

        updateGrid(0,0);
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

    public boolean isInGridPos(int posX, int posY, int w, int h){
        //if one rect is on the left of the other
        if(x > posX + w || posX > x + width){
            return false;
        }

        //if one rect is above the other
        if(y > posY + h || posY > y + height){
            return false;
        }

        return true;
    }

    public ArrayList<GameObject2D> loadVisibleGO(String lvlName, double movementX, double movementY) throws IOException {
        ArrayList<GameObject2D> result = new ArrayList<>();

        Scanner lvlReader = new Scanner(new File("assets/level/"+lvlName+".txt"));

        while (lvlReader.hasNextLine()) {

            String line = lvlReader.nextLine();
            String[] infoGO = line.split(";");

            if(Objects.equals(infoGO[0], "Platform")) {
                int posX = Integer.parseInt(infoGO[3]);
                int posY = Integer.parseInt(infoGO[4]);
                int w = Integer.parseInt(infoGO[1]);
                int h = Integer.parseInt(infoGO[2]);

                //add conditions for optimisation
                if(isInGridPos(posX, posY, w, h)){
                    Platform p = new Platform(w, h, posX - x, posY - y, Color.decode(infoGO[5]));
                    result.add(p);
                }
            }

            if(Objects.equals(infoGO[0], "Player")){

                if(GamePanel.player == null){
                    GamePanel.player = new Player(Integer.parseInt(infoGO[1]), Integer.parseInt(infoGO[2]));
                }else {
                    GamePanel.player.setX((int) (GamePanel.player.getX() + movementX));
                    GamePanel.player.setY((int) (GamePanel.player.getY() + movementY));
                }
                result.add(GamePanel.player);
            }

        }

        lvlReader.close();

        return result;
    }

    public void updateGrid(double movementX, double movementY) throws IOException {

        grid.clear();

        initialise(cellXNum, cellYNum);

        //temporary
        ArrayList<GameObject2D> loaded = loadVisibleGO("1", movementX, movementY);

        GameObject2D.resetVisible();
        GameObject2D.setVisible(loaded);

        for (GameObject2D obj: loaded) {
            addGOInGrid(obj);
        }


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
