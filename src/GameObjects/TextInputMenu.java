package GameObjects;

import handlers.KeyHandler;
import handlers.MouseHandler;
import main.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TextInputMenu extends GameObject2D {

    ArrayList<String> categoryNames;
    ArrayList<String> categoryValues;
    ArrayList<Function<String, Void>> categorySetValues;
    static int categoryHeight = 100;
    static int defaultWidth = 500;
    static int fillSpaceHeight = 50;
    static int fillSpaceWidth = defaultWidth*5/6;
    boolean isOpen;

    int basePosX;
    int basePosY;

    int buttonHeight = 75;
    int buttonWidth = 150;

    boolean isButtonSelected = false;
    int editingFillSpace = -1;
    int selectedFillSpace = -1;

    boolean isInt;

    TextInputMenu(int x, int y, String id, String subLvl, boolean isOnlyInt,
                  List<String> categoryName, List<String> defaultValues, List<Function<String, Void>> variables) throws IOException {
        super(x-defaultWidth/2, y - (categoryHeight * categoryName.size())/2,
                defaultWidth, categoryHeight * categoryName.size(), subLvl);

        type = "DropDownMenu_";
        name = type + id;

        hasPhysicalCollisions = false;
        isGUI = true;

        sprite = new Sprite(ImageIO.read(new File("assets/placeholder.png")), hitbox);

        categoryNames = new ArrayList<>(categoryName);
        categoryValues = new ArrayList<>(defaultValues);
        categorySetValues = new ArrayList<>(variables);

        basePosX = x;
        basePosY = y;

        isInt = isOnlyInt;
    }

    public void setCategoryNames(List<String> categoryName){
        categoryNames = new ArrayList<>(categoryName);
        setHeight(fillSpaceHeight * 2 * categoryNames.size() + 10);
        setY(basePosY - getHeight()/2);
    }

    public void setDefaultValues(List<String> defaultValues){
        categoryValues = new ArrayList<>(defaultValues);
    }

    public void setCategorySetValues(List<Function<String, Void>> variables){
        categorySetValues = new ArrayList<>(variables);
    }

    public void setAsInt(boolean isOnlyInt){
        isInt = isOnlyInt;
    }

    public boolean pointIsOnRect(int pointX, int pointY, int x, int y, int w, int h){
        return x <= pointX && pointX <= x + w &&
                y + 10 <= pointY && pointY <= y + h;
    }

    public boolean pointIsOnButton(int x, int y){
        return pointIsOnRect(
                x,
                y,
                getX() + getWidth()/2 - buttonWidth/2,
                getY() + getHeight() + 10,
                buttonWidth,
                buttonHeight);
    }

    @Override
    public void update() throws IOException, FontFormatException {

        if (!isOpen) return;

        //Selection (by mouse hovering) logic
        selectedFillSpace = -1;

        if (pointIsIn(MouseHandler.getX(), MouseHandler.getY())){
            for (int i = 0; i < categoryValues.size(); i++){

                if (pointIsOnRect(MouseHandler.getX(), MouseHandler.getY(),
                        getX() + getWidth()/2 - fillSpaceWidth/2,
                        getY() + (i + 1)*categoryHeight - fillSpaceHeight,
                        fillSpaceWidth, fillSpaceHeight)){

                    selectedFillSpace = i;
                }
            }
        }
        else {

            isButtonSelected = pointIsOnButton(MouseHandler.getX(), MouseHandler.getY());
        }

        //click logic
        if (MouseHandler.isLeftClickPressed){
            editingFillSpace = selectedFillSpace;
            if (isButtonSelected){
                //resetting variables
                isButtonSelected = false;
                selectedFillSpace = -1;
                editingFillSpace = -1;
                isOpen = false;

                MouseHandler.resetClicks();
                GamePanel.camera.noUpdate = false;

                validate();
            }
        }

        //editing logic
        if (editingFillSpace != -1 && !KeyHandler.getLastStrTyped().isEmpty()) {

            String key = KeyHandler.getLastStrTyped();
            System.out.println(key);
            KeyHandler.resetLastStrTyped();

            if (isInt) {
                intEditing(key);
            } else {
                stringEditing(key);
            }
        }
    }

    void stringEditing(String key){

        if (key.equals("\b")){
            if (!categoryValues.get(editingFillSpace).isEmpty()){
                String s = categoryValues.get(editingFillSpace).substring(0,categoryValues.get(editingFillSpace).length() - 1);
                categoryValues.remove(editingFillSpace);
                categoryValues.add(editingFillSpace, s);
            }
        }
        else if (categoryValues.get(editingFillSpace).length() <= 25) {

            String s = categoryValues.get(editingFillSpace) + key;
            categoryValues.remove(editingFillSpace);
            categoryValues.add(editingFillSpace, s);

        }

    }

    void intEditing(String key){

        if (key.equals("\b")){
            if (!categoryValues.get(editingFillSpace).isEmpty()){
                String s = categoryValues.get(editingFillSpace).substring(0,categoryValues.get(editingFillSpace).length() - 1);
                categoryValues.remove(editingFillSpace);
                categoryValues.add(editingFillSpace, s);
            }
        }

        else if((key.contains("1234567890-.")) &&
                categoryValues.get(editingFillSpace).length() <= 5){

            String s = categoryValues.get(editingFillSpace) + key;
            categoryValues.remove(editingFillSpace);
            categoryValues.add(editingFillSpace, s);

        }
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {
        //returns if is not displayed
        if (!isOpen) return;

        //draw the background
        g2D.setColor(new Color(0f, 0f, 0f, .5f));
        g2D.fillRect(getX(), getY(), getWidth(), getHeight());

        //draw the fillSpaces
        for (int i = 0; i < categoryNames.size(); i++){

            if (i == selectedFillSpace || i == editingFillSpace){

                if (i == editingFillSpace) g2D.setColor(Color.YELLOW);
                else g2D.setColor(Color.gray);

                g2D.drawRect(getX() + getWidth()/2 - fillSpaceWidth/2,
                        getY() + (i + 1)*categoryHeight - fillSpaceHeight,
                        fillSpaceWidth, fillSpaceHeight);

                g2D.setColor(new Color(0f, 0f, 0f, .5f));
            }

            g2D.fillRect(getX() + getWidth()/2 - fillSpaceWidth/2,
                    getY() + (i + 1)*categoryHeight - fillSpaceHeight,
                    fillSpaceWidth, fillSpaceHeight);
        }

        //draw the category names & the text that is being edited
        g2D.setColor(Color.white);
        g2D.setFont(new Font("Eight Bit Dragon", Font.PLAIN, 30));

        for (int i = 0; i < categoryNames.size(); i++){
            g2D.drawString(categoryNames.get(i),
                    getX() + getWidth()/2 - g2D.getFontMetrics().stringWidth(categoryNames.get(i))/2,
                    getY() + i*categoryHeight + g2D.getFontMetrics().getHeight() + 10);

            g2D.drawString(categoryValues.get(i),
                    getX() + getWidth()/2 - g2D.getFontMetrics().stringWidth(categoryValues
                            .get(i))/2,
                    getY() + (i + 1)*categoryHeight - fillSpaceHeight + g2D.getFontMetrics().getHeight() + 10);
        }

        //draw the button, to validate
        g2D.setColor(new Color(0f, 0f, 0f, .60f));
        g2D.fillRect(getX() + getWidth()/2 - buttonWidth/2, getY() + getHeight() + 10,
                buttonWidth, buttonHeight);

        g2D.setColor(Color.white);

        if (isButtonSelected){
            g2D.drawRect(getX() + getWidth()/2 - buttonWidth/2, getY() + getHeight() + 10,
                    buttonWidth, buttonHeight);
        }

        g2D.setFont(new Font("Eight Bit Dragon", Font.PLAIN, 40));
        g2D.drawString("Save",
                getX() + getWidth()/2 - g2D.getFontMetrics().stringWidth("Save")/2,
                getY() + getHeight() + g2D.getFontMetrics().getHeight() + 25);

    }

    void validate(){
        for (int i = 0; i < categoryValues.size(); i++){
            if (categoryValues.get(i).isEmpty()) categorySetValues.get(i).apply("0");
            else categorySetValues.get(i).apply(categoryValues.get(i));
        }
    }
}
