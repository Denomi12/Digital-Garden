package si.um.feri.maprri.raster;

import com.badlogic.gdx.*;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.StaticTiledMapTile;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;

import si.um.feri.assets.AssetDescriptors;
import si.um.feri.assets.RegionNames;
import si.um.feri.maprri.raster.backendCalls.FetchGardens;
import si.um.feri.maprri.raster.screens.Simple3DScreen;
import si.um.feri.maprri.raster.utils.Constants;
import si.um.feri.maprri.raster.utils.Geolocation;
import si.um.feri.maprri.raster.utils.MapRasterTiles;
import si.um.feri.maprri.raster.utils.ZoomXY;
import si.um.feri.maprri.raster.utils.uiUtils.Sidebar;

import java.io.IOException;

public class RasterMap extends ApplicationAdapter implements GestureDetector.GestureListener {

    private final AssetManager assetManager;
    private final TextureAtlas gameAtlas;
    // center geolocation
    private final Geolocation CENTER_GEOLOCATION = new Geolocation(46.557314, 15.637771);
    private ShapeRenderer shapeRenderer;
    private Vector3 touchPosition;
    private TiledMap tiledMap;
    private TiledMapRenderer tiledMapRenderer;
    private OrthographicCamera camera;
    private Texture[] mapTiles;
    private ZoomXY beginTile;   // top left tile
    private SpriteBatch batch;
    private TextureRegion markerTexture;
    private Stage stage;
    private Skin skin;

    private boolean isZoomingToMarker = false;
    private Vector2 zoomTargetPos = new Vector2();
    private float zoomTarget = 1f;
    private float zoomSpeed = 1.5f;  // hitrost zooma
    private float moveSpeed = 3f;    // hitrost premikanja kamere
    private Dialog currentDialog = null;


    private MyGame game;
    private Sidebar sidebar;

    public RasterMap(MyGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.gameAtlas = assetManager.get(AssetDescriptors.GAME_ATLAS);
    }

    @Override
    public void create() {
        shapeRenderer = new ShapeRenderer();
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Constants.MAP_WIDTH, Constants.MAP_HEIGHT);
        camera.position.set(Constants.MAP_WIDTH / 2f, Constants.MAP_HEIGHT / 2f, 0);
        camera.viewportWidth = Constants.MAP_WIDTH / 2f;
        camera.viewportHeight = Constants.MAP_HEIGHT / 2f;
        camera.zoom = 2f;
        camera.update();

        touchPosition = new Vector3();

        try {
            //in most cases, geolocation won't be in the center of the tile because tile borders are predetermined (geolocation can be at the corner of a tile)
            ZoomXY centerTile = MapRasterTiles.getTileNumber(CENTER_GEOLOCATION.lat, CENTER_GEOLOCATION.lng, Constants.ZOOM);
            mapTiles = MapRasterTiles.getRasterTileZone(centerTile, Constants.NUM_TILES);
            //you need the beginning tile (tile on the top left corner) to convert geolocation to a location in pixels.
            beginTile = new ZoomXY(Constants.ZOOM, centerTile.x - ((Constants.NUM_TILES - 1) / 2), centerTile.y - ((Constants.NUM_TILES - 1) / 2));
        } catch (IOException e) {
            e.printStackTrace();
        }

        tiledMap = new TiledMap();
        MapLayers layers = tiledMap.getLayers();

        TiledMapTileLayer layer = new TiledMapTileLayer(Constants.NUM_TILES, Constants.NUM_TILES, MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE);
        int index = 0;
        for (int j = Constants.NUM_TILES - 1; j >= 0; j--) {
            for (int i = 0; i < Constants.NUM_TILES; i++) {
                TiledMapTileLayer.Cell cell = new TiledMapTileLayer.Cell();
                cell.setTile(new StaticTiledMapTile(new TextureRegion(mapTiles[index], MapRasterTiles.TILE_SIZE, MapRasterTiles.TILE_SIZE)));
                layer.setCell(i, j, cell);
                index++;
            }
        }
        layers.add(layer);

        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap);
        markerTexture = gameAtlas.findRegion(RegionNames.MARKER);

        String backendUrl = "http://localhost:3001";
        if (GameManager.getGardens() == null) {

            FetchGardens.getAllGardens(backendUrl, new FetchGardens.GardensCallback() {
                @Override
                public void onSuccess(Array<Garden> gardens) {
                    GameManager.setGardens(gardens);
                    System.out.println("Pridobljeno vrtov: " + gardens.size);
                }

                @Override
                public void onError(Throwable t) {
                    System.err.println("Napaka pri pridobivanju vrtov: " + t.getMessage());
                }
            });
        }

        skin = assetManager.get(AssetDescriptors.UI_SKIN);

        stage = new Stage();
        stage.setDebugAll(false);

        sidebar = new Sidebar(skin);
        sidebar.addTo(stage);

        GestureDetector gestureDetector = new GestureDetector(this);
        Gdx.input.setInputProcessor(new InputMultiplexer(stage, gestureDetector));
