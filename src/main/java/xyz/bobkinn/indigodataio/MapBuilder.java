package xyz.bobkinn.indigodataio;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * NestedKeyMap builder for simple chaining
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class MapBuilder {
    private final MapBuilder parent;
    private final NestedKeyMap map;

    /**
     * Creates a new, empty builder
     * @return new builder
     */
    @Contract(" -> new")
    public static @NotNull MapBuilder newBuilder(){
        return new MapBuilder(null, new NestedKeyMap());
    }

    /**
     * Put value on current level
     * @param key key
     * @param value value
     * @return builder on this level
     */
    public MapBuilder put(String key, Object value){
        map.put(key, value);
        return this;
    }

    /**
     * Goes down to one level by creating nested map
     * @param name key
     * @return builder on new level
     */
    public MapBuilder down(String name){
        var map = new NestedKeyMap();
        this.map.put(name, map);
        return new MapBuilder(this, map);
    }

    /**
     * Goes up to one level or not if this level is top
     * @return parent level if exists, else this level
     */
    public MapBuilder up(){
        return parent != null ? parent : this;
    }

    /**
     * Gathers map from top level
     * @return resulting map
     */
    public NestedKeyMap build(){
        var root = this;
        while (root.parent != null){
            root = root.parent;
        }
        return root.map;
    }
}
