package xyz.bobkinn.indigodataio;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Class for storing objects by dot-separated string key
 */
@SuppressWarnings({"unchecked", "unused"})
public final class NestedKeyMap {
    private final Map<String, Object> map;

    public NestedKeyMap(Map<String, Object> map) {
        this.map = Objects.requireNonNull(map, "map");
    }

    /**
     * Creates shallow copy that means that changes will appear in both Map(s)
     * @param map other nested key map
     */
    public NestedKeyMap(NestedKeyMap map) {
        Objects.requireNonNull(map, "map");
        this.map = map.map();
    }

    public NestedKeyMap() {
        this.map = new HashMap<>();
    }

    public int size() {
        return map.size();
    }

    /**
     *
     * @param key key like <strong>{@code section.value}</strong>
     * @return section where {@code value} need to be placed
     */
    @Contract("_ -> new")
    public @NotNull NestedKeyMap getSectionByFullKey(String key){
        return new NestedKeyMap(resolveMap(extractMapKey(key).getLeft(), true));
    }

    private Map<String, Object> resolveMap(String key) {
        return resolveMap(key, false);
    }

    private Map<String, Object> resolveMap(@NotNull String key, boolean create) {
        var ret = map;
        for (String k : key.split("\\.")) {
            if (k.equals("")) return ret;
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

    @Contract("_ -> new")
    private @NotNull Pair<String, String> extractMapKey(@NotNull String key) {
        int i = key.lastIndexOf(".");
        if (i == -1) return Pair.of("", key);
        var l = key.substring(0, i);
        var r = key.substring(i + 1);
        return Pair.of(l, r);
    }

    public Object getObject(@NotNull String key) {
        if (key.equals("")) return map;
        var keys = extractMapKey(key);
        var map = resolveMap(keys.getLeft());
        if (map == null) return null;
        return map.get(keys.getRight());
    }

    @NotNull
    public Set<String> getKeys(@NotNull String key){
        if (key.equals("")) return map.keySet();
        var map = resolveMap(key);
        if (map == null) return Set.of();
        return map.keySet();
    }

    @Contract(pure = true)
    public @NotNull Set<String> getKeys(){
        return map.keySet();
    }

    public String getString(String key, String def) {
        var v = getObject(key);
        if (v instanceof String s) return s;
        return def;
    }

    public Boolean getBoolean(String key, Boolean def) {
        var v = getObject(key);
        if (v instanceof Boolean b) return b;
        return def;
    }

    public Boolean getBoolean(String key){
        return getBoolean(key, null);
    }

    public String getString(String key) {
        return getString(key, null);
    }

    public Float getFloat(String key, Float def) {
        var v = getObject(key);
        if (v instanceof Number n) return n.floatValue();
        return def;
    }

    @Contract("_, _ -> _")
    public Double getDouble(String key, Double def) {
        var v = getObject(key);
        if (v instanceof Number n) return n.doubleValue();
        return def;
    }

    public Integer getInt(String key, Integer def) {
        var v = getObject(key);
        if (v instanceof Number n) return n.intValue();
        return def;
    }

    public Long getLong(String key, Long def) {
        var v = getObject(key);
        if (v instanceof Number n) return n.longValue();
        return def;
    }

    public Long getLong(String key){
        return getLong(key, null);
    }

    public Integer getInt(String key) {
        return getInt(key, null);
    }

    public Double getDouble(String key) {
        return getDouble(key, null);
    }

    public Map<String, Object> getMap(String key, Map<String, Object> def) {
        var v = getObject(key);
        if (v == null) return def;
        try {
            return (Map<String, Object>) v;
        } catch (ClassCastException ignored){
            return def;
        }
    }

    public Map<String, Object> getMap(String key) {
        return getMap(key, null);
    }

    /**
     * Same as {@link #getMap(String)} but wrapped into NestedKeyMap
     * @param key key
     * @return NestedKeyMap with contents of map or {@code null} if no map found at this key
     */
    public NestedKeyMap getSection(String key) {
        var map = getMap(key);
        if (map != null) return new NestedKeyMap(map);
        else return null;
    }

    public List<String> getStringList(String key, List<String> def){
        var v = getObject(key);
        if (v == null) return def;
        try {
            return (List<String>) v;
        } catch (ClassCastException ignored){
            return def;
        }
    }

    public List<Integer> getIntList(String key, List<Integer> def){
        var v = getObject(key);
        if (v == null) return def;
        try {
            if (v instanceof List<?> l){
                return ((List<Number>) l).stream().map(Number::intValue).toList();
            } else if (v instanceof int[] l) {
                return Arrays.stream(l).boxed().toList();
            } else if (v instanceof float[] l) {
                return NumberUtil.floatToStream(l).map(Number::intValue).toList();
            } else if (v instanceof short[] l) {
                return NumberUtil.shortToStream(l).map(Number::intValue).toList();
            } else if (v instanceof double[] l) {
                return Arrays.stream(l).boxed().map(Number::intValue).toList();
            } else if (v instanceof long[] l) {
                return Arrays.stream(l).boxed().map(Number::intValue).toList();
            }
            return def;
        } catch (ClassCastException ignored){
            return def;
        }
    }

    public List<String> getStringList(String key){
        return getStringList(key, null);
    }

    public List<Map<String, Object>> getMapList(String key, List<Map<String, Object>> def){
        var v = getObject(key);
        if (v == null) return def;
        try {
            return (List<Map<String, Object>>) v;
        } catch (ClassCastException ignored){
            return def;
        }
    }

    public List<NestedKeyMap> getSections(String key, List<NestedKeyMap> def){
        var ls = getMapList(key);
        if (ls == null) return def;
        var ret = new ArrayList<NestedKeyMap>(ls.size());
        for (var map : ls) ret.add(new NestedKeyMap(map));
        return ret;
    }

    public List<NestedKeyMap> getSections(String key){
        return getSections(key, null);
    }

    public List<Map<String, Object>> getMapList(String key){
        return getMapList(key, null);
    }

    /**
     * Clear this map and set other map to this
     * @param newMap other
     */
    public void clearWith(Map<String, Object> newMap){
        this.map.clear();
        this.map.putAll(newMap);
    }

    /**
     * Put object in map
     *
     * @param key   dot-separated path
     * @param value value to set
     * @return previous value or null
     */
    @SuppressWarnings("UnusedReturnValue")
    public @Nullable Object put(String key, Object value) {
        var pair = extractMapKey(key);
        var map = resolveMap(pair.getLeft(), true);
        if (map == null) return null;
        if (value instanceof NestedKeyMap nestedKeyMap) {
            return map.put(pair.getRight(), nestedKeyMap.map());
        }
        return map.put(pair.getRight(), value);
    }

    public Map<String, Object> map() {
        return map;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (NestedKeyMap) obj;
        return Objects.equals(this.map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(map);
    }

    @Override
    public String toString() {
        return map.toString();
    }

}

