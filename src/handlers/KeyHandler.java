package handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean isRightPressed;
    public boolean isLeftPressed;
    public boolean isJumpPressed;

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int k = keyEvent.getKeyCode();
        if (k == KeyEvent.VK_D){
            isRightPressed = true;
        }
        if (k == KeyEvent.VK_A){
            isLeftPressed = true;
        }
        if (k == KeyEvent.VK_SPACE){
            isJumpPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        int k = keyEvent.getKeyCode();
        if (k == KeyEvent.VK_D){
            isRightPressed = false;
        }
        if (k == KeyEvent.VK_A){
            isLeftPressed = false;
        }
        if (k == KeyEvent.VK_SPACE){
            isJumpPressed = false;
        }
    }
}
