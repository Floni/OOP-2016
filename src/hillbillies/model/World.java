package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import be.kuleuven.cs.som.annotate.Model;
import hillbillies.model.exceptions.InvalidCubeTypeException;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.unit.Unit;
import hillbillies.model.util.PathFinder;
import hillbillies.model.vector.IntVector;
import hillbillies.part2.listener.TerrainChangeListener;
import hillbillies.util.ConnectedToBorder;

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// TODO: check everything

/**
 * Class representing a world.
 *
 * @invar The number of units must be less than MAX_UNITS.
 * @invar The number o factions must be less than MAX_FACTIONS
 *
 */
public class World {
    //<editor-fold desc="Constants">
    public static final double POS_EPS = 1e-3;

    private static final int MAX_UNITS = 100;
    private static final int MAX_FACTIONS = 5;
    //</editor-fold>

    //<editor-fold desc="Variables">
    private final Terrain terrain;

    private int totalUnits;
    /**
     * The set of factions of this world.
     * @invar   The set must be effective.
     * @invar   Each faction in the set must be effective.
     * @invar   The size of the set must be less than MAX_FACTIONS.
     * @invar   Each factions size must be less than Faction.MAX_UNITS.
     * @invar   The sum of the size of all factions must be less than MAX_UNITS.
     */
    private final Set<Faction> factions;
    /**
     * The set of all GameObjects (Log & Boulder) in the world.
     *
     * @invar   The set must be effective.
     * @invar   Each GameObject must be effective.
     */
    private final Set<GameObject> gameObjects;
    /**
     * Set of the position of all workshops in the world.
     *
     * @invar The set must be effective.
     * @invar Each position in the set must be effective
     *          and the type of the cube at that position must have type WORKSHOP.
     */
    private final Set<IntVector> workshops;

    private final PathFinder<IntVector> pathFinder;
    //</editor-fold>

    //<editor-fold desc="Constructor">
    /**
     * Creates a new world.
     *
     * @param   terrainTypes
     *          An array of integers specifying the terrain types.
     * @param   modelListener
     *          A listener for terrain changes.
     *
     * @post    The factions, units, logs and boulders are empty.
     *
     * @post    The getTerrain() function will return a newly created terrain using the supplied terrainTypes.
     *          | this.getTerrain() == (new Terrain(this, terrainTypes, modelListener))
     */
    public World(int[][][] terrainTypes, TerrainChangeListener modelListener) throws IllegalArgumentException {
        this.factions = new HashSet<>();
        this.gameObjects = new HashSet<>();
        this.workshops = new HashSet<>();

        this.terrain = new Terrain(this, terrainTypes, modelListener);

        this.pathFinder = new PathFinder<>(new PathFinder.PathGlue<IntVector>() {
            @Override
            public Stream<IntVector> getNeighbours(IntVector pos) {
                return Terrain.getNeighbours(pos).filter(n -> Unit.isValidPosition(World.this, n) && Unit.isStablePosition(World.this, n));
            }

            @Override
            public double getCost(IntVector a, IntVector b) {
                return a.subtract(b).norm();
            }

            @Override
            public int getHeuristic(IntVector a, IntVector b) {
                IntVector diff = a.subtract(b);
                return Math.abs(diff.getX()) + Math.abs(diff.getY()) + Math.abs(diff.getZ());
            }
        });
    }
    //</editor-fold>

    //<editor-fold desc="advanceTime">
    /**
     * Calls advanceTime on all Unit, Boulders and logs.
     *
     * @param   dt
     *          The passed time between the last call to advanceTime
     * @throws  IllegalArgumentException
     *          If the dt is less than 0.2 or smaller than 0.
     */
    public void advanceTime(double dt) throws IllegalArgumentException {
        if (dt >= 0.2 || dt < 0)
            throw new IllegalArgumentException("invalid dt");

        // call advanceTime on all units
        getUnits().stream().filter(Unit::isAlive).forEach(unit -> unit.advanceTime(dt));

        this.gameObjects.forEach(o -> o.advanceTime(dt));
    }
    //</editor-fold>

