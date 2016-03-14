package hillbillies.part2.facade;

import com.sun.javafx.sg.prism.NGShape;
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
        return null;
    }

    @Override
    public int getNbCubesX(World world) throws ModelException {
        return 0;
    }

    @Override
    public int getNbCubesY(World world) throws ModelException {
        return 0;
    }

    @Override
    public int getNbCubesZ(World world) throws ModelException {
        return 0;
    }

    @Override
    public void advanceTime(World world, double dt) throws ModelException {

    }

    @Override
    public int getCubeType(World world, int x, int y, int z) throws ModelException {
        return 0;
    }

    @Override
    public void setCubeType(World world, int x, int y, int z, int value) throws ModelException {

    }

    @Override
    public boolean isSolidConnectedToBorder(World world, int x, int y, int z) throws ModelException {
        return false;
    }

    @Override
    public Unit spawnUnit(World world, boolean enableDefaultBehavior) throws ModelException {
        return null;
    }

    @Override
    public void addUnit(Unit unit, World world) throws ModelException {

    }

    @Override
    public Set<Unit> getUnits(World world) throws ModelException {
        return null;
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
        return false;
    }

    @Override
    public int getExperiencePoints(Unit unit) throws ModelException {
        return 0;
    }

    @Override
    public void workAt(Unit unit, int x, int y, int z) throws ModelException {

    }

    @Override
    public Faction getFaction(Unit unit) throws ModelException {
        return null;
    }

    @Override
    public Set<Unit> getUnitsOfFaction(Faction faction) throws ModelException {
        return null;
    }

    @Override
    public Set<Faction> getActiveFactions(World world) throws ModelException {
        return null;
    }

    @Override
    public double[] getPosition(Boulder boulder) throws ModelException {
        return new double[0];
    }

    @Override
    public Set<Boulder> getBoulders(World world) throws ModelException {
        return null;
    }

    @Override
    public double[] getPosition(Log log) throws ModelException {
        return new double[0];
    }

    @Override
    public Set<Log> getLogs(World world) throws ModelException {
        return null;
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
