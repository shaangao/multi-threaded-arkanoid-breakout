package view;

import controller.Game;
import model.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class GamePanel extends Panel {

    ////////////////////////////////////
    /////// FIELDS
    ////////////////////////////////////

    // The following "off" vars are used for the off-screen double-buffered image.
    private Image imgOff;
    private Graphics grpOff;

    private GameFrame gmf;
    private Font fnt = new Font("SansSerif", Font.BOLD, 12);
    private Font fntBig = new Font("SansSerif", Font.BOLD + Font.ITALIC, 36);
    private FontMetrics fmt;
    private int fontWidth;
    private int fontHeight;
    private String strDisplay = "";

    private final ImageIcon superBallIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/ballred.png")));
    private final ImageIcon crossIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/cross.png")));
    private final ImageIcon rocketIcon = new ImageIcon(Objects.requireNonNull(this.getClass().getResource("/images/rocketup.png")));


    ////////////////////////////////////
    /////// CONSTRUCTOR
    ////////////////////////////////////

    public GamePanel(Dimension dim){
        gmf = new GameFrame();
        gmf.getContentPane().add(this);
        gmf.pack();
        initView();

        gmf.setSize(dim);
        gmf.setTitle("Arkanoid");
        gmf.setResizable(false);
        gmf.setVisible(true);
        setFocusable(true);
    }


    ////////////////////////////////////
    /////// METHODS
    ////////////////////////////////////

    private void initView() {
        Graphics g = getGraphics();			// get the graphics context for the panel
        g.setFont(fnt);						// take care of some simple font stuff
        fmt = g.getFontMetrics();
        fontWidth = fmt.getMaxAdvance();
        fontHeight = fmt.getHeight();
//        g.setFont(fntBig);					// set font info
    }

    // update graphics every ANI_DELAY (see Game class)
    // used only when needing to erase everything from the last frame (not for pause)
    public void update(Graphics g) {

        //create an image off-screen
        imgOff = createImage(Game.DIM.width, Game.DIM.height);
        //get its graphics context
        grpOff = imgOff.getGraphics();

        //Fill the off-screen image background with black.
        grpOff.setColor(Color.BLACK);
        grpOff.fillRect(0, 0, Game.DIM.width, Game.DIM.height);

        // draw theme in use
        drawTheme(grpOff);

        /////// game state dependent graphics //////

        // welcome page
        if (CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.WELCOME) {
            displayTextOnScreen(grpOff);
        }

        // level intro page
        else if (CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.LEVEL_INTRO) {
            // draw intro text
            drawLevelIntro(grpOff);
            // draw remaining lives
            drawBallLivesLeft(grpOff);
        }

        // preparing
        else if (CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.PREPARING) {

            // draw level
            drawLevel(grpOff);
            // draw remaining lives
            drawBallLivesLeft(grpOff);
            // draw score
            drawScore(grpOff);

            // draw preparing
            drawPreparing(grpOff);

            // draw mouse fire trajectory
            if (CommandCenter.getInstance().isMouseFire()) {
                try {
//                System.out.println("x for drawing: " + getMousePosition().x + "; y for drawing: " + getMousePosition().y);
                    drawDashedLine(grpOff,
                            getMousePosition().x,
                            getMousePosition().y,
                            (int) CommandCenter.getInstance().getBallsOnScreen().get(0).getCenterX(),
                            (int) CommandCenter.getInstance().getBallsOnScreen().get(0).getCenterY());
                } catch(Exception e) {}
            }

            // draw cool-down status
            drawCoolDown(grpOff);

            // draw wall
            drawBoundaryWalls(grpOff);

            // paddle
            CommandCenter.getInstance().getPaddle().draw(grpOff);

            // ball
            for (Ball ball : CommandCenter.getInstance().getBallsOnScreen()) {
                ball.draw(grpOff);
            }

            // bricks
            CommandCenter.getInstance().getBrickMap().draw(grpOff);

            // rockets
            for (Rocket rocket : CommandCenter.getInstance().getRocketsOnScreen()) {
                rocket.draw(grpOff);
            }

            // freebies
            List<Freebie> freebiesOnScreen = CommandCenter.getInstance().getFreebiesOnScreen();
            for (Freebie freebie : freebiesOnScreen) {
                freebie.draw(grpOff);
            }

            // bombs
            List<Bomb> bombsOnScreen = CommandCenter.getInstance().getBombsOnScreen();
            for (Bomb bomb : bombsOnScreen) {
                bomb.draw(grpOff);
            }
        }

        // game over
        else if (CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.GAME_OVER) {
//        else if (CommandCenter.getInstance().isGameOver()) {
//            displayTextOnScreen(grpOff);
            CommandCenter.getInstance().setGameState(CommandCenter.GameStates.GAME_OVER);
            drawGameOver(grpOff);
            CommandCenter.getInstance().clearAll();
//            System.out.println("draw game over");
        }

        // victory/level-up interleave [tba]
        else if (CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.LEVEL_CLEARED) {
            // draw level up text
            drawLevelUp(grpOff);
            // draw remaining lives
            drawBallLivesLeft(grpOff);
//             clear
//            CommandCenter.getInstance().clearAll();
        }

        // paused but not other not-playing states: draw without move
        else if (CommandCenter.getInstance().getGameState() == CommandCenter.GameStates.PAUSED) {
            // draw level
            drawLevel(grpOff);
            // draw remaining lives
            drawBallLivesLeft(grpOff);
            // draw score
            drawScore(grpOff);

            // draw paused
            drawPaused(grpOff);

            // draw cool-down status
            drawCoolDown(grpOff);

            // draw wall
            drawBoundaryWalls(grpOff);

            // paddle
//            CommandCenter.getInstance().getPaddle().move();
            CommandCenter.getInstance().getPaddle().draw(grpOff);

            // ball
            for (Ball ball : CommandCenter.getInstance().getBallsOnScreen()) {
//                ball.move();
                ball.draw(grpOff);
            }

            // bricks
            CommandCenter.getInstance().getBrickMap().draw(grpOff);

            // rockets
            for (Rocket rocket : CommandCenter.getInstance().getRocketsOnScreen()) {
//                rocket.move();
                rocket.draw(grpOff);
            }

            // freebies
            List<Freebie> freebiesOnScreen = CommandCenter.getInstance().getFreebiesOnScreen();
            for (Freebie freebie : freebiesOnScreen) {
//                freebie.move();
                freebie.draw(grpOff);
            }

            // bombs
            List<Bomb> bombsOnScreen = CommandCenter.getInstance().getBombsOnScreen();
            for (Bomb bomb : bombsOnScreen) {
//                bomb.move();
                bomb.draw(grpOff);
            }
        }

        // playing and not paused: update buff-activation and cool-down status; update graphics by move() and draw()
        else {

            // draw level
            drawLevel(grpOff);
            // draw remaining lives
            drawBallLivesLeft(grpOff);
            // draw score
            drawScore(grpOff);

            // draw cool-down status
            drawCoolDown(grpOff);

            // draw wall
            drawBoundaryWalls(grpOff);

            // paddle
            CommandCenter.getInstance().getPaddle().move();
            CommandCenter.getInstance().getPaddle().draw(grpOff);

            // ball
            for (Ball ball : CommandCenter.getInstance().getBallsOnScreen()) {
                ball.move();
                ball.draw(grpOff);
            }

            // bricks
            CommandCenter.getInstance().getBrickMap().draw(grpOff);

            // rockets
            for (Rocket rocket : CommandCenter.getInstance().getRocketsOnScreen()) {
                rocket.move();
                rocket.draw(grpOff);
            }

            // freebies
            List<Freebie> freebiesOnScreen = CommandCenter.getInstance().getFreebiesOnScreen();
            for (Freebie freebie : freebiesOnScreen) {
                freebie.move();
                freebie.draw(grpOff);
            }

            // bombs
            List<Bomb> bombsOnScreen = CommandCenter.getInstance().getBombsOnScreen();
            for (Bomb bomb : bombsOnScreen) {
                bomb.move();
                bomb.draw(grpOff);
            }

        }

        //after drawing all the movables or text on the offscreen-image, copy it in one fell-swoop to graphics context
        // of the game panel. If you attempt to draw sprites directly on the gamePanel, e.g.
        // without the use of a double-buffered off-screen image, you will see flickering.
        g.drawImage(imgOff, 0, 0, this);
    }

    // add "Game Paused" to existing graphics
    public void drawPaused(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(fnt);
        String strDisplay = "Press P to Proceed";
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 3 * 2);
    }

    // add preparing text to existing graphics
    public void drawPreparing(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(fnt);

        String strDisplay = "Get ready! Here's your new ball!";
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5 * 3);

        strDisplay = "Press Enter to fire the ball at the default direction";
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5 * 3 + fontHeight);

        strDisplay = "or press the mouse on the ball and drag to set a firing direction";
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 5 * 3 + fontHeight * 2);
    }

    // draw dashed line for mouse fire
    private void drawDashedLine(Graphics g, int x1, int y1, int x2, int y2) {

        Graphics2D g2d = (Graphics2D) g.create();

        // Set the stroke of the copy, not the original
        Stroke dashed = new BasicStroke(2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL,
                0, new float[]{9}, 0);
        g2d.setStroke(dashed);

        // Draw to the copy
        g2d.drawLine(x1, y1, x2, y2);

        // Get rid of the copy
        g2d.dispose();
    }

