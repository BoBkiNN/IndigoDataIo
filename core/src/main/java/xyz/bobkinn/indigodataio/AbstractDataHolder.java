package xyz.bobkinn.indigodataio;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import xyz.bobkinn.indigodataio.ops.MapOps;

import java.util.AbstractMap;

public abstract class AbstractDataHolder<T extends DataHolder<T, P>, P, D extends P> extends AbstractMap<String, P> implements DataHolder<T, P> {

    /**
     * Get raw object used as storage in this holder
     * @return current storage
     */
    public abstract D getRaw();

    /**
     * @implSpec Must be new mutable object
     * @return new object used as storage in this holder
     */
    @Contract("-> new")
    public abstract D getNewRaw();

    /**
     * Creates new holder with data
     * @param data storage
     * @return new holder
     */
    @Contract("_ -> new")
    public abstract T getNewRaw(D data);

    @Contract("_ -> new")
    protected @NotNull Pair<String, String> extractMapKey(@NotNull String key) {
        int i = key.lastIndexOf(".");
        if (i == -1) return Pair.of("", key);
        var l = key.substring(0, i);
        var r = key.substring(i + 1);
        return Pair.of(l, r);
    }

    protected abstract D resolveMap(String key, boolean create);

    @Override
    public P put(String key, Object value) {
        return putValue(key, MapOps.INSTANCE.convertTo(getOps(), value));
    }
}
