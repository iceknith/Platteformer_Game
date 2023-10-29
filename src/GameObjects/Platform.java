package GameObjects;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class Platform extends GameObject2D{

    public char utilType;

    public Platform(int w, int h, int x, int y, char uType, String textureName, String id, String subLvlName) throws IOException {
        super(x,y,w,h,subLvlName);

        utilType = uType;
        type = "Platform_" + utilType + textureName;

        name = type+id;
        sprite = new Sprite(ImageIO.read(new File("assets/Platform/"+textureName+"/0.png")), hitbox);
    }

    public Platform(Platform p){
        super(p);
        utilType = p.utilType;
    }

    @Override
    public void update(){

    }

    public void collision(Entity e){

        switch (utilType){
            case 'w' -> GamePanel.camera.level.openSubLevel("win", false, true);
            case 'k' -> GameObject2D.getPlayer().death(GameObject2D.getPlayer().spawnPointPos);
        }
    }

    @Override
    public GameObject2D copy() {
        return new Platform(this);
    }
}
