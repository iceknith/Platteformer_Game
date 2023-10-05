package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelChangingButton extends Button{
    String level;
    public LevelChangingButton(int w, int h, int x, int y, String textureName, String messageName, String id, String lvl, String subLvlName) throws IOException, FontFormatException {
        super(w, h, x, y, textureName, messageName, id, subLvlName);

        //type = "LevelChanging" + type;
        //name = "LevelChanging" + name;


        level = lvl;
    }

    @Override
    void releasedHandler() throws IOException, FontFormatException {
        super.releasedHandler();

        if (buttonMessage.equals("Level testing")){

            Camera c = GamePanel.camera;

            String subLvlName = "Level Testing Text Input";

            if (c.level.getSubLvl(subLvlName) != null){
                c.level.openSubLevel(subLvlName, false, false);
                return;
            }

            c.level.addSubLvl(new SubLevel(subLvlName));

            TextInputMenu txtInputMenu = new TextInputMenu(c.screenWidth /2, c.screenHeight /2,
                    "#0", subLvlName, false, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            c.level.addToSubLevel(txtInputMenu, subLvlName);

            c.level.openSubLevel(subLvlName, false, false);

            txtInputMenu.setAsInt(false);

            txtInputMenu.setCategoryNames(java.util.List.of("File name"));
            txtInputMenu.setDefaultValues(java.util.List.of("TEST"));
            txtInputMenu.setCategorySetValues(List.of(
                    i -> {
                        if (new File("assets/level/"+i+".lvl").exists()){
                            GamePanel.camera.setNextLevel(i);
                        }
                        txtInputMenu.isOpen = true;
                        return null;
                    }));

            txtInputMenu.isOpen = true;
        }
        else GamePanel.camera.setNextLevel(level);
    }

}
