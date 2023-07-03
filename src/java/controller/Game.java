package controller;

import model.*;
import view.GamePanel;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.*;
import java.util.List;



public class Game implements Runnable, KeyListener, MouseListener {


    ////////////////////////////////////
    /////// FIELDS
    ////////////////////////////////////

    public static final Dimension DIM = new Dimension(600, 700); //the dimension of the game *frame* (not panel)
    private GamePanel gmpPanel;
    public int gmpPanelHeight;

    public static final List<Theme> availableThemes = new LinkedList<Theme>(Arrays.asList(
            new Theme("WINTER", new HashMap<Integer, Color>() {{
                put(1, new Color(212, 247, 255));
                put(2, new Color(148, 226, 243));
                put(3, new Color(84, 209, 236));
            }}),
            new Theme("SPRING", new HashMap<Integer, Color>() {{
                put(1, new Color(216, 246, 212));
                put(2, new Color(168, 235, 156));
                put(3, new Color(115, 224, 96));
            }}),
            new Theme("SUMMER", new HashMap<Integer, Color>() {{
                put(1, new Color(255, 234, 228));
                put(2, new Color(255, 204, 188));
                put(3, new Color(255, 171, 145));
            }}),
            new Theme("FALL", new HashMap<Integer, Color>() {{
                put(1, new Color(245, 235, 212));
                put(2, new Color(228, 214, 183));
                put(3, new Color(208, 189, 147));
            }})
    ));

    public static Random R = new Random();

    public static final int ANI_DELAY = 20; // milliseconds between screen updates (animation)
    public static final int FRAMES_PER_SECOND = 1000 / ANI_DELAY;
    private Thread animationThread;
    //    private long frame;
    private double levelRunTime;


    private boolean muted = true;
    //    private Clip clpThrust;
    private Clip clpMusicBackground;

    private final int PAUSE = 80, // p key. pause/resume
            QUIT = 81, // q key
    //                        START = 10, // enter key (some may use 13 for the enter key)
    START = KeyEvent.VK_ENTER,   // enter key. 10 or 13.
            LEFT = 37, // left arrow. move left
            RIGHT = 39, // right arrow. move right
            ACCELERATE = 70, // f key
            SHIELD = 65, // a key
            LAUNCH_ROCKET = 32, // space key
            SUPER_BALL = 83, // s key
            ROCKET_BALL = 68,  // d key
            MUTE = 77, // m key. mute/unmute
            THEME = 84;  // t key. change theme.



    ////////////////////////////////////
    /////// CONSTRUCTOR
    ////////////////////////////////////

    public Game() {

        gmpPanel = new GamePanel(DIM);
        gmpPanel.addKeyListener(this); //Game object implements KeyListener
        gmpPanel.addMouseListener(this);
        gmpPanelHeight = gmpPanel.getHeight();

        // sound
//        clpThrust = Sound.clipForLoopFactory("whitenoise.wav");
        clpMusicBackground = Sound.clipForLoopFactory("ghostbusters_theme.wav");

        //fire up the animation thread
        animationThread = new Thread(this); // pass the animation thread a runnable object, the Game object
        animationThread.start();

    }


    ////////////////////////////////////
    /////// METHODS
    ////////////////////////////////////

    public static void main(String args[]) {
        //typical Swing application start; we pass EventQueue a Runnable object.
        EventQueue.invokeLater(Game::new);
    }


    // Game implements runnable, and must have run method
    @Override
    public void run() {

        // lower animation thread's priority, thereby yielding to the "main" aka 'Event Dispatch'
        // thread which listens to keystrokes
        animationThread.setPriority(Thread.MIN_PRIORITY);

        // and get the current time
        long lStartTime = System.currentTimeMillis();

        // this thread animates the scene
        while (Thread.currentThread() == animationThread) {

            if (CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.PLAYING) {

                levelRunTime += ANI_DELAY;

                checkCollisions();   // and process add/remove ops after checking all
                CommandCenter.getInstance().updateTimers();

                if (CommandCenter.getInstance().getLevel() >= 3) moveBricksDownChecker();

                gmpPanel.update(gmpPanel.getGraphics());
                checkNewLevel();

                // init new ball if no ball is live on screen
                if (CommandCenter.getInstance().getBallsOnScreen().isEmpty()) {
                    CommandCenter.getInstance().initBallAndDecrementNumBallLives();
                }

                if (CommandCenter.getInstance().isGameOver()) {
                    CommandCenter.getInstance().setGameState(CommandCenter.GameStates.GAME_OVER);
                }

            } else {
                gmpPanel.update(gmpPanel.getGraphics());
            }

            // surround sleep() in a try/catch block. controls delay time between the frames of animation.
            try {
                // The total amount of time: at least ANI_DELAY long.
                // If processing (update) between frames takes longer than ANI_DELAY,
                // then lStartTime - System.currentTimeMillis() < 0, then zero will be the sleep time
                lStartTime += ANI_DELAY;
                Thread.sleep(Math.max(0, lStartTime - System.currentTimeMillis()));
            } catch (InterruptedException e) {
                // do nothing (bury the exception), and just continue, e.g. skip this frame -- no big deal
            }

        } // end while
    }


