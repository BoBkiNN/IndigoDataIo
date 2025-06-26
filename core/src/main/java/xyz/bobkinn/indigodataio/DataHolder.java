package xyz.bobkinn.indigodataio;

import xyz.bobkinn.indigodataio.ops.MapOps;
import xyz.bobkinn.indigodataio.ops.TypeOps;

import java.util.*;

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

    /**
     * Creates another DataHolder with values of this
     * @param to other instance of DataHolder
     * @return new instance of other DataHolder with values
     * @param <TO> type of other DataHolder
     * @param <TP> type of top value in other DataHolder (like JsonElement)
     * @since 3.0.5
     */
    default <TO extends DataHolder<TO, TP>, TP> TO convertTo(TO to) {
        var otherOps = to.getOps();
        var selfOps = this.getOps();
        for (var key : this.keys()) {
            var sv = this.get(key);
            var ov = selfOps.convertTo(otherOps, sv);
            to.putValue(key, ov);
        }
        return to;
    }

    @SafeVarargs
    static <T> List<T> toList(T... array){
        if (array == null) return null;
        return new ArrayList<>(List.of(array));
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

    TypeOps<P> getOps();

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

    /* TYPE ARGS AND MAPS METHODS */

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

    /**
     * Puts java value in this map
     * @implSpec {@link MapOps} ops must be used
     * @param key key
     * @param value value that can be converted using {@link MapOps#convertTo(TypeOps, Object)}
     * @return previous value
     */
    P put(String key, Object value);

    /**
     * Puts type value that extends P. When P is Object then {@link #put(String, Object)} is recommended.
     * Or use precise methods for accurate value storing
     * @param key key
     * @param value value that extends P
     * @return previous value
     */
    P putValue(String key, P value);

    default P putList(String key, List<? extends P> value){
        return putValue(key, getOps().createList(value.stream()));
    }

    default P putArray(String key, P[] value) {
        return putValue(key, getOps().createList(Arrays.stream(value)));
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

    /**
     * Puts map but all values are converted using {@link MapOps#convertMap(TypeOps, Object)}
     * @param key key
     * @param value map
     * @return previous value
     */
    default P putRawMap(String key, Map<String, Object> value){
        return putValue(key, MapOps.INSTANCE.convertMap(getOps(), value));
    }

    P putMapList(String key, List<Map<String, P>> value);

    default P putMapArray(String key, Map<String, P>[] value) {
        return putMapList(key, toList(value));
    }

    //<editor-fold desc="Types methods" defaultstate="collapsed">

    /* String methods */

    // getters

    default String getString(String key, String def){
        return getOps().getString(get(key)).orElse(def);
    }

    default String getString(String key){
        return getString(key, null);
    }

    default List<String> getStringList(String key, List<String> def) {
        return getOps().getStringList(get(key)).orElse(def);
    }

    default List<String> getStringList(String key){
        return getStringList(key, getDefaultList());
    }

    default String[] getStringArray(String key, String[] def){
        return getOps().getStringArray(get(key)).orElse(def);
    }

    default String[] getStringArray(String key){
        return getStringArray(key, null);
    }

    // setters

    default P putString(String key, String value){
        return putValue(key, getOps().createString(value));
    }

    default P putStringList(String key, List<String> value){
        return putValue(key, getOps().createStringList(value));
    }

    default P putStringArray(String key, String[] value){
        return putValue(key, getOps().createStringArray(value));
    }

    /* Bool methods */

    // getters

    default boolean getBoolean(String key, boolean def) {
        return getOps().getBoolean(get(key)).orElse(def);
    }

    default boolean getBoolean(String key){
        return getBoolean(key, false);
    }

    default List<Boolean> getBoolList(String key, List<Boolean> def){
        return getOps().getBoolList(get(key)).orElse(def);
    }

    default List<Boolean> getBoolList(String key){
        return getBoolList(key, getDefaultList());
    }

    default boolean[] getBoolArray(String key, boolean[] def){
        return getOps().getBoolArray(get(key)).orElse(def);
    }

    default boolean[] getBoolArray(String key){
        return getBoolArray(key, null);
    }

    // setters

    default P putBoolean(String key, boolean value){
        return putValue(key, getOps().createBoolean(value));
    }

    default P putBoolList(String key, List<Boolean> value){
        return putValue(key, getOps().createBoolList(value));
    }

    default P putBoolArray(String key, boolean[] value){
        return putValue(key, getOps().createBoolArray(value));
    }


    /* Byte methods */

    // getters

    default byte getByte(String key, byte def) {
        return getOps().getByte(get(key)).orElse(def);
    }

    default byte getByte(String key){
        return getByte(key, (byte) 0);
    }

    default List<Byte> getByteList(String key, List<Byte> def){
        return getOps().getByteList(get(key)).orElse(def);
    }

    default List<Byte> getByteList(String key){
        return getByteList(key, getDefaultList());
    }

    default byte[] getByteArray(String key, byte[] def){
        return getOps().getByteArray(get(key)).orElse(def);
    }

    default byte[] getByteArray(String key){
        return getByteArray(key, null);
    }

    // setters

    default P putByte(String key, byte value){
        return putValue(key, getOps().createByte(value));
    }

    default P putByteList(String key, List<Byte> value){
        return putValue(key, getOps().createByteList(value));
    }

    default P putByteArray(String key, byte[] value){
        return putValue(key, getOps().createByteArray(value));
    }


    /* Short methods */

    // getters

    default short getShort(String key, short def){
        return getOps().getShort(get(key)).orElse(def);
    }

    default short getShort(String key) {
        return getShort(key, (short) 0);
    }

    default List<Short> getShortList(String key, List<Short> def){
        return getOps().getShortList(get(key)).orElse(def);
    }

    default List<Short> getShortList(String key){
        return getShortList(key, getDefaultList());
    }

    default short[] getShortArray(String key, short[] def){
        return getOps().getShortArray(get(key)).orElse(def);
    }

    default short[] getShortArray(String key){
        return getShortArray(key,null);
    }

    // setters

    default P putShort(String key, short value){
        return putValue(key, getOps().createShort(value));
    }

    default P putShortList(String key, List<Short> value){
        return putValue(key, getOps().createShortList(value));
    }

    default P putShortArray(String key, short[] value){
        return putValue(key, getOps().createShortArray(value));
    }


    /* Integer methods */

    // getters

    default int getInt(String key, int def){
        return getOps().getInt(get(key)).orElse(def);
    }

    default int getInt(String key) {
        return getInt(key, 0);
    }

    default List<Integer> getIntList(String key, List<Integer> def){
        return getOps().getIntList(get(key)).orElse(def);
    }

    default List<Integer> getIntList(String key){
        return getIntList(key, getDefaultList());
    }

    default int[] getIntArray(String key, int[] def){
        return getOps().getIntArray(get(key)).orElse(def);
    }

    default int[] getIntArray(String key){
        return getIntArray(key, null);
    }

    // setters

    default P putInt(String key, int value){
        return putValue(key, getOps().createInt(value));
    }

    default P putIntList(String key, List<Integer> value){
        return putValue(key, getOps().createIntList(value));
    }

    default P putIntArray(String key, int[] value){
        return putValue(key, getOps().createIntArray(value));
    }


    /* Long methods */

    // getters

    default long getLong(String key, long def){
        return getOps().getLong(get(key)).orElse(def);
    }

    default long getLong(String key) {
        return getLong(key, 0L);
    }

    default List<Long> getLongList(String key, List<Long> def){
        return getOps().getLongList(get(key)).orElse(def);
    }

    default List<Long> getLongList(String key){
        return getLongList(key, getDefaultList());
    }

    default long[] getLongArray(String key, long[] def){
        return getOps().getLongArray(get(key)).orElse(def);
    }

    default long[] getLongArray(String key){
        return getLongArray(key, null);
    }

    // setters

    default P putLong(String key, long value){
        return putValue(key, getOps().createLong(value));
    }

    default P putLongList(String key, List<Long> value){
        return putValue(key, getOps().createLongList(value));
    }

    default P putLongArray(String key, long[] value){
        return putValue(key, getOps().createLongArray(value));
    }


    /* Float methods */

    // getters

    default float getFloat(String key, float def){
        return getOps().getFloat(get(key)).orElse(def);
    }

    default float getFloat(String key) {
        return getFloat(key, 0);
    }

    default List<Float> getFloatList(String key, List<Float> def){
        return getOps().getFloatList(get(key)).orElse(def);
    }

    default List<Float> getFloatList(String key){
        return getFloatList(key, getDefaultList());
    }

    default float[] getFloatArray(String key, float[] def){
        return getOps().getFloatArray(get(key)).orElse(def);
    }

    default float[] getFloatArray(String key){
        return getFloatArray(key, null);
    }

    // setters

    default P putFloat(String key, float value){
        return putValue(key, getOps().createFloat(value));
    }

    default P putFloatList(String key, List<Float> value){
        return putValue(key, getOps().createFloatList(value));
    }

    default P putFloatArray(String key, float[] value){
        return putValue(key, getOps().createFloatArray(value));
    }


    /* Double methods */

    // getters

    default double getDouble(String key, double def){
        return getOps().getDouble(get(key)).orElse(def);
    }

    default double getDouble(String key) {
        return getDouble(key, 0);
    }

    default List<Double> getDoubleList(String key, List<Double> def){
        return getOps().getDoubleList(get(key)).orElse(def);
    }

    default List<Double> getDoubleList(String key){
        return getDoubleList(key, getDefaultList());
    }

    default double[] getDoubleArray(String key, double[] def){
        return getOps().getDoubleArray(get(key)).orElse(def);
    }

    default double[] getDoubleArray(String key){
        return getDoubleArray(key, null);
    }

    // setters

    default P putDouble(String key, double value){
        return putValue(key, getOps().createDouble(value));
    }

    default P putDoubleList(String key, List<Double> value){
        return putValue(key, getOps().createDoubleList(value));
    }

    default P putDoubleArray(String key, double[] value){
        return putValue(key, getOps().createDoubleArray(value));
    }

    //</editor-fold>

}