    //<editor-fold desc="Getters">
    /**
     * Returns the pathfinder of this world.
     */
    @Basic @Immutable
    public PathFinder<IntVector> getPathFinder() {
        return pathFinder;
    }

    /**
     * Returns the terrain of this world.
     */
    @Basic @Immutable
    public Terrain getTerrain() {
        return terrain;
    }
    //</editor-fold>

    //<editor-fold desc="Logs and Boulders">
    /**
     * Returns a stream of all locations where a workshop is.
     *
     * @return  A stream of positions of all workshops in the world.
     *          | result.allMatch(p -> getCube(p) == WORKSHOP)
     */
    public Stream<IntVector> getAllWorkshops() {
        return workshops.stream();
    }

    /**
     * Adds a workshop to the world.
     *
     * @param   position
     *          The position of the workshop to add.
     *
     * @post    The workshop will be contained in the world.
     */
    public void addWorkShop(IntVector position) {
        this.workshops.add(position);
    }

    /**
     * Returns all the logs in the world.
     */
    @Basic
    public  Set<Log> getLogs() {
        return this.gameObjects.stream().filter(o -> o instanceof Log)
                .map(Log.class::cast).collect(Collectors.toSet());
    }

    /**
     * Returns all the boulders in the world.
     */
    @Basic
    public Set<Boulder> getBoulders() {
        return this.gameObjects.stream().filter(o -> o instanceof Boulder)
                .map(Boulder.class::cast).collect(Collectors.toSet());
    }

    /**
     *  Adds the given gameObject to the world at the given position.
     *
     * @param   gameObject
     *          The gameObject.
     *
     * @post    The position of the gameObject is set to the given cube.
     * @post    The world contains the gameObject and the cube at the given locations will contain the gameObject.
     */
    public void addGameObject(GameObject gameObject) throws InvalidPositionException {
        this.gameObjects.add(gameObject);
        this.getTerrain().addCubeObject(gameObject);
    }

    /**
     * Drops an GameObject at the given position with a chance of 0.25.
     *
     * @param   location
     *          The location to drop.
     * @param   type
     *          Determines the type of object to drop.
     *
     * @post    If a drop occurs a boulder or log will be added at the given position.
     */
    public void dropChance(IntVector location, Terrain.Type type) {
        if (Math.random() < 0.25) {
            if (type == Terrain.Type.ROCK) {
                this.addGameObject(new Boulder(this, location));
            } else if (type == Terrain.Type.TREE) {
                this.addGameObject(new Log(this, location));
            }
        }
    }

    /**
     * Removes one Log from the cube at the given position.
     *
     * @param cubeLoc The position of the cube.
     * @post If the cube at the given position has Logs, one is removed.
     */
    public void consumeLog(IntVector cubeLoc) {
        Set<Log> cubeLogs = this.getTerrain().getLogs(cubeLoc);
        if (cubeLogs.size() >= 1)
            removeGameObject(cubeLogs.iterator().next());
    }

    /**
     * Removes one Boulder from the cube at the given position.
     *
     * @param   cubeLoc
     *          The position of the cube.
     *
     * @post    If the cube at the given position has Boulders, one is removed.
     */
    public void consumeBoulder(IntVector cubeLoc) {
        Set<Boulder> boulders = this.getTerrain().getBoulders(cubeLoc);
        if (boulders.size() >= 1)
            removeGameObject(boulders.iterator().next());
    }

    /**
     * Removes a gameObject from the world.
     *
     * @param   object
     *          The gameObject to be removed
     *
     * @post    The gameObject is removed from the world.
     * @effect  The gameObject is removed from its cube.
     *          | removeCubeObject(object)
     */
    public void removeGameObject (GameObject object) {
        this.getTerrain().removeCubeObject(object);
        gameObjects.remove(object);
    }
    //</editor-fold>

    //<editor-fold desc="Factions">

    /**
     * Returns all the active factions of the world.
     */
    @Basic
    public Set<Faction> getFactions() {
        return new HashSet<>(this.factions);
    }

