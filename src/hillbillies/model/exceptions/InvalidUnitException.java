package hillbillies.model.exceptions;

import hillbillies.model.unit.Unit;

/**
 * Exception indicating an invalid unit was used.
 */
public class InvalidUnitException extends RuntimeException {
    private static final long serialVersionUID = 3420911460895425791L;

    /**
     * Create an exception indicating that the given unit is invalid.
     *
     * @param   unit
     *          The invalid unit.
     *
     * @effect  Initialize the exception.
     *          | super("Invalid unit: " + unit.getName())
     */
    public InvalidUnitException(Unit unit) {
        super("Invalid unit: " + unit.getName());
    }

    /**
     * Create an exception indicating that the given unit is invalid.
     *
     * @param   message
     *          The message to display.
     *
     * @effect  Initialize the exception.
     *          | super(message)
     */
    public InvalidUnitException(String message) {
        super(message);
    }
}
