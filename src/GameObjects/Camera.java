package GameObjects;

import main.GamePanel;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Camera extends GameGrid {

    double speedX = 5;
    double speedY = 2;

    double velocityX = 0;
    double velocityY = 0;

    public ArrayList<Rectangle> borders = new ArrayList<Rectangle>();

    public Camera(int screenW, int screenH, int posX, int posY) throws IOException {
        super(screenW, screenH, posX, posY);

        borders.add(new Rectangle((int) (- 0.2 * screenW) , 0, (int) (0.4 * screenW), screenH));

        borders.add(new Rectangle((int) (0.8 * screenW), 0, (int) (0.4 * screenW), screenH));

        borders.add(new Rectangle(0 , (int) (-0.1 * screenH), screenW, (int) (0.2 * screenH)));

        borders.add(new Rectangle(0, (int) (0.7 * screenH), screenW, (int) (0.6 * screenH)));
    }

    public void updateCamera() throws IOException {

        boolean intersects = false;

        for (Rectangle b: borders) {
            if (b.intersects(GamePanel.player.getHitbox())) {
                intersects = true;

                int i = borders.indexOf(b);
                if (i - 2 < 0){
                    int m = (int) Math.signum((GamePanel.player.getX() + GamePanel.player.getWidth()/2f) - (b.x + b.width/2f));
                    moveX(m, b.intersection(GamePanel.player.getHitbox()));
                }
                else{
                    moveY((int) Math.signum(GamePanel.player.getY() - b.y), b.intersection(GamePanel.player.getHitbox()));
                }
            }
        }

        if(velocityX != 0 || velocityY != 0){
            if(!intersects ){
                stopMovement();
            }
            updateGrid(velocityX, velocityY);
        }
    }

    void moveX(int movement, Rectangle intersection) throws IOException {
        velocityX = movement*intersection.width;
        x -= velocityX;
    }

    void moveY(int movement, Rectangle intersection) throws IOException {
        velocityY = movement*intersection.height;
        y -= velocityY;
    }

    void stopMovement(){
        if(velocityY > 1){
            velocityY -= speedY;
        }else {
            velocityY = 0;
        }

        if(velocityX > 1){
            velocityX -= speedX;
        }else {
            velocityX = 0;
        }
    }
}
