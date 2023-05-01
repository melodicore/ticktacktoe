package me.datafox.ticktacktoe.backend.model;

import lombok.*;
import me.datafox.ticktacktoe.api.MoveDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author datafox
 */
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Game {
    @EqualsAndHashCode.Include
    @Id
    private String id;

    private long timestamp;

    private String name;

    @Builder.Default
    private boolean finished = false;

    private int width;

    private int height;

    private int winCondition;

    private boolean fallMode;

    private String[] board;

    @Builder.Default
    private ArrayList<MoveDto> moves = new ArrayList<>();

    @DBRef
    @Builder.Default
    private HashMap<String, Player> players = new HashMap<>();

    @DBRef
    private Player currentPlayer;
}
