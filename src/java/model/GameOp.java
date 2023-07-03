package model;

//import lombok.Data;

//@Data
public class GameOp {    // a sprite and the action that needs to be performed on the sprite

    public enum Action {
        ADD, REMOVE
    }

    private Sprite sprite;
    private Action action;


    public GameOp(Sprite sprite, Action action) {
        this.sprite = sprite;
        this.action = action;
    }


    public Sprite getSprite() {
        return sprite;
    }

    public Action getAction() {
        return action;
    }


    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
