package GameObjects;

import main.GamePanel;

import java.io.IOException;
import java.util.ArrayList;

public class Entity extends GameObject2D{
    double maxSpeed;
    double acceleration;
    double friction;

    double airMaxSpeed;
    double airAcceleration;
    double airFriction;

    double speedConversionPercent;

    double jumpForce;
    double jumpingTime;
    double gravity;

    int jumps;
    int maxJumps;
    double maxJumpingTime;

    double velocityY;
    double velocityX;

    int prevX;
    int prevY;

    boolean isOnGround;
    boolean isJumping;
    boolean wasJumping;

    Entity(int x, int y, int w, int h, String subLvl){
        super(x, y, w, h, subLvl);
    }

    Entity(Entity e){
        super(e);
        maxSpeed = e.maxSpeed;
        acceleration = e.acceleration;
        friction = e.friction;

        airMaxSpeed = e.airMaxSpeed;
        airAcceleration = e.airAcceleration;
        airFriction = e.airFriction;

        speedConversionPercent = e.speedConversionPercent;

        jumpForce = e.jumpForce;
        jumpingTime = e.jumpingTime;
        gravity = e.gravity;

        jumps = e.jumps;
        maxJumps = e.maxJumps;
        maxJumpingTime = e.maxJumpingTime;

        velocityY = e.velocityY;
        velocityX = e.velocityX;
        prevX = e.prevX;
        prevY = e.prevY;

        isOnGround = e.isOnGround;
        isJumping = e.isJumping;
        wasJumping = e.wasJumping;
    }

    @Override
    public GameObject2D copy() throws IOException {
        return new Entity(this);
    }

    public double getVelocityX(){return velocityX;}

    public double getVelocityY(){return velocityY;}

    public int getPreviousX(){return prevX;}

    public int getPreviousY(){return prevY;}

    public boolean getOnGround() {return isOnGround;}

    void walk(int direction, double maxSpeed, double acceleration, double speedConversion){
        if (direction == - Math.signum(velocityX)){
            velocityX = -velocityX * speedConversion / 100;
            return;
        }
        if (velocityX * direction < maxSpeed){
            velocityX += direction * acceleration;
        }
        else{
            velocityX = maxSpeed * direction;
        }
    }

    void stop(int direction, double friction){
        velocityX -= direction * friction;
        if (velocityX * direction <= 0){ //if forceX has crossed 0 since we stopped moving
            velocityX = 0;
        }
    }

    void jump(double jumpForce){

        jumpingTime += GamePanel.deltaTime;

        if (jumpingTime > maxJumpingTime){
            isJumping = false;
        }

        velocityY = jumpForce*20 - jumpingTime;
    }

    void fall(){
        if (! isOnGround){
            velocityY -= gravity * GamePanel.deltaTime * 6;
        }
    }

    void collision(GameObject2D go) {

        if (getY() + getHeight() < go.getY() || getY() > go.getY() + go.getHeight() ||
            getX() > go.getX() + go.getWidth() || getX() + getWidth() < go.getX()){
            return;
        }

        if(getY() + getHeight() >= go.getY() && getPreviousY() + getHeight() < go.getPreviousY()){
            if (go.hasPhysicalCollisions){
                setY(go.getY() - getHeight() - 1);
                velocityY = go.getVelocityY();
            }
            go.collision(this);
            return;
        }
        if(getY() <= go.getY() + go.getHeight() && getPreviousY() > go.getPreviousY() + go.getHeight()){
            if (go.hasPhysicalCollisions){
                setY(go.getY() + go.getHeight() + 1);
                velocityY = go.getVelocityY();
            }
            go.collision(this);
            return;
        }
        if(getX() + getWidth() >= go.getX() && getPreviousX() + getWidth() < go.getPreviousX()){
            if (go.hasPhysicalCollisions){
                setX(go.getX() - getWidth() - 1);
                velocityX = go.getVelocityX();
            }
            go.collision(this);
            return;
        }
        if(getX() <= go.getX() + go.getWidth() && getPreviousX() > go.getPreviousX() + go.getWidth()){
            if (go.hasPhysicalCollisions){
                setX(go.getX() + go.getWidth() + 1);
                velocityX = go.getVelocityX();
            }
            go.collision(this);
        }
    }

    void checkGround(GameObject2D go){
        if (getY() + getHeight() + 2 < go.getY() || getY() > go.getY() + go.getHeight() ||
                getX() > go.getX() + go.getWidth() || getX() + getWidth() < go.getX()){
            return;
        }

        if(getY() + getHeight() + 2 >= go.getY() && getPreviousY() + getHeight() < go.getPreviousY() && go.hasPhysicalCollisions){
            isOnGround = true;
        }
    }

    ArrayList<GameObject2D> getNear(){

        ArrayList<int[]> thisEntityGridCells = GamePanel.camera.findRectPosInGrid(this);
        ArrayList<GameObject2D> result = new ArrayList<>();

        for ( int[] pos: thisEntityGridCells) {

            ArrayList<GameObject2D> cell = GamePanel.camera.getCellContent(pos[0], pos[1]);

            for (GameObject2D object: cell) {

                if (!result.contains(object) && object != this){

                    result.add(object);
                }
            }
        }
        return result;
    }

    void move() throws IOException {

        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round(velocityX * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round(velocityY * GamePanel.deltaTime)));

        isOnGround = false;
        for (GameObject2D go: getNear()){
            collision(go);
            checkGround(go);
        }
    }

    @Override
    public Player getThisPlayer() {
        return getThisEntityPlayer();
    }

    public Player getThisEntityPlayer(){
        return null;
    }
}
