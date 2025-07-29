package com.ytasakawa.rpg_used_javafx_ytasakawa;

public class GameMap {
    private MapTileType[][] tiles;
    private int width;
    private int height;

    public GameMap(int width, int height) {
        this.width = width;
        this.height = height;
        this.tiles = new MapTileType[height][width];
        initializeMap();
    }

    private void initializeMap() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                tiles[y][x] = MapTileType.GRASS;
            }
        }

        tiles[0][1] = MapTileType.FOREST;
        tiles[0][2] = MapTileType.FOREST;
        tiles[1][1] = MapTileType.FOREST;
        tiles[1][2] = MapTileType.FOREST;

        tiles[0][5] = MapTileType.MOUNTAIN_BROWN;
        tiles[1][5] = MapTileType.MOUNTAIN_BROWN;

        tiles[0][6] = MapTileType.MOUNTAIN_GREY;
        tiles[0][7] = MapTileType.MOUNTAIN_GREY;
        tiles[1][6] = MapTileType.MOUNTAIN_GREY;
        tiles[1][7] = MapTileType.MOUNTAIN_GREY;


        tiles[2][6] = MapTileType.RIVER;
        tiles[3][6] = MapTileType.RIVER;
        tiles[4][6] = MapTileType.RIVER;
        tiles[5][6] = MapTileType.RIVER;

        tiles[7][5] = MapTileType.SEA;
        tiles[8][5] = MapTileType.SEA;
        tiles[9][5] = MapTileType.SEA;
        tiles[7][6] = MapTileType.SEA;
        tiles[8][6] = MapTileType.SEA;
        tiles[9][6] = MapTileType.SEA;
        tiles[8][7] = MapTileType.SEA;
        tiles[9][7] = MapTileType.SEA;
        tiles[8][8] = MapTileType.SEA;
        tiles[9][8] = MapTileType.SEA;
        tiles[8][9] = MapTileType.SEA;
        tiles[9][9] = MapTileType.SEA;

        tiles[4][4] = MapTileType.TOWN;
        tiles[4][5] = MapTileType.TOWN;
        tiles[5][4] = MapTileType.TOWN;
        tiles[5][5] = MapTileType.TOWN;

        tiles[4][7] = MapTileType.CAVE;
        tiles[4][8] = MapTileType.CAVE;
        tiles[5][7] = MapTileType.CAVE;
        tiles[5][8] = MapTileType.CAVE;
    }

    public boolean isWalkable(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return false;
        }
        MapTileType tileType = tiles[y][x];
        return !(tileType == MapTileType.MOUNTAIN_GREY ||
                 tileType == MapTileType.RIVER ||
                 tileType == MapTileType.SEA);
    }

    public MapTileType getTileType(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            return MapTileType.GRASS;
        }
        return tiles[y][x];
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
