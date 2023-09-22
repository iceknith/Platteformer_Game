package handlers;

import main.GamePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class MouseHandler implements MouseListener {

    public static boolean isLeftClickPressed;
    public static boolean isRightClickPressed;

    public static int getX(){
        Point point = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(point, GamePanel.getGamePannel());
        return point.x;
    }

    public static int getY(){
        Point point = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(point, GamePanel.getGamePannel());
        return point.y;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {
        switch (mouseEvent.getButton()){
            case 1 -> isLeftClickPressed = true;
            case 3 -> isRightClickPressed = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {
        switch (mouseEvent.getButton()){
            case 1 -> isLeftClickPressed = false;
            case 3 -> isRightClickPressed = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    public static void resetClicks(){
        isLeftClickPressed = false;
        isRightClickPressed = false;
    }
}
