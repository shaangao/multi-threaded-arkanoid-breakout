package model;

import javax.swing.*;
import java.awt.*;

public class Freebie extends Sprite implements Movable {

    ////////////////////////////////////
    /////// FIELDS
    ////////////////////////////////////

    private final int RADIUS = 6;
    private final int STEP = 3;
    private final Color COLOR = Color.YELLOW;
    private final ImageIcon freebieIcon = new ImageIcon(this.getClass().getResource("/images/bonus.png"));



    ////////////////////////////////////
    /////// CONSTRUCTOR
    ////////////////////////////////////

    public Freebie(double centerX, double centerY) {
        setCenterX(centerX);
        setCenterY(centerY);
        setWidth(RADIUS * 2);
        setHeight(RADIUS * 2);
//        setColor(COLOR);
    }


    ////////////////////////////////////
    /////// GETTERS AND SETTERS
    ////////////////////////////////////

    public int getRADIUS() {
        return RADIUS;
    }
    public int getSTEP() {
        return STEP;
    }
    public Color getCOLOR() {
        return COLOR;
    }


    ////////////////////////////////////
    /////// METHODS
    ////////////////////////////////////

    @Override
    public void move() {
        setCenterY(getCenterY() + STEP);
    }

    @Override
    public void draw(Graphics g) {
//        g.setColor(getColor());
//        g.fillOval(getCenterX() - getWidth() / 2, getCenterY() - getHeight() / 2, getWidth(), getHeight());
        g.drawImage(freebieIcon.getImage(), (int) (getCenterX() - getWidth() / 2), (int) (getCenterY() - getHeight() / 2), RADIUS * 2, RADIUS * 2, null);
    }
}
