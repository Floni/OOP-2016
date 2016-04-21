package hillbillies.model.programs.expression;

/**
 * Interface representing an Expression returning a Boolean.
 */
public interface BooleanExpression extends Expression<Boolean> {
    @Override
    default BooleanExpression getRead(String variable) {
        return t -> (Boolean)t.getVariable(variable); // should always exist in a valid program.
    }
}