//        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);

        handleInput();

        camera.update();

        tiledMapRenderer.setView(camera);

        tiledMapRenderer.render();
        drawGardens();

        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();

        if (isZoomingToMarker) {
            camera.position.x += (zoomTargetPos.x - camera.position.x) * Gdx.graphics.getDeltaTime() * moveSpeed;
            camera.position.y += (zoomTargetPos.y - camera.position.y - 140) * Gdx.graphics.getDeltaTime() * moveSpeed;

            camera.zoom += (zoomTarget - camera.zoom) * Gdx.graphics.getDeltaTime() * zoomSpeed;

            if (Math.abs(camera.position.x - zoomTargetPos.x) < 1f &&
                    Math.abs(camera.position.y - zoomTargetPos.y) < 1f &&
                    Math.abs(camera.zoom - zoomTarget) < 0.01f) {
                camera.position.set(zoomTargetPos.x, zoomTargetPos.y, 0);
                camera.zoom = zoomTarget;
                isZoomingToMarker = false;
            }
        }

    }

    private void drawGardens() {
        if (GameManager.getGardens() == null) return;

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (Garden g : GameManager.getGardens()) {
            Vector2 pos = MapRasterTiles.getPixelPosition(g.latitude, g.longitude, beginTile.x, beginTile.y);
            batch.draw(markerTexture,
                    pos.x - markerTexture.getRegionWidth() / 2f,
                    pos.y - markerTexture.getRegionHeight() / 2f);
        }
        batch.end();
    }


    @Override
    public void dispose() {
        shapeRenderer.dispose();
        batch.dispose();
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        touchPosition.set(x, y, 0);
        camera.unproject(touchPosition);
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        touchPosition.set(x, y, 0);
        camera.unproject(touchPosition);

        if (GameManager.getGardens() != null) {
            for (Garden g : GameManager.getGardens()) {
                Vector2 pos = MapRasterTiles.getPixelPosition(g.latitude, g.longitude, beginTile.x, beginTile.y);

                float radius = markerTexture.getRegionWidth() / 2f;

                if (touchPosition.dst(pos.x, pos.y, 0) <= radius) {
                    zoomToMarker(pos, 0.5f);
                    showPopup(g);
                    break;
                }

            }
        }

        return true;
    }

    private void zoomToMarker(Vector2 markerPos, float zoom) {
        zoomTargetPos.set(markerPos);
        zoomTarget = zoom;
        isZoomingToMarker = true;
    }

    private void showPopup(Garden garden) {
        if (currentDialog != null) {
            currentDialog.hide();
        }

        currentDialog = new Dialog("", skin) {
            @Override
            protected void result(Object object) {
                if (object.equals("visit")) {
                    game.setScreen(new Simple3DScreen(game, garden));
                } else if (object.equals("back")) {
                    Gdx.app.log("Popup", "Back clicked");
                }
                isZoomingToMarker = false;
                currentDialog = null;
            }
        };

        currentDialog.setModal(false);
        currentDialog.text(garden.name);
        currentDialog.getContentTable().pad(30);
        currentDialog.getButtonTable().pad(20);

        currentDialog.button("Visit", "visit");
        currentDialog.button("Back", "back");

        for (int i = 0; i < currentDialog.getButtonTable().getChildren().size; i++) {
            TextButton btn = (TextButton) currentDialog.getButtonTable().getChildren().get(i);
            btn.pad(5, 10, 5, 10);
            currentDialog.getButtonTable().getCell(btn).width(120).height(40);
        }

        float dialogWidth = Gdx.graphics.getWidth() * 0.7f;
        float dialogHeight = Gdx.graphics.getHeight() * 0.6f;
        currentDialog.setSize(dialogWidth, dialogHeight);
        currentDialog.setPosition(
                (Gdx.graphics.getWidth() - currentDialog.getWidth()) / 2f,
                (Gdx.graphics.getHeight() - currentDialog.getHeight()) / 2f
        );

        currentDialog.show(stage);
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        camera.translate(-deltaX, deltaY);
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        if (initialDistance >= distance)
            camera.zoom += 0.02;
        else
            camera.zoom -= 0.02;
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        return false;
    }

    @Override
    public void pinchStop() {

    }

    private void handleInput() {
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            camera.zoom += 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            camera.zoom -= 0.02;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            camera.translate(-3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            camera.translate(3, 0, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            camera.translate(0, -3, 0);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            camera.translate(0, 3, 0);
        }

        camera.zoom = MathUtils.clamp(camera.zoom, 0.5f, 2f);

        float effectiveViewportWidth = camera.viewportWidth * camera.zoom;
        float effectiveViewportHeight = camera.viewportHeight * camera.zoom;

        camera.position.x = MathUtils.clamp(camera.position.x, effectiveViewportWidth / 2f, Constants.MAP_WIDTH - effectiveViewportWidth / 2f);
        camera.position.y = MathUtils.clamp(camera.position.y, effectiveViewportHeight / 2f, Constants.MAP_HEIGHT - effectiveViewportHeight / 2f);
    }
}
