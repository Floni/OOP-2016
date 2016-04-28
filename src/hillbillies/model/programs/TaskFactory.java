package hillbillies.model.programs;

import hillbillies.model.Faction;
import hillbillies.model.Task;
import hillbillies.model.World;
import hillbillies.model.programs.exceptions.TaskErrorException;
import hillbillies.model.programs.exceptions.TaskInterruptException;
import hillbillies.model.unit.Unit;
import hillbillies.model.vector.*;
import hillbillies.model.vector.Vector;
import hillbillies.part3.programs.ITaskFactory;
import hillbillies.part3.programs.SourceLocation;
import hillbillies.model.programs.expression.*;
import hillbillies.model.programs.statement.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * TaskFactory for crating new tasks.
 */
public class TaskFactory implements ITaskFactory<Expression<?>, Statement, Task> {

    private Map<String, Expression<?>> varTypeMap;

    public TaskFactory() {
        varTypeMap = new HashMap<>();
    }

    @Override
    public List<Task> createTasks(String name, int priority, Statement activity, List<int[]> selectedCubes) {
        if (selectedCubes.isEmpty())
            return Collections.singletonList(new Task(name, priority, activity, null));
        return selectedCubes.stream()
                .map(cube -> new Task(name, priority, activity, new IntVector(cube)))
                .collect(Collectors.toList());
    }

    //<editor-fold desc="Statements">
    @Override
    public Statement createAssignment(String variableName, Expression<?> value, SourceLocation sourceLocation) {
        varTypeMap.put(variableName, value.getRead(variableName));
        return new AssignStatement<>(variableName, value);
    }

    @Override
    public Statement createWhile(Expression<?> condition, Statement body, SourceLocation sourceLocation) {
        return new WhileStatement((BooleanExpression)condition, body);
    }

    @Override
    public Statement createIf(Expression<?> condition, Statement ifBody, Statement elseBody, SourceLocation sourceLocation) {
        return new IfStatement((BooleanExpression)condition, ifBody, elseBody);
    }

    @Override
    public Statement createBreak(SourceLocation sourceLocation) {
        return new BreakStatement();
    }

    @Override
    public Statement createPrint(Expression<?> value, SourceLocation sourceLocation) {
        return new Statement() {
            boolean done = false;
            @Override
            public void reset() {
                done = false;
            }

            @Override
            public boolean isDone(Task task) {
                return done;
            }

            @Override
            public void execute(Task task) {
                done = true;
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
        return new FollowStatement((UnitExpression) unit);
    }

    @Override
    public Statement createAttack(Expression<?> unit, SourceLocation sourceLocation) {
        return new AttackStatement((UnitExpression)unit);
    }
    //</editor-fold>

    //<editor-fold desc="Expressions">
    @Override
    public Expression<?> createReadVariable(String variableName, SourceLocation sourceLocation) {
        return varTypeMap.get(variableName);
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
        return (PositionExpression) t -> {
            final Unit unit = t.getAssignedUnit();
            final World world = unit.getWorld();
            final IntVector unitPos = unit.getPosition().toIntVector();
            return world.getLogs().stream().map(l -> l.getPosition().toIntVector())
                    .filter(l -> world.getPathFinder().isReachable(unitPos, l))
                    .min((Comparator<IntVector>) (l1, l2) -> (int)(l1.distance(unitPos) - l2.distance(unitPos)))
                    .orElseThrow(() -> new TaskInterruptException("no possible logs"));
        };
    }

    @Override
    public Expression<?> createBoulderPosition(SourceLocation sourceLocation) {
        return (PositionExpression) t -> {
            final Unit unit = t.getAssignedUnit();
            final World world = unit.getWorld();
            final IntVector unitPos = unit.getPosition().toIntVector();
            return world.getBoulders().stream().map(l -> l.getPosition().toIntVector())
                    .filter(l -> world.getPathFinder().isReachable(unitPos, l))
                    .min((Comparator<IntVector>) (l1, l2) -> (int)(l1.distance(unitPos) - l2.distance(unitPos)))
                    .orElseThrow(() -> new TaskInterruptException("no possible logs"));
        };
    }

    @Override
    public Expression<?> createWorkshopPosition(SourceLocation sourceLocation) {
        return (PositionExpression) t -> {
            final Unit unit = t.getAssignedUnit();
            final World world = unit.getWorld();
            final IntVector unitPos = unit.getPosition().toIntVector();
            return world.getAllWorkshops()
                    .filter(w -> world.getPathFinder().isReachable(unitPos, w))
                    .min((Comparator<IntVector>) (w1, w2) -> (int)(w1.distance(unitPos) - w2.distance(unitPos)))
                    .orElseThrow(() -> new TaskInterruptException("no possible workshops"));
        };
    }

    @Override
    public Expression<?> createSelectedPosition(SourceLocation sourceLocation) {
        return (PositionExpression) (task) -> task.getSelectedPosition()
                .orElseThrow(() -> new TaskErrorException("selected used in program, but task has no selected cubes"));
    }

    @Override
    public Expression<?> createNextToPosition(Expression<?> position, SourceLocation sourceLocation) {
        return new NextToPositionExpression((PositionExpression)position);
    }

    @Override
    public Expression<?> createPositionOf(Expression<?> unit, SourceLocation sourceLocation) {
        return new PositionOfPositionExpression((UnitExpression)unit);
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
        return (UnitExpression) t -> {
            final Unit unit = t.getAssignedUnit();
            final World world = unit.getWorld();
            final Vector unitPos = unit.getPosition();
            final Faction fac = unit.getFaction();
            return fac.getUnits().stream().filter( u -> u != unit)
                    .filter(u -> world.getPathFinder().isReachable(unitPos.toIntVector(), u.getPosition().toIntVector()))
                    .min((Comparator<Unit>) (u1, u2) -> (int)(u1.getPosition().distance(unitPos)))
                    .orElseThrow(() -> new TaskInterruptException("no reachable friends found"));
        };
    }

    @Override
    public Expression<?> createEnemy(SourceLocation sourceLocation) {
        return (UnitExpression) task -> {
            final Unit unit = task.getAssignedUnit();
            final Faction fac = unit.getFaction();
            final World world = unit.getWorld();
            final Vector unitPos = unit.getPosition();
            return world.getUnits().stream().filter(u -> u.getFaction() != fac)
                    .filter(u -> world.getPathFinder().isReachable(unitPos.toIntVector(), u.getPosition().toIntVector()))
                    .min((Comparator<Unit>) (u1, u2) -> (int)(u1.getPosition().distance(unitPos)))
                    .orElseThrow(() -> new TaskInterruptException("no reachable enemies found"));
        };
    }

    @Override
    public Expression<?> createAny(SourceLocation sourceLocation) {
        return (UnitExpression) task -> {
            final Unit unit = task.getAssignedUnit();
            final World world = unit.getWorld();
            final Vector unitPos = unit.getPosition();
            return world.getUnits().stream().filter(u -> u != unit)
                    .filter(u -> world.getPathFinder().isReachable(unitPos.toIntVector(), u.getPosition().toIntVector()))
                    .min((Comparator<Unit>) (u1, u2) -> (int)(u1.getPosition().distance(unitPos)))
                    .orElseThrow(() -> new TaskInterruptException("no other units available"));
        };
    }

    @Override
    public Expression<?> createTrue(SourceLocation sourceLocation) {
        return (BooleanExpression)task -> true;
    }

    @Override
    public Expression<?> createFalse(SourceLocation sourceLocation) {
        return (BooleanExpression) task -> false;
    }
    //</editor-fold>
}
