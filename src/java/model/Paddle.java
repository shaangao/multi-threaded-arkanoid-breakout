package model;

import controller.Game;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Paddle extends Sprite implements Movable {


    ////////////////////////////////////
    /////// FIELDS
    ////////////////////////////////////

    private double step = 10;
    private final double ACCELERATE = 1.07;

//    public static final int FADE_INITIAL_VALUE = 51;

    private int lives;
    private boolean movingRight = false;
    private boolean movingLeft = false;
    private boolean accelerating = false;
    private boolean shield;

    private final ImageIcon paddleShield3 = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/shield3.png")));
    private final ImageIcon paddleShield2 = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/shield2.png")));
    private final ImageIcon paddleShield1 = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/shield1.png")));
    private final ImageIcon paddleUnshield3 = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/unshield3.png")));
    private final ImageIcon paddleUnshield2 = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/unshield2.png")));
    private final ImageIcon paddleUnshield1 = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/unshield1.png")));




    ////////////////////////////////////
    /////// CONSTRUCTOR
    ////////////////////////////////////

    public Paddle() {
        setCenterX(300);
        setCenterY(600);
        setWidth(60);
        setHeight(10);
        setColor(Color.GRAY);
        setLives(3);

//        setDeltaX(0);
//        setDeltaY(0);
    }


    ////////////////////////////////////
    /////// GETTERS AND SETTERS
    ////////////////////////////////////


    public int getLives() {
        return lives;
    }
    public double getStep() {
        return step;
    }
    public boolean isMovingRight() {
        return movingRight;
    }
    public boolean isMovingLeft() {
        return movingLeft;
    }
    public boolean isShield() {
        return shield;
    }
    public boolean isAccelerating() {
        return accelerating;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }
    public void setShield(boolean shield) {
        this.shield = shield;
    }
    public void setStep(double step) {
        this.step = step;
    }


    ////////////////////////////////////
    /////// METHODS
    ////////////////////////////////////

    // move the paddle
    public void moveLeft() {
        movingLeft = true;
        setStep(10);

    }

    public void moveRight() {
        movingRight = true;
        setStep(10);     // step acceleration needs to be reset when changing direction

    }

    public void stopMoving() {
        movingLeft = false;
        movingRight = false;
//        accelerateOff();
//        setStep(10);
    }

    // accelerate control
    public void accelerateOn() {
        accelerating = true;
    }
    public void accelerateOff() {
        accelerating = false;
        setStep(10);
    }


    // update model instance variables
    @Override
    public void move() {
        if (accelerating) {
            setStep(getStep() * ACCELERATE);
//            setStep(15);
        }

        // update paddle position
        if (movingRight) {
            setCenterX((int) Math.min(getCenterX() + step, Game.DIM.width - (Brick.WIDTH + 2) - getWidth() / 2));   // ensure the paddle stays inside the wall
        }
        if (movingLeft) {
            setCenterX((int) Math.max(getCenterX() - step,  (Brick.WIDTH + 2) + getWidth() / 2));   // ensure the paddle stays inside the wall
        }
    }

    // draw paddle
    @Override
    public void draw(Graphics g) {
        if (shield) {
//            g.setColor(Color.BLUE);
//            g.fillRect(getCenterX() - getWidth() / 2, getCenterY() - getHeight() / 2, getWidth(), getHeight());
            if (lives == 3) g.drawImage(paddleShield3.getImage(), (int) (getCenterX() - getWidth() / 2), (int) (getCenterY() - getHeight() / 2), getWidth(), getHeight(), null);
            else if (lives == 2) g.drawImage(paddleShield2.getImage(), (int) getCenterX() - getWidth() / 2, (int) getCenterY() - getHeight() / 2, getWidth(), getHeight(), null);
            else if (lives == 1) g.drawImage(paddleShield1.getImage(), (int) getCenterX() - getWidth() / 2, (int) getCenterY() - getHeight() / 2, getWidth(), getHeight(), null);
            else {}
        } else {
//            g.setColor(getColor());
//            g.fillRect(getCenterX() - getWidth() / 2, getCenterY() - getHeight() / 2, getWidth(), getHeight());
            if (lives == 3) g.drawImage(paddleUnshield3.getImage(), (int) getCenterX() - getWidth() / 2, (int) getCenterY() - getHeight() / 2, getWidth(), getHeight(), null);
            else if (lives == 2) g.drawImage(paddleUnshield2.getImage(), (int) getCenterX() - getWidth() / 2, (int) getCenterY() - getHeight() / 2, getWidth(), getHeight(), null);
            else if (lives == 1) g.drawImage(paddleUnshield1.getImage(), (int) getCenterX() - getWidth() / 2, (int) getCenterY() - getHeight() / 2, getWidth(), getHeight(), null);
            else {}
        }
    }

}
