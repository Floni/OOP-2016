package hillbillies.part1.facade;

import hillbillies.model.exceptions.InvalidActionException;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.exceptions.InvalidUnitException;
import hillbillies.model.exceptions.UnreachableTargetException;
import hillbillies.model.unit.Unit;
import hillbillies.model.vector.IntVector;
import ogp.framework.util.ModelException;

/**
 * Facade for part1.
 */
public class Facade implements IFacade {
    @Override
    public Unit createUnit(String name, int[] initialPosition, int weight, int agility, int strength, int toughness, boolean enableDefaultBehavior) throws ModelException {
        try {
            Unit ret = new Unit(name, initialPosition[0], initialPosition[1], initialPosition[2],
                    weight, strength, agility, toughness);
            if (enableDefaultBehavior)
                ret.startDefaultBehaviour();
            return ret;
        } catch (IllegalArgumentException | InvalidPositionException err) {
            throw new ModelException(err.getMessage(), err);
        }
    }

    @Override
    public double[] getPosition(Unit unit) throws ModelException {
        return unit.getPosition().toDoubleArray();
    }

    @Override
    public int[] getCubeCoordinate(Unit unit) throws ModelException {
        return unit.getPosition().toIntVector().toIntArray();
    }

    @Override
    public String getName(Unit unit) throws ModelException {
        return unit.getName();
    }

    @Override
    public void setName(Unit unit, String newName) throws ModelException {
        try {
            unit.setName(newName);
        } catch(IllegalArgumentException err){
            throw new ModelException(err.getMessage(), err);
        }
    }

    @Override
    public int getWeight(Unit unit) throws ModelException {
        return unit.getWeight();
    }

    @Override
    public void setWeight(Unit unit, int newValue) throws ModelException {
        unit.setWeight(newValue);
    }

    @Override
    public int getStrength(Unit unit) throws ModelException {
        return unit.getStrength();
    }

    @Override
    public void setStrength(Unit unit, int newValue) throws ModelException {
        unit.setStrength(newValue);
    }

    @Override
    public int getAgility(Unit unit) throws ModelException {
        return unit.getAgility();
    }

    @Override
    public void setAgility(Unit unit, int newValue) throws ModelException {
        unit.setAgility(newValue);
    }

    @Override
    public int getToughness(Unit unit) throws ModelException {
        return unit.getToughness();
    }

    @Override
    public void setToughness(Unit unit, int newValue) throws ModelException {
        unit.setToughness(newValue);
    }

    @Override
    public int getMaxHitPoints(Unit unit) throws ModelException {
        return unit.getMaxPoints();
    }

    @Override
    public int getCurrentHitPoints(Unit unit) throws ModelException {
        return unit.getHitPoints();
    }

    @Override
    public int getMaxStaminaPoints(Unit unit) throws ModelException {
        return unit.getMaxPoints();
    }

    @Override
    public int getCurrentStaminaPoints(Unit unit) throws ModelException {
        return unit.getStamina();
    }

    @Override
    public void advanceTime(Unit unit, double dt) throws ModelException {
        try {
            unit.advanceTime(dt);
        } catch (IllegalArgumentException err) {
            throw new ModelException(err.getMessage(), err);
        }
    }

    @Override
    public void moveToAdjacent(Unit unit, int dx, int dy, int dz) throws ModelException {
        try {
            unit.moveToAdjacent(dx, dy, dz);
        } catch (IllegalArgumentException | InvalidActionException | InvalidPositionException err) {
            throw new ModelException(err.getMessage(), err);
        }
    }

    @Override
    public double getCurrentSpeed(Unit unit) throws ModelException {
        return unit.getSpeedScalar();
    }

    @Override
    public boolean isMoving(Unit unit) throws ModelException {
        return unit.isMoving();
    }

    @Override
    public void startSprinting(Unit unit) throws ModelException {
        try {
            unit.setSprinting(true);
        } catch (InvalidActionException err) {
            throw new ModelException(err.getMessage(), err);
        }
    }

    @Override
    public void stopSprinting(Unit unit) throws ModelException {
        try {
            unit.setSprinting(false);
        } catch (InvalidActionException err) {
            throw new ModelException(err.getMessage(), err);
        }
    }

    @Override
    public boolean isSprinting(Unit unit) throws ModelException {
        return unit.isSprinting();
    }

    @Override
    public double getOrientation(Unit unit) throws ModelException {
        return unit.getOrientation();
    }

    @Override
    public void moveTo(Unit unit, int[] cube) throws ModelException {
        try {
            unit.moveTo(new IntVector(cube));
        } catch (InvalidActionException | InvalidPositionException | UnreachableTargetException err) {
            throw new ModelException(err.getMessage(), err);
        }
    }

    @Override
    public void work(Unit unit) throws ModelException {
        try{
            //unit.work();
            throw new ModelException("deprecated!");
        } catch (IllegalArgumentException err) {
            throw new ModelException(err.getMessage(), err);
        }
    }

    @Override
    public boolean isWorking(Unit unit) throws ModelException {
        return unit.isWorking();
    }

    @Override
    public void fight(Unit attacker, Unit defender) throws ModelException {
        try {
            attacker.attack(defender);
        } catch (InvalidActionException | InvalidUnitException err) {
            throw new ModelException(err.getMessage(), err);
        }
    }

    @Override
    public boolean isAttacking(Unit unit) throws ModelException {
        return unit.isAttacking();
    }

    @Override
    public void rest(Unit unit) throws ModelException {
        try {
            unit.rest();
        } catch (InvalidActionException err) {
            throw new ModelException(err.getMessage(), err);
        }
    }

    @Override
    public boolean isResting(Unit unit) throws ModelException {
        return unit.isResting();
    }

    @Override
    public void setDefaultBehaviorEnabled(Unit unit, boolean value) throws ModelException {
        if (value) {
            unit.startDefaultBehaviour();
        } else {
            unit.stopDefaultBehaviour();
        }
    }

    @Override
    public boolean isDefaultBehaviorEnabled(Unit unit) throws ModelException {
        return unit.isDefaultEnabled();
    }
}
