package GameObjects;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class MovingPlatform extends Entity{

    int posX1, posY1, posX2, posY2;
    final double animSpeed = 5;
    double speed;

    MovingPlatform(int x, int y, int x2, int y2, int w, int h, String animName, int framesCount, String id, String subLvl) throws IOException {
        this(x, y, x2, y2, w, h, 20, animName, framesCount, id, subLvl);
    }

    MovingPlatform(int x, int y, int x2, int y2, int w, int h, int s, String animName, int framesCount, String id, String subLvl) throws IOException {
        super(x, y, w, h, subLvl);

        posX1 = x;
        posY1 = y;
        posX2 = x2;
        posY2 = y2;

        speed = s;

        type = "MovingPlatform_" + animName;
        name = type+id;

        sprite = new Sprite(ImageIO.read(new File("assets/MovingPlatform/"+animName+"/0.png")), hitbox);
        setAnimation(getAnimationList("MovingPlatform",animName, framesCount), animSpeed);
    }

    MovingPlatform(MovingPlatform m){
        super(m);

        speed = m.speed;

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

        velocityX = speed;
        /*int distX = posX2 - getX();
        int distY = posY2 - getY();
        if (Math.pow(distX, 2) + Math.pow(distY, 2) >= Math.pow(earlySpeed, 2)){ //if distance > speed

            if (distX > distY){
                velocityY = earlySpeed * ((double) distY/distX);
                velocityX = earlySpeed * (1 - ((double) distY/distX));
            }
            else{
                velocityX = earlySpeed * ((double) distX/distY);
                velocityY = earlySpeed * (1 - ((double) distX/distY));
            }

        }
        else { //if not turn around
            int tempX1, tempY1;
            tempX1 = posX1; tempY1 = posY1;
            posX1 = posX2; posY1 = posY2;
            posX2 = tempX1; posY2 = tempY1;

            velocityX = 0; velocityY = 0;
        }*/

        move();
    }

    @Override
    public GameObject2D copy() throws IOException {
        return new MovingPlatform(this);
    }
}
