package GameObjects;

import handlers.MouseHandler;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class DropDownMenu extends GameObject2D{

    boolean isOpen;
    boolean isAnimating;

    int openAnimSlowness;

    int displayedWidth;
    int displayedHeight;

    int buttonHeight;
    ArrayList<String> buttonText = new ArrayList<>();
    ArrayList<Function<Void, Void>> buttonExec = new ArrayList<>();
    int selectedButton;

    DropDownMenu(int posX, int posY, int w, int h, String id, String subLvl, List<String> bNames, List<Function<Void, Void>> bExec) throws IOException {
        super(posX, posY, w, h, subLvl);

        type = "DropDownMenu_";
        name = type + id;

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(ImageIO.read(new File("assets/placeholder.png")), hitbox);

        isOpen = false;
        isAnimating = false;
        openAnimSlowness = 100000;

        displayedWidth = w;
        displayedHeight = 0;

        buttonHeight = 50;
        buttonText.addAll(bNames);
        buttonExec.addAll(bExec);
    }

    public void setButtonHeight(int bh){
        buttonHeight = bh;
    }

    public void setButtonText(List<String> bText){
        buttonText = new ArrayList<>(bText);
    }

    public void setButtonExec(List<Function<Void, Void>> bExec){
        buttonExec = new ArrayList<>(bExec);
    }

    @Override
    public boolean pointIsIn(int x, int y){
        return isOpen && super.pointIsIn(x,y);
    }

    public void activate(){
        isOpen = !isOpen;
        isAnimating = true;
    }

    @Override
    public void update() throws IOException, FontFormatException {
        if (pointIsIn(MouseHandler.getX(), MouseHandler.getY())){
            selectedButton = (getY() + displayedHeight - MouseHandler.getY()) / (buttonHeight + 10);

            if (MouseHandler.isLeftClickPressed && selectedButton < buttonExec.size()){
                buttonExec.get(selectedButton).apply(null);
            }
        }
        else{
            selectedButton = buttonText.size();
        }
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {
        if (!isOpen && !isAnimating){
            return;
        }
        if (isAnimating){
            if (displayedHeight > getHeight() - (double) 1 / openAnimSlowness * GamePanel.deltaTime && isOpen){
                isAnimating = false;
                displayedHeight = getHeight();
            }
            else if (isOpen) displayedHeight += (int) ((double) 1 / openAnimSlowness * GamePanel.deltaTime);

            //close
            else if (displayedHeight < (double) 1 / openAnimSlowness * GamePanel.deltaTime){
                isAnimating = false;
                displayedHeight = 0;
            }
            else displayedHeight -= (int) ((double) 1 / openAnimSlowness * GamePanel.deltaTime);
        }

        g2D.setColor(new Color(0f, 0f, 0f, .5f));
        g2D.fillRect(getX(), getY(), displayedWidth, displayedHeight);

        int i = 0;
        for (int j = displayedHeight - buttonHeight; j > -10 && i < buttonText.size() ; j -= buttonHeight + 10){
            if (i == selectedButton){
                g2D.setColor(Color.lightGray);
                g2D.drawRect(getX(), getY() + j, displayedWidth, buttonHeight);
                g2D.setColor(new Color(0f, 0f, 0f, .5f));
            }
            g2D.fillRect(getX(), getY() + j, displayedWidth, buttonHeight);
            i ++;
        }

        g2D.setColor(Color.white);
        g2D.setFont(new Font("Eight Bit Dragon", Font.PLAIN, 30));
        i = 0;
        for (int j = displayedHeight - buttonHeight; j > -10 && i < buttonText.size(); j -= buttonHeight + 10){
            g2D.drawString(buttonText.get(i),
                    getX() + displayedWidth/2 - g2D.getFontMetrics().stringWidth(buttonText.get(i))/2,
                    getY() + j + buttonHeight - g2D.getFontMetrics().getHeight()/2);
            i++;
        }
    }
}
