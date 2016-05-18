package hillbillies.part2.facade;

import com.sun.javafx.sg.prism.NGShape;
import hillbillies.model.*;
// import hillbillies.part1.facade.Facade;
import hillbillies.model.exceptions.InvalidActionException;
import hillbillies.model.exceptions.InvalidCubeTypeException;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.unit.Unit;
import hillbillies.model.vector.IntVector;
import hillbillies.part2.listener.TerrainChangeListener;
import ogp.framework.util.ModelException;

import java.util.Set;

/**
 * Facade
 */
public class Facade extends hillbillies.part1.facade.Facade implements IFacade {
    @Override
    public World createWorld(int[][][] terrainTypes, TerrainChangeListener modelListener) throws ModelException {
        return new World(terrainTypes, modelListener);
    }

    @Override
    public int getNbCubesX(World world) throws ModelException {
        return world.getTerrain().getMaxX();
    }

    @Override
    public int getNbCubesY(World world) throws ModelException {
        return world.getTerrain().getMaxY();
    }

    @Override
    public int getNbCubesZ(World world) throws ModelException {
        return world.getTerrain().getMaxZ();
    }

    @Override
    public void advanceTime(World world, double dt) throws ModelException {
        try {
            world.advanceTime(dt);
        } catch (IllegalArgumentException ex) {
            throw new ModelException(ex.getMessage(), ex);
        }
    }

    @Override
    public int getCubeType(World world, int x, int y, int z) throws ModelException {
        try {
            return world.getTerrain().getCubeType(new IntVector(x, y, z)).getId();
        } catch (InvalidPositionException err) {
            throw new ModelException(err);
        }
    }

    @Override
    public void setCubeType(World world, int x, int y, int z, int value) throws ModelException {
        try {
            world.getTerrain().setCubeType(new IntVector(x, y, z), Terrain.Type.fromId(value));
        } catch (InvalidPositionException | InvalidCubeTypeException err) {
            throw new ModelException(err);
        }
    }

    @Override
    public boolean isSolidConnectedToBorder(World world, int x, int y, int z) throws ModelException {
        try {
            return world.getTerrain().isCubeConnected(new IntVector(x, y, z));
        } catch (InvalidPositionException err) {
            throw new ModelException(err);
        }
    }

    @Override
    public Unit spawnUnit(World world, boolean enableDefaultBehavior) throws ModelException {
        try {
            return world.spawnUnit(enableDefaultBehavior);
        } catch(Exception err) {
            throw new ModelException(err);
        }
    }

    @Override
    public void addUnit(Unit unit, World world) throws ModelException {
        try {
            world.addUnit(unit);
        } catch (InvalidPositionException e) {
            throw new ModelException(e.getMessage(), e);
        }
    }

    @Override
    public Set<Unit> getUnits(World world) throws ModelException {
        return world.getUnits();
    }

    @Override
    public boolean isCarryingLog(Unit unit) throws ModelException {
        return unit.isCarryingLog();
    }

    @Override
    public boolean isCarryingBoulder(Unit unit) throws ModelException {
        return unit.isCarryingBoulder();
    }

    @Override
    public boolean isAlive(Unit unit) throws ModelException {
        return unit.isAlive();
    }

    @Override
    public int getExperiencePoints(Unit unit) throws ModelException {
        return unit.getXp();
    }

    @Override
    public void workAt(Unit unit, int x, int y, int z) throws ModelException {
        try {
            unit.workAt(new IntVector(x, y, z));
        } catch (InvalidActionException | InvalidPositionException err) {
            throw new ModelException(err.getMessage(), err);
        }
    }

    @Override
    public Faction getFaction(Unit unit) throws ModelException {
        return unit.getFaction();
    }

    @Override
    public Set<Unit> getUnitsOfFaction(Faction faction) throws ModelException {
        return faction.getUnits();
    }

    @Override
    public Set<Faction> getActiveFactions(World world) throws ModelException {
        return world.getFactions();
    }

    @Override
    public double[] getPosition(Boulder boulder) throws ModelException {
        return boulder.getPosition().toDoubleArray();
    }

    @Override
    public Set<Boulder> getBoulders(World world) throws ModelException {
        return world.getBoulders();
    }

    @Override
    public double[] getPosition(Log log) throws ModelException {
        return log.getPosition().toDoubleArray();
    }

    @Override
    public Set<Log> getLogs(World world) throws ModelException {
        return world.getLogs();
    }

    @Override
    public void advanceTime(Unit unit, double dt) throws ModelException {
        Facade.super.advanceTime(unit, dt);
    }

    @Override
    public void work(Unit unit) throws ModelException {
        Facade.super.work(unit);
    }
}
