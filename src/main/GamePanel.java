package main;
import GameObjects.*;
import handlers.KeyHandler;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    // Screen Settings
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    final int width = 750; //dim.width;
    final int height = 500; //dim.height;

    // game loop variables
    final int fps = 60;
    final double frameInterval = 1000000000f / fps;
    public static double deltaTime = 0;
    long lastFrameTime = System.nanoTime();
    long currentFrameTime = System.nanoTime();
    boolean is_game_running = true;

    Thread gameThread;
    KeyHandler keyHandler = new KeyHandler();

    public static Camera camera;

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
    public static Player player;

    @Override
    public void run() {
        try {
            camera = new Camera(width, height, 0, 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lastFrameTime = System.nanoTime();
        // game loop
        while (is_game_running) {

            currentFrameTime = System.nanoTime();
            deltaTime += currentFrameTime - lastFrameTime;
            lastFrameTime = currentFrameTime;

            if(deltaTime >= frameInterval){
                deltaTime = deltaTime / 100000000; //in tenth of a second

                try {
                    update();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                repaint();
                deltaTime = 0;
            }
        }
    }

    public void update() throws IOException {
        camera.updateCamera();
        player.updatePlayer();
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);

        if(camera == null){
            return;
        }

        Graphics2D g2D = (Graphics2D) g;


        for (GameObject2D go : GameObject2D.getVisible()) {
            g2D.setColor(go.getColor());
            g2D.fillRect(go.getX() - camera.getX(), go.getY() - camera.getY(), //g2D.drawImage(go.getSprite(),go.getX() - camera.getX, go.getY() - camera.getY(),
                    go.getWidth(), go.getHeight()); //go.getWidth(), go.getHeight(), this);
        }


        g2D.drawImage(player.getSprite(),player.getX() - camera.getX(), player.getY()- camera.getY(),
                player.getWidth(), player.getHeight(), this);

        //temporary
        //g2D.setColor(Color.red);
        //for (Rectangle b: camera.borders) {
        //    g2D.drawRect(b.x - camera.getX(), b.y - camera.getY(), b.width, b.height);
        //}


        //g2D.setColor(Color.blue);
        //g2D.draw(new Rectangle(width/2-50,0,100,height));
        //g2D.setColor(Color.green);
        //g2D.draw(new Rectangle(0,height/2-100,width,200));

        Toolkit.getDefaultToolkit().sync(); //IMPORTANT prevents visual lag

        g2D.dispose();
    }
}
