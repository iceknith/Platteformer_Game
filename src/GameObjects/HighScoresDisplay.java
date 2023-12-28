package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class HighScoresDisplay extends GameObject2D {

    HighScoresDisplay(int posX, int posY, String subLvl) throws IOException {
        this(posX, posY, subLvl, GamePanel.camera.nextLevel);
    }

    HighScoresDisplay(int posX, int posY, String subLvl, String lvl) throws IOException {
        super(0, 0, 0, 0, subLvl);

        type = "ScoreDisplay_";
        name = type + "0";

        hasPhysicalCollisions = false;
        isGUI = true;


        sprite = new Sprite(readImageBuffered("assets/placeholder.png"), hitbox);

        x = posX;
        y = posY;
        if (x==0){
            x = GamePanel.camera.screenWidth /4;
        }
        if (y==0){
            y = GamePanel.camera.screenHeight /4;
        }
        BufferedReader reader;

        try {
            reader = new BufferedReader(new FileReader("assets/HighScores/"+lvl+".highscore"));
            String line = reader.readLine();

            while (line != null) {

                String[] parts = line.split("-");
                globalHighScoresNames.add(parts[0]);
                globalHighScoresTimes.add(Integer.valueOf(parts[1]));
                highScoresNames.add(parts[0]);
                highScoresTimes.add(Integer.valueOf(parts[1]));

                line = reader.readLine();
            }

            reader.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    int x; int y;

    ArrayList<String> highScoresNames = new ArrayList<>();
    ArrayList<Integer> highScoresTimes = new ArrayList<>();
    public static ArrayList<String> globalHighScoresNames = new ArrayList<>();
    public static ArrayList<Integer> globalHighScoresTimes = new ArrayList<>();

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {

        g2D.setColor(new Color(0f, 0f, 0f, .5f));
        g2D.fillRect(x, y, GamePanel.camera.screenWidth /4, GamePanel.camera.screenHeight *3/5);
        g2D.setColor(Color.gray);
        g2D.drawRect(x, y, GamePanel.camera.screenWidth /4, GamePanel.camera.screenHeight *3/5);

        g2D.setColor(Color.white);
        g2D.setFont(new Font("Eight Bit Dragon", Font.PLAIN, 30));
        int yOffset = g2D.getFontMetrics().getHeight();

        g2D.drawString("Scores :", x+GamePanel.camera.screenWidth /8-g2D.getFontMetrics().stringWidth("Scores :")/2, y+yOffset);

        g2D.setFont(new Font("Eight Bit Dragon", Font.PLAIN, 20));

        //System.out.println("Nombre de lignes maximal "+(GamePanel.camera.height/2)/g2D.getFontMetrics().getHeight());
        for (int i = 0; i < Math.min(highScoresNames.size(), (GamePanel.camera.screenHeight *3/5 - yOffset)/(g2D.getFontMetrics().getHeight() + 10)); i++){

            int posX = x+30+g2D.getFontMetrics().stringWidth(highScoresNames.get(i) + " : ");
            int posY = y+40+yOffset+i*(g2D.getFontMetrics().getHeight() + 10);
            String score = highScoresTimes.get(i)/60000 + " min " + (highScoresTimes.get(i)/1000)%60 + " s " + highScoresTimes.get(i)%1000 + " ms";

            //draw names
            g2D.setColor(Color.white);
            g2D.drawString(highScoresNames.get(i) + " : ",x+20, posY);

            //draw times
            if (i == 0) g2D.setColor(new Color(1f, .84f, .0f)); //Gold
            if (i == 1) g2D.setColor(new Color(.75f, .75f, .75f)); //Silver
            if (i == 2) g2D.setColor(new Color(.8f, .5f, .2f)); //Bronze

            g2D.drawString(score, posX, posY);
        }
    }

    public static int getCurrentGlobalHighScores(){
        if (!globalHighScoresTimes.isEmpty()) return globalHighScoresTimes.get(0);
        return Integer.MAX_VALUE;
    }

    public static void resetHighScores(){
        globalHighScoresTimes = new ArrayList<>();
        globalHighScoresNames = new ArrayList<>();
    }

}
