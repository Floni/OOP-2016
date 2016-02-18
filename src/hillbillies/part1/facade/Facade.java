package hillbillies.part1.facade;

import hillbillies.model.Unit;
import ogp.framework.util.ModelException;

/**
 *
 *
 * Created by timo on 17/02/2016.
 */
public class Facade implements IFacade {
    @Override
    public Unit createUnit(String name, int[] initialPosition, int weight, int agility, int strength, int toughness, boolean enableDefaultBehavior) throws ModelException {
        return new Unit(initialPosition[0], initialPosition[1], initialPosition[2]);
    }

    @Override
    public double[] getPosition(Unit unit) throws ModelException {
        return unit.getPosition();
    }

    @Override
    public int[] getCubeCoordinate(Unit unit) throws ModelException {
        return new int[0];
    }

    @Override
    public String getName(Unit unit) throws ModelException {
        return null;
    }

    @Override
    public void setName(Unit unit, String newName) throws ModelException {

    }

    @Override
    public int getWeight(Unit unit) throws ModelException {
        return 0;
    }

    @Override
    public void setWeight(Unit unit, int newValue) throws ModelException {

    }

    @Override
    public int getStrength(Unit unit) throws ModelException {
        return 0;
    }

    @Override
    public void setStrength(Unit unit, int newValue) throws ModelException {

    }

    @Override
    public int getAgility(Unit unit) throws ModelException {
        return 0;
    }

    @Override
    public void setAgility(Unit unit, int newValue) throws ModelException {

    }

    @Override
    public int getToughness(Unit unit) throws ModelException {
        return 0;
    }

    @Override
    public void setToughness(Unit unit, int newValue) throws ModelException {

    }

    @Override
    public int getMaxHitPoints(Unit unit) throws ModelException {
        return 0;
    }

    @Override
    public int getCurrentHitPoints(Unit unit) throws ModelException {
        return 0;
    }

    @Override
    public int getMaxStaminaPoints(Unit unit) throws ModelException {
        return 0;
    }

    @Override
    public int getCurrentStaminaPoints(Unit unit) throws ModelException {
        return 0;
    }

    @Override
    public void advanceTime(Unit unit, double dt) throws ModelException {

    }

    @Override
    public void moveToAdjacent(Unit unit, int dx, int dy, int dz) throws ModelException {

    }

    @Override
    public double getCurrentSpeed(Unit unit) throws ModelException {
        return 0;
    }

    @Override
    public boolean isMoving(Unit unit) throws ModelException {
        return false;
    }

    @Override
    public void startSprinting(Unit unit) throws ModelException {

    }

    @Override
    public void stopSprinting(Unit unit) throws ModelException {

    }

    @Override
    public boolean isSprinting(Unit unit) throws ModelException {
        return false;
    }

    @Override
    public double getOrientation(Unit unit) throws ModelException {
        return 0;
    }

    @Override
    public void moveTo(Unit unit, int[] cube) throws ModelException {

    }

    @Override
    public void work(Unit unit) throws ModelException {

    }

    @Override
    public boolean isWorking(Unit unit) throws ModelException {
        return false;
    }

    @Override
    public void fight(Unit attacker, Unit defender) throws ModelException {

    }

    @Override
    public boolean isAttacking(Unit unit) throws ModelException {
        return false;
    }

    @Override
    public void rest(Unit unit) throws ModelException {

    }

    @Override
    public boolean isResting(Unit unit) throws ModelException {
        return false;
    }

    @Override
    public void setDefaultBehaviorEnabled(Unit unit, boolean value) throws ModelException {

    }

    @Override
    public boolean isDefaultBehaviorEnabled(Unit unit) throws ModelException {
        return false;
    }
}
