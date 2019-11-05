package planner;

import info.gridworld.grid.Location;

public interface MotionPlanner {
    public Location next(Location current);
}
