package xyz.bobkinn.indigodataio;

import lombok.RequiredArgsConstructor;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Data holder builder for simple chaining
 * @param <T> holder impl
 * @param <P> holder base data type
 */
@RequiredArgsConstructor
public class MapBuilder<T extends DataHolder<T, P>, P> {
    private final MapBuilder<T, P> parent;
    private final T holder;
    private final Supplier<T> creator;
    private final Function<Number, P> numberConv;
    private final Function<Boolean, P> boolConv;
    private final Function<String, P> strConv;

    /**
     * Put section on current level
     * @param key key
     * @param value value
     * @return builder on this level
     */
    public MapBuilder<T, P> put(String key, T value){
        holder.putSection(key, value);
        return this;
    }

    /**
     * Put value on current level
     * @param key key
     * @param value value
     * @return builder on this level
     */
    public MapBuilder<T, P> put(String key, P value){
        holder.put(key, value);
        return this;
    }

    /**
     * Put number on current level
     * @param key key
     * @param value value
     * @return builder on this level
     */
    public MapBuilder<T, P> put(String key, Number value){
        holder.put(key, numberConv.apply(value));
        return this;
    }

    /**
     * Put boolean on current level
     * @param key key
     * @param value value
     * @return builder on this level
     */
    public MapBuilder<T, P> put(String key, Boolean value){
        holder.put(key, boolConv.apply(value));
        return this;
    }

    /**
     * Put string on current level
     * @param key key
     * @param value value
     * @return builder on this level
     */
    public MapBuilder<T, P> put(String key, String value){
        holder.put(key, strConv.apply(value));
        return this;
    }

    /**
     * Goes down to one level by creating nested map
     * @param name key
     * @return builder on new level
     */
    public MapBuilder<T, P> down(String name){
        var map = creator.get();
        this.holder.putSection(name, map);
        return new MapBuilder<>(this, map, creator, numberConv, boolConv, strConv);
    }

    /**
     * Goes up to one level or not if this level is top
     * @return parent level if exists, else this level
     */
    public MapBuilder<T, P> up(){
        return parent != null ? parent : this;
    }

    /**
     * @return top level holder
     */
    public T build(){
        var root = this;
        while (root.parent != null){
            root = root.parent;
        }
        return root.holder;
    }
}
