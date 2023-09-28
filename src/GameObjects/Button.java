package GameObjects;

import handlers.KeyHandler;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public class Button extends GameObject2D{

    boolean focused;
    boolean key_focused;
    boolean triggered;

    Font buttonFont;
    String buttonFontName;
    String buttonMessage;

    int buttonFontSize;
    int buttonMessageX;
    int buttonMessageY;

    Color buttonMessageColor;


    public Button(int w, int h, int x, int y, String textureName, String message, String id, String subLvlName) throws IOException, FontFormatException {
        super(x-w/2,y-h/2,w,h,subLvlName);

        type = "Button_" + textureName;
        name = type+id;

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(ImageIO.read(new File("assets/Button/"+textureName+"/unfocused/0.png")), hitbox);

        buttonFontName = "Eight Bit Dragon";
        buttonFontSize = 100;
        buttonMessage = message;

        buttonMessageColor = new Color(199, 219, 237);

        calibrateMessage(10);
    }

    public boolean isFocused(){return focused;}

    public boolean isKey_focused(){return key_focused;}

    public void setKey_focused(boolean focused){key_focused = focused;}

    public boolean isTriggered(){return triggered;}

    @Override
    public void update() throws IOException, FontFormatException {

        if (key_focused){

            if (KeyHandler.isSelectPressed){
                triggerHandler();
            }
            else if (isTriggered()){
                releasedHandler();
            }
            else if (!isFocused()){
                focusHandler();
            }


        } else if (isFocused() || isTriggered()){
            unfocusedHandler();
        }
    }

    public void draw(Graphics2D g2D, ImageObserver imageObserver){
        super.draw(g2D, imageObserver);

        //draw message
        g2D.setFont(buttonFont);
        g2D.setColor(buttonMessageColor);
        g2D.drawString(buttonMessage, buttonMessageX, buttonMessageY);


    }

    void unfocusedHandler() throws  IOException{
        sprite = new Sprite(ImageIO.read(new File("assets/Button/"+type.substring(7)+"/unfocused/0.png")), hitbox);
        focused = false;
        triggered = false;

        calibrateMessage(15);
    }

    void focusHandler() throws IOException {
        sprite = new Sprite(ImageIO.read(new File("assets/Button/"+type.substring(7)+"/focused/0.png")), hitbox);
        focused = true;

        calibrateMessage(30);
    }

    void triggerHandler() throws IOException, FontFormatException {
        sprite = new Sprite(ImageIO.read(new File("assets/Button/"+type.substring(7)+"/clicked/0.png")), hitbox);
        triggered = true;

        calibrateMessage(50);
    }

    void releasedHandler() throws IOException, FontFormatException {
        //is overwritten
        triggered = false;
    }

    void calibrateMessage(int sizeDiff){

        buttonFontSize = getWidth();

        for (int i = 0; i < getWidth(); i++) {
            Font font = new Font(buttonFontName, Font.PLAIN, buttonFontSize);
            int testWidth = GamePanel.getGamePannel().getFontMetrics(font).stringWidth(buttonMessage);

            if (testWidth > getWidth() - sizeDiff) {
                buttonFontSize--;
            } else {
                break;
            }
        }

        buttonFont = new Font(buttonFontName, Font.PLAIN, buttonFontSize);

        buttonMessageX = getX() + getWidth()/2 - GamePanel.getGamePannel().getFontMetrics(buttonFont).stringWidth(buttonMessage)/2;
        buttonMessageY = getY() + getHeight()/2 + GamePanel.getGamePannel().getFontMetrics(buttonFont).getHeight()/2;
    }

    @Override
    public String getDebugInfos(){
        return super.getDebugInfos() + ",isFocused:" + isFocused() + ",isTriggered" + isTriggered();
    }

    @Override
    public Button getButton(){
        return this;
    }
}