    // move bricks down checker
    private void moveBricksDownChecker(){
        // moves down faster as you progress through levels and plays longer in each level
        if (levelRunTime % (1000 * Math.exp(-levelRunTime * CommandCenter.getInstance().getLevel() / 2) + 1000) == 0) {
            CommandCenter.getInstance().getBrickMap().moveDownAndAddOnTop();
        }
    }

    private void checkCollisions() {

        Paddle paddle = CommandCenter.getInstance().getPaddle();
        Rectangle paddleRect = new Rectangle((int) (paddle.getCenterX() - paddle.getWidth() / 2), (int) (paddle.getCenterY() - paddle.getHeight() / 2),
                paddle.getWidth(), paddle.getHeight());

        // rockets vs bricks
        for (Rocket rocket : CommandCenter.getInstance().getRocketsOnScreen()) {

            double rocketCenterX = rocket.getCenterX();
            double rocketCenterY = rocket.getCenterY();
            double rocketWidthHalf = rocket.getWidth() / 2.0;
            double rocketHeightHalf = rocket.getHeight() / 2.0;

            // first, remove out-of-scope rockets
            if (rocketCenterX - rocketWidthHalf <= Brick.WIDTH + 2 || rocketCenterX + rocketWidthHalf >= Game.DIM.width - (Brick.WIDTH + 2)
                    || rocketCenterY - rocketHeightHalf <= 40 + (Brick.HEIGHT + 2) || (rocket.getDeltaY() > 0 && rocketCenterY + rocketHeightHalf >= paddle.getCenterY() - paddle.getHeight() / 2.0 - 2)) {
                CommandCenter.getInstance().getOpsQueue().enqueue(rocket, GameOp.Action.REMOVE);
                continue;
            }

            // check collision
            for (Brick brick : CommandCenter.getInstance().getBrickMap().getBrickList()) {
                Rectangle brickRect = new Rectangle((int) (brick.getCenterX() - brick.getWidth() / 2),
                        (int) (brick.getCenterY() - brick.getHeight() / 2),
                        brick.getWidth(),
                        brick.getHeight());
                Rectangle rocketRect = new Rectangle((int) (rocket.getCenterX() - rocket.getWidth() / 2),
                        (int) (rocket.getCenterY() - rocket.getHeight() / 2),
                        rocket.getWidth(),
                        rocket.getHeight());
                if (rocketRect.intersects(brickRect)) {
                    brick.setLives(0);
                    Sound.playSound("shot2.wav");
                    CommandCenter.getInstance().setScore(CommandCenter.getInstance().getScore() + 20);
                }
            }
        }


        // balls vs walls/paddle/bricks
        for (Ball ball: CommandCenter.getInstance().getBallsOnScreen()) {

            // first, check if the ball fell dead
            if (ball.fellDead(gmpPanel.getHeight())) {
                CommandCenter.getInstance().getOpsQueue().enqueue(ball, GameOp.Action.REMOVE);
                continue;
            }

            // then check bounce
            boolean bounced = false;  // for each ball, only 1 of the 3 types of bounce (wall, paddle, brick) is possible at each update.
            // this variable tracks if any type of bounce has happened to stop unnecessary computation.

            // possibility 1: update v if the ball bounces off wall
            if (ball.getCenterX() <= (Brick.WIDTH + 2) + ball.getWidth() / 2.0) {  // left wall
                ball.setDeltaX(-ball.getDeltaX());
                ball.setCenterX((Brick.WIDTH + 2) + ball.getWidth() / 2.0);
                bounced = true;
                Sound.playSound("ballbounce.wav");
            }
            else if (ball.getCenterY() <= 40 + (Brick.HEIGHT + 2) + ball.getHeight() / 2.0) {  // ceiling
                ball.setDeltaY(-ball.getDeltaY());
                ball.setCenterY(40 + (Brick.HEIGHT + 2) + ball.getHeight() / 2.0);
                bounced = true;
                Sound.playSound("ballbounce.wav");
            }
            else if (ball.getCenterX() >= Game.DIM.width - (Brick.WIDTH + 2) - ball.getWidth() / 2.0) {  // right wall
                ball.setDeltaX(-ball.getDeltaX());
                ball.setCenterX(Game.DIM.width - (Brick.WIDTH + 2) - ball.getWidth() / 2.0);
                bounced = true;
                Sound.playSound("ballbounce.wav");
            }

            // collision box for the ball (needed for possibility 2 and 3)
            Rectangle ballRect = new Rectangle((int) (ball.getCenterX() - ball.getWidth() / 2), (int) (ball.getCenterY() - ball.getHeight() / 2),
                    ball.getWidth(), ball.getHeight());

            // possibility 2: update v if the ball bounces off paddle
            if (!bounced) {

                if (ballRect.intersects(new Rectangle((int) (paddle.getCenterX() - paddle.getWidth() / 2),
                        (int) (paddle.getCenterY() - paddle.getHeight() / 2),
                        paddle.getWidth(),
                        paddle.getHeight()))) {

                    bounced = true;
                    Sound.playSound("ballbounce.wav");

                    if (ball.getCenterX() + Ball.RADIUS - 1 <= paddle.getCenterX() - paddle.getWidth() / 2.0) {   // left bounce
                        ball.setDeltaX(-ball.getDeltaX());
                        ball.setCenterX((int) paddle.getCenterX() - paddle.getWidth() / 2.0 - Ball.RADIUS);
                    } else if (ball.getCenterX() - Ball.RADIUS + 1 >= paddle.getCenterX() + paddle.getWidth() / 2.0) {   // right bounce
                        ball.setDeltaX(-ball.getDeltaX());
                        ball.setCenterX((int) paddle.getCenterX() + paddle.getWidth() / 2.0 + Ball.RADIUS);
                    } else {    // top and bottom bounce
                        ball.setDeltaY(-ball.getDeltaY());
                        if (ball.getCenterY() <= paddle.getCenterY()) ball.setCenterY(paddle.getCenterY() - paddle.getHeight() / 2.0 - Ball.RADIUS);   // top bounce
                        else ball.setCenterY(paddle.getCenterY() + paddle.getHeight() / 2.0 + Ball.RADIUS);   // bottom bounce
                    }  // end ball velocity change
                }
            }

            // possibility 3: update v if the ball bounces off bricks
            checkBrickBounce:
            if (!bounced) {
                for (Brick brick : CommandCenter.getInstance().getBrickMap().getBrickList()) {
                    Rectangle brickRect = new Rectangle((int) (brick.getCenterX() - brick.getWidth() / 2),
                            (int) (brick.getCenterY() - brick.getHeight() / 2),
                            brick.getWidth(),
                            brick.getHeight());
                    if (ballRect.intersects(brickRect)) {
                        // update hit brick
                        brick.setLives(brick.getLives() - 1);
                        CommandCenter.getInstance().setScore(CommandCenter.getInstance().getScore() + 5);
                        Sound.playSound("ballbounce.wav");
                        // if super ball
                        if (CommandCenter.getInstance().isSuperBallActivated()) {
                            brick.findNeighborsAndDecrementLives(CommandCenter.getInstance().getBrickMap().getBrickList());
                            Sound.playSound("buff.wav");
                        }
                        // if rocket ball
                        if (CommandCenter.getInstance().isRocketBallActivated()) {
                            CommandCenter.getInstance().getOpsQueue().enqueue(new Rocket("up", ball.getCenterX(), ball.getCenterY()), GameOp.Action.ADD);
                            CommandCenter.getInstance().getOpsQueue().enqueue(new Rocket("down", ball.getCenterX(), ball.getCenterY()), GameOp.Action.ADD);
                            CommandCenter.getInstance().getOpsQueue().enqueue(new Rocket("left", ball.getCenterX(), ball.getCenterY()), GameOp.Action.ADD);
                            CommandCenter.getInstance().getOpsQueue().enqueue(new Rocket("right", ball.getCenterX(), ball.getCenterY()), GameOp.Action.ADD);
                            Sound.playSound("rocketlaunch.wav");
                        }

                        // update ball velocity
                        if (ball.getCenterX() + Ball.RADIUS - 1 <= brick.getCenterX() - brick.getWidth() / 2.0
                                || ball.getCenterX() - Ball.RADIUS + 1 >= brick.getCenterX() + brick.getWidth() / 2.0) {      // side bounce
                            ball.setDeltaX(-ball.getDeltaX() * 1.01);
                            ball.setDeltaY(ball.getDeltaY() * 1.01);
                        } else {    // top and bottom bounce
                            ball.setDeltaY(-ball.getDeltaY() * 1.01);
                            ball.setDeltaX(ball.getDeltaX() * 1.01);
                        }
                        break checkBrickBounce;   // if bounces off one brick, don't check the remaining bricks with this ball for efficiency
                    }
                }
            }  // end A
        }

        // freebies vs paddle
        List<Freebie> freebiesOnScreen = CommandCenter.getInstance().getFreebiesOnScreen();
        for (Freebie freebie : freebiesOnScreen) {
            Rectangle freebieRect = new Rectangle((int) (freebie.getCenterX() - freebie.getWidth() / 2),
                    (int) (freebie.getCenterY() - freebie.getHeight() / 2),
                    freebie.getWidth(), freebie.getHeight());
            if (freebieRect.intersects(paddleRect)) {
                CommandCenter.getInstance().getOpsQueue().enqueue(freebie, GameOp.Action.REMOVE);
                Sound.playSound("bonus2.wav");
                List<Ball> balls = CommandCenter.getInstance().getBallsOnScreen();
                if (balls.size() <= 50) {   // limit the number of balls
                    for (Ball ball : CommandCenter.getInstance().getBallsOnScreen()) {
                        if (ball.getCenterY() + ball.getHeight() / 2.0 <= paddle.getCenterY() - paddle.getHeight() / 2.0) {
                            ball.multiplyOneBall();
                        }
                    }
                }
            }
        }

        // bombs vs paddle
        List<Bomb> bombsOnScreen = CommandCenter.getInstance().getBombsOnScreen();
        for (Bomb bomb : bombsOnScreen) {
            Rectangle bombRect = new Rectangle((int) (bomb.getCenterX() - bomb.getWidth() / 2),
                    (int) (bomb.getCenterY() - bomb.getHeight() / 2),
                    bomb.getWidth(), bomb.getHeight());
            if (bombRect.intersects(paddleRect)) {
                CommandCenter.getInstance().getOpsQueue().enqueue(bomb, GameOp.Action.REMOVE);
                if (!paddle.isShield()) {
                    paddle.setLives(paddle.getLives() - 1);
                    Sound.playSound("explosion.wav");
//                gmpPanel.drawBombed(gmpPanel.getGraphics());
                } else {
                    CommandCenter.getInstance().setMaterialCollected(Math.min(CommandCenter.getInstance().getMaterialCollected() + 1,
                            CommandCenter.getInstance().getROCKET_MATERIAL_NUM()));
                    Sound.playSound("bonus.wav");
                }
            }
        }

        // process ops
        processGameOpsQueue();

    }

