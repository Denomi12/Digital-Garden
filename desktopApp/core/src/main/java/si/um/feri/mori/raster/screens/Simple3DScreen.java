package si.um.feri.mori.raster.screens;

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
import com.badlogic.gdx.utils.Array;
import si.um.feri.mori.assets.AssetDescriptors;
import si.um.feri.mori.assets.RegionNames;
import si.um.feri.mori.raster.Garden;
import si.um.feri.mori.raster.MyGame;

import java.util.HashMap;
import java.util.Map;

public class Simple3DScreen implements Screen {

    private final MyGame game;
    private final TextureAtlas gameAtlas;
    private final AssetManager assetManager;
    private Stage stage;
    private Skin skin;

    private Model gredaModel;
    private Model potkaModel;
    private Model visokaGredaModel;

    private ModelInstance instance;
    private ModelBatch batch;
    private PerspectiveCamera camera;
    private FirstPersonCameraController controller;
    private final Garden garden;

    private Array<ModelInstance> instances;

    public Simple3DScreen(MyGame game, Garden garden) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.gameAtlas = assetManager.get(AssetDescriptors.GAME_ATLAS);
        this.garden = garden;
        for(Garden.Element g : garden.elements){
            System.out.println("Element: " + g.type);
            System.out.println("gx: " + g.x);
            System.out.println("gy: " + g.y);
        }
    }

    @Override
    public void show() {
        TextureRegion gredaTexture = gameAtlas.findRegion(RegionNames.GREDA);
        TextureRegion potkaTexture = gameAtlas.findRegion(RegionNames.POT);
        TextureRegion visokaGredaTexture = gameAtlas.findRegion(RegionNames.VISOKA_GREDA);

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
        instances = new Array<>();
        Map<String, Model> modelMap = new HashMap<>();

        ModelBuilder modelBuilder = new ModelBuilder();
        long attributes = VertexAttributes.Usage.Position | VertexAttributes.Usage.TextureCoordinates;

        gredaModel = modelBuilder.createBox(
                1f, 1f, 1f,
                new Material(TextureAttribute.createDiffuse(gredaTexture)),
                attributes
        );

        potkaModel = modelBuilder.createBox(
                1f, 1f, 1f,
                new Material(TextureAttribute.createDiffuse(potkaTexture)),
                attributes
        );

        visokaGredaModel = modelBuilder.createBox(
                1f, 1.5f, 1f,
                new Material(TextureAttribute.createDiffuse(visokaGredaTexture)),
                attributes
        );

        modelMap.put("Greda", gredaModel);
        modelMap.put("Potka", potkaModel);
        modelMap.put("Visoka greda", visokaGredaModel);


        float cellSize = 1f;

        for (Garden.Element g : garden.elements) {
            Model model = modelMap.get(g.type);
            if (model == null) continue;

            ModelInstance instance = new ModelInstance(model);

            float worldX = g.x * cellSize;
            float worldZ = g.y * cellSize;

            switch (g.type) {
                case "Potka":
                    instance.transform
                            .setToTranslation(worldX, 0.25f, worldZ)
                            .scale(1f, 0.5f, 1f);
                    break;
                case "Greda":
                    instance.transform.setToTranslation(worldX, 0.5f, worldZ);
                    break;
                case "Visoka greda":
                    instance.transform.setToTranslation(worldX, 1f, worldZ);
                    break;
            }

            instances.add(instance);
        }


        batch = new ModelBatch();


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
        for (ModelInstance instance : instances) {
            batch.render(instance);
        }
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
        gredaModel.dispose();
        potkaModel.dispose();
        visokaGredaModel.dispose();
        batch.dispose();
        stage.dispose();
        batch.dispose();
    }
}
