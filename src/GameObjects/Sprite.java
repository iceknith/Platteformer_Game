package GameObjects;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sprite {

    BufferedImage image;

    int width;
    int height;
    int x;
    int y;
    int offsetX;
    int offsetY;
    int direction;
    double resizeFactor;

    public Sprite(BufferedImage img, Rectangle hitbox){
        offsetX = 0;
        offsetY = 0;
        direction = 1;

        setImage(img, hitbox);

    }

    public Sprite(BufferedImage img, double resizeFact){
        resizeFactor = resizeFact;
        offsetX = 0;
        offsetY = 0;
        direction = 1;

        setImage(img);

    }

    public BufferedImage getImage(){return image;}

    public int getWidth(){return width*direction;}

    public int getHeight(){return height;}

    public int getX(){return x;}

    public int getY(){return y;}

    public int getOffsetPosX(Rectangle hitbox){
        setDirection(direction);
        return hitbox.x - offsetX +  hitbox.width/2 - width/2;
    }

    public int getOffsetPosY(Rectangle hitbox){return hitbox.y - offsetY  +  hitbox.height/2 - height/2;}

    public void setX(int posX, Rectangle hitbox){x = posX - offsetX + hitbox.width/2 - width/2;}

    public void setY(int posY, Rectangle hitbox){y = posY - offsetY + hitbox.width/2 - width/2;}

    void setImage(BufferedImage newSprite){
        image = newSprite;

        width = (int) (image.getWidth()* resizeFactor);
        height = (int) (image.getHeight()* resizeFactor);
    }

    void setImage(BufferedImage newSprite, Rectangle hitbox){
        image = newSprite;

        width = hitbox.width;
        height = hitbox.height;
    }

    void setDirection(int newDir){
        if (newDir > 0){
            offsetX = 0;
            direction = 1;
        }
        else{
            offsetX = -width;
            direction = -1;
        }
    }

    void setResizeFactor(int newResizeFactor){resizeFactor = newResizeFactor;}
}
