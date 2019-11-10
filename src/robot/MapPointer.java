package robot;

import info.gridworld.actor.Actor;

import java.awt.*;

public class MapPointer extends Actor {
    @Override
    public void act() {
        if (this.getColor().equals(Color.RED))
            this.setColor(Color.BLACK);
    }
}
