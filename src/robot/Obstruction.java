package robot;

import info.gridworld.actor.Rock;

import java.awt.*;

public class Obstruction extends Rock {
    public Obstruction() {
        this(Color.BLACK);
    }

    public Obstruction(Color rockColor) {
        super(rockColor);
    }
}
