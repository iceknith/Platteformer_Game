package GameObjects;

import handlers.KeyHandler;
import main.GamePanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.io.IOException;

public class KeyChangingButton extends Button{

    String buttonKey;
    boolean isWaitingKey;

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

        isWaitingKey = true;
        KeyHandler.resetLastKeyPressed();

        //setting up key waiter
        int w = GamePanel.camera.getWidth()/2;
        int h = GamePanel.camera.getHeight()/2;

        SubLevel s = new SubLevel("key waiter");
        KeyWaitingButton waiter = new KeyWaitingButton(400,200, w-225, h-100, "keyWaiter", "Press any key", "None", buttonKey, s, "key waiter");
        s.objectList.add(waiter);
        GamePanel.camera.level.addSubLvl(s);
        GamePanel.camera.level.openSubLevel("key waiter", false, true);

    }

    @Override
    void calibrateMessage(int sizeDiff){
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver imageObserver) {
        super.draw(g2D, imageObserver);

        String key = KeyEvent.getKeyText(KeyHandler.getKey(buttonKey));
        int keyWidth = GamePanel.getGamePannel().getFontMetrics(buttonFont).stringWidth(key);
        int keyFontSize = Math.min((int) (getWidth() / ((float) keyWidth/buttonFontSize)), getHeight()-30);

        if (isFocused()){
            keyFontSize -= 10;
        }
        if (isTriggered()){
            keyFontSize -= 20;
        }

        Font f = new Font(buttonFontName, Font.PLAIN, keyFontSize);
        keyWidth = GamePanel.getGamePannel().getFontMetrics(f).stringWidth(key);

        g2D.setFont(f);
        g2D.setColor(new Color(196, 190, 0));

        g2D.drawString(key,getX() + (getWidth() - keyWidth)/2,getY() + getHeight()/2 + keyFontSize/2);
    }
}
