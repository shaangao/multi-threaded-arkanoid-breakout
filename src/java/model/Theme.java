package model;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Theme {

    private String name;
    private Map<Integer, Color> brickLiveToColor;

    public Theme(String name, Map<Integer, Color> brickLiveToColor) {
        this.name = name;
        this.brickLiveToColor = brickLiveToColor;
    }

    public String getName() {
        return name;
    }

    public Map<Integer, Color> getBrickLiveToColor() {
        return brickLiveToColor;
    }
}
