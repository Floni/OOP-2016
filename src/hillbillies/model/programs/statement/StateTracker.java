package hillbillies.model.programs.statement;

/**
 * Interface for tracking the state of an activity, when an activity is interrupted it marks the task as interrupted.
 * But when an activity is finished it needs to mark the executing statement done, via this interface.
 */
public interface StateTracker {
    void setDone();
}
