package si.um.feri.maprri.raster.screens;

import com.badlogic.gdx.Screen;
import si.um.feri.maprri.raster.MyGame;
import si.um.feri.maprri.raster.RasterMap;

public class RasterMapScreen implements Screen {

    private final MyGame game;
    private RasterMap rasterMap;

    public RasterMapScreen(MyGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        rasterMap = new RasterMap(game);
        rasterMap.create();
    }

    @Override
    public void render(float delta) {
        rasterMap.render();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {
        rasterMap.dispose();
    }

    @Override
    public void dispose() {
        rasterMap.dispose();
    }
}