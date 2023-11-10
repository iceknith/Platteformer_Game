package GameObjects;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class ImageObject extends GameObject2D{

    final double animSpeed = 2.5;

    public ImageObject(int x, int y, String animName, int framesCount, String id, String subLvlName) throws IOException {
        super(x,y,0,0,subLvlName);

        type = "ImageObject_" + animName;
        name = type+id;
        hasPhysicalCollisions = false;

        sprite = new Sprite(ImageIO.read(new File("assets/ImageObject/"+animName+"/0.png")), 3);
        hitbox.setSize(sprite.width, sprite.height);

        setAnimation(getAnimationList("ImageObject",animName, framesCount), animSpeed);
    }

    public ImageObject(int w, int h, int x, int y, String animName, int framesCount, String id, String subLvlName) throws IOException {
        super(x,y,w,h,subLvlName);

        type = "ImageObject_" + animName;
        name = type+id;
        hasPhysicalCollisions = false;

        sprite = new Sprite(ImageIO.read(new File("assets/ImageObject/"+animName+"/0.png")), hitbox);

        setAnimation(getAnimationList("ImageObject",animName, framesCount), animSpeed);
    }
    public ImageObject(ImageObject i){
        super(i);
    }
    public void update(){
        animate();
    }

    @Override
    public GameObject2D copy() throws IOException {
        return new ImageObject(this);
    }
}
