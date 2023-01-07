package GameObjects;

import handlers.KeyHandler;
import main.GamePanel;

import java.awt.*;
import java.io.IOException;

public class KeyWaitingButton extends Button{

    String buttonKey;

    SubLevel selfSubLvl;

    public KeyWaitingButton(int w, int h, int x, int y, String textureName, String message, String id, String key, SubLevel subLvl) throws IOException, FontFormatException {
        super(w, h, x, y, textureName, message, id);

        buttonKey = key;
        selfSubLvl = subLvl;
    }

    @Override
    public void update() throws IOException, FontFormatException {
        super.update();

        if (KeyHandler.getLastKeyPressed() != -1){

            KeyHandler.changeKey(buttonKey, KeyHandler.getLastKeyPressed());
            GamePanel.camera.level.subLevelBackHandler();
            GamePanel.camera.level.deleteSubLvl(selfSubLvl);
        }
    }
}
