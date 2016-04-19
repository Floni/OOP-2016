package hillbillies.model.exceptions;

import java.nio.channels.UnresolvedAddressException;

/**
 * Created by timo on 4/18/16.
 */
public class UnreachableTargetException extends RuntimeException {
    public UnreachableTargetException() {
        super("Unable to reach target position");
    }
}
