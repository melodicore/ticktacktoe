package me.datafox.ticktacktoe.frontend.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.DistanceFieldFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import lombok.AccessLevel;
import lombok.Getter;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.element.MainPane;
import me.datafox.ticktacktoe.frontend.ui.screen.Screens;
import me.datafox.ticktacktoe.frontend.utils.ColorUtils;
import me.datafox.ticktacktoe.frontend.utils.Defaults;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.prefs.BackingStoreException;

/**
 * @author datafox
 */
@Getter
public class UiHandler {
    private static final Map<Integer,Float> QUALITY = Map.of(1, 0.1f, 2, 0.5f, 3, 2f, 4, 5f);

    private final Batch batch;
    private final TextureRegion white;
    private final ShapeDrawer drawer;
    private final Viewport viewport;
    private final DistanceFieldFont font;
    private final DistanceFieldFont smallFont;
    private final DistanceFieldFont titleFont;
    private final Stage stage;
    private final MainPane pane;
    @Getter(AccessLevel.NONE) private final Color actualColor;
    private final Supplier<Color> color;
    private final Set<Action> actions;
    private float evolution;
    private int quality;

    public UiHandler() {
        batch = new SpriteBatch();
        batch.setShader(createDistanceFieldShader());
        white = createWhite();
        drawer = new ShapeDrawer(batch, white);
        viewport = new ExtendViewport(Defaults.WORLD_MIN_WIDTH, Defaults.WORLD_MIN_HEIGHT);
        font = createFont();
        smallFont = createSmallFont();
        titleFont = createTitleFont();
        stage = new Stage(viewport, batch);
        pane = new MainPane();
        actualColor = new Color(ColorUtils.random().get());
        color = ColorUtils.of(actualColor);
        actions = new HashSet<>();
        quality = Game.prefs().getInt("quality", 3);

        Gdx.input.setInputProcessor(stage);

        Gdx.app.postRunnable(() -> pane.setScreen(Screens.Title));

        stage.addActor(pane);
    }

    private ShaderProgram createDistanceFieldShader() {
        return new ShaderProgram(Gdx.files.internal("vertex.glsl"),
                Gdx.files.internal("fragment.glsl"));
    }

    private DistanceFieldFont createFont() {
        Texture texture = new Texture(Gdx.files.internal("kenyancoffee.png"));
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        return new DistanceFieldFont(Gdx.files.internal("kenyancoffee.fnt"), new TextureRegion(texture), false);
    }

    private DistanceFieldFont createSmallFont() {
        Texture texture = new Texture(Gdx.files.internal("kenyancoffeesmall.png"));
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        return new DistanceFieldFont(Gdx.files.internal("kenyancoffeesmall.fnt"), new TextureRegion(texture), false);
    }

    private DistanceFieldFont createTitleFont() {
        Texture texture = new Texture(Gdx.files.internal("youregone.png"));
        texture.setFilter(Texture.TextureFilter.MipMapLinearNearest, Texture.TextureFilter.Linear);
        return new DistanceFieldFont(Gdx.files.internal("youregone.fnt"), new TextureRegion(texture), false);
    }

    private TextureRegion createWhite() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        TextureRegion white = new TextureRegion(new Texture(pixmap));
        pixmap.dispose();
        return white;
    }

    public void setColor(Color color) {
        if(color == null) return;
        actions.clear();
        actions.add(new LerpColorAction(actualColor.cpy(), color, 2));
    }

    public void randomColor() {
        actualColor.set(ColorUtils.random().get());
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
        drawer.updatePixelSize();
        drawer.setPixelSize(drawer.getPixelSize() / QUALITY.get(quality));
        font.setDistanceFieldSmoothing(3 * getScale());
        smallFont.setDistanceFieldSmoothing(1.5f * getScale());
        titleFont.setDistanceFieldSmoothing(7 * getScale());
    }

    public void updateAndRender(float delta) {
        evolution += delta;
        stage.act(delta);
        processActions(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT |
                (Gdx.graphics.getBufferFormat().coverageSampling ? GL20.GL_COVERAGE_BUFFER_BIT_NV : 0));
        stage.draw();
    }

    private void processActions(float delta) {
        actions.stream().filter(a -> a.act(delta)).toList().forEach(actions::remove);
    }

    public void setQuality(int quality) {
        quality = Math.max(Math.min(quality, 4), 1);
        this.quality = quality;
        drawer.updatePixelSize();
        drawer.setPixelSize(drawer.getPixelSize() / QUALITY.get(quality));
    }

    public float getWorldWidth() {
        return viewport.getWorldWidth();
    }

    public float getWorldHeight() {
        return viewport.getWorldHeight();
    }

    public float getScale() {
        return viewport.getScreenWidth() / viewport.getWorldWidth();
    }

    public float getRatio() {
        return viewport.getWorldWidth() / viewport.getWorldHeight();
    }

    public void saveSettings() {
        Game.prefs().putInt("quality", quality);
        try {
            Game.prefs().flush();
        } catch(BackingStoreException ignored) {}
    }

    private class LerpColorAction extends Action {
        private final Color before;
        private final Color after;
        private final float time;
        private float progress;

        private LerpColorAction(Color before, Color after, float time) {
            this.before = before;
            this.after = after;
            this.time = time;
            progress = 0;
        }
        @Override
        public boolean act(float delta) {
            progress += delta / time;
            if(progress >= 1) {
                actualColor.set(after);
                return true;
            }
            actualColor.set(before).lerp(after, progress);
            return false;
        }
    }
}
