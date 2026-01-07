package si.um.feri.maprri.raster;

import com.badlogic.gdx.Game;
import si.um.feri.maprri.raster.screens.RasterMapScreen;

public class MyGame extends Game {

    @Override
    public void create() {
        setScreen(new RasterMapScreen(this));
    }
}