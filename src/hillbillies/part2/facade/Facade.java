package hillbillies.part2.facade;

import com.sun.javafx.sg.prism.NGShape;
import com.sun.org.apache.xpath.internal.operations.Mod;
import hillbillies.model.*;
// import hillbillies.part1.facade.Facade;
import hillbillies.part2.listener.TerrainChangeListener;
import ogp.framework.util.ModelException;

import java.util.Set;

/**
 * Created by timo on 3/14/16.
 */
public class Facade extends hillbillies.part1.facade.Facade implements IFacade {
    @Override
    public World createWorld(int[][][] terrainTypes, TerrainChangeListener modelListener) throws ModelException {
        return new World(terrainTypes, modelListener);
    }

    @Override
    public int getNbCubesX(World world) throws ModelException {
        return world.X_MAX;
    }

    @Override
    public int getNbCubesY(World world) throws ModelException {
        return world.Y_MAX;
    }

    @Override
    public int getNbCubesZ(World world) throws ModelException {
        return world.Z_MAX;
    }

    @Override
    public void advanceTime(World world, double dt) throws ModelException {
        try {
            world.advanceTime(dt);
        } catch (Exception ex) {
            throw new ModelException(ex.getMessage(), ex);
        }
    }

    @Override
    public int getCubeType(World world, int x, int y, int z) throws ModelException {
        return world.getCubeType(x, y, z);
    }

    @Override
    public void setCubeType(World world, int x, int y, int z, int value) throws ModelException {
        world.setCubeType(x, y, z, value);
    }

    @Override
    public boolean isSolidConnectedToBorder(World world, int x, int y, int z) throws ModelException {
        return world.isCubeConnected(x, y, z);
    }

    @Override
    public Unit spawnUnit(World world, boolean enableDefaultBehavior) throws ModelException {
        return world.spawnUnit(enableDefaultBehavior);
    }

    @Override
    public void addUnit(Unit unit, World world) throws ModelException {
        world.addUnit(unit);
    }

    @Override
    public Set<Unit> getUnits(World world) throws ModelException {
        return world.getUnits();
    }

    @Override
    public boolean isCarryingLog(Unit unit) throws ModelException {
        return false;
    }

    @Override
    public boolean isCarryingBoulder(Unit unit) throws ModelException {
        return false;
    }

    @Override
    public boolean isAlive(Unit unit) throws ModelException {
        return true;
    }

    @Override
    public int getExperiencePoints(Unit unit) throws ModelException {
        return unit.getXp();
    }

    @Override
    public void workAt(Unit unit, int x, int y, int z) throws ModelException {

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
