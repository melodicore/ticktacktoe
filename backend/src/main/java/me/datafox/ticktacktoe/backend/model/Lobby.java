package me.datafox.ticktacktoe.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.HashMap;

/**
 * @author datafox
 */
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Lobby {
    @EqualsAndHashCode.Include
    @Id
    private String id;

    private String name;

    @Builder.Default
    private int width = 3;

    @Builder.Default
    private int height = 3;

    @Builder.Default
    private int winCondition = 3;

    @Builder.Default
    private int playerCount = 2;

    @Builder.Default
    private boolean fallMode = false;

    @DBRef
    private Player host;

    @DBRef
    @Builder.Default
    private HashMap<String, Player> players = new HashMap<>();

    @Builder.Default
    private boolean started = false;

    @Builder.Default
    private boolean full = false;
}
