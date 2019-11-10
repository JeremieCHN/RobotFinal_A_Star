package planner;

import info.gridworld.actor.Actor;
import info.gridworld.actor.ActorWorld;
import info.gridworld.actor.Bug;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class DStarPlanner implements MotionPlanner {
    private Location start, target;
    private Grid<Actor> robotGrid;
    private Grid<Actor> plannerGrid;
    private Map<Location, Cell> cellMap;


    public DStarPlanner(Location start, Location target, Grid<Actor> robotGrid) {

    }

    private void planFirst() {
        ActorWorld plannerWorld = new ActorWorld(new BoundedGrid<>(robotGrid.getNumRows(), robotGrid.getNumCols()));
        plannerWorld.show();
        plannerGrid = plannerWorld.getGrid();
        plannerGrid.put(start, new Bug(Color.BLUE));
        cellMap = new HashMap<>();



    }

    @Override
    public Location next(Location current) {
        return null;
    }

    class Cell {
        public Location loc;
        public float c;
    }

    enum TAG { NEW, OPEN, CLOSED }
}