    /**
     * Adds a new faction to the world.
     * @return  The new faction
     *
     * @pre     The number of active factions must be less than World.MAX_FACTIONS
     */
    private Faction addFaction() {
        assert factions.size() < MAX_FACTIONS;

        Faction faction = new Faction();
        factions.add(faction);
        return faction;

    }
    //</editor-fold>

    //<editor-fold desc="Units">
    /**
     *  Returns a random number between 25 and 100 inclusive.
     */
    @Basic @Model
    private int getRandomAttribute() {
        return (int)Math.floor(25 + 76*Math.random());
    }

    /**
     * Spawns an new unit.
     *
     * @param   defaultBehaviour
     *          Determines whether or not the default behaviour of the unit will be enabled.
     * @return  If the number of units exceeds MAX_UNITS
     *          then a dead unit is returned.
     *          Otherwise a new unit with a random position above a solid position with random attributes (see getRandomAttribute).
     *          The new unit will have its default behaviour activated depending on the defaultBehaviour argument.
     * @effect  The unit is added to the world
     *          | this.addUnit(result)
     */
    public Unit spawnUnit(boolean defaultBehaviour) {
        IntVector randPos;
        do {
            randPos = new IntVector(Math.random()*this.getTerrain().getMaxX(),
                    Math.random()*this.getTerrain().getMaxY(),
                    Math.random()*this.getTerrain().getMaxZ());
        } while (!this.getTerrain().isValidPosition(randPos) // must be within world bounds
                || (randPos.getZ() != 0 && !Terrain.isSolid(this.getTerrain().getCubeType(randPos.add(0, 0, -1)))) // floor must be solid.
                || Terrain.isSolid(this.getTerrain().getCubeType(randPos))); // the randPos can't be solid.

        // shouldn't throw because name is valid & position is valid.
        Unit unit = new Unit("Spawn", randPos.getX(), randPos.getY(), randPos.getZ(), getRandomAttribute(), getRandomAttribute(),
                getRandomAttribute(), getRandomAttribute());
        if (defaultBehaviour)
            unit.startDefaultBehaviour();

        if (totalUnits >= MAX_UNITS)
            unit.terminate();
        addUnit(unit);

        return unit;
    }

    /**
     * Adds an unit to the world.
     *
     * @param   unit
     *          The unit to be added to the world.
     *
     * @post    If the amount of active units exceeds MAX_UNITS, the function does nothing.
     *          If not, the unit will be assigned a faction.
     *          Either a new faction will be created for the unit if the number of active factions is less than MAX_FACTIONS
     *          or the unit is added to the faction with the least amount of units.
     *
     * @effect  The world of the unit is set to this world
     *          | unit.setWorld(this)
     */
    public void addUnit(Unit unit) {
        if (totalUnits >= MAX_UNITS)
            return;

        unit.setWorld(this);
        if (factions.size() < MAX_FACTIONS) {
            Faction newFaction = addFaction();
            newFaction.addUnit(unit);
        } else {
            Faction minFaction = factions.stream().filter(f -> f.getFactionSize() < Faction.MAX_UNITS)
                    .min((f1, f2) -> Integer.compare(f1.getFactionSize(), f2.getFactionSize())).orElse(null);
            if (minFaction != null) {
                minFaction.addUnit(unit);
            }
        }
        totalUnits += 1;
    }

    /**
     * Returns a set of all the units in the world.
     *
     * @return  A set which is the union of all units from all factions.
     */
    public Set<Unit> getUnits() {
        return factions.stream().collect(HashSet<Unit>::new, (s, f) -> s.addAll(f.getUnits()), AbstractCollection::addAll);
    }

    /**
     * Removes the given unit from the world.
     *
     * @param   unit
     *          The unit to be removed.
     * @post    The unit is removed from its faction and if the faction is empty, the faction is removed from the world.
     */
    public void removeUnit (Unit unit) {
        Faction unitFac = unit.getFaction();
        unitFac.removeUnit(unit);
        if (unitFac.getFactionSize() <= 0)
            factions.remove(unitFac); //TODO: destructor for faction + scheduler
    }
    //</editor-fold>
}
