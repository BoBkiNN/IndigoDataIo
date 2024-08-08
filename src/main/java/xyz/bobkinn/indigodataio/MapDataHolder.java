package xyz.bobkinn.indigodataio;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

@RequiredArgsConstructor
public class MapDataHolder implements DataHolder<MapDataHolder, Object>{
    @NonNull // to generate check
    private final @NotNull Map<String, Object> data;

    public MapDataHolder(){
        this(new HashMap<>());
    }

    public Map<String, Object> getRaw(){
        return data;
    }

    @Override
    public MapDataHolder getNew() {
        return new MapDataHolder();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + data;
    }

    @Contract("_, true -> !null")
    @SuppressWarnings("unchecked")
    private Map<String, Object> resolveMap(@NotNull String key, boolean create) {
        var ret = data;
        for (String k : key.split("\\.")) {
            if (k.isEmpty()) return ret;
            if (ret == null) return null;
            Object o;
            if (ret.containsKey(k)) {
                o = ret.get(k);
            } else if (create) {
                o = new HashMap<String, Object>();
                ret.put(k, o);
            } else {
                return null;
            }
            try {
                ret = (Map<String, Object>) o;
            } catch (ClassCastException e) {
                return null;
            }
        }
        return ret;
    }

    /**
     * @param key full key
     * @return left - map key, right - key inside map
     */
    @Contract("_ -> new")
    private @NotNull Pair<String, String> extractMapKey(@NotNull String key) {
        int i = key.lastIndexOf(".");
        if (i == -1) return Pair.of("", key);
        var l = key.substring(0, i);
        var r = key.substring(i + 1);
        return Pair.of(l, r);
    }

    private static void checkEmptyKey(String key){
        if (key.isEmpty()) throw new IllegalArgumentException("Empty key '"+key+"'");
    }

    @Override
    public Object remove(String key) {
        checkEmptyKey(key);
        var p = extractMapKey(key);
        var map = resolveMap(p.getLeft(), false);
        if (map == null) return null;
        return map.remove(p.getRight());
    }

    @Override
    public boolean contains(String key) {
        checkEmptyKey(key);
        return data.containsKey(key);
    }

    @Override
    public boolean contains(String key, Class<?> type) {
        var d = get(key);
        return type.isInstance(d);
    }

    @Override
    public boolean containsSection(String key) {
        return getMap(key) != null;
    }

    @Override
    public Set<String> keys() {
        return data.keySet();
    }

    @Override
    public Set<String> keys(String key) {
        var p = extractMapKey(key);
        var map = resolveMap(p.getLeft(), false);
        if (map == null) return Set.of();
        return map.keySet();
    }

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public void clear() {
        data.clear();
    }

    public <A extends Number> List<A> getNumberList(String key, Function<Number, A> f, List<A> def){
        var v = get(key);
        if (v == null) return def;
        try {
            if (v instanceof List<?> l){
                //noinspection unchecked
                return ((List<Number>) l).stream().map(f).toList();
            } else if (v instanceof int[] l) {
                return Arrays.stream(l).boxed().map(f).toList();
            } else if (v instanceof float[] l) {
                return NumberUtil.floatToStream(l).map(f).toList();
            } else if (v instanceof short[] l) {
                return NumberUtil.shortToStream(l).map(f).toList();
            } else if (v instanceof double[] l) {
                return Arrays.stream(l).boxed().map(f).toList();
            } else if (v instanceof long[] l) {
                return Arrays.stream(l).boxed().map(f).toList();
            } else if (v instanceof byte[] l) {
                return NumberUtil.byteToList(l).stream().map(f).toList();
            }
            return def;
        } catch (ClassCastException ignored){
            return def;
        }
    }

    @Override
    public Object get(String key, Object def) {
        var p = extractMapKey(key);
        var map = resolveMap(p.getLeft(), false);
        if (map == null) return def;
        return map.getOrDefault(p.getRight(), def);
    }

    @Override
    public Object get(String key) {
        return get(key, null);
    }

    @Override
    public List<?> getList(String key, List<?> def) {
        var o = get(key, def);
        if (o instanceof List<?> ls){
            return ls;
        } else return def;
    }

    @Override
    public Object put(String key, Object value) {
        if (value instanceof MapDataHolder){
            throw new IllegalArgumentException("Use putSection to put MapDataHolder");
        }
        var p = extractMapKey(key);
        var map = resolveMap(p.getLeft(), true);
        return map.put(p.getRight(), value);
    }

    @Override
    public Object putList(String key, List<?> value) {
        return put(key, value);
    }

    @Override
    public MapDataHolder getSection(String key, MapDataHolder def) {
        var m = getMap(key, null);
        if (m == null) return def;
        return new MapDataHolder(m);
    }

    @Override
    public MapDataHolder getSection(String key) {
        return getSection(key, null);
    }

    @Override
    public List<MapDataHolder> getSectionList(String key, List<MapDataHolder> def) {
        var ls = getMapList(key);
        if (ls == null) return def;
        return ls.stream().map(MapDataHolder::new).toList();
    }

    @Override
    public Object putSection(String key, MapDataHolder value) {
        return put(key, value != null ? value.data : null);
    }

