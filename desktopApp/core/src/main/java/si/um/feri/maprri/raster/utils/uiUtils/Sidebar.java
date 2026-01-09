package si.um.feri.maprri.raster.utils.uiUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;

import si.um.feri.maprri.raster.GameManager;
import si.um.feri.maprri.raster.Garden;

public class Sidebar {
    private Window sidebar;
    private boolean collapsed = false;
    private static final float SIDEBAR_WIDTH = 280f;
    private Table scrollContent;
    private Skin skin;

    public Sidebar(Skin skin) {
        this.skin = skin;
        sidebar = createSidebarUI();
    }

    private Window createSidebarUI() {
        float sidebarWidth = 280f;

        // sidebar window creation
        assert skin != null;
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
        Table widgetContainer = new Table(skin);
        widgetContainer.top();
        widgetContainer.defaults().pad(5);

        Label titleLabel = new Label("My Gardens", skin, "title");
        titleLabel.setTouchable(Touchable.enabled);

        titleLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleSidbar();
            }
        });


        titleLabel.setAlignment(Align.center);

        widgetContainer.add(titleLabel).expandX().top().center().row();

        scrollContent = new Table(skin);
        scrollContent.top();
        scrollContent.defaults().expandX().fillX().pad(5);

        ScrollPane gardenScroll = new ScrollPane(scrollContent, skin);
        gardenScroll.setFadeScrollBars(false);
        gardenScroll.setScrollingDisabled(true, false);

        widgetContainer.add(gardenScroll)
                .expand()
                .fill()
                .row();



        return widgetContainer;
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

    public void addGardens(Array<Garden> gardens, TextureRegion gardenIcon) {
        if (gardens == null) return;

        for (int i = 0; i < gardens.size; i++) {
            Garden g = gardens.get(i);

            Table gardenTable = new Table(skin);
            gardenTable.defaults()
                    .padTop(4)
                    .padBottom(4)
                    .padLeft(2)
                    .padRight(2);

            Image icon = new Image(gardenIcon);
            icon.setScaling(Scaling.fit);

            TextButton btn = new TextButton(g.name, skin);

            // Enable wrapping on button text
            btn.getLabel().setWrap(true);
            btn.getLabel().setAlignment(Align.left);

            // Layout
            gardenTable.add(icon)
                    .size(32, 32)
                    .padLeft(6)
                    .padRight(6);

            gardenTable.add(btn)
                    .expandX()
                    .fillX()
                    .minHeight(52)        // taller button
                    .padRight(4);

            scrollContent.add(gardenTable)
                    .expandX()
                    .fillX()
                    .row();
        }
    }

}
