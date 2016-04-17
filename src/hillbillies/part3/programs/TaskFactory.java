package hillbillies.part3.programs;

import hillbillies.model.Task;
import hillbillies.model.vector.IntVector;
import hillbillies.model.unit.Unit;
import hillbillies.part3.programs.expression.*;
import hillbillies.part3.programs.statement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by timo on 4/13/16.
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
        return new HerePositionExpression();
    }

    @Override
    public Expression<?> createLogPosition(SourceLocation sourceLocation) {
        return new LogPositionExpression();
    }

    @Override
    public Expression<?> createBoulderPosition(SourceLocation sourceLocation) {
        return new BoulderPositionExpression();
    }

    @Override
    public Expression<?> createWorkshopPosition(SourceLocation sourceLocation) {
        return new WorkshopPositionExpression();
    }

    @Override
    public Expression<?> createSelectedPosition(SourceLocation sourceLocation) {
        return new SelectedPositionExpression();
    }

    @Override
    public Expression<?> createNextToPosition(Expression<?> position, SourceLocation sourceLocation) {
        return new NextToPositionExpression((PositionExpression)position);
    }

    @Override
    public Expression<?> createLiteralPosition(int x, int y, int z, SourceLocation sourceLocation) {
        return new LiteralPositionExpression(x, y, z);
    }

    @Override
    public Expression<?> createThis(SourceLocation sourceLocation) {
        return new ThisUnitExpression();
    }

    @Override
    public Expression<?> createFriend(SourceLocation sourceLocation) {
        return new FriendUnitExpression();
    }

    @Override
    public Expression<?> createEnemy(SourceLocation sourceLocation) {
        return new EnemyUnitExpression();
    }

    @Override
    public Expression<?> createAny(SourceLocation sourceLocation) {
        return new AnyUnitExpression();
    }

    @Override
    public Expression<?> createTrue(SourceLocation sourceLocation) {
        return ((BooleanExpression)task -> true);
    }

    @Override
    public Expression<?> createFalse(SourceLocation sourceLocation) {
        return new FalseBooleanExpression();
    }
    //</editor-fold>
}
