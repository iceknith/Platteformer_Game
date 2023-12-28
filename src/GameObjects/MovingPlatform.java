package GameObjects;

import main.GamePanel;

import java.awt.image.BufferedImage;
import java.io.IOException;
import static java.lang.Math.cos;

public class MovingPlatform extends Entity{

    int posX1, posY1, posX2, posY2;
    double animSpeed;
    int travelTime;
    double friction;
    double time = 0;

    MovingPlatform(int x, int y, int x2, int y2, int w, int h, char uType, String animName, int framesCount, String id, String subLvl) throws IOException {
        this(x, y, x2, y2, w, h, 5, uType, animName, framesCount, id, subLvl);
    }

    MovingPlatform(int x, int y, int x2, int y2, int w, int h, int s, char uType, String animName, int framesCount, String id, String subLvl) throws IOException {
        super(x, y, w, h, subLvl);

        utilType = uType;

        posX1 = x;
        posY1 = y;
        posX2 = x2;
        posY2 = y2;

        travelTime = s;

        type = "MovingPlatform_" + animName;
        name = type+id;

        switch (utilType){
            case 'i' -> friction = 0.5;
            default -> friction = 2.5;
        }

        switch (animName){
            case "killers/saw" -> animSpeed = 1;
            default ->  animSpeed = 5;
        }

        BufferedImage img = readImageBuffered("assets/MovingPlatform/"+animName+"/0.png");
        sprite = new Sprite(img, hitbox);

        if (utilType == 's'){
            // Set the sprite to a fixed size
            Sprite sprite2 = new Sprite(img, 1);
            sprite = new Sprite(img, (double) sprite.getWidth() /sprite2.getWidth());

            // Change the size of the hitbox (half of the original)
            hitbox.setBounds(
                    (int) (getX()+getWidth()*0.25),
                    (int) (getY()+getHeight()*0.25),
                    (int) (getWidth()*0.5),
                    (int) (getHeight()*0.5));
        }
        setAnimation(getAnimationList("MovingPlatform", animName, framesCount), animSpeed);
    }

    MovingPlatform(MovingPlatform m){
        super(m);
        friction = m.friction;

        travelTime = m.travelTime;
        time = m.time;

        posX1 = m.posX1;
        posY1 = m.posY1;
        posX2 = m.posX2;
        posY2 = m.posY2;
        setAnimation(m.currentAnimation, m.animSpeed);
    }

    @Override
    public void update() throws Exception {
        super.update();
        animate();

        time += GamePanel.deltaTime/10;

        //Find new pos
        double newX = (double) (posX1 + posX2) /2 + ((double) (posX1 - posX2)/2) * cos(time*Math.PI/travelTime);
        double newY = (double) (posY1 + posY2) /2 + ((double) (posY1 - posY2)/2) * cos(time*Math.PI/travelTime);

        //System.out.println(posX1 + " < " + getX() + " < " + posX2);
        velocityX = newX - getX();
        velocityY = getY() - newY;

        move();
    }

    @Override
    public double getFriction(){
        return friction;
    }

    @Override
    public GameObject2D copy() throws IOException {
        return new MovingPlatform(this);
    }

    @Override
    public void collision(Entity e){
        switch (utilType){
            case 'w' -> GamePanel.camera.level.openSubLevel("win", false, true);
            case 'k', 's' -> GameObject2D.getPlayer().death(GameObject2D.getPlayer().spawnPointPos);
        }
    }

    @Override
    public MovingPlatform getThisMovingPlatform(){
        return this;
    }
}
