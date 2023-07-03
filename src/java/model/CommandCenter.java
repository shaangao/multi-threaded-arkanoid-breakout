package model;


import controller.Game;

import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;


public class CommandCenter {  // maintains current game state

    ////////////////////////////////////
    /////// FIELDS
    ////////////////////////////////////

    private static CommandCenter instance = null;
    private final GameOpsQueue opsQueue = new GameOpsQueue();
    public enum GameStates {
        WELCOME, LEVEL_INTRO, PREPARING, PAUSED, PLAYING, LEVEL_CLEARED, GAME_OVER
    }
    private GameStates gameState = GameStates.WELCOME;
    private boolean mouseFire = false;

    private Theme themeInUse = Game.availableThemes.get(0);    // the theme currently in use. default to the first theme. see Game class for all available themes.

    private int level;   // start from 1
    private long score;
    private int numBallLives;    // start from 4 when initGame: 1 active, 3 backups

    private boolean superBallActivated;  // reduces the lives of nearby bricks by 1 as well when bouncing off a brick
    private double superBallActivationTime;  // system time in mm when super ball is activated
    private final double SUPER_BALL_DURATION = 5000;   // the duration of each super ball activation
    private final double SUPER_BALL_COOL_DOWN_STEP =  1.0/300;  // cool down progress increment for each ANI_DELAY.
                                                                // cool-down time is 300 / FRAMES_PER_SECOND = 6s in total.
    private double superBallCoolDownProgress = 1;   // 0 - 1. cool down progress for the next available super ball

    private boolean rocketBallActivated;  // fires horizontal and vertical rockets when bouncing off a brick
    private double rocketBallActivationTime;
    private final double ROCKET_BALL_DURATION = 2000;
    private final double ROCKET_BALL_COOL_DOWN_STEP =  1.0/500;  // cool-down time is 10s in total.
    private double rocketBallCoolDownProgress = 1;

    private final int ROCKET_MATERIAL_NUM = 5;  // 5 nuc required for constructing 1 paddle rocket
    private int materialCollected;

    private final List<Ball> ballsOnScreen = new LinkedList<>();
    private Paddle paddle; // = new Paddle();
    private BrickMap brickMap; //= new BrickMap();    // refactor to List<Brick> brickList?
    private final List<Freebie> freebiesOnScreen = new LinkedList<>();
    private final List<Bomb> bombsOnScreen = new LinkedList<>();
    private final List<Rocket> rocketsOnScreen = new LinkedList<>();


    ////////////////////////////////////
    /////// CONSTRUCTOR
    ////////////////////////////////////

    // Constructor made private
    private CommandCenter() {}


    ////////////////////////////////////
    /////// GETTERS AND SETTERS
    ////////////////////////////////////

    // get/instantiate game state
    public static CommandCenter getInstance(){
        if (instance == null){
            instance = new CommandCenter();
        }
        return instance;
    }

    // other getters and setters
    public GameOpsQueue getOpsQueue() {
        return opsQueue;
    }
    public Theme getThemeInUse() {
        return themeInUse;
    }
    public int getLevel() {
        return level;
    }
    public long getScore() {
        return score;
    }

    public GameStates getGameState() {
        return gameState;
    }

    public int getNumBallLives() {
        return numBallLives;
    }
    public List<Ball> getBallsOnScreen() {
        return ballsOnScreen;
    }
    public Paddle getPaddle() {
        return paddle;
    }
    public BrickMap getBrickMap() {
        return brickMap;
    }
    public List<Rocket> getRocketsOnScreen() {
        return rocketsOnScreen;
    }
    public List<Freebie> getFreebiesOnScreen() {
        return freebiesOnScreen;
    }
    public List<Bomb> getBombsOnScreen() {
        return bombsOnScreen;
    }
    public boolean isSuperBallActivated() {
        return superBallActivated;
    }
    public boolean isRocketBallActivated() {
        return rocketBallActivated;
    }
    public double getSuperBallCoolDownProgress() {
        return superBallCoolDownProgress;
    }
    public double getRocketBallCoolDownProgress() {
        return rocketBallCoolDownProgress;
    }
    public int getROCKET_MATERIAL_NUM() {
        return ROCKET_MATERIAL_NUM;
    }
    public int getMaterialCollected() {
        return materialCollected;
    }
    public boolean isMouseFire() {
        return mouseFire;
    }

