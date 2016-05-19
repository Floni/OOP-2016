package hillbillies.model.programs.expression;

/**
 * Interface representing an Expression returning a Boolean.
 */
public interface BooleanExpression extends Expression<Boolean> {
    @Override
    default BooleanExpression getRead(String variable) {
        return task -> (Boolean)task.getVariable(variable); // should always exist in a valid program.
    }

    @Override
    default Expression<Boolean> castExpr(Expression<?> value) {
        return (BooleanExpression)value;
    }
}
