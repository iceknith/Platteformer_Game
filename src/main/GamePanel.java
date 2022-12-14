package main;
import GameObjects.*;
import handlers.KeyHandler;
import handlers.MouseHandler;

import java.awt.*;
import java.io.IOException;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable {

    static GamePanel self;

    // Screen Settings
    Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
    final int width = dim.width;
    final int height = dim.height;


    // game loop variables
    final int fps = 120;
    final double frameInterval = 1000000000f / fps;
    public static double deltaTime = 0;

    long lastFrameTime = System.nanoTime();
    long currentFrameTime = System.nanoTime();
    boolean is_game_running = true;

    int activeFps = 0;
    int displayedFps = 0;
    double timeFps;

    Thread gameThread;
    KeyHandler keyHandler = new KeyHandler();
    MouseHandler mouseHandler = new MouseHandler();

    public static Camera camera;

    public GamePanel() {

        // setDefaults
        this.setPreferredSize(new Dimension(width, height));
        this.setBackground(new Color(0, 27, 122));
        this.setDoubleBuffered(false); // optimises rendering by using a buffer
        this.addKeyListener(keyHandler);
        this.addMouseListener(mouseHandler);
        this.setFocusable(true);
        self = this;

        System.out.println(width + " : " + height);
    }

    public static GamePanel getGamePannel(){
        return self;
    }

    public void startGameThread() {

        gameThread = new Thread(this);
        gameThread.start();
    }


    @Override
    public void run() {
        try {
            camera = new Camera(width, height, 0, 0);
            camera.setNextLevel("menu");
            camera.loadNextLevel();
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
                deltaTime = deltaTime / 100000000; //in seconds
                timeFps += deltaTime;
                activeFps += 1;

                if (timeFps >= 10){
                    displayedFps = activeFps;
                    timeFps = 0;
                    activeFps = 0;
                }
                if (camera.isOperational){
                    try {
                        update();

                        if (camera.hasNextLevel()){
                            camera.loadNextLevel();
                        }

                    } catch (IOException | FontFormatException e) {
                        throw new RuntimeException(e);
                    }

                    repaint();

                    deltaTime = 0;
                }

            }
        }
    }

    public void update() throws IOException, FontFormatException {
        camera.updateAll();
    }

    public void paintComponent(Graphics g){

        super.paintComponent(g);

        if(! camera.isOperational){
            return;
        }
        Graphics2D g2D = (Graphics2D) g;

        for (GameObject2D go : camera.getVisible()) {
            go.draw(g2D,this);
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
                if (! go.isGUI){
                    g2D.drawRect(go.getX() - camera.getX(), go.getY() - camera.getY(), go.getWidth(), go.getHeight());
                }
            }

            //center of screen
            g2D.setColor(Color.blue);
            g2D.drawOval(width/2-5,height/2-5,10,10);

            //fps
            g2D.setColor(Color.white);
            g2D.setFont(new Font("Sans Serif", Font.BOLD, 17));
            g2D.drawString("FPS : " + displayedFps, 15, 25);

            //player info
            g2D.setFont(new Font("Sans Serif", Font.PLAIN, 12));
            g2D.drawString("X : " + GameObject2D.getPlayer().getX(),15,60);
            g2D.drawString("Y : " + GameObject2D.getPlayer().getY(),15,75);
            g2D.drawString("Velocity X : " + GameObject2D.getPlayer().getVelocityX(),15,100);
            g2D.drawString("Velocity Y : " + GameObject2D.getPlayer().getVelocityY(),15,115);
            g2D.drawString("Is On Ground : " + GameObject2D.getPlayer().getOnGround(),15,130);

            //camera info
            g2D.drawString("Camera Velocity X : " + camera.getVelocityX(),15,155);
            g2D.drawString("Camera Velocity Y : " + camera.getVelocityY(),15,170);

        }

        Toolkit.getDefaultToolkit().sync(); //IMPORTANT prevents visual lag

        g2D.dispose();
    }
}
