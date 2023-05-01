package me.datafox.ticktacktoe.frontend.ui.element;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import lombok.Getter;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.drawable.CursorDrawable;
import me.datafox.ticktacktoe.frontend.drawable.SelectionDrawable;
import me.datafox.ticktacktoe.frontend.utils.Defaults;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;

import java.util.LinkedList;
import java.util.List;

/**
 * @author datafox
 */
public class GameTextField extends BaseTable {
    public static ValidatorBuilder validator() {
        return new ValidatorBuilder();
    }

    public static Validator usernameValidator() {
        return validator().regex("[^a-zA-Z0-9]").length(16).lowercase().build();
    }

    public static Validator freeformValidator() {
        return validator().regex("[^ -~¡-¥¨-«®°²³´·-ÿ]").length(16).build();
    }

    public static Validator passwordValidator() {
        return validator().length(64).build();
    }

    public static Validator addressValidator() {
        return validator().regex("[^a-zA-Z\\-.0-:]").build();
    }

    @Getter private final TextField field;
    private final Validator validator;
    private final Drawable over;
    private final Drawable disabled;
    private boolean mouseOver;

    public GameTextField(String text, Validator validator) {
        super();
        padTop(Defaults.TABLE_SPACING);
        padBottom(Defaults.TABLE_SPACING);
        field = new TextField(text, new TextField.TextFieldStyle(Game.ui().getFont(), Color.WHITE,
                new CursorDrawable(), new SelectionDrawable(), null));
        this.validator = validator;
        over = Defaults.lighterFilledFrame();
        disabled = Defaults.desaturatedFilledFrame();
        mouseOver = false;

        if(validator != null) field.addListener(UiUtils.simpleChangeListener(this::validateText));

        add(field);
        addListener(new MouseOverListener());
        addAction(new MouseOverAction());
    }

    public GameTextField(String text, char password, Validator validator) {
        this(text, validator);
        getField().setPasswordMode(true);
        getField().setPasswordCharacter(password);
        getField().setText(text);
    }

    private void validateText() {
        int c = field.getCursorPosition();
        field.setText(validator.validate(field.getText()));
        field.setCursorPosition(c);
    }

    public String getText() {
        return getField().getText();
    }

    public interface Validator {
        String validate(String input);
    }

    public static class ValidatorBuilder {
        private String regex;
        private int length;
        private boolean lowercase;

        private ValidatorBuilder() {
            regex = null;
            length = -1;
            lowercase = false;
        }

        public ValidatorBuilder regex(String regex) {
            this.regex = regex;
            return this;
        }

        public ValidatorBuilder length(int length) {
            this.length = length;
            return this;
        }

        public ValidatorBuilder lowercase() {
            lowercase = true;
            return this;
        }

        public Validator build() {
            List<Validator> parts = new LinkedList<>();
            if(regex != null) parts.add(buildRegex());
            if(length >= 0) parts.add(buildLength());
            if(lowercase) parts.add(buildLowercase());
            if(parts.isEmpty()) return s -> s;
            if(parts.size() == 1) return parts.get(0);
            return s -> {
                for(Validator v : parts) s = v.validate(s);
                return s;
            };
        }

        private Validator buildRegex() {
            return s -> s.replaceAll(regex, "");
        }

        private Validator buildLength() {
            return s -> s.substring(0, Math.min(length, s.length()));
        }

        private Validator buildLowercase() {
            return String::toLowerCase;
        }
    }

    private class MouseOverListener extends ClickListener {
        @Override
        public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
            mouseOver = true;
        }

        @Override
        public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
            mouseOver = false;
        }
    }

    private class MouseOverAction extends Action {
        @Override
        public boolean act(float delta) {
            if(field.isDisabled()) background(disabled);
            else if(mouseOver) background(over);
            else background(getDefaultBackground());
            return false;
        }
    }
}
