package si.um.feri.maprri.raster.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class Simple3DScreen implements Screen {

    private final Game game;
    private Stage stage;
    private Skin skin;

    public Simple3DScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage();
        skin = new Skin(Gdx.files.internal("comicSkin/comic-ui.json"));

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
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}