//    public void drawBombed(Graphics g) {
//        g.setColor(Color.WHITE);
//        g.setFont(fnt);
//        String strDisplay = "YOU ARE BOMBED";
//        g.drawString(strDisplay,
//                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 3 * 2);
//    }

//    @SafeVarargs
//    // update the model variables and graphics of movables (move + draw)
//    private final void iterateMovables(final Graphics g, List<Movable>... arrayOfListMovables) {}

    private void drawLevel(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(fnt);
        strDisplay = "LEVEL " + CommandCenter.getInstance().getLevel();
        g.drawString(strDisplay, (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, fontHeight * 2);   // baseline of the leftmost character is at position (fontWidth, fontHeight)
//        if (CommandCenter.getInstance().getScore() != 0) {
//            g.drawString("SCORE :  " + CommandCenter.getInstance().getScore(), fontWidth, fontHeight);
//        } else {
//            g.drawString("NO SCORE", fontWidth, fontHeight);
//        }
    }

    public void drawTheme(Graphics g) {
        // draw colors
        Map<Integer, Color> colorMap = CommandCenter.getInstance().getThemeInUse().getBrickLiveToColor();
        int nColors = colorMap.size();
        int heightEachColor = fontHeight / nColors;
        for (int i = 0; i < nColors; i++) {
            g.setColor(colorMap.get(i + 1));
            g.fillRect(Game.DIM.width / 3, 2 + i * heightEachColor, Game.DIM.width / 3, heightEachColor);
        }
        // draw triangular arrows to indicate theme is changeable
        g.setColor(Color.WHITE);
        g.drawPolygon(new int[] {Game.DIM.width / 3 - 3, Game.DIM.width / 3 - 3, Game.DIM.width / 3 - 5}, new int[] {4, fontHeight - 2, (fontHeight + 2) / 2}, 3);
        g.drawPolygon(new int[] {Game.DIM.width / 3 * 2 + 2, Game.DIM.width / 3 * 2 + 2, Game.DIM.width / 3 * 2 + 4}, new int[] {4, fontHeight - 2, (fontHeight + 2) / 2}, 3);
        // draw theme name
        strDisplay = CommandCenter.getInstance().getThemeInUse().getName().toUpperCase();
        g.setColor(Color.BLACK);
        g.setFont(fnt);
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, fontHeight);
    }

    private void drawBallLivesLeft(Graphics g) {    // remaining lives of ball

        // text
        g.setColor(Color.WHITE);
        g.setFont(fnt);
        String strDisplay = "NEW BALLS: ";
        g.drawString(strDisplay, fontWidth, fontHeight);   // baseline of the leftmost character is at position (fontWidth, fontHeight)
//        g.drawString(strDisplay, Game.DIM.width - fontWidth - size * 3 - fmt.stringWidth(strDisplay), fontHeight);   // baseline of the leftmost character is at position (fontWidth, fontHeight)

        int ballStartX = fmt.stringWidth(strDisplay);
        // draw the contour of all possible backup lives (3)
        for (int i = 0; i < 3; i++) {
            g.drawOval(fontWidth + ballStartX + i * fontHeight, 4, fontHeight - 4, fontHeight - 4);
//            g.drawOval(Game.DIM.width - fontWidth - fontHeight * (3 - i), 4, fontHeight - 4, fontHeight - 4);
        }
        // if the backup live is not used, fill up the contour
        for (int i = 0; i < CommandCenter.getInstance().getNumBallLives(); i++) {
            g.fillOval(fontWidth + ballStartX + i * fontHeight, 4, fontHeight - 4, fontHeight - 4);
//            g.fillOval(Game.DIM.width - fontWidth - fontHeight * (3 - i), 4, fontHeight - 4, fontHeight - 4);
        }
    }

    private void drawScore(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(fnt);
        String strDisplay = "YOUR SCORE: " + CommandCenter.getInstance().getScore();
        g.drawString(strDisplay, fontWidth, fontHeight * 2);   // baseline of the leftmost character is at position (fontWidth, fontHeight)
    }

    private void drawCoolDown(Graphics g) {
        drawSuperBallCoolDown(g);
        drawRocketBallCoolDown(g);
        if (CommandCenter.getInstance().getLevel() > 1) drawRocketConstruction(g);
    }

    private void drawSuperBallCoolDown(Graphics g) {
        // progress bar
        g.setColor(Color.GREEN);
        g.fillRect(Game.DIM.width - 103, 3, (int) (100 * CommandCenter.getInstance().getSuperBallCoolDownProgress()), fontHeight - 6);
        // frame
        g.setColor(Color.WHITE);
        g.drawRect(Game.DIM.width - 103, 3, 100, fontHeight - 6);
        // icon
        g.drawImage(superBallIcon.getImage(), Game.DIM.width - 103 - fontHeight, 3, fontHeight - 4, fontHeight - 4, null);
    }

    private void drawRocketBallCoolDown(Graphics g) {
        // progress bar
        g.setColor(Color.GREEN);
        g.fillRect(Game.DIM.width - 103, 3 + fontHeight, (int) (100 * CommandCenter.getInstance().getRocketBallCoolDownProgress()), fontHeight - 6);
        // frame
        g.setColor(Color.WHITE);
        g.drawRect(Game.DIM.width - 103, 3 + fontHeight, 100, fontHeight - 6);
        // icon
        g.drawImage(crossIcon.getImage(), Game.DIM.width - 103 - fontHeight, 3 + fontHeight, fontHeight - 4, fontHeight - 4, null);
    }

    private void drawRocketConstruction(Graphics g) {
        Paddle paddle = CommandCenter.getInstance().getPaddle();
        double paddleCenterX = paddle.getCenterX();
        double paddleCenterY = paddle.getCenterY();
        double paddleHeightHalf = paddle.getHeight() / 2.0;
        // icon
        g.drawImage(rocketIcon.getImage(), (int) (paddleCenterX - 4), (int) (paddleCenterY + paddleHeightHalf + 3), 8, 16, null);
        // construction progress
        double diameter = Math.sqrt(Math.pow(10, 2) + Math.pow(20, 2));
        g.setColor(Color.GREEN);
        g.drawArc((int) (paddleCenterX - diameter / 2), (int) (paddleCenterY + paddleHeightHalf + 1), (int) diameter, (int) diameter,
                90, 360 * CommandCenter.getInstance().getMaterialCollected() / CommandCenter.getInstance().getROCKET_MATERIAL_NUM());
    }

    private void drawBoundaryWalls(Graphics g) {
        g.setColor(Color.GRAY);
        for (int i = 0; i < Game.DIM.width / (Brick.WIDTH + 2); i++) {   // ceiling
            g.fillRect(1 + (Brick.WIDTH + 2) * i, 41, Brick.WIDTH, Brick.HEIGHT);   // height of game status display: 40
        }
        for (int i = 0; i <= (Game.DIM.height - 100 - Brick.HEIGHT - 2) / (Brick.HEIGHT + 2) + 1; i++) {   // left and right walls
            g.fillRect(1, 41 + (Brick.HEIGHT + 2) * (i + 1), Brick.WIDTH, Brick.HEIGHT);
            g.fillRect(Game.DIM.width - Brick.WIDTH - 1, 41 + (Brick.HEIGHT + 2) * (i + 1), Brick.WIDTH, Brick.HEIGHT);
        }
    }

    // draw level up interleave
    public void drawLevelUp(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(fnt);

        strDisplay = "WELL DONE! YOU LEVELED UP!";
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);

        strDisplay = "YOU SCORED " + CommandCenter.getInstance().getScore() + " POINTS FOR LEVEL " + (CommandCenter.getInstance().getLevel() - 1);
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                        + fontHeight + 40);

        strDisplay = "press 'Enter' to Start level " + CommandCenter.getInstance().getLevel();
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                        + fontHeight + 120);
    }

    // draw level intro
    private void drawLevelIntro(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(fnt);

        if (CommandCenter.getInstance().getLevel() == 1) {

            strDisplay = "WELCOME TO LEVEL 1!";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);

            strDisplay = "In this level, your goal is to keep your ball alive and eliminate the bricks!";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight + 40);
            strDisplay = "use arrow keys to move the paddle left and right";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight + 80);
            strDisplay = "press 'F' to accelerate";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight + 120);

            strDisplay = "And of course, to make things more fun, we have some bonus for you!";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight * 2 + 160);
            strDisplay = "press 'D' to activate 'rocket ball' for 2s";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight * 2 + 200);
            strDisplay = "press 'S' to activate 'super ball' for 5s";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight * 2 + 240);
            strDisplay = "check out their cool-down progress in the top right corner!";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight * 2 + 280);

            strDisplay = "during the game, press 'P' at any time to pause/resume";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight * 3 + 320);

            strDisplay = "press 'Enter' to continue";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight * 3 + 360);
        }

        else if (CommandCenter.getInstance().getLevel() == 2) {

            strDisplay = "GOOD JOB! LET'S TAKE A LOOK AT WHAT'S NEW IN LEVEL 2";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);

            strDisplay = "In this level, you will actually need to defense your paddle";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight + 40);
            strDisplay = "from bombs released by dying bricks!";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight + 80);
            strDisplay = "left pinkie on 'A' for Shield";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight + 120);
            strDisplay = "and the bricks will have more than one lives! (denoted by color shades)";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight + 160);

            strDisplay = "And of course -- bonus as usual!";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight * 2 + 200);
            strDisplay = "Your paddle, if shielded, will collect 5 bombs to make a rocket";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight * 2 + 240);
            strDisplay = "press space bar to fire a rocket";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight * 2 + 280);
            strDisplay = "look at the green circle for rocket construction progress";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight * 2 + 320);

            strDisplay = "press 'Enter' to continue";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight * 3 + 360);
        }

        else {
            strDisplay = "LEVEL " + CommandCenter.getInstance().getLevel() + "!!!";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);

            strDisplay = "In this level, the brick block will keep moving down slowly";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight + 40);
            strDisplay = "get rid of the bricks before they reach the paddle!";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight + 80);
            strDisplay = "The bricks move down faster as you progress";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight + 120);
            strDisplay = "You will level up when there is no more bricks left on screen";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight + 160);
            strDisplay = "All bonuses from the previous levels are still valid!";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight + 200);

            strDisplay = "ready for the challenge? press 'Enter' to continue";
            g.drawString(strDisplay,
                    (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                            + fontHeight * 3 + 360);
        }
    }

    // draw game over text
    private void drawGameOver(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(fnt);

        strDisplay = "GAME OVER";
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);

        strDisplay = "YOU SCORED " + CommandCenter.getInstance().getScore() + " POINTS FOR LEVEL " + CommandCenter.getInstance().getLevel();
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4 + fontHeight + 40);

        strDisplay = "press 'Enter' to Restart";
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                        + fontHeight + 120);
    }

    // draw text to the middle of the screen before a game
    private void displayTextOnScreen(Graphics g) {

        g.setColor(Color.WHITE);
        g.setFont(fnt);

        strDisplay = "ARKANOID DEFENSE";
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4);

        strDisplay = "press 'T' at any time during the game to change themes";
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                        + fontHeight + 80);

        strDisplay = "press 'Enter' to continue";
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                        + fontHeight + 120);

        strDisplay = "press 'Q' to quit";
        g.drawString(strDisplay,
                (Game.DIM.width - fmt.stringWidth(strDisplay)) / 2, Game.DIM.height / 4
                        + fontHeight + 160);
    }
}
