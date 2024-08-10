package xyz.bobkinn.indigodataio.ops;

import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigodataio.Pair;

import java.util.Map;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public interface BaseMap<T> {
    @Nullable
    T get(final T key);

    @Nullable
    T get(final String key);

    Stream<Pair<T, T>> entries();

    static <T> BaseMap<T> forMap(final Map<T, T> map, final TypeOps<T> ops) {
        return new BaseMap<>() {
            @Nullable
            @Override
            public T get(final T key) {
                return map.get(key);
            }

            @Nullable
            @Override
            public T get(final String key) {
                return get(ops.createString(key));
            }

            @Override
            public Stream<Pair<T, T>> entries() {
                return map.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue()));
            }

            @Override
            public String toString() {
                return "BaseMap[" + map + "]";
            }
        };
    }
}
