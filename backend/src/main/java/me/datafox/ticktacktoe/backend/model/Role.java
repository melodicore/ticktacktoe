package me.datafox.ticktacktoe.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
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
public class Role {
    @EqualsAndHashCode.Include
    @Id
    private String id;

    @DBRef
    @Builder.Default
    private List<Player> players = new ArrayList<>();
}
