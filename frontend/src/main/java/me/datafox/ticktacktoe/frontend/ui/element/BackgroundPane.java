package me.datafox.ticktacktoe.frontend.ui.element;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import me.datafox.ticktacktoe.api.Symbols;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.ColorBuilder;
import me.datafox.ticktacktoe.frontend.utils.Defaults;
import me.datafox.ticktacktoe.frontend.utils.DrawingUtils;
import space.earlygrey.shapedrawer.ShapeDrawer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * @author datafox
 */
public class BackgroundPane extends Table {
    private final Random rng;
    private final List<BackgroundParticle> particles;
    private final float rate;
    private float adjustedRate;
    private float timer;

    public BackgroundPane(float rate) {
        super();
        rng = Game.rng();
        particles = new ArrayList<>();
        this.rate = rate;
        adjustedRate = rate;
        timer = 0;

        Gdx.app.postRunnable(() -> background(new TextureRegionDrawable(Game.ui().getWhite())));
        setFillParent(true);

        Gdx.app.postRunnable(() -> {
            int it = (int) ((1 / rate) * 10);
            for(int i=0;i<it;i++) createInitialParticle();
        });
    }

    private void createParticle(float x) {
        float scale = rng.nextFloat() * rng.nextFloat();
        float size = scale * 250 + 50;
        Vector2 location = new Vector2(x + size, rng.nextFloat(Math.max(getY() + getHeight(), 0)));
        float speed = (rng.nextFloat() + scale / 2) * 100 + 5;
        float rotation = (float) rng.nextGaussian() * (1 - scale) / 10;
        if(rotation > 0) rotation += 0.1f;
        else rotation -= 0.1f;
        float angle = rng.nextFloat(Defaults.PI * 2);
        float multiplier = rng.nextFloat(0.8f, 1.25f);
        float saturation = rng.nextFloat(0.75f, 1.2f);
        BackgroundParticle particle = new BackgroundParticle(Symbols.ALL[rng.nextInt(2)], location, size, speed, rotation, angle, multiplier, saturation);
        particles.add(particle);
        adjustedRate = rate * (1 + scale * 2.5f) * Math.min(Game.ui().getRatio(), 1);
    }

    private void createParticle() {
        createParticle(getX() + getWidth());
    }

    private void createInitialParticle() {
        createParticle(rng.nextFloat(getX(), getWidth()));
    }

    @Override
    public void act(float delta) {
        timer += delta;
        if(timer >= adjustedRate) {
            timer -= adjustedRate;
            createParticle();
        }
        setColor(Game.ui().getColor().get());
        super.act(delta);
        particles.forEach(p -> p.act(delta));
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        particles.forEach(p -> p.draw(Game.ui().getDrawer()));
    }

    private class BackgroundParticle {
        private final String symbol;
        private final Vector2 location;
        private final float size;
        private final float speed;
        private final float rotation;
        private float angle;
        private final Supplier<Color> color;

        private BackgroundParticle(String symbol, Vector2 location, float size, float speed,
                                   float rotation, float angle, float multiplier, float saturation) {
            this.symbol = symbol;
            this.location = location;
            this.size = size;
            this.speed = speed;
            this.rotation = rotation;
            this.angle = angle;
            color = ColorBuilder.def().highlight().multiply(multiplier).desaturate(saturation).build();
        }


        private void act(float delta) {
            location.sub(speed * delta, 0);
            angle += rotation * delta;
            if(location.x < -size || location.x > BackgroundPane.this.getX() + BackgroundPane.this.getWidth() + (size * 2) ||
               location.y < -size || location.y > BackgroundPane.this.getY() + BackgroundPane.this.getHeight() + size)
                Gdx.app.postRunnable(() -> particles.remove(this));
        }

        private void draw(ShapeDrawer drawer) {
            DrawingUtils.drawSymbol(drawer, symbol, location, size, angle, color.get(), true);
        }
    }
}
