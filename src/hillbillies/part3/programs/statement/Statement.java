package hillbillies.part3.programs.statement;

import hillbillies.model.Task;

/**
 * Created by timo on 4/13/16.
 */
public interface Statement {

    /**
     * reset the statement progress
     */
    void reset();

    boolean isDone(Task task);

    /**
     * execute one step of the statement.
     */
    void execute(Task task);
}










