package si.um.feri.mori.raster;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import si.um.feri.mori.assets.AssetDescriptors;
import si.um.feri.mori.raster.screens.RasterMapScreen;

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
