package me.datafox.ticktacktoe.frontend.ui.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Align;
import me.datafox.ticktacktoe.api.PlayerDto;
import me.datafox.ticktacktoe.frontend.Game;
import me.datafox.ticktacktoe.frontend.connection.ConnectionCallback;
import me.datafox.ticktacktoe.frontend.ui.element.GameLabel;
import me.datafox.ticktacktoe.frontend.ui.element.GameTextButton;
import me.datafox.ticktacktoe.frontend.ui.element.GameTextField;
import me.datafox.ticktacktoe.frontend.ui.element.NumberSelector;
import me.datafox.ticktacktoe.frontend.ui.screen.TitleScreen;
import me.datafox.ticktacktoe.frontend.utils.ColorUtils;
import me.datafox.ticktacktoe.frontend.utils.UiUtils;

import javax.swing.*;

/**
 * @author datafox
 */
public class SettingsView extends View {
    private final GameLabel qualityLabel;
    private final NumberSelector quality;
    private final GameLabel nicknameLabel;
    private final GameTextField nickname;
    private final GameLabel passwordLabel;
    private final GameTextField password;
    private final GameTextButton color;
    private final GameTextButton save;
    private final GameTextButton ignore;
    private int originalQuality;
    private Color originalColor;

    public SettingsView(TitleScreen parent) {
        super(parent);
        qualityLabel = new GameLabel("Graphics quality:");
        quality = new NumberSelector(1, 4, Game.ui().getQuality());
        nicknameLabel = new GameLabel("Nickname:");
        nickname = new GameTextField(Game.player().getNickname(), GameTextField.freeformValidator());
        passwordLabel = new GameLabel("Password:");
        password = new GameTextField(Game.player().getPassword(), GameTextField.passwordValidator());
        color = new GameTextButton("Change color");
        save = new GameTextButton("Apply settings");
        ignore = new GameTextButton("Return without applying");
        originalQuality = Game.ui().getQuality();
        originalColor = Game.ui().getColor().get();

        qualityLabel.setAlignment(Align.right);
        nicknameLabel.setAlignment(Align.right);
        passwordLabel.setAlignment(Align.right);

        quality.addListener(this::setQuality);
        color.addListener(UiUtils.simpleChangeListener(this::selectColor));
        save.addListener(UiUtils.simpleChangeListener(this::save));

        add(qualityLabel).align(Align.right);
        add(quality);
        add().row();
        add(nicknameLabel).align(Align.right);
        add(nickname).colspan(2).row();
        add(passwordLabel).align(Align.right);
        add(password).colspan(2).row();
        add(color).colspan(3).row();
        add(save).colspan(3).row();
        add(ignore).colspan(3);
    }

    private void setQuality(int quality) {
        Game.ui().setQuality(quality);
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

    private void save() {
        if(nickname.getText().isBlank()) {
            parent.setMessage("Nickname cannot be empty");
            return;
        }
        PlayerDto.PlayerDtoBuilder builder = PlayerDto
                .builder()
                .nickname(nickname.getText())
                .color(ColorUtils.toDto(Game.ui().getColor().get()));
        if(!password.getText().isBlank()) builder.password(password.getText());

        Game.con().post("/modify", builder.build(), ConnectionCallback
                .builder(PlayerDto.class)
                .completed(this::applied)
                .failed(parent::setMessage).build());
        Game.ui().saveSettings();
        parent.setView(Views.MainMenu);
    }

    private void applied(PlayerDto player) {
        Game.player(player);
    }

    private void ignore() {
        Game.ui().setQuality(originalQuality);
        Game.ui().setColor(originalColor);
        parent.setView(Views.MainMenu);
    }
}
