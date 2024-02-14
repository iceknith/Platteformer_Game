package GameObjects;

import handlers.KeyHandler;
import main.GamePanel;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.ArrayList;

public class TextObject extends GameObject2D{

    String rawText;
    ArrayList<String> text;
    int textSize;
    Font font;

    int nextWidth = 0, nextHeight = 0;

    TextObject(int x, int y, String txt, int size, String id, String subLvl) throws IOException {
        super(x, y, 0, 0, subLvl);
        type = "TextObject";
        name = type + id;

        hasPhysicalCollisions = false;

        setText(txt);
        setSize(size);

        sprite = new Sprite(readImageBuffered("assets/placeholder.png"), getHitbox());
    }

    TextObject(TextObject t) {
        super(t);

        rawText = t.rawText;
        text = t.text;
        textSize = t.textSize;
        font = t.font;
        nextWidth = t.nextWidth;
        nextHeight = t.nextHeight;
    }

    @Override
    public void update() throws Exception {
        super.update();

        //adjust the width & height, to display the text correctly
        if (nextWidth != 0){
            GamePanel.camera.deleteGOInGrid(this);
            setWidth(nextWidth);
            setHeight(nextHeight);
            GamePanel.camera.addGOInGrid(this);

            nextWidth = 0; nextHeight = 0;
        }
    }

    @Override
    public void draw(Graphics2D g2D, ImageObserver IO) {
        //adjust the width & height, to display the text correctly
        if (g2D.getFontMetrics(font).stringWidth(rawText) != getWidth()){
            nextWidth = g2D.getFontMetrics(font).stringWidth(rawText);
            nextHeight = g2D.getFontMetrics(font).getHeight();

            //if is in Level Editor, buffer an update, so that the width & height of text register correctly
            if (GamePanel.camera.level.hasLevelMaker){
                GamePanel.camera.level.getLvlMaker().updatableGo = this;
            }
        }

        int xOffset = 0;
        g2D.setFont(font);
        for (String txt : text){
            g2D.setColor(Color.decode(txt.substring(0, 8)));
            //draw key text
            if (txt.charAt(8) == '^'){
                final String str = KeyEvent.getKeyText(KeyHandler.getKey(txt.substring(9)));
                g2D.drawString(
                        str,
                        getX() + xOffset - GamePanel.camera.getScreenX(),
                        getY() + getHeight() - GamePanel.camera.getScreenY());
                xOffset += g2D.getFontMetrics(font).stringWidth(str);
            }
            //draw normal text
            else {
                final String str = txt.substring(8);
                g2D.drawString(
                        str,
                        getX() + xOffset - GamePanel.camera.getScreenX() ,
                        getY() + getHeight() - GamePanel.camera.getScreenY());
                xOffset += g2D.getFontMetrics(font).stringWidth(str);
            }
        }
    }
    public void setText(String newText){
        rawText = newText;
        text = computeText(newText);
    }
    public ArrayList<String> getText(){
        return text;
    }
    public ArrayList<String> computeText(String text){

        ArrayList<String> result = new ArrayList<>();
        int prevSavePoint = 1;
        for (int i = 1; i < text.length(); i++){
            //key
            switch (text.charAt(i)){
                case '#' -> {
                    result.add(subTxtFormatting(text.substring(prevSavePoint, i), false));
                    prevSavePoint = i;
                }
                case '<'-> {
                    if (prevSavePoint != i - 8 || text.charAt(prevSavePoint) != '#'){
                        result.add(subTxtFormatting(text.substring(prevSavePoint, i), false));
                        prevSavePoint = i;
                    }
                }
                case '>' -> {
                    if (text.charAt(prevSavePoint) == '<'){
                        result.add(subTxtFormatting(text.substring(prevSavePoint+1, i), true));
                        prevSavePoint = i+1;
                    }
                    else if (text.charAt(prevSavePoint) == '#' && text.charAt(prevSavePoint + 8) == '<'){
                        result.add(subTxtFormatting(text.substring(prevSavePoint, prevSavePoint+8) + text.substring(prevSavePoint+9, i), true));
                        prevSavePoint = i+1;
                    }
                }
            }
        }
        if (prevSavePoint < text.length()){
            result.add(subTxtFormatting(text.substring(prevSavePoint), false));
        }
        System.out.println(result);
        return result;
    }
    String subTxtFormatting(String subTxt, boolean isKey){
        if (subTxt.charAt(0) == '#'){
            if (isKey) return subTxt.substring(0,8) + "^" + subTxt.substring(8);
            return subTxt;
        }
        if (isKey) return "#FFFFFFF" + "^" + subTxt;
        return "#FFFFFFF" + subTxt;
    }

    public void setSize(int size){
        textSize = size;
        font = new Font("Eight Bit Dragon", Font.PLAIN, size);
    }
    public int getSize(){return textSize;}

    @Override
    public GameObject2D copy() throws IOException {
        return new TextObject(this);
    }

    @Override
    public TextObject getThisTextObject() throws Exception {
        return this;
    }
}
