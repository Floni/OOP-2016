package hillbillies.model;

import hillbillies.model.Unit.Unit;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by timo on 3/14/16.
 */
public class Faction {

    private Set<Unit> units;

    public Faction() {
        this.units = new HashSet<>();
    }

    public void addUnit(Unit unit) {
        if (getFactionSize() < 50 && !units.contains(unit))
            units.add(unit);
    }

    public int getFactionSize() {
        return units.size();
    }

    public Set<Unit> getUnits() {
        return units;
    }

    public void removeUnit(Unit unit) {
        if (units.contains(unit))
            units.remove(unit);
    }

}
