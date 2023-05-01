package me.datafox.ticktacktoe.frontend.ui;

import com.badlogic.gdx.math.MathUtils;
import make.some.noise.Noise;
import me.datafox.ticktacktoe.frontend.Game;

import java.util.LinkedList;
import java.util.function.Supplier;

/**
 * @author datafox
 */
public class NoiseBuilder {
    public static NoiseBuilder get() {
        return new NoiseBuilder();
    }

    private final LinkedList<Operation> operations;
    private Supplier<Float> evolution;
    private Supplier<Float> speed;
    private int seed;
    private NoiseBuilder() {
        operations = new LinkedList<>();
        evolution = Game.ui()::getEvolution;
        speed = () -> 0.2f;
        seed = Game.rng().nextInt();
    }

    public NoiseBuilder evolution(Supplier<Float> evolution) {
        this.evolution = evolution;
        return this;
    }

    public NoiseBuilder speed(float speed) {
        return speed(() -> speed);
    }

    public NoiseBuilder speed(Supplier<Float> speed) {
        this.speed = speed;
        return this;
    }

    public NoiseBuilder seed(int seed) {
        this.seed = seed;
        return this;
    }

    public NoiseBuilder lerp(float min, float max) {
        return lerp(() -> min, () -> max);
    }

    public NoiseBuilder lerp(Supplier<Float> min, Supplier<Float> max) {
        operations.add(new LerpOperation(min, max));
        return this;
    }

    public NoiseBuilder multiply(float multiplier) {
        return multiply(() -> multiplier);
    }

    public NoiseBuilder multiply(Supplier<Float> multiplier) {
        operations.add(new MultiplyOperation(multiplier));
        return this;
    }

    public Supplier<Float> build() {
        return () -> {
            float evo = evolution.get() * speed.get();
            final float[] f = {Noise.instance.singleSimplex(seed, evo, evo)};
            operations.forEach(o -> f[0] = o.apply(f[0]));
            return f[0];
        };
    }

    private interface Operation {
        float apply(float value);
    }

    private record LerpOperation(Supplier<Float> min, Supplier<Float> max) implements Operation {
        @Override
        public float apply(float value) {
            float max = this.max.get();
            float mid = (min.get() + max) / 2;
            return MathUtils.lerp(mid, max, value);
        }
    }

    private record MultiplyOperation(Supplier<Float> multiplier) implements Operation {
        @Override
        public float apply(float value) {
            return value * multiplier.get();
        }
    }
}
