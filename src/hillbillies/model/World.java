package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Model;
import hillbillies.model.Unit.Unit;
import hillbillies.model.Vector.IntVector;
import hillbillies.part2.listener.TerrainChangeListener;
import hillbillies.util.ConnectedToBorder;
import ogp.framework.util.ModelException;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by timo on 3/14/16.
 *
 */
public class World {

    private static class Cube {
        Cube(int type) {
            this.type = type;
            this.gameObjects = new HashSet<>();
        }
        public int type;
        Set<GameObject> gameObjects;

    }


    //<editor-fold desc="Constants">
    public static final int AIR = 0;
    public static final int WORKSHOP = 3;
    public static final int ROCK = 1;
    public static final int TREE = 2;

    public static final double Lc = 1.0;
    private static final int MAX_UNITS = 100;
    private static final int MAX_FACTION_SIZE = 50;
    private static final int MAX_FACTIONS = 5;
    //</editor-fold>

    //<editor-fold desc="Variables">
    public final int X_MAX;
    public final int Y_MAX;
    public final int Z_MAX;

    private final TerrainChangeListener updateListener;

    private Cube[][][] cubes;

    private int totalUnits;
    private Set<Faction> factions;
    private Set<GameObject> gameObjects;

    private ConnectedToBorder connectedToBorder;
    //</editor-fold>

    //<editor-fold desc="Constructor">

    /**
     * Creates a new world
     *
     * @post    The X_MAX, Y_MAX and Z_MAX variables are set to the size of the terrain.
     * @post    The cube types are set for each cube and checked if they are connected to the border,
     *          if not they cave in.
     *
     * @post    The factions, units, logs and boulders are empty.
     *
     * @param   terrainTypes
     *          An array of integers specifying the terrain types.
     * @param   modelListener
     *          A listener for terrain changes.
     */
    public World(int[][][] terrainTypes, TerrainChangeListener modelListener) {
        this.updateListener = modelListener;

        this.X_MAX = terrainTypes.length;
        this.Y_MAX = terrainTypes[0].length;
        this.Z_MAX = terrainTypes[0][0].length;

        this.cubes = new Cube[X_MAX][Y_MAX][Z_MAX];

        connectedToBorder = new ConnectedToBorder(X_MAX, Y_MAX, Z_MAX);

        this.factions = new HashSet<>();
        this.gameObjects = new HashSet<>();

        Set<IntVector> startCaveIn = new HashSet<>();

        for (int x = 0; x < X_MAX; x++) {
            for (int y = 0; y < Y_MAX; y++) {
                for (int z = 0; z < Z_MAX; z++) {
                    this.cubes[x][y][z] = new Cube(terrainTypes[x][y][z]);
                    if (!isSolid(terrainTypes[x][y][z]))
                        startCaveIn.addAll(connectedToBorder.changeSolidToPassable(x, y, z).stream().map(IntVector::new).collect(Collectors.toSet()));
                }
            }
        }

        startCaveIn.forEach(this::breakCube);

    }
    //</editor-fold>

    //<editor-fold desc="advanceTime">

    /**
     * Calls advanceTime on all Unit, Boulders and logs.
     *
     * @param   dt
     *          The passed time between the last call to advanceTime
     * @throws  ModelException
     *          If the dt is less than 0.2 or smaller than 0.
     */
    public void advanceTime(double dt) throws ModelException {
        if (dt >= 0.2 || dt < 0)
            throw new IllegalArgumentException("invalid dt");

        // call advanceTime on all units
        for (Unit unit : getUnits()) {
            unit.advanceTime(dt);
        }

        for (GameObject object : this.gameObjects) {
            object.advanceTime(dt);
        }
    }
    //</editor-fold>

    /**
     * Checks whether the given position is valid in the world.
     *
     * @param   pos
     *          The position to be checked
     * @return  result is true if the position is within world bounds.
     */
    public boolean isValidPosition(IntVector pos) {
        return pos.getX() >= 0 && pos.getX() < X_MAX && pos.getY() >= 0
                && pos.getY() < Y_MAX && pos.getZ() >= 0 && pos.getZ() < Z_MAX;
    }

    //<editor-fold desc="Cubes">

