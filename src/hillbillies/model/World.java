package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.part2.listener.TerrainChangeListener;
import hillbillies.util.ConnectedToBorder;

import java.util.HashSet;
import java.util.Set;

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

    public final int X_MAX;
    public final int Y_MAX;
    public final int Z_MAX;

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
    }

    public void advanceTime(double dt) {
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

    private void addFaction() {
        if (factions.size() < 5)
            factions.add(new Faction());
    }
}
