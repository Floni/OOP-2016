package hillbillies.model.programs.statement;

/**
 * Class for tracking if a break statement is in a while loop.
 */
public class BreakChecker {
    private int depth;
    private boolean valid;

    public BreakChecker() {
        depth = 0;
        valid = true;
    }

    public void enterWhile() {
        depth++;
    }

    public void exitWhile() {
        depth--;
    }

    public void testBreak() {
        if (depth == 0)
            valid = false;
    }

    public boolean isValid() {
        return valid;
    }
}
