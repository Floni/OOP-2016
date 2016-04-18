package hillbillies.model.programs.expression;

import hillbillies.model.Task;

/**
 * Created by timo on 4/13/16.
 */
public interface Expression<T> {
    T getValue(Task task);
}
