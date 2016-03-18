package hillbillies.model;

import hillbillies.model.Unit.Unit;
import hillbillies.model.Vector.IntVector;
import hillbillies.part2.listener.TerrainChangeListener;
import hillbillies.util.ConnectedToBorder;
import ogp.framework.util.ModelException;

import java.util.*;
import java.util.stream.Stream;

/**
 * Created by timo on 3/14/16.
 *
 */
public class World {
    private static class Cube {
        public Cube(int type) {
            this.type = type;
            this.gameObjects = new HashSet<>();
        }
        public int type;
        public Set<GameObject> gameObjects;
    }
    public static final int AIR = 0;
    public static final int WORKSHOP = 3;
    public static final int ROCK = 1;
    public static final int TREE = 2;

    public final int X_MAX;
    public final int Y_MAX;
    public final int Z_MAX;

    public final static double Lc = 1.0;
    public final static int MAX_UNITS = 100;
    public final static int MAX_FACTION_SIZE = 50;

    private final TerrainChangeListener updateListener;

    private Cube[][][] cubes;

    private int totalUnits;
    private Set<Faction> factions;
    private Set<Log> logs;
    private Set<Boulder> boulders;


    private ConnectedToBorder connectedToBorder;

    public World(int[][][] terrainTypes, TerrainChangeListener modelListener) {
        this.updateListener = modelListener;

        this.X_MAX = terrainTypes.length;
        this.Y_MAX = terrainTypes[0].length;
        this.Z_MAX = terrainTypes[0][0].length;

        this.cubes = new Cube[X_MAX][Y_MAX][Z_MAX];

        connectedToBorder = new ConnectedToBorder(X_MAX, Y_MAX, Z_MAX);

        for (int x = 0; x < X_MAX; x++) {
            for (int y = 0; y < Y_MAX; y++) {
                for (int z = 0; z < Z_MAX; z++) {
                    this.cubes[x][y][z] = new Cube(terrainTypes[x][y][z]);
                    if (!isSolid(terrainTypes[x][y][z]))
                        connectedToBorder.changeSolidToPassable(x, y, z);
                }
            }
        }

        this.factions = new HashSet<>();
        this.logs = new HashSet<>();
        this.boulders = new HashSet<>();
    }

    public void advanceTime(double dt) throws ModelException {
        if (dt >= 0.2 || dt < 0)
            throw new IllegalArgumentException("invalid dt");

        // call advanceTime on all units
        for (Unit unit : getUnits()) {
            unit.advanceTime(dt);
        }
    }

    /**
     * @deprecated  use IntVector
     */
    public boolean isValidPosition(int x, int y, int z) {
        return x >= 0 && x < X_MAX && y >= 0 && y < Y_MAX && z >= 0 && z < Z_MAX;
    }

    public boolean isValidPosition(IntVector pos) {
        return isValidPosition(pos.getX(), pos.getY(), pos.getZ());
    }

    public static boolean isSolid(int type) {
        return type == ROCK || type == TREE;
    }

    public int getCubeType(int x, int y, int z) {
         return cubes[x][y][z].type;
    }
    public int getCubeType(IntVector cube) {
         return getCubeType(cube.getX(), cube.getY(), cube.getZ());
    }

    public void setCubeType(int x, int y, int z, int type) {
        if (isSolid(getCubeType(x, y, z)) && !isSolid(type)) {
            for (int[] coord : connectedToBorder.changeSolidToPassable(x, y, z)) {
                cubes[coord[0]][coord[1]][coord[2]].type = AIR;
                updateListener.notifyTerrainChanged(coord[0], coord[1], coord[2]);
                //TODO: cave in?
            }
        }
        cubes[x][y][z].type = type;
        updateListener.notifyTerrainChanged(x, y, z);
    }

    public void setCubeType(IntVector cube, int type) {
        setCubeType(cube.getX(), cube.getY(), cube.getZ(), type);
    }

    public boolean isCubeConnected(int x, int y, int z) {
        return connectedToBorder.isSolidConnectedToBorder(x, y, z);
    }

    public Set<Faction> getFactions() {
        return this.factions;
    }

    public  Set<Log> getLogs() {
        return this.logs;
    }

    public Set<Boulder> getBoulders() {
        return this.boulders;
    }

    // TODO: advanceTime to drop object
    private void addLog(int x, int y, int z) {
        int weight = (int)Math.floor(10.0 + 41.0 * Math.random());
        logs.add(new Log(x, y, z, weight));
    }

