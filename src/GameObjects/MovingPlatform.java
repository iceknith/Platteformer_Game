package GameObjects;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class MovingPlatform extends Entity{

    int posX1, posY1, posX2, posY2;
    final double animSpeed = 5;
    int travelTime;
    double friction;

    double initTime;

    MovingPlatform(int x, int y, int x2, int y2, int w, int h, String animName, int framesCount, String id, String subLvl) throws IOException {
        this(x, y, x2, y2, w, h, 20, animName, framesCount, id, subLvl);
    }

    MovingPlatform(int x, int y, int x2, int y2, int w, int h, int s, String animName, int framesCount, String id, String subLvl) throws IOException {
        super(x, y, w, h, subLvl);

        posX1 = x;
        posY1 = y;
        posX2 = x2;
        posY2 = y2;

        travelTime = s;
        friction = 2.5;
        initTime = (double) System.nanoTime() / 1000000000;

        type = "MovingPlatform_" + animName;
        name = type+id;


        sprite = new Sprite(ImageIO.read(new File("assets/MovingPlatform/"+animName+"/0.png")), hitbox);
        setAnimation(getAnimationList("MovingPlatform",animName, framesCount), animSpeed);
    }

    MovingPlatform(MovingPlatform m){
        super(m);

        travelTime = m.travelTime;

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

        //velocityX = speed;
        double time = ((double) System.nanoTime() / 1000000000) - initTime;
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
    public MovingPlatform getThisMovingPlatform(){
        return this;
    }
}
