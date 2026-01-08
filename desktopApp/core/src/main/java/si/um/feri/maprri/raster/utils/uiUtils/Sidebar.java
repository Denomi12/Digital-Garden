package si.um.feri.maprri.raster.utils.uiUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

public class Sidebar {
    private Window sidebar;
    private boolean collapsed = false;
    private static final float SIDEBAR_WIDTH = 280f;

    public Sidebar(Skin skin) {
        sidebar = createSidebarUI(skin);
    }

    private Window createSidebarUI(Skin skin) {
        float sidebarWidth = 280f;

        // sidebar window creation
        Window sidebar = new Window("", skin, "dialog");
        sidebar.addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if (collapsed) {
                    toggleSidbar();
                }
            }
        });


        sidebar.setMovable(false);
        sidebar.setResizable(true);

        sidebar.setSize(sidebarWidth, Gdx.graphics.getHeight());
        sidebar.setPosition(0, 0);

        sidebar.pad(10);

        sidebar.setClip(true);

        // Adding widgets to sidebar

        Table content = createSidebarWidgets(skin);
        sidebar.add(content).expand().fill();

        return sidebar;
    }

    private Table createSidebarWidgets(Skin skin) {
        Table table = new Table(skin);
        table.top();
        table.defaults().pad(5);

        Label titleLabel = new Label("My Gardens", skin, "title");
        titleLabel.setTouchable(Touchable.enabled);

        titleLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleSidbar();
            }
        });


        titleLabel.setAlignment(Align.center);

        table.add(titleLabel).expandX().top().center().row();

        return table;
    }

    private void toggleSidbar() {
        sidebar.clearActions();
        if (!collapsed) {
            sidebar.addAction(
                    Actions.sizeTo(2f, sidebar.getHeight(), 0.3f)
            );
        } else {
            sidebar.addAction(
                    Actions.sizeTo(SIDEBAR_WIDTH, sidebar.getHeight(), 0.3f)
            );
        }

        collapsed = !collapsed;

    }

    public void addTo(Stage stage) {
        stage.addActor(sidebar);
    }

}
