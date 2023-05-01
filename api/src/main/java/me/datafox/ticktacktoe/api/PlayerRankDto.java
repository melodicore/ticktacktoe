package me.datafox.ticktacktoe.api;

import lombok.*;

/**
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerRankDto {
    private PlayerDto player;

    private long rank;

    private long wins;

    private long losses;

    private long draws;

    private float ratio;
}
