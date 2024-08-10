package xyz.bobkinn.indigodataio.ops;

import xyz.bobkinn.indigodataio.NumberUtil;
import xyz.bobkinn.indigodataio.Pair;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Class that provides conversations between data types. Based on DynamicOps from Mojang/DataFixerUpper
 * @param <T> data type
 */
@SuppressWarnings("unused")
public interface TypeOps<T> {
    T empty();

    default T emptyMap() {
        return createMap(Map.of());
    }

    default T emptyList() {
        return createList(Stream.empty());
    }

    <U> U convertTo(TypeOps<U> outOps, T input);

    Optional<Number> getNumberValue(T input);

    default Number getNumberValue(final T input, final Number defaultValue) {
        return getNumberValue(input).orElse(defaultValue);
    }

    T createNumeric(Number i);

    // byte

    default T createByte(final byte value) {
        return createNumeric(value);
    }

    default Optional<Byte> getByte(T input) {
        return getNumberValue(input).map(Number::byteValue);
    }

    // short

    default T createShort(final short value) {
        return createNumeric(value);
    }

    default Optional<Short> getShort(T input) {
        return getNumberValue(input).map(Number::shortValue);
    }

    // int

    default T createInt(final int value) {
        return createNumeric(value);
    }

    default Optional<Integer> getInt(T input) {
        return getNumberValue(input).map(Number::intValue);
    }

    // long

    default T createLong(final long value) {
        return createNumeric(value);
    }

    default Optional<Long> getLong(T input) {
        return getNumberValue(input).map(Number::longValue);
    }

    // float

    default T createFloat(final float value) {
        return createNumeric(value);
    }

    default Optional<Float> getFloat(T input) {
        return getNumberValue(input).map(Number::floatValue);
    }

    // double

    default T createDouble(final double value) {
        return createNumeric(value);
    }

    default Optional<Double> getDouble(T input) {
        return getNumberValue(input).map(Number::doubleValue);
    }

    // boolean

    default T createBoolean(final boolean value) {
        return createByte((byte) (value ? 1 : 0));
    }

    default Optional<Boolean> getBoolean(final T input) {
        return getNumberValue(input).map(number -> number.byteValue() != 0);
    }

    // end

    Optional<String> getString(T input);

    T createString(String value);

    Optional<T> mergeToList(T list, T value);

    default Optional<T> mergeToList(final T list, final List<T> values) {
        Optional<T> result = Optional.of(list);
        for (final T value : values) {
            result = result.flatMap(r -> mergeToList(r, value));
        }
        return result;
    }

    Optional<T> mergeToMap(T map, T key, T value);

    default Optional<T> mergeToMap(final T map, final Map<T, T> values) {
        return mergeToMap(map, BaseMap.forMap(values, this));
    }

    default Optional<T> mergeToMap(final T map, final BaseMap<T> values) {
        final AtomicReference<Optional<T>> result = new AtomicReference<>(Optional.of(map));

        values.entries().forEach(entry ->
                result.setPlain(result.getPlain().flatMap(r -> mergeToMap(r, entry.getFirst(), entry.getSecond())))
        );
        return result.getPlain();
    }

    default Optional<T> mergeToPrimitive(final T prefix, final T value) {
        if (!Objects.equals(prefix, empty())) {
            return Optional.empty();
        }
        return Optional.of(value);
    }

    Optional<Stream<Pair<T, T>>> getMapValues(T input);

    default Optional<Consumer<BiConsumer<T, T>>> getMapEntries(final T input) {
        return getMapValues(input).map(s -> c -> s.forEach(p -> c.accept(p.getFirst(), p.getSecond())));
    }

    T createMap(Stream<Pair<T, T>> map);

    default Optional<BaseMap<T>> getMap(final T input) {
        return getMapValues(input).flatMap(s -> {
            try {
                return Optional.of(BaseMap.forMap(s.collect(Pair.toMap()), this));
            } catch (final IllegalStateException e) {
                return Optional.empty();
            }
        });
    }

    default T createMap(final Map<T, T> map) {
        return createMap(map.entrySet().stream().map(e -> Pair.of(e.getKey(), e.getValue())));
    }

    Optional<Stream<T>> getStream(T input);

    default Optional<Consumer<Consumer<T>>> getList(final T input) {
        return getStream(input).map(s -> s::forEach);
    }

    T createList(Stream<? extends T> input);

    T createArray(T[] input);

    // string lists

