package me.datafox.ticktacktoe.api;

import lombok.*;

/**
 * A simple color class with red, green and blue floating point values for easy conversion with libGdx Color class
 *
 * @author datafox
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ColorDto {
    private float r, g, b;
}
