package xyz.bobkinn.indigodataio.ops;

import org.jetbrains.annotations.Nullable;
import xyz.bobkinn.indigodataio.Pair;

import java.nio.*;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;


/**
 * Type operations used in {@link xyz.bobkinn.indigodataio.NestedKeyMap}. Converts arrays to lists
 */
public class MapOps implements TypeOps<Object> {
    public static final MapOps INSTANCE = new MapOps();

    @Override
    public Object empty() {
        return null;
    }

    @Override
    public <U> U convertTo(final TypeOps<U> outOps, final Object input) {
        if (input == null) return outOps.empty();
        if (input instanceof Map) return convertMap(outOps, input);
        if (input instanceof final byte[] value) return outOps.createByteArray(value);
        if (input instanceof final short[] value) return outOps.createShortArray(value);
        if (input instanceof final int[] value) return outOps.createIntArray(value);
        if (input instanceof final long[] value) return outOps.createLongArray(value);
        if (input instanceof final float[] value) return outOps.createFloatArray(value);
        if (input instanceof final double[] value) return outOps.createDoubleArray(value);
        if (input instanceof final Object[] value)
            return outOps.createList(Arrays.stream(value).map(v -> convertTo(outOps, v)));
        if (input instanceof ByteBuffer value) return outOps.createByteBuffer(value);
        if (input instanceof ShortBuffer value) return outOps.createShortBuffer(value);
        if (input instanceof IntStream value) return outOps.createIntStream(value);
        if (input instanceof IntBuffer value) return outOps.createIntArray(value.array());
        if (input instanceof LongStream value) return outOps.createLongStream(value);
        if (input instanceof LongBuffer value) return outOps.createLongArray(value.array());
        if (input instanceof FloatBuffer value) return outOps.createFloatBuffer(value);
        if (input instanceof DoubleBuffer value) return outOps.createDoubleBuffer(value);
        if (input instanceof List) return convertList(outOps, input);
        if (input instanceof final String value) return outOps.createString(value);
        if (input instanceof final Boolean value) return outOps.createBoolean(value);
        if (input instanceof final Byte value) return outOps.createByte(value);
        if (input instanceof final Short value) return outOps.createShort(value);
        if (input instanceof final Integer value) return outOps.createInt(value);
        if (input instanceof final Long value) return outOps.createLong(value);
        if (input instanceof final Float value) return outOps.createFloat(value);
        if (input instanceof final Double value) return outOps.createDouble(value);
        if (input instanceof final Number value) return outOps.createNumeric(value);
        throw new IllegalStateException("Don't know how to convert " + input);
    }

    @Override
    public Optional<Number> getNumberValue(Object input) {
        if (input instanceof Number n) return Optional.of(n);
        else return Optional.empty();
    }

    @Override
    public Object createNumeric(Number i) {
        return i;
    }

    @Override
    public Object createByte(byte value) {
        return value;
    }

    @Override
    public Object createBoolean(boolean value) {
        return value;
    }

    @Override
    public Object createShort(short value) {
        return value;
    }

    @Override
    public Object createInt(int value) {
        return value;
    }

    @Override
    public Object createLong(long value) {
        return value;
    }

    @Override
    public Object createFloat(float value) {
        return value;
    }

    @Override
    public Object createDouble(double value) {
        return value;
    }

    @Override
    public Optional<String> getString(Object input) {
        if (input instanceof String s) return Optional.of(s);
        else return Optional.empty();
    }

    @Override
    public Object createString(String value) {
        return value;
    }

    @Override
    public Optional<Object> mergeToList(Object list, Object value) {
        return Optional.empty();
    }

    @Override
    public Optional<Object> mergeToMap(Object map, Object key, Object value) {
        return Optional.empty();
    }

    @Override
    public Optional<Stream<Pair<Object, Object>>> getMapValues(Object input) {
        if (input instanceof final Map<?, ?> map) {
            return Optional.of(map.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())));
        }
        return Optional.empty();
    }

    private static Stream<Pair<Object, Object>> getMapEntries(final Map<?, ?> input) {
        return input.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue()));
    }

    @Override
    public Optional<Consumer<BiConsumer<Object, Object>>> getMapEntries(final Object input) {
        if (input instanceof final Map<?, ?> map) {
            return Optional.of(map::forEach);
        }
        return Optional.empty();
    }

    @Override
    public Optional<BaseMap<Object>> getMap(Object input) {
        if (input instanceof final Map<?, ?> map) {
            return Optional.of(
                    new BaseMap<>() {
                        @Nullable
                        @Override
                        public Object get(final Object key) {
                            return map.get(key);
                        }

                        @Nullable
                        @Override
                        public Object get(final String key) {
                            return map.get(key);
                        }

                        @Override
                        public Stream<Pair<Object, Object>> entries() {
                            return getMapEntries(map);
                        }

                        @Override
                        public String toString() {
                            return "BaseMap[" + map + "]";
                        }
                    }
            );
        }
        return Optional.empty();
    }

    @Override
    public Object createMap(Stream<Pair<Object, Object>> map) {
        var ls = map.map(Pair::toEntry).toArray(Map.Entry[]::new);
        return Map.ofEntries(ls);
    }

    @Override
    public Optional<Stream<Object>> getStream(Object input) {
        if (input instanceof List<?> ls) {
            return Optional.of(ls.stream().map(o -> o));
        }
        return Optional.empty();
    }

    @Override
    public Object createList(Stream<?> input) {
        return input.toList();
    }

    @Override
    public Object createArray(Object[] input) {
        return createList(Arrays.stream(input));
    }

    @Override
    public Object remove(Object input, String key) {
        if (input instanceof final Map<?, ?> map) {
            final Map<Object, Object> result = new LinkedHashMap<>(map);
            result.remove(key);
            return Map.copyOf(result);
        }
        return input;
    }
}
