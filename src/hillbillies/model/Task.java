package hillbillies.model;

import hillbillies.model.Vector.IntVector;
import hillbillies.model.unit.Unit;
import hillbillies.part3.programs.statement.Statement;

/**
 * Created by timo on 4/13/16.
 */
public class Task implements Comparable<Task> {

    private Unit assignedUnit;
    private int priority;

    private final String name;
    private final Statement mainStatement;
    private final IntVector selected;


    public Task(String name, int priority, Statement main, IntVector selected) {
        this.name = name;
        this.mainStatement = main;
        this.selected = selected;
        this.setPriority(priority);

    }

    public boolean isAssigned() {
        return assignedUnit != null;
    }

    public Unit getAssignedUnit() {
        return assignedUnit;
    }

    public void setAssignedUnit(Unit assignedUnit) {
        this.assignedUnit = assignedUnit;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public int compareTo(Task o) {
        return Integer.valueOf(this.getPriority()).compareTo(o.getPriority());
    }
}
