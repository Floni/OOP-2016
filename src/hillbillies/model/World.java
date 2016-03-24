package hillbillies.model;

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
/**
 * TODO:
 *  - Only use Vector and IntVector
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


    //<editor-fold desc="Constants">
    public static final int AIR = 0;
    public static final int WORKSHOP = 3;
    public static final int ROCK = 1;
    public static final int TREE = 2;

    public static final double Lc = 1.0;
    public static final int MAX_UNITS = 100;
    public static final int MAX_FACTION_SIZE = 50;
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
    public void advanceTime(double dt) throws ModelException {
        if (dt >= 0.2 || dt < 0)
            throw new IllegalArgumentException("invalid dt");

        // call advanceTime on all units
        for (Unit unit : getUnits()) {
            unit.advanceTime(dt);
        }

        for (Boulder boulder : getBoulders()) {
            boulder.advanceTime(dt);
        }

        for (Log log : getLogs()) {
            log.advanceTime(dt);
        }
    }
    //</editor-fold>

    public boolean isValidPosition(IntVector pos) {
        return pos.getX() >= 0 && pos.getX() < X_MAX && pos.getY() >= 0
                && pos.getY() < Y_MAX && pos.getZ() >= 0 && pos.getZ() < Z_MAX;
    }

    public static boolean isSolid(int type) {
        return type == ROCK || type == TREE;
    }

    //<editor-fold desc="Cubes">
    private Cube getCube(IntVector cubeLoc) {
        return cubes[cubeLoc.getX()][cubeLoc.getY()][cubeLoc.getZ()];
    }

    public boolean isCubeConnected(int x, int y, int z) {
        return connectedToBorder.isSolidConnectedToBorder(x, y, z);
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
                breakCube(new IntVector(coord));
                updateListener.notifyTerrainChanged(coord[0], coord[1], coord[2]);
            }
        }
        cubes[x][y][z].type = type;
        updateListener.notifyTerrainChanged(x, y, z);
    }
    public void setCubeType(IntVector cube, int type) {
        setCubeType(cube.getX(), cube.getY(), cube.getZ(), type);
    }

    private void dropChance(IntVector location, int type) {
        if (Math.random() < 0.25) {
            if (type == World.ROCK) {
                addGameObject(location, new Boulder(this, location));
            } else if (type == World.TREE) {
                addGameObject(location, new Log(this, location));
            }
        }
    }

    public void breakCube(IntVector location) {
        int type = getCubeType(location);
        setCubeType(location, AIR);
        dropChance(location, type);
    }
    //</editor-fold>

    //<editor-fold desc="Logs and Boulders">
    //TODO: return stream?
    public  Set<Log> getLogs() {
        return this.gameObjects.stream().filter(o -> o instanceof Log)
                .map(Log.class::cast).collect(Collectors.toSet());
    }

    public Set<Boulder> getBoulders() {
        return this.gameObjects.stream().filter(o -> o instanceof Boulder)
                .map(Boulder.class::cast).collect(Collectors.toSet());
    }

    public Set<Log> getLogs(IntVector cubeLoc) {
        Cube cube = getCube(cubeLoc);
        return cube.gameObjects.stream().filter(o -> o instanceof Log)
                .map(Log.class::cast).collect(Collectors.toSet());
    }

    public Set<Boulder> getBoulders(IntVector cubeLoc) {
        Cube cube = getCube(cubeLoc);
        return cube.gameObjects.stream().filter(o -> o instanceof Boulder)
                .map(Boulder.class::cast).collect(Collectors.toSet());
    }

    public void addGameObject(IntVector cubeLoc, GameObject gameObject) {
        gameObject.setPosition(cubeLoc.toVector().add(Lc/2));
        Cube cube = getCube(cubeLoc);
        gameObjects.add(gameObject);
        cube.gameObjects.add(gameObject);
    }

    public void consumeLog(IntVector cubeLoc) {
        Set<Log> cubeLogs = getLogs(cubeLoc);
        if (cubeLogs.size() >= 1)
            removeGameObject(cubeLogs.iterator().next());
    }

    public void consumeBoulder(IntVector cubeLoc) {
        Set<Boulder> boulders = getBoulders(cubeLoc);
        if (boulders.size() >= 1)
            removeGameObject(boulders.iterator().next());
    }

    public void removeGameObject (GameObject object) {
        removeCubeObject(object);
        gameObjects.remove(object);
        object.terminate();
    }

    public void removeCubeObject(GameObject object) {
        getCube(object.getPosition().toIntVector()).gameObjects.remove(object);
    }

    public void addCubeObject(GameObject object) {
        getCube(object.getPosition().toIntVector()).gameObjects.add(object);
    }
    //</editor-fold>

    //<editor-fold desc="Factions">
    public Set<Faction> getFactions() {
        return this.factions;
    }

    private Faction addFaction() {
        assert factions.size() < 5;

        Faction faction = new Faction();
        factions.add(faction);
        return faction;

    }
    //</editor-fold>

    //<editor-fold desc="Units">
    private int getRandomAttribute() {
        return (int)Math.floor(25 + 76*Math.random());
    }

    public Unit spawnUnit(boolean defaultBehaviour) {
        if (totalUnits >= MAX_UNITS)
            return null; // silently reject?

        IntVector randPos;
        do {
            randPos = new IntVector(Math.random()*X_MAX,
                    Math.random()*Y_MAX,
                    Math.random()*Z_MAX);
        } while (!isValidPosition(randPos) || isSolid(getCubeType(randPos)) || (randPos.getZ() != 0 && !isSolid(getCubeType(randPos.add(0, 0, -1)))));

        Unit unit = new Unit(this, "Spawn", randPos.getX(), randPos.getY(), randPos.getZ(), getRandomAttribute(), getRandomAttribute(),
                getRandomAttribute(), getRandomAttribute());
        if (defaultBehaviour)
            unit.startDefaultBehaviour();
        addUnit(unit);
        return unit;
    }

    public void addUnit(Unit unit) {
        unit.setWorld(this);
        if (factions.size() < 5) {
            Faction newFaction = addFaction();
            unit.setFaction(newFaction);
            newFaction.addUnit(unit);
        } else {
            Faction minFaction = factions.stream().filter(f -> f.getFactionSize() < MAX_FACTION_SIZE)
                    .min((f1, f2) -> Integer.compare(f1.getFactionSize(), f2.getFactionSize())).orElse(null);
            if (minFaction != null) {
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
    //</editor-fold>

    //<editor-fold desc="Neighbours">
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
    //</editor-fold>
}
