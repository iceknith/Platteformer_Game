package GameObjects;

import GameObjects.Enemy.*;
import handlers.KeyHandler;
import main.GamePanel;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Objects;

public class Level {

    String levelName;
    ArrayList<SubLevel> subLevels = new ArrayList<>();
    ArrayList<String> subLvlQueue = new ArrayList<>();
    ArrayList<GameObject2D> updatable = new ArrayList<>();
    ArrayList<GameObject2D> updatableBuffer = new ArrayList<>();
    ArrayList<GameObject2D> permanentUpdatable = new ArrayList<>();
    ArrayList<GameObject2D> permanentUpdatableAddBuffer = new ArrayList<>();
    ArrayList<GameObject2D> permanentUpdatableRemoveBuffer = new ArrayList<>();
    ArrayList<Button> buttons = new ArrayList<>();

    final int permanentUpdatableRenderDistance = 5000;

    boolean noUpdatableModification;
    boolean wasMenuKeyPressed;
    boolean forceUpdate;
    boolean willUpdatableClear = false;
    boolean resetAll;

    //level maker
    boolean hasLevelMaker;
    boolean updateLevelMaker;
    LevelMaker lvlMaker;

    public LevelMaker getLvlMaker() {
        return lvlMaker;
    }

    public Level(){

    }

    public Level(String name) throws FileNotFoundException {
        levelName = name;
        loadLevel();
    }

    public Level(String name, LevelMaker levelMaker, boolean onlyMain) throws Exception {
        levelName = name;

        hasLevelMaker = true;
        updateLevelMaker = true;
        lvlMaker = levelMaker;

        loadLevel(name, new ArrayList<>(), onlyMain);
        addToMainSubLevel(levelMaker);
    }

    public String getLevelName(){
        return levelName;
    }

    public ArrayList<GameObject2D> getPermaDisplayed() {
        ArrayList<GameObject2D> displayedGUI = new ArrayList<>();
        for (SubLevel subLvl : subLevels) {
            if (subLvl.isDisplayed){
                displayedGUI.addAll(subLvl.permaDisplayedObjects);
            }
        }
        return displayedGUI;
    }

    public void update() throws Exception {

        if(KeyHandler.isMenuKeyPressed && ! wasMenuKeyPressed){
            subLevelBackHandler();
            wasMenuKeyPressed = true;
        }

        else if(!KeyHandler.isMenuKeyPressed){
            wasMenuKeyPressed = false;
        }

        if (forceUpdate){
            GamePanel.camera.updateGrid();
            forceUpdate = false;
        }

        if(updateLevelMaker){
            lvlMaker.update();
            return;
        }

        noUpdatableModification = true;

        for (GameObject2D go: updatable) {
            go.update();
        }
        for (GameObject2D go: permanentUpdatable){
            if (getSubLvl(go.subLevelName).isUpdated &&
                    (GameObject2D.getPlayer().getDistance(go) <= permanentUpdatableRenderDistance ||
                            go.getType().contains("MovingPlatform_"))){
                go.update();
            }
        }
        for (SubLevel sl: subLevels){
            if (sl.isUpdated){
                sl.update();
            }
        }

        noUpdatableModification = false;

        //buffered actions
        if (willUpdatableClear) {
            updatable.clear();
            willUpdatableClear = false;
        }

        updatable.addAll(updatableBuffer);
        updatableBuffer.clear();

        permanentUpdatable.removeAll(permanentUpdatableRemoveBuffer);
        permanentUpdatableRemoveBuffer.clear();
        for (GameObject2D go: permanentUpdatableAddBuffer){
            if (!permanentUpdatable.contains(go)){
                permanentUpdatable.add(go);
            }
        }
        permanentUpdatableAddBuffer.clear();

        if (resetAll){
            resetAll = false;
            for (GameObject2D go : new ArrayList<>(GamePanel.camera.allGOInGrid)){
                go.reset();
            }
        }
    }

    public void loadLevel() throws FileNotFoundException {
        if (levelName.isEmpty()){
            throw new FileNotFoundException("No level selected");
        }
        hasLevelMaker = false;
        lvlMaker = null;
        loadLevel(levelName, new ArrayList<>(), false);
        updateLevelMaker = hasLevelMaker;
    }

