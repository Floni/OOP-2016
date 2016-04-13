package hillbillies.part3.facade;

import hillbillies.model.Faction;
import hillbillies.model.Scheduler;
import hillbillies.model.Task;
import hillbillies.model.unit.Unit;
import hillbillies.part3.programs.ITaskFactory;
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
        return null;
    }

    @Override
    public boolean isWellFormed(Task task) throws ModelException {
        return false;
    }

    @Override
    public Scheduler getScheduler(Faction faction) throws ModelException {
        return null;
    }

    @Override
    public void schedule(Scheduler scheduler, Task task) throws ModelException {

    }

    @Override
    public void replace(Scheduler scheduler, Task original, Task replacement) throws ModelException {

    }

    @Override
    public boolean areTasksPartOf(Scheduler scheduler, Collection<Task> tasks) throws ModelException {
        return false;
    }

    @Override
    public Iterator<Task> getAllTasksIterator(Scheduler scheduler) throws ModelException {
        return null;
    }

    @Override
    public Set<Scheduler> getSchedulersForTask(Task task) throws ModelException {
        return null;
    }

    @Override
    public Unit getAssignedUnit(Task task) throws ModelException {
        return null;
    }

    @Override
    public Task getAssignedTask(Unit unit) throws ModelException {
        return null;
    }

    @Override
    public String getName(Task task) throws ModelException {
        return null;
    }

    @Override
    public int getPriority(Task task) throws ModelException {
        return 0;
    }
}
