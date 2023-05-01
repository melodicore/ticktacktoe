package me.datafox.ticktacktoe.frontend.ui.view;

import com.badlogic.gdx.Gdx;
import me.datafox.ticktacktoe.api.PlayerDto;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.element.GameLabel;
import me.datafox.ticktacktoe.frontend.ui.element.GameTextButton;
import me.datafox.ticktacktoe.frontend.ui.element.GameTextField;
import me.datafox.ticktacktoe.frontend.ui.screen.TitleScreen;
import me.datafox.ticktacktoe.frontend.utils.ColorUtils;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;
import me.datafox.ticktacktoe.frontend.connection.ConnectionCallback;
import me.datafox.ticktacktoe.frontend.connection.EmptyCallback;

import java.util.prefs.BackingStoreException;

/**
 * @author datafox
 */
public class LoginView extends View {
    private final GameLabel usernameLabel;
    private final GameTextField username;
    private final GameLabel passwordLabel;
    private final GameTextField password;
    private final GameTextButton login;
    private final GameTextButton register;

    public LoginView(TitleScreen parent) {
        super(parent);
        usernameLabel = new GameLabel("Username:");
        username = new GameTextField(Game.prefs().get("username", ""), GameTextField.usernameValidator());
        passwordLabel = new GameLabel("Password:");
        password = new GameTextField("", '*', GameTextField.passwordValidator());
        login = new GameTextButton("Log in");
        register = new GameTextButton("Create new account");

        login.addListener(UiUtils.simpleChangeListener(this::login));
        register.addListener(UiUtils.simpleChangeListener(() -> parent.setView(Views.Register)));

        add(usernameLabel);
        add(username).row();
        add(passwordLabel);
        add(password).row();
        add(login).colspan(2).row();
        add(register).colspan(2);
    }

    private void login() {
        if(username.getText().isBlank()) {
            parent.setMessage("Username cannot be empty");
            return;
        }
        if(password.getText().isBlank()) {
            parent.setMessage("Password cannot be empty");
            return;
        }
        Game.con().login(username.getText(), password.getText(),
                EmptyCallback.builder()
                        .completed(this::loginSuccess)
                        .failed(parent::setMessage)
                        .response(401, "Invalid username or password")
                        .build());
    }

    private void loginSuccess() {
        Game.con().get("/who", ConnectionCallback
                .builder(PlayerDto.class)
                .completed(this::loginUser)
                .failed(parent::setMessage)
                .build());
    }

    private void loginUser(PlayerDto player) {
        Game.prefs().put("username", player.getUsername());
        try {
            Game.prefs().flush();
        } catch(BackingStoreException ignored) {}
        Gdx.app.postRunnable(() -> {
            Game.ui().setColor(ColorUtils.toGdx(player.getColor()));
            Game.player(player);
            parent.setMessage("Welcome, " + player.getNickname());
            parent.setView(Views.MainMenu);
        });
    }
}
