package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class LevelChangingButton extends Button{
    String level;
    public LevelChangingButton(int w, int h, int x, int y, String textureName, String messageName, String id, String lvl, String subLvlName) throws IOException, FontFormatException {
        super(w, h, x, y, textureName, messageName, id, subLvlName);

        //type = "LevelChanging" + type;
        //name = "LevelChanging" + name;

        level = lvl;
    }

    @Override
    void releasedHandler() throws FileNotFoundException {
        super.releasedHandler();

        GamePanel.camera.setNextLevel(level);
    }

}
