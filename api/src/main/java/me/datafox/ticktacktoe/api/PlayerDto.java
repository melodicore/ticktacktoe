package me.datafox.ticktacktoe.api;


import lombok.*;

/**
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PlayerDto {
    private String username;

    private String nickname;

    private String password;

    private ColorDto color;
}
