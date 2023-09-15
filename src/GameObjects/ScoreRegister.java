package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.io.*;

import static GameObjects.HighScoresDisplay.highScoresNames;
import static GameObjects.HighScoresDisplay.highScoresTimes;

public class ScoreRegister extends Button {

    ScoreRegister(int posX, int posY, String subLvl) throws IOException, FontFormatException {
        super(300,100,posX,posY,"base","Register Score", "0", subLvl);
    }

    //int i;

    @Override
    void releasedHandler() throws IOException {

        System.out.println(highScoresTimes+" Before");

        int j = 0;
        //add the score, so that the score list is still sorted
        while (j < highScoresTimes.size()){
            if(GamePanel.inGameTimer < highScoresTimes.get(j)){
                highScoresTimes.add(j, GamePanel.inGameTimer);
                highScoresNames.add(j, "Default Name");
                break;
            }
            j++;
        }
        if (j<50 && j == highScoresTimes.size()){
            highScoresTimes.add(GamePanel.inGameTimer);
            highScoresNames.add("Default Name");
        }

        //making sure that the lists aren't too big
        while (highScoresTimes.size() >= 50){
            highScoresTimes.remove(highScoresTimes.size() - 1);
            highScoresNames.remove(highScoresNames.size() - 1);
        }

        //writing in the file
        File fnew = new File("assets/HighScores/"+GamePanel.camera.level.getLevelName()+".highscore");
        FileWriter fw = new FileWriter(fnew, false);
        PrintWriter pw = new PrintWriter(fw);

        System.out.println(highScoresTimes);

        for (int i = 0; i < highScoresTimes.size(); i++){
            //fw.write(hsN.get(i)+"-"+hsT.get(i)+"\n");
            String txt = highScoresNames.get(i)+"-"+highScoresTimes.get(i);
            pw.println(txt);
        }

        pw.close();
        super.releasedHandler();

        GamePanel.camera.setNextLevel("menu");
    }

}
