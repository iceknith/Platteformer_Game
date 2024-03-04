package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.IOException;

public class ScoreDisplay extends GameObject2D {

    ScoreDisplay(int posX, int posY, String subLvl) throws IOException {
        super(0, 0, 0, 0, subLvl);

        type = "ScoreDisplay_";
        name = type + "0";

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(readImageBuffered("assets/placeholder.png"), hitbox);

        x = posX;
        y = posY;
        if (x==0){
            x = GamePanel.camera.screenWidth /2;
        }
        if (y==0){
            y = GamePanel.camera.screenHeight /7;
        }
    }

    int x; int y;

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO){
        String timerText;

        if (GamePanel.inGameTimer < HighScoresDisplay.getCurrentGlobalHighScores()) {
            timerText = "NEW HIGH SCORE: " + GamePanel.inGameTimer/60000 + " min " + (GamePanel.inGameTimer/1000)%60 + " s " + GamePanel.inGameTimer%1000 + " ms";
        }
        else {
            timerText = "Your time: " + GamePanel.inGameTimer/60000 + " min " + (GamePanel.inGameTimer/1000)%60 + " s " + GamePanel.inGameTimer%1000 + " ms";
        }
        g2D.setFont(new Font("Eight Bit Dragon", Font.PLAIN, 30));

        g2D.setColor(new Color(0f, 0f, 0f, .5f));
        g2D.fillRect(x-g2D.getFontMetrics().stringWidth(timerText)/2-20,y-g2D.getFontMetrics().getHeight()-15, g2D.getFontMetrics().stringWidth(timerText)+40, g2D.getFontMetrics().getHeight()+40);

        if (GamePanel.inGameTimer < HighScoresDisplay.getCurrentGlobalHighScores()) g2D.setColor(Color.yellow);
        else g2D.setColor(Color.gray);
        g2D.drawRect(x-g2D.getFontMetrics().stringWidth(timerText)/2-20,y-g2D.getFontMetrics().getHeight()-15, g2D.getFontMetrics().stringWidth(timerText)+40, g2D.getFontMetrics().getHeight()+40);

        if (GamePanel.inGameTimer < HighScoresDisplay.getCurrentGlobalHighScores()) g2D.setColor(Color.yellow);
        else g2D.setColor(Color.white);
        g2D.drawString(timerText, x-g2D.getFontMetrics().stringWidth(timerText)/2, y);

    }
}
