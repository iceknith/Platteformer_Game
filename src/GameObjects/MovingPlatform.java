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

        //velocityX = speed;
        int distX = posX2 - posX1;
        int distY = posY2 - posY1;
        int maxDist = Math.max(Math.abs(distX), Math.abs(distY));

        velocityX = speed * distX/maxDist;
        velocityY = speed * distY/maxDist;
        if (Math.signum(distX) * getX() > Math.signum(distX) * posX2 &&
            Math.signum(distY) * getY() > Math.signum(distY) * posY2){
            int tempPosX1 = posX1, tempPosY1 = posY1;
            posX1 = posX2; posY1 = posY2;
            posX2 = tempPosX1; posY2 = tempPosY1;
        }

        move();
    }

    @Override
    public GameObject2D copy() throws IOException {
        return new MovingPlatform(this);
    }
}
