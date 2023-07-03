package model;


import controller.Game;

import java.awt.*;
import java.util.List;

public class Brick extends Sprite {

    ////////////////////////////////////
    /////// FIELDS
    ////////////////////////////////////

    public static final int WIDTH = 18;  // leave 2 pixels blank between bricks. also must be even (see BrickMap)
    public static final int HEIGHT = 18;

    private int lives;

    private enum Spawns {
//        FREEBIE    // for test
        NONE, FREEBIE, BOMB
    }
    private Spawns spawns;


    ////////////////////////////////////
    /////// CONSTRUCTOR
    ////////////////////////////////////

    public Brick(int lives, int centerX, int centerY) {
        setCenterX(centerX);
        setCenterY(centerY);
        setWidth(WIDTH);
        setHeight(HEIGHT);

        setLives(lives);
        setSpawnsRandom();   // randomly determine what will be spawned when the brick is killed
//        setColor(liveToColorWinter.get(lives));
    }


    ////////////////////////////////////
    /////// GETTERS AND SETTERS
    ////////////////////////////////////

    public Spawns getSpawns() {
        return spawns;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = Math.max(lives, 0);
        if (lives == 0) {
            CommandCenter.getInstance().getOpsQueue().enqueue(this, GameOp.Action.REMOVE);
            if (CommandCenter.getInstance().getBallsOnScreen().size() <= 5 && spawns == Spawns.FREEBIE) {    // if there are too many balls on screen, bonus does not spawn
                CommandCenter.getInstance().getOpsQueue().enqueue(new Freebie(getCenterX(), getCenterY()), GameOp.Action.ADD);
            }
            else if (CommandCenter.getInstance().getLevel() > 1 && spawns == Spawns.BOMB) {
                CommandCenter.getInstance().getOpsQueue().enqueue(new Bomb(getCenterX(), getCenterY()), GameOp.Action.ADD);
            }
            else {}
        }
    }

    public void setSpawns(Spawns spawns) {
        this.spawns = spawns;

    }

    public void setSpawnsRandom() {
        this.spawns = randomEnum(Spawns.class);
    }


    ////////////////////////////////////
    /////// METHODS
    ////////////////////////////////////

    // randomly pick a value from enum
    // https://stackoverflow.com/questions/1972392/pick-a-random-value-from-an-enum
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = Game.R.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }

    public void findNeighborsAndDecrementLives(List<Brick> brickList) {
        double centerX = getCenterX();
        double centerY = getCenterY();
        int maxSearchWidthHalf = WIDTH + 2;
        int maxSearchHeightHalf = HEIGHT + 2;
        for (Brick brick : brickList) {
            if (brick.getLives() > 0 && Math.abs(brick.getCenterX() - centerX) <= maxSearchWidthHalf
                    && Math.abs(brick.getCenterY() - centerY) <= maxSearchHeightHalf) {
                brick.setLives(brick.getLives() - 1);
                CommandCenter.getInstance().setScore(CommandCenter.getInstance().getScore() + 5);
            }
        }
    }

    public void draw(Graphics g) {
        g.setColor(CommandCenter.getInstance().getThemeInUse().getBrickLiveToColor().get(lives));
//        g.setColor(getColor());
        g.fillRect((int) (getCenterX() - WIDTH / 2), (int) (getCenterY() - HEIGHT / 2), WIDTH, HEIGHT);
    }
}
