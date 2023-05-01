package me.datafox.ticktacktoe.api;

import lombok.*;

import java.util.TreeMap;

/**
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LobbyDto {
    private String id;

    private String name;

    private int width;

    private int height;

    private int winCondition;

    private int playerCount;

    private boolean fallMode;

    private PlayerDto host;

    private TreeMap<String, PlayerDto> players;

    private boolean started;
}