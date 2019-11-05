package planner;

import info.gridworld.actor.Actor;
import info.gridworld.actor.ActorWorld;
import info.gridworld.actor.Bug;
import info.gridworld.grid.BoundedGrid;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;
import info.gridworld.grid.UnboundedGrid;
import robot.MapPointer;
import robot.Obstruction;
import robot.TargetActor;

import java.awt.*;
import java.util.*;
import java.util.List;

public class AStarPlanner implements MotionPlanner {

    private Grid<Actor> robotGrid;
    private Location start;
    private Location target;
    private Map<Location, Location> parentMap;

    public AStarPlanner(Location start, Location target, Grid<Actor> robotGrid) {
        this.robotGrid = robotGrid;
        this.start = start;
        this.target = target;
    }

    private void buildPath() {
        // 初始化展示
        CellMap cellMap = new CellMap();
        Grid<Actor> planGrid;
        if (robotGrid.getNumRows() == -1) {
            planGrid = new UnboundedGrid<>();
        } else {
            planGrid = new BoundedGrid<>(robotGrid.getNumRows(), robotGrid.getNumCols());
        }
        planGrid.put(start, new Bug(Color.BLUE));
        new ActorWorld(planGrid).show();

        // openSet，自动根据F值进行排序
        // 比较函数要小心，因为返回0的话会认为两个元素是同一个然后不添加
        SortedSet<Location> openSet = new TreeSet<>((loc1, loc2) -> {
            Cell cell1 = cellMap.get(loc1);
            Cell cell2 = cellMap.get(loc2);
            if (cell1.fValue == -1 && cell2.fValue != -1) return 1;
            if (cell2.fValue == -1 && cell1.fValue != -1) return 1;
            if (cell1.fValue != cell2.fValue) return cell1.fValue - cell2.fValue;
            if (cell1.hValue != cell2.hValue) return cell1.hValue - cell2.hValue;
            if (cell1.gValue != cell2.gValue) return cell1.gValue - cell2.gValue;
            if (loc1.getRow() != loc2.getRow()) return loc1.getRow() - loc2.getRow();
            if (loc1.getCol() != loc2.getCol()) return loc1.getCol() - loc2.getCol();
            return 0;
        });
        // closeSet
        Set<Location> closeSet = new HashSet<>();

        // 初始化加入起点
        Cell startCell = new Cell(start);
        startCell.gValue = 0;
        startCell.hValue = Math.abs(start.getRow() - target.getRow()) + Math.abs(start.getCol() - target.getCol());
        startCell.fValue = startCell.gValue + startCell.hValue;
        cellMap.put(start, startCell);

        openSet.add(start);
        while (!openSet.isEmpty()) {
            Location curr = openSet.first();
            if (curr.equals(target)) break;

            openSet.remove(curr);
            closeSet.add(curr);

            Cell cell = cellMap.get(curr);
            List<Location> neighbors = getNeighbors(curr);
            for (Location neighLoc : neighbors) {
                // 位于closeSet或者地图外或者障碍物不需要进行处理
                if (closeSet.contains(neighLoc))
                    continue;
                else if (!robotGrid.isValid(neighLoc)) {
                    closeSet.add(neighLoc);
                    continue;
                } else if (robotGrid.get(neighLoc) instanceof Obstruction) {
                    planGrid.put(neighLoc, new Obstruction());
                    closeSet.add(neighLoc);
                    continue;
                }

                Cell neighCell = cellMap.get(neighLoc);
                if (neighCell.hValue == -1)
                    neighCell.hValue = Math.abs(neighLoc.getRow() - target.getRow()) + Math.abs(neighLoc.getCol() - target.getCol());

                // 计算以当前点为父节点时，邻居的一众数值
                int gValue = cell.gValue + (neighLoc.getDirectionToward(curr) % 90 == 0 ? 10 : 14);
                int fValue = neighCell.hValue + gValue;

                if (neighCell.parent == null || neighCell.fValue > fValue) {
                    neighCell.parent = curr;
                    neighCell.gValue = gValue;
                    neighCell.fValue = fValue;

                    // 展示部分
                    Actor neighPointer = planGrid.get(neighCell.loc);
                    if (neighPointer == null) {
                        neighPointer = new MapPointer();
                        planGrid.put(neighCell.loc, neighPointer);
                    }
                    neighPointer.setDirection(neighCell.loc.getDirectionToward(neighCell.parent));
                }

                openSet.add(neighLoc);
            }
        }

        // 终点的标志换一下颜色
        planGrid.get(target).setColor(Color.green);

        parentMap = new HashMap<>();
        Cell it = cellMap.get(target);
        while (it.parent != null) {
            parentMap.put(it.parent, it.loc);
            it = cellMap.get(it.parent);
        }
    }

    // 获取一个点的8个邻居
    private List<Location> getNeighbors(Location loc) {
        List<Location> neighbors = new ArrayList<>();
        neighbors.add(new Location(loc.getRow() - 1, loc.getCol()));
        neighbors.add(new Location(loc.getRow(), loc.getCol() - 1));
        neighbors.add(new Location(loc.getRow() + 1, loc.getCol()));
        neighbors.add(new Location(loc.getRow(), loc.getCol() + 1));
        neighbors.add(new Location(loc.getRow() - 1, loc.getCol() - 1));
        neighbors.add(new Location(loc.getRow() - 1, loc.getCol() + 1));
        neighbors.add(new Location(loc.getRow() + 1, loc.getCol() - 1));
        neighbors.add(new Location(loc.getRow() + 1, loc.getCol() + 1));
        return neighbors;
    }

    @Override
    public Location next(Location current) {
        if (parentMap == null)
            buildPath();

        if (parentMap.containsKey(current))
            return parentMap.get(current);

        return null;
    }

    // 封装一个可以保存位置和数值的映射类
    class CellMap extends HashMap<Location, Cell> {
        Cell get(Location key) {
            if (!containsKey(key)) {
                Cell cell = new Cell(key);
                put(key, cell);
            }
            return super.get(key);
        }

        void put(Location loc) {
            super.put(loc, new Cell(loc));
        }
    }

    // 用来存放格子内数值的类
    class Cell {
        Cell(Location loc) {
            this.loc = loc;
            gValue = -1;
            fValue = -1;
            hValue = -1;
        }

        Location loc;
        int gValue;
        int fValue;
        int hValue;
        Location parent = null;

        @Override
        public int hashCode() {
            long value = loc.getCol() + ((long) loc.getRow() << 32);
            return Long.hashCode(value);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Cell) {
                return ((Cell) obj).loc.equals(this.loc);
            }
            return false;
        }
    }
}
