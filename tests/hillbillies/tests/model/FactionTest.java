package hillbillies.tests.model;

import hillbillies.model.Faction;
import hillbillies.model.Unit.Unit;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Basic tests for the class Faction
 */
public class FactionTest {

    private Faction faction;

    @Before
    public void setUp() throws Exception {
        this.faction = new Faction();
    }

    private Unit addUnit() {
        Unit unit = new Unit("Spawn", 0, 0, 0, 50, 50, 50, 50);
        this.faction.addUnit(unit);
        return unit;
    }

    @Test
    public void testAddUnit() throws Exception {
        Unit test1 = addUnit();
        Unit test2 = addUnit();
        assertTrue(this.faction.getUnits().contains(test1));
        assertEquals(faction, test1.getFaction());
        assertTrue(this.faction.getUnits().contains(test2));
        assertEquals(faction, test2.getFaction());
        for (int i = 0; i < 50; i++) {
            addUnit();
        }
        assertEquals(50, this.faction.getFactionSize());

    }

    @Test
    public void testGetFactionSize() {
        addUnit();
        assertEquals(1, this.faction.getFactionSize());
        assertEquals(this.faction.getFactionSize(), this.faction.getUnits().size());
        for (int i = 1; i < 50; i++) {
            assertEquals(this.faction.getFactionSize(), i);
            addUnit();
        }
    }

    @Test
    public void testGetUnits() throws Exception {
        Unit test1 = addUnit();
        Unit test2 = addUnit();
        assertTrue(this.faction.getUnits().contains(test1));
        assertTrue(this.faction.getUnits().contains(test2));
        assertEquals(2, this.faction.getUnits().size());
    }

    @Test
    public void testRemoveUnit() throws Exception {
        Unit unit = addUnit();
        assertTrue(this.faction.getUnits().contains(unit));
        assertEquals(1, this.faction.getFactionSize());
        this.faction.removeUnit(unit);
        assertFalse(this.faction.getUnits().contains(unit));
        assertEquals(0, this.faction.getFactionSize());
        assertNull(unit.getFaction());

    }
}