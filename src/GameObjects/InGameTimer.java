package GameObjects;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public class InGameTimer extends GameObject2D{

    InGameTimer(String subLvl) throws IOException {
        super(0, 0, 0, 0, subLvl);

        type = "InGameTimer_";
        name = type+"0";

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(ImageIO.read(new File("assets/placeholder.png")), hitbox);

        GamePanel.inGameTimer = 0;
    }

    @Override
    public void update() throws IOException, FontFormatException {
        GamePanel.inGameTimer += (int) (GamePanel.deltaTime*100);
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {
        String timerText = GamePanel.inGameTimer/60000 + " min " + (GamePanel.inGameTimer/1000)%60 + " s " + GamePanel.inGameTimer%1000 + " ms";
        g2D.setColor(new Color(0f, 0f, 0f, .5f));
        g2D.setFont(new Font("Eight Bit Dragon", Font.PLAIN, 25));
        g2D.fillRect(GamePanel.camera.screenWidth -g2D.getFontMetrics().stringWidth(timerText)-20,0,
                g2D.getFontMetrics().stringWidth(timerText)+20, g2D.getFontMetrics().getHeight()+50);

        g2D.setColor(Color.white);
        g2D.drawString(timerText, GamePanel.camera.screenWidth -g2D.getFontMetrics().stringWidth(timerText)-10, 50);
    }
}
