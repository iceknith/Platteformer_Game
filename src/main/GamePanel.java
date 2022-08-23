package main;
import GameObjects.Player;
import handlers.KeyHandler;

import java.awt.*;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    // Screen Settings
    final int width = 750;
    final int height = 500;

    // game loop variables
    final int fps = 60;
    final double frameInterval = 1000000000/fps;
    double deltaTime = 0;
    long lastFrameTime = System.nanoTime();
    long currentFrameTime = System.nanoTime();
    boolean is_game_running = true;
    long timer;
    int displayedFPS = 0;

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

    //temporary
    public static Player player = new Player();

    @Override
    public void run() {

        lastFrameTime = System.nanoTime();
        timer = System.nanoTime();
        // game loop
        while (is_game_running) {

            currentFrameTime = System.nanoTime();
            deltaTime += (currentFrameTime - lastFrameTime) / frameInterval;
            lastFrameTime = currentFrameTime;

            if(deltaTime >= 1){
                update();
                repaint();
                deltaTime = 0;
            }
            if (System.nanoTime() - timer >= 1000000000){
                System.out.println(displayedFPS+" fps");
                timer = System.nanoTime();
                displayedFPS = 0;
            }
        }
    }

    public void update(){
        if (keyHandler.isRightPressed){
            player.move(1);
        }
        if (keyHandler.isLeftPressed){
            player.move(-1);
        }
        if (keyHandler.isJumpPressed){
            player.jump();
        }
        player.forceY += 0.1;
        //player.pos[1] += player.forceY;
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);

        Graphics2D g2D = (Graphics2D) g;

        g2D.setColor(player.color);

        g2D.fill(player);

        System.out.println(player.getX()+" au temps : "+ System.nanoTime());

        g2D.dispose();
        displayedFPS++;
    }
}
