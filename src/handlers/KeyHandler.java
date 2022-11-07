package handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.im.InputContext;

public class KeyHandler implements KeyListener {

    final int rightKey = KeyEvent.VK_D;
    final int leftKey = KeyEvent.VK_A;
    final int suicideKey = KeyEvent.VK_E;
    final int jumpKey = KeyEvent.VK_SPACE;
    final int debugKey = KeyEvent.VK_F3;

    public KeyHandler(){
        InputContext context = InputContext.getInstance();
        System.out.println(context.getLocale().toString());
    }


    public static boolean isRightPressed;
    public static boolean isLeftPressed;
    public static boolean isJumpPressed;
    public static boolean isDebugKeyPressed;
    public static boolean isSuicideKeyPressed;

    public static long rightPressedTime = 0;
    public static long leftPressedTime = 0;

    @Override
    public void keyTyped(KeyEvent keyEvent) {

    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int k = keyEvent.getKeyCode();
        switch (k) {
            case rightKey -> {
                isRightPressed = true;
                if (rightPressedTime == 0) {
                    rightPressedTime = System.nanoTime();
                }
            }
            case leftKey -> {
                isLeftPressed = true;
                if (leftPressedTime == 0) {
                    leftPressedTime = System.nanoTime();
                }
            }
            case jumpKey -> isJumpPressed = true;
            case debugKey -> isDebugKeyPressed = ! isDebugKeyPressed;
            case suicideKey -> isSuicideKeyPressed = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent keyEvent) {
        int k = keyEvent.getKeyCode();
        switch (k) {
            case rightKey -> {
                isRightPressed = false;
                rightPressedTime = 0;
            }
            case leftKey -> {
                isLeftPressed = false;
                leftPressedTime = 0;
            }
            case jumpKey -> isJumpPressed = false;
            case suicideKey -> isSuicideKeyPressed = false;
        }
    }
}
