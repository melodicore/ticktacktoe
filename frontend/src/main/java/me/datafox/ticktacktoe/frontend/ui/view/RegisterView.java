package me.datafox.ticktacktoe.frontend.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import me.datafox.ticktacktoe.api.PlayerDto;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.element.GameLabel;
import me.datafox.ticktacktoe.frontend.ui.element.GameTextButton;
import me.datafox.ticktacktoe.frontend.ui.element.GameTextField;
import me.datafox.ticktacktoe.frontend.ui.screen.TitleScreen;
import me.datafox.ticktacktoe.frontend.utils.ColorUtils;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;
import me.datafox.ticktacktoe.frontend.connection.EmptyCallback;

import javax.swing.*;

/**
 * @author datafox
 */
public class RegisterView extends View {
    private final GameLabel usernameLabel;
    private final GameTextField username;
    private final GameLabel nicknameLabel;
    private final GameTextField nickname;
    private final GameLabel passwordLabel;
    private final GameTextField password;
    private final GameTextButton color;
    private final GameTextButton register;
    private final GameTextButton cancel;

    public RegisterView(TitleScreen parent) {
        super(parent);
        usernameLabel = new GameLabel("Username:");
        username = new GameTextField("", GameTextField.usernameValidator());
        nicknameLabel = new GameLabel("Nickname:");
        nickname = new GameTextField("", GameTextField.freeformValidator());
        passwordLabel = new GameLabel("Password:");
        password = new GameTextField("", '*', GameTextField.passwordValidator());
        color = new GameTextButton("Select color");
        register = new GameTextButton("Register account");
        cancel = new GameTextButton("Cancel");

        color.addListener(UiUtils.simpleChangeListener(this::selectColor));
        register.addListener(UiUtils.simpleChangeListener(this::register));
        cancel.addListener(UiUtils.simpleChangeListener(() -> parent.setView(Views.Login)));

        add(usernameLabel);
        add(username).row();
        add(nicknameLabel);
        add(nickname).row();
        add(passwordLabel);
        add(password).row();
        add(color).colspan(2).row();
        add(register).colspan(2).row();
        add(cancel).colspan(2);
    }

    private void selectColor() {
        Game.scheduler().execute(() -> {
            java.awt.Color current = ColorUtils.toAwt(
                    ColorUtils.toRealColor(
                            Game.ui().getColor().get()));
            Color color = ColorUtils.toAccentColor(
                    ColorUtils.toGdx(
                            JColorChooser.showDialog(null, "Select a color", current, false)));
            Gdx.app.postRunnable(() -> Game.ui().setColor(color));
        });
    }

    private void register() {
        if(username.getText().isBlank()) {
            parent.setMessage("Username cannot be empty");
            return;
        }
        if(nickname.getText().isBlank()) {
            parent.setMessage("Nickname cannot be empty");
            return;
        }
        if(password.getText().isBlank()) {
            parent.setMessage("Password cannot be empty");
            return;
        }
        PlayerDto player = PlayerDto
                .builder()
                .username(username.getText())
                .nickname(nickname.getText())
                .password(password.getText())
                .color(ColorUtils.toDto(Game.ui().getColor().get()))
                .build();
        Game.con().post("/register", player, EmptyCallback
                .builder()
                .completed(this::registered)
                .failed(parent::setMessage).build());
    }

    private void registered() {
        Gdx.app.postRunnable(() -> {
            parent.setMessage("Registration successful, please log in");
            parent.setView(Views.Login);
        });
    }
}
