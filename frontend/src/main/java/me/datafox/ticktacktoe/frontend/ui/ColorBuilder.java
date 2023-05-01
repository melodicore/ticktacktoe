package me.datafox.ticktacktoe.frontend.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import me.datafox.ticktacktoe.frontend.Game;

import java.util.LinkedList;
import java.util.function.Supplier;

/**
 * @author datafox
 */
public class ColorBuilder {
    public static ColorBuilder def() {
        return new ColorBuilder(Game.ui().getColor());
    }

    public static ColorBuilder of(Color color) {
        return new ColorBuilder(() -> color);
    }

    public static ColorBuilder of(Supplier<Color> color) {
        return new ColorBuilder(color);
    }

    private final Supplier<Color> color;
    private final LinkedList<Operation> operations;

    private ColorBuilder(Supplier<Color> color) {
        this.color = color;
        operations = new LinkedList<>();
    }

    public ColorBuilder lighten() {
        return add(0.1f);
    }

    public ColorBuilder darken() {
        return multiply(0.75f);
    }

    public ColorBuilder add(float addend) {
        return add(() -> addend);
    }

    public ColorBuilder add(Supplier<Float> addend) {
        operations.add(new AddValueOperation(addend));
        return this;
    }

    public ColorBuilder multiply(float multiplier) {
        return multiply(() -> multiplier);
    }

    public ColorBuilder multiply(Supplier<Float> multiplier) {
        operations.add(new MultiplyValueOperation(multiplier));
        return this;
    }

    public ColorBuilder highlight() {
        return highlight(0.2f, 0.2f, 0.5f);
    }

    public ColorBuilder antiHighlight() {
        return highlight(-0.1f, -0.1f, 0.5f);
    }


    public ColorBuilder highlight(float darken, float lighten, float threshold) {
        return highlight(() -> darken, () -> lighten, () -> threshold);
    }

    public ColorBuilder highlight(Supplier<Float> darken, Supplier<Float> lighten, Supplier<Float> threshold) {
        operations.add(new HighlightOperation(darken, lighten, threshold));
        return this;
    }

    public ColorBuilder hue(float hue) {
        return hue(() -> hue);
    }

    public ColorBuilder hue(Supplier<Float> hue) {
        operations.add(new HueOperation(hue));
        return this;
    }

    public ColorBuilder transparent() {
        return transparent(0.75f);
    }

    public ColorBuilder transparent(float multiplier) {
        return transparent(() -> multiplier);
    }

    public ColorBuilder transparent(Supplier<Float> multiplier) {
        operations.add(new TransparencyOperation(multiplier));
        return this;
    }

    public ColorBuilder desaturate() {
        return desaturate(0.5f);
    }

    public ColorBuilder desaturate(float multiplier) {
        return desaturate(() -> multiplier);
    }

    public ColorBuilder desaturate(Supplier<Float> multiplier) {
        operations.add(new MultiplySaturationOperation(multiplier));
        return this;
    }

    public Supplier<Color> build() {
        return () -> {
            Color c = color.get().cpy();
            operations.forEach(o -> o.apply(c));
            return c;
        };
    }

    private interface Operation {
        void apply(Color color);
    }

    private record HueOperation(Supplier<Float> addend) implements Operation {
        @Override
        public void apply(Color color) {
            float[] hsv = color.toHsv(new float[3]);
            hsv[0] = (hsv[0] + addend.get()) % 360;
            color.fromHsv(hsv);
        }
    }

    private record MultiplySaturationOperation(Supplier<Float> multiplier) implements Operation {
        @Override
        public void apply(Color color) {
            float[] hsv = color.toHsv(new float[3]);
            hsv[1] = MathUtils.clamp(hsv[1] * multiplier.get(), 0, 1);
            color.fromHsv(hsv);
        }
    }

    private record AddSaturationOperation(Supplier<Float> addend) implements Operation {
        @Override
        public void apply(Color color) {
            float[] hsv = color.toHsv(new float[3]);
            hsv[1] = MathUtils.clamp(hsv[1] + addend.get(), 0, 1);
            color.fromHsv(hsv);
        }
    }

    private record MultiplyValueOperation(Supplier<Float> multiplier) implements Operation {
        @Override
        public void apply(Color color) {
            float[] hsv = color.toHsv(new float[3]);
            hsv[2] = MathUtils.clamp(hsv[2] * multiplier.get(), 0, 1);
            color.fromHsv(hsv);
        }
    }

    private record AddValueOperation(Supplier<Float> addend) implements Operation {
        @Override
        public void apply(Color color) {
            float[] hsv = color.toHsv(new float[3]);
            hsv[2] = MathUtils.clamp(hsv[2] + addend.get(), 0, 1);
            color.fromHsv(hsv);
        }
    }

    private record TransparencyOperation(Supplier<Float> multiplier) implements Operation {
        @Override
        public void apply(Color color) {
            color.mul(1, 1, 1, MathUtils.clamp(color.a * multiplier.get(), 0, 1));
        }
    }

    private record HighlightOperation(Supplier<Float> darken, Supplier<Float> lighten, Supplier<Float> threshold) implements Operation {
        @Override
        public void apply(Color color) {
            float[] hsv = color.toHsv(new float[3]);
            float addend;
            if(hsv[2] > threshold.get()) addend = -darken.get();
            else addend = lighten.get();
            hsv[2] = MathUtils.clamp(hsv[2] + addend, 0, 1);
            color.fromHsv(hsv);
        }
    }
}
