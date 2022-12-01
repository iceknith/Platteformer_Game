package GameObjects;

import main.GamePanel;

import java.io.*;
import java.util.ArrayList;

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

    public void loadLevel(String lvlName) throws FileNotFoundException {

        try {
            FileInputStream fis = new FileInputStream("assets/level/" + lvlName + ".lvl");
            BufferedInputStream reader = new BufferedInputStream(fis);

            StringBuilder header = new StringBuilder();
            for (int i = 0; i < 14 ; i ++){
                header.append((char) reader.read());
            }
            if (! header.toString().equals("GameIce->VB0.1")){
                System.out.println("File not recognised");
                return;
            }

            int ch;
            int i = 0;
            while ((ch = reader.read()) != -1) {
                i++;
                switch ((char) ch) {
                    case 'J' -> { //Player
                        int x = reader.read()*256 + reader.read() - 32767;
                        int y = reader.read()*256 + reader.read() - 32767;

                        GamePanel.player = new Player(x, y,"#" + i);
                        level.add(GamePanel.player);
                        setX(x - width/2);
                        setY(y - height/2);
                    }
                    case 'P' -> { //Platform
                        int w = reader.read()*256 + reader.read() - 32767;
                        int h = reader.read()*256 + reader.read() - 32767;
                        int posX = reader.read()*256 + reader.read() - 32767;
                        int posY = reader.read()*256 + reader.read() - 32767;

                        StringBuilder texture = new StringBuilder();
                        int cha;
                        while ((cha = reader.read()) != 10) {
                            texture.append((char) cha);
                        }

                        Platform p = new Platform(w, h, posX, posY, texture.toString(),"#" + i);
                        level.add(p);
                    }
                    case 'C' ->{ //Checkpoint
                        int posX = reader.read()*256 + reader.read() - 32767;
                        int posY = reader.read()*256 + reader.read() - 32767;

                        CheckPoint c = new CheckPoint(posX, posY, "#" + i);
                        level.add(c);
                    }
                }
            }

            reader.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateGrid() {
        loadVisible();
    }
    
    public void visualiseGrid(){
        int j = 0;
        for (ArrayList<GameObject2D> y: grid.get(0)) {
            int i = 0;
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
