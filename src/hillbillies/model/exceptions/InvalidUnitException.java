package hillbillies.model.exceptions;

import hillbillies.model.unit.Unit;

/**
 * Exception indicating an invalid unit was used.
 */
public class InvalidUnitException extends RuntimeException {
    public InvalidUnitException(Unit unit) {
        super("Invalid unit: " + unit.getName());
    }

    public InvalidUnitException(String message) {
        super(message);
    }
}