    // get collision box
    private void getCollisionBox(Sprite sprite) {   // refactoring

    }

    private void processGameOpsQueue() {     // see GameOpsQueue class

        // deferred mutation: these operations are done AFTER completing collision detection to avoid
        // mutating the sprites linkedlists while iterating them (ConcurrentModificationException)
        while(!CommandCenter.getInstance().getOpsQueue().isEmpty()){

            GameOp gameOp =  CommandCenter.getInstance().getOpsQueue().dequeue();
            Sprite sprite = gameOp.getSprite();
            GameOp.Action action = gameOp.getAction();

            // brick
            if (sprite.getClass() == Brick.class) {
                if (action == GameOp.Action.REMOVE) {
                    CommandCenter.getInstance().getBrickMap().getBrickList().remove(sprite);
                } else {
                    CommandCenter.getInstance().getBrickMap().getBrickList().add((Brick) sprite);
                }
            }

            // freebie
            if (sprite.getClass() == Freebie.class) {
                if (action == GameOp.Action.REMOVE) {
                    CommandCenter.getInstance().getFreebiesOnScreen().remove(sprite);
                } else {
                    CommandCenter.getInstance().getFreebiesOnScreen().add((Freebie) sprite);
                }
            }

            // ball
            if (sprite.getClass() == Ball.class) {
                if (action == GameOp.Action.REMOVE) {
                    CommandCenter.getInstance().getBallsOnScreen().remove(sprite);
                } else {
                    CommandCenter.getInstance().getBallsOnScreen().add((Ball) sprite);
                }
            }

            // rocket
            if (sprite.getClass() == Rocket.class) {
                if (action == GameOp.Action.REMOVE) {
                    CommandCenter.getInstance().getRocketsOnScreen().remove(sprite);
                } else {
                    CommandCenter.getInstance().getRocketsOnScreen().add((Rocket) sprite);
                }
            }

            // bomb
            if (sprite.getClass() == Bomb.class) {
                if (action == GameOp.Action.REMOVE) {
                    CommandCenter.getInstance().getBombsOnScreen().remove(sprite);
                } else {
                    CommandCenter.getInstance().getBombsOnScreen().add((Bomb) sprite);
                }
            }


        }
    }



