package GameObjects;

import com.ibm.jvm.trace.format.api.Message;
import handlers.KeyHandler;
import handlers.MouseHandler;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

public class LevelMaker extends GameObject2D{
    //A Game object, that performs more as a handler.
    //It allows for the creation of levels inside the game,
    //WARNING: currently it can only be used in the main class

    public static boolean cameraCanMove = true;
    boolean canPlaceObj = true;

    ArrayList<GameObject2D> objects = new ArrayList<>();
    int id_counter = 2;

    boolean isLevelLaunched = false;
    DropDownMenu rightClickMenu;
    TextInputMenu txtInputMenu;

    ArrayList<Button> buttons = new ArrayList<>();

    int defaultObjWidth = 100;
    int defaultObjHeight = 100;

    String nextObjType = "";
    String nextObjTexture = "";


    LevelMaker() throws IOException, FontFormatException {
        super(0, 0, 0, 0, "");

        type = "ScoreDisplay_";
        name = type + "0";

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(ImageIO.read(new File("assets/placeholder.png")), hitbox);

        int buttonHeight = 75;
        rightClickMenu = new DropDownMenu(0,0, 150, 3*buttonHeight, "#0", "",
                new ArrayList<>(), new ArrayList<>());
        txtInputMenu = new TextInputMenu(GamePanel.camera.width/2, GamePanel.camera.height/2,
                "#1", "", new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        buttons.add(new Button(150, 150, 400, 100, "base", "Platform", "#2", ""));
        buttons.add(new Button(150, 150, 600, 100, "base", "Checkpoint", "#3", ""));
        buttons.add(new Button(150, 150, 800, 100, "base", "Save", "#2", ""));
    }

    @Override
    public void update() throws IOException, FontFormatException {

        rightClickMenu.update();
        txtInputMenu.update();

        //if level is already launched
        if (isLevelLaunched){
            if (KeyHandler.isLaunchKeyPressed){
                KeyHandler.isLaunchKeyPressed = false;
                isLevelLaunched = false;
                GamePanel.camera.level.updateLevelMaker = true;

                //unload all loaded GO in level, and replace them with the saved ones
                Level lvl = GamePanel.camera.level;
                lvl.getSubLvl("main").objectList = new ArrayList<>();
                lvl.addToMainSubLevel(this);
                for (GameObject2D go : objects){
                    lvl.addToMainSubLevel(go);

                    if (Objects.equals(go.getType(), "Player")){
                        player = go.getThisPlayer();

                        GamePanel.camera.x = player.getX() - GamePanel.camera.width/2;
                        GamePanel.camera.y = player.getY() - GamePanel.camera.height/2;
                    }

                }

            }
            else return;
        }

        buttonLogic();

        //close opened tabs logic
        if (MouseHandler.isRightClickPressed || MouseHandler.isLeftClickPressed){
            if (rightClickMenu.isOpen &&
                    !rightClickMenu.pointIsIn(MouseHandler.getX(), MouseHandler.getY())){

                MouseHandler.resetClicks();
                rightClickMenu.activate();
                GamePanel.camera.noUpdate = false;
            }
            else if(txtInputMenu.isOpen &&
                    (!txtInputMenu.pointIsIn(MouseHandler.getX(), MouseHandler.getY()) &&
                     !txtInputMenu.pointIsOnButton(MouseHandler.getX(), MouseHandler.getY())) ){
                MouseHandler.resetClicks();
                txtInputMenu.isOpen = false;
                GamePanel.camera.noUpdate = false;
            }
        }
        //place objects
        if (MouseHandler.isLeftClickPressed && canPlaceObj){
            MouseHandler.resetClicks();
            id_counter += 1;
            //place objects

            //player
            if (hasNoPlayer()){
                Player p = new Player(GamePanel.camera.x + MouseHandler.getX(),
                        GamePanel.camera.y + MouseHandler.getY(),
                        "#0", "");
                GamePanel.camera.level.addToMainSubLevel(p);
                objects.add(p);
                player = p;
            }

            //platform
            else if (nextObjType.equals("Platform")) {
                Platform p = new Platform(defaultObjWidth, defaultObjHeight,
                        GamePanel.camera.x + MouseHandler.getX(), GamePanel.camera.y + MouseHandler.getY(),
                        nextObjTexture, "#"+id_counter, "");
                GamePanel.camera.level.addToMainSubLevel(p);
                objects.add(p);
            }
            //checkpoint
            else if (nextObjType.equals("Checkpoint")) {
                CheckPoint c = new CheckPoint(GamePanel.camera.x + MouseHandler.getX(),
                        GamePanel.camera.y + MouseHandler.getY(), "#"+id_counter, "");
                GamePanel.camera.level.addToMainSubLevel(c);
                objects.add(c);
            }

        }

        //edit placed objects
        if (MouseHandler.isRightClickPressed && !rightClickMenu.isOpen){

            int mouseX = MouseHandler.getX();
            int mouseY = MouseHandler.getY();

            for (GameObject2D go : objects){

                if (go.pointIsIn(mouseX, mouseY)){

                    Function<Void, Void> delete =
                            unused -> {
                                objects.remove(go);
                                GamePanel.camera.level.getSubLvl("main").objectList.remove(go);
                                GamePanel.camera.updateGrid();

                                rightClickMenu.activate();
                                GamePanel.camera.noUpdate = false;

                                MouseHandler.resetClicks();

                                return unused;
                            };

                    Function<Void, Void> resize =
                            unused -> {
                                txtInputMenu.setCategoryNames(Arrays.asList("Width: ", "Height: "));
                                txtInputMenu.setDefaultValues(Arrays.asList(String.valueOf(go.getWidth()), String.valueOf(go.getHeight())));
                                txtInputMenu.setCategorySetValues(Arrays.asList(
                                        i -> {go.setWidth(i); go.sprite.setWidth(i); defaultObjWidth = i; return null;},
                                        i -> {go.setHeight(i); go.sprite.setHeight(i); defaultObjHeight = i; return null;}));

                                txtInputMenu.isOpen = true;
                                rightClickMenu.activate();

                                MouseHandler.resetClicks();

                                return unused;
                            };

                    Function<Void, Void> move =
                            unused -> {
                                txtInputMenu.setCategoryNames(Arrays.asList("Position X: ", "Position Y: "));
                                txtInputMenu.setDefaultValues(Arrays.asList(String.valueOf(go.getX()), String.valueOf(go.getY())));
                                txtInputMenu.setCategorySetValues(Arrays.asList(
                                        i -> {go.setX(i); if (go.name.contains("Player")) player.spawnPointPos[0] = i; return null;},
                                        i -> {go.setY(i); if (go.name.contains("Player")) player.spawnPointPos[1] = i; return null;}));

                                txtInputMenu.isOpen = true;
                                rightClickMenu.activate();

                                MouseHandler.resetClicks();

                                return unused;
                            };

                    rightClickMenu.setX(mouseX);
                    rightClickMenu.setY(mouseY);
                    rightClickMenu.setDisplayedWidth(150);

                    if (go.type.contains("Player")){
                        rightClickMenu.setButtonText(List.of("Move"));
                        rightClickMenu.setButtonExec(List.of(move));

                        rightClickMenu.setHeight(rightClickMenu.buttonHeight);
                    }

                    else if (go.type.contains("Checkpoint")){
                        rightClickMenu.setButtonText(Arrays.asList("Delete", "Move"));
                        rightClickMenu.setButtonExec(Arrays.asList(delete, move));

                        rightClickMenu.setHeight((rightClickMenu.buttonHeight+10)*2 - 10);
                    }
                    else{
                        rightClickMenu.setButtonText(Arrays.asList("Delete", "Resize", "Move"));
                        rightClickMenu.setButtonExec(Arrays.asList(delete, resize, move));

                        rightClickMenu.setHeight((rightClickMenu.buttonHeight+10)*3 - 10);
                    }

                    rightClickMenu.activate();

                    GamePanel.camera.update();
                    GamePanel.camera.noUpdate = true;
                    break;
                }
            }
        }

        //Launch Level
        if (KeyHandler.isLaunchKeyPressed && !hasNoPlayer()){

            //save every object in its actual state
            ArrayList<GameObject2D> saveObj = new ArrayList<>();
            for (GameObject2D go : objects){
                saveObj.add(go.copy());
            }
            objects = saveObj;

            KeyHandler.isLaunchKeyPressed = false;
            GamePanel.camera.level.updateLevelMaker = false;
            isLevelLaunched = true;
        }
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {

        if (!isLevelLaunched){
            for (Button b : buttons){
                b.draw(g2D, IO);
            }
        }

        rightClickMenu.draw(g2D, IO);
        txtInputMenu.draw(g2D, IO);
    }

    void buttonLogic() throws IOException, FontFormatException {

        cameraCanMove = true;
        canPlaceObj = true;

        for (Button b : buttons){
            if (b.pointIsIn(MouseHandler.getX(), MouseHandler.getY())){

                cameraCanMove = false;
                canPlaceObj = false;
                if (!b.isTriggered()) b.focusHandler();

                if (MouseHandler.isLeftClickPressed){

                    MouseHandler.resetClicks();

                    //un-trigger all buttons
                    for (Button b2: buttons) {
                        if (b2.isTriggered()) {
                            b2.unfocusedHandler();
                        }
                    }

                    b.triggerHandler();

                    nextObjType = b.buttonMessage;

                    if (nextObjType.equals("Platform")) {

                        nextObjTexture = "neon_green";

                        rightClickMenu.setX(MouseHandler.getX());
                        rightClickMenu.setY(MouseHandler.getY());

                        rightClickMenu.setButtonText(List.of("win", "killer", "neon_red", "neon_yellow", "neon_green"));
                        rightClickMenu.setButtonExec(Arrays.asList(
                                unused -> {nextObjTexture = "win"; rightClickMenu.activate();
                                    MouseHandler.resetClicks(); GamePanel.camera.noUpdate = false; return unused;},

                                unused -> {nextObjTexture = "killer"; rightClickMenu.activate();
                                    MouseHandler.resetClicks(); GamePanel.camera.noUpdate = false; return unused;},

                                unused -> {nextObjTexture = "neon_red"; rightClickMenu.activate();
                                    MouseHandler.resetClicks(); GamePanel.camera.noUpdate = false; return unused;},

                                unused -> {nextObjTexture = "neon_yellow"; rightClickMenu.activate();
                                    MouseHandler.resetClicks(); GamePanel.camera.noUpdate = false; return unused;},

                                unused -> {nextObjTexture = "neon_green"; rightClickMenu.activate();
                                    MouseHandler.resetClicks(); GamePanel.camera.noUpdate = false; return unused;}
                        ));

                        rightClickMenu.setDisplayedWidth(500);
                        rightClickMenu.setHeight((rightClickMenu.buttonHeight+10)*5 - 10);

                        if (!rightClickMenu.isOpen){
                            rightClickMenu.activate();
                        }

                        GamePanel.camera.update();
                        GamePanel.camera.noUpdate = true;

                    }

                    else if (nextObjType.equals("Save")) saveLevel("test");
                }

                break;

            } else if (b.isFocused() && !b.isTriggered()){
                b.unfocusedHandler();
            }
        }
    }

    public void saveLevel(String lvlName) {
        try{

            File level = new File("assets/level/"+lvlName+".lvl");
            level.createNewFile();

            FileOutputStream fw = new FileOutputStream(level, false);

            //header
            fw.write("GameIce->VB0.3\nT\n".getBytes());

            //body
            for (GameObject2D go : objects){
                if (go.getType().contains("Player")){
                    fw.write("J".getBytes());
                    fw.write((go.getX() + 32767)/256);
                    fw.write((go.getX() + 32767)%256);
                    fw.write((go.getY() + 32767)/256);
                    fw.write((go.getY() + 32767)%256);
                    fw.write("\n".getBytes());
                }
                else if (go.getType().contains("Platform_")){
                    fw.write("P".getBytes());
                    fw.write(go.getWidth()/256);
                    fw.write(go.getWidth()%256);
                    fw.write(go.getHeight()/256);
                    fw.write(go.getHeight()%256);
                    fw.write((go.getX() + 32767)/256);
                    fw.write((go.getX() + 32767)%256);
                    fw.write((go.getY() + 32767)/256);
                    fw.write((go.getY() + 32767)%256);
                    fw.write((go.getType().substring(9) + "\n").getBytes());
                }
                else if (go.getType().contains("Checkpoint")){
                    fw.write("C".getBytes());
                    fw.write((go.getX() + 32767)/256);
                    fw.write((go.getX() + 32767)%256);
                    fw.write((go.getY() + 32767)/256);
                    fw.write((go.getY() + 32767)%256);
                    fw.write("\n".getBytes());
                }
            }

            //endss
            fw.write("LTTmain\nI".getBytes());
            for (int i = 0; i < 8; i++) fw.write(0);
            fw.write("settingsBg\nBL".getBytes());
            fw.write(307/256);
            fw.write(307%256);
            fw.write(150/256);
            fw.write(150%256);
            fw.write(500/256);
            fw.write(500%256);
            fw.write(400/256);
            fw.write(400%256);
            fw.write((lvlName + ";Reset;base\nFsettings\nFwin\n").getBytes());

            fw.close();
            System.out.println("File /assets/level/"+lvlName+".lvl has been successfully saved" );

            File highScores = new File("assets/HighScores/"+lvlName+".highscore");
            highScores.createNewFile();
        }
        catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
