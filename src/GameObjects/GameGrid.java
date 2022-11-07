package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.Scanner;

public class GameGrid {

    ArrayList<ArrayList<ArrayList<GameObject2D>>> grid;
    ArrayList<CheckPoint> activatedCheckpoints = new ArrayList<>();

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

        updateGrid();
    }

    public ArrayList<GameObject2D> getCellContent(int cellX, int cellY){
        return grid.get(cellX).get(cellY);
    }

    public int getX() {return x;}

    public int getY() {return y;}

    public void setX(int posX) {x = posX;}

    public void setY(int posY) {y = posY;}

    public void addActivatedCheckpoint(CheckPoint c){
        if (! activatedCheckpoints.contains(c)){
            activatedCheckpoints.add(c);
        }
    }

    public boolean hasActivatedCheckpoints(){return ! activatedCheckpoints.isEmpty();}


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

    public ArrayList<GameObject2D> loadVisibleGO(String lvlName) throws IOException {
        ArrayList<GameObject2D> result = new ArrayList<>();

        Scanner lvlReader = new Scanner(new File("assets/level/"+lvlName+".txt"));

        while (lvlReader.hasNextLine()) {

            String line = new String(Base64.getDecoder().decode(lvlReader.nextLine()));
            String[] infoGO = line.split(";");

            if(Objects.equals(infoGO[0], "Platform")) {
                int posX = Integer.parseInt(infoGO[3]);
                int posY = Integer.parseInt(infoGO[4]);
                int w = Integer.parseInt(infoGO[1]);
                int h = Integer.parseInt(infoGO[2]);

                //add conditions for optimisation
                if(isInGridPos(posX, posY, w, h)){
                    Platform p = new Platform(w, h, posX, posY, infoGO[5], infoGO[6]);
                    result.add(p);
                }
            }

            if(Objects.equals(infoGO[0], "Checkpoint")){
                int posX = Integer.parseInt(infoGO[1]);
                int posY = Integer.parseInt(infoGO[2]);

                if(isInGridPos(posX, posY, 20, 75)){
                    boolean isActivated = false;
                    for (CheckPoint c: activatedCheckpoints) {
                        if (c.getX() == posX && c.getY() == posY){
                            isActivated = true;
                            break;
                        }
                    }
                    CheckPoint c = new CheckPoint(posX, posY, isActivated, infoGO[3]);
                    result.add(c);
                }
            }

            if(Objects.equals(infoGO[0], "Player")){

                if(GamePanel.player == null){
                    GamePanel.player = new Player(Integer.parseInt(infoGO[1]), Integer.parseInt(infoGO[2]), infoGO[3]);
                }
                result.add(GamePanel.player);
            }

        }

        lvlReader.close();

        return result;
    }

    public void updateGrid() throws IOException {

        grid.clear();

        initialise(cellXNum, cellYNum);

        //temporary
        ArrayList<GameObject2D> loaded = loadVisibleGO("1");

        //GameObject2D.resetVisible();
        GameObject2D.setVisible(loaded);

        for (GameObject2D obj: GameObject2D.getVisible()) {
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
