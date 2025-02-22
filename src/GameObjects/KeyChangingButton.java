package GameObjects;

import handlers.KeyHandler;
import main.GamePanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;

public class KeyChangingButton extends Button{

    String buttonKey;
    String keyText;
    BufferedImage arcadeKeyImage;
    float arcadeButtonImageWidthRatio = 1.4f;
    boolean keyChanged = true;

    public KeyChangingButton(int w, int h, int x, int y, String textureName, String messageName, String id, String key, String subLvlName) throws IOException, FontFormatException {
        super(w, h, x, y, textureName, messageName, id, subLvlName);

        buttonKey = key;

        buttonFontSize = getHeight()/2;
        buttonFont = new Font(buttonFontName, Font.PLAIN, buttonFontSize);

        int textWidth = GamePanel.getGamePannel().getFontMetrics(new Font(buttonFontName, Font.PLAIN, buttonFontSize)).stringWidth(buttonMessage);

        buttonMessageX = getX() - textWidth - getWidth()/10;
        buttonMessageY = getY() + getHeight()/2 + buttonFontSize/2;
    }

    @Override
    void triggerHandler() throws IOException, FontFormatException {
        super.triggerHandler();

        keyChanged = true;
        KeyHandler.resetLastKeyPressed();

        //setting up key waiter
        int w = GamePanel.camera.getScreenWidth()/2;
        int h = GamePanel.camera.getScreenHeight()/2;

        SubLevel s = new SubLevel("key waiter");
        KeyWaitingButton waiter = new KeyWaitingButton(400,200, w, h, "keyWaiter", "Press any key", "None", buttonKey, s, "key waiter");
        s.permaDisplayedObjects.add(waiter);
        GamePanel.camera.level.addSubLvl(s);
        GamePanel.camera.level.openSubLevel("key waiter", false, true);
    }

    @Override
    public void update() throws Exception {
        if (keyChanged) {
            keyChanged = false;

            int key = KeyHandler.getKey(buttonKey);
            if (GamePanel.isArcadeVersion){
                arcadeKeyImage = readImageBuffered(("assets/Image/arcadeButtons/"+key+".png"));
            }
            else {
                keyText = KeyEvent.getKeyText(key);
            }
        }

        super.update();
    }

    @Override
    void calibrateMessage(int sizeDiff, Graphics2D g2D){
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver imageObserver) {
        super.draw(g2D, imageObserver);


        if (GamePanel.isArcadeVersion){
            int height = getHeight() - 10;
            if (isFocused()){
                height -= 10;
            }
            if (isTriggered()){
                height -= 20;
            }
            int width = (int) (height * arcadeButtonImageWidthRatio);

            g2D.drawImage(arcadeKeyImage,
                    getX() + (getWidth() - width)/2,
                    getY(),
                    width, height, imageObserver);
        }
        else {

            int keyWidth = GamePanel.getGamePannel().getFontMetrics(buttonFont).stringWidth(keyText);
            int keyFontSize = Math.min((int) (getWidth() / ((float) keyWidth/buttonFontSize)), getHeight()-30);

            if (isFocused()){
                keyFontSize -= 10;
            }
            if (isTriggered()){
                keyFontSize -= 20;
            }

            Font f = new Font(buttonFontName, Font.PLAIN, keyFontSize);
            keyWidth = GamePanel.getGamePannel().getFontMetrics(f).stringWidth(keyText);

            g2D.setFont(f);
            g2D.setColor(new Color(196, 190, 0));

            g2D.drawString(keyText,getX() + (getWidth() - keyWidth)/2,getY() + getHeight()/2 + keyFontSize/2);
        }


    }
}
