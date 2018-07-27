package com.elvarg.game.content.skill.construction;

/**
 * Manages a palette of map regions for use in the constructed map region
 * packet.
 *
 * @author Graham
 */
public class Palette {
    /**
     * Normal direction.
     */
    public static final int DIRECTION_NORMAL = 0;
    /**
     * Rotation direction clockwise by 0 degrees.
     */
    public static final int DIRECTION_CW_0 = 0;
    /**
     * Rotation direction clockwise by 90 degrees.
     */
    public static final int DIRECTION_CW_90 = 1;
    /**
     * Rotation direction clockwise by 180 degrees.
     */
    public static final int DIRECTION_CW_180 = 2;
    /**
     * Rotation direction clockwise by 270 degrees.
     */
    public static final int DIRECTION_CW_270 = 3;
    /**
     * The array of tiles.
     */
    private PaletteTile[][][] tiles = new PaletteTile[13][13][4];

    /**
     * Create a palette with a given coords
     *
     * @param xStart South-west X value
     * @param yStart South-west Y value
     * @return The create palette
     */
    public static Palette createPalette(int xStart, int yStart) {
        Palette palette = new Palette();
        for (int x = 4; x < 8; x++) {
            for (int y = 4; y < 8; y++) {
                palette.setTile(x, y, 0, new PaletteTile(xStart + ((x - 3) * 8), yStart + ((y - 3) * 8), 0));
            }
        }
        return palette;
    }

    /**
     * Gets a tile.
     *
     * @param x X getLocation.
     * @param y Y getLocation.
     * @param z Z getLocation.
     * @return The tile.
     */
    public PaletteTile getTile(int x, int y, int z) {
        return tiles[x][y][z];
    }

    /**
     * Sets a tile.
     *
     * @param x    X getLocation.
     * @param y    Y getLocation.
     * @param z    Z getLocation.
     * @param tile The tile.
     */
    public void setTile(int x, int y, int z, PaletteTile tile) {
        tiles[x][y][z] = tile;
    }

    /**
     * Represents a tile to copy in the palette.
     *
     * @author Graham Edgecombe
     */
    public static class PaletteTile {
        /**
         * X coordinate.
         */
        private int x;
        /**
         * Y coordinate.
         */
        private int y;
        /**
         * Z coordinate.
         */
        private int z;
        /**
         * Rotation.
         */
        private int rot;

        /**
         * Creates a tile.
         *
         * @param x The x coordinate.
         * @param y The y coordinate.
         */
        public PaletteTile(int x, int y) {
            this(x, y, 0);
        }

        /**
         * Creates a tile.
         *
         * @param x The x coordinate.
         * @param y The y coordinate.
         * @param z The z coordinate.
         */
        public PaletteTile(int x, int y, int z) {
            this(x, y, z, DIRECTION_NORMAL);
        }

        /**
         * Creates a tile.
         *
         * @param x   The x coordinate.
         * @param y   The y coordinate.
         * @param z   The z coordinate.
         * @param rot The rotation.
         */
        public PaletteTile(int x, int y, int z, int rot) {
            this.x = x;
            this.y = y;
            this.z = z;
            this.rot = rot;
        }

        /**
         * Gets the x coordinate.
         *
         * @return The x coordinate.
         */
        public int getX() {
            return x / 8;
        }

        /**
         * Gets the y coordinate.
         *
         * @return The y coordinate.
         */
        public int getY() {
            return y / 8;
        }

        /**
         * Gets the z coordinate.
         *
         * @return The z coordinate.
         */
        public int getZ() {
            return z % 4;
        }

        /**
         * Gets the rotation.
         *
         * @return The rotation.
         */
        public int getRotation() {
            return rot % 4;
        }
    }
}