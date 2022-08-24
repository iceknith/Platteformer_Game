package main;
import GameObjects.Platform;
import GameObjects.Player;
import handlers.KeyHandler;

import java.awt.*;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    // Screen Settings
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    final int width = dim.width;
    final int height = dim.height;

    // game loop variables
    final int fps = 60;
    final double frameInterval = 1000000000f / fps;
    public static double deltaTime = 0;
    long lastFrameTime = System.nanoTime();
    long currentFrameTime = System.nanoTime();
    boolean is_game_running = true;

    Thread gameThread;
    KeyHandler keyHandler = new KeyHandler();

    public GamePanel() {

        // setDefaults
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(new Color(0, 27, 122));
        this.setDoubleBuffered(false); // optimises rendering by using a buffer
        this.addKeyListener(keyHandler);
        this.setFocusable(true);
    }

    public void startGameThread() {

        gameThread = new Thread(this);
        gameThread.start();
    }

    Player player;

    @Override
    public void run() {

        //temporary
        player = new Player();
        new Platform("rectangle",width-200,300, 100, height-150, Color.LIGHT_GRAY);
        new Platform("rectangle", 100, 50, 300, height-350, Color.GRAY);
        new Platform("rectangle", 75, 25, 500, height-250, Color.GRAY);

        lastFrameTime = System.nanoTime();
        // game loop
        while (is_game_running) {

            currentFrameTime = System.nanoTime();
            deltaTime += currentFrameTime - lastFrameTime;
            lastFrameTime = currentFrameTime;

            if(deltaTime >= frameInterval){
                deltaTime = deltaTime / 100000000;
                update();
                repaint();
                deltaTime = 0;
            }
        }
    }

    public void update(){
        player.updatePlayer();
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g;

        g2D.setColor(player.getColor());
        g2D.fill(player.getHitbox());

        for (Platform p : Platform.visiblePlatforms) {
            g2D.setColor(p.getColor());
            g2D.fill(p.getHitbox());
        }
        Toolkit.getDefaultToolkit().sync(); //IMPORTANT prevents visual lag

        g2D.dispose();
    }
}