    // TODO: advanceTime to drop object
    private void addBoulder (int x, int y, int z) {
        int weight = (int)Math.floor(10.0 + 41.0 * Math.random());
        boulders.add(new Boulder(x, y, z, weight));
    }


    private Faction addFaction() {
        assert factions.size() < 5;

        Faction faction = new Faction();
        factions.add(faction);
        return faction;

    }

    private int getRandomAttribute() {
        return (int)Math.floor(25 + 76*Math.random());
    }

    public Unit spawnUnit(boolean defautBehaviour) {
        if (totalUnits >= 100)
            return null; //TODO: throw?

        //TODO: optimize
        List<int[]> possiblePositions = new ArrayList<>();
        if (getUnits().size() < MAX_UNITS) {
            for (int x = 0; x < X_MAX; x++) {
                for (int y = 0; y < Y_MAX; y++) {
                    for (int z = 0; z < Z_MAX; z++) {
                        if (!isSolid(getCubeType(x, y, z)) && (z == 0 || isSolid(getCubeType(x, y, z - 1))))
                            possiblePositions.add(new int[]{x, y, z});
                    }
                }
            }
        }

        int randIdx = (int)Math.floor(possiblePositions.size() * Math.random());
        int[] pos = possiblePositions.get(randIdx);

        Unit unit = new Unit(this, "Spawn", pos[0], pos[1], pos[2], getRandomAttribute(), getRandomAttribute(),
                getRandomAttribute(), getRandomAttribute());

        addUnit(unit);
        return unit;
    }

    public void addUnit(Unit unit) {
        if (factions.size() < 5) {
            Faction newFaction = addFaction();
            unit.setFaction(newFaction);
            newFaction.addUnit(unit);
        } else {
            Faction minFaction = factions.stream().filter(f -> f.getFactionSize() < MAX_FACTION_SIZE)
                    .min((f1, f2) -> Integer.compare(f1.getFactionSize(), f2.getFactionSize())).orElse(null);
            if (minFaction != null) {
                // TODO: what if no faction is found? can't happen?
                unit.setFaction(minFaction);
                minFaction.addUnit(unit);
            }
        }
        totalUnits += 1;
    }

    public Set<Unit> getUnits() {
        return factions.stream().collect(HashSet<Unit>::new, (s, f) -> s.addAll(f.getUnits()), AbstractCollection::addAll);
    }

    public void removeUnit (Unit unit) {
        Faction unitFac = unit.getFaction();
        unitFac.removeUnit(unit);
        if (unitFac.getFactionSize() <= 0)
            factions.remove(unitFac);
    }

    public void removeGameObject (GameObject object) {
        if (object.getClass().equals(Log.class))
            logs.remove(object);
        else if (object.getClass().equals(Boulder.class))
            boulders.remove(object);

        object.destruct();
    }

    private static final int[][] neighbourOffsets = new int[][] {

            { -1, 0, 0 },
            { +1, 0, 0 },
            { 0, -1, 0 },
            { 0, +1, 0 },
            { 0, 0, -1 },
            { 0, 0, +1 },

            { -1, -1, -1 },
            { -1, -1, 0 },
            { -1, 0, -1 },
            { 0, -1, -1 },

            { +1, +1, +1 },
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

    public static Stream<IntVector> getNeighbours(IntVector pos) {
        return Arrays.stream(neighbourOffsets).map(offset -> pos.add(offset[0], offset[1], offset[2]));
    }

    public static Stream<IntVector> getDirectlyAdjacent(IntVector pos) {
        return Arrays.stream(neighbourOffsets).limit(6).map(offset -> pos.add(offset[0], offset[1], offset[2]));
    }


    /**
     * Returns the coordinates of the cube that the unit currently occupies.
     *
     * @deprecated Use IntVector instead?
     *
     * @param   position
     *          The position to be converted.
     *
     * @return  Returns the rounded down position.
     *          | result[0] == floor(position[0]) &&
     *          | result[1] == floor(position[1]) &&
     *          | result[2] == floor(position[2]}
     */
    @Deprecated
    public static int[] getCubePosition(double[] position) {
        return new int[] {
                (int)Math.floor(position[0]),
                (int)Math.floor(position[1]),
                (int)Math.floor(position[2])
        };
    }
}
