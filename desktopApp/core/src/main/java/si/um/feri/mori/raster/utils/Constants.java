package si.um.feri.mori.raster.utils;

import com.badlogic.gdx.Gdx;
import si.um.feri.mori.raster.utils.MapRasterTiles;

public class Constants {
    public static final int NUM_TILES = 6;
    public static final int ZOOM = 11;
    public static final int MAP_WIDTH = MapRasterTiles.TILE_SIZE * NUM_TILES;
    public static final int MAP_HEIGHT = si.um.feri.mori.raster.utils.MapRasterTiles.TILE_SIZE * NUM_TILES;
    public static final int HUD_WIDTH = Gdx.graphics.getWidth();
    public static final int HUD_HEIGHT = Gdx.graphics.getHeight();
}
