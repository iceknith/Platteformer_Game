package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class Camera extends GameGrid {

    int speed = 5;

    public ArrayList<Rectangle> borders = new ArrayList<Rectangle>();

    public Camera(int screenW, int screenH, int posX, int posY) throws FileNotFoundException {
        super(screenW, screenH, posX, posY);

        borders.add(new Rectangle(0 , 0, (int) (0.2 * screenW), screenH));

        borders.add(new Rectangle((int) (0.8 * screenW), 0, (int) (0.2 * screenW), screenH));

        borders.add(new Rectangle(0 , 0, screenW, (int) (0.1 * screenH)));

        borders.add(new Rectangle(0, (int) (0.7 * screenH), screenW, (int) (0.3 * screenH)));
    }

    public void updateCamera() throws FileNotFoundException {
        for (Rectangle b: borders) {
            if (b.intersects(GamePanel.player.getHitbox())) {
                int i = borders.indexOf(b);
                if (i - 2 < 0){
                    moveX((int) Math.signum(GamePanel.player.getX() - b.x), b.intersection(GamePanel.player.getHitbox()));
                }
                else{
                    moveY((int) Math.signum(GamePanel.player.getY() - b.y), b.intersection(GamePanel.player.getHitbox()));
                }
            }
        }
    }

    void moveX(int movement, Rectangle intersection) throws FileNotFoundException {
        x -= movement*intersection.width;
        GamePanel.player.setX(GamePanel.player.getX() + movement*intersection.width);
        updateGrid();
    }

    void moveY(int movement, Rectangle intersection) throws FileNotFoundException {
        y -= movement*intersection.height;
        GamePanel.player.setY(GamePanel.player.getY() + movement*intersection.height);
        updateGrid();
    }
}
