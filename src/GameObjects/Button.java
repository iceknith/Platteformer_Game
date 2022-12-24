package GameObjects;

import handlers.MouseHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Button extends GameObject2D{

    boolean focused;
    boolean triggered;


    public Button(int w, int h, int x, int y, String textureName, String id) throws IOException {
        type = "Button_" + textureName;
        name = type+id;

        hasPhysicalCollisions = false;
        hitbox = new Rectangle(x, y, w, h);
        System.out.println("assets/Button/"+textureName+"/unfocused/0.png");
        sprite = new Sprite(ImageIO.read(new File("assets/Button/"+textureName+"/unfocused/0.png")), hitbox);
    }

    boolean pointIsOver(int x, int y){
        return x > getX() && x < getX() + getWidth() && y > getY() && y < getY() + getHeight();
    }

    public boolean isFocused(){return focused;}

    public boolean isTriggered(){return triggered;}

    @Override
    public void update() throws IOException {

        if (pointIsOver(MouseHandler.getX(), MouseHandler.getY())){

            if (MouseHandler.isRightClickPressed || MouseHandler.isLeftClickPressed){
                triggerHandler();
            }
            else{
                if (isTriggered()){
                    releasedHandler();
                }else{
                    focusHandler();
                }
            }

        } else{
            unfocusedHandler();
        }
    }

    void unfocusedHandler() throws  IOException{
        sprite = new Sprite(ImageIO.read(new File("assets/Button/"+type.substring(7)+"/unfocused/0.png")), hitbox);
        focused = false;
        triggered = false;
    }

    void focusHandler() throws IOException {
        sprite = new Sprite(ImageIO.read(new File("assets/Button/"+type.substring(7)+"/focused/0.png")), hitbox);
        focused = true;
    }

    void triggerHandler() throws IOException {
        sprite = new Sprite(ImageIO.read(new File("assets/Button/"+type.substring(7)+"/clicked/0.png")), hitbox);
        triggered = true;
    }

    void releasedHandler() throws FileNotFoundException {
        //is overwritten
        triggered = false;
    }
}
