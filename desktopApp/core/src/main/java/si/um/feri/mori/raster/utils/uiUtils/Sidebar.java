package si.um.feri.mori.raster.utils.uiUtils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import si.um.feri.mori.raster.Garden;
import si.um.feri.mori.raster.RasterMap;
import si.um.feri.mori.raster.utils.MapRasterTiles;

public class Sidebar {
    private Window sidebar;
    private boolean collapsed = false;
    private static final float SIDEBAR_WIDTH = 280f;
    private static final float HOVER_STRIP_WIDTH = 40f;
    private Table scrollContent;
    private Skin skin;
    private Image toggleButton;
    private boolean isAnimating = false;

    public Sidebar(Skin skin) {
        this.skin = skin;
        sidebar = createSidebarUI();
    }


    private Window createSidebarUI() {
        Window sidebar = new Window("", skin, "dialog");
        sidebar.setMovable(false);
        sidebar.setResizable(true);
        sidebar.setSize(SIDEBAR_WIDTH, Gdx.graphics.getHeight());
        sidebar.setPosition(0, 0);
        sidebar.pad(10);
        sidebar.setClip(true);

        Table content = createSidebarWidgets(skin);
        sidebar.add(content).expand().fill();

        return sidebar;
    }

    private Table createSidebarWidgets(Skin skin) {
        Table widgetContainer = new Table(skin);
        widgetContainer.top();
        widgetContainer.defaults().pad(5);

        Label titleLabel = new Label("My Gardens", skin);
        titleLabel.setAlignment(Align.center);

        Texture backTexture = new Texture(Gdx.files.internal("assets/backbutton.png"));
        toggleButton = new Image(backTexture);
        toggleButton.setTouchable(Touchable.enabled);
        toggleButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                toggleSidebar();
            }
        });

        Table header = new Table();
        header.add(titleLabel).expandX().left().padLeft(10);
        header.add(toggleButton).size(32,32).padRight(10).top().right();

        widgetContainer.add(header).expandX().fillX().top().row();

        scrollContent = new Table(skin);
        scrollContent.top();
        scrollContent.defaults().expandX().fillX().pad(5);

        ScrollPane gardenScroll = createGardenScrollPane(scrollContent);

        widgetContainer.add(gardenScroll).expand().fill().row();

        return widgetContainer;
    }


    private ScrollPane createGardenScrollPane(Table content) {
        ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();

        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1.0f,1.0f,1.0f,0.3f);
        pixmap.fill();
        style.background = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        pixmap = new Pixmap(0,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(1.0f,1.0f,1.0f,0.7f);
        pixmap.fill();
        style.vScroll = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        style.vScrollKnob = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        ScrollPane gardenScroll = new ScrollPane(content, style);
        gardenScroll.setFadeScrollBars(false);
        gardenScroll.setScrollingDisabled(true, false);
        return gardenScroll;
    }


    private void toggleSidebar() {
        if (isAnimating) return;
        isAnimating = true;

        sidebar.clearActions();

        if (!collapsed) {
            sidebar.addAction(Actions.sequence(
                    Actions.sizeTo(0, sidebar.getHeight(), 0.3f),
                    Actions.run(() -> isAnimating = false)
            ));
            toggleButton.addAction(Actions.fadeOut(0.2f));
        } else {
            sidebar.addAction(Actions.sequence(
                    Actions.sizeTo(SIDEBAR_WIDTH, sidebar.getHeight(), 0.3f),
                    Actions.run(() -> isAnimating = false)
            ));
            toggleButton.addAction(Actions.fadeIn(0.2f));
        }

        collapsed = !collapsed;
    }

    public void addTo(Stage stage) {
        stage.addActor(sidebar);

        Image hoverArea = new Image();
        hoverArea.setSize(HOVER_STRIP_WIDTH, Gdx.graphics.getHeight());
        hoverArea.setPosition(0,0);
        hoverArea.setColor(1,1,1,0);
        hoverArea.addListener(new InputListener(){
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                if(collapsed && !isAnimating){
                    toggleSidebar();
                }
            }
        });
        stage.addActor(hoverArea);
    }

    public void addGardens(Array<Garden> gardens, TextureRegion gardenIcon, RasterMap rasterMap) {
        if (gardens == null) return;

        Color hoverColor = skin.getColor("button");
        Pixmap pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        TextureRegionDrawable whiteBackground = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
        pixmap.dispose();

        for (Garden g : gardens) {
            if (!rasterMap.isGardenVisible(g)) continue;

            pixmap = new Pixmap(1,1, Pixmap.Format.RGBA8888);
            pixmap.setColor(hoverColor);
            pixmap.fill();
            TextureRegionDrawable hoverBackground = new TextureRegionDrawable(new TextureRegion(new Texture(pixmap)));
            pixmap.dispose();

            TextButton.TextButtonStyle baseStyle = skin.get(TextButton.TextButtonStyle.class);
            TextButton.TextButtonStyle customStyle = new TextButton.TextButtonStyle(baseStyle);
            customStyle.up = whiteBackground;
            customStyle.over = hoverBackground;
            customStyle.down = hoverBackground;

            customStyle.fontColor = new Color(0.2f,0.6f,0.2f,1f);
            customStyle.overFontColor = Color.WHITE;
            customStyle.downFontColor = Color.WHITE;

            TextButton btn = new TextButton(g.name, customStyle);
            btn.getLabel().setWrap(true);
            btn.getLabel().setAlignment(Align.left);
            btn.getLabel().setFontScale(0.85f);
            btn.padLeft(15);

            btn.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Vector2 targetPos = MapRasterTiles.getPixelPosition(g.latitude, g.longitude,
                            rasterMap.beginTile.x, rasterMap.beginTile.y);

                    rasterMap.zoomToMarker(targetPos, 0.5f);
                }
            });

            scrollContent.add(btn).expandX().fillX().minHeight(52).row();
        }
    }


}
