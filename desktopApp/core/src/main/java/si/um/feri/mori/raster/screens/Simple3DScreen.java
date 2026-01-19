package si.um.feri.mori.raster.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix4;
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

    private ModelBatch batch;
    private PerspectiveCamera camera;
    private FirstPersonCameraController controller;
    private final Garden garden;

    private Array<ModelInstance> instances;

    private Cubemap skybox;
    private Mesh skyboxMesh;
    private ShaderProgram skyboxShader;

    public Simple3DScreen(MyGame game, Garden garden) {
        this.game = game;
        this.assetManager = game.getAssetManager();
        this.gameAtlas = assetManager.get(AssetDescriptors.GAME_ATLAS);
        this.garden = garden;
    }

    @Override
    public void show() {
        TextureRegion gredaTexture = gameAtlas.findRegion(RegionNames.GREDA);
        TextureRegion potkaTexture = gameAtlas.findRegion(RegionNames.POT);
        TextureRegion visokaGredaTexture = gameAtlas.findRegion(RegionNames.VISOKA_GREDA);

        // --- Kamera ---
        camera = new PerspectiveCamera(70, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.position.set(0, 2, 5);
        camera.lookAt(0, 0, 0);
        camera.near = 0.1f;
        camera.far = 1000f;
        camera.update();

        controller = new FirstPersonCameraController(camera);
        controller.setVelocity(3f);

        Gdx.gl.glEnable(GL20.GL_DEPTH_TEST);

        // --- Models ---
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
                    instance.transform.setToTranslation(worldX, 0.25f, worldZ).scale(1f, 0.5f, 1f);
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

        createSkybox();

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

    private void createSkybox() {
        skybox = new Cubemap(
                Gdx.files.internal("skybox/px.png"),
                Gdx.files.internal("skybox/nx.png"),
                Gdx.files.internal("skybox/py.png"),
                Gdx.files.internal("skybox/ny.png"),
                Gdx.files.internal("skybox/pz.png"),
                Gdx.files.internal("skybox/nz.png")
        );

        String vertexShader =
                "attribute vec3 a_position;\n" +
                        "uniform mat4 u_projView;\n" +
                        "varying vec3 v_texCoords;\n" +
                        "void main() {\n" +
                        "   v_texCoords = a_position;\n" +
                        "   vec4 pos = u_projView * vec4(a_position, 1.0);\n" +
                        "   gl_Position = pos.xyww;\n" +
                        "}\n";

        String fragmentShader =
                "#ifdef GL_ES\n" +
                        "precision mediump float;\n" +
                        "#endif\n" +
                        "uniform samplerCube u_skybox;\n" +
                        "varying vec3 v_texCoords;\n" +
                        "void main() {\n" +
                        "   gl_FragColor = textureCube(u_skybox, v_texCoords);\n" +
                        "}\n";

        skyboxShader = new ShaderProgram(vertexShader, fragmentShader);
        if (!skyboxShader.isCompiled()) {
            System.err.println("Skybox shader error: " + skyboxShader.getLog());
        }

        float[] vertices = {
                -1, -1,  1,  1, -1,  1,  1,  1,  1, -1,  1,  1, // front
                -1, -1, -1, -1,  1, -1,  1,  1, -1,  1, -1, -1, // back
                -1,  1, -1, -1,  1,  1,  1,  1,  1,  1,  1, -1, // top
                -1, -1, -1,  1, -1, -1,  1, -1,  1, -1, -1,  1, // bottom
                1, -1, -1,  1,  1, -1,  1,  1,  1,  1, -1,  1, // right
                -1, -1, -1, -1, -1,  1, -1,  1,  1, -1,  1, -1  // left
        };

        short[] indices = {
                0, 1, 2, 2, 3, 0,       // front
                4, 5, 6, 6, 7, 4,       // back
                8, 9, 10, 10, 11, 8,    // top
                12, 13, 14, 14, 15, 12, // bottom
                16, 17, 18, 18, 19, 16, // right
                20, 21, 22, 22, 23, 20  // left
        };

        skyboxMesh = new Mesh(true, 24, 36,
                new VertexAttribute(VertexAttributes.Usage.Position, 3, "a_position"));
        skyboxMesh.setVertices(vertices);
        skyboxMesh.setIndices(indices);
    }

    @Override
    public void render(float delta) {
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

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        renderSkybox();

        batch.begin(camera);
        for (ModelInstance instance : instances) batch.render(instance);
        batch.end();

        stage.act(delta);
        stage.draw();
    }

    private void renderSkybox() {
        Gdx.gl.glDepthFunc(GL20.GL_LEQUAL);

        skyboxShader.bind();

        Matrix4 viewMatrix = new Matrix4(camera.view);
        viewMatrix.val[Matrix4.M03] = 0;
        viewMatrix.val[Matrix4.M13] = 0;
        viewMatrix.val[Matrix4.M23] = 0;

        Matrix4 projView = new Matrix4(camera.projection).mul(viewMatrix);
        skyboxShader.setUniformMatrix("u_projView", projView);

        skybox.bind(0);
        skyboxShader.setUniformi("u_skybox", 0);

        skyboxMesh.render(skyboxShader, GL20.GL_TRIANGLES);

        Gdx.gl.glDepthFunc(GL20.GL_LESS);
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
        skybox.dispose();
        if (skyboxMesh != null) skyboxMesh.dispose();
        if (skyboxShader != null) skyboxShader.dispose();
    }
}