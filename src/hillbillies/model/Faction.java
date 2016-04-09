package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.model.Unit.Unit;

import java.util.HashSet;
import java.util.Set;

/**
 * A class containing a set of units.
 *
 */
public class Faction {

    private static final int MAX_UNITS = 50;

    private Set<Unit> units;

    /**
     * Create a new faction which contains no units.
     *
     * @post    The faction size will be zero.
     */
    public Faction() {
        this.units = new HashSet<>();
    }

    /**
     * Adds the given unit to this faction.
     *
     * @param   unit
     *          The unit to be added to this faction.
     * @post    If the faction size is less than MAX_UNITS, the faction will now contain the unit.
     * @effect  If the faction size is less than MAX_UNITS, the units faction is set to this faction.
     *          | unit.setFaction(this)
     */
    public void addUnit(Unit unit) {
        if (getFactionSize() < MAX_UNITS) {
            units.add(unit);
            unit.setFaction(this);
        }
    }

    /**
     * Returns the size of the faction.
     */
    @Basic
    public int getFactionSize() {
        return units.size();
    }

    /**
     * Returns all units in the faction.
     */
    @Basic
    public Set<Unit> getUnits() {
        return new HashSet<>(units);
    }

    /**
     * Removes the given unit from the faction.
     *
     * @param   unit
     *          The unit to be removed.
     * @post    The factions will not contain the unit and if the faction previously contained the unit,
     *          the units faction is set to null.
     */
    public void removeUnit(Unit unit) {
        if (units.contains(unit)) {
            units.remove(unit);
            unit.setFaction(null);
        }
    }

}
