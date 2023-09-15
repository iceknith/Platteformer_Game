package GameObjects;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public class ScoreDisplay extends GameObject2D {

    ScoreDisplay(int posX, int posY, String subLvl) throws IOException {
        super(0, 0, 0, 0, subLvl);

        type = "ScoreDisplay_";
        name = type + "0";

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(ImageIO.read(new File("assets/placeholder.png")), hitbox);

        x = posX;
        y = posY;
        if (x==0){
            x = GamePanel.camera.width/2;
        }
        if (y==0){
            y = GamePanel.camera.height/7;
        }
    }

    int x; int y;

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO){
        String timerText;

        if (GamePanel.inGameTimer < HighScoresDisplay.getCurrentHighScore()) {//insert a high score condition
            timerText = "NEW HIGH SCORE: " + GamePanel.inGameTimer/60000 + " min " + (GamePanel.inGameTimer/1000)%60 + " s " + GamePanel.inGameTimer%1000 + " ms";
        }
        else {
            timerText = "Your time: " + GamePanel.inGameTimer/60000 + " min " + (GamePanel.inGameTimer/1000)%60 + " s " + GamePanel.inGameTimer%1000 + " ms";
        }
        g2D.setFont(new Font("Sans Serif", Font.BOLD, 30));

        g2D.setColor(new Color(0f, 0f, 0f, .5f));
        g2D.fillRect(x-g2D.getFontMetrics().stringWidth(timerText)/2-20,y-g2D.getFontMetrics().getHeight()-15, g2D.getFontMetrics().stringWidth(timerText)+40, g2D.getFontMetrics().getHeight()+40);

        g2D.setColor(Color.white);
        g2D.drawRect(x-g2D.getFontMetrics().stringWidth(timerText)/2-20,y-g2D.getFontMetrics().getHeight()-15, g2D.getFontMetrics().stringWidth(timerText)+40, g2D.getFontMetrics().getHeight()+40);

        g2D.drawString(timerText, x-g2D.getFontMetrics().stringWidth(timerText)/2, y);

    }
}