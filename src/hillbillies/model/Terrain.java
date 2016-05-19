package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;
import be.kuleuven.cs.som.annotate.Immutable;
import hillbillies.model.exceptions.InvalidCubeTypeException;
import hillbillies.model.exceptions.InvalidPositionException;
import hillbillies.model.vector.IntVector;
import hillbillies.part2.listener.TerrainChangeListener;
import hillbillies.util.ConnectedToBorder;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Class managing terrain and cubes of a World.
 *
 * @invar The TerrainChangeListener must be effective.
 * @invar The world must be effective.
 */
public class Terrain {

    /**
     * Class representing a cube in the world.
     */
    private static class Cube {
        Cube(Type type) {
            this.type = type;
            this.gameObjects = new HashSet<>();
        }
        public Type type;

        /**
         * Set of all GameObject laying on this cube.
         * @invar The set must be effective.
         * @invar Each GameObject must be effective.
         * @invar Each GameObjects position must be this cube.
         */
        final Set<GameObject> gameObjects;
    }

    /**
     * Enum representing cube types.
     */
    public enum Type {
        AIR(0),
        ROCK(1),
        TREE(2),
        WORKSHOP(3);

        private final int id;
        Type(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public static Type fromId(int id) throws InvalidCubeTypeException {
            for (Type type : Type.values()) {
                if (type.getId() == id)
                    return type;
            }
            throw new InvalidCubeTypeException(id);
        }
    }

    //<editor-fold desc="Constants">
    public static final double Lc = 1.0;
    //</editor-fold>

    //<editor-fold desc="Variables">
    private final int maxX;
    private final int maxY;
    private final int maxZ;

    /**
     * A 3 dimensional array of all cubes in the world.
     *
     * @invar   The array must be effective.
     * @invar   Each cube must be effective.
     */
    private final Cube[][][] cubes;

    private final TerrainChangeListener updateListener;

    private final ConnectedToBorder connectedToBorder;

    private final World world;
    //</editor-fold>

    //<editor-fold desc="Constructor">

    /**
     * Constructs a new terrain.
     *
     * @param   world
     *          The world this terrain belongs to.
     * @param   maxX
     *          The size of the world in the X direction.
     * @param   maxY
     *          The size of the world in the Y direction.
     * @param   maxZ
     *          The size of the world in the Z direction.
     *
     * @param   modelListener
     *          The listener for terrain changes.
     *
     * @post    The getMaxX(), getMaxY() and getMaxZ() functions will return the size of the terrain.
     *
     * @throws  IllegalArgumentException
     *          If the modelListener isn't effective.
     * @throws  InvalidCubeTypeException
     *          If any of the supplied terrainTypes are invalid.
     */
    public Terrain(World world, int maxX, int maxY, int maxZ, TerrainChangeListener modelListener)
            throws IllegalArgumentException {

        if (modelListener == null)
            throw new IllegalArgumentException("no model listener given");
        this.updateListener = modelListener;
        this.world = world;

        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;

        this.cubes = new Cube[maxX][maxY][maxZ];

        connectedToBorder = new ConnectedToBorder(maxX, maxY, maxZ);

    }

    /**
     * Sets the terrain types.
     *
     * @param   terrainTypes
     *          A list with the types of all cubes.
     *
     * @post    A new cube is created for each position in the terrain, the type is set to the supplied type.
     * @post    Any cube that isn't connected to the border will cave in at start.
     *
     * @throws  IllegalArgumentException
     *          | if the terrainTypes isn't effective or the size doesn't match.
     */
    public void setTerrain(int[][][] terrainTypes) throws IllegalArgumentException, InvalidCubeTypeException {
        if (terrainTypes == null || terrainTypes.length != maxX
                || terrainTypes[0].length != maxY || terrainTypes[0][0].length != maxZ)
            throw new IllegalArgumentException("invalid terrainTypes given");

        Set<IntVector> startCaveIn = new HashSet<>();

        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                for (int z = 0; z < maxZ; z++) {
                    Type type = Type.fromId(terrainTypes[x][y][z]);
                    this.cubes[x][y][z] = new Cube(type);
                    if (type == Type.WORKSHOP)
                        this.getWorld().addWorkshop(new IntVector(x, y, z));
                    if (!isSolid(type))
                        startCaveIn.addAll(connectedToBorder.changeSolidToPassable(x, y, z).stream().map(IntVector::new).collect(Collectors.toSet()));
                }
            }
        }

