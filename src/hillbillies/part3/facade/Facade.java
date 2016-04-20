package hillbillies.part3.facade;

import hillbillies.model.Faction;
import hillbillies.model.Scheduler;
import hillbillies.model.Task;
import hillbillies.model.unit.Unit;
import hillbillies.part3.programs.ITaskFactory;
import hillbillies.model.programs.TaskFactory;
import ogp.framework.util.ModelException;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by timo on 4/13/16.
 */
public class Facade extends hillbillies.part2.facade.Facade implements IFacade {
    @Override
    public ITaskFactory<?, ?, Task> createTaskFactory() {
        return new TaskFactory();
    }

    @Override
    public boolean isWellFormed(Task task) throws ModelException {
        return task.isWellFormed();
    }

    @Override
    public Scheduler getScheduler(Faction faction) throws ModelException {
        return faction.getScheduler();
    }

    @Override
    public void schedule(Scheduler scheduler, Task task) throws ModelException {
        scheduler.schedule(task);
    }

    @Override
    public void replace(Scheduler scheduler, Task original, Task replacement) throws ModelException {
        scheduler.replace(original, replacement);
    }

    @Override
    public boolean areTasksPartOf(Scheduler scheduler, Collection<Task> tasks) throws ModelException {
        return  scheduler.containsAllTasks(tasks);
    }

    @Override
    public Iterator<Task> getAllTasksIterator(Scheduler scheduler) throws ModelException {
        return scheduler.getAllTasksIterator();
    }

    @Override
    public Set<Scheduler> getSchedulersForTask(Task task) throws ModelException {
        return task.getSchedulers();
    }

    @Override
    public Unit getAssignedUnit(Task task) throws ModelException {
        return task.getAssignedUnit();
    }

    @Override
    public Task getAssignedTask(Unit unit) throws ModelException {
        return unit.getAssignedTask();
    }

    @Override
    public String getName(Task task) throws ModelException {
        return task.getName();
    }

    @Override
    public int getPriority(Task task) throws ModelException {
        return task.getPriority();
    }
}
