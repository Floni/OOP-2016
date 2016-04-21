package hillbillies.model.programs.statement;

/**
 * Interface for tracking the state of an activity an unit is conducting.
 */
public interface ActivityTracker {
    void setDone();
    void setInterrupt();
}
