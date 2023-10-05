package GameObjects;

import handlers.KeyHandler;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.ArrayList;

import static GameObjects.HighScoresDisplay.globalHighScoresNames;
import static GameObjects.HighScoresDisplay.globalHighScoresTimes;

public class ScoreRegister extends GameObject2D{

    ScoreRegister(int posX, int posY, String subLvl) throws IOException, FontFormatException {
        super(300,100,posX,posY, subLvl);

        type = "InGameTimer_";
        name = type+"0";

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(ImageIO.read(new File("assets/placeholder.png")), hitbox);

        lineSize = 7;
        keyWidth = 100;
        keyHeight = 100;
        kX = GamePanel.camera.screenWidth /2 - (lineSize-1)*keyWidth/2;
        kY = GamePanel.camera.screenHeight /2;

        //adding letters to keyboard
        for (int i = 0; i < alphabet.length(); i++){
            if (i%lineSize == 0){
                keyboard.add(new ArrayList<>());
            }
            Button b = new Button(keyWidth, keyHeight,kX + keyWidth*(i%lineSize), kY + keyHeight*(i/lineSize),
                    "key", "  "+alphabet.charAt(i)+"  ", "#"+i, subLvl);
            //prevents visual glitch
            b.focusHandler();
            b.unfocusedHandler();
            keyboard.get(i/lineSize).add(b);
        }

        //adding symbols to keyboard

        Button b = new Button(keyWidth*2, keyHeight,
                kX + keyWidth*(alphabet.length()%lineSize) + keyWidth/2, kY + keyHeight*(alphabet.length()/lineSize),
                "key", "  Shift  ", "#Shift", subLvl);
        b.focusHandler();
        b.unfocusedHandler();
        keyboard.get(keyboard.size()-1).add(b);

        keyboard.add(new ArrayList<>());

        b = new Button(keyWidth*3, keyHeight,
                kX + keyWidth*((alphabet.length()+2)%lineSize) + keyWidth, kY + keyHeight*((alphabet.length()+2)/lineSize),
                "key", " Space ", "#Space", subLvl);
        b.focusHandler();
        b.unfocusedHandler();
        keyboard.get(keyboard.size()-1).add(b);

        b = new Button(keyWidth*2, keyHeight,
                kX + keyWidth*((alphabet.length()+5)%lineSize) + keyWidth/2, kY + keyHeight*((alphabet.length()+5)/lineSize),
                "key", " Delete ", "#Delete", subLvl);
        b.focusHandler();
        b.unfocusedHandler();
        keyboard.get(keyboard.size()-1).add(b);

        b = new Button(keyWidth*2, keyHeight,
                kX + keyWidth*((alphabet.length()+7)%lineSize) + keyWidth/2, kY + keyHeight*((alphabet.length()+7)/lineSize),
                "key", " Enter ", "#Enter", subLvl);
        b.focusHandler();
        b.unfocusedHandler();
        keyboard.get(keyboard.size()-1).add(b);

        focusedKeyX = 0;
        focusedKeyY = 0;
        keyboard.get(focusedKeyX).get(focusedKeyY).focusHandler();

        isLowerCase = true;
    }

    //keyboard variables
    String alphabet = "abcdefghijklmnopqrstuvwxyz";
    int lineSize;
    int kX;
    int kY;
    int keyWidth;
    int keyHeight;
    int focusedKeyX;
    int focusedKeyY;
    double lastMoveTime = 0;
    boolean isLowerCase;

    //other variables
    String playerName = "";
    boolean selectWasPressed = false;

    ArrayList<ArrayList<Button>> keyboard = new ArrayList<>();

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {

        for(ArrayList<Button> i : keyboard){
            for(Button b: i){
                b.draw(g2D, IO);
            }
        }

        //display name that is being edited
        g2D.setFont(new Font("Eight Bit Dragon", Font.PLAIN, 60));

        g2D.setColor(new Color(0f, 0f, 0f, .5f));
        g2D.fillRect(GamePanel.camera.screenWidth /2-400, GamePanel.camera.screenHeight /4-50-g2D.getFontMetrics().getHeight()/2, 800, 100);

        g2D.setColor(Color.white);
        g2D.drawRect(GamePanel.camera.screenWidth /2-400, GamePanel.camera.screenHeight /4-50-g2D.getFontMetrics().getHeight()/2, 800, 100);

        g2D.drawString(playerName, GamePanel.camera.screenWidth /2-g2D.getFontMetrics().stringWidth(playerName)/2, GamePanel.camera.screenHeight /4);

    }

