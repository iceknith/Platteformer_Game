package main;
import GameObjects.*;
import handlers.KeyHandler;

import java.awt.*;
import java.io.IOException;

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
            camera.loadLevel("1");
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
        camera.update();
        for (GameObject2D go: camera.getVisible()) {
            go.update();
        }
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);

        if(camera == null){
            return;
        }
        Graphics2D g2D = (Graphics2D) g;

        for (GameObject2D go : camera.getVisible()) {
            g2D.drawImage(go.getSprite().getImage(),
                        go.getSprite().getOffsetX(go.getHitbox()) - camera.getX() ,
                        go.getSprite().getOffsetY(go.getHitbox()) - camera.getY(),
                        go.getSprite().getWidth(), go.getSprite().getHeight(), this);
        }

        if (KeyHandler.isDebugKeyPressed){
            //camera borders
            //hard borders
            g2D.setColor(Color.red);
            g2D.draw(new Rectangle(width/2-camera.getHardBorderX(),0,2*camera.getHardBorderX(),height));
            g2D.draw(new Rectangle(0,height/2-camera.getHardBorderY(),width,2*camera.getHardBorderY()));

            //soft borders
            g2D.setColor(Color.green);
            g2D.draw(new Rectangle(width/2-camera.getSoftBorderX(),0,2*camera.getSoftBorderX(),height));
            g2D.draw(new Rectangle(0,height/2-camera.getSoftBorderY(),width,2*camera.getSoftBorderY()));

            //hitboxes
            g2D.setColor(Color.white);
            for (GameObject2D go : camera.getVisible()){
                g2D.drawRect(go.getX() - camera.getX(), go.getY() - camera.getY(), go.getWidth(), go.getHeight());
            }

            //center of screen
            g2D.setColor(Color.blue);
            g2D.drawOval(width/2-5,height/2-5,10,10);

        }

        Toolkit.getDefaultToolkit().sync(); //IMPORTANT prevents visual lag

        g2D.dispose();
    }
}
