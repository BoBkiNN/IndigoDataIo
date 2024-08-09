package xyz.bobkinn.indigodataio;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Main interface for data holders.<br>
 * Data types: <br>
 * <ul>
 *     <li>{@link T} section</li>
 *     <li>{@link P} basic type</li>
 *     <li>{@link Map} {@code Map<String, P>} map</li>
 *     <li>{@link String}</li>
 *     <li>{@link Byte}</li>
 *     <li>{@link Short}</li>
 *     <li>{@link Integer}</li>
 *     <li>{@link Long}</li>
 *     <li>{@link Float}</li>
 *     <li>{@link Double}</li>
 * </ul>
 * And all of this has {@link List} and array getters and setter too
 * @param <T> implementation type
 * @param <P> parent class type, like Object from {@link NestedKeyMap}, JsonElement from GSON or Tag from NBT.<br>
 *           Meant to be internal type that used as value in internal object
 */
@SuppressWarnings({"unused", "UnusedReturnValue"})
public interface DataHolder<T extends DataHolder<T, P>, P> {

    @SafeVarargs
    static <T> List<T> toList(T... array){
        if (array == null) return null;
        return List.of(array);
    }

    @SuppressWarnings("unchecked")
    static <T> T[] toArray(List<T> c){
        if (c == null) return null;
        return (T[]) c.toArray();
    }

    T getNew();

    /**
     * @return new builder
     */
    MapBuilder<T, P> toBuilder();

    /**
     * Get a builder view on this key
     * @param key key
     * @return new builder
     */
    MapBuilder<T, P> toBuilder(String key);

    P remove(String key);

    boolean contains(String key);

    boolean contains(String key, Class<? extends P> type);

    boolean containsSection(String key);

    Set<String> keys();

    Set<String> keys(String key);

    int size();

    void clear();

    default <O> List<O> getDefaultList(){
        return null;
    }

    /* BASIC METHODS */

    // getters

    P get(String key, P def);

    P get(String key);

    List<? extends P> getList(String key, List<? extends P> def);

    default List<? extends P> getList(String key) {
        return getList(key, getDefaultList());
    }

    default P[] getArray(String key, P[] def) {
        return toArray(getList(key, toList(def)));
    }

    default P[] getArray(String key){
        return toArray(getList(key));
    }

    // setters

    P put(String key, P value);

    P putList(String key, List<? extends P> value);

    default P putArray(String key, P[] value) {
        return putList(key, toList(value));
    }

    /* SECTION METHODS */

    // getters

    T getSection(String key, T def);

    T getSection(String key);

    List<T> getSectionList(String key, List<T> def);

    default List<T> getSectionList(String key){
        return getSectionList(key, getDefaultList());
    }

    default T[] getSectionArray(String key, T[] def){
        return toArray(getSectionList(key, toList(def)));
    }

    default T[] getSectionArray(String key){
        return toArray(getSectionList(key));
    }

    // setters

    P putSection(String key, T value);

    P putSectionList(String key, List<T> value);

    default P putSectionArray(String key, T[] value){
        return putSectionList(key, toList(value));
    }

    /* MAP METHODS */

    // getters

    Map<String, P> getMap(String key, Map<String, P> def);

    Map<String, P> getMap(String key);

    List<Map<String, P>> getMapList(String key, List<Map<String, P>> def);

    default List<Map<String, P>> getMapList(String key){
        return getMapList(key, getDefaultList());
    }

    default Map<String, P>[] getMapArray(String key, Map<String, P>[] def) {
        //noinspection unchecked
        return (Map<String, P>[]) getMapList(key, toList(def)).toArray(Map[]::new);
    }

    default Map<String, P>[] getMapArray(String key){
        //noinspection unchecked
        return (Map<String, P>[]) getMapList(key).toArray(Map[]::new);
    }

    // setters

    P putMap(String key, Map<String, P> value);

    P putMapList(String key, List<Map<String, P>> value);

    default P putMapArray(String key, Map<String, P>[] value) {
        return putMapList(key, toList(value));
    }

    //<editor-fold desc="Types methods" defaultstate="collapsed">

    /* String methods */

    // getters

    String getString(String key, String def);

    String getString(String key);

    List<String> getStringList(String key, List<String> def);

    default List<String> getStringList(String key){
        return getStringList(key, getDefaultList());
    }

    default String[] getStringArray(String key, String[] def){
        return toArray(getStringList(key, toList(def)));
    }

    default String[] getStringArray(String key){
        return toArray(getStringList(key));
    }

