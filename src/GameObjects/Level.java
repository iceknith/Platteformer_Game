package GameObjects;

import handlers.KeyHandler;
import main.GamePanel;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Level {

    String levelName;
    ArrayList<SubLevel> subLevels = new ArrayList<>();
    ArrayList<String> subLvlQueue = new ArrayList<>();
    ArrayList<GameObject2D> updatable = new ArrayList<>();

    boolean wasMenuKeyPressed;
    boolean forceUpdate;

    public Level(String name) throws FileNotFoundException {
        levelName = name;
        loadLevel();
    }

    public Level(){

    }

    public String getLevelName(){
        return levelName;
    }

    public ArrayList<GameObject2D> getDisplayedObjects() {
        ArrayList<GameObject2D> displayedObjects = new ArrayList<>();
        for (SubLevel subLvl : subLevels) {
            if (subLvl.isDisplayed){
                displayedObjects.addAll(subLvl.objectList);
            }
        }
        return displayedObjects;
    }

    public void update() throws IOException, FontFormatException {
        if(KeyHandler.isMenuKeyPressed && ! wasMenuKeyPressed){
            subLevelBackHandler();
            wasMenuKeyPressed = true;
        }
        else{if(!KeyHandler.isMenuKeyPressed){
            wasMenuKeyPressed = false;
        }}

        if (forceUpdate){
            GamePanel.camera.updateGrid();
            forceUpdate = false;
        }

        for (GameObject2D go: updatable) {
            if (!go.getName().equals("Player")){
                go.update();
            }
        }
    }

    public void loadLevel() throws FileNotFoundException {
        if (levelName.isEmpty()){
            throw new FileNotFoundException("No level selected");
        }
        loadLevel(levelName);
    }

    public void loadLevel(String level) throws FileNotFoundException {
        try {
            FileInputStream fis = new FileInputStream("assets/level/" + level + ".lvl");
            BufferedInputStream reader = new BufferedInputStream(fis);

            StringBuilder header = new StringBuilder();
            for (int i = 0; i < 14 ; i ++){
                header.append((char) reader.read());
            }
            //check if version is correct
            if (! header.toString().equals("GameIce->VB0.2")){
                throw new FileNotFoundException("File not recognized");
            }

            int ch;
            int i = 0;
            ArrayList<GameObject2D> objectsBuffer = new ArrayList<>();

            while ((ch = reader.read()) != -1) {
                i++;
                switch ((char) ch) {
                    case 'J' -> { //Player
                        int x = reader.read()*256 + reader.read() - 32767;
                        int y = reader.read()*256 + reader.read() - 32767;

                        GameObject2D.setPlayer(new Player(x, y,"#" + i, ""));
                        objectsBuffer.add(GameObject2D.getPlayer());
                        GamePanel.camera.setX(x - GamePanel.camera.getWidth()/2);
                        GamePanel.camera.setY(y - GamePanel.camera.getHeight()/2);
                    }
                    case 'P' -> { //Platform
                        int w = reader.read()*256 + reader.read();
                        int h = reader.read()*256 + reader.read();
                        int posX = reader.read()*256 + reader.read() - 32767;
                        int posY = reader.read()*256 + reader.read() - 32767;

                        StringBuilder texture = new StringBuilder();
                        int cha;
                        while ((cha = reader.read()) != 10) {
                            texture.append((char) cha);
                        }

                        Platform p = new Platform(w, h, posX, posY, texture.toString(),"#" + i, "");
                        //System.out.println("x: " + p.getX() + ", y: " + p.getY() + ", w: " + p.getWidth() + ", h: " + p.getHeight());
                        objectsBuffer.add(p);
                    }
                    case 'C' ->{ //Checkpoint
                        int posX = reader.read()*256 + reader.read() - 32767;
                        int posY = reader.read()*256 + reader.read() - 32767;

                        CheckPoint c = new CheckPoint(posX, posY, "#" + i, "");
                        objectsBuffer.add(c);
                    }
                    case 'B' ->{ //Button
                        int cha;

                        switch ((char) reader.read()){

                            case 'L' -> { //levelChangingButton
                                int w = reader.read()*256 + reader.read();
                                int h = reader.read()*256 + reader.read();
                                int posX = reader.read()*256 + reader.read();
                                int posY = reader.read()*256 + reader.read();

                                StringBuilder lvl = new StringBuilder();
                                while ((cha = reader.read()) != 59) { //59 == ';'
                                    lvl.append((char) cha);
                                }

                                StringBuilder messageName = new StringBuilder();
                                while ((cha = reader.read()) != 59) { //59 == ';'
                                    messageName.append((char) cha);
                                }

                                StringBuilder texture = new StringBuilder();
                                while ((cha = reader.read()) != 10) { // 10 == '\n'
                                    texture.append((char) cha);
                                }

                                LevelChangingButton b = new LevelChangingButton(w,h,posX,posY,texture.toString(), messageName.toString(), "#"+i, lvl.toString(), "");
                                objectsBuffer.add(b);
                            }

                            case 'S' -> { //subLevelChangingButton
                                int w = reader.read()*256 + reader.read();
                                int h = reader.read()*256 + reader.read();
                                int posX = reader.read()*256 + reader.read();
                                int posY = reader.read()*256 + reader.read();

                                StringBuilder subLvl = new StringBuilder();
                                while ((cha = reader.read()) != 59) { //59 == ';'
                                    subLvl.append((char) cha);
                                }

                                StringBuilder messageName = new StringBuilder();
                                while ((cha = reader.read()) != 59) { //59 == ';'
                                    messageName.append((char) cha);
                                }

                                StringBuilder texture = new StringBuilder();
                                while ((cha = reader.read()) != 10) { // 10 == '\n'
                                    texture.append((char) cha);
                                }

                                SubLevelChangingButton b = new SubLevelChangingButton(w,h,posX,posY,texture.toString(),messageName.toString(),"#"+i, subLvl.toString(), "");
                                objectsBuffer.add(b);
                            }

                            case 'K' -> {//KeyChangingButton
                                int w = reader.read()*256 + reader.read();
                                int h = reader.read()*256 + reader.read();
                                int posX = reader.read()*256 + reader.read();
                                int posY = reader.read()*256 + reader.read();

                                StringBuilder keyName = new StringBuilder();
                                while ((cha = reader.read()) != 59) { //59 == ';'
                                    keyName.append((char) cha);
                                }

                                StringBuilder messageName = new StringBuilder();
                                while ((cha = reader.read()) != 59) { //59 == ';'
                                    messageName.append((char) cha);
                                }

                                StringBuilder texture = new StringBuilder();
                                while ((cha = reader.read()) != 10) { // 10 == '\n'
                                    texture.append((char) cha);
                                }

                                KeyChangingButton b = new KeyChangingButton(w,h,posX,posY,texture.toString(),messageName.toString(),"#"+i, keyName.toString(), "");
                                objectsBuffer.add(b);
                            }
                        }
                    }
                    case 'I' -> { //image
                        int w = reader.read()*256 + reader.read();
                        int h = reader.read()*256 + reader.read();
                        int posX = reader.read()*256 + reader.read();
                        int posY = reader.read()*256 + reader.read();

                        int cha;

                        StringBuilder texture = new StringBuilder();
                        while ((cha = reader.read()) != 10) { // 10 == '\n'
                            texture.append((char) cha);
                        }

                        Image b = new Image(w,h,posX,posY,texture.toString(),"#"+i, "");
                        objectsBuffer.add(b);
                    }

                    case 'L' ->{ //subLevel

                        boolean isDisplayed = reader.read() == 'T';
                        boolean isUpdated = reader.read() == 'T';

                        int cha;
                        StringBuilder name = new StringBuilder();
                        while ((cha = reader.read()) != 10) { //10 == '\n'
                            name.append((char) cha);
                        }

                        SubLevel subLvl = new SubLevel(name.toString(), objectsBuffer);

                        subLvl.isDisplayed = isDisplayed;
                        subLvl.isUpdated = isUpdated;
                        subLevels.add(subLvl);

                        objectsBuffer = new ArrayList<>();
                    }
                    case 'F'->{ //file

                        int cha;
                        StringBuilder lvlName = new StringBuilder();
                        while ((cha = reader.read()) != 10) { //10 == '\n'
                            lvlName.append((char) cha);
                        }

                        loadLevel(lvlName.toString());

                    }
                }
            }

            reader.close();

        } catch (IOException | FontFormatException e) {
            throw new RuntimeException(e);
        }

        forceUpdate = true;
    }

    public void setSubLvlDisplay(String subLevelName, Boolean doDisplay){
        for (SubLevel subLvl : subLevels) {
            if (subLvl.getName().equals(subLevelName)){
                subLvl.isDisplayed = doDisplay;
            }
        }
    }

    public void setSubLvlUpdate(String subLevelName, Boolean doUpdate){
        for (SubLevel subLvl : subLevels) {
            if (subLvl.getName().equals(subLevelName)){
                subLvl.isUpdated = doUpdate;
            }
        }
    }

    public SubLevel getSubLvl(String subLvlName){
        for (SubLevel lvl: subLevels) {
            if (lvl.name.equals(subLvlName)){
                return lvl;
            }
        }
        return null;
    }

    public String getLastSubLvlName(){
        if(subLvlQueue.isEmpty()){
            return "main";
        }else{
            return subLvlQueue.get(subLvlQueue.size()-1);
        }
    }

    public void subLevelBackHandler(){
        if (subLvlQueue.isEmpty()){
            openSubLevel("pause",false, true);
        }
        else{
            String subLvlName = subLvlQueue.remove(subLvlQueue.size()-1);
            setSubLvlUpdate(subLvlName, false);
            setSubLvlDisplay(subLvlName, false);
            if (subLvlQueue.isEmpty()){
                setSubLvlUpdate("main", true);
            }else{
                setSubLvlUpdate(subLvlQueue.get(subLvlQueue.size()-1), true);
                setSubLvlDisplay(subLvlQueue.get(subLvlQueue.size()-1), true);
            }
        }

        forceUpdate = true;
    }

    public void openSubLevel(String subLvlName, boolean doLastLvlUpdate, boolean doLastLvlDisplay){
        if (subLvlName.equals("back")){
            subLevelBackHandler();
            return;
        }
        if (! subLvlExists(subLvlName)){
            return;
        }

        String prevSubLvlName;
        if (subLvlQueue.isEmpty()){
            prevSubLvlName = "main";
        }else{
            prevSubLvlName = subLvlQueue.get(subLvlQueue.size()-1);
        }
        setSubLvlDisplay(prevSubLvlName, doLastLvlDisplay);
        setSubLvlUpdate(prevSubLvlName, doLastLvlUpdate);
        setSubLvlUpdate(subLvlName, true);
        setSubLvlDisplay(subLvlName, true);
        subLvlQueue.add(subLvlName);

        forceUpdate = true;
    }

    public boolean subLvlExists(String subLvlName){
        for (SubLevel s: subLevels) {
            if (s.name.equals(subLvlName)){
                return true;
            }
        }
        return false;
    }

    public void addSubLvl(SubLevel subLvl){
        subLevels.add(subLvl);
    }

    public void deleteSubLvl(SubLevel subLevel){
        subLevels.remove(subLevel);
    }

    public void addUpdatable(GameObject2D go){
        if (getSubLvl(go.subLevelName).isUpdated){
            updatable.add(go);
        }
    }

    public ArrayList<GameObject2D> getUpdatable(){
        return updatable;
    }

    public void clearUpdatable(){
        updatable.clear();
    }

    public boolean hasNoPlayer(){
        for (GameObject2D go: getDisplayedObjects()) {
            if (go.type.equals("Player")){
                return false;
            }
        }
        return true;
    }
}
