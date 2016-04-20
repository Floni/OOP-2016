package hillbillies.model.programs.statement;

/**
 * Created by timo on 4/20/16.
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
