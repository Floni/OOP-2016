package hillbillies.part3.programs;

import hillbillies.model.Task;

import java.util.List;

/**
 * Created by timo on 4/13/16.
 */
public class TaskFactory implements ITaskFactory<Expression, Statement, Task> {
    @Override
    public List<Task> createTasks(String name, int priority, Statement activity, List<int[]> selectedCubes) {
        return null;
    }

    @Override
    public Statement createAssignment(String variableName, Expression value, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Statement createWhile(Expression condition, Statement body, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Statement createIf(Expression condition, Statement ifBody, Statement elseBody, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Statement createBreak(SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Statement createPrint(Expression value, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Statement createSequence(List<Statement> statements, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Statement createMoveTo(Expression position, SourceLocation sourceLocation) {
        return new MoveToStatement((PositionExpression) position);
    }

    @Override
    public Statement createWork(Expression position, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Statement createFollow(Expression unit, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Statement createAttack(Expression unit, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createReadVariable(String variableName, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createIsSolid(Expression position, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createIsPassable(Expression position, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createIsFriend(Expression unit, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createIsEnemy(Expression unit, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createIsAlive(Expression unit, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createCarriesItem(Expression unit, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createNot(Expression expression, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createAnd(Expression left, Expression right, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createOr(Expression left, Expression right, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createHerePosition(SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createLogPosition(SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createBoulderPosition(SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createWorkshopPosition(SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createSelectedPosition(SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createNextToPosition(Expression position, SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createLiteralPosition(int x, int y, int z, SourceLocation sourceLocation) {
        return new PositionExpression(x, y, z);
    }

    @Override
    public Expression createThis(SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createFriend(SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createEnemy(SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createAny(SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createTrue(SourceLocation sourceLocation) {
        return null;
    }

    @Override
    public Expression createFalse(SourceLocation sourceLocation) {
        return null;
    }
}
