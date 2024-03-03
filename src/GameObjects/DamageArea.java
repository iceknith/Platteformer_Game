package GameObjects;

import main.GamePanel;

import java.io.IOException;

public class DamageArea extends Entity{

    double timer;
    int damageAmount;
    boolean damageEnemies;


    public DamageArea(int x, int y, int w, int h, double timer, int damageAmount, boolean damageEnemies, String subLvl) throws IOException {
        super(x, y, w, h, subLvl);
        type = "DamageArea";
        name = type;

        hasHP = false;
        hasPhysicalCollisions = false;
        doesDamage = true;

        this.timer = timer;
        this.damageAmount = damageAmount;
        this.damageEnemies = damageEnemies;

        sprite = new Sprite(readImageBuffered("assets/placeholder.png"), hitbox);
    }

    @Override
    public void update() throws Exception {
        super.update();

        timer -= GamePanel.deltaTime/10;
        for (GameObject2D go: getInBox(getWidth(), getHeight())){
            if (go.isEntity && go.getThisEntity().hasHP &&
                    (!damageEnemies || !go.getThisEntity().isEnemy)){
                go.getThisEntity().damage(damageAmount);
            }
        }

        if (timer <= 0) killThisEntity();
    }

    @Override
    public void reset() throws Exception {
        super.reset();

        killThisEntity();
    }
}
