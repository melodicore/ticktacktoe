package me.datafox.ticktacktoe.api;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GameDto {
    private String id;

    private long timestamp;

    private String name;

    private boolean finished;

    private int width;

    private int height;

    private int winCondition;

    private boolean fallMode;

    private String[] board;

    private ArrayList<MoveDto> moves;

    private PlayerDto currentPlayer;

    private HashMap<String, PlayerDto> players;
}
