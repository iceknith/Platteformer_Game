package main;
import javax.swing.JFrame;
import java.awt.*;

public class Main {

    public static void main(String[] args) {

        GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = graphics.getDefaultScreenDevice();

        JFrame window = new JFrame("Platformer Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setUndecorated(true);
        window.setResizable(false);
        device.setFullScreenWindow(window);

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.setVisible(true);

        gamePanel.startGameThread();
    }
}
