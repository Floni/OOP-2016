package hillbillies.model;

import java.util.Set;

/**
 * Created by timo on 3/14/16.
 */
public class Faction {

    private Set<Unit> units;


    //TODO: check if unit not already in a faction
    public void addUnit(Unit unit) {
        if (getFactionSize() < 50)
            units.add(unit);
    }

    public int getFactionSize() {
        return units.size();
    }

    public Set<Unit> getUnits() {
        return units;
    }

}