    /**
     * Checks whether the given cube type is solid.
     *
     * @param   type
     *          The type to be checked.
     * @return  true if the type is solid.
     */
    public static boolean isSolid(int type) {
        return type == ROCK || type == TREE;
    }

    /**
     * Returns a cube object for the given location.
     *
     * @param   cubeLoc
     *          The location
     * @return  The cube at the given location
     * @throws  IllegalArgumentException
     *          If the given position is invalid.
     */
    private Cube getCube(IntVector cubeLoc) throws IllegalArgumentException {
        if (!isValidPosition(cubeLoc))
            throw new IllegalArgumentException("invalid position");
        return cubes[cubeLoc.getX()][cubeLoc.getY()][cubeLoc.getZ()];
    }

    /**
     * Checks whether the cube at the given position has a path to the border.
     *
     * @param   pos
     *          The position of the cube to be checked.
     * @return  true of the cube is connected to the border.
     * @throws  IllegalArgumentException
     *          If the given position is invalid.
     */
    public boolean isCubeConnected(IntVector pos) throws IllegalArgumentException {
        if (!isValidPosition(pos))
            throw new IllegalArgumentException("invalid position");
        return connectedToBorder.isSolidConnectedToBorder(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Gets the type of the cube at the given position.
     *
     * @param   cube
     *          The location of the cube.
     * @return  An integer representing the type of the cube.
     * @throws  IllegalArgumentException
     *          If the given position is invalid
     */
    public int getCubeType(IntVector cube) throws IllegalArgumentException {
         return getCube(cube).type;
    }

    /**
     * Sets the type of the cube at the given position.
     *
     * @param   pos
     *          The position of the cube
     * @param   type
     *          The new type
     *
     * @post    The cube at position pos will have the given type and
     *          any cubes that aren't connected to the border anymore will cave in (include the given cube).
     *          When cubes cave in they may drop a boulder or a log.
     *
     * @throws  IllegalArgumentException
     *          If the given position is invalid.
     */
    public void setCubeType(IntVector pos, int type) throws IllegalArgumentException {
        if (!isValidPosition(pos))
            throw new IllegalArgumentException("invalid position");

        if (isSolid(getCubeType(pos)) && !isSolid(type)) {
            for (int[] coord : connectedToBorder.changeSolidToPassable(pos.getX(), pos.getY(), pos.getZ())) {
                breakCube(new IntVector(coord));
                updateListener.notifyTerrainChanged(coord[0], coord[1], coord[2]);
            }
        }
        cubes[pos.getX()][pos.getY()][pos.getZ()].type = type;
        updateListener.notifyTerrainChanged(pos.getX(), pos.getY(), pos.getZ());
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
    private void dropChance(IntVector location, int type) {
        if (Math.random() < 0.25) {
            if (type == World.ROCK) {
                addGameObject(location, new Boulder(this, location));
            } else if (type == World.TREE) {
                addGameObject(location, new Log(this, location));
            }
        }
    }

    /**
     * Break a cube at the given position.
     *
     * @param   location
     *          The location of the cube.
     *
     * @post    The type of the cube at the given location will be AIR.
     *
     * @effect  There is a chance to drop an boulder or log.
     *          | this.dropChance(location, this.getCubeType(location))
     */
    public void breakCube(IntVector location) {
        int type = getCubeType(location);
        setCubeType(location, AIR);
        dropChance(location, type);
    }
    //</editor-fold>

    //<editor-fold desc="Logs and Boulders">

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
     * Returns all the logs at the given location.
     * @param   cubeLoc
     *          The location of the cube.
     * @throws  IllegalArgumentException
     *          If the given position is not valid.
     */
    @Basic
    public Set<Log> getLogs(IntVector cubeLoc) throws IllegalArgumentException {
        Cube cube = getCube(cubeLoc);
        return cube.gameObjects.stream().filter(o -> o instanceof Log)
                .map(Log.class::cast).collect(Collectors.toSet());
    }

    /**
     * Returns all the boulders at the given location.
     * @param   cubeLoc
     *          The location of the cube.
     * @throws  IllegalArgumentException
     *          If the given position is not valid.
     */
    @Basic
    public Set<Boulder> getBoulders(IntVector cubeLoc) throws IllegalArgumentException {
        Cube cube = getCube(cubeLoc);
        return cube.gameObjects.stream().filter(o -> o instanceof Boulder)
                .map(Boulder.class::cast).collect(Collectors.toSet());
    }

    /**
     *  Adds the given gameObject to the world at the given position.
     *
     * @param   cubeLoc
     *          The location of the gameObject.
     * @param   gameObject
     *          The gameObject.
     *
     * @post    The position of the gameObject is set to the given cube.
     * @post    The world contains the gameObject and the cube at the given locations will contain the gameObject.
     */
    public void addGameObject(IntVector cubeLoc, GameObject gameObject) throws IllegalArgumentException {
        gameObject.setPosition(cubeLoc.toVector().add(Lc/2));
        Cube cube = getCube(cubeLoc);
        gameObjects.add(gameObject);
        cube.gameObjects.add(gameObject);
    }

    /**
     * Removes one Log from the cube at the given position.
     *
     * @param   cubeLoc
     *          The position of the cube.
     *
     * @post    If the cube at the given position has Logs, one is removed.
     */
    public void consumeLog(IntVector cubeLoc) {
        Set<Log> cubeLogs = getLogs(cubeLoc);
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
        Set<Boulder> boulders = getBoulders(cubeLoc);
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
        removeCubeObject(object);
        gameObjects.remove(object);
    }

    /**
     * Removes the given object from its cube.
     *
     * @param   object
     *          The object to be removed
     * @post    The cube where the object was positioned won't contain the object anymore.
     */
    public void removeCubeObject(GameObject object) {
        getCube(object.getPosition().toIntVector()).gameObjects.remove(object);
    }

    /**
     * Adds an gameObject to its cube.
     *
     * @param   object
     *          The gameObject to add to its cube.
     * @post    The cube given by the gameObjects position will now contain the gameObject.
     */
    public void addCubeObject(GameObject object) {
        getCube(object.getPosition().toIntVector()).gameObjects.add(object);
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
            randPos = new IntVector(Math.random()*X_MAX,
                    Math.random()*Y_MAX,
                    Math.random()*Z_MAX);
        } while (!isValidPosition(randPos) || isSolid(getCubeType(randPos)) || (randPos.getZ() != 0 && !isSolid(getCubeType(randPos.add(0, 0, -1)))));

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
            Faction minFaction = factions.stream().filter(f -> f.getFactionSize() < MAX_FACTION_SIZE)
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
            factions.remove(unitFac);
    }
    //</editor-fold>

    //<editor-fold desc="Neighbours">
    private static final int[][] neighbourOffsets = new int[][] {

            { -1, 0, 0 },
            { +1, 0, 0 },
            { 0, -1, 0 },
            { 0, +1, 0 },
            { 0, 0, -1 },
            { 0, 0, +1 },

            { -1, -1, -1},
            { -1, -1, 0 },
            { -1, 0, -1 },
            { 0, -1, -1 },

            { +1, +1, +1},
            { +1, +1, 0 },
            { +1, 0, +1 },
            { 0, +1, +1 },

            { +1 , -1, +1},
            { +1 , +1, -1},
            { -1 , +1, +1},
            { -1 , -1, +1},
            { -1 , +1, -1},
            { +1 , -1, -1},

            { +1, -1, 0 },
            { 0, +1, -1 },
            { -1, 0, +1 },
            { +1, 0, -1 },
            { 0, -1, +1 },
            { -1, +1 , 0}

    };

    /**
     * Returns a stream of all neighbours to the given position.
     *
     * @param   pos
     *          The position to get all neighbours from.
     * @return  an stream gotten by adding each offset [-1, 0, 1] to the position.
     */
    public static Stream<IntVector> getNeighbours(IntVector pos) {
        return Arrays.stream(neighbourOffsets).map(offset -> pos.add(offset[0], offset[1], offset[2]));
    }

    /**
     * Returns a stream of all neighbours to the given position.
     *
     * @param   pos
     *          The position to get all neighbours from.
     * @return  an stream gotten by adding each offset [-1, 0, 1] to the position but no diagonal positions.
     */
    public static Stream<IntVector> getDirectlyAdjacent(IntVector pos) {
        return Arrays.stream(neighbourOffsets).limit(6).map(offset -> pos.add(offset[0], offset[1], offset[2]));
    }
    //</editor-fold>
}
