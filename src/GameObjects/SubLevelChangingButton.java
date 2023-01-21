package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SubLevelChangingButton extends Button{

    String SubLevel;
    public SubLevelChangingButton(int w, int h, int x, int y, String textureName, String messageName, String id, String lvl, String subLevel) throws IOException, FontFormatException {
        super(w, h, x, y, textureName, messageName, id, subLevel);

        //type = "LevelChanging" + type;
        //name = "LevelChanging" + name;

        SubLevel = lvl;
    }

    @Override
    void releasedHandler() throws FileNotFoundException {
        super.releasedHandler();

        GamePanel.camera.level.openSubLevel(SubLevel, false, false);
    }


}
