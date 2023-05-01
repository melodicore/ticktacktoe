package me.datafox.ticktacktoe.backend.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author datafox
 */
@Service
public class MapService {
    public <K, V> Map<K,V> reverseMap(Map<V,K> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
    }
}