    public void setLevel(int level) {
        this.level = level;
    }
    public void setNumBallLives(int numBallLives) {
        this.numBallLives = numBallLives;
    }
    public void setGameState(GameStates gameState) {
        this.gameState = gameState;
    }
    public void setThemeInUse(Theme themeInUse) {
        this.themeInUse = themeInUse;
    }
    public void setPaddle(Paddle paddle) {
        this.paddle = paddle;
    }
    public void setMouseFire(boolean mouseFire) {
        this.mouseFire = mouseFire;
    }

    public void setScore(long score) {
        this.score = score;
    }

    public void setSuperBallActivated(boolean superBallActivated) {
        this.superBallActivated = superBallActivated;
    }

    public void setRocketBallActivated(boolean rocketBallActivated) {
        this.rocketBallActivated = rocketBallActivated;
    }

    public void setSuperBallActivationTime(double superBallActivationTime) {
        this.superBallActivationTime = superBallActivationTime;
    }

    public void setSuperBallCoolDownProgress(double superBallCoolDownProgress) {
        this.superBallCoolDownProgress = superBallCoolDownProgress;
    }

    public void setRocketBallActivationTime(double rocketBallActivationTime) {
        this.rocketBallActivationTime = rocketBallActivationTime;
    }

    public void setRocketBallCoolDownProgress(double rocketBallCoolDownProgress) {
        this.rocketBallCoolDownProgress = rocketBallCoolDownProgress;
    }

    public void setMaterialCollected(int materialCollected) {
        this.materialCollected = materialCollected;
    }


    ////////////////////////////////////
    /////// METHODS
    ////////////////////////////////////

    // game state control

    // init new game
    public void initGame() {
        clearAll();
        level = 1;
        score = 0;
        paddle = new Paddle();
        brickMap = new BrickMap(level);   // must be done after setting level bc brickMap pattern is level-dependent
        materialCollected = ROCKET_MATERIAL_NUM;
        superBallActivated = false;
        rocketBallActivated = false;
        superBallCoolDownProgress = 1;
        rocketBallCoolDownProgress = 1;
        setNumBallLives(4);
        initBallAndDecrementNumBallLives();
        gameState = GameStates.LEVEL_INTRO;
//        opsQueue.enqueue(falcon, GameOp.Action.ADD);
    }

    // init new level
    // does not reset numBallLives, but resets paddle lives
    public void initNewLevel() {
        clearAll();
        score = 0;
        paddle = new Paddle();
        brickMap = new BrickMap(level);
        materialCollected = ROCKET_MATERIAL_NUM;
        superBallActivated = false;
        rocketBallActivated = false;
        superBallCoolDownProgress = 1;
        rocketBallCoolDownProgress = 1;
        setNumBallLives(getNumBallLives() + 1);  // bc initBallAndDecrementNumBallLives will decrement it by 1
        initBallAndDecrementNumBallLives();
        gameState = GameStates.LEVEL_INTRO;
    }


    public void clearAll() {
        ballsOnScreen.clear();
        freebiesOnScreen.clear();
        bombsOnScreen.clear();
        rocketsOnScreen.clear();
    }

    public void updateTimers() {
        // cool-down timers
        superBallCoolDownProgress = Math.min(superBallCoolDownProgress + SUPER_BALL_COOL_DOWN_STEP, 1.0);
        rocketBallCoolDownProgress = Math.min(rocketBallCoolDownProgress + ROCKET_BALL_COOL_DOWN_STEP, 1.0);
        // activation status timer
        if (System.currentTimeMillis() - superBallActivationTime >= SUPER_BALL_DURATION) superBallActivated = false;
        if (System.currentTimeMillis() - rocketBallActivationTime >= ROCKET_BALL_DURATION) rocketBallActivated = false;
    }

    public void initBallAndDecrementNumBallLives(){
        if (getNumBallLives() >= 1) {
//            clearAll();
            setNumBallLives(getNumBallLives() - 1);
            ballsOnScreen.add(new Ball());
            gameState = CommandCenter.GameStates.PREPARING;
        }
    }


    public boolean isGameOver() {		// if no ball or no paddle live or bricks touch paddle, then game over
        // for debugging
        if (getBallsOnScreen().isEmpty()) {
            System.out.println("ball used up");
        }
        if (paddle.getLives() <= 0) {
            System.out.println("paddle died" + paddle.getLives());
        }
        if (brickMap.bricksHitPaddle()) {
            System.out.println("brick hits paddle");
        }
        return getBallsOnScreen().isEmpty()
                || paddle.getLives() <= 0
                || brickMap.bricksHitPaddle();
    }

    private void loadGraphics() {

    }

//    private BufferedImage loadGraphic(String imgName) {
//
//    }

}