        startCaveIn.forEach(this::breakCube);
    }
    //</editor-fold>

    /**
     * Checks whether the given position is valid in the world.
     *
     * @param   pos
     *          The position to be checked
     *
     * @return  result is true if the position is within world bounds.
     */
    public boolean isValidPosition(IntVector pos) {
        return pos != null && pos.getX() >= 0 && pos.getX() < maxX && pos.getY() >= 0
                && pos.getY() < maxY && pos.getZ() >= 0 && pos.getZ() < maxZ;
    }

    //<editor-fold desc="Getters">
    /**
     * Returns the world this terrain belongs to.
     */
    @Basic @Immutable
    private World getWorld() {
        return world;
    }

    /**
     * Returns the number of cubes in the X direction.
     */
    @Basic @Immutable
    public int getMaxX() {
        return this.maxX;
    }

    /**
     * Returns the number of cubes in the Y direction.
     */
    @Basic @Immutable
    public int getMaxY() {
        return this.maxY;
    }

    /**
     * Returns the number of cubes in the Z direction.
     */
    @Basic @Immutable
    public int getMaxZ() {
        return this.maxZ;
    }
    //</editor-fold>

    //<editor-fold desc="Cubes">
    /**
     * Checks whether the given cube type is solid.
     *
     * @param   type
     *          The type to be checked.
     *
     * @return  true if the type is solid.
     */
    public static boolean isSolid(Type type) {
        return type == Type.ROCK || type == Type.TREE;
    }

    /**
     * Returns a cube object for the given location.
     *
     * @param   cubeLoc
     *          The location
     *
     * @return  The cube at the given location.
     *
     * @throws  InvalidPositionException
     *          Throws if the given position is invalid.
     */
    private Cube getCube(IntVector cubeLoc) throws InvalidPositionException {
        if (!isValidPosition(cubeLoc))
            throw new InvalidPositionException(cubeLoc);
        return cubes[cubeLoc.getX()][cubeLoc.getY()][cubeLoc.getZ()];
    }

    /**
     * Checks whether the cube at the given position has a path to the border.
     *
     * @param   pos
     *          The position of the cube to be checked.
     *
     * @return  True if the cube is connected to the border.
     *
     * @throws  InvalidPositionException
     *          Throws if the given position is invalid.
     */
    public boolean isCubeConnected(IntVector pos) throws InvalidPositionException {
        if (!isValidPosition(pos))
            throw new InvalidPositionException(pos);
        return connectedToBorder.isSolidConnectedToBorder(pos.getX(), pos.getY(), pos.getZ());
    }


    /**
     * Gets the type of the cube at the given position.
     *
     * @param   cube
     *          The location of the cube.
     *
     * @return  An integer representing the type of the cube.
     *
     * @throws  InvalidPositionException
     *          If the given position is invalid.
     */
    public Type getCubeType(IntVector cube) throws InvalidPositionException {
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
     *          any cubes that aren't connected to the border anymore will cave in.
     *          When cubes cave in they may drop a boulder or a log.
     *
     * @effect  If the type is a workshop, the workshop is added to the world.
     *
     * @throws  InvalidPositionException
     *          If the given position is invalid.
     */
    public void setCubeType(IntVector pos, Type type) throws InvalidPositionException {
        if (!isValidPosition(pos))
            throw new InvalidPositionException(pos);

        if (isSolid(getCubeType(pos)) && !isSolid(type)) {
            for (int[] cord : connectedToBorder.changeSolidToPassable(pos.getX(), pos.getY(), pos.getZ())) {
                breakCube(new IntVector(cord));
                updateListener.notifyTerrainChanged(cord[0], cord[1], cord[2]);
            }
        }

        if (type == Type.WORKSHOP)
            this.getWorld().addWorkshop(pos);

        cubes[pos.getX()][pos.getY()][pos.getZ()].type = type;
        updateListener.notifyTerrainChanged(pos.getX(), pos.getY(), pos.getZ());
    }

    /**
     * Break a cube at the given position.
     *
     * @param   location
     *          The location of the cube.
     *
     * @effect  Sets the CubeType to air.
     *          | this.setCubeType(location, Type.Air)
     *
     * @effect  There is a chance to drop an boulder or log.
     *          | this.getWorld().dropChance(location, this.getCubeType(location))
     */
    public void breakCube(IntVector location) throws InvalidPositionException {
        Type type = getCubeType(location);
        setCubeType(location, Type.AIR);
        this.getWorld().dropChance(location, type);
    }

    /**
     * Removes the given object from its cube.
     *
     * @param   object
     *          The object to be removed.
     *
     * @post    The cube where the object was positioned won't contain the object anymore.
     */
    public void removeObjectFromCube(GameObject object) {
        getCube(object.getPosition().toIntVector()).gameObjects.remove(object);
    }

    /**
     * Adds a gameObject to its cube.
     *
     * @param   object
     *          The gameObject to add to its cube.
     *
     * @post    The cube given by the gameObjects position will now contain the gameObject.
     */
    public void addObjectToCube(GameObject object) {
        getCube(object.getPosition().toIntVector()).gameObjects.add(object);
    }

    /**
     * Returns all the logs at the given location.
     *
     * @param   cubeLoc
     *          The location of the cube.
     *
     * @throws  InvalidPositionException
     *          If the given position is not valid.
     */
    public Set<Log> getLogs(IntVector cubeLoc) throws InvalidPositionException {
        Cube cube = getCube(cubeLoc);
        return cube.gameObjects.stream().filter(o -> o instanceof Log)
                .map(Log.class::cast).collect(Collectors.toSet());
    }

    /**
     * Returns all the boulders at the given location.
     *
     * @param   cubeLoc
     *          The location of the cube.
     *
     * @throws  InvalidPositionException
     *          If the given position is not valid.
     */
    public Set<Boulder> getBoulders(IntVector cubeLoc) throws InvalidPositionException {
        Cube cube = getCube(cubeLoc);
        return cube.gameObjects.stream().filter(o -> o instanceof Boulder)
                .map(Boulder.class::cast).collect(Collectors.toSet());
    }
    //</editor-fold>

    //<editor-fold desc="Neighbours">

    /**
     * All neighbouring offsets of a cube.
     */
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
     * Returns a stream of all neighbours of the given position.
     *
     * @param   pos
     *          The position to get all neighbours from.
     *
     * @return  A stream obtained by adding each offset [-1, 0, 1] to the position.
     */
    public static Stream<IntVector> getNeighbours(IntVector pos) {
        return Arrays.stream(neighbourOffsets).map(offset -> pos.add(offset[0], offset[1], offset[2]));
    }

    /**
     * Returns a stream of all neighbours of the given position.
     *
     * @param   pos
     *          The position to get all neighbours from.
     *
     * @return  A stream gotten by adding each offset [-1, 0, 1] to the position without any diagonal positions.
     */
    public static Stream<IntVector> getDirectlyAdjacent(IntVector pos) {
        return Arrays.stream(neighbourOffsets).limit(6).map(offset -> pos.add(offset[0], offset[1], offset[2]));
    }
    //</editor-fold>
}