    default Optional<List<String>> getStringList(final T input){
        return getStream(input).flatMap(stream -> {
            final List<T> list = stream.toList();
            if (list.stream().allMatch(element -> getString(element).isPresent())) {
                return Optional.of(list.stream().map(e -> getString(e).orElseThrow()).toList());
            }
            return Optional.empty();
        });
    }

    default Optional<String[]> getStringArray(final T input){
        return getStringList(input).map(l -> l.toArray(String[]::new));
    }

    default T createStringList(List<String> value){
        return createList(value.stream().map(this::createString));
    }

    default T createStringArray(String[] value){
        return createStringList(Arrays.stream(value).toList());
    }

    // bool lists

    default Optional<List<Boolean>> getBoolList(final T input){
        return getStream(input).flatMap(stream -> {
            final List<T> list = stream.toList();
            if (list.stream().allMatch(element -> getBoolean(element).isPresent())) {
                return Optional.of(list.stream().map(e -> getBoolean(e).orElseThrow()).toList());
            }
            return Optional.empty();
        });
    }

    default Optional<boolean[]> getBoolArray(final T input){
        return getStream(input).flatMap(stream -> {
            final List<T> list = stream.toList();
            if (list.stream().allMatch(element -> getBoolean(element).isPresent())) {
                final boolean[] buf = new boolean[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    buf[i] = getBoolean(list.get(i)).orElseThrow();
                }
                return Optional.of(buf);
            }
            return Optional.empty();
        });
    }

    default T createBoolList(List<Boolean> value){
        return createList(value.stream().map(this::createBoolean));
    }

    default T createBoolArray(boolean[] value){
        return createList(NumberUtil.boolToStream(value).map(this::createBoolean));
    }

    // byte lists

    default Optional<ByteBuffer> getByteBuffer(final T input) {
        return getByteArray(input).map(ByteBuffer::wrap);
    }

    default Optional<byte[]> getByteArray(final T input) {
        return getStream(input).flatMap(stream -> {
            final List<T> list = stream.toList();
            if (list.stream().allMatch(element -> getNumberValue(element).isPresent())) {
                final byte[] buf = new byte[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    buf[i] = getByte(list.get(i)).orElse((byte) 0);
                }
                return Optional.of(buf);
            }
            return Optional.empty();
        });
    }

    default Optional<List<Byte>> getByteList(final T input){
        return getByteArray(input).map(NumberUtil::byteToList);
    }

    default T createByteBuffer(final ByteBuffer input) {
        return createList(IntStream.range(0, input.capacity()).mapToObj(i -> createByte(input.get(i))));
    }

    default T createByteArray(final byte[] input) {
        return createList(IntStream.range(0, input.length).mapToObj(i -> createByte(input[i])));
    }

    default T createByteList(final List<Byte> input) {
        return createList(input.stream().map(this::createByte));
    }

    // short lists

    default Optional<ShortBuffer> getShortBuffer(final T input) {
        return getShortArray(input).map(ShortBuffer::wrap);
    }

    default Optional<short[]> getShortArray(final T input) {
        return getStream(input).flatMap(stream -> {
            final List<T> list = stream.toList();
            if (list.stream().allMatch(element -> getNumberValue(element).isPresent())) {
                final short[] buf = new short[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    buf[i] = getShort(list.get(i)).orElse((short) 0);
                }
                return Optional.of(buf);
            }
            return Optional.empty();
        });
    }

    default Optional<List<Short>> getShortList(final T input){
        return getShortArray(input).map(NumberUtil::shortToList);
    }

    default T createShortBuffer(final ShortBuffer input) {
        return createList(IntStream.range(0, input.capacity()).mapToObj(i -> createShort(input.get(i))));
    }

    default T createShortArray(final short[] input) {
        return createList(IntStream.range(0, input.length).mapToObj(i -> createShort(input[i])));
    }

    default T createShortList(final List<Short> input) {
        return createList(input.stream().map(this::createShort));
    }

    // int lists

    default Optional<IntStream> getIntStream(final T input) {
        return getIntArray(input).map(IntStream::of);
    }

    default Optional<int[]> getIntArray(final T input) {
        return getStream(input).flatMap(stream -> {
            final List<T> list = stream.toList();
            if (list.stream().allMatch(element -> getNumberValue(element).isPresent())) {
                return Optional.of(list.stream().mapToInt(element -> getInt(element).orElseThrow()).toArray());
            }
            return Optional.empty();
        });
    }

    default Optional<List<Integer>> getIntList(final T input) {
        return getIntArray(input).map(NumberUtil::intToList);
    }

    default T createIntStream(final IntStream input) {
        return createList(input.mapToObj(this::createInt));
    }

