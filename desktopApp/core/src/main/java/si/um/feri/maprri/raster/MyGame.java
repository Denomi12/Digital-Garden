package si.um.feri.maprri.raster;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;

import si.um.feri.maprri.raster.screens.RasterMapScreen;
import si.um.feri.assets.AssetDescriptors;

public class MyGame extends Game {
    private AssetManager assetManager;



    @Override
    public void create() {
        assetManager = new AssetManager();

        assetManager.load(AssetDescriptors.FONT);
        assetManager.load(AssetDescriptors.GAME_ATLAS);
        assetManager.load(AssetDescriptors.UI_SKIN);

        assetManager.finishLoading();

        setScreen(new RasterMapScreen(this));
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}