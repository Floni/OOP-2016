package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;
import com.sun.org.apache.xpath.internal.operations.Mod;
import hillbillies.part2.facade.Facade;
import hillbillies.part2.listener.TerrainChangeListener;
import hillbillies.util.ConnectedToBorder;
import ogp.framework.util.ModelException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;

/**
 * Created by timo on 3/14/16.
 */
public class World {
    private static class Cube {
        public Cube(int type) {
            this.type = type;
            this.gameObjects = new HashSet<GameObject>();
        }
        public int type;
        public Set<GameObject> gameObjects;
    }
    public static final int AIR = 0;
    public static final int WORKSHOP = 3;
    public static final int ROCK = 1;
    public static final int TREE = 2;

    public static int X_MAX;
    public static int Y_MAX;
    public static int Z_MAX;

    public final static double Lc = 1.0;

    private final TerrainChangeListener updateListener;

    private Cube[][][] cubes;
    private boolean[][][] connectedCubeFlags;
    private boolean dirty;

    private Set<Faction> factions;
    private Set<Log> logs;
    private Set<Boulder> boulders;


    private ConnectedToBorder connectedToBorder;

    public World(int[][][] terrainTypes, TerrainChangeListener modelListener) {
        this.updateListener = modelListener;

        this.dirty = true; //TODO: can terrain be invalid when constructed

        this.X_MAX = terrainTypes.length;
        this.Y_MAX = terrainTypes[0].length;
        this.Z_MAX = terrainTypes[0][0].length;

        this.cubes = new Cube[X_MAX][Y_MAX][Z_MAX];
        this.connectedCubeFlags = new boolean[X_MAX][Y_MAX][Z_MAX];

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

        // update terrain if dirt
        if (this.dirty) {
            this.dirty = false;
            //updateTerrain();
            /*
            for (int x = 0; x < X_MAX; x++) {
                for (int y = 0; y < Y_MAX; y++) {
                    for (int z = 0; z < Z_MAX; z++) {
                        if (isSolid(getCubeType(x, y, z)) && !isCubeConnected(x, y, z)) {
                            setCubeType(x, y, z, WORKSHOP);
                            updateListener.notifyTerrainChanged(x, y, z);
                            System.out.println("cave in!");
                        }
                    }
                }
            }*/
        }
        // call advanceTime on all units
        for (Unit unit : getUnits()) {
            unit.advanceTime(dt);
        }
    }

    public boolean isValidPosition(int x, int y, int z) {
        return x >= 0 && x < X_MAX && y >= 0 && y < Y_MAX && z >= 0 && z < Z_MAX;
    }

    /*

    private void updateTerrain() {
        for (boolean[][] xr : connectedCubeFlags) {
            for (boolean[] yr : xr) {
                Arrays.fill(yr, false);
            }
        }

        // TODO: for each wall/direction, recursive
        Queue<int[]> toCheckCubes = new ArrayDeque<>();

        for (int z : new int[]{0, Z_MAX-1}) {
            for (int x = 0; x < X_MAX; x++) {
                for (int y = 0; y < Y_MAX; y++) {
                    if (isSolid(getCubeType(x, y, z)))
                        toCheckCubes.add(new int[]{x, y, z});
                }
            }
        }
        for (int y : new int[]{0, Y_MAX-1}) {
            for (int x = 0; x < X_MAX; x++) {
                for (int z = 0; z < Z_MAX; z++) {
                    if (isSolid(getCubeType(x, y, z)))
                        toCheckCubes.add(new int[]{x, y, z});
                }
            }
        }
        for (int x : new int[]{0, X_MAX-1}) {
            for (int z = 0; z < Z_MAX; z++) {
                for (int y = 0; y < Y_MAX; y++) {
                    if (isSolid(getCubeType(x, y, z)))
                        toCheckCubes.add(new int[]{x, y, z});
                }
            }
        }

        while (!toCheckCubes.isEmpty()) {
            int[] cur_cube = toCheckCubes.remove();
            int x = cur_cube[0];
            int y = cur_cube[1];
            int z = cur_cube[2];
            connectedCubeFlags[x][y][z] = true;
            if (isValidPosition(x, y, z+1) && isSolid(getCubeType(x, y, z+1)) && !connectedCubeFlags[x][y][z+1])
                toCheckCubes.add(new int[]{x, y, z+1});
            if (isValidPosition(x, y, z-1) && isSolid(getCubeType(x, y, z-1)) && !connectedCubeFlags[x][y][z-1])
                toCheckCubes.add(new int[]{x, y, z-1});
            if (isValidPosition(x, y-1, z) && isSolid(getCubeType(x, y-1, z)) && !connectedCubeFlags[x][y-1][z])
                toCheckCubes.add(new int[]{x, y-1, z});
            if (isValidPosition(x, y+1, z) && isSolid(getCubeType(x, y+1, z)) && !connectedCubeFlags[x][y+1][z])
                toCheckCubes.add(new int[]{x, y+1, z});
            if (isValidPosition(x-1, y, z) && isSolid(getCubeType(x-1, y, z)) && !connectedCubeFlags[x-1][y][z])
                toCheckCubes.add(new int[]{x-1, y, z});
            if (isValidPosition(x+1, y, z) && isSolid(getCubeType(x+1, y, z)) && !connectedCubeFlags[x+1][y][z])
                toCheckCubes.add(new int[]{x+1, y, z});
        }

        for (int x = 0; x < X_MAX; x++) {
            for (int y = 0; y < Y_MAX; y++) {
                for (int z = 0; z < Z_MAX; z++) {
                    if (!connectedCubeFlags[x][y][z] && isSolid(getCubeType(x, y, z))) {
                        setCubeType(x, y, z, WORKSHOP);
                        updateListener.notifyTerrainChanged(x, y, z);
                        System.out.println("cave in!");
                    }
                        //caveIn(x, y, z);

                }
            }
        }
    }

    */

    public static boolean isSolid(int type) {
        return type == ROCK || type == TREE;
    }

    public int getCubeType(int x, int y, int z) {
         return cubes[x][y][z].type;
    }

    public void setCubeType(int x, int y, int z, int type) {
        cubes[x][y][z].type = type;
    }

    public boolean isCubeConnected(int x, int y, int z) {
        //return connectedCubeFlags[x][y][z];
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
        if (factions.size() < 5) {
            Faction faction = new Faction();
            factions.add(faction);
            return faction;
        }
        return null;
    }

    private int getRandomAttribute() {
        return (int)Math.floor(25 + 76*Math.random());
    }

    public Unit spawnUnit(boolean defautBehaviour) {
        List<int[]> possiblePositions = new ArrayList<>();
        for (int x = 0; x < X_MAX; x++) {
            for (int y = 0; y < Y_MAX; y++) {
                for (int z = 0; z < Z_MAX; z++) {
                    if (!isSolid(getCubeType(x, y, z)) && (z == 0 || isSolid(getCubeType(x, y, z-1))))
                        possiblePositions.add(new int[]{x, y, z});
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
        if (factions.size() != 5) {
            Faction newFaction = addFaction();
            unit.setFaction(newFaction);
            newFaction.addUnit(unit);
        } else {
            Faction minFaction = null;
            for (Faction faction : factions) {
                if (minFaction == null || minFaction.getFactionSize() > faction.getFactionSize())
                    minFaction = faction;
            }
            unit.setFaction(minFaction);
            minFaction.addUnit(unit);
        }
    }

    public Set<Unit> getUnits() {
        Set<Unit> ret = new HashSet<>();
        for (Faction faction : factions) {
            ret.addAll(faction.getUnits());
        }
        return ret;
    }
}