    // setters

    P putString(String key, String value);

    P putStringList(String key, List<String> value);

    default P putStringArray(String key, String[] value){
        return putStringList(key, toList(value));
    }


    /* Byte methods */

    // getters

    byte getByte(String key, byte def);

    byte getByte(String key);

    List<Byte> getByteList(String key, List<Byte> def);

    default List<Byte> getByteList(String key){
        return getByteList(key, getDefaultList());
    }

    default byte[] getByteArray(String key, byte[] def){
        return NumberUtil.listToByte(getByteList(key, NumberUtil.byteToList(def)));
    }

    default byte[] getByteArray(String key){
        return NumberUtil.listToByte(getByteList(key));
    }

    // setters

    P putByte(String key, byte value);

    P putByteList(String key, List<Byte> value);

    default P putByteArray(String key, byte[] value){
        return putByteList(key, NumberUtil.byteToList(value));
    }


    /* Short methods */

    // getters

    short getShort(String key, short def);

    short getShort(String key);

    List<Short> getShortList(String key, List<Short> def);

    default List<Short> getShortList(String key){
        return getShortList(key, getDefaultList());
    }

    default short[] getShortArray(String key, short[] def){
        return NumberUtil.listToShort(getShortList(key, NumberUtil.shortToList(def)));
    }

    default short[] getShortArray(String key){
        return NumberUtil.listToShort(getShortList(key));
    }

    // setters

    P putShort(String key, short value);

    P putShortList(String key, List<Short> value);

    default P putShortArray(String key, short[] value){
        return putShortList(key, NumberUtil.shortToList(value));
    }


    /* Integer methods */

    // getters

    int getInt(String key, int def);

    int getInt(String key);

    List<Integer> getIntList(String key, List<Integer> def);

    default List<Integer> getIntList(String key){
        return getIntList(key, getDefaultList());
    }

    default int[] getIntArray(String key, int[] def){
        return NumberUtil.listToInt(getIntList(key, NumberUtil.intToList(def)));
    }

    default int[] getIntArray(String key){
        return NumberUtil.listToInt(getIntList(key));
    }

    // setters

    P putInt(String key, int value);

    P putIntList(String key, List<Integer> value);

    default P putIntArray(String key, int[] value){
        return putIntList(key, NumberUtil.intToList(value));
    }


    /* Long methods */

    // getters

    long getLong(String key, long def);

    long getLong(String key);

    List<Long> getLongList(String key, List<Long> def);

    default List<Long> getLongList(String key){
        return getLongList(key, getDefaultList());
    }

    default long[] getLongArray(String key, long[] def){
        return NumberUtil.listToLong(getLongList(key, NumberUtil.longToList(def)));
    }

    default long[] getLongArray(String key){
        return NumberUtil.listToLong(getLongList(key));
    }

    // setters

    P putLong(String key, long value);

    P putLongList(String key, List<Long> value);

    default P putLongArray(String key, long[] value){
        return putLongList(key, NumberUtil.longToList(value));
    }


    /* Float methods */

    // getters

    float getFloat(String key, float def);

    float getFloat(String key);

    List<Float> getFloatList(String key, List<Float> def);

    default List<Float> getFloatList(String key){
        return getFloatList(key, getDefaultList());
    }

    default float[] getFloatArray(String key, float[] def){
        return NumberUtil.listToFloat(getFloatList(key, NumberUtil.floatToList(def)));
    }

    default float[] getFloatArray(String key){
        return NumberUtil.listToFloat(getFloatList(key));
    }

    // setters

    P putFloat(String key, float value);

    P putFloatList(String key, List<Float> value);

    default P putFloatArray(String key, float[] value){
        return putFloatList(key, NumberUtil.floatToList(value));
    }


    /* Double methods */

    // getters

    double getDouble(String key, double def);

    double getDouble(String key);

    List<Double> getDoubleList(String key, List<Double> def);

    default List<Double> getDoubleList(String key){
        return getDoubleList(key, getDefaultList());
    }

    default double[] getDoubleArray(String key, double[] def){
        return NumberUtil.listToDouble(getDoubleList(key, NumberUtil.doubleToList(def)));
    }

    default double[] getDoubleArray(String key){
        return NumberUtil.listToDouble(getDoubleList(key));
    }

    // setters

    P putDouble(String key, double value);

    P putDoubleList(String key, List<Double> value);

    default P putDoubleArray(String key, double[] value){
        return putDoubleList(key, NumberUtil.doubleToList(value));
    }

    //</editor-fold>

}
