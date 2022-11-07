package GameObjects;

import main.GamePanel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Objects;
import java.util.Scanner;

public class GameGrid {

    ArrayList<ArrayList<ArrayList<GameObject2D>>> grid;
    ArrayList<CheckPoint> activatedCheckpoints = new ArrayList<>();

    ArrayList<GameObject2D> visible = new ArrayList<>();
    ArrayList<GameObject2D> level = new ArrayList<>();

    int x;
    int y;

    int cellXNum = 10;
    int cellYNum = 10;

    int cellWidth;
    int cellHeight;

    int width;
    int height;

    public GameGrid(int w, int h, int gridX, int gridY) {

        width = w;
        height = h;

        cellWidth = (int) (width / cellXNum);
        cellHeight = (int) (height / cellYNum);

        x = gridX;
        y = gridY;

        grid = new ArrayList<>();
    }

    public ArrayList<GameObject2D> getCellContent(int cellX, int cellY){
        return grid.get(cellX).get(cellY);
    }

    public int getX() {return x;}

    public int getY() {return y;}

    public boolean hasActivatedCheckpoints(){return ! activatedCheckpoints.isEmpty();}

    public ArrayList<GameObject2D> getVisible(){return visible;}


    public void setX(int posX) {x = posX;}

    public void setY(int posY) {y = posY;}

    public void addActivatedCheckpoint(CheckPoint c){
        if (! activatedCheckpoints.contains(c)){
            activatedCheckpoints.add(c);
        }
    }

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

    public boolean isInGrid(GameObject2D go){
        //if one rect is on the left of the other
        if(x > go.getX() + go.getWidth() || go.getX() > x + width){
            return false;
        }

        //if one rect is above the other
        if(y > go.getY() + go.getHeight() || go.getY() > y + height){
            return false;
        }

        return true;
    }

    public void loadVisible(){

        visible.clear();
        grid.clear();
        initialise(cellXNum, cellYNum);

        for (GameObject2D go: level) {
            if (isInGrid(go)){
                visible.add(go);
                addGOInGrid(go);
            }
        }
    }

    public void loadLevel(String lvlName) throws  IOException{
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
                Platform p = new Platform(w, h, posX, posY, infoGO[5], infoGO[6]);
                level.add(p);
            }

            if(Objects.equals(infoGO[0], "Checkpoint")){
                int posX = Integer.parseInt(infoGO[1]);
                int posY = Integer.parseInt(infoGO[2]);

                CheckPoint c = new CheckPoint(posX, posY, infoGO[3]);
                level.add(c);
            }

            if(Objects.equals(infoGO[0], "Player")){
                int x = Integer.parseInt(infoGO[1]);
                int y = Integer.parseInt(infoGO[2]);

                GamePanel.player = new Player(x, y, infoGO[3]);
                level.add(GamePanel.player);
                setX(x - width/2);
                setY(y - height/2);
            }

        }

        lvlReader.close();
        loadVisible();
    }

    public void updateGrid() throws IOException {

        loadVisible();
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
