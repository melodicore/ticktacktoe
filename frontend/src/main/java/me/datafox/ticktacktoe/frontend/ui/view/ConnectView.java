package me.datafox.ticktacktoe.frontend.ui.view;

import com.badlogic.gdx.Gdx;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.ui.element.GameLabel;
import me.datafox.ticktacktoe.frontend.ui.element.GameTextButton;
import me.datafox.ticktacktoe.frontend.ui.element.GameTextField;
import me.datafox.ticktacktoe.frontend.ui.screen.TitleScreen;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;
import me.datafox.ticktacktoe.frontend.connection.EmptyCallback;

import java.util.prefs.BackingStoreException;

/**
 * @author datafox
 */
public class ConnectView extends View {
    private final GameLabel addressLabel;
    private final GameTextField address;
    private final GameTextButton connect;

    public ConnectView(TitleScreen parent) {
        super(parent);
        addressLabel = new GameLabel("Server address:");
        address = new GameTextField(Game.prefs().get("host", "localhost"), GameTextField.addressValidator());
        connect = new GameTextButton("Connect");

        connect.addListener(UiUtils.simpleChangeListener(this::connect));

        add(addressLabel);
        add(address).row();
        add(connect).colspan(2);
    }

    private void connect() {
        Game.con().connect(address.getText(),
                EmptyCallback.builder()
                        .completed(this::connected)
                        .failed(parent::setMessage)
                        .build());
    }

    private void connected() {
        Game.prefs().put("host", address.getText());
        try {
            Game.prefs().flush();
        } catch(BackingStoreException ignored) {}
        Gdx.app.postRunnable(() -> parent.setView(Views.Login));
    }
}
