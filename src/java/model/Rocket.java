package model;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Rocket extends Sprite implements Movable {

    ////////////////////////////////////
    /////// FIELDS
    ////////////////////////////////////

    private final double STEP = 20;

    private double deltaX;
    private double deltaY;
    private ImageIcon rocketIcon;


    ////////////////////////////////////
    /////// CONSTRUCTOR
    ////////////////////////////////////

    public Rocket(String direction, double centerX, double centerY) {
        setCenterX(centerX);
        setCenterY(centerY);
        switch (direction.toUpperCase()) {
            case "UP":
                deltaX = 0;
                deltaY = -STEP;
                setWidth(10);
                setHeight(20);
                rocketIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/rocketup.png")));
                break;
            case "DOWN":
                deltaX = 0;
                deltaY = STEP;
                setWidth(10);
                setHeight(20);
                rocketIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/rocketdown.png")));
                break;
            case "RIGHT":
                deltaX = STEP;
                deltaY = 0;
                setWidth(20);
                setHeight(10);
                rocketIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/rocketright.png")));
                break;
            case "LEFT":
                deltaX = -STEP;
                deltaY = 0;
                setWidth(20);
                setHeight(10);
                rocketIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/rocketleft.png")));
                break;
            default:
                break;
        }
    }

    ////////////////////////////////////
    /////// GETTERS AND SETTERS
    ////////////////////////////////////

    public double getDeltaX() {
        return deltaX;
    }

    public double getDeltaY() {
        return deltaY;
    }


    ////////////////////////////////////
    /////// METHODS
    ////////////////////////////////////

    @Override
    public void move() {
        setCenterX((int) (getCenterX() + deltaX));
        setCenterY((int) (getCenterY() + deltaY));
    }

    public void draw(Graphics g) {
        g.drawImage(rocketIcon.getImage(), (int) (getCenterX() - getWidth() / 2), (int) (getCenterY() - getHeight() / 2), getWidth(), getHeight(), null);
    }
}
