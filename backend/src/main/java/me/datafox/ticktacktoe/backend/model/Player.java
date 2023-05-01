package me.datafox.ticktacktoe.backend.model;

import lombok.*;
import me.datafox.ticktacktoe.api.ColorDto;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
import java.util.List;

/**
 * @author datafox
 */
@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Player {
    @EqualsAndHashCode.Include
    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    private String nickname;

    private String password;

    private ColorDto color;

    @DBRef
    @Builder.Default
    private List<Game> games = new ArrayList<>();

    @DBRef
    @Builder.Default
    private List<Role> roles = new ArrayList<>();
}
