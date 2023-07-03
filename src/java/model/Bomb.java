package model;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class Bomb extends Sprite implements Movable {

    ////////////////////////////////////
    /////// FIELDS
    ////////////////////////////////////

    private final int RADIUS = 10;
    private final int STEP = 4;
    private final Color COLOR = Color.RED;
    private final ImageIcon bombIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/nuc.png")));


    ////////////////////////////////////
    /////// CONSTRUCTOR
    ////////////////////////////////////

    public Bomb(double centerX, double centerY) {
        setCenterX(centerX);
        setCenterY(centerY);
        setWidth(RADIUS * 2);
        setHeight(RADIUS * 2);
//        setColor(COLOR);
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
        g.drawImage(bombIcon.getImage(), (int) (getCenterX() - getWidth() / 2), (int) (getCenterY() - getHeight() / 2), getWidth(), getHeight(), null);

    }
}
