package handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    static int rightKey = KeyEvent.VK_D;
    static int leftKey = KeyEvent.VK_A;

    static int upKey = KeyEvent.VK_W;
    static int downKey = KeyEvent.VK_S;
    static int selectKey = KeyEvent.VK_ENTER;
    static int suicideKey = KeyEvent.VK_E;
    static int jumpKey = KeyEvent.VK_SPACE;
    static int debugKey = KeyEvent.VK_F3;
    static int menuKey = KeyEvent.VK_ESCAPE;

    static int lastKeyPressed;

    public static boolean isRightPressed;
    public static boolean isLeftPressed;
    public static boolean isUpPressed;
    public static boolean isDownPressed;
    public static boolean isSelectPressed;
    public static boolean isJumpPressed;
    public static boolean isDebugKeyPressed;
    public static boolean isSuicideKeyPressed;
    public static boolean isMenuKeyPressed;

    public static long rightPressedTime = 0;
    public static long leftPressedTime = 0;

    @Override
    public void keyTyped(KeyEvent keyEvent) {
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int k = keyEvent.getKeyCode();

        if (k == rightKey){
            isRightPressed = true;
            if (rightPressedTime == 0) {
                rightPressedTime = System.nanoTime();
            }
        }
        if (k == leftKey){
            isLeftPressed = true;
            if (leftPressedTime == 0) {
                leftPressedTime = System.nanoTime();
            }
        }
        if (k == upKey){
            isUpPressed = true;
        }
        if (k == downKey){
            isDownPressed = true;
        }
        if (k == jumpKey){
            isJumpPressed = true;
        }
        if (k==debugKey){
            isDebugKeyPressed = !isDebugKeyPressed;
        }
        if (k == suicideKey){
            isSuicideKeyPressed = true;
        }
        if (k == menuKey){
            isMenuKeyPressed = true;
        }
        if (k == selectKey){
            isSelectPressed = true;
        }

        lastKeyPressed = k;
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        int k = keyEvent.getKeyCode();

        if (k == rightKey){
            isRightPressed = false;
            rightPressedTime = 0;
        }
        if (k == leftKey){
            isLeftPressed = false;
            leftPressedTime = 0;
        }
        if (k == upKey){
            isUpPressed = false;
        }
        if (k == downKey){
            isDownPressed = false;
        }
        if (k == jumpKey){
            isJumpPressed = false;
        }
        if (k == suicideKey){
            isSuicideKeyPressed = false;
        }
        if (k == menuKey){
            isMenuKeyPressed = false;
        }
        if (k == selectKey){
            isSelectPressed = false;
        }
    }

    public static void changeKey(String keyName, int key){
        switch (keyName){
            case "right" -> rightKey = key;
            case "left" -> leftKey = key;
            case "jump" -> jumpKey = key;
            case "suicide" -> suicideKey = key;
            case "debug" -> debugKey = key;
            case "menu" -> menuKey = key;
        }
    }

    public static int getKey(String keyName){
        switch (keyName){
            case "right" -> {
                return rightKey;
            }
            case "left" -> {
                return leftKey;
            }
            case "jump" -> {
                return jumpKey;
            }
            case "suicide" -> {
                return suicideKey;
            }
            case "debug" -> {
                return debugKey;
            }
            case "menu" -> {
                return menuKey;
            }
        }
        return -1;
    }

    public static void resetLastKeyPressed(){
        lastKeyPressed = -1;
    }

    public static int getLastKeyPressed(){
        return lastKeyPressed;
    }
}
