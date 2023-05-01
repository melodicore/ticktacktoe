package me.datafox.ticktacktoe.frontend.utils;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author datafox
 */
public class MapUtils {
    public static <K, V> Map<K,V> reverseMap(Map<V,K> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }
}
