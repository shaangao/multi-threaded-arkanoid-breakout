package model;

import controller.Game;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Ball extends Sprite implements Movable {


    ////////////////////////////////////
    /////// FIELDS
    ////////////////////////////////////

    public static final int RADIUS = 6;
//    public static final Color COLOR = Color.WHITE;
    private final ImageIcon ballIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/ballgray.png")));
    private final ImageIcon superBallIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/ballred.png")));
    private final ImageIcon crossIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/cross.png")));
//    private final double SPEEDUP_STEP = 0.01;

    private double speed = 5;
    private double deltaX;
    private double deltaY;


    ////////////////////////////////////
    /////// CONSTRUCTOR
    ////////////////////////////////////

    public Ball() {
        setCenterX(300);
        setCenterY(500);
        setWidth(RADIUS * 2);
        setHeight(RADIUS * 2);
//        setColor(COLOR);

        setDeltaX(-3);
        setDeltaY(-4);
    }

    public Ball(double centerX, double centerY, double deltaX, double deltaY) {
        setCenterX(centerX);
        setCenterY(centerY);
        setWidth(RADIUS * 2);
        setHeight(RADIUS * 2);
//        setColor(COLOR);

        setDeltaX(deltaX);
        setDeltaY(deltaY);
    }


    ////////////////////////////////////
    /////// GETTERS AND SETTERS
    ////////////////////////////////////

    public int getRADIUS() {
        return RADIUS;
    }

//    public double getSPEEDUP_STEP() {
//        return SPEEDUP_STEP;
//    }

    public double getSpeed() {
        return speed;
    }

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }

//    public boolean isSuperBallActivated() {
//        return superBallActivated;
//    }

//    public boolean isRocketBallActivated() {
//        return rocketBallActivated;
//    }


    public void setDeltaX(double deltaX) {
        this.deltaX = deltaX;
    }

    public void setDeltaY(double deltaY) {
        this.deltaY = deltaY;
    }

//    public void setSuperBallActivated(boolean superBallActivated) {
//        this.superBallActivated = superBallActivated;
//    }


//    public void setRocketBallActivated(boolean rocketBallActivated) {
//        this.rocketBallActivated = rocketBallActivated;
//    }




    ////////////////////////////////////
    /////// METHODS
    ////////////////////////////////////


    public void multiplyOneBall() {

//        for (Ball ball : CommandCenter.getInstance().getBallsOnScreen()) {
            double centerX = getCenterX();
            double centerY = getCenterY();
            double deltaX = getDeltaX();
            double deltaY = getDeltaY();
            double ballSpeed = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
//            double ballRadians = Math.atan2(deltaY, deltaX);
//            getOpsQueue().enqueue(new Ball(centerX, centerY, (int) (ballSpeed * Math.cos(ballRadians + Math.PI / 3)), (int) (ballSpeed * Math.sin(ballRadians + Math.PI / 3))), GameOp.Action.ADD);
//            getOpsQueue().enqueue(new Ball(centerX, centerY, (int) (ballSpeed * Math.cos(ballRadians - Math.PI / 3)), (int) (ballSpeed * Math.sin(ballRadians - Math.PI / 3))), GameOp.Action.ADD);
            double randomRadians1 = (double) (Game.R.nextInt(60) + 20) / 100 * Math.PI;
            CommandCenter.getInstance().getOpsQueue().enqueue(new Ball(centerX, centerY, Math.round(ballSpeed * Math.cos(randomRadians1) * 1.02), Math.round(ballSpeed * Math.sin(randomRadians1) * 1.02)), GameOp.Action.ADD);
//            CommandCenter.getInstance().getOpsQueue().enqueue(new Ball(centerX, centerY,
//                    (int) (deltaX / 1.5 + somePosNegValue(10)), (int) (deltaY / 1.5 + somePosNegValue(10))), GameOp.Action.ADD);
//            CommandCenter.getInstance().getOpsQueue().enqueue(new Ball(centerX, centerY,
//                    (int) (deltaX / 1.5 + somePosNegValue(10)), (int) (deltaY / 1.5 + somePosNegValue(10))), GameOp.Action.ADD);
            double randomRadians2 = (double) (Game.R.nextInt(60) + 120) / 100 * Math.PI;
            CommandCenter.getInstance().getOpsQueue().enqueue(new Ball(centerX, centerY, Math.round(ballSpeed * Math.cos(randomRadians2) * 1.02), Math.round(ballSpeed * Math.sin(randomRadians2) * 1.02)), GameOp.Action.ADD);
//        }
    }


    // update ball position
    @Override
    public void move() {
        setCenterX((int) (getCenterX() + getDeltaX()));
        setCenterY((int) (getCenterY() + getDeltaY()));
    }

    // check if a ball fell to death
    public boolean fellDead(int deathY) {
        return getCenterY() > deathY;
//        return getCenterY() > Game.DIM.height;
//        Paddle paddle = CommandCenter.getInstance().getPaddle();
//        return getCenterY() > paddle.getCenterY() + paddle.getHeight();
    }

    // draw ball
    @Override
    public void draw(Graphics g) {
//        g.setColor(getColor());
//        g.fillOval(getCenterX() - getWidth() / 2, getCenterY() - getHeight() / 2, getWidth(), getHeight());
        if (CommandCenter.getInstance().isRocketBallActivated())
            g.drawImage(crossIcon.getImage(), (int) (getCenterX() - RADIUS * 2), (int) (getCenterY() - RADIUS * 2), RADIUS * 4, RADIUS * 4, null);

        if (CommandCenter.getInstance().isSuperBallActivated()) {
            g.drawImage(superBallIcon.getImage(), (int) (getCenterX() - RADIUS), (int) (getCenterY() - RADIUS), RADIUS * 2, RADIUS * 2, null);
        } else {
            g.drawImage(ballIcon.getImage(), (int) (getCenterX() - RADIUS), (int) (getCenterY() - RADIUS), RADIUS * 2, RADIUS * 2, null);
        }
    }
}
