package model;


import controller.Game;

import java.awt.*;

public abstract class Sprite {   // not all sprites are movable

    private double centerX;
    private double centerY;  //the center-point of this sprite
    private int width, height;   // width and height of sprite
    private Color color;    //the color of this sprite
    private int fade;    //use for fade-in/fade-out


//    private int deltaX, deltaY;    // not all sprites are movable

//    //every sprite has a team: friend, foe, floater, or debris.
//    private Movable.Team team;



    ////////////////////////////////////
    /////// GETTERS AND SETTERS
    ////////////////////////////////////

    public double getCenterX() {
        return centerX;
    }

    public double getCenterY() {
        return centerY;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Color getColor() {
        return color;
    }

//    public int getDeltaX() {
//        return deltaX;
//    }

//    public int getDeltaY() {
//        return deltaY;
//    }

    public int getFade() {
        return fade;
    }

//    public boolean isProtected() {
//        //by default, sprites are not protected
//        return false;
//    }

    public void setCenterX(double centerX) {
        this.centerX = centerX;
    }

    public void setCenterY(double centerY) {
        this.centerY = centerY;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setColor(Color color) {
        this.color = color;
    }

//    public void setDeltaX(int deltaX) {
//        this.deltaX = deltaX;
//    }

//    public void setDeltaY(int deltaY) {
//        this.deltaY = deltaY;
//    }

    public void setFade(int fade) {
        this.fade = fade;
    }



    ////////////////////////////////////
    /////// METHODS
    ////////////////////////////////////

    protected int somePosNegValue(int seed) {
        int randomNumber = Game.R.nextInt(seed);
        if (randomNumber % 2 == 0)
            randomNumber = -randomNumber;
        return randomNumber;
    }

    public void draw(Graphics g) {  // implemented separately in subclasses rn
//        //set the native color of the sprite
//        g.setColor(getColor());
//        render(g);
    }

//    public void draw(Graphics g, Color color) {
//        //set custom color
//        g.setColor(color);
//        render(g);
//    }

//    private void render(Graphics g) {}

}