    @Override
    public Object putSectionList(String key, List<MapDataHolder> value) {
        Object old;
        if (value == null) {
            old = put(key, null);
        } else {
            old = put(key, value.stream().map(MapDataHolder::getRaw).toList());
        }
        return old;
    }

    @Override
    public Map<String, Object> getMap(String key, Map<String, Object> def) {
        var o = get(key, def);
        try {
            //noinspection unchecked
            return (Map<String, Object>) o;
        } catch (Exception e){
            return def;
        }
    }

    @Override
    public Map<String, Object> getMap(String key) {
        return getMap(key, null);
    }

    @Override
    public List<Map<String, Object>> getMapList(String key, List<Map<String, Object>> def) {
        var v = get(key, def);
        try {
            //noinspection unchecked
            return (List<Map<String, Object>>) v;
        } catch (ClassCastException ignored){
            return def;
        }
    }

    @Override
    public Object putMap(String key, Map<String, Object> value) {
        return put(key, value);
    }

    @Override
    public Object putMapList(String key, List<Map<String, Object>> value) {
        return put(key, value);
    }

    @Override
    public String getString(String key, String def) {
        var v = get(key);
        if (v instanceof String s) return s;
        else return def;
    }

    @Override
    public String getString(String key) {
        return getString(key, null);
    }

    @Override
    public List<String> getStringList(String key, List<String> def) {
        var v = get(key, def);
        try {
            //noinspection unchecked
            return (List<String>) v;
        } catch (Exception e) {
            return def;
        }
    }

    @Override
    public Object putString(String key, String value) {
        return put(key, value);
    }

    @Override
    public Object putStringList(String key, List<String> value) {
        return put(key, value);
    }

    @Override
    public byte getByte(String key, byte def) {
        var v = get(key);
        if (v == null) return def;
        if (v instanceof Number n) return n.byteValue();
        return def;
    }

    @Override
    public byte getByte(String key) {
        return getByte(key, (byte) 0);
    }

    @Override
    public List<Byte> getByteList(String key, List<Byte> def) {
        return getNumberList(key, Number::byteValue, def);
    }

    @Override
    public Object putByte(String key, byte value) {
        return put(key, value);
    }

    @Override
    public Object putByteList(String key, List<Byte> value) {
        return put(key, value);
    }

    @Override
    public short getShort(String key, short def) {
        var v = get(key);
        if (v == null) return def;
        if (v instanceof Number n) return n.shortValue();
        return def;
    }

    @Override
    public short getShort(String key) {
        return getShort(key, (short) 0);
    }

    @Override
    public List<Short> getShortList(String key, List<Short> def) {
        return getNumberList(key, Number::shortValue, def);
    }

    @Override
    public Object putShort(String key, short value) {
        return put(key, value);
    }

    @Override
    public Object putShortList(String key, List<Short> value) {
        return put(key, value);
    }

    @Override
    public int getInt(String key, int def) {
        var v = get(key);
        if (v == null) return def;
        if (v instanceof Number n) return n.intValue();
        return def;
    }

    @Override
    public int getInt(String key) {
        return getInt(key, 0);
    }

    @Override
    public List<Integer> getIntList(String key, List<Integer> def) {
        return getNumberList(key, Number::intValue, def);
    }

    @Override
    public Object putInt(String key, int value) {
        return put(key, value);
    }

    @Override
    public Object putIntList(String key, List<Integer> value) {
        return put(key, value);
    }

    @Override
    public long getLong(String key, long def) {
        var v = get(key);
        if (v == null) return def;
        if (v instanceof Number n) return n.longValue();
        return def;
    }

    @Override
    public long getLong(String key) {
        return getLong(key, 0L);
    }

    @Override
    public List<Long> getLongList(String key, List<Long> def) {
        return getNumberList(key, Number::longValue, def);
    }

    @Override
    public Object putLong(String key, long value) {
        return put(key, value);
    }

    @Override
    public Object putLongList(String key, List<Long> value) {
        return put(key, value);
    }

    @Override
    public float getFloat(String key, float def) {
        var v = get(key);
        if (v == null) return def;
        if (v instanceof Number n) return n.floatValue();
        return def;
    }

    @Override
    public float getFloat(String key) {
        return getFloat(key, 0);
    }

    @Override
    public List<Float> getFloatList(String key, List<Float> def) {
        return getNumberList(key, Number::floatValue, def);
    }

    @Override
    public Object putFloat(String key, float value) {
        return put(key, value);
    }

    @Override
    public Object putFloatList(String key, List<Float> value) {
        return put(key, value);
    }

    @Override
    public double getDouble(String key, double def) {
        var v = get(key);
        if (v == null) return def;
        if (v instanceof Number n) return n.doubleValue();
        return def;
    }

    @Override
    public double getDouble(String key) {
        return getDouble(key, 0);
    }

    @Override
    public List<Double> getDoubleList(String key, List<Double> def) {
        return getNumberList(key, Number::doubleValue, def);
    }

    @Override
    public Object putDouble(String key, double value) {
        return put(key, value);
    }

    @Override
    public Object putDoubleList(String key, List<Double> value) {
        return put(key, value);
    }

}