    default T createIntArray(final int[] input) {
        return createIntStream(Arrays.stream(input));
    }

    default T createIntList(final List<Integer> input) {
        return createList(input.stream().map(this::createInt));
    }

    // long list

    default Optional<LongStream> getLongStream(final T input) {
        return getLongArray(input).map(LongStream::of);
    }

    default Optional<long[]> getLongArray(final T input) {
        return getStream(input).flatMap(stream -> {
            final List<T> list = stream.toList();
            if (list.stream().allMatch(element -> getNumberValue(element).isPresent())) {
                return Optional.of(list.stream().mapToLong(element -> getLong(element).orElseThrow()).toArray());
            }
            return Optional.empty();
        });
    }

    default Optional<List<Long>> getLongList(final T input) {
        return getLongStream(input).map(LongStream::boxed).map(Stream::toList);
    }

    default T createLongStream(final LongStream input) {
        return createList(input.mapToObj(this::createLong));
    }

    default T createLongList(final List<Long> input) {
        return createList(input.stream().map(this::createLong));
    }

    default T createLongArray(final long[] input) {
        return createLongStream(LongStream.of(input));
    }

    // float list

    default Optional<FloatBuffer> getFloatBuffer(final T input) {
        return getStream(input).flatMap(stream -> {
            final List<T> list = stream.toList();
            if (list.stream().allMatch(element -> getNumberValue(element).isPresent())) {
                final var buf = FloatBuffer.wrap(new float[list.size()]);
                for (T t : list) {
                    buf.put(getFloat(t).orElse((0f)));
                }
                return Optional.of(buf);
            }
            return Optional.empty();
        });
    }

    default Optional<float[]> getFloatArray(final T input) {
        return getFloatBuffer(input).map(FloatBuffer::array);
    }

    default Optional<List<Float>> getFloatList(final T input) {
        return getFloatArray(input).map(NumberUtil::floatToList);
    }

    default T createFloatBuffer(final FloatBuffer input) {
        return createFloatArray(input.array());
    }

    default T createFloatList(final List<Float> input) {
        return createList(input.stream().map(this::createFloat));
    }

    default T createFloatArray(final float[] input) {
        return createFloatList(NumberUtil.floatToList(input));
    }

    // double lists

    default Optional<DoubleBuffer> getDoubleBuffer(final T input) {
        return getStream(input).flatMap(stream -> {
            final List<T> list = stream.toList();
            if (list.stream().allMatch(element -> getNumberValue(element).isPresent())) {
                final var buf = DoubleBuffer.wrap(new double[list.size()]);
                for (T t : list) {
                    buf.put(getDouble(t).orElse(0d));
                }
                return Optional.of(buf);
            }
            return Optional.empty();
        });
    }

    default Optional<double[]> getDoubleArray(final T input) {
        return getDoubleBuffer(input).map(DoubleBuffer::array);
    }

    default Optional<List<Double>> getDoubleList(final T input) {
        return getDoubleArray(input).map(NumberUtil::doubleToList);
    }

    default T createDoubleBuffer(final DoubleBuffer input) {
        return createDoubleArray(input.array());
    }

    default T createDoubleList(final List<Double> input) {
        return createList(input.stream().map(this::createDouble));
    }

    default T createDoubleArray(final double[] input) {
        return createDoubleList(NumberUtil.doubleToList(input));
    }

    // end

    T remove(T input, String key);

    default Optional<T> get(final T input, final String key) {
        return getGeneric(input, createString(key));
    }

    default Optional<T> getGeneric(final T input, final T key) {
        return getMap(input).flatMap(map -> Optional.ofNullable(map.get(key)));
    }

    default T set(final T input, final String key, final T value) {
        return mergeToMap(input, createString(key), value).orElse(input);
    }

    default T update(final T input, final String key, final Function<T, T> function) {
        return get(input, key).map(value -> set(input, key, function.apply(value))).orElse(input);
    }

    default T updateGeneric(final T input, final T key, final Function<T, T> function) {
        return getGeneric(input, key).flatMap(value -> mergeToMap(input, key, function.apply(value))).orElse(input);
    }

    default <U> U convertList(final TypeOps<U> outOps, final T input) {
        return outOps.createList(getStream(input).orElse(Stream.empty()).map(e -> convertTo(outOps, e)));
    }

    default <U> U convertMap(final TypeOps<U> outOps, final T input) {
        return outOps.createMap(getMapValues(input).orElse(Stream.empty()).map(e ->
                Pair.of(convertTo(outOps, e.getFirst()), convertTo(outOps, e.getSecond()))
        ));
    }
}
