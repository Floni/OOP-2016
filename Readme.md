extra cmd javadoc:
-taglet be.kuleuven.cs.som.taglet.InvarTaglet -taglet be.kuleuven.cs.som.taglet.PreTaglet -taglet be.kuleuven.cs.som.taglet.PostTaglet -taglet be.kuleuven.cs.som.taglet.EffectTaglet -taglet be.kuleuven.cs.som.taglet.ReturnTaglet -taglet be.kuleuven.cs.som.taglet.ThrowsTaglet -taglet be.kuleuven.cs.som.taglet.NoteTaglet -tagletpath "lib\AnnotationsDoclets.jar"

/*

    private void updateTerrain() {
        for (boolean[][] xr : connectedCubeFlags) {
            for (boolean[] yr : xr) {
                Arrays.fill(yr, false);
            }
        }
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