    @Override
    public void update() throws IOException, FontFormatException {

        if ((KeyHandler.isUpPressed || KeyHandler.isDownPressed || KeyHandler.isRightPressed || KeyHandler.isLeftPressed)
                && System.nanoTime() - lastMoveTime >= 200000000){
            keyboard.get(focusedKeyX).get(focusedKeyY).unfocusedHandler();
            lastMoveTime = System.nanoTime();

            if (KeyHandler.isUpPressed){
                focusedKeyX = (focusedKeyX-1 + keyboard.size())%keyboard.size();
                focusedKeyY = Math.min(focusedKeyY, keyboard.get(focusedKeyX).size()-1);
            }
            if (KeyHandler.isDownPressed){
                focusedKeyX = (focusedKeyX+1)%keyboard.size();
                focusedKeyY = Math.min(focusedKeyY,keyboard.get(focusedKeyX).size()-1);
            }

            if (KeyHandler.isLeftPressed){
                focusedKeyY = (focusedKeyY-1 + keyboard.get(focusedKeyX).size())%keyboard.get(focusedKeyX).size();
            }

            if (KeyHandler.isRightPressed){
                focusedKeyY = (focusedKeyY+1)%keyboard.get(focusedKeyX).size();
            }


            keyboard.get(focusedKeyX).get(focusedKeyY).focusHandler();
        }

        if (KeyHandler.isSelectPressed){
            if (!selectWasPressed){
                selectWasPressed = true;

                switch (keyboard.get(focusedKeyX).get(focusedKeyY).buttonMessage){
                    case " Space " -> {
                        if (playerName.length() <= 14){
                            playerName += " ";
                        }
                    }
                    case "  Shift  " -> {
                        for (int i = 0; i < alphabet.length(); i++){
                            if (isLowerCase) {
                                keyboard.get(i / lineSize).get(i % lineSize).buttonMessage = keyboard.get(i / lineSize).get(i % lineSize).buttonMessage.toUpperCase();
                            }

                            else {
                                keyboard.get(i / lineSize).get(i % lineSize).buttonMessage = keyboard.get(i / lineSize).get(i % lineSize).buttonMessage.toLowerCase();
                            }
                        }
                        isLowerCase = !isLowerCase;
                    }
                    case " Enter " -> validate();

                    case " Delete " -> {
                        if (!playerName.isEmpty()){
                            playerName = playerName.substring(0,playerName.length()-1);
                        }
                    }
                    default -> {
                        if (playerName.length() <= 14){
                            playerName += keyboard.get(focusedKeyX).get(focusedKeyY).buttonMessage.charAt(2);
                        }
                    }
                }
            }
        }
        else{
            selectWasPressed = false;
        }
    }

    void validate() throws IOException {

        int j = 0;
        //add the score, so that the score list is still sorted
        while (j < globalHighScoresTimes.size()){
            if(GamePanel.inGameTimer < globalHighScoresTimes.get(j)){
                globalHighScoresTimes.add(j, GamePanel.inGameTimer);
                globalHighScoresNames.add(j, playerName);
                break;
            }
            j++;
        }
        if (j<50 && j == globalHighScoresTimes.size()){
            globalHighScoresTimes.add(GamePanel.inGameTimer);
            globalHighScoresNames.add(playerName);
        }

        //making sure that the lists aren't too big
        while (globalHighScoresTimes.size() >= 50){
            globalHighScoresTimes.remove(globalHighScoresTimes.size() - 1);
            globalHighScoresNames.remove(globalHighScoresNames.size() - 1);
        }

        //writing in the file
        File fnew = new File("assets/HighScores/"+GamePanel.camera.level.getLevelName()+".highscore");
        FileWriter fw = new FileWriter(fnew, false);
        PrintWriter pw = new PrintWriter(fw);

        for (int i = 0; i < globalHighScoresTimes.size(); i++){
            //fw.write(hsN.get(i)+"-"+hsT.get(i)+"\n");
            String txt = globalHighScoresNames.get(i)+"-"+ globalHighScoresTimes.get(i);
            pw.println(txt);
        }

        pw.close();
        KeyHandler.isSelectPressed = false;
        KeyHandler.isSelectPressed = false;
        GamePanel.camera.setNextLevel("menu");
    }

}
