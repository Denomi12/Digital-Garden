package si.um.feri.mori.raster;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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

        try {
            URL url = new URL("http://localhost:3001/garden");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setConnectTimeout(3000);
            con.setReadTimeout(3000);

            int status = con.getResponseCode();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            status >= 200 && status < 300
                                    ? con.getInputStream()
                                    : con.getErrorStream()
                    )
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();
            con.disconnect();

            String jsonString = response.toString();

            Json gdxJson = new Json();
            Array<Garden> gardens =
                    gdxJson.fromJson(Array.class, Garden.class, jsonString);
            GameManager.setGardens(gardens);

        } catch (Exception e) {
            System.err.println("REQUEST FAILED");
            e.printStackTrace();
        }

        setScreen(new RasterMapScreen(this));
    }

    public AssetManager getAssetManager() {
        return assetManager;
    }
}
