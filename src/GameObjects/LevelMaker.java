package GameObjects;

import handlers.KeyHandler;
import handlers.MouseHandler;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class LevelMaker extends GameObject2D{
    //A Game object, that performs more as a handler.
    //It allows for the creation of levels inside the game,
    //WARNING: currently it can only be used in the main class

    ArrayList<GameObject2D> objects = new ArrayList<>();
    int id_counter = 0;

    boolean hasPlacedObject = false;
    boolean isLevelLaunched = false;


    LevelMaker() throws IOException {
        super(0, 0, 0, 0, "");

        type = "ScoreDisplay_";
        name = type + "0";

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(ImageIO.read(new File("assets/placeholder.png")), hitbox);
    }

    @Override
    public void update() throws IOException, FontFormatException {

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

        //place objects
        if (MouseHandler.isLeftClickPressed && !hasPlacedObject){
            hasPlacedObject = true;
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
            else if (KeyHandler.isUpPressed) {
                Platform p = new Platform(100, 100,
                        GamePanel.camera.x + MouseHandler.getX(), GamePanel.camera.y + MouseHandler.getY(),
                        "neon_green", "#"+id_counter, "");
                GamePanel.camera.level.addToMainSubLevel(p);
                objects.add(p);
            }
            //checkpoint
            else if (KeyHandler.isDownPressed) {
                CheckPoint c = new CheckPoint(GamePanel.camera.x + MouseHandler.getX(),
                        GamePanel.camera.y + MouseHandler.getY(), "#"+id_counter, "");
                GamePanel.camera.level.addToMainSubLevel(c);
                objects.add(c);
            }
            //killer
            else if (KeyHandler.isLeftPressed) {
                Platform p = new Platform(100, 100,
                        GamePanel.camera.x + MouseHandler.getX(), GamePanel.camera.y + MouseHandler.getY(),
                        "killer", "#"+id_counter, "");
                GamePanel.camera.level.addToMainSubLevel(p);
                objects.add(p);
            }
            //win
            else if (KeyHandler.isRightPressed){
                Platform p = new Platform(100, 100,
                        GamePanel.camera.x + MouseHandler.getX(), GamePanel.camera.y + MouseHandler.getY(),
                        "win", "#"+id_counter, "");
                GamePanel.camera.level.addToMainSubLevel(p);
                objects.add(p);
            }

        }
        else if (!MouseHandler.isLeftClickPressed && hasPlacedObject){
            hasPlacedObject = false;
        }

        //Launch Level
        if (KeyHandler.isLaunchKeyPressed){

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
}
