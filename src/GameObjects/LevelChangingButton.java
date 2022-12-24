package GameObjects;

import main.GamePanel;

import java.io.FileNotFoundException;
import java.io.IOException;

public class LevelChangingButton extends Button{

    String level;

    public LevelChangingButton(int w, int h, int x, int y, String textureName, String id, String lvl) throws IOException {
        super(w, h, x, y, textureName, id);

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
