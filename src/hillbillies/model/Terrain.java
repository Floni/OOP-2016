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
 * Class managing terrain and cubes.
 */
public class Terrain {
    /**
     * Class representing a cube in the world.
     */
    private static class Cube {
        Cube(int type) {
            this.type = type;
            this.gameObjects = new HashSet<>();
        }
        public int type;

        /**
         * Set of all GameObject laying on this cube.
         * @invar The set must be effective.
         * @invar Each GameObject must be effective.
         * @invar Each GameObjects position must be this cube.
         */
        final Set<GameObject> gameObjects;
    }

    public static final int AIR = 0;
    public static final int WORKSHOP = 3;
    public static final int ROCK = 1;
    public static final int TREE = 2;

    public static final double Lc = 1.0;

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


    public Terrain(World world, int[][][] terrainTypes, TerrainChangeListener modelListener) throws IllegalArgumentException {
        if (terrainTypes == null|| modelListener == null)
            throw new IllegalArgumentException("no terrain types given");
        this.updateListener = modelListener;
        this.world = world;

        this.maxX = terrainTypes.length;
        this.maxY = terrainTypes[0].length;
        this.maxZ = terrainTypes[0][0].length;

        this.cubes = new Cube[maxX][maxY][maxZ];

        connectedToBorder = new ConnectedToBorder(maxX, maxY, maxZ);
        Set<IntVector> startCaveIn = new HashSet<>();

        for (int x = 0; x < maxX; x++) {
            for (int y = 0; y < maxY; y++) {
                for (int z = 0; z < maxZ; z++) {
                    this.cubes[x][y][z] = new Cube(terrainTypes[x][y][z]);
                    if (terrainTypes[x][y][z] == WORKSHOP)
                        this.getWorld().addWorkShop(new IntVector(x, y, z));
                    if (!isSolid(terrainTypes[x][y][z]))
                        startCaveIn.addAll(connectedToBorder.changeSolidToPassable(x, y, z).stream().map(IntVector::new).collect(Collectors.toSet()));
                }
            }
        }

        startCaveIn.forEach(this::breakCube);
    }

    /**
     * Checks whether the given position is valid in the world.
     *
     * @param   pos
     *          The position to be checked
     * @return  result is true if the position is within world bounds.
     */
    public boolean isValidPosition(IntVector pos) {
        return pos != null && pos.getX() >= 0 && pos.getX() < maxX && pos.getY() >= 0
                && pos.getY() < maxY && pos.getZ() >= 0 && pos.getZ() < maxZ;
    }

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


    private World getWorld() {
        return world;
    }

    @Basic @Immutable
    public int getMaxX() {
        return this.maxX;
    }

    @Basic @Immutable
    public int getMaxY() {
        return this.maxY;
    }

    @Basic @Immutable
    public int getMaxZ() {
        return this.maxZ;
    }

    /**
     * Returns a cube object for the given location.
     *
     * @param   cubeLoc
     *          The location
     * @return  The cube at the given location
     * @throws InvalidPositionException
     *          If the given position is invalid.
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
     * @return  true of the cube is connected to the border.
     * @throws  InvalidPositionException
     *          If the given position is invalid.
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
     * @return  An integer representing the type of the cube.
     * @throws  InvalidPositionException
     *          If the given position is invalid
     */
    public int getCubeType(IntVector cube) throws InvalidPositionException {
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
     * @throws  InvalidPositionException
     *          If the given position is invalid.
     */
    public void setCubeType(IntVector pos, int type) throws InvalidPositionException {
        if (!isValidPosition(pos))
            throw new InvalidPositionException(pos);
        if (!isValidCubeType(type))
            throw new InvalidCubeTypeException(type);

        if (isSolid(getCubeType(pos)) && !isSolid(type)) {
            for (int[] cord : connectedToBorder.changeSolidToPassable(pos.getX(), pos.getY(), pos.getZ())) {
                breakCube(new IntVector(cord));
                updateListener.notifyTerrainChanged(cord[0], cord[1], cord[2]);
            }
        }

        if (type == WORKSHOP)
            this.getWorld().addWorkShop(pos);

        cubes[pos.getX()][pos.getY()][pos.getZ()].type = type;
        updateListener.notifyTerrainChanged(pos.getX(), pos.getY(), pos.getZ());
    }

    private boolean isValidCubeType(int type) {
        return type >= 0 && type < 4;
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
        this.getWorld().dropChance(location, type);
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

    /**
     * Returns all the logs at the given location.
     * @param   cubeLoc
     *          The location of the cube.
     * @throws  InvalidPositionException
     *          If the given position is not valid.
     */
    @Basic
    public Set<Log> getLogs(IntVector cubeLoc) throws InvalidPositionException {
        Cube cube = getCube(cubeLoc);
        return cube.gameObjects.stream().filter(o -> o instanceof Log)
                .map(Log.class::cast).collect(Collectors.toSet());
    }

    /**
     * Returns all the boulders at the given location.
     * @param   cubeLoc
     *          The location of the cube.
     * @throws  InvalidPositionException
     *          If the given position is not valid.
     */
    @Basic
    public Set<Boulder> getBoulders(IntVector cubeLoc) throws InvalidPositionException {
        Cube cube = getCube(cubeLoc);
        return cube.gameObjects.stream().filter(o -> o instanceof Boulder)
                .map(Boulder.class::cast).collect(Collectors.toSet());
    }

    //<editor-fold desc="Neighbours">
    // TODO: move to some class? terrain or util?
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
