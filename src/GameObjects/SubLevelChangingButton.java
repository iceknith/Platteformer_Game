package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.io.IOException;

public class SubLevelChangingButton extends Button{

    String subLevel;
    public SubLevelChangingButton(int w, int h, int x, int y, String textureName, String messageName, String id, String lvl, String subLevel) throws IOException, FontFormatException {
        super(w, h, x, y, textureName, messageName, id, subLevel);

        //type = "LevelChanging" + type;
        //name = "LevelChanging" + name;

        this.subLevel = lvl;
    }

    @Override
    void releasedHandler() throws Exception {
        super.releasedHandler();

        GamePanel.camera.level.openSubLevel(subLevel, false, false);
    }


}
