package si.um.feri.maprri.raster.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import si.um.feri.assets.AssetDescriptors;
import si.um.feri.assets.RegionNames;
import si.um.feri.maprri.raster.MyGame;

public class Simple3DScreen implements Screen {

    private final MyGame game;
    private final TextureAtlas gameAtlas;
    private final AssetManager assetManager;
    private Stage stage;
    private Skin skin;

    private Model model;
    private ModelBuilder modelBuilder;
    private ModelInstance instance;
    private ModelBatch batch;
    private TextureRegion texture;
    private PerspectiveCamera camera;
    private FirstPersonCameraController controller;

    public Simple3DScreen(MyGame game) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.gameAtlas = assetManager.get(AssetDescriptors.GAME_ATLAS);
    }

    @Override
    public void show() {
        //Kamera
        camera = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 0, 5);
        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.far = 1000f;
        camera.update();

        controller = new FirstPersonCameraController(camera);
        controller.setVelocity(3f);
        Gdx.input.setInputProcessor(controller);

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        //Model kocke
        texture = gameAtlas.findRegion(RegionNames.GREDA);
        modelBuilder = new ModelBuilder();
        Material material = new Material(TextureAttribute.createDiffuse(texture));
        long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates;
        model = modelBuilder.createBox(1f, 1f, 1f, material, attributes);
        instance = new ModelInstance(model);
        batch = new ModelBatch();

        stage = new Stage();
        skin = assetManager.get(AssetDescriptors.UI_SKIN);

        stage = new Stage();
        skin = assetManager.get(AssetDescriptors.UI_SKIN);
        TextButton backButton = new TextButton("Back", skin);
        backButton.setSize(120, 45);
        backButton.setPosition(
                Gdx.graphics.getWidth() - backButton.getWidth() - 20,
                Gdx.graphics.getHeight() - backButton.getHeight() - 20
        );
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                game.setScreen(new RasterMapScreen(game));
            }
        });
        stage.addActor(backButton);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(controller);
        Gdx.input.setInputProcessor(multiplexer);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        controller.update();

        float turnSpeed = 30 * delta;
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.LEFT)) {
            camera.rotate(Vector3.Y, turnSpeed);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.RIGHT)) {
            camera.rotate(Vector3.Y, -turnSpeed);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.UP)) {
            camera.direction.rotate(camera.direction.cpy().crs(camera.up), turnSpeed);
        }
        if (Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.DOWN)) {
            camera.direction.rotate(camera.direction.cpy().crs(camera.up), -turnSpeed);
        }

        camera.update();

        batch.begin(camera);
        batch.render(instance);
        batch.end();

        stage.act(delta);
        stage.draw();
    }


    @Override
    public void resize(int width, int height) {
        camera.viewportWidth = width;
        camera.viewportHeight = height;
        camera.update();
        controller.update();
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        model.dispose();
        batch.dispose();
    }
}