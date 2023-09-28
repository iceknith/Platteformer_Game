package GameObjects;

import java.awt.*;
import java.io.IOException;

public class ExitButton extends Button{

    String SubLevel;
    public ExitButton(int w, int h, int x, int y, String textureName, String messageName, String id, String subLevel) throws IOException, FontFormatException {

        super(w, h, x, y, textureName, messageName, id, subLevel);

        SubLevel = subLevel;
    }

    @Override
    void releasedHandler() throws IOException, FontFormatException {
        super.releasedHandler();

        System.exit(0);
    }
}
