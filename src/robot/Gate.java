package robot;

import info.gridworld.actor.Actor;

import java.awt.*;

public class Gate extends Actor {
    public Gate() {
        super.setColor(Color.GREEN);
    }

    @Override
    public void setColor(Color color) {}

    public boolean isOpen = true;

    public void open() {
        super.setColor(Color.GREEN);
        isOpen = true;
    }

    public void close() {
        super.setColor(Color.RED);
        isOpen = false;
    }
}
