package hillbillies.model;

import be.kuleuven.cs.som.annotate.Basic;
import hillbillies.part2.listener.TerrainChangeListener;

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
    private boolean[][][] isCubeConnected;
    private boolean dirty;

    private Set<Faction> factions;
    private Set<Log> logs;
    private Set<Boulder> boulders;


    public World(int[][][] terrainTypes, TerrainChangeListener modelListener) {
        this.updateListener = modelListener;

        this.X_MAX = terrainTypes.length;
        this.Y_MAX = terrainTypes[0].length;
        this.Z_MAX = terrainTypes[0][0].length;

        this.cubes = new Cube[X_MAX][Y_MAX][Z_MAX];

        for (int x = 0; x < X_MAX; x++) {
            for (int y = 0; y < Y_MAX; y++) {
                for (int z = 0; z < Z_MAX; z++) {
                    this.cubes[x][y][z] = new Cube(terrainTypes[x][y][z]);
                }
            }
        }
    }

    public void advanceTime(double dt) {
        // update terrain if dirt
        // call advanceTime on all units
    }

    public int getCubeType(int x, int y, int z) {
         return cubes[x][y][z].type;
    }

    public void setCubeType(int x, int y, int z, int type) {
        cubes[x][y][z].type = type;
    }

    public boolean isCubeConnected(int x, int y, int z) {
        return isCubeConnected[x][y][z];
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