    public ArrayList<GameObject2D> loadLevel(String level, ArrayList<GameObject2D> objBuffer, boolean loadOnlyMain){

        ArrayList<GameObject2D> objectsBuffer = objBuffer;

        //resetting high scores
        if (objBuffer.isEmpty()) HighScoresDisplay.resetHighScores();

        try {
            FileInputStream fis = new FileInputStream("assets/level/" + level + ".lvl");
            BufferedInputStream reader = new BufferedInputStream(fis);

            StringBuilder header = new StringBuilder();
            for (int i = 0; i < 14 ; i ++){
                header.append((char) reader.read());
            }
            //check if version is correct
            if (! header.toString().equals("GameIce->VB0.3")){
                throw new FileNotFoundException("File not recognized");
            }

            int ch;
            int i = 0;

            while ((ch = reader.read()) != -1) {
                i++;
                switch ((char) ch) {
                    case 'J' -> { //Player
                        int x = reader.read()*256 + reader.read() - 32767;
                        int y = reader.read()*256 + reader.read() - 32767;

                        GameObject2D.setPlayer(new Player(x, y,"#" + i, ""));
                        objectsBuffer.add(GameObject2D.getPlayer());
                        GamePanel.camera.setScreenX(x - GamePanel.camera.getScreenWidth()/2);
                        GamePanel.camera.setScreenY(y - GamePanel.camera.getScreenHeight()/2);
                    }
                    case 'T' -> { //InGameTimer
                        InGameTimer t = new InGameTimer("");
                        objectsBuffer.add(t);
                    }
                    case 'P' -> { //Platform
                        int w = reader.read()*256 + reader.read();
                        int h = reader.read()*256 + reader.read();
                        int posX = reader.read()*256 + reader.read() - 32767;
                        int posY = reader.read()*256 + reader.read() - 32767;

                        char uType = (char) reader.read();
                        int frameCount = reader.read();
                        int direction = reader.read() - 2;

                        StringBuilder texture = new StringBuilder();
                        int cha;
                        while ((cha = reader.read()) != 10) {
                            texture.append((char) cha);
                        }

                        Platform p = new Platform(w, h, posX, posY, uType, texture.toString(), frameCount,"#" + i, "");
                        p.setDirection(direction);
                        objectsBuffer.add(p);
                    }
                    case 'm' -> { //Moving Platform
                        int w = reader.read()*256 + reader.read();
                        int h = reader.read()*256 + reader.read();
                        int posX1 = reader.read()*256 + reader.read() - 32767;
                        int posY1 = reader.read()*256 + reader.read() - 32767;
                        int posX2 = reader.read()*256 + reader.read() - 32767;
                        int posY2 = reader.read()*256 + reader.read() - 32767;
                        int speed = reader.read()*256 + reader.read();
                        int initTime = reader.read()*256 + reader.read() - 32767;

                        char uType = (char) reader.read();

                        int frameCount = reader.read();
                        int direction = reader.read() - 2;

                        StringBuilder texture = new StringBuilder();
                        int cha;
                        while ((cha = reader.read()) != 10) {
                            texture.append((char) cha);
                        }

                        MovingPlatform m = new MovingPlatform(posX1, posY1, posX2, posY2, w, h, speed, initTime, uType, texture.toString(), frameCount,"#" + i, "");
                        m.setDirection(direction);
                        objectsBuffer.add(m);
                    }
                    case 'h' -> { //Hyena
                        int x = reader.read()*256 + reader.read() - 32767;
                        int y = reader.read()*256 + reader.read() - 32767;
                        int direction = reader.read() - 2;

                        Hyena h = new Hyena(x, y, "#" + i, "");
                        h.setDirection(direction);
                        h.dropsKey = reader.read() == 1;
                        objectsBuffer.add(h);
                    }
                    case 'c' -> { //Chicken
                        int x = reader.read()*256 + reader.read() - 32767;
                        int y = reader.read()*256 + reader.read() - 32767;
                        int direction = reader.read() - 2;

                        Chicken c = new Chicken(x, y, "#" + i, "");
                        c.setDirection(direction);
                        c.dropsKey = reader.read() == 1;
                        objectsBuffer.add(c);
                    }
                    case 'k' -> { //Knight
                        int x = reader.read()*256 + reader.read() - 32767;
                        int y = reader.read()*256 + reader.read() - 32767;
                        int direction = reader.read() - 2;

                        Knight k = new Knight(x, y, "#" + i, "");
                        k.setDirection(direction);
                        k.dropsKey = reader.read() == 1;
                        objectsBuffer.add(k);
                    }
                    case 'a' -> { //DarkKnight
                        int x = reader.read()*256 + reader.read() - 32767;
                        int y = reader.read()*256 + reader.read() - 32767;
                        int direction = reader.read() - 2;

                        DarkKnight k = new DarkKnight(x, y, "#" + i, "");
                        k.setDirection(direction);
                        k.dropsKey = reader.read() == 1;
                        objectsBuffer.add(k);
                    }
                    case 'g' -> { //Magician
                        int x = reader.read()*256 + reader.read() - 32767;
                        int y = reader.read()*256 + reader.read() - 32767;
                        int direction = reader.read() - 2;

                        Magician m = new Magician(x, y, "#" + i, "");
                        m.setDirection(direction);
                        m.dropsKey = reader.read() == 1;
                        objectsBuffer.add(m);
                    }
                    case 's' -> { //SkeletalReaper
                        int x = reader.read()*256 + reader.read() - 32767;
                        int y = reader.read()*256 + reader.read() - 32767;
                        int direction = reader.read() - 2;

                        SkeletalReaper s = new SkeletalReaper(x, y, "#" + i, "");
                        s.setDirection(direction);
                        s.dropsKey = reader.read() == 1;
                        objectsBuffer.add(s);
                    }
                    case 'r' -> { //Dragon
                        int x = reader.read()*256 + reader.read() - 32767;
                        int y = reader.read()*256 + reader.read() - 32767;
                        int direction = reader.read() - 2;

                        Dragon d = new Dragon(x, y, "#" + i, "");
                        d.setDirection(direction);
                        d.dropsKey = reader.read() == 1;
                        objectsBuffer.add(d);
                    }
                    case 'i' -> { //GiantKnight
                        int x = reader.read()*256 + reader.read() - 32767;
                        int y = reader.read()*256 + reader.read() - 32767;
                        int direction = reader.read() - 2;

                        GiantKnight g = new GiantKnight(x, y, "#" + i, "");
                        g.setDirection(direction);
                        g.dropsKey = reader.read() == 1;
                        objectsBuffer.add(g);
                    }
                    case 'o' -> { //key
                        int x = reader.read()*256 + reader.read() - 32767;
                        int y = reader.read()*256 + reader.read() - 32767;

                        KeyObject k = new KeyObject(x, y, false, "#" + i, "");
                        objectsBuffer.add(k);
                    }
                    case 'd' -> { //Door
                        int w = reader.read()*256 + reader.read();
                        int h = reader.read()*256 + reader.read();
                        int posX = reader.read()*256 + reader.read() - 32767;
                        int posY = reader.read()*256 + reader.read() - 32767;

                        Door d = new Door(posX, posY, w, h, "#" + i, "");
                        objectsBuffer.add(d);
                    }
                    case 'O' -> { //ImageObject
                        int w = reader.read()*256 + reader.read();
                        int h = reader.read()*256 + reader.read();
                        int posX = reader.read()*256 + reader.read() - 32767;
                        int posY = reader.read()*256 + reader.read() - 32767;

                        int frameCount = reader.read();
                        int direction = reader.read() - 2;

                        StringBuilder texture = new StringBuilder();
                        int cha;
                        while ((cha = reader.read()) != 10) {
                            texture.append((char) cha);
                        }

                        ImageObject img = new ImageObject(w, h, posX, posY, texture.toString(), frameCount,"#" + i, "");
                        img.setDirection(direction);
                        objectsBuffer.add(img);
                    }
                    case 't' -> { //TextObject
                        int posX = reader.read()*256 + reader.read() - 32767;
                        int posY = reader.read()*256 + reader.read() - 32767;

                        int size = reader.read();
                        StringBuilder text = new StringBuilder();
                        int cha;
                        while ((cha = reader.read()) != 10) {
                            text.append((char) cha);
                        }

                        TextObject t = new TextObject(posX, posY, text.toString(), size, "#" + i, "");
                        objBuffer.add(t);
                    }

                    case 'C' ->{ //Checkpoint
                        int posX = reader.read()*256 + reader.read() - 32767;
                        int posY = reader.read()*256 + reader.read() - 32767;
                        char uType = (char) reader.read();
                        int direction = reader.read() - 2;

                        CheckPoint c = new CheckPoint(posX, posY, uType, "#" + i, "");
                        c.setDirection(direction);
                        objectsBuffer.add(c);
                    }
                    case 'G' -> { //Snowflake generator
                        int posX = reader.read()*256 + reader.read() - 32767;
                        int posY = reader.read()*256 + reader.read() - 32767;
                        int snowflakeCnt = reader.read();
                        int direction = reader.read() - 2;

                        SnowflakeGenerator s = new SnowflakeGenerator(posX, posY, snowflakeCnt, "#" + i, "");
                        s.setDirection(direction);
                        objectsBuffer.add(s);
                    }

                    case 'B' ->{ //Button
                        int cha;

                        switch ((char) reader.read()){

                            case 'L' -> { //levelChangingButton
                                int w = reader.read()*256 + reader.read();
                                int h = reader.read()*256 + reader.read();
                                int posX = GamePanel.camera.screenWidth * (reader.read()*256 + reader.read())/1000;
                                int posY = GamePanel.camera.screenHeight * (reader.read()*256 + reader.read())/1000;

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
                                buttons.add(b);
                            }

                            case 'S' -> { //subLevelChangingButton
                                int w = reader.read()*256 + reader.read();
                                int h = reader.read()*256 + reader.read();
                                int posX = GamePanel.camera.screenWidth * (reader.read()*256 + reader.read())/1000;
                                int posY = GamePanel.camera.screenHeight * (reader.read()*256 + reader.read())/1000;

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
                                buttons.add(b);
                            }

                            case 'K' -> {//KeyChangingButton
                                int w = reader.read()*256 + reader.read();
                                int h = reader.read()*256 + reader.read();
                                int posX = GamePanel.camera.screenWidth * (reader.read()*256 + reader.read())/1000;
                                int posY = GamePanel.camera.screenHeight * (reader.read()*256 + reader.read())/1000;

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
                                buttons.add(b);
                            }
                            case 'E' -> { //ExitButton
                                int w = reader.read()*256 + reader.read();
                                int h = reader.read()*256 + reader.read();
                                int posX = GamePanel.camera.screenWidth * (reader.read()*256 + reader.read())/1000;
                                int posY = GamePanel.camera.screenHeight * (reader.read()*256 + reader.read())/1000;

                                StringBuilder messageName = new StringBuilder();
                                while ((cha = reader.read()) != 59) { //59 == ';'
                                    messageName.append((char) cha);
                                }

                                StringBuilder texture = new StringBuilder();
                                while ((cha = reader.read()) != 10) { // 10 == '\n'
                                    texture.append((char) cha);
                                }

                                ExitButton b = new ExitButton(w,h,posX,posY, texture.toString(), messageName.toString(), "#"+i, "");
                                objectsBuffer.add(b);
                                buttons.add(b);
                            }
                        }
                    }

                    case 'D' -> { //drop down menu
                        int w = reader.read()*256 + reader.read();
                        int h = reader.read()*256 + reader.read();
                        int posX = GamePanel.camera.screenWidth * (reader.read()*256 + reader.read())/1000;
                        int posY = GamePanel.camera.screenHeight * (reader.read()*256 + reader.read())/1000;

                        DropDownMenu d = new DropDownMenu(posX, posY, w, h, "#" + i, "",
                                new ArrayList<>(), new ArrayList<>());
                        objectsBuffer.add(d);
                    }

                    case 'A' -> { //Background (arriere plan)
                        int w = reader.read()*256 + reader.read();
                        int h = reader.read()*256 + reader.read();

                        int posX = reader.read()*256 + reader.read() - 32767;
                        int posY = reader.read()*256 + reader.read() - 32767;

                        float zoomAmount = (float) (reader.read() * 256 + reader.read()) / 100;
                        float scrollSlowness = (float) (reader.read() * 256 + reader.read()) / 100;
                        int r = reader.read();
                        boolean isInfiniteX = r == 1;
                        boolean isInfiniteY = reader.read() == 1;

                        int cha;
                        StringBuilder texture = new StringBuilder();
                        while ((cha = reader.read()) != 10) { // 10 == '\n'
                            texture.append((char) cha);
                        }

                        Background bg = new Background(posX, posY, w, h, zoomAmount, scrollSlowness,
                                isInfiniteX, isInfiniteY, texture.toString(),"#" + i, "");
                        objectsBuffer.add(bg);
                    }

                    case 'I' -> { //image
                        int w = reader.read()*256 + reader.read();
                        int h = reader.read()*256 + reader.read();
                        int posX = GamePanel.camera.screenWidth * (reader.read()*256 + reader.read())/1000;
                        int posY = GamePanel.camera.screenHeight * (reader.read()*256 + reader.read())/1000;

                        if (w == 0 && h == 0){
                            w = GamePanel.camera.screenWidth;
                            h = GamePanel.camera.screenHeight;

                            posX += w/2;
                            posY += h/2;
                        }

                        int cha;

                        StringBuilder texture = new StringBuilder();
                        while ((cha = reader.read()) != 10) { // 10 == '\n'
                            texture.append((char) cha);
                        }

                        Image b = new Image(w,h,posX,posY,texture.toString(),"#"+i, "");
                        objectsBuffer.add(b);
                    }

                    case 'S' -> { //Score Display
                        int posX = GamePanel.camera.screenWidth * (reader.read()*256 + reader.read())/1000;
                        int posY = GamePanel.camera.screenHeight * (reader.read()*256 + reader.read())/1000;

                        ScoreDisplay s = new ScoreDisplay(posX, posY, "");
                        objectsBuffer.add(s);
                    }
                    case 'H' -> { //High Score Display
                        int posX = GamePanel.camera.screenWidth * (reader.read()*256 + reader.read())/1000;
                        int posY = GamePanel.camera.screenHeight * (reader.read()*256 + reader.read())/1000;

                        int cha;

                        StringBuilder lvl = new StringBuilder();
                        while ((cha = reader.read()) != 10) { // 10 == '\n'
                            lvl.append((char) cha);
                        }

                        HighScoresDisplay h;

                        if (lvl.isEmpty()) h = new HighScoresDisplay(posX, posY, "");
                        else h = new HighScoresDisplay(posX, posY, "", lvl.toString());
                        objectsBuffer.add(h);
                    }
                    case 'R' -> { //High Score Register
                        int posX = GamePanel.camera.screenWidth * (reader.read()*256 + reader.read())/1000;
                        int posY = GamePanel.camera.screenHeight * (reader.read()*256 + reader.read())/1000;

                        ScoreRegister r = new ScoreRegister(posX, posY, "");
                        objectsBuffer.add(r);
                    }
                    case 'M' -> { //level maker
                        LevelMaker l = new LevelMaker();

                        hasLevelMaker = true;
                        lvlMaker = l;

                        objectsBuffer.add(l);
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

                        if (loadOnlyMain) {
                            forceUpdate = true;
                            return objectsBuffer;
                        }
                    }
                    case 'F'->{ //file

                        int cha;
                        StringBuilder lvlName = new StringBuilder();
                        while ((cha = reader.read()) != 10) { //10 == '\n'
                            lvlName.append((char) cha);
                        }

                        objectsBuffer = loadLevel(lvlName.toString(), objectsBuffer, loadOnlyMain);
                    }
                }
            }

            reader.close();

        } catch (Exception e) {
            System.out.println("Error while loading level: assets/level/" + level + ".lvl");
            throw new RuntimeException(e);
        }

        forceUpdate = true;
        return objectsBuffer;
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
            if (updateLevelMaker) updateLevelMaker = false;
        }
        else{
            String subLvlName = subLvlQueue.remove(subLvlQueue.size()-1);
            setSubLvlUpdate(subLvlName, false);
            setSubLvlDisplay(subLvlName, false);
            if (subLvlQueue.isEmpty()){
                if (Objects.equals(subLvlName, "win")) GamePanel.camera.setNextLevel("menu");

                setSubLvlUpdate("main", true);
                setSubLvlDisplay("main", true);
                if (hasLevelMaker && !lvlMaker.isLevelLaunched) updateLevelMaker = true;
            }else{
                setSubLvlUpdate(subLvlQueue.get(subLvlQueue.size()-1), true);
                setSubLvlDisplay(subLvlQueue.get(subLvlQueue.size()-1), true);
            }
        }
        forceUpdate = true;
    }

    public void openSubLevel(@NotNull String subLvlName, boolean doLastLvlUpdate, boolean doLastLvlDisplay){
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

    public void addUpdatable(@NotNull GameObject2D go){
        if (getSubLvl(go.subLevelName).isUpdated){
            if (noUpdatableModification)
                updatableBuffer.add(go);
            else
                updatable.add(go);
        }
    }

    public void addToMainSubLevel(GameObject2D go) throws Exception {
        addToSubLevel(go, "main");
    }
    public void addToSubLevel(GameObject2D go, String subLvlName) throws Exception {
        SubLevel subLvl = getSubLvl(subLvlName);
        subLvl.addObject(go);
        if (subLvl.isUpdated){
            updatableBuffer.add(go);
        }
        if (subLvl.isDisplayed){
            GamePanel.camera.displayableBuffer.add(go);
        }
    }

    public ArrayList<GameObject2D> getUpdatable(){
        return updatable;
    }

    public void clearUpdatable(){
        if (noUpdatableModification) {
            willUpdatableClear = true;
        }
        else{
            updatable.clear();
        }
    }

    public boolean hasNoPlayer(){
        for (GameObject2D go: permanentUpdatable) {
            if (go.type.equals("Player")){
                return false;
            }
        }
        return true;
    }
}
