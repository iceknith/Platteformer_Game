package GameObjects;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public class Background extends GameObject2D{

    float zoom;
    float scrollingSlowness;

    Background(int w, int h, String textureName, String id, String subLvl) throws IOException{
        this(0, -2000, w, h, 7f, 0.3f, textureName, id, subLvl);
    }

    Background(int x, int y, int w, int h, float zoomAmount, float scrollSlowness, String textureName, String id, String subLvl) throws IOException {
        super(x - (int)(zoomAmount * (w/2)), y - (int)(zoomAmount * (h/2)), w, h, subLvl);

        isGUI = true;

        type = "Background_" + textureName;
        name = type+id;
        sprite = new Sprite(ImageIO.read(new File("assets/Background/"+textureName+"/0.png")), hitbox);

        zoom = zoomAmount;
        scrollingSlowness = scrollSlowness;
    }

    Background(Background go) {
        super(go);

        zoom = go.zoom;
        scrollingSlowness = go.scrollingSlowness;
    }

    public float getZoom(){
        return zoom;
    }

    public float getScrollingSlowness(){
        return scrollingSlowness;
    }

    public void setX(int posX){
        super.setX(posX - (int)(zoom * (getWidth()/2)));
    }

    public void setY(int posY){
        super.setY(posY - (int)(zoom * (getHeight()/2)));
    }


    public void setZoom(float newZoom){
        zoom = newZoom;
    }

    public void setScrollingSlowness(float newScrollingSlowness){
        scrollingSlowness = newScrollingSlowness;
    }

    @Override
    public GameObject2D copy() {
        return new Background(this);
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {

        int posX = getX() - (int) (GamePanel.camera.getX() * scrollingSlowness);
        posX = Math.max(GamePanel.camera.width - (int) (getWidth() * zoom), Math.min(0, posX));

        int posY = getY() - (int) (GamePanel.camera.getY() * scrollingSlowness);
        posY = Math.max(GamePanel.camera.height - (int) (getHeight() * zoom), Math.min(0, posY));

        g2D.drawImage(getSprite().getImage(), posX, posY,
                (int) (getSprite().getWidth() * zoom), (int) (getSprite().getHeight() * zoom), IO);
    }
}
