package handlers;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

public class KeyHandler implements KeyListener {

    static int rightKey = KeyEvent.VK_D;
    static int leftKey = KeyEvent.VK_A;
    static int upKey = KeyEvent.VK_W;
    static int downKey = KeyEvent.VK_S;
    static int selectKey = KeyEvent.VK_ENTER;
    static int suicideKey = KeyEvent.VK_E;
    static int resetKey = KeyEvent.VK_R;
    static int jumpKey = KeyEvent.VK_SPACE;
    static int debugKey = KeyEvent.VK_F3;
    static int menuKey = KeyEvent.VK_ESCAPE;
    static  int launchKey = KeyEvent.VK_F5;
    static int[] instantQuitKeys = {KeyEvent.VK_ESCAPE, KeyEvent.VK_BACK_SPACE};

    static int lastKeyPressed = -1;
    static String lastStrTyped = "";

    public static boolean isRightPressed;
    public static boolean isLeftPressed;
    public static boolean isUpPressed;
    public static boolean isDownPressed;
    public static boolean isSelectPressed;
    public static boolean isJumpPressed;
    public static boolean isDebugKeyPressed;
    public static boolean isSuicideKeyPressed;
    public static boolean isResetKeyPressed;
    public static boolean isMenuKeyPressed;
    public static boolean isLaunchKeyPressed;
    boolean[] isInstantQuitKeysPressed = {false, false};

    public static long rightPressedTime = 0;
    public static long leftPressedTime = 0;

    public static void initialise() throws IOException {
        FileInputStream keyFile = new FileInputStream("settings/keys.settings");
        BufferedInputStream reader = new BufferedInputStream(keyFile);

        int ch;
        while ((ch = reader.read()) != -1){
            char keyName = (char) (ch);

            switch (keyName){
                case 'r' -> rightKey = reader.read()*256 + reader.read();
                case 'l' -> leftKey = reader.read()*256 + reader.read();
                case 'u' -> upKey = reader.read()*256 + reader.read();
                case 'd' -> downKey = reader.read()*256 + reader.read();
                case 's' -> selectKey = reader.read()*256 + reader.read();
                case 'e' -> suicideKey = reader.read()*256 + reader.read();
                case 'h' -> resetKey = reader.read()*256 + reader.read();
                case 'j' -> jumpKey = reader.read()*256 + reader.read();
            }
        }
    }

    public static void saveKeys() throws IOException {
        File keyFile = new File("settings/keys.settings");
        keyFile.createNewFile();

        FileOutputStream fw = new FileOutputStream(keyFile, false);

        //right key
        fw.write('r');
        fw.write(rightKey/256);
        fw.write(rightKey%256);

        //left key
        fw.write('l');
        fw.write(leftKey/256);
        fw.write(leftKey%256);

        //up key
        fw.write('u');
        fw.write(upKey/256);
        fw.write(upKey%256);

        //down key
        fw.write('d');
        fw.write(downKey/256);
        fw.write(downKey%256);

        //select key
        fw.write('s');
        fw.write(selectKey/256);
        fw.write(selectKey%256);

        //suicide key
        fw.write('e');
        fw.write(suicideKey/256);
        fw.write(suicideKey%256);

        //reset key
        fw.write('h');
        fw.write(resetKey/256);
        fw.write(resetKey%256);

        //jump key
        fw.write('j');
        fw.write(jumpKey/256);
        fw.write(jumpKey%256);
    }

    @Override
    public void keyTyped(KeyEvent keyEvent) {
        if (!String.valueOf(keyEvent.getKeyChar()).isEmpty()){
            lastStrTyped = String.valueOf(keyEvent.getKeyChar());
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        int k = keyEvent.getKeyCode();

        lastKeyPressed = k;

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
        if (k == resetKey){
            isResetKeyPressed = true;
        }
        if (k == menuKey){
            isMenuKeyPressed = true;
        }
        if (k == selectKey){
            isSelectPressed = true;
        }
        if (k == launchKey){
            isLaunchKeyPressed = true;
        }
        if (k == instantQuitKeys[0]){
            isInstantQuitKeysPressed[0] = true;
            if (isInstantQuitKeysPressed[1]) System.exit(0);
        }
        if (k == instantQuitKeys[1]){
            isInstantQuitKeysPressed[1] = true;
            if (isInstantQuitKeysPressed[0]) System.exit(0);
        }
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
        if (k == resetKey){
            isResetKeyPressed = false;
        }
        if (k == menuKey){
            isMenuKeyPressed = false;
        }
        if (k == selectKey){
            isSelectPressed = false;
        }
        if (k == launchKey){
            isLaunchKeyPressed = false;
        }
        if (k == instantQuitKeys[0]){
            isInstantQuitKeysPressed[0] = false;
        }
        if (k == instantQuitKeys[1]){
            isInstantQuitKeysPressed[1] = false;
        }
    }

    public static void changeKey(String keyName, int key) throws IOException {
        switch (keyName){
            case "right" -> rightKey = key;
            case "left" -> leftKey = key;
            case "up" -> upKey = key;
            case "down" -> downKey = key;
            case "jump" -> jumpKey = key;
            case "suicide" -> suicideKey = key;
            case "reset" -> resetKey = key;
            case "select" -> selectKey = key;
            case "debug" -> debugKey = key;
            case "menu" -> menuKey = key;
        }

        saveKeys();
    }

    public static int getKey(String keyName){
        switch (keyName){
            case "right" -> {return rightKey;}
            case "left" -> {return leftKey;}
            case "up" -> {return upKey;}
            case "down" -> {return downKey;}
            case "jump" -> {return jumpKey;}
            case "suicide" -> {return suicideKey;}
            case "reset" -> {return resetKey;}
            case "select" -> {return selectKey;}
            case "debug" -> {return debugKey;}
            case "menu" -> {return menuKey;}
        }
        return -1;
    }

    public static void resetLastKeyPressed(){
        lastKeyPressed = -1;
    }

    public static int getLastKeyPressed(){
        return lastKeyPressed;
    }

    public static void resetLastStrTyped(){lastStrTyped = "";}

    public static String getLastStrTyped(){
        return lastStrTyped;
    }

}
