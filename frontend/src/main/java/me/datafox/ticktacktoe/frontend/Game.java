package me.datafox.ticktacktoe.frontend;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import lombok.Getter;
import me.datafox.ticktacktoe.api.PlayerDto;
import me.datafox.ticktacktoe.frontend.connection.ConnectionManager;
import me.datafox.ticktacktoe.frontend.connection.GameHandler;
import me.datafox.ticktacktoe.frontend.connection.LobbyHandler;
import me.datafox.ticktacktoe.frontend.ui.UiHandler;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.prefs.Preferences;

/**
 * @author datafox
 */
@Getter
public class Game implements ApplicationListener {
    private static Game instance;

    public static Preferences prefs() {
        return instance.preferences;
    }

    public static ScheduledExecutorService scheduler() {
        return instance.scheduler;
    }

    public static Json json() {
        return instance.json;
    }

    public static Random rng() {
        return instance.rng;
    }

    public static UiHandler ui() {
        return instance.ui;
    }

    public static ConnectionManager con() {
        return instance.con;
    }

    public static PlayerDto player() {
        return instance.player;
    }

    public static void player(PlayerDto player) {
        instance.player = player;
    }

    public static LobbyHandler lobby() {
        return instance.lobby;
    }

    public static GameHandler game() {
        return instance.game;
    }

    private Preferences preferences;
    private ScheduledExecutorService scheduler;
    private Json json;
    private Random rng;
    private UiHandler ui;
    private ConnectionManager con;
    private PlayerDto player;
    private LobbyHandler lobby;
    private GameHandler game;

    @Override
    public void create() {
        instance = this;
        preferences = Preferences.userRoot().node("ticktacktoe");
        scheduler = Executors.newSingleThreadScheduledExecutor();
        json = new Json(JsonWriter.OutputType.json);
        json.setIgnoreUnknownFields(true);
        rng = new Random();
        ui = new UiHandler();
        con = new ConnectionManager();
        lobby = new LobbyHandler();
        game = new GameHandler();
    }

    @Override
    public void resize(int width, int height) {
        ui.resize(width, height);
    }

    @Override
    public void render() {
        ui.updateAndRender(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        con.disconnect();
        scheduler.shutdownNow();
        System.exit(0);
    }
}