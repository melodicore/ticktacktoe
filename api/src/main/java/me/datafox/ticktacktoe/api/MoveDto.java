package me.datafox.ticktacktoe.api;

import lombok.*;

/**
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MoveDto {
    private int x;

    private int y;

    private String symbol;
}
