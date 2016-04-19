package hillbillies.model.programs;

import hillbillies.model.Faction;
import hillbillies.model.Task;
import hillbillies.model.World;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.unit.Unit;
import hillbillies.model.vector.IntVector;
import hillbillies.part3.programs.ITaskFactory;
import hillbillies.part3.programs.SourceLocation;
import hillbillies.model.programs.expression.*;
import hillbillies.model.programs.statement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * TaskFactory for crating new tasks.
 */
public class TaskFactory implements ITaskFactory<Expression<?>, Statement, Task> {

    @Override
    public List<Task> createTasks(String name, int priority, Statement activity, List<int[]> selectedCubes) {
        if (selectedCubes.isEmpty()) {
            List<Task> ret = new ArrayList<>();
            ret.add(new Task(name, priority, activity, null));
            return ret;
        }
        return selectedCubes.stream().map(cube -> new Task(name, priority, activity, new IntVector(cube))).collect(Collectors.toList());
    }

    //<editor-fold desc="Statements">
    @Override
    public Statement createAssignment(String variableName, Expression<?> value, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Statement createWhile(Expression<?> condition, Statement body, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Statement createIf(Expression<?> condition, Statement ifBody, Statement elseBody, SourceLocation sourceLocation) {
        return new IfStatement((BooleanExpression)condition, ifBody, elseBody);
    }

    @Override
    public Statement createBreak(SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Statement createPrint(Expression<?> value, SourceLocation sourceLocation) {
        return new Statement() {
            @Override
            public void reset() {
                // NOP
            }

            @Override
            public boolean isDone(Task task) {
                return true;
            }

            @Override
            public void execute(Task task) {
                System.out.println(value.getValue(task));
            }
        };
    }

    @Override
    public Statement createSequence(List<Statement> statements, SourceLocation sourceLocation) {
        return new SequenceStatement(statements);
    }

    @Override
    public Statement createMoveTo(Expression<?> position, SourceLocation sourceLocation) {
        return new MoveToStatement((PositionExpression)position);
    }

    @Override
    public Statement createWork(Expression<?> position, SourceLocation sourceLocation) {
        return new WorkStatement((PositionExpression)position);
    }

    @Override
    public Statement createFollow(Expression<?> unit, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Statement createAttack(Expression<?> unit, SourceLocation sourceLocation) {
        return new AttackStatement((UnitExpression)unit);
    }
    //</editor-fold>


    //<editor-fold desc="Expressions">
    @Override
    public Expression<?> createReadVariable(String variableName, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression<?> createIsSolid(Expression<?> position, SourceLocation sourceLocation) {
        return new IsSolidBooleanExpression((PositionExpression) position);
    }

    @Override
    public Expression<?> createIsPassable(Expression<?> position, SourceLocation sourceLocation) {
        return new IsPassableBooleanExpression((PositionExpression) position);
    }

    @Override
    public Expression<?> createIsFriend(Expression<?> unit, SourceLocation sourceLocation) {
        return new IsFriendBooleanExpression((UnitExpression) unit);
    }

    @Override
    public Expression<?> createIsEnemy(Expression<?> unit, SourceLocation sourceLocation) {
        return new IsEnemyBooleanExpression((UnitExpression) unit);
    }

    @Override
    public Expression<?> createIsAlive(Expression<?> unit, SourceLocation sourceLocation) {
        return new IsAliveBooleanExpression((UnitExpression) unit);
    }

    @Override
    public Expression<?> createCarriesItem(Expression<?> unit, SourceLocation sourceLocation) {
        return new CarriesItemExpression((UnitExpression) unit);
    }

    @Override
    public Expression<?> createNot(Expression<?> expression, SourceLocation sourceLocation) {
        return new NotBooleanExpression((BooleanExpression) expression);
    }

    @Override
    public Expression<?> createAnd(Expression<?> left, Expression<?> right, SourceLocation sourceLocation) {
        return new AndBooleanExpression((BooleanExpression) left, (BooleanExpression) right);
    }

    @Override
    public Expression<?> createOr(Expression<?> left, Expression<?> right, SourceLocation sourceLocation) {
        return new OrBooleanExpression((BooleanExpression) left, (BooleanExpression) right);
    }

    @Override
    public Expression<?> createHerePosition(SourceLocation sourceLocation) {
        return (PositionExpression) task -> task.getAssignedUnit().getPosition().toIntVector();
    }

    @Override
    public Expression<?> createLogPosition(SourceLocation sourceLocation) {
        //TODO return null & boulder (hasNext)
        return (PositionExpression) task -> task.getAssignedUnit().getWorld().getLogs().iterator().next().getPosition().toIntVector();
    }

    @Override
    public Expression<?> createBoulderPosition(SourceLocation sourceLocation) {
        // TODO: see Log
        return (PositionExpression) task -> task.getAssignedUnit().getWorld().getBoulders().iterator().next().getPosition().toIntVector();
    }

    @Override
    public Expression<?> createWorkshopPosition(SourceLocation sourceLocation) {
        return null; //TODO
    }

    @Override
    public Expression<?> createSelectedPosition(SourceLocation sourceLocation) {
        return (PositionExpression) Task::getSelectedPosition; // TODO: throw if no selected?
    }

    @Override
    public Expression<?> createNextToPosition(Expression<?> position, SourceLocation sourceLocation) {
        return new NextToPositionExpression((PositionExpression)position);
    }

    @Override
    public Expression<?> createLiteralPosition(int x, int y, int z, SourceLocation sourceLocation) {
        return (PositionExpression) task -> new IntVector(x, y, z);
    }

    @Override
    public Expression<?> createThis(SourceLocation sourceLocation) {
        return (UnitExpression) Task::getAssignedUnit;
    }

    @Override
    public Expression<?> createFriend(SourceLocation sourceLocation) {
        return (UnitExpression) task -> {
            Faction fac = task.getAssignedUnit().getFaction();
            return fac.getUnits().stream().filter( u -> u != task.getAssignedUnit())
                    .findAny().orElseThrow(() -> new TaskInterruptException("Unit has no friends"));
        };
    }

    @Override
    public Expression<?> createEnemy(SourceLocation sourceLocation) {
        return (UnitExpression) task -> {
            Faction fac = task.getAssignedUnit().getFaction();
            World world = task.getAssignedUnit().getWorld();
            return world.getUnits().stream().filter(u -> u.getFaction() != fac)
                    .findAny().orElseThrow(() -> new TaskInterruptException("unit has no enemies"));
        };
    }

    @Override
    public Expression<?> createAny(SourceLocation sourceLocation) {
        return (UnitExpression) task -> {
            World world = task.getAssignedUnit().getWorld();
            return world.getUnits().stream().filter(u -> u != task.getAssignedUnit())
                    .findAny().orElseThrow(() -> new TaskInterruptException("no other units available"));
        };
    }

    @Override
    public Expression<?> createTrue(SourceLocation sourceLocation) {
        return ((BooleanExpression)task -> true);
    }

    @Override
    public Expression<?> createFalse(SourceLocation sourceLocation) {
        return (BooleanExpression) task -> false;
    }
    //</editor-fold>
}
