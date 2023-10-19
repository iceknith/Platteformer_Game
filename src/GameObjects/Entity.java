package GameObjects;

import main.GamePanel;

import java.io.IOException;
import java.util.ArrayList;

public class Entity extends GameObject2D{

    double speedThreshold;
    double earlySpeed;
    double maxSpeed;
    double earlyAcceleration;
    double lateAcceleration;
    double friction;

    double airSpeedThreshold;
    double airEarlySpeed;
    double airMaxSpeed;
    double airEarlyAcceleration;
    double airLateAcceleration;
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
    boolean wasOnGround;
    boolean isJumping;
    boolean wasJumping;

    Entity(int x, int y, int w, int h, String subLvl){
        super(x, y, w, h, subLvl);
    }

    Entity(Entity e){
        super(e);
        maxSpeed = e.maxSpeed;
        earlyAcceleration = e.earlyAcceleration;
        friction = e.friction;

        airMaxSpeed = e.airMaxSpeed;
        airEarlyAcceleration = e.airEarlyAcceleration;
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
        wasOnGround = e.wasOnGround;
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

    void walk(int direction,
              double earlySpeed, double maxSpeed,
              double maxSpeedThreshold,
              double earlyAcceleration, double lateAcceleration,
              double speedConversion){

        //if you changed direction
        if (direction == - Math.signum(velocityX)){
            velocityX = -velocityX * speedConversion / 100;
            return;
        }
        //earlyAcceleration
        if (velocityX * direction < earlySpeed){
            velocityX += direction * earlyAcceleration;
        }
        //lateAcceleration
        else if (velocityX * direction < maxSpeed) {
            velocityX += direction * lateAcceleration;
        }
        //lateDeceleration (if your speed is greater than the maxSpeed + speedThreshold)
        else if (velocityX * direction > maxSpeed + maxSpeedThreshold){
            velocityX -= direction * lateAcceleration;

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
            velocityY = Math.max(-150, velocityY - (gravity * GamePanel.deltaTime * 6));
        }
    }

    boolean collision(GameObject2D go) {

        if (getY() + getHeight() < go.getY() || getY() > go.getY() + go.getHeight() ||
            getX() > go.getX() + go.getWidth() || getX() + getWidth() < go.getX()){
            return false;
        }
        if(getX() + getWidth() >= go.getX() && getPreviousX() + getWidth() < go.getPreviousX()){
            if (go.hasPhysicalCollisions){
                setX(go.getX() - getWidth() - 1);
                velocityX = go.getVelocityX();
            }
            go.collision(this);
            return true;
        }

        if(getX() <= go.getX() + go.getWidth() && getPreviousX() > go.getPreviousX() + go.getWidth()){
            if (go.hasPhysicalCollisions){
                setX(go.getX() + go.getWidth() + 1);
                velocityX = go.getVelocityX();
            }
            go.collision(this);
            return true;
        }

        if(getY() + getHeight() >= go.getY() && getPreviousY() + getHeight() <= go.getPreviousY()){
            if (go.hasPhysicalCollisions){
                isOnGround = true;
                setY(go.getY() - getHeight() - 1);
                velocityY = go.getVelocityY();
            }
            go.collision(this);
            return true;
        }

        if(getY() <= go.getY() + go.getHeight() && getPreviousY() > go.getPreviousY() + go.getHeight()){
            if (go.hasPhysicalCollisions){
                setY(go.getY() + go.getHeight() + 1);
                velocityY = go.getVelocityY();
            }
            go.collision(this);
            return true;
        }
        return false;
    }

    void checkGround(GameObject2D go){
        if (wasOnGround && go.hasPhysicalCollisions){
            if (getY() + getHeight() < go.getY() - 2 || getY() > go.getY() + go.getHeight() ||
                    getX() > go.getX() + go.getWidth() || getX() + getWidth() < go.getX()){
                return;
            }

            isOnGround = getY() + getHeight() >= go.getY() - 2 && getPreviousY() + getHeight() <= go.getPreviousY();
        }
    }

    ArrayList<GameObject2D> getNear(){

        ArrayList<int[]> thisEntityGridCells = GamePanel.camera.findRectPosInGrid(this, 0, 0, 0, 2);
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

    void move() {

        GamePanel.camera.deleteGOInGrid(this);
        prevX = getX();
        prevY = getY();
        setX((int) (getX() + Math.round(velocityX * GamePanel.deltaTime)));
        setY((int) (getY() - Math.round(velocityY * GamePanel.deltaTime)));
        GamePanel.camera.addGOInGrid(this);

        wasOnGround = isOnGround;
        isOnGround = false;
        for (GameObject2D go: getNear()){
            checkGround(go);
            collision(go);
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
