package main;
import javax.swing.JFrame;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Main {

    public static void main(String[] args) {

        GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();

        JFrame window = new JFrame("Platformer Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setExtendedState(JFrame.MAXIMIZED_BOTH);
        window.setUndecorated(true);
        window.setResizable(false);
        window.setCursor( window.getToolkit().createCustomCursor(
                new BufferedImage( 1, 1, BufferedImage.TYPE_INT_ARGB ),
                new Point(),
                null ) );

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.setVisible(true);

        gamePanel.startGameThread();
    }
}
