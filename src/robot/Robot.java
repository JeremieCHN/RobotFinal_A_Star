package robot;

import info.gridworld.actor.Actor;
import info.gridworld.actor.Bug;
import info.gridworld.actor.Flower;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import planner.AStarPlanner;
import planner.MotionPlanner;

import javax.swing.*;
import java.awt.*;

public class Robot extends Bug {

    private Location target;
    private MotionPlanner planner;

    public void setTarget(Location target) {
        // 已经有终点
        if (this.target != null) {
            JOptionPane.showMessageDialog(null, "已经有终点");
            return;
        }
        this.target = target;
        planner = new AStarPlanner(this.getLocation(), target, this.getGrid());
        this.getGrid().put(target, new TargetActor());
    }

    @Override
    public void act() {
        // 没有设置终点
        if (target == null || planner == null) {
            JOptionPane.showMessageDialog(null, "未设置终点");
            return;
        }

        Location current = this.getLocation();

        // 已经抵达终点
        if (current.equals(target)) {
            JOptionPane.showMessageDialog(null, "已经抵达终点");
            return;
        }

        // 获取下一步
        Location next = planner.next(current);
        if (next != null) {
            setDirection(current.getDirectionToward(next));
            move(next);
        } else {
            JOptionPane.showMessageDialog(null, "决策返回空的下一步");
        }
    }

    private void move(Location loc) {
        Location oldLoc = this.getLocation();
        Grid<Actor> gr = this.getGrid();
        Actor next = gr.get(loc);
        // 下一步是障碍物
        if (next instanceof Obstruction) {
            JOptionPane.showMessageDialog(null, "Error: next location " + loc.toString() + " is Obstruction");
            return;
        } else if (next instanceof TargetActor) {
            gr.remove(loc);
            JOptionPane.showMessageDialog(null, "即将抵达目标");
        } else if (next instanceof Flower) {
            gr.remove(loc);
        }
        super.moveTo(loc);
        Flower flower = new Flower(this.getColor());
        flower.putSelfInGrid(gr, oldLoc);
    }

    @Override
    public void setColor(Color newColor) {}

    @Override
    public void moveTo(Location loc) {}
}
