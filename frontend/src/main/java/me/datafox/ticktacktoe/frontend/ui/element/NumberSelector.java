package me.datafox.ticktacktoe.frontend.ui.element;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Disableable;
import lombok.Getter;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.utils.Defaults;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * @author datafox
 */
public class NumberSelector extends Table implements Disableable {
    private final GameTextButton decrease;
    private final GameTextButton increase;
    private final GameLabel indicator;
    private final Set<Consumer<Integer>> listeners;
    @Getter private int min;
    @Getter private int max;
    @Getter private int value;
    private int oldValue;
    private boolean disabled;

    public NumberSelector(int min, int max, int value) {
        decrease = new GameTextButton("<", Game.ui().getSmallFont());
        increase = new GameTextButton(">", Game.ui().getSmallFont());
        indicator = new GameLabel(Integer.toString(value), Game.ui().getFont());
        listeners = new HashSet<>();
        this.min = min;
        this.max = max;
        this.value = value;
        oldValue = value;
        disabled = false;

        defaults().fill().expand().space(Defaults.TABLE_SPACING);

        decrease.addListener(UiUtils.simpleChangeListener(this::decrease));
        increase.addListener(UiUtils.simpleChangeListener(this::increase));

        add(decrease, indicator, increase);

        refresh();
    }

    public void setMin(int min) {
        if(min > max) throw new RuntimeException();
        this.min = min;
        refresh();
    }

    public void setMax(int max) {
        if(max < min) throw new RuntimeException();
        this.max = max;
        refresh();
    }

    public void setValue(int value) {
        if(value < min || value > max) throw new RuntimeException();
        this.value = value;
        refresh();
    }

    public void addListener(Consumer<Integer> listener) {
        listeners.add(listener);
    }

    private void decrease() {
        if(value > min) {
            value--;
        }
        refresh();
    }

    private void increase() {
        if(value < max) {
            value++;
        }
        refresh();
    }

    private void refresh() {
        value = Math.max(Math.min(value, max), min);
        decrease.setDisabled(value == min || disabled);
        increase.setDisabled(value == max || disabled);
        indicator.setText(Integer.toString(value));
        if(value != oldValue) listeners.forEach(l -> l.accept(value));
        oldValue = value;
    }

    @Override
    public void setDisabled(boolean isDisabled) {
        disabled = isDisabled;
        refresh();
    }

    @Override
    public boolean isDisabled() {
        return disabled;
    }
}
