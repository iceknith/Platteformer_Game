package GameObjects;

import handlers.KeyHandler;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public class DropDownMenu extends GameObject2D{

    boolean isOpen;
    boolean isAnimating;
    boolean wasLaunchPressed;

    int openAnimSlowness;

    int displayedWidth;
    int displayedHeight;
    DropDownMenu(int posX, int posY, int w, int h, String id, String subLvl) throws IOException {
        super(posX, posY, w, h, subLvl);

        type = "DropDownMenu_";
        name = type + id;

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(ImageIO.read(new File("assets/placeholder.png")), hitbox);

        isOpen = false;
        isAnimating = false;
        openAnimSlowness = 1000000000; //time :

        displayedWidth = w;
        displayedHeight = 0;
    }

    public void activate(){
        isOpen = !isOpen;
        if (isOpen) isAnimating = true;
    }

    @Override
    public void update() throws IOException, FontFormatException {
        super.update();

        if(KeyHandler.isLaunchKeyPressed){
            if (!wasLaunchPressed) {
                activate();
                wasLaunchPressed = true;
            }
        }
        else if (wasLaunchPressed) wasLaunchPressed = false;
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {
        if (!isOpen && !isAnimating){
            return;
        }
        if (isAnimating){
            if (displayedHeight > getHeight() && isOpen){
                isAnimating = false;
                displayedHeight = getHeight();
            }
            else if (isOpen) displayedHeight += (int) ((getHeight() * GamePanel.deltaTime) / openAnimSlowness);

            //close
            else if (displayedHeight < 0){
                isAnimating = false;
                displayedHeight = 0;
            }
            else displayedHeight -= (int) ((getHeight() * GamePanel.deltaTime) / openAnimSlowness);

            System.out.println(displayedHeight);
        }

        g2D.setColor(Color.black);
        g2D.fillRect(getX(), getY(), displayedWidth, displayedHeight);
    }
}
