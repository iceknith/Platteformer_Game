package GameObjects;

import handlers.MouseHandler;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Button extends GameObject2D{

    boolean focused;
    boolean triggered;

    Font buttonFont;
    String buttonFontName;
    String buttonMessage;

    int buttonFontSize;
    int buttonMessageX;
    int buttonMessageY;

    Color buttonMessageColor;


    public Button(int w, int h, int x, int y, String textureName, String message, String id, String subLvlName) throws IOException, FontFormatException {
        super(x,y,w,h,subLvlName);

        type = "Button_" + textureName;
        name = type+id;

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(ImageIO.read(new File("assets/Button/"+textureName+"/unfocused/0.png")), hitbox);

        //text
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("assets/font/EightBitDragon-anqx.ttf")));

        buttonFontName = "Eight Bit Dragon";
        buttonFontSize = 100;
        buttonMessage = message;

        buttonMessageColor = new Color(199, 219, 237);

        calibrateMessage(10);
    }

    boolean pointIsOver(int x, int y){
        return x > getX() && x < getX() + getWidth() && y > getY() && y < getY() + getHeight();
    }

    public boolean isFocused(){return focused;}

    public boolean isTriggered(){return triggered;}

    @Override
    public void update() throws IOException, FontFormatException {

        if (pointIsOver(MouseHandler.getX(), MouseHandler.getY())){

            if (MouseHandler.isRightClickPressed || MouseHandler.isLeftClickPressed){
                triggerHandler();
            }
            else{
                if (isTriggered()){
                    releasedHandler();
                }else{
                    focusHandler();
                }
            }

        } else{
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

    void releasedHandler() throws FileNotFoundException {
        //is overwritten
        triggered = false;
    }

    void calibrateMessage(int sizeDiff){
        int textWidth = GamePanel.getGamePannel().getFontMetrics(new Font(buttonFontName, Font.PLAIN, buttonFontSize)).stringWidth(buttonMessage);
        buttonFontSize = (int) ((getWidth()-sizeDiff) / ((float) textWidth/buttonFontSize));

        buttonFont = new Font(buttonFontName, Font.PLAIN, buttonFontSize);

        buttonMessageX = getX() + sizeDiff/2;
        buttonMessageY = getY() + getHeight()/2 + buttonFontSize/2;
    }

    @Override
    public String getDebugInfos(){
        return super.getDebugInfos() + ",isFocused:" + isFocused() + ",isTriggered" + isTriggered();
    }
}
