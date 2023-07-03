package model;

import java.awt.*;
import java.util.LinkedList;
import java.util.List;

import static controller.Game.R;

public class BrickMap {

    ////////////////////////////////////
    /////// FIELDS
    ////////////////////////////////////

    private int row = 10;
    private int col = 20;

    private int[][] brickMapProto = new int[row][col];    // 2d array of int between 0-3 for determining the brick at each slot
    private List<Brick> brickList = new LinkedList<>();    // list of bricks


    ////////////////////////////////////
    /////// CONSTRUCTOR
    ////////////////////////////////////


    public BrickMap(int level) {

        if (level == 1) {
            brickMapProto = new int[][]{{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                                        {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                                        {1, 0, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1},
                                        {1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1},
                                        {1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1},
                                        {1, 0, 0, 0, 1, 0, 1, 0, 1, 0, 1, 0, 0, 1, 1, 1, 1, 0, 0, 1},
                                        {1, 0, 1, 1, 1, 0, 1, 0, 1, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 1},
                                        {1, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0, 1, 1, 1, 1, 1, 0, 0, 0, 1},
                                        {1, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 1},
                                        {1, 0, 1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}};
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    int brickLives = brickMapProto[i][j];
                    if (brickLives != 0) brickList.add(new Brick(brickLives,
                                                                (int) (100 + (Brick.WIDTH + 2) * (0.5 + j)),
                                                                (int) (100 + (Brick.HEIGHT + 2) * (0.5 + i))));
                }
            }
        }

        else if (level == 2) {
            brickMapProto = new int[][]{{0, 0, 0, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                        {0, 1, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                        {0, 1, 2, 3, 3, 3, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                                        {1, 1, 2, 3, 3, 3, 2, 2, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                                        {2, 0, 2, 3, 3, 3, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2},
                                        {1, 1, 2, 3, 3, 3, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3},
                                        {0, 1, 2, 3, 3, 3, 2, 1, 0, 0, 0, 0, 0, 3, 2, 0, 0, 3, 2, 0},
                                        {0, 1, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 3, 2, 0, 0, 3, 2, 0},
                                        {0, 0, 0, 1, 2, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 3, 2, 0},
                                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}};
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    int brickLives = brickMapProto[i][j];
                    if (brickLives != 0) brickList.add(new Brick(brickLives,
                            (int) (100 + (Brick.WIDTH + 2) * (0.5 + j)),
                            (int) (100 + (Brick.HEIGHT + 2) * (0.5 + i))));
                }
            }
        }

        else {
//            new BrickMap();
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < col; j++) {
                    int brickLives = R.nextInt(4);
//                int brickLives = 1;   // for test
                    brickMapProto[i][j] = brickLives;
                    if (brickLives != 0) brickList.add(new Brick(brickLives,
                            (int) (100 + (Brick.WIDTH + 2) * (0.5 + j)),
                            (int) (100 + (Brick.HEIGHT + 2) * (0.5 + i))));
                }
            }
        }
    }


    ////////////////////////////////////
    /////// GETTERS AND SETTERS
    ////////////////////////////////////

    public List<Brick> getBrickList() {
        return brickList;
    }


    ////////////////////////////////////
    /////// METHODS
    ////////////////////////////////////

    public void moveDownAndAddOnTop() {
        // move down
        for (Brick brick : brickList) {
            brick.setCenterY(brick.getCenterY() + Brick.HEIGHT + 2);
        }
        // add new row of random bricks on top
        for (int j = 0; j < col; j++) {
            int brickLives = R.nextInt(4);
            if (brickLives != 0) CommandCenter.getInstance().getOpsQueue().enqueue(
                    new Brick(brickLives,
                            (int) (100 + (Brick.WIDTH + 2) * (0.5 + j)),
                            (int) (100 + (Brick.HEIGHT + 2) * 0.5)),
                    GameOp.Action.ADD);
        }
    }

    public boolean bricksHitPaddle() {
        Paddle paddle = CommandCenter.getInstance().getPaddle();
        double paddleTopY = paddle.getCenterY() - paddle.getHeight() / 2.0;
        for (Brick brick : brickList) {
            if (brick.getCenterY() + brick.getHeight() / 2.0 >= paddleTopY) return true;
        }
        return false;
    }

    public void draw(Graphics g) {
        for (Brick brick : brickList) {
            brick.draw(g);
        }
    }
}