    // set new level (see CommandCenter for init)
    // does not reset numBallLives
    private void checkNewLevel() {
        if (isLevelClear()) {
            CommandCenter.getInstance().setGameState(CommandCenter.GameStates.LEVEL_CLEARED);
            CommandCenter.getInstance().setLevel(CommandCenter.getInstance().getLevel() + 1);
//            CommandCenter.getInstance().initNewLevel();
            levelRunTime = 0;
        }
    }
    private boolean isLevelClear() {
        //if there are no more bricks on the screen
        return CommandCenter.getInstance().getBrickMap().getBrickList().isEmpty();
    }


    // Varargs for stopping looping-music-clips
    private static void stopLoopingSounds(Clip... clpClips) {
        for (Clip clp : clpClips) {
            clp.stop();
        }
    }



    ////////////////////////////////////
    /////// KEY LISTENER METHODS
    ////////////////////////////////////

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) {

        Paddle paddle = CommandCenter.getInstance().getPaddle();
        int nKey = e.getKeyCode();

        // apply to all states
        if (nKey == QUIT) System.exit(0);

        // state specific
        if (nKey == START && CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.WELCOME) {   // when isWelcome, isGameOver is always true
            CommandCenter.getInstance().initGame();
            CommandCenter.getInstance().setGameState(CommandCenter.GameStates.LEVEL_INTRO);
        }

        else if (nKey == START && CommandCenter.getInstance().isGameOver()) {   // isGameOver but not isWelcome
            CommandCenter.getInstance().setGameState(CommandCenter.GameStates.WELCOME);   // restart from welcome page
        }

        else if (nKey == START && CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.LEVEL_CLEARED) {
            CommandCenter.getInstance().initNewLevel();
            CommandCenter.getInstance().setGameState(CommandCenter.GameStates.LEVEL_INTRO);
        }

        else if (nKey == START && CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.LEVEL_INTRO) {
            CommandCenter.getInstance().setGameState(CommandCenter.GameStates.PREPARING);
        }

        else if (nKey == START && CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.PREPARING) {
            CommandCenter.getInstance().setGameState(CommandCenter.GameStates.PLAYING);
        }

        else if (CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.PLAYING) {
            switch (nKey) {
                case PAUSE:
                    CommandCenter.getInstance().setGameState(CommandCenter.GameStates.PAUSED);
//                            stopLoopingSounds(clpMusicBackground, clpThrust);
                case LEFT:
                    paddle.stopMoving();
                    paddle.moveLeft();
                    break;
                case RIGHT:
                    paddle.stopMoving();
                    paddle.moveRight();
                    break;

                case SHIELD:
//                    paddle.setShield(true);
                    if (CommandCenter.getInstance().getLevel() > 1) {
                        paddle.setShield(true);
                        Sound.playSound("shieldup.wav");
                    }
                    break;
                case ACCELERATE:
                    paddle.accelerateOn();
                    break;
                case LAUNCH_ROCKET:
                    if (CommandCenter.getInstance().getLevel() > 1
                            && CommandCenter.getInstance().getMaterialCollected() >= CommandCenter.getInstance().getROCKET_MATERIAL_NUM()) {
                        CommandCenter.getInstance().getOpsQueue().enqueue(
                                new Rocket("up", paddle.getCenterX(), paddle.getCenterY()), GameOp.Action.ADD);
                        CommandCenter.getInstance().setMaterialCollected(0);
                        Sound.playSound("rocketlaunch.wav");
                    }
                    break;

                case SUPER_BALL:
                    if (CommandCenter.getInstance().getSuperBallCoolDownProgress() >= 1) {
                        CommandCenter.getInstance().setSuperBallActivationTime(System.currentTimeMillis());
                        CommandCenter.getInstance().setSuperBallCoolDownProgress(0);
                        CommandCenter.getInstance().setSuperBallActivated(true);
                        Sound.playSound("superballon.wav");
                    }
                    break;
                case ROCKET_BALL:
                    if (CommandCenter.getInstance().getRocketBallCoolDownProgress() >= 1) {
                        CommandCenter.getInstance().setRocketBallActivationTime(System.currentTimeMillis());
                        CommandCenter.getInstance().setRocketBallCoolDownProgress(0);
                        CommandCenter.getInstance().setRocketBallActivated(true);
                        Sound.playSound("rocketballon.wav");
                    }
                    break;

                default:
                    break;
            }
        }
        else if (CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.PAUSED) {
            CommandCenter.getInstance().setGameState(CommandCenter.GameStates.PLAYING);
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

        Paddle paddle = CommandCenter.getInstance().getPaddle();
        int nKey = e.getKeyCode();
        System.out.println(nKey); // show key code in console

        // apply to all states
        if (nKey == THEME) {
            int indexThemeInUse = availableThemes.indexOf(CommandCenter.getInstance().getThemeInUse());
            // get the next available theme or back to the beginning
            CommandCenter.getInstance().setThemeInUse(availableThemes.get((indexThemeInUse + 1) % availableThemes.size()));
        }
        if (nKey == MUTE) {
            if (!muted){
                stopLoopingSounds(clpMusicBackground);
            }
            else {
                clpMusicBackground.loop(Clip.LOOP_CONTINUOUSLY);
            }
            muted = !muted;
        }

        // state dependent
        if (CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.PLAYING) {

            switch (nKey) {

                case LEFT:
                    paddle.stopMoving();
                    break;
                case RIGHT:
                    paddle.stopMoving();
                    break;

                case SHIELD:
                    paddle.setShield(false);
                    break;
                case ACCELERATE:
                    paddle.accelerateOff();
                    break;

                default:
                    break;
            }
        }   // end if
    }


    @Override
    public void mousePressed(MouseEvent e) {
        if (CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.PREPARING) {

            double x = e.getX();
            double y = e.getY();

            Ball ball = CommandCenter.getInstance().getBallsOnScreen().get(0);
            double ballCenterX = ball.getCenterX();
            double ballCenterY = ball.getCenterY();

            if (Math.pow(x - ballCenterX, 2) + Math.pow(y - ballCenterY, 2) <= Math.pow(Ball.RADIUS, 2)) {
                CommandCenter.getInstance().setMouseFire(true);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (CommandCenter.getInstance().isMouseFire()) {

            CommandCenter.getInstance().setMouseFire(false);

            double x = e.getX();
            double y = e.getY();
//            System.out.println("x for firing: " + x + "; y for firing: " + y);

            Ball ball = CommandCenter.getInstance().getBallsOnScreen().get(0);
            double ballCenterX = ball.getCenterX();
            double ballCenterY = ball.getCenterY();

//            double angleRadians = Math.atan((y - ballCenterY) / (x - ballCenterX));
//            ball.setDeltaX(Math.cos(angleRadians) * ball.getSpeed());
//            ball.setDeltaY(Math.sin(angleRadians) * ball.getSpeed());

            double scaling = ball.getSpeed() / Math.sqrt(Math.pow(x - ballCenterX, 2) + Math.pow(y - ballCenterY, 2));

            ball.setDeltaX((x - ballCenterX) * scaling);
            ball.setDeltaY((y - ballCenterY) * scaling);
//            System.out.println("deltax for firing: " + (x - ballCenterX) * scaling + "; deltay for firing: " + (y - ballCenterY) * scaling);


            CommandCenter.getInstance().setGameState(CommandCenter.GameStates.PLAYING);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}


}