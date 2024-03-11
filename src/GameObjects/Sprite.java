package GameObjects;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Sprite {

    BufferedImage image;

    public int width;
    int height;
    int offsetX;
    int offsetY;
    public int directionOffsetX;
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

    public Sprite(Sprite s){
        image = s.image;

        width = s.width;
        height = s.height;
        offsetX = s.offsetX;
        offsetY = s.offsetY;
        directionOffsetX = s.directionOffsetX;
        direction = s.direction;
        resizeFactor = s.resizeFactor;
    }

    public BufferedImage getImage(){return image;}

    public int getWidth(){return width*direction;}

    public int getHeight(){return height;}

    public int getOffsetX(Rectangle hitbox){
        setDirection(direction);
        return hitbox.x - offsetX*direction - directionOffsetX +  hitbox.width/2 - width/2;
    }

    public int getOffsetY(Rectangle hitbox){return hitbox.y - offsetY  +  hitbox.height/2 - height/2;}

    void setImage(BufferedImage newSprite){
        image = newSprite;

        width = (int) (image.getWidth() * resizeFactor);
        height = (int) (image.getHeight() * resizeFactor);
    }

    void setImage(BufferedImage newSprite, Rectangle hitbox){
        image = newSprite;

        width = hitbox.width;
        height = hitbox.height;
    }

    void setWidth(int w){
        width = w;
    }

    void setHeight(int h){
        height = h;
    }

    void resize(int w, int h){
        width = w;
        height = h;
    }

    public int getDirection(){
        return direction;
    }

    public void setDirection(int newDir){
        if (newDir > 0){
            directionOffsetX = 0;
            direction = 1;
        }
        else{
            directionOffsetX = -width;
            direction = -1;
        }
    }

    void setResizeFactor(int newResizeFactor){resizeFactor = newResizeFactor;}

    public Sprite copy(){
        return new Sprite(this);
    }
}
